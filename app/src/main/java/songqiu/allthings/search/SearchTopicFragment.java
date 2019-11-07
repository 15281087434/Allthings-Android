package songqiu.allthings.search;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import songqiu.allthings.R;
import songqiu.allthings.adapter.SearchTopicAdapter;
import songqiu.allthings.adapter.SearchTxtAdapter;
import songqiu.allthings.base.BaseFragment;
import songqiu.allthings.bean.HomeGambitHotBean;
import songqiu.allthings.bean.SearchTopicBean;
import songqiu.allthings.bean.SearchTxtBean;
import songqiu.allthings.http.BaseBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.http.OkHttp;
import songqiu.allthings.http.RequestCallBack;
import songqiu.allthings.iterface.HomeHotGambitListener;
import songqiu.allthings.mine.attention.AttentionActivity;
import songqiu.allthings.util.ToastUtil;
import songqiu.allthings.util.VibratorUtil;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/8/22
 *
 *类描述：
 *
 ********/
public class SearchTopicFragment extends BaseFragment {
    @BindView(R.id.recycle)
    RecyclerView recycle;
    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout smartRefreshLayout;
    @BindView(R.id.emptyLayout)
    LinearLayout emptyLayout;
    public String keyword;
    SearchTopicAdapter adapter;
    List<HomeGambitHotBean> list;
    int pageNo = 1;
    SearchResultListActivity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (SearchResultListActivity) context;//保存Context引用
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
        initRecyc();
        getTxtSearch(pageNo,keyword,false);
    }

    public void initRecyc() {
        list = new ArrayList<>();
        adapter = new SearchTopicAdapter(activity,list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycle.setLayoutManager(linearLayoutManager);
        recycle.setAdapter(adapter);

        smartRefreshLayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                pageNo = pageNo+1;
                getTxtSearch(pageNo,keyword,false);
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                pageNo = 1;
                getTxtSearch(pageNo,keyword,true);
            }
        });

        adapter.setHomeHotGambitListener(new HomeHotGambitListener() {
            @Override
            public void addFollow(String url, int id, List<HomeGambitHotBean> item) {
                follow(url,id,item);
            }
        });
    }

    public void getTxtSearch(int pageNo,String keyword,boolean ringDown) {
        Map<String,String> map = new HashMap<>();
        map.put("type","3"); //分类：1=文章，2=视频，3=话题
        map.put("keyword",keyword);
        map.put("num",10+"");
        map.put("page",pageNo+"");
        OkHttp.post(activity, smartRefreshLayout, HttpServicePath.URL_SEARCH, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                if(null != activity) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Gson gson = new Gson();
                            String data = gson.toJson(baseBean.data);
                            List<HomeGambitHotBean> searchTopicList = gson.fromJson(data, new TypeToken<List<HomeGambitHotBean>>() {}.getType());
                            if(pageNo ==1) {
                                list.clear();
                                if(null == searchTopicList || 0 == searchTopicList.size()) {
                                    emptyLayout.setVisibility(View.VISIBLE);
                                    smartRefreshLayout.setVisibility(View.GONE);
                                }
                            }
                            if(null != searchTopicList && 0!= searchTopicList.size()) {
                                list.addAll(searchTopicList);
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

    public void follow(String url,int talk_id,List<HomeGambitHotBean> item) {
        Map<String, String> map = new HashMap<>();
        map.put("talk_id", talk_id + "");
        OkHttp.post(activity, url, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                if(null != activity) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(url.contains("follow_talk_no")) { //取消关注
                                for(int i = 0;i<item.size();i++) {
                                    if(talk_id == item.get(i).id) {
                                        item.get(i).is_follow = 0;
                                    }
                                }
                            }else {
                                for(int i = 0;i<item.size();i++) {
                                    if(talk_id == item.get(i).id) {
                                        item.get(i).is_follow = 1;
                                    }
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }

}
