package songqiu.allthings.auth.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import songqiu.allthings.R;
import songqiu.allthings.auth.bean.AuthStateBean;
import songqiu.allthings.base.BaseActivity;
import songqiu.allthings.http.BaseBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.http.OkHttp;
import songqiu.allthings.http.RequestCallBack;
import songqiu.allthings.util.ToastUtil;

/**
 * create by: ADMIN
 * time:2019/12/2417:41
 * e_mail:734090232@qq.com
 * description:认证（签约）申请（详情）界面
 */
public class AuthDetailsActivity extends BaseActivity {
    @BindView(R.id.backImg)
    ImageView backImg;
    @BindView(R.id.titleTv)
    TextView titleTv;
    @BindView(R.id.rightTv)
    TextView rightTv;
    @BindView(R.id.line)
    LinearLayout line;
    @BindView(R.id.btn_auth)
    Button btnAuth;
    int level=0;
    @Override
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_auth_details);
        ButterKnife.bind(this);
        level=getIntent().getIntExtra("level",0);
        if(level==0) {
            titleTv.setText("原创作者认证");
            btnAuth.setText("申请原创作者");
        }else {
            titleTv.setText("签约作者认证");
            btnAuth.setText("申请签约作者");
        }

    }

    @OnClick({R.id.backImg,R.id.btn_auth})
    public  void   onClick(View v){
        switch (v.getId()){
            case R.id.backImg:
                finish();
                break;
            case R.id.btn_auth:
                onAuth();
                break;
        }
    }

    public void  onAuth(){
        HashMap<String,String> map=new HashMap<>();
        map.put("type",(level+1)+"");
        OkHttp.post(this, HttpServicePath.URL_STATE_APPLY,map,new RequestCallBack(){

            @Override
            public void httpResult(BaseBean baseBean) {
                ToastUtil.showToast(baseBean.msg);
            }
        });
    }

    @Override
    public void init() {

    }
}
