package songqiu.allthings.auth.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import songqiu.allthings.R;
import songqiu.allthings.activity.CommentWebViewActivity;
import songqiu.allthings.auth.bean.AuthStateBean;
import songqiu.allthings.base.BaseActivity;
import songqiu.allthings.http.BaseBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.http.OkHttp;
import songqiu.allthings.http.RequestCallBack;

/**
 * create by: ADMIN
 * time:2019/12/2417:03
 * e_mail:734090232@qq.com
 * description:认证（签约）界面
 */
public class AuthActivity extends BaseActivity {
    @BindView(R.id.backImg)
    ImageView backImg;
    @BindView(R.id.titleTv)
    TextView titleTv;
    @BindView(R.id.rightTv)
    TextView rightTv;
    @BindView(R.id.line)
    LinearLayout line;
    @BindView(R.id.tv_auth_original)
    TextView tvAuthOriginal;
    @BindView(R.id.tv_auth_sign)
    TextView tvAuthSign;
    private AuthStateBean stateBean;

    @Override
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_auth);
        ButterKnife.bind(this);
        titleTv.setText("身份认证");


    }
    @Override
    public void init() {
        OkHttp.post(this, HttpServicePath.URL_STATE_DATA,new HashMap<>(),new RequestCallBack(){

            @Override
            public void httpResult(BaseBean baseBean) {
                Gson gson = new Gson();
                String data = gson.toJson(baseBean.data);
                stateBean = gson.fromJson(data,AuthStateBean.class);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        updateViewState();
                    }
                });

            }
        });
    }

    //更新UI
    private void updateViewState() {
        switch (stateBean.getLevel()) {
            case 1:
                tvAuthOriginal.setEnabled(false);
                tvAuthSign.setEnabled(true);
                tvAuthSign.setText("可申请");
                tvAuthOriginal.setText("已认证");
                tvAuthOriginal.setTextColor(getResources().getColor(R.color.FFE7A722));
                break;
            case 0:
                tvAuthOriginal.setEnabled(true);
                tvAuthSign.setEnabled(false);
                tvAuthSign.setText("原创作者可申请");
                break;
            case 2:
                tvAuthOriginal.setEnabled(false);
                tvAuthSign.setEnabled(false);
                tvAuthOriginal.setText("已认证");
                tvAuthSign.setText("已认证");
                tvAuthSign.setTextColor(getResources().getColor(R.color.FFE7A722));
                tvAuthOriginal.setTextColor(getResources().getColor(R.color.FFE7A722));
                break;
            case 3:
                tvAuthSign.setEnabled(false);
                tvAuthOriginal.setEnabled(false);
                tvAuthOriginal.setText("审核中");
                break;
            case 4:
                tvAuthSign.setEnabled(false);
                tvAuthSign.setText("审核中");
                tvAuthOriginal.setEnabled(false);
                tvAuthOriginal.setText("已认证");
                tvAuthOriginal.setTextColor(getResources().getColor(R.color.FFE7A722));
                break;
        }
    }

    @OnClick({R.id.tv_auth_original,R.id.tv_auth_sign,R.id.backImg})
    public void onAuth(View view){
        switch (view.getId()){
            case R.id.backImg:
                finish();
                break;
            case R.id.tv_auth_original:
                if(stateBean==null){
                    return;
                }
                //level为0,v_status为2时为实名认证通过，可进入申请原创作者

                    if (stateBean.getV_status() == 2&&stateBean.getStatus()==0) {
                        Intent intent=new Intent(this, CommentWebViewActivity.class);
                        intent.putExtra("url","http://192.168.0.195:8080/certification/original");
                        intent.putExtra("authType",1);
                        startActivity(intent);
                    } else {
                        Intent intent=new Intent(this,RealNameAuthActivity.class);
                        intent.putExtra("V_status",stateBean.getV_status());
                        startActivity(intent);
                    }

                break;
            case R.id.tv_auth_sign:
                if(stateBean==null){
                    return;
                }
                //level为1,v_status为2时为原创作者通过，可进入申请签约作者
                if(stateBean.getLevel()==1){
                    Intent intent=new Intent(this, CommentWebViewActivity.class);
                    intent.putExtra("url","http://192.168.0.195:8080/certification/signing");
                    intent.putExtra("authType",2);
                    startActivity(intent);
                }
        }
    }


}
