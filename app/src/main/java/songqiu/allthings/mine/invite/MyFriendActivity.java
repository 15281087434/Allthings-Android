package songqiu.allthings.mine.invite;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
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
import songqiu.allthings.R;
import songqiu.allthings.activity.CommentWebViewActivity;
import songqiu.allthings.adapter.MyFriendAdapter;
import songqiu.allthings.base.BaseActivity;
import songqiu.allthings.bean.InviteParameterBean;
import songqiu.allthings.bean.MyFriendBean;
import songqiu.allthings.constant.SnsConstants;
import songqiu.allthings.http.BaseBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.http.OkHttp;
import songqiu.allthings.http.RequestCallBack;
import songqiu.allthings.login.LoginActivity;
import songqiu.allthings.util.SharedPreferencedUtils;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.util.TokenManager;
import songqiu.allthings.util.VibratorUtil;
import songqiu.allthings.util.statusbar.StatusBarUtils;
import songqiu.allthings.util.theme.ThemeManager;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/9/25
 *
 *类描述：我的好友
 *
 ********/
public class MyFriendActivity extends BaseActivity {

    @BindView(R.id.backImg)
    ImageView backImg;
    @BindView(R.id.titleTv)
    TextView titleTv;
    @BindView(R.id.rightTv)
    TextView rightTv;
    @BindView(R.id.line)
    LinearLayout line;
    @BindView(R.id.invateTv)
    TextView invateTv;
    @BindView(R.id.recycle)
    RecyclerView recycle;
    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout smartRefreshLayout;
    @BindView(R.id.shadowLayout)
    LinearLayout shadowLayout;

    @BindView(R.id.iv_icon)
    ImageView ivIcon;
    @BindView(R.id.tv_message)
    TextView tvMessage;
    @BindView(R.id.emptyLayout)
    LinearLayout emptyLayout;

    int pageNo = 1;
    List<MyFriendBean> list;
    MyFriendAdapter adapter;


    @Override
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_my_friend);
    }

    @Override
    public void init() {
        boolean dayModel = SharedPreferencedUtils.getBoolean(this, SharedPreferencedUtils.dayModel, true);
        modeUi(dayModel);
        titleTv.setText("我的好友");
        initRecycle();
        getData(pageNo,false);
    }

    public void modeUi(boolean isDay) {
        if (isDay) {
            shadowLayout.setVisibility(View.GONE);
            StatusBarUtils.with(this)
                    .setColor(getResources().getColor(ThemeManager.getCurrentThemeRes(this, R.color.FFF9FAFD)))
                    .init()
                    .setStatusTextColorAndPaddingTop(true, this);
        } else {
            shadowLayout.setVisibility(View.VISIBLE);
            StatusBarUtils.with(this)
                    .setColor(getResources().getColor(ThemeManager.getCurrentThemeRes(this, R.color.trans_6)))
                    .init()
                    .setStatusTextColorAndPaddingTop(true, this);
        }
    }


    public void initRecycle() {
        list = new ArrayList<>();
        adapter = new MyFriendAdapter(this, list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycle.setLayoutManager(linearLayoutManager);
        recycle.setAdapter(adapter);

        smartRefreshLayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                pageNo = pageNo + 1;
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
        Map<String, String> map = new HashMap<>();
        map.put("num", 10 + "");
        map.put("page", pageNo + "");
        OkHttp.post(this, smartRefreshLayout, HttpServicePath.URL_FRIEND_MONEY, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        String data = gson.toJson(baseBean.data);
                        if (StringUtil.isEmpty(data)) return;
                        List<MyFriendBean> myFriendBeanList = gson.fromJson(data, new TypeToken<List<MyFriendBean>>() {
                        }.getType());
                        if (pageNo == 1) {
                            list.clear();
                            if(null == myFriendBeanList || 0 == myFriendBeanList.size()) {
                                emptyLayout.setVisibility(View.VISIBLE);
                                recycle.setVisibility(View.GONE);
                                ivIcon.setImageResource(R.mipmap.state_empty_friend);
                                tvMessage.setText("还没有好友哦\n快去邀请好友赚钱吧!");
                            }else {
                                emptyLayout.setVisibility(View.GONE);
                                recycle.setVisibility(View.VISIBLE);
                            }
                        }
                        list.addAll(myFriendBeanList);
                        adapter.notifyDataSetChanged();

                        if(ringDown) {
                            VibratorUtil.ringDown(MyFriendActivity.this);
                        }
                    }
                });
            }
        });
    }


    public void getInviteParameter() {
        Map<String, String> map = new HashMap<>();
        OkHttp.post(this, HttpServicePath.URL_INVITE_PARAMETER, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Gson gson = new Gson();
                            String data = gson.toJson(baseBean.data);
                            if (StringUtil.isEmpty(data)) return;
                            InviteParameterBean inviteParameterBean = gson.fromJson(data, InviteParameterBean.class);
                            if(null == inviteParameterBean) return;
                            Intent intent = new Intent(MyFriendActivity.this,CommentWebViewActivity.class);
                            boolean dayModel = SharedPreferencedUtils.getBoolean(MyFriendActivity.this,SharedPreferencedUtils.dayModel,true);
                            if(dayModel) {
                                intent.putExtra("url", SnsConstants.getUrlInviteFriend(inviteParameterBean.friend_num,inviteParameterBean.money,inviteParameterBean.total_coin));
                            }else {
                                intent.putExtra("url", SnsConstants.getUrlInviteFriendNight(inviteParameterBean.friend_num,inviteParameterBean.money,inviteParameterBean.total_coin));
                            }
                            startActivity(intent);
                        }
                    });
            }
        });
    }

    @OnClick({R.id.backImg, R.id.invateTv})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.backImg:
                finish();
                break;
            case R.id.invateTv:
                String token = TokenManager.getRequestToken(this);
                if(StringUtil.isEmpty(token)) {
                    Intent intent = new Intent(this,LoginActivity.class);
                    startActivity(intent);
                }else {
                    getInviteParameter();
                }
                break;
        }
    }

}
