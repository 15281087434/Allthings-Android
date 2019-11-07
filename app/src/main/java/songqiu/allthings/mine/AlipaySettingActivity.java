package songqiu.allthings.mine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import songqiu.allthings.R;
import songqiu.allthings.base.BaseActivity;
import songqiu.allthings.bean.EarningsBean;
import songqiu.allthings.http.BaseBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.http.OkHttp;
import songqiu.allthings.http.RequestCallBack;
import songqiu.allthings.iterface.IEditTextChangeListener;
import songqiu.allthings.util.EditTextCheckUtil;
import songqiu.allthings.util.SharedPreferencedUtils;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.util.ToastUtil;
import songqiu.allthings.util.statusbar.StatusBarUtils;
import songqiu.allthings.util.theme.ThemeManager;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/8/23
 *
 *类描述：支付宝设置
 *
 ********/
public class AlipaySettingActivity extends BaseActivity {
    @BindView(R.id.backImg)
    ImageView backImg;
    @BindView(R.id.titleTv)
    TextView titleTv;
    @BindView(R.id.rightTv)
    TextView rightTv;
    @BindView(R.id.line)
    LinearLayout line;
    @BindView(R.id.img1)
    ImageView img1;
    @BindView(R.id.img2)
    ImageView img2;
    @BindView(R.id.aplipayEt)
    EditText aplipayEt;
    @BindView(R.id.aplipayNameEt)
    EditText aplipayNameEt;
    @BindView(R.id.nextTv)
    TextView nextTv;
    @BindView(R.id.shadowLayout)
    LinearLayout shadowLayout;

    @Override
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_alipay_setting);
    }

    @Override
    public void init() {
        boolean dayModel = SharedPreferencedUtils.getBoolean(this,SharedPreferencedUtils.dayModel,true);
        modeUi(dayModel);
        titleTv.setText("支付宝设置");
        EditTextCheckUtil.textChangeListener textChangeListener = new EditTextCheckUtil.textChangeListener(nextTv);
        textChangeListener.addAllEditText(aplipayEt, aplipayNameEt);
        EditTextCheckUtil.setChangeListener(new IEditTextChangeListener() {
            @Override
            public void textChange(boolean isHasContent) {
                if (isHasContent) {
                    nextTv.setBackgroundResource(R.mipmap.common_botton);
                    img1.setImageResource(R.mipmap.icon_alipay_setting_logo);
                    img2.setImageResource(R.mipmap.icon_alipay_setting);
                }else {
                    nextTv.setBackgroundResource(R.drawable.rectangle_common_login_999999);
                    img1.setImageResource(R.mipmap.icon_alipay_setting_logo_gray);
                    img2.setImageResource(R.mipmap.icon_alipay_setting_gray);
                }
            }
        });
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

    public void submit(String account,String name) {
        Map<String, String> map = new HashMap<>();
        map.put("zfb",account);
        map.put("zfb_name",name);
        OkHttp.post(this, HttpServicePath.URL_BIND_ZFB, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(AlipaySettingActivity.this,baseBean.msg);
                        finish();
                    }
                });
            }
        });
    }
    @OnClick({R.id.backImg,R.id.nextTv})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.backImg:
                finish();
                break;
            case R.id.nextTv:
                String account = aplipayEt.getText().toString().trim();
                String name = aplipayNameEt.getText().toString().trim();
                if(StringUtil.isEmpty(account)) {
                    ToastUtil.showToast(this,"请输入支付宝账号");
                    return;
                }
                if(StringUtil.isEmpty(name)) {
                    ToastUtil.showToast(this,"请输入支付宝姓名");
                    return;
                }
                submit(account,name);
//                Intent intent = new Intent(AlipaySettingActivity.this,WithdrawActivity.class);
//                startActivity(intent);
                break;
        }
    }

}
