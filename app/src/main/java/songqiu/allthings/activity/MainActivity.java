package songqiu.allthings.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.google.gson.Gson;
import com.heartfor.heartvideo.video.HeartVideo;
import com.heartfor.heartvideo.video.HeartVideoManager;
import com.heartfor.heartvideo.video.PlayerStatus;
import com.pili.pldroid.player.PLOnInfoListener;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import songqiu.allthings.Event.EventTags;
import songqiu.allthings.R;
import songqiu.allthings.api.AndroidScheduler;
import songqiu.allthings.api.BaseSubscriber;
import songqiu.allthings.api.NetApi;
import songqiu.allthings.articledetail.ArticleDetailActivity;
import songqiu.allthings.base.BaseActivity;
import songqiu.allthings.base.BaseMainActivity;
import songqiu.allthings.bean.BaseBean;
import songqiu.allthings.bean.ChangePage;
import songqiu.allthings.bean.ReadAwardBean;
import songqiu.allthings.home.HomePageFragment;
import songqiu.allthings.home.gambit.GambitDetailActivity;
import songqiu.allthings.home.gambit.HotGambitDetailActivity;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.http.OkHttp;
import songqiu.allthings.http.RequestCallBack;
import songqiu.allthings.look.LookPageFragment;
import songqiu.allthings.mine.MinePageFragment;
import songqiu.allthings.mine.userpage.UserPagerActivity;
import songqiu.allthings.task.TaskPageFragment;
import songqiu.allthings.util.CheckLogin;
import songqiu.allthings.util.ClickUtil;
import songqiu.allthings.util.LocationUtils;
import songqiu.allthings.util.LogUtil;
import songqiu.allthings.util.SharedPreferencedUtils;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.util.ToastUtil;
import songqiu.allthings.util.statusbar.StatusBarUtils;
import songqiu.allthings.util.theme.ThemeManager;
import songqiu.allthings.videodetail.VideoDetailActivity;
import songqiu.allthings.view.CustomCircleProgress;

public class MainActivity extends BaseMainActivity {

    public static final int INDEX_HOME_PAGE = 0;
    public static final int INDEX_LOOK_PAGE = 1;
    public static final int INDEX_TASK_PAGE = 2;
    public static final int INDEX_MINE_PAGE = 3;

    /**
     * 用于fragment管理
     */
    private FragmentManager fragmentManager;

    /**
     * 首页的fragment
     */
    private HomePageFragment homePageFragment = new HomePageFragment();
    /**
     * 快看的fragment
     */
    private LookPageFragment lookPageFragment = new LookPageFragment();
    /**
     * 任务的fragment
     */
    private TaskPageFragment taskPageFragment = new TaskPageFragment();
    /**
     * 我的的fragment
     */
    private MinePageFragment minePageFragment = new MinePageFragment();

    @BindView(R.id.content)
    FrameLayout content;
    @BindView(R.id.homePageImg)
    ImageView homePageImg;
    @BindView(R.id.homePageTv)
    TextView homePageTv;
    @BindView(R.id.homePageLayout)
    LinearLayout homePageLayout;
    @BindView(R.id.lookImg)
    ImageView lookImg;
    @BindView(R.id.lookTv)
    TextView lookTv;
    @BindView(R.id.lookLayout)
    LinearLayout lookLayout;
    @BindView(R.id.taskImg)
    ImageView taskImg;
    @BindView(R.id.taskTv)
    TextView taskTv;
    @BindView(R.id.taskLayout)
    LinearLayout taskLayout;
    @BindView(R.id.mineImg)
    ImageView mineImg;
    @BindView(R.id.mineTv)
    TextView mineTv;
    @BindView(R.id.mineLayout)
    LinearLayout mineLayout;
    @BindView(R.id.shadowLayout)
    LinearLayout shadowLayout;


    MyBroadcastReceiver myBroadcastReceiver;
    //倒计时
    @BindView(R.id.circleProgress)
    CustomCircleProgress circleProgress;

    //奖励
    @BindView(R.id.goldTv)
    TextView goldTv;
    @BindView(R.id.videoAwardLayout)
    LinearLayout videoAwardLayout;
    @BindView(R.id.videoGoldImg)
    ImageView videoGoldImg;
    AnimationDrawable animationDrawable;

