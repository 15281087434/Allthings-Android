package songqiu.allthings.mine.help;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import songqiu.allthings.R;
import songqiu.allthings.adapter.FeedbackDetailAdapter;
import songqiu.allthings.base.BaseActivity;
import songqiu.allthings.bean.FeedbackDetailBean;
import songqiu.allthings.bean.UserInfoBean;
import songqiu.allthings.http.BaseBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.http.OkHttp;
import songqiu.allthings.http.RequestCallBack;
import songqiu.allthings.util.ClickUtil;
import songqiu.allthings.util.DateUtil;
import songqiu.allthings.util.GlideCircleTransform;
import songqiu.allthings.util.GlideLoadUtils;
import songqiu.allthings.util.LogUtil;
import songqiu.allthings.util.SharedPreferencedUtils;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.util.statusbar.StatusBarUtils;
import songqiu.allthings.util.theme.ThemeManager;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/9/12
 *
 *类描述：反馈详情
 *
 ********/
public class FeedbackDetailActivity extends BaseActivity {

    @BindView(R.id.backImg)
    ImageView backImg;
    @BindView(R.id.titleTv)
    TextView titleTv;
    @BindView(R.id.rightTv)
    TextView rightTv;
    @BindView(R.id.line)
    LinearLayout line;
    @BindView(R.id.userIcon)
    ImageView userIcon;
    @BindView(R.id.userName)
    TextView userName;
    @BindView(R.id.timeTv)
    TextView timeTv;
    @BindView(R.id.userLayout)
    RelativeLayout userLayout;
    @BindView(R.id.contentTv)
    TextView contentTv;
    @BindView(R.id.gridView)
    GridView gridView;
    @BindView(R.id.replyTv)
    TextView replyTv;
    @BindView(R.id.emptyLayout)
    RelativeLayout emptyLayout;
    @BindView(R.id.emptyHintTv)
    TextView emptyHintTv;
    @BindView(R.id.shadowLayout)
    LinearLayout shadowLayout;
    List<String> list ;
    FeedbackDetailAdapter adapter;


    @Override
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_feedback_detail);
    }

    @Override
    public void init() {
        boolean dayModel = SharedPreferencedUtils.getBoolean(this,SharedPreferencedUtils.dayModel,true);
        modeUi(dayModel);
        titleTv.setText("我的反馈");
        int id = getIntent().getIntExtra("fid",0);
        list = new ArrayList<>();
        initGridView();
        getFeedbackDetail(id);
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

    public void initGridView() {
        adapter = new FeedbackDetailAdapter(this,list);
        gridView.setAdapter(adapter);
    }

    public void initUi(FeedbackDetailBean feedbackDetailBean) {
        if(null == feedbackDetailBean) return;
        RequestOptions options = new RequestOptions()
                .circleCrop().transforms(new GlideCircleTransform(this))
                .error(R.mipmap.head_default)
                .placeholder(R.mipmap.head_default);
        if(!StringUtil.isEmpty(feedbackDetailBean.avatar)) {
            if(!feedbackDetailBean.avatar.contains("http")) {
                feedbackDetailBean.avatar = HttpServicePath.BasePicUrl+feedbackDetailBean.avatar;
            }
        }
        GlideLoadUtils.getInstance().glideLoadHead(this,feedbackDetailBean.avatar,userIcon);
        userName.setText(feedbackDetailBean.user_nickname);
        contentTv.setText(feedbackDetailBean.feedback_con);
        long time = feedbackDetailBean.created*1000;
        timeTv.setText(DateUtil.getTimeBig4(time));
        if(1==feedbackDetailBean.status) {
            replyTv.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
            emptyHintTv.setText("暂时没有回复哦!");
        }else {
            replyTv.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);
            replyTv.setText(feedbackDetailBean.official);
            replyTv.setTextColor(getResources().getColor(R.color.FF666666));
        }
    }

    public void getFeedbackDetail(int id) {
        Map<String, String> map = new HashMap<>();
        map.put("fid",id+"");
        OkHttp.post(this, HttpServicePath.URL_FEEDBACK_DETAIL, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        String data = gson.toJson(baseBean.data);
                        if (StringUtil.isEmpty(data)) return;
                        FeedbackDetailBean feedbackDetailBean = gson.fromJson(data, FeedbackDetailBean.class);
                        initUi(feedbackDetailBean);
                        if(null != feedbackDetailBean.feedback_img && 0!=feedbackDetailBean.feedback_img.size()) {
                            list.addAll(feedbackDetailBean.feedback_img);
                        }
                        adapter.notifyDataSetChanged();
//                        if(!StringUtil.isEmpty(strString)) {
//                            strString = strString.replace("[","").replace("]","");
//                            LogUtil.i("strString："+strString);
//                            if(!StringUtil.isEmpty(strString)&& strString.contains(",")) {
//                                String[] ss = strString.split(",");
//                                if(null != ss && 0!= ss.length) {
//                                    for(int i = 0;i<ss.length;i++) {
//                                        list.add(ss[i]);
//                                    }
//                                }
//                            }else if(!StringUtil.isEmpty(strString)){
//                                list.add(strString);
//                            }
//                            adapter.notifyDataSetChanged();
//                        }
                    }
                });
            }
        });
    }

    @OnClick({R.id.backImg,R.id.sureTv})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.backImg:
                finish();
                break;
            case R.id.sureTv:
               if(ClickUtil.onClick()) {
                   Intent intent = new Intent(FeedbackDetailActivity.this,MyQuestionActivity.class);
                   startActivity(intent);
                   finish();
               }
                break;
        }
    }

}
