package songqiu.allthings.mine.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;
import songqiu.allthings.Event.EventTags;
import songqiu.allthings.R;
import songqiu.allthings.activity.CommentWebViewActivity;
import songqiu.allthings.application.MyApplication;
import songqiu.allthings.base.BaseActivity;
import songqiu.allthings.constant.SnsConstants;
import songqiu.allthings.login.LoginActivity;
import songqiu.allthings.mine.userpage.ModificationInfoActivity;
import songqiu.allthings.util.CacheDataManager;
import songqiu.allthings.util.CheckLogin;
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
 *创建时间 2019/8/20
 *
 *类描述：
 *
 ********/
public class SettingActivity extends BaseActivity {
    @BindView(R.id.backImg)
    ImageView backImg;
    @BindView(R.id.titleTv)
    TextView titleTv;
    @BindView(R.id.versionTv)
    TextView versionTv;
    @BindView(R.id.cacheTv)
    TextView cacheTv;
    @BindView(R.id.loginoutTv)
    TextView loginoutTv;
    @BindView(R.id.shadowLayout)
    LinearLayout shadowLayout;

    @Override
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_setting);
    }

    @Override
    public void init() {
        boolean dayModel = SharedPreferencedUtils.getBoolean(this,SharedPreferencedUtils.dayModel,true);
        modeUi(dayModel);
        titleTv.setText("设置");
        versionTv.setText(MyApplication.getInstance().versionName);
        try {
            cacheTv.setText(CacheDataManager.getTotalCacheSize(SettingActivity.this));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(!CheckLogin.isLogin(this)) {
            loginoutTv.setVisibility(View.GONE);
        }
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

    @OnClick({R.id.backImg,R.id.loginoutTv,R.id.cleanCacheLayout,R.id.compileInfoLayout,R.id.protocolLayout,R.id.privaceLayout,
            R.id.aboutLayout,R.id.accountLayout})
    public void onViewClick(View view) {
        Intent intent;
        boolean dayModel = SharedPreferencedUtils.getBoolean(this,SharedPreferencedUtils.dayModel,true);
        switch (view.getId()) {
            case R.id.backImg:
                finish();
                break;
            case R.id.compileInfoLayout: //编辑资料
                String token = TokenManager.getRequestToken(this);
                if(StringUtil.isEmpty(token)) {
                    intent = new Intent(SettingActivity.this,LoginActivity.class);
                }else {
                    intent = new Intent(SettingActivity.this,ModificationInfoActivity.class);
                }
                startActivity(intent);
                break;
            case R.id.cleanCacheLayout: //清除缓存
                CacheDataManager.clearAllCache(SettingActivity.this);
                cacheTv.setText("0kb");
                ToastUtil.showToast(this,"清除缓存成功");
                break;
            case R.id.loginoutTv:
                SharedPreferencedUtils.setString(SettingActivity.this, "SYSTOKEN","");
                SharedPreferencedUtils.setString(SettingActivity.this, "SYSNICKNAME", "");
                SharedPreferencedUtils.setString(SettingActivity.this, "SYSAVATAR", "");
                SharedPreferencedUtils.setString(SettingActivity.this, "SYSINVITATIONCODE", "");
                SharedPreferencedUtils.setInteger(SettingActivity.this, "SYSUSERID", 0);
                SharedPreferencedUtils.setBoolean(SettingActivity.this,SharedPreferencedUtils.LOGIN,false);
                intent = new Intent(SettingActivity.this,LoginActivity.class);
                startActivity(intent);
                EventBus.getDefault().post(new EventTags.ToLogin());
                finish();
                break;
            case R.id.protocolLayout: //用户协议
                intent = new Intent(SettingActivity.this, CommentWebViewActivity.class);
                if(dayModel) {
                    intent.putExtra("url", SnsConstants.URL_USER_PROTOCOL);
                }else {
                    intent.putExtra("url", SnsConstants.URL_USER_PROTOCOL_NIGHT);
                }
                startActivity(intent);
                break;
            case R.id.privaceLayout: //隐私政策
                intent = new Intent(SettingActivity.this, CommentWebViewActivity.class);
                if(dayModel) {
                    intent.putExtra("url", SnsConstants.URL_PRIVACY);
                }else {
                    intent.putExtra("url", SnsConstants.URL_PRIVACY_NIGHT);
                }
                startActivity(intent);
                break;
            case R.id.aboutLayout: //关于我们
                intent = new Intent(SettingActivity.this, CommentWebViewActivity.class);
                if(dayModel) {
                    intent.putExtra("url", SnsConstants.URL_ABOUT);
                }else {
                    intent.putExtra("url", SnsConstants.URL_ABOUT_NIGHT);
                }
                startActivity(intent);
                break;
            case R.id.accountLayout:
                intent = new Intent(SettingActivity.this, CommentWebViewActivity.class);
                intent.putExtra("url", SnsConstants.URL_ZHUXIAO);
                startActivity(intent);
                break;
        }
    }

}
