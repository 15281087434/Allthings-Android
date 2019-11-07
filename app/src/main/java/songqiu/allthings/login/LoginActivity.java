package songqiu.allthings.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import songqiu.allthings.Event.EventTags;
import songqiu.allthings.R;
import songqiu.allthings.activity.CommentWebViewActivity;
import songqiu.allthings.activity.MainActivity;
import songqiu.allthings.base.BaseActivity;
import songqiu.allthings.bean.UserBean;
import songqiu.allthings.bean.WeChatBean;
import songqiu.allthings.constant.SnsConstants;
import songqiu.allthings.http.BaseBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.http.OkHttp;
import songqiu.allthings.http.RequestCallBack;
import songqiu.allthings.iterface.IEditTextChangeListener;
import songqiu.allthings.mine.BindingPhoneActivity;
import songqiu.allthings.util.ClickUtil;
import songqiu.allthings.util.EditTextCheckUtil;
import songqiu.allthings.util.LogUtil;
import songqiu.allthings.util.SharedPreferencedUtils;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.util.TextVerifyUtil;
import songqiu.allthings.util.ToastUtil;
import songqiu.allthings.util.statusbar.StatusBarUtils;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/8/16
 *
 *类描述：
 *
 ********/
public class LoginActivity extends BaseActivity implements PlatformActionListener{

    @BindView(R.id.codeTv)
    TextView codeTv;
    @BindView(R.id.backImg)
    ImageView backImg;
    @BindView(R.id.phoneEt)
    EditText phoneEt;
    @BindView(R.id.codeEt)
    EditText codeEt;
    @BindView(R.id.loginTv)
    TextView loginTv;
    @BindView(R.id.shadowLayout)
    LinearLayout shadowLayout;
//    @BindView(R.id.preloadLayout)
//    RelativeLayout preloadLayout;
    WeChatBean weChatBean;
    int bindType = 1; //1、微信授权 2、QQ授权

    boolean begined;
    Timer timer;
    int time = 60;
    int tempTime;
    Handler handler;