    int videoId;
    boolean activityVisible = false;
    int progress;
    int circleTime = 300;
    public final int PROGRESS_CIRCLE_STARTING = 0x110;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case PROGRESS_CIRCLE_STARTING:
                    progress = circleProgress.getProgress();
                    circleProgress.setProgress(++progress);
                    if(progress >= 100){
                        handler.removeMessages(PROGRESS_CIRCLE_STARTING);
                        progress = 0;
                        circleProgress.setProgress(0);
                        circleProgress.setStatus(CustomCircleProgress.Status.End);//修改显示状态为完成
                        //调用接口
                        carryOutTime();
                    }else{
                        //延迟100ms后继续发消息，实现循环，直到progress=100
                        handler.sendEmptyMessageDelayed(PROGRESS_CIRCLE_STARTING, circleTime);
                    }
                    break;
            }
        }
    };

    @Override
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
    }

    @Override
    public void init() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        boolean dayModel = SharedPreferencedUtils.getBoolean(this,SharedPreferencedUtils.dayModel,true);
        Intent intent = getIntent();
        notificationJump(intent);
        StatusBarUtils.with(MainActivity.this).init().setStatusTextColorWhite(true, MainActivity.this);
        modeUi(dayModel);
        fragmentManager = getSupportFragmentManager();
        setTabSelection(INDEX_HOME_PAGE,0);

        animationDrawable = (AnimationDrawable)videoGoldImg.getBackground();
        initBroadcastReceiver();
    }

    public void initBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("start_video");
        intentFilter.addAction("stop_video");
        myBroadcastReceiver = new MyBroadcastReceiver();
        registerReceiver(myBroadcastReceiver, intentFilter);
    }

    private class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("start_video".equals(intent.getAction())) {
                videoId = intent.getIntExtra("videoId",0);
                    //延迟100ms后继续发消息，实现循环，直到progress=100
                if(activityVisible) {
                    if(CheckLogin.isLogin(MainActivity.this)) {
                        if(0!= videoId) {
                            handler.sendEmptyMessageDelayed(PROGRESS_CIRCLE_STARTING, circleTime);
                        }
                    }
                }
            }else if("stop_video".equals(intent.getAction())) {
                    //点击则变成关闭暂停状态
                circleProgress.setStatus(CustomCircleProgress.Status.End);
                //注意，当我们暂停时，同时还要移除消息，不然的话进度不会被停止
                handler.removeMessages(PROGRESS_CIRCLE_STARTING);
                //将当前进度存入本地
                SharedPreferencedUtils.setInteger(MainActivity.this, SharedPreferencedUtils.VEDIO_READ_TIME,circleProgress.getProgress());
//                if(activityVisible) {
//                   if(CheckLogin.isLogin(MainActivity.this)) {
//                       if(0!=videoId) {
//                           circleProgress.setStatus(CustomCircleProgress.Status.End);
//                           //注意，当我们暂停时，同时还要移除消息，不然的话进度不会被停止
//                           handler.removeMessages(PROGRESS_CIRCLE_STARTING);
//                           //将当前进度存入本地
//                           SharedPreferencedUtils.setInteger(MainActivity.this, SharedPreferencedUtils.VEDIO_READ_TIME,circleProgress.getProgress());
//                       }
//                    }
//                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        activityVisible = true;
        circleProgress.setProgress(SharedPreferencedUtils.getInteger(this,SharedPreferencedUtils.VEDIO_READ_TIME,0));
    }

    @Override
    protected void onPause() {
        super.onPause();
        activityVisible = false;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        notificationJump(intent);
    }

    public void notificationJump(Intent intent) {
        if(null == intent)return;
        String type = intent.getStringExtra("type");
        int id = intent.getIntExtra("id",0);
        Intent mIntent = null;
        if("1".equals(type)) {
            mIntent = new Intent(MainActivity.this, ArticleDetailActivity.class);
            mIntent.putExtra("articleid",id);
            startActivity(mIntent);
        }else if("2".equals(type)) {
            mIntent = new Intent(MainActivity.this, VideoDetailActivity.class);
            mIntent.putExtra("articleid",id);
            startActivity(mIntent);
        }else { // 分享
            Uri uri = intent.getData();
            if(uri!=null){
                String shareType = uri.getQueryParameter("type");
                String shareId = uri.getQueryParameter("id");
                if("1".equals(shareType)) {
                    mIntent = new Intent(MainActivity.this, ArticleDetailActivity.class);
                    mIntent.putExtra("articleid",Integer.valueOf(shareId));
                    startActivity(mIntent);
                }else if("2".equals(shareType)) {
                    mIntent = new Intent(MainActivity.this, VideoDetailActivity.class);
                    mIntent.putExtra("articleid",Integer.valueOf(shareId));
                    startActivity(mIntent);
                }else if("3".equals(shareType)) {
                    String isMoment = uri.getQueryParameter("isMoment");
                    if("1".equals(isMoment)) { //朋友圈详情
                        mIntent = new Intent(MainActivity.this, GambitDetailActivity.class);
                    }else {
                        mIntent = new Intent(MainActivity.this, HotGambitDetailActivity.class);
                    }
                    mIntent.putExtra("talkid",Integer.valueOf(shareId));
                    startActivity(mIntent);
                }
            }
        }
    }

    public void modeUi(boolean isDay) {
        if(isDay) {
            shadowLayout.setVisibility(View.GONE);
        }else {
            shadowLayout.setVisibility(View.VISIBLE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void toJump(EventTags.ToJump toJump) {
        int position = toJump.getJump();
        int childPosition = toJump.getChildPosition();
        setTabSelection(position,childPosition);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void dayMoulde(EventTags.DayMoulde dayMoulde) {
        modeUi(dayMoulde.getMoulde());
    }

    /**
     * 根据传入的index参数来设置选中的tab页。
     *
     * @param index 每个tab页对应的下标
     */
    public void setTabSelection(int index,int childPosition) {
        // 每次选中之前先清楚掉上次的选中状态
        clearSelection();
        switch (index) {
            case INDEX_HOME_PAGE:
                homePageImg.setImageResource(R.mipmap.tab_home);
                homePageTv.setTextColor(getResources().getColor(R.color.normal_color));
                if(1==childPosition) {
                    ChangePage changePage = new ChangePage();
                    changePage.type = "home_recommend";
                    EventBus.getDefault().post(changePage);
                }
                swithFragment(mContent, homePageFragment);
                break;
            case INDEX_LOOK_PAGE:
                lookImg.setImageResource(R.mipmap.tab_look);
                lookTv.setTextColor(getResources().getColor(R.color.normal_color));
                if(10==childPosition) {
                    ChangePage changePage = new ChangePage();
                    changePage.type = "look_recommend";
                    EventBus.getDefault().post(changePage);
                }
                swithFragment(mContent, lookPageFragment);
                break;
            case INDEX_TASK_PAGE:
                taskImg.setImageResource(R.mipmap.tab_task);
                taskTv.setTextColor(getResources().getColor(R.color.normal_color));
                swithFragment(mContent, taskPageFragment);
                if(ClickUtil.onClick()) {
                    EventBus.getDefault().post(new EventTags.TaskRefresh());
                }
                break;
            case INDEX_MINE_PAGE:
                mineImg.setImageResource(R.mipmap.tab_mine);
                mineTv.setTextColor(getResources().getColor(R.color.normal_color));
                swithFragment(mContent, minePageFragment);
                break;
            default:
                break;
        }
        //   transaction.commitAllowingStateLoss();
        //使用的commit方法是在Activity的onSaveInstanceState()之后调用的，这样会出错，因为onSaveInstanceState
        //方法是在该Activity即将被销毁前调用，来保存Activity数据的，如果在保存玩状态后再给它添加Fragment就会出错。解决办法就
        //是把commit（）方法替换成 commitAllowingStateLoss()就行了，其效果是一样的。
    }

    private void clearSelection() {
        homePageImg.setImageResource(R.mipmap.tab_home_normal);
        lookImg.setImageResource(R.mipmap.tab_look_normal);
        taskImg.setImageResource(R.mipmap.tab_task_normal);
        mineImg.setImageResource(R.mipmap.tab_mine_normal);
        homePageTv.setTextColor(getResources().getColor(R.color.FF666666));
        lookTv.setTextColor(getResources().getColor(R.color.FF666666));
        taskTv.setTextColor(getResources().getColor(R.color.FF666666));
        mineTv.setTextColor(getResources().getColor(R.color.FF666666));
    }

    Fragment mContent;
    private void swithFragment(Fragment from, Fragment to) {
        try {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            // 开启一个Fragment事务
            if (!to.isAdded()) {
                if (from != null) {
                    transaction.hide(from);
                }
                transaction.add(R.id.content, to).show(to).commitAllowingStateLoss();
            } else {
                transaction.hide(from)
                        .show(to)
                        .commitAllowingStateLoss();
            }
            mContent = to;
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }


    //转圈圈倒计时
    public void carryOutTime() {
        Map<String, String> map = new HashMap<>();
        map.put("articleid", videoId + "");
        OkHttp.post(this, HttpServicePath.URL_READ_VIDEO, map, new RequestCallBack() {
            @Override
            public void httpResult(songqiu.allthings.http.BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        String data = gson.toJson(baseBean.data);
                        if (StringUtil.isEmpty(data)) return;
                        ReadAwardBean readAwardBean = gson.fromJson(data, ReadAwardBean.class);
                        if (null == readAwardBean) return;
                        goldTv.setText("+"+ readAwardBean.coin);
                        videoAwardLayout.setVisibility(View.VISIBLE);
                        animationDrawable.start();
                        new Handler().postDelayed(new Runnable(){
                            public void run() {
                                //execute the task
                                animationDrawable.stop();
                                videoAwardLayout.setVisibility(View.GONE);
                            }
                        }, 1500);
                        handler.sendEmptyMessageDelayed(PROGRESS_CIRCLE_STARTING,circleTime);
                    }
                });
            }
        });
    }


    private long firstTime = 0;

    private void exitBy2Click() {
        //第二次按返回键的时间戳
        long secondTime = System.currentTimeMillis();
        //如果第二次的时间戳减去第一次的时间戳大于2000毫秒，则提示再按一次退出，如果小于2000毫秒则直接退出。
        if (secondTime - firstTime > 2000) {
            //弹出是提示消息，推荐Snackbar
            ToastUtil.showToast("再按一次退出");
            firstTime = secondTime;
        } else {
            finish();
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            HeartVideo heartVideo = HeartVideoManager.getInstance().getCurrPlayVideo();
            if(null != heartVideo && heartVideo.getCurrModeStatus()== PlayerStatus.MODE_FULL_SCREEN) {
                HeartVideoManager.getInstance().getCurrPlayVideo().exitFullScreen();
            }else {
                exitBy2Click(); // 调用双击退出函数
            }
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        unregisterReceiver(myBroadcastReceiver);
    }


    @OnClick({R.id.homePageLayout,R.id.lookLayout,R.id.taskLayout,R.id.mineLayout})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.homePageLayout:
                setTabSelection(INDEX_HOME_PAGE,0);
//                if(effectiveHomeClick) {
//                    setTabSelection(INDEX_HOME_PAGE,0);
//                }
//                if(ClickUtil.onClick()) {
//                    effectiveHomeClick = false;
//                }
                break;
            case R.id.lookLayout:
                setTabSelection(INDEX_LOOK_PAGE,0);
//                if(ClickUtil.onClick()) {
//                    setTabSelection(INDEX_LOOK_PAGE,0);
//                }
                break;
            case R.id.taskLayout:
                setTabSelection(INDEX_TASK_PAGE,0);
//                if(ClickUtil.onClick()) {
//                    setTabSelection(INDEX_TASK_PAGE,0);
//                }
                break;
            case R.id.mineLayout:
                setTabSelection(INDEX_MINE_PAGE,0);
//                if(ClickUtil.onClick()) {
//                    setTabSelection(INDEX_MINE_PAGE,0);
//                }
                break;
        }
    }

}
