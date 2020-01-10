package songqiu.allthings.auth.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import songqiu.allthings.R;
import songqiu.allthings.activity.CommentWebViewActivity;
import songqiu.allthings.adapter.OriginalApplyConditionAdapter;
import songqiu.allthings.adapter.SignApplyConditionAdapter;
import songqiu.allthings.auth.bean.AuthStateBean;
import songqiu.allthings.base.BaseActivity;
import songqiu.allthings.constant.SnsConstants;
import songqiu.allthings.http.BaseBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.http.OkHttp;
import songqiu.allthings.http.RequestCallBack;
import songqiu.allthings.util.LogUtil;
import songqiu.allthings.util.ScrollLinearLayoutManager;
import songqiu.allthings.util.SharedPreferencedUtils;
import songqiu.allthings.util.statusbar.StatusBarUtils;

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
    @BindView(R.id.shadowLayout)
    LinearLayout shadowLayout;

    @BindView(R.id.originalRecycle)
    RecyclerView originalRecycle;
    @BindView(R.id.siginRecycle)
    RecyclerView siginRecycle;
    List<String> originalList;
    List<String> siginList;

    OriginalApplyConditionAdapter adapter;
    SignApplyConditionAdapter signApplyConditionAdapter;

    private AuthStateBean stateBean;

    boolean dayModel;
    @Override
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_auth);
        ButterKnife.bind(this);
        titleTv.setText("身份认证");
        dayModel = SharedPreferencedUtils.getBoolean(this,SharedPreferencedUtils.dayModel,true);
        modeUi(dayModel);

    }
    @Override
    public void init() {
        initRecycl();
    }

    public void initRecycl() {
        originalList = new ArrayList<>();
        siginList = new ArrayList<>();
        adapter = new OriginalApplyConditionAdapter(this,originalList);
        signApplyConditionAdapter = new SignApplyConditionAdapter(this,siginList);
        ScrollLinearLayoutManager linearLayoutManager = new ScrollLinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        linearLayoutManager.setmCanVerticalScroll(false);
        originalRecycle.setLayoutManager(linearLayoutManager);
        originalRecycle.setAdapter(adapter);
        ScrollLinearLayoutManager linearLayoutManager1 = new ScrollLinearLayoutManager(this);
        linearLayoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
        linearLayoutManager1.setmCanVerticalScroll(false);
        siginRecycle.setLayoutManager(linearLayoutManager1);
        siginRecycle.setAdapter(signApplyConditionAdapter);
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

    @Override
    protected void onResume() {
        super.onResume();
        originalList.clear();
        siginList.clear();
        getAuthenticationState();
    }

    public void getAuthenticationState() {
        OkHttp.post(this, HttpServicePath.URL_STATE_DATA,new HashMap<>(),new RequestCallBack(){
            @Override
            public void httpResult(BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        String data = gson.toJson(baseBean.data);
                        stateBean = gson.fromJson(data,AuthStateBean.class);
                        if(null == stateBean) return;
                        originalList.addAll(stateBean.data1);
                        siginList.addAll(stateBean.data2);
                        adapter.notifyDataSetChanged();
                        signApplyConditionAdapter.notifyDataSetChanged();
                        updateViewState();
                    }
                });

            }
        });
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
                        if(dayModel) {
                            intent.putExtra("url", SnsConstants.URL_ORIGINAL);
                        }else {
                            intent.putExtra("url", SnsConstants.URL_ORIGINAL_NIGHT);
                        }
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
                    if(dayModel) {
                        intent.putExtra("url", SnsConstants.URL_SIGNING);
                    }else {
                        intent.putExtra("url", SnsConstants.URL_SIGNING_NIGHT);
                    }
                    intent.putExtra("authType",2);
                    startActivity(intent);
                }
        }
    }


}