    IWXAPI api;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Gson gson = new Gson();
                    try {
                        String data = msg.obj.toString();
                        if(!StringUtil.isEmpty(data)) {
                            weChatBean = gson.fromJson(data, WeChatBean.class);
                            if(bindType == 1) {
                                bindLogin(HttpServicePath.URL_WECHAT_LOGIN,weChatBean);
                            }else {
                                weChatBean.unionid = weChatBean.userID;
                                bindLogin(HttpServicePath.URL_QQ_LOGIN,weChatBean);
                            }
                        }
                    } catch (Exception e) {
                        LogUtil.i("数据转换失败！");
                    }
                    break;
                case 1:
                    LogUtil.i("绑定错误！");
                    break;
                case 2:
                    LogUtil.i("微信绑定已取消！");
                    break;
                default:
            }
            return false;
        }
    });

    @Override
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_login);
    }

    @Override
    public void init() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        StatusBarUtils.with(LoginActivity.this).init().setStatusTextColorWhite(true, LoginActivity.this);
        boolean dayModel = SharedPreferencedUtils.getBoolean(this,SharedPreferencedUtils.dayModel,true);
        modeUi(dayModel);
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

        //通过WXAPIFactory工厂获取IWXApI的示例
        api = WXAPIFactory.createWXAPI(this, SnsConstants.APP_ID,true);
        //将应用的appid注册到微信
        api.registerApp(SnsConstants.APP_ID);
    }

    public void modeUi(boolean isDay) {
        if(isDay) {
            shadowLayout.setVisibility(View.GONE);
        }else {
            shadowLayout.setVisibility(View.VISIBLE);
        }
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

    public void login(String phone, String pwd) {
        if (StringUtil.isEmpty(phone)) {
            ToastUtil.showToast("手机号不能为空");
            return;
        }

        if (StringUtil.isEmpty(pwd)) {
            ToastUtil.showToast("验证码不能为空");
            return;
        }
        if (!TextVerifyUtil.checkMobile(phone)) return;


        Map<String, String> map = new HashMap<>();
        map.put("phone", phone);
        map.put("code", pwd);
        map.put("phone_type", 2+"");
        OkHttp.post(this, HttpServicePath.URL_LOGIN, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        String data = gson.toJson(baseBean.data);
                        UserBean userBean = gson.fromJson(data, UserBean.class);
                        if (null != userBean) {
                            SharedPreferencedUtils.setString(LoginActivity.this, "SYSTOKEN", userBean.token);
                            SharedPreferencedUtils.setString(LoginActivity.this, "SYSNICKNAME", userBean.user_nickname);
                            SharedPreferencedUtils.setString(LoginActivity.this, "SYSAVATAR", userBean.avatar);
                            SharedPreferencedUtils.setString(LoginActivity.this, "SYSINVITATIONCODE", userBean.invitation_code);
                            SharedPreferencedUtils.setInteger(LoginActivity.this, "SYSUSERID", userBean.userid);
                            SharedPreferencedUtils.setBoolean(LoginActivity.this,SharedPreferencedUtils.LOGIN,true);
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            EventBus.getDefault().post(new EventTags.LoginSucceed());
                            finish();
                        }
                    }
                });
            }
        });
    }

    public void bindLogin(String url,WeChatBean weChatBean) {
        if(null == weChatBean) return;
//        showLoading(this);
        Map<String, String> map = new HashMap<>();
        map.put("unionid", weChatBean.unionid);
        map.put("avatar", weChatBean.icon);
        map.put("nickname",weChatBean.nickname);
        map.put("phone_type", 2+"");
        OkHttp.post(this,url, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        String data = gson.toJson(baseBean.data);
                        UserBean userBean = gson.fromJson(data, UserBean.class);
                        if (null != userBean) {
//                            preloadLayout.setVisibility(View.GONE);
                            SharedPreferencedUtils.setString(LoginActivity.this, "SYSTOKEN", userBean.token);
                            SharedPreferencedUtils.setString(LoginActivity.this, "SYSNICKNAME", userBean.user_nickname);
                            SharedPreferencedUtils.setString(LoginActivity.this, "SYSAVATAR", userBean.avatar);
                            SharedPreferencedUtils.setString(LoginActivity.this, "SYSINVITATIONCODE", userBean.invitation_code);
                            SharedPreferencedUtils.setInteger(LoginActivity.this, "SYSUSERID", userBean.userid);
                            SharedPreferencedUtils.setBoolean(LoginActivity.this,SharedPreferencedUtils.LOGIN,true);
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            EventBus.getDefault().post(new EventTags.LoginSucceed());
                            finish();
                        }
                    }
                });
            }
        });
    }

    /**
     * 绑定微信 QQ，获取微信用户头像、用户名
     */
    private void authorize(Platform plat, int type) {
        switch (type) {
            case 1:
                bindType = 1;
                ToastUtil.showToast(this,"正在微信授权...");
                break;
            case 2:
                bindType = 2;
                ToastUtil.showToast(this,"正在QQ授权...");
                break;
        }
        if (plat.isAuthValid()) { //如果授权就删除授权资料
            plat.removeAccount(true);
        }
        plat.showUser(null);//授权并获取用户信息
    }

    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
        String data = platform.getDb().exportData();
        Message msg = new Message();
        msg.what = 0;
        msg.obj = data;
        mHandler.sendMessage(msg);
    }

    @Override
    public void onError(Platform platform, int i, Throwable throwable) {
        mHandler.sendEmptyMessage(1);
    }

    @Override
    public void onCancel(Platform platform, int i) {
        mHandler.sendEmptyMessage(2);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void toBaindPhone(EventTags.ToBaindPhone toBaindPhone) {
        Intent intent = new Intent(this, BindingPhoneActivity.class);
        intent.putExtra("weChatBean",weChatBean);
        intent.putExtra("type",toBaindPhone.getType());
        startActivity(intent);
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @OnClick({R.id.backImg,R.id.loginTv, R.id.codeTv,R.id.qqImg,R.id.weChatImg,R.id.privaceTv,R.id.userTv})
    public void onViewClick(View view) {
        Intent intent;
        boolean dayModel = SharedPreferencedUtils.getBoolean(this,SharedPreferencedUtils.dayModel,true);
        switch (view.getId()) {
            case R.id.backImg:
                finish();
                break;
            case R.id.loginTv:
                String phone = phoneEt.getText().toString().replaceAll(" ", "");
                String code = codeEt.getText().toString().replaceAll(" ", "");
                login(phone, code);
                break;
            case R.id.codeTv:
                String strPhone = phoneEt.getText().toString().replaceAll(" ", "");
                getCode(strPhone);
                break;
            case R.id.qqImg:
                if(ClickUtil.onClick()) {
                    Platform qq = ShareSDK.getPlatform(QQ.NAME);
                    qq.setPlatformActionListener(this);
                    qq.SSOSetting(false);
                    authorize(qq, 2);
                }
                break;
            case R.id.weChatImg:
                if(ClickUtil.onClick()) {
                    Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
                    wechat.setPlatformActionListener(this);
                    wechat.SSOSetting(false);
                    authorize(wechat, 1);
                }
                break;
            case R.id.privaceTv: //隐私
                intent = new Intent(this,CommentWebViewActivity.class);
                if(dayModel) {
                    intent.putExtra("url", SnsConstants.URL_PRIVACY);
                }else {
                    intent.putExtra("url", SnsConstants.URL_PRIVACY_NIGHT);
                }
                startActivity(intent);
                break;
            case R.id.userTv: //用户协议
                intent = new Intent(this,CommentWebViewActivity.class);
                if(dayModel) {
                    intent.putExtra("url", SnsConstants.URL_USER_PROTOCOL);
                }else {
                    intent.putExtra("url", SnsConstants.URL_USER_PROTOCOL_NIGHT);
                }
                startActivity(intent);
                break;
        }
    }

}
