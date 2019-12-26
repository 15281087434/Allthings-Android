package songqiu.allthings.auth.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import songqiu.allthings.R;
import songqiu.allthings.base.BaseActivity;
import songqiu.allthings.http.BaseBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.http.OkHttp;
import songqiu.allthings.http.RequestCallBack;
import songqiu.allthings.util.ToastUtil;

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
    int maxMoney = 9999, cashoutMoney;
    String zfb;
    @BindView(R.id.tv_cash_all)
    TextView tvCashAll;
    @BindView(R.id.btn_cash)
    Button btnCash;

    @Override
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_cashout);
        ButterKnife.bind(this);
        titleTv.setText("提现");
        tvTips.setText("可提现金额" + maxMoney + "元");

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
                    cashoutMoney = Integer.valueOf(s.toString());
                    if (cashoutMoney > 0) {
                        btnCash.setEnabled(true);
                    }

                }

                if (cashoutMoney > maxMoney) {
                    cashoutMoney = maxMoney;
                    etMoney.setText(cashoutMoney + "");
                    etMoney.setSelection(etMoney.getText().length());
                }
            }
        });
    }

    @OnClick(R.id.tv_cash_all)
    public void onCashAll() {
        cashoutMoney = maxMoney;
        etMoney.setText(cashoutMoney+"");
        etMoney.setSelection(etMoney.getText().length());
        if(cashoutMoney>0){
            btnCash.setEnabled(true);
        }
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
                    }
                });
            }
        });
    }

    @OnClick(R.id.backImg)
    public void onBack() {
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
