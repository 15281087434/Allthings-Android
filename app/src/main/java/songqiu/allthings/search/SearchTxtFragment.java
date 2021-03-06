package songqiu.allthings.search;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import songqiu.allthings.Event.EventTags;
import songqiu.allthings.R;
import songqiu.allthings.adapter.HomeTabClassAdapter;
import songqiu.allthings.adapter.SearchTxtAdapter;
import songqiu.allthings.base.BaseFragment;
import songqiu.allthings.bean.HomeSubitemBean;
import songqiu.allthings.bean.SearchTxtBean;
import songqiu.allthings.http.BaseBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.http.OkHttp;
import songqiu.allthings.http.RequestCallBack;
import songqiu.allthings.util.LogUtil;
import songqiu.allthings.util.ShowNumUtil;
import songqiu.allthings.util.ToastUtil;
import songqiu.allthings.util.VibratorUtil;
import songqiu.allthings.util.theme.ThemeManager;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/8/22
 *
 *类描述：
 *
 ********/
public class SearchTxtFragment extends BaseFragment {

    @BindView(R.id.recycle)
    RecyclerView recycle;
    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout smartRefreshLayout;
    @BindView(R.id.emptyLayout)
    LinearLayout emptyLayout;
    SearchTxtAdapter adapter;
    int pageNo = 1;
    List<SearchTxtBean> item;

    SearchActivity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (SearchActivity) context;//保存Context引用
    }
    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }

    @Override
    public int initView() {
        return R.layout.fragment_search_list;
    }

    @Override
    public void init() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        item = new ArrayList<>();
        initRecyc();
        getTxtSearch(pageNo,activity.keyword,false);
    }

    //
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void SearchKeyword(EventTags.SearchKeyword searchKeyword) {
        getTxtSearch(pageNo,searchKeyword.getKeyWord(),false);
    }

    public void initRecyc() {
        adapter = new SearchTxtAdapter(activity,item);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycle.setLayoutManager(linearLayoutManager);
        recycle.setAdapter(adapter);

        smartRefreshLayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                pageNo = pageNo+1;
                getTxtSearch(pageNo,activity.keyword,false);
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                pageNo = 1;
                getTxtSearch(pageNo,activity.keyword,true);
            }
        });
    }

    public void getTxtSearch(int pageNo,String keyword,boolean ringDown) {
        Map<String,Object> map = new HashMap<>();
        map.put("type",1); //分类：1=文章，2=视频，3=话题
        map.put("keyword",keyword);
        map.put("num",10);
        map.put("page",pageNo);
        OkHttp.post(activity, smartRefreshLayout,HttpServicePath.URL_SEARCH, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                if(null != activity) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Gson gson = new Gson();
                            String data = gson.toJson(baseBean.data);
                            List<SearchTxtBean> searchTxtList = gson.fromJson(data, new TypeToken<List<SearchTxtBean>>() {}.getType());
                            if(pageNo ==1) {
                                item.clear();
                                if(null == searchTxtList || 0 == searchTxtList.size()) {
                                    emptyLayout.setVisibility(View.VISIBLE);
                                    smartRefreshLayout.setVisibility(View.GONE);
                                }else {
                                    emptyLayout.setVisibility(View.GONE);
                                    smartRefreshLayout.setVisibility(View.VISIBLE);
                                }
                            }
                            if(null != searchTxtList && 0!= searchTxtList.size()) {
                                item.addAll(searchTxtList);
                                adapter.notifyDataSetChanged();
                            }
                            if(ringDown) {
                                new Handler().postDelayed(new Runnable(){
                                    public void run() {
                                        //execute the task
                                        VibratorUtil.ringDown(activity);
                                    }
                                }, 500);
                            }
                        }
                    });
                }
            }
        });
    }


    //接受到收藏/取消收藏的通知
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void collectEvent(EventTags.CollectEvent collectEvent) {
        if(null == item || 0 == item.size()) return;
        for(int i = 0;i<item.size();i++) {
            if(item.get(i).articleid == collectEvent.getArticleid()) {
                if(collectEvent.getCollect()) {
                    item.get(i).collect_num =  item.get(i).collect_num + 1;
                }else {
                    item.get(i).collect_num =  item.get(i).collect_num - 1>0?item.get(i).collect_num:0;
                }
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
