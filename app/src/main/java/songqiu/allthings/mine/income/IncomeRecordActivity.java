package songqiu.allthings.mine.income;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import songqiu.allthings.Event.EventTags;
import songqiu.allthings.R;
import songqiu.allthings.activity.CommentWebViewActivity;
import songqiu.allthings.adapter.EarningsAdapter;
import songqiu.allthings.adapter.HeaderViewAdapter;
import songqiu.allthings.adapter.TaskPageAdapter;
import songqiu.allthings.base.BaseActivity;
import songqiu.allthings.bean.EarningsBean;
import songqiu.allthings.bean.EarningsListBean;
import songqiu.allthings.constant.SnsConstants;
import songqiu.allthings.http.BaseBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.http.OkHttp;
import songqiu.allthings.http.RequestCallBack;
import songqiu.allthings.iterface.TaskItemListenter;
import songqiu.allthings.login.LoginActivity;
import songqiu.allthings.mine.InviteCodeActivity;
import songqiu.allthings.mine.WithdrawActivity;
import songqiu.allthings.mine.invite.MyFriendActivity;
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
 *创建时间 2019/8/23
 *
 *类描述：收入记录页面
 *
 ********/
public class IncomeRecordActivity extends BaseActivity {
    @BindView(R.id.backImg)
    ImageView backImg;
    @BindView(R.id.titleTv)
    TextView titleTv;
    @BindView(R.id.rightTv)
    TextView rightTv;
    @BindView(R.id.line)
    LinearLayout line;
    @BindView(R.id.hintTv)
    TextView hintTv;
    @BindView(R.id.propertyTypeTv)
    TextView propertyTypeTv;
    @BindView(R.id.propertyNumTv)
    TextView propertyNumTv;
    @BindView(R.id.withDrawTv)
    TextView withDrawTv;
    @BindView(R.id.approximateTv)
    TextView approximateTv;
    @BindView(R.id.todayPropertyTv)
    TextView todayPropertyTv;
    @BindView(R.id.todayPropertyNumTv)
    TextView todayPropertyNumTv;
    @BindView(R.id.totalPropertyTv)
    TextView totalPropertyTv;
    @BindView(R.id.totalPropertyNumTv)
    TextView totalPropertyNumTv;
    @BindView(R.id.reyclerView)
    RecyclerView reyclerView;
    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout smartRefreshLayout;
    @BindView(R.id.shadowLayout)
    LinearLayout shadowLayout;

    LinearLayout goldLayout;
    LinearLayout cashLayout;
    ImageView goldImg;
    ImageView cashImg;
    TextView goldTv;
    TextView cashTv;

    HeaderViewAdapter mHeaderAdapter;
    View mHeadView;
    List<EarningsListBean> list;
    EarningsAdapter adapter;

    public int type;
    int pageNo = 1;


