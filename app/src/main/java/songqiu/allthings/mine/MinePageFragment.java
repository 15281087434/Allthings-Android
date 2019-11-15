package songqiu.allthings.mine;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import songqiu.allthings.Event.EventTags;
import songqiu.allthings.R;
import songqiu.allthings.activity.CommentWebViewActivity;
import songqiu.allthings.activity.MainActivity;
import songqiu.allthings.adapter.BannerLooperAdapter;
import songqiu.allthings.adapter.BannerMineAdapter;
import songqiu.allthings.articledetail.ArticleDetailActivity;
import songqiu.allthings.base.BaseFragment;
import songqiu.allthings.bean.BannerBean;
import songqiu.allthings.bean.InviteParameterBean;
import songqiu.allthings.bean.RedNewsBean;
import songqiu.allthings.bean.UserCenterBean;
import songqiu.allthings.constant.SnsConstants;
import songqiu.allthings.home.gambit.HotGambitDetailActivity;
import songqiu.allthings.http.BaseBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.http.OkHttp;
import songqiu.allthings.http.RequestCallBack;
import songqiu.allthings.login.LoginActivity;
import songqiu.allthings.mine.attention.AttentionActivity;
import songqiu.allthings.mine.collect.CollectActivity;
import songqiu.allthings.mine.help.FeedbackAndHelpActivity;
import songqiu.allthings.mine.income.IncomeRecordActivity;
import songqiu.allthings.mine.inform.InformActivity;
import songqiu.allthings.mine.setting.SettingActivity;
import songqiu.allthings.mine.userpage.UserPagerActivity;
import songqiu.allthings.util.ClickUtil;
import songqiu.allthings.util.CopyButtonLibrary;
import songqiu.allthings.util.GlideCircleTransform;
import songqiu.allthings.util.LogUtil;
import songqiu.allthings.util.SharedPreferencedUtils;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.util.ToastUtil;
import songqiu.allthings.util.TokenManager;
import songqiu.allthings.util.statusbar.StatusBarUtils;
import songqiu.allthings.videodetail.VideoDetailActivity;
import songqiu.allthings.view.ProGrossCircleView;
import songqiu.allthings.view.banner.ColorPointHintView;
import songqiu.allthings.view.banner.RollPagerView;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/8/6
 *
 *类描述：我的
 *
 ********/
public class MinePageFragment extends BaseFragment {
    @BindView(R.id.userIcon)
    ImageView userIcon;
    @BindView(R.id.userName)
    TextView userName;
    @BindView(R.id.userCode)
    TextView userCode;
    @BindView(R.id.allGoldTv)
    TextView allGoldTv;
    @BindView(R.id.todayGoldTv)
    TextView todayGoldTv;
    @BindView(R.id.dotImg)
    ImageView dotImg;
    @BindView(R.id.roll_page_mine)
    RollPagerView roll_page_mine;

    List<BannerBean> mBannerList;
    BannerMineAdapter mBannerAdapter;

    InviteParameterBean inviteParameterBean;
    String token;

