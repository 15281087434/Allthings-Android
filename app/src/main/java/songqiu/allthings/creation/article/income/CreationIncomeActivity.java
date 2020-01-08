package songqiu.allthings.creation.article.income;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import songqiu.allthings.Event.EventTags;
import songqiu.allthings.R;
import songqiu.allthings.auth.activity.CashOutActivity;
import songqiu.allthings.auth.activity.CashOutRecordActivity;
import songqiu.allthings.auth.activity.RoyaltiesActivity;
import songqiu.allthings.base.BaseActivity;
import songqiu.allthings.bean.CreationIncomeBean;
import songqiu.allthings.http.BaseBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.http.OkHttp;
import songqiu.allthings.http.RequestCallBack;
import songqiu.allthings.mine.AlipaySettingActivity;
import songqiu.allthings.mine.WithdrawActivity;
import songqiu.allthings.mine.WithdrawRecordActivity;
import songqiu.allthings.util.ClickUtil;
import songqiu.allthings.util.CopyButtonLibrary;
import songqiu.allthings.util.SharedPreferencedUtils;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.util.ToastUtil;
import songqiu.allthings.util.statusbar.StatusBarUtils;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/12/20
 *
 *类描述：创作收入
 *
 ********/
public class CreationIncomeActivity extends BaseActivity {
    @BindView(R.id.backImg)
    ImageView backImg;
    @BindView(R.id.titleTv)
    TextView titleTv;
    @BindView(R.id.rightTv)
    TextView rightTv;
    @BindView(R.id.line)
    LinearLayout line;
    @BindView(R.id.shadowLayout)
    LinearLayout shadowLayout;
    @BindView(R.id.moneyTv)
    TextView moneyTv;
    @BindView(R.id.withdrawTv)
    TextView withdrawTv;
    @BindView(R.id.freezeMoneyTv)
    TextView freezeMoneyTv;
    @BindView(R.id.allMoneyTv)
    TextView allMoneyTv;
    @BindView(R.id.hintTv)
    TextView hintTv;
    @BindView(R.id.explainLayout)
    LinearLayout explainLayout;
    @BindView(R.id.withdrawLayout)
    LinearLayout withdrawLayout;
    CreationIncomeBean creationIncomeBean;


    @Override
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_creation_income);
    }

    @Override
    public void init() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        boolean dayModel = SharedPreferencedUtils.getBoolean(this, SharedPreferencedUtils.dayModel, true);
        modeUi(dayModel);
        titleTv.setText("创作收入");
        getCreateCome();
    }


    public void modeUi(boolean isDay) {
        if (isDay) {
            shadowLayout.setVisibility(View.GONE);
            StatusBarUtils.with(this)
                    .setColor(getResources().getColor(R.color.FFF9FAFD))
                    .init()
                    .setStatusTextColorAndPaddingTop(true, this);
        } else {
            shadowLayout.setVisibility(View.VISIBLE);
            StatusBarUtils.with(this)
                    .setColor(getResources().getColor(R.color.trans_6))
                    .init()
                    .setStatusTextColorAndPaddingTop(true, this);
        }
    }

    public void initUi(CreationIncomeBean creationIncomeBean) {
        moneyTv.setText(creationIncomeBean.now_money);
        freezeMoneyTv.setText(creationIncomeBean.frozen_money);
        allMoneyTv.setText(creationIncomeBean.total_money);
    }

    public void getCreateCome() {
        Map<String, String> map = new HashMap<>();
        OkHttp.post(this, HttpServicePath.URL_CREATE_COME, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        String data = gson.toJson(baseBean.data);
                        if (StringUtil.isEmpty(data)) return;
                        creationIncomeBean = gson.fromJson(data, CreationIncomeBean.class);
                        if (null == creationIncomeBean) return;
                        initUi(creationIncomeBean);
                    }
                });
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void bindAlipay(EventTags.BindAlipay bindAlipay) {
       getCreateCome();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @OnClick({R.id.backImg,R.id.hintTv,R.id.withdrawTv,R.id.withdrawLayout,R.id.explainLayout})
    public void onViewClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.backImg:
                finish();
                break;
            case R.id.hintTv:
                CopyButtonLibrary copyButtonLibrary = new CopyButtonLibrary(CreationIncomeActivity.this, hintTv);
                copyButtonLibrary.init(hintTv);
                ToastUtil.showToast(CreationIncomeActivity.this, "复制成功:" + hintTv.getText().toString());
                break;
            case R.id.withdrawTv:
                if(ClickUtil.onClick()) {
                    if(null == creationIncomeBean) return;
                    if(0 == creationIncomeBean.is_bind) {
                        intent = new Intent(CreationIncomeActivity.this, AlipaySettingActivity.class);
                        startActivity(intent);
                    }else {
                        intent = new Intent(CreationIncomeActivity.this, CashOutActivity.class);
                        intent.putExtra("info",creationIncomeBean);
                        startActivity(intent);
                    }
                }
                break;
            case R.id.withdrawLayout:
                if(ClickUtil.onClick()) {
                    intent = new Intent(CreationIncomeActivity.this, CashOutRecordActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.explainLayout:
                if(ClickUtil.onClick()) {
                    intent = new Intent(CreationIncomeActivity.this, RoyaltiesActivity.class);
                    startActivity(intent);
                }
                break;

        }

    }

}
