package songqiu.allthings.auth.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import songqiu.allthings.Event.EventTags;
import songqiu.allthings.R;
import songqiu.allthings.base.BaseActivity;
import songqiu.allthings.bean.CreationIncomeBean;
import songqiu.allthings.http.BaseBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.http.OkHttp;
import songqiu.allthings.http.RequestCallBack;
import songqiu.allthings.util.SharedPreferencedUtils;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.util.ToastUtil;
import songqiu.allthings.util.statusbar.StatusBarUtils;

/**
 * create by: linyinjianying
 * time:
 * e_mail:734090232@qq.com
 * description:稿酬提现页
 */
public class CashOutActivity extends BaseActivity {

    TextView tvAuthSign;
    //用户状态
    int userStatus;
    @BindView(R.id.backImg)
    ImageView backImg;
    @BindView(R.id.titleTv)
    TextView titleTv;
    @BindView(R.id.rightTv)
    TextView rightTv;
    @BindView(R.id.line)
    LinearLayout line;
    @BindView(R.id.tv_user_zfb)
    TextView tvUserZfb;
    @BindView(R.id.et_money)
    EditText etMoney;
    @BindView(R.id.tv_tips)
    TextView tvTips;
    double maxMoney, cashoutMoney;
    @BindView(R.id.shadowLayout)
    LinearLayout shadowLayout;
    @BindView(R.id.tv_cash_all)
    TextView tvCashAll;
    @BindView(R.id.btn_cash)
    Button btnCash;
    CreationIncomeBean info;

    @Override
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_cashout);
        ButterKnife.bind(this);
        titleTv.setText("提现");
        StatusBarUtils.with(this)
                .setColor(getResources().getColor(R.color.FFF9FAFD))
                .init()
                .setStatusTextColorAndPaddingTop(true, this);
        info = getIntent().getParcelableExtra("info");
        initData();
        boolean dayModel = SharedPreferencedUtils.getBoolean(this, SharedPreferencedUtils.dayModel, true);
        modeUi(dayModel);

    }

    private void initData() {
        maxMoney = Double.parseDouble(info.now_money);
        tvTips.setText("可提现金额" + maxMoney + "元");
        tvUserZfb.setText(info.zfb + "");
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

    @Override
    public void init() {
        etMoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)) {
                    cashoutMoney = 0;
                    btnCash.setEnabled(false);
                } else {
                    int posDot = s.toString().indexOf(".");
                    if (posDot > 0) {
                        if (s.length() - posDot - 1 > 2) {
                            s.delete(posDot + 3, posDot + 4);

                        }
                    }

                    cashoutMoney = Double.valueOf(s.toString());



                }


                if (cashoutMoney > 0) {
                    btnCash.setEnabled(true);
                }


                if (cashoutMoney > maxMoney) {
                    cashoutMoney = maxMoney;
                    etMoney.setText(cashoutMoney + "");
                    etMoney.setSelection(etMoney.getText().length());
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCreateCome();
    }

    @OnClick(R.id.tv_cash_all)
    public void onCashAll() {
        cashoutMoney = maxMoney;
        etMoney.setText(cashoutMoney + "");
        etMoney.setSelection(etMoney.getText().length());
        if (cashoutMoney > 0) {
            btnCash.setEnabled(true);
        }
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
                        info = gson.fromJson(data, CreationIncomeBean.class);
                        if (null == info) return;
                        initData();
                    }
                });
            }
        });
    }

    @OnClick(R.id.btn_cash)
    public void onCash() {
        if (cashoutMoney <= 0) {
            ToastUtil.showToast("请输入正确的提现金额");
            return;
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("type", 3 + "");
        map.put("num", cashoutMoney + "");
        OkHttp.post(this, HttpServicePath.URL_ORDER, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(baseBean.msg);
                        getCreateCome();
                        EventBus.getDefault().post(new EventTags.BindAlipay());
                    }
                });
            }
        });
    }

    @OnClick(R.id.backImg)
    public void onBack() {
        finish();
    }

    @OnClick(R.id.tv_delete)
    public void onClear() {
        etMoney.setText("");
    }

}
