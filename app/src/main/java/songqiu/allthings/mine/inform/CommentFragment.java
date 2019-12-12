package songqiu.allthings.mine.inform;

import android.content.Context;
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
import songqiu.allthings.adapter.CommentAdapter;
import songqiu.allthings.adapter.InteractionAdapter;
import songqiu.allthings.base.BaseFragment;
import songqiu.allthings.bean.CommentBean;
import songqiu.allthings.bean.CommentListBean;
import songqiu.allthings.http.BaseBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.http.OkHttp;
import songqiu.allthings.http.RequestCallBack;
import songqiu.allthings.mine.help.FeedbackAndHelpActivity;
import songqiu.allthings.util.LogUtil;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.util.VibratorUtil;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/9/23
 *
 *类描述：评论
 *
 ********/
public class CommentFragment extends BaseFragment {

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

    int pageNo = 1;
    List<CommentListBean> list;
    CommentAdapter adapter;
    CommentBean commentBean;

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
        adapter = new CommentAdapter(activity,list);
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

    }

    public void getData(int pageNo,boolean ringDown) {
        Map<String, Object> map = new HashMap<>();
        map.put("type",2); //1=互动，2=评论，3=系统
        map.put("num",10);
        map.put("page",pageNo);
        OkHttp.post(activity,smartRefreshLayout, HttpServicePath.URL_NEWS, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                if(null != activity) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Gson gson = new Gson();
                            String data = gson.toJson(baseBean.data);
                            if (StringUtil.isEmpty(data)) return;
                            commentBean = gson.fromJson(data, CommentBean.class);
                            if(null == commentBean) return;
                            if(pageNo == 1) {
                                list.clear();
                                if(null == commentBean.lists || 0 == commentBean.lists.size()) {
                                    emptyLayout.setVisibility(View.VISIBLE);
                                    recycle.setVisibility(View.GONE);
                                }else {
                                    emptyLayout.setVisibility(View.GONE);
                                    recycle.setVisibility(View.VISIBLE);
                                }
                            }
                            if(null != commentBean.lists || 0 != commentBean.lists.size()) {
                                list.addAll(commentBean.lists);
                                adapter.notifyDataSetChanged();
                            }
                            if(commentBean.news_num>0) {
                                EventBus.getDefault().post(new EventTags.ShowDot(true,1));
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void attention(EventTags.RefreshDot refreshDot) {
        int position = refreshDot.getPosition();
        if(1==position) {
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
