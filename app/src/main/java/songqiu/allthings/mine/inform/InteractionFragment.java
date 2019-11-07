package songqiu.allthings.mine.inform;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
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
import songqiu.allthings.adapter.InteractionAdapter;
import songqiu.allthings.articledetail.ArticleDetailActivity;
import songqiu.allthings.base.BaseFragment;
import songqiu.allthings.bean.InteractionBean;
import songqiu.allthings.bean.InteractionListBean;
import songqiu.allthings.home.gambit.GambitDetailActivity;
import songqiu.allthings.home.gambit.HotGambitDetailActivity;
import songqiu.allthings.http.BaseBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.http.OkHttp;
import songqiu.allthings.http.RequestCallBack;
import songqiu.allthings.iterface.InteractionListener;
import songqiu.allthings.mine.help.FeedbackAndHelpActivity;
import songqiu.allthings.util.LogUtil;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.util.VibratorUtil;
import songqiu.allthings.videodetail.VideoDetailActivity;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/9/23
 *
 *类描述：互动
 *
 ********/
public class InteractionFragment extends BaseFragment {

    @BindView(R.id.recycle)
    RecyclerView recycle;
    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout smartRefreshLayout;
    @BindView(R.id.iv_icon)
    ImageView ivIcon;
    @BindView(R.id.tv_message)
    TextView tvMessage;
    @BindView(R.id.emptyLayout)
    LinearLayout emptyLayout;

    //
    int pageNo = 1;
    InteractionBean interactionBean;
    List<InteractionListBean> list;
    InteractionAdapter adapter;

    InformActivity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (InformActivity) context;//保存Context引用
    }
    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }

    @Override
    public int initView() {
        return R.layout.fragment_interaction;
    }

    @Override
    public void init() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        initRecycle();
        pageNo = 1;
        getData(pageNo,false);
    }

    public void initRecycle() {
        list = new ArrayList<>();
        adapter = new InteractionAdapter(activity,list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycle.setLayoutManager(linearLayoutManager);
        recycle.setAdapter(adapter);

        smartRefreshLayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                pageNo = pageNo+1;
                getData(pageNo,false);
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                pageNo = 1;
                getData(pageNo,true);
            }
        });

        adapter.setInteractionListener(new InteractionListener() {
            @Override
            public void toLookAndAttention(InteractionListBean interactionListBean) {
                if(1== interactionListBean.type) {//点赞状态去查看
                    Intent intent;
                    if(1==interactionListBean.change_type) { // //1 文章  2视频  3话题
                        intent = new Intent(activity, ArticleDetailActivity.class);
                        intent.putExtra("articleid",interactionListBean.mid);
                        startActivity(intent);
                    }else if(2==interactionListBean.change_type) {
                        intent = new Intent(activity, VideoDetailActivity.class);
                        intent.putExtra("articleid",interactionListBean.mid);
                        startActivity(intent);
                    }else { //为话题时再判断：talk_type 2 朋友圈  1跟进话题详情
//                        if(2 == interactionListBean.talk_type) {
//                            intent = new Intent(activity, GambitDetailActivity.class);
//                            intent.putExtra("talkid",interactionListBean.mid);
//                            startActivity(intent);
//                        }else {
//                            intent = new Intent(activity,HotGambitDetailActivity.class);
//                            intent.putExtra("talkid",interactionListBean.mid);
//                            startActivity(intent);
//                        }
                        //更改后
                        intent = new Intent(activity, GambitDetailActivity.class);
                        intent.putExtra("talkid",interactionListBean.mid);
                        startActivity(intent);
                    }
                }else { //关注 调用接口
                    if (0 == interactionListBean.is_follow) {//去关注
                        addFollow(interactionListBean.parentid, 1,interactionListBean);
                    } else { //取消关注
                        addFollow(interactionListBean.parentid, 2,interactionListBean);
                    }
                }
            }
        });
    }

    public void getData(int pageNo,boolean ringDown) {
        Map<String, String> map = new HashMap<>();
        map.put("type",1+""); //1=互动，2=评论，3=系统
        map.put("num",10+"");
        map.put("page",pageNo+"");
        OkHttp.post(activity,smartRefreshLayout, HttpServicePath.URL_NEWS, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                if(null !=activity) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Gson gson = new Gson();
                            String data = gson.toJson(baseBean.data);
                            if (StringUtil.isEmpty(data)) return;
                            interactionBean = gson.fromJson(data, InteractionBean.class);
                            if(null == interactionBean) return;
                            if(pageNo == 1) {
                                list.clear();
                                if(null == interactionBean.lists || 0 == interactionBean.lists.size()) {
                                    emptyLayout.setVisibility(View.VISIBLE);
                                    recycle.setVisibility(View.GONE);
                                }else {
                                    emptyLayout.setVisibility(View.GONE);
                                    recycle.setVisibility(View.VISIBLE);
                                }
                            }
                            if(null != interactionBean.lists || 0 != interactionBean.lists.size()) {
                                list.addAll(interactionBean.lists);
                                adapter.notifyDataSetChanged();
                            }
                            if(interactionBean.news_num>0) {
                                EventBus.getDefault().post(new EventTags.ShowDot(true,0));
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

    public void addFollow(int parentid, int type,InteractionListBean interactionListBean) { //用户id 	type、1=添加关注，2=取消关注
        Map<String, String> map = new HashMap<>();
        map.put("parentid", parentid + "");
        map.put("type", type + "");
        OkHttp.post(activity, HttpServicePath.URL_ADD_FOLLOW, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                if(null != activity) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (1 == type) {
                                for(int i = 0;i<list.size();i++) {
                                    if(2== interactionListBean.type && parentid == list.get(i).parentid) {
                                        list.get(i).is_follow = 1;
                                    }
                                }
                            } else {
                                for(int i = 0;i<list.size();i++) {
                                    if(2== interactionListBean.type && parentid == list.get(i).parentid) {
                                        list.get(i).is_follow = 0;
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


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void attention(EventTags.RefreshDot refreshDot) {
        int position = refreshDot.getPosition();
        if(0==position) {
            pageNo = 1;
            getData(pageNo,false);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
