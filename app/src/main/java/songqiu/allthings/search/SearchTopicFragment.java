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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import songqiu.allthings.Event.EventTags;
import songqiu.allthings.R;
import songqiu.allthings.adapter.HomeHotGambitAdapter;
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
import songqiu.allthings.util.LogUtil;
import songqiu.allthings.util.ShowNumUtil;
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
    SearchTopicAdapter adapter;
    List<HomeGambitHotBean> list;
    int pageNo = 1;
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
        initRecyc();
        getTxtSearch(pageNo,activity.keyword,false);
    }
    //
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void SearchKeyword(EventTags.SearchKeyword searchKeyword) {
        getTxtSearch(pageNo,searchKeyword.getKeyWord(),false);
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
                getTxtSearch(pageNo,activity.keyword,false);
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                pageNo = 1;
                getTxtSearch(pageNo,activity.keyword,true);
            }
        });

        adapter.setHomeHotGambitListener(new HomeHotGambitListener() {
            @Override
            public void addFollow(String url, int id,RecyclerView.ViewHolder viewHolder) {
                follow(url,id,viewHolder);
            }
        });
    }

    public void getTxtSearch(int pageNo,String keyword,boolean ringDown) {
        Map<String,Object> map = new HashMap<>();
        map.put("type",3); //分类：1=文章，2=视频，3=话题
        map.put("keyword",keyword);
        map.put("num",10);
        map.put("page",pageNo);
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
                                }else {
                                    emptyLayout.setVisibility(View.GONE);
                                    smartRefreshLayout.setVisibility(View.VISIBLE);
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

    public void follow(String url,int talk_id,RecyclerView.ViewHolder viewHolder) {
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
                                for(int i = 0;i<list.size();i++) {
                                    if(talk_id == list.get(i).id) {
                                        list.get(i).is_follow = 0;
                                        list.get(i).follow_num = list.get(i).follow_num -1;
                                        if(viewHolder instanceof SearchTopicAdapter.GambitViewholder) {
                                            ((SearchTopicAdapter.GambitViewholder)viewHolder).attentionTv.setText("关注");
                                            ((SearchTopicAdapter.GambitViewholder)viewHolder).attentionTv.setBackgroundResource(R.drawable.rectangle_common_attention);
                                            ((SearchTopicAdapter.GambitViewholder)viewHolder).attentionNumTv.setText(ShowNumUtil.showUnm(list.get(i).follow_num)+" 关注");
                                        }
                                    }
                                }
                            }else {
                                for(int i = 0;i<list.size();i++) {
                                    if(talk_id == list.get(i).id) {
                                        list.get(i).is_follow = 1;
                                        list.get(i).follow_num = list.get(i).follow_num +1;
                                        if(viewHolder instanceof HomeHotGambitAdapter.GambitViewholder) {
                                            ((HomeHotGambitAdapter.GambitViewholder)viewHolder).attentionTv.setText("已关注");
                                            ((HomeHotGambitAdapter.GambitViewholder)viewHolder).attentionTv.setBackgroundResource(R.drawable.rectangle_common_no_attention);
                                            ((HomeHotGambitAdapter.GambitViewholder)viewHolder).attentionNumTv.setText(ShowNumUtil.showUnm(list.get(i).follow_num)+" 关注");
                                        }
                                    }
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
