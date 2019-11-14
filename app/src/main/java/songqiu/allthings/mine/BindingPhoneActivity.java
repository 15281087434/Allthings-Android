package songqiu.allthings.mine;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import songqiu.allthings.Event.EventTags;
import songqiu.allthings.R;
import songqiu.allthings.activity.MainActivity;
import songqiu.allthings.application.MyApplication;
import songqiu.allthings.base.BaseActivity;
import songqiu.allthings.bean.UserBean;
import songqiu.allthings.bean.WeChatBean;
import songqiu.allthings.http.BaseBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.http.OkHttp;
import songqiu.allthings.http.RequestCallBack;
import songqiu.allthings.iterface.IEditTextChangeListener;
import songqiu.allthings.util.EditTextCheckUtil;
import songqiu.allthings.util.LogUtil;
import songqiu.allthings.util.SharedPreferencedUtils;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.util.TextVerifyUtil;
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
 *类描述：绑定手机
 *
 ********/
public class BindingPhoneActivity extends BaseActivity {

    @BindView(R.id.backImg)
    ImageView backImg;
    @BindView(R.id.titleTv)
    TextView titleTv;

    @BindView(R.id.phoneEt)
    EditText phoneEt;
    @BindView(R.id.codeEt)
    EditText codeEt;
    @BindView(R.id.codeTv)
    TextView codeTv;
    @BindView(R.id.loginTv)
    TextView loginTv;
    @BindView(R.id.shadowLayout)
    LinearLayout shadowLayout;
    boolean begined;
    Timer timer;
    int time = 60;
    int tempTime;
    Handler handler;
    WeChatBean weChatBean;
    int type;

    @Override
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_binding_phone);
    }

    @Override
    public void init() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        boolean dayModel = SharedPreferencedUtils.getBoolean(this,SharedPreferencedUtils.dayModel,true);
        modeUi(dayModel);
        weChatBean = (WeChatBean)getIntent().getSerializableExtra("weChatBean");
        type = getIntent().getIntExtra("type",1);
        titleTv.setText("绑定手机");
        EditTextCheckUtil.textChangeListener textChangeListener = new EditTextCheckUtil.textChangeListener(loginTv);
        textChangeListener.addAllEditText(phoneEt, codeEt);
        EditTextCheckUtil.setChangeListener(new IEditTextChangeListener() {
            @Override
            public void textChange(boolean isHasContent) {
                if (isHasContent) {
                    loginTv.setBackgroundResource(R.mipmap.common_botton);
                    loginTv.setTextColor(getResources().getColor(R.color.white));
                } else {
                    loginTv.setBackgroundResource(R.drawable.rectangle_common_login_999999);
                    loginTv.setTextColor(getResources().getColor(R.color.FFBFBFBF));
                }
            }
        });
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
                    .setColor(getResources().getColor( R.color.trans_6))
                    .init()
                    .setStatusTextColorAndPaddingTop(true, this);
        }
    }

    public void verify(String phone,String code) {
        if (StringUtil.isEmpty(phone)) {
            ToastUtil.showToast("手机号不能为空");
            return;
        }
        if (!TextVerifyUtil.checkMobile(phone)) return;
        if (StringUtil.isEmpty(code)) {
            ToastUtil.showToast("验证码不能为空");
            return;
        }
        bindPhoneLogin(weChatBean,type,phone,code);
    }

    public void getCode(String phone) {
        if (StringUtil.isEmpty(phone)) {
            ToastUtil.showToast("手机号不能为空");
            return;
        }
        if (!TextVerifyUtil.checkMobile(phone)) return;
        setCodeUi();
        Map<String, String> map = new HashMap<>();
        map.put("phone", phone);
        OkHttp.post(this, HttpServicePath.URL_GET_CODE, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {

            }
        });
    }

    public void setCodeUi() {
        begined = true;
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Message message = new Message();
                message.what = (Date());
                handler.sendMessage(message);
            }
        }, 500, 1000);
        setText();
    }

    public void setText() {
        handler = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (begined) {
                    codeTv.setText(msg.what + "s");
                    codeTv.setClickable(false);
                    codeTv.setTextColor(getResources().getColor(R.color.FF999999));
                } else {
                    timer.cancel(); // Timer停止
                    time = 60;
                    codeTv.setText("获取验证码");
                    codeTv.setClickable(true);
                    codeTv.setTextColor(getResources().getColor(R.color.normal_color));
                }
            }
        };
    }

    public int Date() {
        tempTime = time--;
        if (tempTime >= 0) {
        } else {
            begined = false;
        }
        return tempTime;
    }

    public void bindPhoneLogin(WeChatBean weChatBean,int type,String phone,String code) {
        if(null == weChatBean) return;
        Map<String, String> map = new HashMap<>();
        map.put("unionid", weChatBean.unionid);
        map.put("avatar", weChatBean.icon);
        map.put("nickname",weChatBean.nickname);
        map.put("openid", weChatBean.openid);
        map.put("type", type+"");
        map.put("phone",phone);
        map.put("code",code);
        map.put("phone_type", 2+"");
        OkHttp.post(this, HttpServicePath.URL_LOGIN_BAN, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        String data = gson.toJson(baseBean.data);
                        UserBean userBean = gson.fromJson(data, UserBean.class);
                        if (null != userBean) {
                            SharedPreferencedUtils.setString(BindingPhoneActivity.this, "SYSTOKEN", userBean.token);
                            SharedPreferencedUtils.setString(BindingPhoneActivity.this, "SYSNICKNAME", userBean.user_nickname);
                            SharedPreferencedUtils.setString(BindingPhoneActivity.this, "SYSAVATAR", userBean.avatar);
                            SharedPreferencedUtils.setString(BindingPhoneActivity.this, "SYSINVITATIONCODE", userBean.invitation_code);
                            SharedPreferencedUtils.setInteger(BindingPhoneActivity.this, "SYSUSERID", userBean.userid);
                            SharedPreferencedUtils.setBoolean(BindingPhoneActivity.this, SharedPreferencedUtils.LOGIN, true);
                            EventBus.getDefault().post(new EventTags.LoginSucceed());
                            Intent intent = new Intent(BindingPhoneActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    }
                });
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void toBaindPhone(EventTags.BaindedPhone baindedPhone) {
        phoneEt.setText("");
        codeEt.setText("");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @OnClick({R.id.backImg,R.id.codeTv,R.id.loginTv})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.backImg:
                finish();
                break;
            case R.id.codeTv:
                String strPhone = phoneEt.getText().toString().replaceAll(" ", "");
                getCode(strPhone);
                break;
            case R.id.loginTv:
                String phone = phoneEt.getText().toString().replaceAll(" ", "");
                String code = codeEt.getText().toString().replaceAll(" ", "");
                verify(phone,code);
                break;
        }
    }

}
