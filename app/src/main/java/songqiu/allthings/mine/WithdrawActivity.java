package songqiu.allthings.mine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import songqiu.allthings.R;
import songqiu.allthings.adapter.TaskSignAdapter;
import songqiu.allthings.adapter.WithdrawTypeAdapter;
import songqiu.allthings.base.BaseActivity;
import songqiu.allthings.bean.TaskSiginListBean;
import songqiu.allthings.bean.WithdrawBean;
import songqiu.allthings.bean.WithdrawListBean;
import songqiu.allthings.http.BaseBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.http.OkHttp;
import songqiu.allthings.http.RequestCallBack;
import songqiu.allthings.iterface.TaskSignListener;
import songqiu.allthings.iterface.WithdrawListener;
import songqiu.allthings.login.LoginActivity;
import songqiu.allthings.util.ClickUtil;
import songqiu.allthings.util.LogUtil;
import songqiu.allthings.util.SharedPreferencedUtils;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.util.ToastUtil;
import songqiu.allthings.util.TokenManager;
import songqiu.allthings.util.statusbar.StatusBarUtils;
import songqiu.allthings.util.theme.ThemeManager;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/8/23
 *
 *类描述：提现页面  1、金币 2、现金
 *
 ********/
public class WithdrawActivity extends BaseActivity {


    @BindView(R.id.backImg)
    ImageView backImg;
    @BindView(R.id.titleTv)
    TextView titleTv;
    @BindView(R.id.rightTv)
    TextView rightTv;
    @BindView(R.id.line)
    LinearLayout line;
    @BindView(R.id.goldWithdrawTv)
    TextView goldWithdrawTv;

    @BindView(R.id.currentTv)
    TextView currentTv;
    @BindView(R.id.numTv)
    TextView numTv;
    @BindView(R.id.gridView)
    GridView gridView;
    @BindView(R.id.approximateTv)
    TextView approximateTv;

    @BindView(R.id.shadowLayout)
    LinearLayout shadowLayout;

    int withdrawType; //1、金币  2、金钱
    List<WithdrawListBean> list;
    WithdrawTypeAdapter adapter;
    int mNum; //金钱单位：元,无论哪种提现方式，都是金钱
    int currentMoney;


    @Override
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_withdraw);
    }

    @Override
    public void init() {
        boolean dayModel = SharedPreferencedUtils.getBoolean(this,SharedPreferencedUtils.dayModel,true);
        modeUi(dayModel);
        withdrawType = getIntent().getIntExtra("withdrawType", 1);
        rightTv.setText("提现记录");
        rightTv.setVisibility(View.VISIBLE);
        if (withdrawType == 1) {
            titleTv.setText("金币提现");
            currentTv.setText("当前金币 (个)");
        } else {
            titleTv.setText("现金提现");
            currentTv.setText("当前现金 (元)");
            approximateTv.setVisibility(View.GONE);
        }
        initGridView();
        getData(withdrawType);
    }

    public void modeUi(boolean isDay) {
        if(isDay) {
            shadowLayout.setVisibility(View.GONE);
            StatusBarUtils.with(this)
                    .setColor(getResources().getColor(ThemeManager.getCurrentThemeRes(this, R.color.FFF9FAFD)))
                    .init()
                    .setStatusTextColorAndPaddingTop(true, this);
        }else {
            shadowLayout.setVisibility(View.VISIBLE);
            StatusBarUtils.with(this)
                    .setColor(getResources().getColor(ThemeManager.getCurrentThemeRes(this, R.color.trans_6)))
                    .init()
                    .setStatusTextColorAndPaddingTop(true, this);
        }
    }

    public void initGridView() {
        list = new ArrayList<>();
        adapter = new WithdrawTypeAdapter(this,list);
        gridView.setAdapter(adapter);
        adapter.setWithdrawListener(new WithdrawListener() {
            @Override
            public void withDraw(int position,int num) {
                mNum = num;
                if(null != list && 0!=list.size() && position<list.size()) {
                    for(int i =0;i<list.size();i++) {
                        if(position == i) {
                            list.get(i).isClick = true;
                        }else {
                            list.get(i).isClick = false;
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    public void initUi(WithdrawBean withdrawBean) {
        if(withdrawType == 1) {
            numTv.setText(String.valueOf(withdrawBean.real_coin));
            approximateTv.setText("≈"+String.format("%.2f", (double)withdrawBean.real_coin/withdrawBean.prop_coin)+"元");
            currentMoney = withdrawBean.real_coin/withdrawBean.prop_coin;
        }else {
            numTv.setText(String.valueOf(withdrawBean.take_money));
            currentMoney = (int)Math.floor(withdrawBean.take_money);
        }

    }

    public void getData(int type) { //1=金币，2=金钱
        list.clear();
        Map<String, String> map = new HashMap<>();
        map.put("type", type + "");
        OkHttp.post(this, HttpServicePath.URL_COINLIST, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        String data = gson.toJson(baseBean.data);
                        if (StringUtil.isEmpty(data)) return;
                        WithdrawBean withdrawBean = gson.fromJson(data, WithdrawBean.class);
                        if(null == withdrawBean) return;
                        initUi(withdrawBean);
                        if(1==withdrawType) {
                            if(null != withdrawBean.num && 0!=withdrawBean.num.size()) {
                                for(int i = 0;i<withdrawBean.num.size();i++) {
                                    WithdrawListBean withdrawListBean = new WithdrawListBean();
                                    withdrawListBean.money_type = withdrawBean.num.get(i).money_type;
                                    withdrawListBean.hint = "用"+(withdrawBean.num.get(i).money_type*withdrawBean.prop_coin)+"金币兑换";
                                    list.add(withdrawListBean);
                                }
                            }
                        }else {
                            list.addAll(withdrawBean.num);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    public void withDraw() {
        Map<String, String> map = new HashMap<>();
        map.put("type", withdrawType + ""); //1、金币提现  2、现金提现
        map.put("num", mNum + "");
        OkHttp.post(this, HttpServicePath.URL_ORDER, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                      ToastUtil.showToast(WithdrawActivity.this,baseBean.msg);
                        getData(withdrawType); //提现成功后刷新
                    }
                });
            }
        });
    }

    @OnClick({R.id.backImg, R.id.rightTv,R.id.goldWithdrawTv})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.backImg:
                finish();
                break;
            case R.id.rightTv:
                if (ClickUtil.onClick()) {
                    Intent intent = new Intent(WithdrawActivity.this, WithdrawRecordActivity.class);
                    intent.putExtra("withdrawType",withdrawType);
                    startActivity(intent);
                }
                break;
            case R.id.goldWithdrawTv:
                if(0==mNum) {
                    ToastUtil.showToast(this,"请选择金额");
                    return;
                }
               if(currentMoney < mNum) {
                   ToastUtil.showToast(this,"可提金额不足");
                   return;
               }
               withDraw();
                break;
        }

    }

}
