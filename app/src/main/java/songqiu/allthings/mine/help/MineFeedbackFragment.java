package songqiu.allthings.mine.help;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import songqiu.allthings.R;
import songqiu.allthings.adapter.MineFeedbackAdapter;
import songqiu.allthings.base.BaseFragment;
import songqiu.allthings.bean.MineFeedbackBean;
import songqiu.allthings.http.BaseBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.http.OkHttp;
import songqiu.allthings.http.RequestCallBack;
import songqiu.allthings.iterface.MineFeedbackListener;
import songqiu.allthings.login.LoginActivity;
import songqiu.allthings.util.CheckLogin;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.util.VibratorUtil;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/8/29
 *
 *类描述：我的反馈 /反馈列表
 *
 ********/
public class MineFeedbackFragment extends BaseFragment {
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
    @BindView(R.id.loginLayout)
    LinearLayout loginLayout;

    List<MineFeedbackBean> item;
    MineFeedbackAdapter adapter;
    int pageNo = 1;
    FeedbackAndHelpActivity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (FeedbackAndHelpActivity) context;//保存Context引用
    }
    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }

    @Override
    public int initView() {
        return R.layout.fragment_mine_feedback;
    }

    @Override
    public void init() {
        initRecyc();
        if(CheckLogin.isLogin(activity)) {
            loginLayout.setVisibility(View.GONE);
            smartRefreshLayout.setVisibility(View.VISIBLE);
            getFeedBack(pageNo,false);
        }else {
            loginLayout.setVisibility(View.VISIBLE);
            smartRefreshLayout.setVisibility(View.GONE);
        }
    }


    public void initRecyc() {
        item = new ArrayList<>();
        adapter = new MineFeedbackAdapter(activity, item);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycle.setLayoutManager(linearLayoutManager);
        recycle.setAdapter(adapter);

        adapter.setMineFeedbackListener(new MineFeedbackListener() {
            @Override
            public void lookFeedback(int fid) {
                Intent intent = new Intent(activity, FeedbackDetailActivity.class);
                intent.putExtra("fid", fid);
                startActivity(intent);
            }
        });

        smartRefreshLayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                pageNo = pageNo + 1;
                getFeedBack(pageNo,false);
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                pageNo = 1;
                getFeedBack(pageNo,true);
            }
        });
    }

    public void getFeedBack(int pageNo,boolean ringDown) {
        Map<String, Object> map = new HashMap<>();
        map.put("page", pageNo);
        map.put("num", 10);
        OkHttp.post(activity, smartRefreshLayout, HttpServicePath.URL_FEEDBACK_LIST, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                if(null != activity) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Gson gson = new Gson();
                            String data = gson.toJson(baseBean.data);
                            if (StringUtil.isEmpty(data)) return;
                            List<MineFeedbackBean> mineFeedbackList = gson.fromJson(data, new TypeToken<List<MineFeedbackBean>>() {
                            }.getType());
                            if (pageNo == 1) {
                                item.clear();
                                if(null != mineFeedbackList && 0!=mineFeedbackList.size()) {
                                    emptyLayout.setVisibility(View.GONE);
                                    recycle.setVisibility(View.VISIBLE);
                                }else {
                                    emptyLayout.setVisibility(View.VISIBLE);
                                    recycle.setVisibility(View.GONE);
                                    tvMessage.setText("暂时没有反馈内容哦!");
                                }
                            }
                            item.addAll(mineFeedbackList);
                            adapter.notifyDataSetChanged();
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

    @OnClick({R.id.loginTv})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.loginTv:
                Intent intent = new Intent(activity, LoginActivity.class);
                startActivity(intent);
                break;
        }
    }

}