    @Override
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_income_record);
    }

    @Override
    public void init() {
        boolean dayModel = SharedPreferencedUtils.getBoolean(this,SharedPreferencedUtils.dayModel,true);
        modeUi(dayModel);
        titleTv.setText("收入记录");
        setUi(0);
        type = 1;
        initRecycle();
        getData(type, pageNo,false);
    }

    public void modeUi(boolean isDay) {
        if(isDay) {
            shadowLayout.setVisibility(View.GONE);
            StatusBarUtils.with(this)
                    .setColor(getResources().getColor(R.color.FFF9FAFD))
                    .init()
                    .setStatusTextColorAndPaddingTop(true, this);
        }else {
            shadowLayout.setVisibility(View.VISIBLE);
            StatusBarUtils.with(this)
                    .setColor(getResources().getColor(R.color.trans_6))
                    .init()
                    .setStatusTextColorAndPaddingTop(true, this);
        }
    }

    public void initRecycle() {
        list = new ArrayList<>();
        adapter = new EarningsAdapter(this,list);
        mHeadView = LayoutInflater.from(this).inflate(R.layout.income_record_recycle_heard, null, false);
        goldLayout = mHeadView.findViewById(R.id.goldLayout);
        cashLayout = mHeadView.findViewById(R.id.cashLayout);
        goldImg = mHeadView.findViewById(R.id.goldImg);
        cashImg = mHeadView.findViewById(R.id.cashImg);
        goldTv = mHeadView.findViewById(R.id.goldTv);
        cashTv = mHeadView.findViewById(R.id.cashTv);

        mHeaderAdapter = new HeaderViewAdapter(adapter);
        mHeaderAdapter.addHeaderView(mHeadView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        reyclerView.setLayoutManager(linearLayoutManager);
        reyclerView.setAdapter(mHeaderAdapter);

        smartRefreshLayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                pageNo = pageNo + 1;
                getData(type,pageNo,false);
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                pageNo = 1;
                getData(type,pageNo,true);
            }
        });

        goldLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goldImg.setImageDrawable(getResources().getDrawable(R.mipmap.money_yellow));
                cashImg.setImageDrawable(getResources().getDrawable(R.mipmap.cash_gray));
                goldTv.setTextColor(getResources().getColor(R.color.bottom_tab_tv));
                cashTv.setTextColor(getResources().getColor(R.color.FF999999));
                setUi(0);
                type = 1;
                pageNo = 1;
                getData(type,pageNo,false);
            }
        });

        cashLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goldImg.setImageDrawable(getResources().getDrawable(R.mipmap.gold_gray));
                cashImg.setImageDrawable(getResources().getDrawable(R.mipmap.money_red));
                cashTv.setTextColor(getResources().getColor(R.color.bottom_tab_tv));
                goldTv.setTextColor(getResources().getColor(R.color.FF999999));
                setUi(1);
                type = 2;
                pageNo = 1;
                getData(type,pageNo,false);
            }
        });

    }

    public void setUi(int type) { //0、金币收益   1、现金收益
        if (type == 0) {
            propertyTypeTv.setText("当前金币");
            approximateTv.setVisibility(View.VISIBLE);
            todayPropertyTv.setText("今日金币");
            totalPropertyTv.setText("累计金币");
        } else {
            propertyTypeTv.setText("当前现金 (元)");
            approximateTv.setVisibility(View.INVISIBLE);
            todayPropertyTv.setText("今日现金");
            totalPropertyTv.setText("累计现金");
        }
    }

    public void initUi(EarningsBean earningsBean) {
        if (1 == type) {
            propertyNumTv.setText(String.valueOf(earningsBean.real_coin));
            todayPropertyNumTv.setText(String.valueOf(earningsBean.today_coin));
            totalPropertyNumTv.setText(String.valueOf(earningsBean.total_coin));
            approximateTv.setText("≈" + String.format("%.2f", (double) earningsBean.real_coin / earningsBean.prop_coin) + "元");
        } else {
            propertyNumTv.setText(earningsBean.real_money);
            todayPropertyNumTv.setText(earningsBean.today_money);
            totalPropertyNumTv.setText(earningsBean.total_money);
        }
        hintTv.setText("当前汇率：" + earningsBean.prop_coin + "金币=1元（汇率可浮动）");
    }


    public void getData(int type, int pageNo,boolean ringDown) {
        Map<String, Object> map = new HashMap<>();
        map.put("type", type);
        map.put("num", 10);
        map.put("page", pageNo);
        OkHttp.post(this,smartRefreshLayout, HttpServicePath.URL_GET_LIST, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        String data = gson.toJson(baseBean.data);
                        if (StringUtil.isEmpty(data)) return;
                        EarningsBean earningsBean = gson.fromJson(data, EarningsBean.class);
                        if (null == earningsBean) return;
                        initUi(earningsBean);
                        if (pageNo == 1) {
                            list.clear();
                            if(null == earningsBean.list || 0 ==earningsBean.list.size()) {
                                EarningsListBean earningsListBean = new EarningsListBean();
                                earningsListBean.isEmpty = true;
                                earningsBean.list.add(earningsListBean);
                            }
                        }
                        list.addAll(earningsBean.list);
                        adapter.notifyDataSetChanged();

                        if(ringDown) {
                            VibratorUtil.ringDown(IncomeRecordActivity.this);
                        }
                    }
                });
            }
        });
    }


    @OnClick({R.id.backImg, R.id.withDrawTv})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.backImg:
                finish();
                break;
            case R.id.withDrawTv:
                Intent intent = new Intent(IncomeRecordActivity.this, WithdrawActivity.class);
                if (type == 1) {
                    intent.putExtra("withdrawType", 1);
                } else {
                    intent.putExtra("withdrawType", 2);
                }
                startActivity(intent);
                break;
        }
    }

}