    MainActivity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;//保存Context引用
    }
    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }

    @Override
    public int initView() {
        return R.layout.fragment_mine_page;
    }

    @Override
    public void init() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        initBanner();
        initBannerEvent();
    }

    @Override
    public void onResume() {
        super.onResume();
        token = TokenManager.getRequestToken(activity);
        if(StringUtil.isEmpty(token)) {
            initUi(false);
        }else {
            initUi(true);
            getUserCenter();
            getDot();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void initBannerEvent() {
        roll_page_mine.setOnItemClickListener(position -> {
            if (mBannerList == null || position >= mBannerList.size()) {
                return;
            }
            //0 不跳转 1 跳转文章 2视频 3 h5url 4 收入记录页面 5 邀请好友页面
            Intent intent;
            if(1==mBannerList.get(position).jump_type) { //文章
                intent= new Intent(activity, ArticleDetailActivity.class);
                intent.putExtra("articleid", mBannerList.get(position).url_id);
                startActivity(intent);
            }else if(2==mBannerList.get(position).jump_type) { //视频
                intent= new Intent(activity, VideoDetailActivity.class);
                intent.putExtra("articleid", mBannerList.get(position).url_id);
                startActivity(intent);
            }else if(3==mBannerList.get(position).jump_type) { //h5
                intent= new Intent(activity, CommentWebViewActivity.class);
                intent.putExtra("url", mBannerList.get(position).url);
                startActivity(intent);
            }else if(4==mBannerList.get(position).jump_type) { //
                if(StringUtil.isEmpty(token)) {
                    intent = new Intent(activity,LoginActivity.class);
                }else {
                    intent = new Intent(activity,IncomeRecordActivity.class);
                }
                startActivity(intent);
            }else if(5==mBannerList.get(position).jump_type) { //邀请好友
                if(StringUtil.isEmpty(token)) {
                    intent = new Intent(activity,LoginActivity.class);
                    startActivity(intent);
                }else {
                    getInviteParameter();
                }
            }
        });
    }
    public void initBanner() {
        mBannerList = new ArrayList<>();
        mBannerAdapter = new BannerMineAdapter(roll_page_mine, (ArrayList<BannerBean>) mBannerList);
        roll_page_mine.setAdapter(mBannerAdapter);
        roll_page_mine.setHintView(new ColorPointHintView(activity, Color.WHITE, Color.GRAY));
        roll_page_mine.setHintPadding(0, 0, 0, 10);
        roll_page_mine.resume();
        getBanner();
    }

    public void initUi(boolean isLogin){
       if(isLogin) {
           String nickName = SharedPreferencedUtils.getString(activity,"SYSNICKNAME");
           String invitationCode = SharedPreferencedUtils.getString(activity,"SYSINVITATIONCODE");
           userName.setText(nickName);
           userCode.setText("点击复制邀请码:"+invitationCode);
           userCode.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   String str = userCode.getText().toString();
                   String[] strArray = str.split(":");
                   if(null != strArray && strArray.length>=2) {
                       String code = strArray[1];
                       CopyButtonLibrary copyButtonLibrary = new CopyButtonLibrary(activity,code);
                       copyButtonLibrary.init(code);
                       ToastUtil.showToast(activity,"复制成功:"+code);
                   }
               }
           });
       }else {
           userName.setText("点此登录");
           userCode.setText("登录后,享受更好的阅读体验");
           userIcon.setImageResource(R.mipmap.head_default);
           allGoldTv.setText("0");
           todayGoldTv.setText("0");
       }
    }

    public void initUiData(UserCenterBean userCenterBean) {
        if(null == userCenterBean) return;
        allGoldTv.setText(String.valueOf(userCenterBean.total_coin));
        todayGoldTv.setText(String.valueOf(userCenterBean.today_coin));
        userName.setText(userCenterBean.user_nickname);
        userCode.setText("点击复制邀请码:"+userCenterBean.code);
         RequestOptions options = new RequestOptions()
                .circleCrop().transforms(new GlideCircleTransform(activity))
                .error(R.mipmap.head_default)
                .placeholder(R.mipmap.head_default);
         if(!StringUtil.isEmpty(userCenterBean.avatar)) {
             if(!userCenterBean.avatar.contains("http")) {
                 userCenterBean.avatar = HttpServicePath.BasePicUrl+userCenterBean.avatar;
             }
         }
        Glide.with(activity).load(userCenterBean.avatar).apply(options).into(userIcon);
        //存入userid
        SharedPreferencedUtils.setInteger(activity, "SYSUSERID", userCenterBean.userid);
        SnsConstants.URL_DOWNLOAD = userCenterBean.android_url;
    }

    public void getBanner() {
        Map<String, String> map = new HashMap<>();
        map.put("type",3+""); //1、文章  2、话题 3、会员中心
        OkHttp.post(activity, HttpServicePath.URL_BANNER, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                if(null != activity) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Gson gson = new Gson();
                            String data = gson.toJson(baseBean.data);
                            if (StringUtil.isEmpty(data)) return;
                            List<BannerBean> bannerBeanList = gson.fromJson(data, new TypeToken<List<BannerBean>>() {}.getType());
                            if(null != bannerBeanList && 0!=bannerBeanList.size()) {
                                mBannerList.addAll(bannerBeanList);
                                mBannerAdapter.notifyDataSetChanged();
                                if(1==bannerBeanList.size()) {
                                    roll_page_mine.pause();
                                    roll_page_mine.setHintViewVisibility(false);
                                    roll_page_mine.setScrollable(false);
                                }else {
                                    roll_page_mine.setScrollable(true);
                                }
                            }else {
                                roll_page_mine.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        });
    }

    public void getUserCenter() {
        Map<String, String> map = new HashMap<>();
        OkHttp.post(activity, HttpServicePath.URL_USER_CENTER, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                if(null != activity) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Gson gson = new Gson();
                            String data = gson.toJson(baseBean.data);
                            if (StringUtil.isEmpty(data)) return;
                            UserCenterBean userCenterBean = gson.fromJson(data, UserCenterBean.class);
                            initUiData(userCenterBean);
                        }
                    });
                }
            }
        });
    }

    public void getInviteParameter() {
        Map<String, String> map = new HashMap<>();
        OkHttp.post(activity, HttpServicePath.URL_INVITE_PARAMETER, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                if(null != activity) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Gson gson = new Gson();
                            String data = gson.toJson(baseBean.data);
                            if (StringUtil.isEmpty(data)) return;
                            inviteParameterBean = gson.fromJson(data, InviteParameterBean.class);
                            if(null == inviteParameterBean) return;
                            Intent intent = new Intent(activity,CommentWebViewActivity.class);
                            boolean dayModel = SharedPreferencedUtils.getBoolean(activity,SharedPreferencedUtils.dayModel,true);
                            if(dayModel) {
                                intent.putExtra("url", SnsConstants.getUrlInviteFriend(inviteParameterBean.friend_num,inviteParameterBean.money,inviteParameterBean.total_coin));
                            }else {
                                intent.putExtra("url", SnsConstants.getUrlInviteFriendNight(inviteParameterBean.friend_num,inviteParameterBean.money,inviteParameterBean.total_coin));
                            }
                            startActivity(intent);
                        }
                    });
                }
            }
        });
    }

    public void getDot() {
        Map<String, String> map = new HashMap<>();
        OkHttp.post(activity, HttpServicePath.URL_RED_NEWS, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                if(null != activity) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Gson gson = new Gson();
                            String data = gson.toJson(baseBean.data);
                            if (StringUtil.isEmpty(data)) return;
                            RedNewsBean redNewsBean = gson.fromJson(data, RedNewsBean.class);
                            if(null == redNewsBean)return;
                            if(redNewsBean.num>0) {
                                dotImg.setVisibility(View.VISIBLE);
                            }else {
                                dotImg.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void toLogin(EventTags.LoginLose loginLose) {
        token = TokenManager.getRequestToken(activity);
        initUi(false);
    }


    @OnClick({R.id.settingLayout,R.id.toLoginLayou,R.id.collectLayout,R.id.attentionLayout,R.id.informImg,
                R.id.feedbackAndHelpLayout,R.id.goldWithdrawLayout,R.id.incomeLayout,R.id.inviteLayout})
    public void onViewClick(View view) {//attention
        Intent intent;
        boolean dayModel = SharedPreferencedUtils.getBoolean(activity,SharedPreferencedUtils.dayModel,true);
        switch (view.getId()) {
            case R.id.settingLayout:
                if(ClickUtil.onClick()) {
                    intent = new Intent(activity,SettingActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.toLoginLayou:
                if(ClickUtil.onClick()) {
                    if(StringUtil.isEmpty(token)) {
                        intent = new Intent(activity,LoginActivity.class);
                        startActivity(intent);
                    }else {
                        intent = new Intent(activity,UserPagerActivity.class);
                        intent.putExtra("userId",SharedPreferencedUtils.getInteger(activity,"SYSUSERID",0));
                        startActivity(intent);
                    }
                }
                break;
            case R.id.collectLayout:
                if(ClickUtil.onClick()) {
                    intent = new Intent(activity,CollectActivity.class);
                    startActivity(intent);
//                    if(StringUtil.isEmpty(token)) {
//                        intent = new Intent(activity,LoginActivity.class);
//                        startActivity(intent);
//                    }else {
//                        intent = new Intent(activity,CollectActivity.class);
//                        startActivity(intent);
//                    }
                }
                break;
            case R.id.attentionLayout:
                if(ClickUtil.onClick()) {
                    intent = new Intent(activity,AttentionActivity.class);
                    startActivity(intent);
//                    if(StringUtil.isEmpty(token)) {
//                        intent = new Intent(activity,LoginActivity.class);
//                        startActivity(intent);
//                    }else {
//                        intent = new Intent(activity,AttentionActivity.class);
//                        startActivity(intent);
//                    }
                }
                break;
            case R.id.informImg:
                if(ClickUtil.onClick()) {
                    if(StringUtil.isEmpty(token)) {
                        intent = new Intent(activity,LoginActivity.class);
                        startActivity(intent);
                    }else {
                        intent = new Intent(activity,InformActivity.class);
                        startActivity(intent);
                    }
                }
                break;
            case R.id.feedbackAndHelpLayout:
                if(ClickUtil.onClick()) {
                    intent = new Intent(activity,FeedbackAndHelpActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.goldWithdrawLayout://提现兑换
                if(ClickUtil.onClick()) {
                    if(StringUtil.isEmpty(token)) {
                        intent = new Intent(activity,LoginActivity.class);
                    }else {
                        intent = new Intent(activity,WithdrawActivity.class);
                        intent.putExtra("withdrawType",1);
                    }
                    startActivity(intent);
                }
                break;
            case R.id.incomeLayout: //收入记录
                if(ClickUtil.onClick()) {
                    if(StringUtil.isEmpty(token)) {
                        intent = new Intent(activity,LoginActivity.class);
                    }else {
                        intent = new Intent(activity,IncomeRecordActivity.class);
                    }
                    startActivity(intent);
                }
                break;
            case R.id.inviteLayout://邀请好友
                if(ClickUtil.onClick()) {
                    if(StringUtil.isEmpty(token)) {
                        intent = new Intent(activity,LoginActivity.class);
                        startActivity(intent);
                    }else {
                        getInviteParameter();
                    }
                }
                break;
        }
    }

}
