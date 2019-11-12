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
import songqiu.allthings.bean.UserInfoBean;
import songqiu.allthings.http.BaseBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.http.OkHttp;
import songqiu.allthings.http.RequestCallBack;
import songqiu.allthings.util.SharedPreferencedUtils;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.util.statusbar.StatusBarUtils;
import songqiu.allthings.util.theme.ThemeManager;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/8/23
 *
 *类描述：邀请码页面
 *
 ********/
public class InviteCodeActivity extends BaseActivity {
    @BindView(R.id.backImg)
    ImageView backImg;
    @BindView(R.id.titleTv)
    TextView titleTv;
    @BindView(R.id.rightTv)
    TextView rightTv;
    @BindView(R.id.line)
    LinearLayout line;
    @BindView(R.id.codeEt)
    EditText codeEt;
    @BindView(R.id.nextTv)
    TextView nextTv;
    @BindView(R.id.shadowLayout)
    LinearLayout shadowLayout;

    @Override
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_invite_code);
    }

    @Override
    public void init() {
        boolean dayModel = SharedPreferencedUtils.getBoolean(this,SharedPreferencedUtils.dayModel,true);
        modeUi(dayModel);
        titleTv.setText("输入邀请码");
    }

    public void modeUi(boolean isDay) {
        if(isDay) {
            shadowLayout.setVisibility(View.GONE);
            StatusBarUtils.with(this)
                    .setColor(getResources().getColor( R.color.FFF9FAFD))
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

    public void toInvite(String code) {
        if(StringUtil.isEmpty(code)) return;
        Map<String, String> map = new HashMap<>();
        map.put("parent_code",code);
        OkHttp.post(this, HttpServicePath.URL_INVITE, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        finish();
//                        Gson gson = new Gson();
//                        String data = gson.toJson(baseBean.data);
//                        if (StringUtil.isEmpty(data)) return;
//                        UserInfoBean userInfoBean = gson.fromJson(data, UserInfoBean.class);
//                        initUi(userInfoBean);
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
                String code = codeEt.getText().toString().trim();
                toInvite(code);
                break;
        }
    }

}
