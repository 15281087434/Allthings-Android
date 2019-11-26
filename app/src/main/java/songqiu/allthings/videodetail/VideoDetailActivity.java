package songqiu.allthings.videodetail;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.heartfor.heartvideo.video.HeartVideo;
import com.heartfor.heartvideo.video.HeartVideoInfo;
import com.heartfor.heartvideo.video.HeartVideoManager;
import com.heartfor.heartvideo.video.PlayerStatus;
import com.heartfor.heartvideo.video.VideoControl;
import com.mob.MobSDK;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.BinaryOperator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import pl.droidsonroids.gif.GifImageView;
import songqiu.allthings.Event.EventTags;
import songqiu.allthings.R;
import songqiu.allthings.activity.CommentWebViewActivity;
import songqiu.allthings.adapter.VideoDetailCommentAdapter;
import songqiu.allthings.adapter.VideoDetailIntroAdapter;
import songqiu.allthings.articledetail.ArticleDetailActivity;
import songqiu.allthings.base.BaseActivity;
import songqiu.allthings.bean.AdvertiseBean;
import songqiu.allthings.bean.ReadAwardBean;
import songqiu.allthings.bean.ReportBean;
import songqiu.allthings.bean.VideoDetailBean;
import songqiu.allthings.bean.VideoDetailCommentBean;
import songqiu.allthings.bean.VideoDetailIntroBean;
import songqiu.allthings.constant.SnsConstants;
import songqiu.allthings.http.BaseBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.http.OkHttp;
import songqiu.allthings.http.RequestCallBack;
import songqiu.allthings.iterface.CommentListener;
import songqiu.allthings.iterface.VideoDetailCommentItemListener;
import songqiu.allthings.iterface.WindowShareListener;
import songqiu.allthings.mine.userpage.UserPagerActivity;
import songqiu.allthings.util.CheckLogin;
import songqiu.allthings.util.ClickUtil;
import songqiu.allthings.util.CopyButtonLibrary;
import songqiu.allthings.util.GlideCircleTransform;
import songqiu.allthings.util.GlideLoadUtils;
import songqiu.allthings.util.LogUtil;
import songqiu.allthings.util.ScrollLinearLayoutManager;
import songqiu.allthings.util.SharedPreferencedUtils;
import songqiu.allthings.util.ShowNumUtil;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.util.ToastUtil;
import songqiu.allthings.util.VibratorUtil;
import songqiu.allthings.util.ViewProportion;
import songqiu.allthings.util.WindowUtil;
import songqiu.allthings.util.statusbar.StatusBarUtils;
import songqiu.allthings.util.theme.ShareUrl;
import songqiu.allthings.view.CommentWindow;
import songqiu.allthings.view.CustomCircleProgress;
import songqiu.allthings.view.ReportPopupWindows;
import songqiu.allthings.view.SharePopupWindows;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/8/14
 *
 *类描述：视频详情
 *
 ********/
public class VideoDetailActivity extends BaseActivity {

    @BindView(R.id.videoView)
    HeartVideo videoView;
    @BindView(R.id.backImg)
    ImageView backImg;
    @BindView(R.id.shareImg)
    ImageView shareImg;
    @BindView(R.id.titleLayout)
    RelativeLayout titleLayout;
    @BindView(R.id.userIcon)
    ImageView userIcon;
    @BindView(R.id.attentionTv)
    TextView attentionTv;
    @BindView(R.id.collectImg)
    ImageView collectImg;
    @BindView(R.id.likeImg)
    ImageView likeImg;
    @BindView(R.id.commentNumTv)
    TextView commentNumTv;
    @BindView(R.id.lookNumTv)
    TextView lookNumTv;
    @BindView(R.id.originalTv)
    TextView originalTv;
    @BindView(R.id.videoRecycl)
    RecyclerView videoRecycl;
    @BindView(R.id.commentRecycl)
    RecyclerView commentRecycl;
    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout smartRefreshLayout;
    @BindView(R.id.scrollView)
    ScrollView scrollView;
    @BindView(R.id.shadowLayout)
    LinearLayout shadowLayout;
    @BindView(R.id.emptyLayout)
    LinearLayout emptyLayout;
    @BindView(R.id.prestrainLayout)
    RelativeLayout prestrainLayout;

    List<VideoDetailIntroBean> item;
    VideoDetailIntroAdapter videoDetailIntroAdapter;

    List<VideoDetailCommentBean> item1;
    VideoDetailCommentAdapter videoDetailCommentAdapter;
    VideoDetailBean videoDetailBean;

    int articleid;
    int videoId;
    @BindView(R.id.titleTv)
    TextView titleTv;
    @BindView(R.id.userName)
    TextView userName;
    @BindView(R.id.likeNumTv)
    TextView likeNumTv;
    int pageNo = 1;

    //广告
    @BindView(R.id.advertisingImg)
    GifImageView advertisingImg;
    @BindView(R.id.advertisingVideoView)
    HeartVideo advertisingVideoView;
    @BindView(R.id.advertisingTitleTv)
    TextView advertisingTitleTv;
    @BindView(R.id.downloadLayout)
    LinearLayout downloadLayout;
    @BindView(R.id.jumpLayout)
    RelativeLayout jumpLayout;
    @BindView(R.id.advertisingLayout)
    LinearLayout advertisingLayout;
    AdvertiseBean advertiseBean;

    //倒计时
    @BindView(R.id.goldTv)
    TextView goldTv;
    @BindView(R.id.circleProgress)
    CustomCircleProgress circleProgress;
    AnimationDrawable animationDrawable;
    MyBroadcastReceiver myBroadcastReceiver;

    //奖励
    @BindView(R.id.videoAwardLayout)
    LinearLayout videoAwardLayout;
    @BindView(R.id.videoGoldImg)
    ImageView videoGoldImg;

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
                        if(null != videoDetailBean) {
                            //调用接口
                            carryOutTime();
                        }
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
        setContentView(R.layout.activity_video_detail);
    }

    @Override
    public void init() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        StatusBarUtils.with(VideoDetailActivity.this).init().setStatusTextColorWhite(true, VideoDetailActivity.this);
        boolean dayModel = SharedPreferencedUtils.getBoolean(this, SharedPreferencedUtils.dayModel, true);
        modeUi(dayModel);
        articleid = getIntent().getIntExtra("articleid", 0);
        animationDrawable = (AnimationDrawable)videoGoldImg.getBackground();
        viewPro();
        initRecycl();
        getVideoDtailData(articleid);
        getComment(articleid, pageNo,false);
        getVoide();
        initBroadcastReceiver();
        getAdvertise();
        getReadlog();
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
            videoId = intent.getIntExtra("videoId",0);
            if ("start_video".equals(intent.getAction())) {
                    //延迟100ms后继续发消息，实现循环，直到progress=100
                    if(CheckLogin.isLogin(VideoDetailActivity.this)) {
                        if(0!=videoId) {
                        handler.sendEmptyMessageDelayed(PROGRESS_CIRCLE_STARTING, circleTime);
                    }
                }
            }else if("stop_video".equals(intent.getAction())) {
                //点击则变成关闭暂停状态
//                LogUtil.i("***************stop_video："+circleProgress.getProgress());
                if(CheckLogin.isLogin(VideoDetailActivity.this)) {
                    if(0!=videoId) {
                        circleProgress.setStatus(CustomCircleProgress.Status.End);
                        //注意，当我们暂停时，同时还要移除消息，不然的话进度不会被停止
                        handler.removeMessages(PROGRESS_CIRCLE_STARTING);
                        //将当前进度存入本地
//                SharedPreferencedUtils.setInteger(VideoDetailActivity.this,SharedPreferencedUtils.VEDIO_READ_TIME,circleProgress.getProgress());
                    }
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //点击则变成关闭暂停状态
        circleProgress.setStatus(CustomCircleProgress.Status.End);
        //注意，当我们暂停时，同时还要移除消息，不然的话进度不会被停止
        handler.removeMessages(PROGRESS_CIRCLE_STARTING);
        //将当前进度存入本地
        SharedPreferencedUtils.setInteger(VideoDetailActivity.this,SharedPreferencedUtils.VEDIO_READ_TIME,circleProgress.getProgress());
    }

    public void viewPro() {
        videoView.post(new Runnable() {
            @Override
            public void run() {
                videoView.setLayoutParams(ViewProportion.getFrameParams(videoView, 1.77));
            }
        });
    }

    public void initRecycl() {
        item = new ArrayList<>();
        videoDetailIntroAdapter = new VideoDetailIntroAdapter(this, item);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        videoRecycl.setLayoutManager(linearLayoutManager);
        videoRecycl.setAdapter(videoDetailIntroAdapter);
        videoRecycl.setNestedScrollingEnabled(false);//禁止rcyc嵌套滑动

        item1 = new ArrayList<>();
        videoDetailCommentAdapter = new VideoDetailCommentAdapter(this, item1);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this);
        linearLayoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
        commentRecycl.setLayoutManager(linearLayoutManager1);
        commentRecycl.setAdapter(videoDetailCommentAdapter);

        smartRefreshLayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                pageNo = pageNo + 1;
                getComment(articleid, pageNo,false);
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                pageNo = 1;
                getComment(videoId, pageNo,true);
            }
        });


        videoDetailCommentAdapter.setVideoDetailCommentItemListener(new VideoDetailCommentItemListener() {
            @Override
            public void addLike(String url, int type, int mid, VideoDetailCommentBean videoDetailCommentBean) {
                like(url, type, mid, videoDetailCommentBean);
            }

            @Override
            public void toReport(int id, int commentId, int articleid, int type, int position) {
                int userId = SharedPreferencedUtils.getInteger(VideoDetailActivity.this, "SYSUSERID", 0);
                if (userId == id) {
                    delComment(commentId, articleid, type, position);
                } else {
                    showReportWindow(commentId,4);
                }
            }
        });
    }

    public void modeUi(boolean isDay) {
        if (isDay) {
            shadowLayout.setVisibility(View.GONE);
        } else {
            shadowLayout.setVisibility(View.VISIBLE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void dayMoulde(EventTags.DayMoulde dayMoulde) {
        modeUi(dayMoulde.getMoulde());
    }

    public void getVideoDtailData(int articleid) {
        Map<String, String> map = new HashMap<>();
        map.put("articleid", articleid + "");
        map.put("type", 2 + "");
        OkHttp.post(this, HttpServicePath.URL_DETAILS, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        String data = gson.toJson(baseBean.data);
                        if (StringUtil.isEmpty(data)) return;
                        videoDetailBean = gson.fromJson(data, VideoDetailBean.class);
                        setUi(videoDetailBean);
                    }
                });
            }
        });
    }

    public void getComment(int articleid, int page, boolean ringDown) {
        Map<String, String> map = new HashMap<>();
        map.put("articleid", articleid + "");
        map.put("type", 2 + "");
        map.put("page", page + "");
        map.put("num", 10 + "");
        OkHttp.post(this, smartRefreshLayout, HttpServicePath.URL_COMMENT, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        String data = gson.toJson(baseBean.data);
                        if (StringUtil.isEmpty(data)) return;
                        List<VideoDetailCommentBean> videoDetailCommentBeanList = gson.fromJson(data, new TypeToken<List<VideoDetailCommentBean>>() {
                        }.getType());
                        if (page == 1) {
                            item1.clear();
                            if (null != videoDetailCommentBeanList && 0 != videoDetailCommentBeanList.size()) {
                                emptyLayout.setVisibility(View.GONE);
                                commentRecycl.setVisibility(View.VISIBLE);
                            } else {
                                emptyLayout.setVisibility(View.VISIBLE);
                                commentRecycl.setVisibility(View.GONE);
                            }
                        }
                        if (null != videoDetailCommentBeanList && 0 != videoDetailCommentBeanList.size()) {
                            item1.addAll(videoDetailCommentBeanList);
                            videoDetailCommentAdapter.notifyDataSetChanged();
                        }

                        if(ringDown) {
                            VibratorUtil.ringDown(VideoDetailActivity.this);
                        }
                    }
                });
            }
        });
    }

    public void getVoide() {
        Map<String, String> map = new HashMap<>();
        map.put("type", 2 + "");
        OkHttp.post(this, smartRefreshLayout, HttpServicePath.URL_RAND, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        String data = gson.toJson(baseBean.data);
                        if (StringUtil.isEmpty(data)) return;
                        List<VideoDetailIntroBean> videoDetailIntroBean = gson.fromJson(data, new TypeToken<List<VideoDetailIntroBean>>() {
                        }.getType());
                        if (null != videoDetailIntroBean && 0 != videoDetailIntroBean.size()) {
                            item.addAll(videoDetailIntroBean);
                            videoDetailIntroAdapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        });
    }

    public void getReadlog() {
        Map<String, String> map = new HashMap<>();
        map.put("type", 2 + "");
        map.put("mid", articleid + "");
        OkHttp.post(this, HttpServicePath.URL_MY_READLOG, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {

            }
        });
    }

    public void getAdvertise() {
        Map<String, String> map = new HashMap<>();
        map.put("category", 6 + "");
        OkHttp.post(this, HttpServicePath.URL_ADVERTISE, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        String data = gson.toJson(baseBean.data);
                        if (StringUtil.isEmpty(data)) return;
                        List<AdvertiseBean> advertiseBeanListBean = gson.fromJson(data, new TypeToken<List<AdvertiseBean>>() {}.getType());
                        if(null==advertiseBeanListBean || 0==advertiseBeanListBean.size()) return;
                        advertiseBean = advertiseBeanListBean.get(0);
                        if (null != advertiseBean) {
                            String url = advertiseBean.url.replaceAll("\"","");;
                            if (!StringUtil.isEmpty(url)) {
                                if (!url.contains("http")) {
                                    url = HttpServicePath.BasePicUrl + url;
                                }
                            }
                            advertisingLayout.setVisibility(View.VISIBLE);
                            advertisingTitleTv.setText(advertiseBean.title);
                            if (1 == advertiseBean.type) { //广告图片
                                advertisingImg.setVisibility(View.VISIBLE);
                                RequestOptions options = new RequestOptions()
                                        .error(R.mipmap.pic_default)
                                        .placeholder(R.mipmap.pic_default);
                                GlideLoadUtils.getInstance().glideLoad(VideoDetailActivity.this,url,options,advertisingImg);
                                if (2 == advertiseBean.change_type) { //大图无下载
                                    downloadLayout.setVisibility(View.GONE);
                                }
                            }else { //广告视频
                                advertisingVideoView.setVisibility(View.VISIBLE);
                                String path = advertiseBean.video_url;//
                                if(!StringUtil.isEmpty(path)) {
                                    HeartVideoInfo info = HeartVideoInfo.Builder().setTitle("").setPath(path).setImagePath(url).setSaveProgress(false).builder();
                                    VideoControl control = new VideoControl(VideoDetailActivity.this);
                                    control.setInfo(info);
                                    advertisingVideoView.setHeartVideoContent(control);
                                }
                                if (5 == advertiseBean.change_type) { //大图无下载
                                    downloadLayout.setVisibility(View.GONE);
                                }
                            }
                        }
                    }
                });
            }
        });
    }

    @SuppressLint("NewApi")
    public void setUi(VideoDetailBean videoDetailBean) {
        if (null == videoDetailBean) return;
        prestrainLayout.setVisibility(View.GONE);
        if (!StringUtil.isEmpty(videoDetailBean.video_url)) {
            if(null != VideoDetailActivity.this && !VideoDetailActivity.this.isDestroyed()) {
                if (!videoDetailBean.photo.contains("http")) {
                    videoDetailBean.photo = HttpServicePath.BasePicUrl + videoDetailBean.photo;
                }
                if(!StringUtil.isEmpty(videoDetailBean.video_url)) { //
                    HeartVideoInfo info = HeartVideoInfo.Builder().setTitle("").setPath(videoDetailBean.video_url).setImagePath(videoDetailBean.photo)
                            .setSaveProgress(false).setVideoId(videoDetailBean.articleid).builder();
                    VideoControl control = new VideoControl(VideoDetailActivity.this);
                    control.setInfo(info);
                    videoView.setHeartVideoContent(control);
                }
            }
        }
        lookNumTv.setText(ShowNumUtil.showUnm1(videoDetailBean.view_num)+"万次播放");
        if(0==videoDetailBean.is_original) {
            originalTv.setText("原创");
            originalTv.setTextColor(getResources().getColor(R.color.FFDE5C51));
        }else {
            originalTv.setText("转载");
        }
        titleTv.setText(videoDetailBean.title);
        if (!videoDetailBean.avatar.contains("http")) {
            videoDetailBean.avatar = HttpServicePath.BasePicUrl + videoDetailBean.avatar;
        }
        GlideLoadUtils.getInstance().glideLoadHead(this,videoDetailBean.avatar,userIcon);
        userName.setText(videoDetailBean.user_nickname);
        likeNumTv.setText(ShowNumUtil.showUnm(videoDetailBean.up_num));
        commentNumTv.setText(String.valueOf(videoDetailBean.comment_num));
        if (0 == videoDetailBean.is_follow) {
            attentionTv.setText("关注");
            attentionTv.setBackgroundResource(R.drawable.rectangle_common_attention);
        } else {
            attentionTv.setText("已关注");
            attentionTv.setBackgroundResource(R.drawable.rectangle_common_no_attention);
        }

        if (0 == videoDetailBean.is_collect) {
            collectImg.setImageResource(R.mipmap.item_collect);
        } else {
            collectImg.setImageResource(R.mipmap.item_collect_pre);
        }

        if (0 == videoDetailBean.is_up) {
            likeImg.setImageResource(R.mipmap.item_like);
            likeNumTv.setTextColor(getResources().getColor(R.color.FF666666));
        } else {
            likeImg.setImageResource(R.mipmap.item_like_pre);
            likeNumTv.setTextColor(getResources().getColor(R.color.FFDE5C51));
        }
//        if(null != videoDetailBean) {
//            if(0 == videoDetailBean.is_coin) {
//                //登录之后开始开始转圈
//                if (CheckLogin.isLogin(VideoDetailActivity.this)) {
//                    circleProgress.setProgress(SharedPreferencedUtils.getInteger(VideoDetailActivity.this,SharedPreferencedUtils.VEDIO_READ_TIME,0));
//                }
//            }
//        }
        circleProgress.setProgress(SharedPreferencedUtils.getInteger(VideoDetailActivity.this,SharedPreferencedUtils.VEDIO_READ_TIME,0));
    }

    //评论点赞/取消点赞
    public void like(String url, int type, int mid, VideoDetailCommentBean videoDetailCommentBean) {
        Map<String, String> map = new HashMap<>();
        map.put("type", type + "");
        map.put("mid", mid + "");
        OkHttp.post(this, url, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (url.equals(HttpServicePath.URL_LIKE)) {
                            videoDetailCommentBean.up_num = videoDetailCommentBean.up_num + 1;
                            videoDetailCommentBean.is_up = 1;
                        } else {
                            videoDetailCommentBean.up_num = videoDetailCommentBean.up_num - 1;
                            videoDetailCommentBean.is_up = 0;
                        }
                        videoDetailCommentAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    //视频取消点赞/点赞
    public void addLike(String url, int type, int mid) {
        Map<String, String> map = new HashMap<>();
        map.put("type", type + "");
        map.put("mid", mid + "");
        OkHttp.post(this, url, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        EventBus.getDefault().post(new EventTags.RefreshLook(url,mid));
                        if (url.equals(HttpServicePath.URL_LIKE)) {
                            videoDetailBean.is_up = 1;
                            videoDetailBean.up_num = videoDetailBean.up_num + 1;
                            likeNumTv.setText(String.valueOf(videoDetailBean.up_num));
                            likeNumTv.setTextColor(getResources().getColor(R.color.FFDE5C51));
                            likeImg.setImageResource(R.mipmap.item_like_pre);
                        } else {
                            videoDetailBean.is_up = 0;
                            videoDetailBean.up_num = videoDetailBean.up_num - 1;
                            likeNumTv.setText(String.valueOf(videoDetailBean.up_num));
                            likeNumTv.setTextColor(getResources().getColor(R.color.FF666666));
                            likeImg.setImageResource(R.mipmap.item_like);
                        }
                    }
                });
            }
        });
    }

    //添加评论
    public void addComment(int type, int articleid, String content) { //评论类型,1=文章，2=视频，3=话题
        Map<String, String> map = new HashMap<>();
        map.put("type", type + "");
        map.put("articleid", articleid + "");
        map.put("content", content);
        OkHttp.post(this, HttpServicePath.URL_ADD_COMMENT, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pageNo = 1;
                        getComment(articleid, pageNo,false);
                        //评论数
                        videoDetailBean.comment_num = videoDetailBean.comment_num + 1;
                        commentNumTv.setText(String.valueOf(videoDetailBean.comment_num));
                    }
                });
            }
        });
    }

    //删除评论
    public void delComment(int commentId, int articleid, int type, int position) {
        Map<String, String> map = new HashMap<>();
        map.put("commentid", commentId + "");
        map.put("articleid", articleid + "");
        map.put("type", type + "");
        OkHttp.post(this, HttpServicePath.URL_DEL_COMMENT, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        item1.remove(position);
                        videoDetailCommentAdapter.notifyDataSetChanged();
                        //评论数
                        videoDetailBean.comment_num = videoDetailBean.comment_num - 1;
                        commentNumTv.setText(String.valueOf(videoDetailBean.comment_num));
                        if (null == item1 || 0 == item1.size()) {
                            emptyLayout.setVisibility(View.VISIBLE);
                            commentRecycl.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
    }

    //转圈圈倒计时
    public void carryOutTime() {
        Map<String, String> map = new HashMap<>();
        map.put("articleid", videoDetailBean.articleid + "");
        OkHttp.post(this, HttpServicePath.URL_READ_VIDEO, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
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

    public void showPopupwindow() {
        CommentWindow fw = new CommentWindow(this);
        fw.showAtLocation(titleTv, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        fw.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1.0f;
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                getWindow().setAttributes(lp);
            }
        });
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.6f;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setAttributes(lp);
        fw.setCommentListener(new CommentListener() {
            @Override
            public void publishComment(String comment) {
                if (null != videoDetailBean) {
                    addComment(2, videoDetailBean.articleid, comment);
                }
                fw.dismiss();
            }
        });
    }


    private List<String> getReportTitle() {
        return Arrays.asList("内容低劣", "广告软文", "政治敏感", "色情低俗", "违法信息", "错误观念引导", "人身攻击", "涉嫌侵犯");
    }

    public List<ReportBean> reportList() {
        List<ReportBean> list = new ArrayList<>();
        List<String> titleList = getReportTitle();
        for (String title : titleList) {
            ReportBean reportBean = new ReportBean();
            reportBean.title = title;
            list.add(reportBean);
        }
        return list;
    }

    //举报弹窗
    public void showReportWindow(int mid,int type) {
        ReportPopupWindows rw = new ReportPopupWindows(this, reportList(),mid,type);
        WindowUtil.windowDeploy(this, rw, backImg);
    }


    private void showShare(String platform) {
        final OnekeyShare oks = new OnekeyShare();
        //指定分享的平台，如果为空，还是会调用九宫格的平台列表界面
        if (platform != null) {
            oks.setPlatform(platform);
        }
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(videoDetailBean.title);
        // titleUrl是标题的网络链接，仅在Linked-in,QQ和QQ空间使用
        oks.setTitleUrl(ShareUrl.getUrl(videoDetailBean.articleid, 2));
        // text是分享文本，所有平台都需要这个字段
        oks.setText(videoDetailBean.description);
        if (!StringUtil.isEmpty(videoDetailBean.photo)) {
            if (!videoDetailBean.photo.contains("http")) {
                if (!videoDetailBean.photo.contains("http")) {
                    videoDetailBean.photo = HttpServicePath.BasePicUrl + videoDetailBean.photo;
                }
            }
            oks.setImageUrl(videoDetailBean.photo);
        } else {
            oks.setImageUrl(HttpServicePath.BasePicUrl+"sharelog.png?time="+System.currentTimeMillis());
        }
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(ShareUrl.getUrl(videoDetailBean.articleid, 2));
        //启动分享
        oks.show(MobSDK.getContext());
    }

    //分享弹窗
    public void showShareWindow(int type) {
        SharePopupWindows rw = new SharePopupWindows(this, type, 0);
        WindowUtil.windowDeploy(this, rw, backImg);
        rw.setWindowShareListener(new WindowShareListener() {
            @Override
            public void qqShare(int position) {
                if (null == videoDetailBean) return;
                showShare(QQ.NAME);
                totalShare(2, videoDetailBean.articleid);
                rw.dismiss();
            }

            @Override
            public void wechatShare(int position) {
                if (null == videoDetailBean) return;
                showShare(Wechat.NAME);
                totalShare(2, videoDetailBean.articleid);
                rw.dismiss();
            }

            @Override
            public void wechatFriendShare(int position) {
                if (null == videoDetailBean) return;
                showShare(WechatMoments.NAME);
                totalShare(2, videoDetailBean.articleid);
                rw.dismiss();
            }

            @Override
            public void link(int position) {
                if (null == videoDetailBean) return;
                String link = ShareUrl.getUrl(videoDetailBean.articleid, 2);
                CopyButtonLibrary copyButtonLibrary = new CopyButtonLibrary(VideoDetailActivity.this, link);
                copyButtonLibrary.init(link);
                ToastUtil.showToast(VideoDetailActivity.this, "复制成功!");
            }

            @Override
            public void report() {
                if (null == videoDetailBean) return;
                showReportWindow(videoDetailBean.articleid,2);
                rw.dismiss();
            }

            @Override
            public void daytime() {
            }

            @Override
            public void night() {
                boolean dayModel = SharedPreferencedUtils.getBoolean(VideoDetailActivity.this, SharedPreferencedUtils.dayModel, true);
                if (dayModel) {
                    SharedPreferencedUtils.setBoolean(VideoDetailActivity.this, SharedPreferencedUtils.dayModel, false);
                    EventBus.getDefault().post(new EventTags.DayMoulde(false));
                } else {
                    SharedPreferencedUtils.setBoolean(VideoDetailActivity.this, SharedPreferencedUtils.dayModel, true);
                    EventBus.getDefault().post(new EventTags.DayMoulde(true));
                }
            }
        });
    }

    public void addFollow(int parentid, int type) { //用户id 	type、1=添加关注，2=取消关注
        Map<String, String> map = new HashMap<>();
        map.put("parentid", parentid + "");
        map.put("type", type + "");
        OkHttp.post(this, HttpServicePath.URL_ADD_FOLLOW, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (1 == type) {
                            videoDetailBean.is_follow = 1;
                            attentionTv.setText("已关注");
                            attentionTv.setBackgroundResource(R.drawable.rectangle_common_no_attention);
                            //点赞通知
                            EventBus.getDefault().post(new EventTags.Attention(parentid, 1));
                        } else {
                            videoDetailBean.is_follow = 0;
                            attentionTv.setText("关注");
                            attentionTv.setBackgroundResource(R.drawable.rectangle_common_attention);
                            //取消点赞通知
                            EventBus.getDefault().post(new EventTags.Attention(parentid, 0));
                        }
                    }
                });
            }
        });
    }

    public void addCollect(String url, int type, int mid) { //收藏/取消收藏
        Map<String, String> map = new HashMap<>();
        map.put("type", type + "");
        map.put("mid", mid + "");
        OkHttp.post(this, url, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (url.equals(HttpServicePath.URL_COLLECT)) {
                            videoDetailBean.is_collect = 1;
                            collectImg.setImageResource(R.mipmap.item_collect_pre);
                        } else {
                            videoDetailBean.is_collect = 0;
                            collectImg.setImageResource(R.mipmap.item_collect);
                        }
                    }
                });
            }
        });
    }


    @Override
    public void onStop() {
        super.onStop();
        HeartVideoManager.getInstance().release();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        unregisterReceiver(myBroadcastReceiver);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            HeartVideo heartVideo = HeartVideoManager.getInstance().getCurrPlayVideo();
            if(null != heartVideo && heartVideo.getCurrModeStatus()== PlayerStatus.MODE_FULL_SCREEN) {
                HeartVideoManager.getInstance().getCurrPlayVideo().exitFullScreen();
            }else {
                finish();
            }
        }
        return false;
    }

    @OnClick({R.id.titleLayout, R.id.backImg, R.id.showEdit, R.id.likeLayout, R.id.attentionTv,R.id.shareFriendLayout,R.id.shareWxLayout,
            R.id.collectImg, R.id.settingImg, R.id.shareImg, R.id.lookCommentImg, R.id.userLayout,R.id.jumpLayout,R.id.advertisingImg})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.titleLayout:
                break;
            case R.id.backImg:
                finish();
                break;
            case R.id.showEdit:
                if (null != videoDetailBean) {
                    if (1 == videoDetailBean.is_comment) {
                        showPopupwindow();
                    } else {
                        ToastUtil.showToast(this, "暂时不能评论!");
                    }
                }
                break;
            case R.id.likeLayout: //点赞
                if (null == videoDetailBean) return;
                if (0 == videoDetailBean.is_up) {//去点赞
                    addLike(HttpServicePath.URL_LIKE, 2, videoDetailBean.articleid);
                } else { //取消点赞
                    addLike(HttpServicePath.URL_NO_LIKE, 2, videoDetailBean.articleid);
                }
                break;
//            case R.id.reportLayout: //举报
//                showReportWindow();
//                break;
            case R.id.attentionTv: //关注
                if (null == videoDetailBean) return;
                if (0 == videoDetailBean.is_follow) {//去关注
                    addFollow(videoDetailBean.userid, 1);
                } else { //取消关注
                    addFollow(videoDetailBean.userid, 2);
                }
                break;
            case R.id.collectImg:
                if (null == videoDetailBean) return;
                if (0 == videoDetailBean.is_collect) {//去收藏
                    addCollect(HttpServicePath.URL_COLLECT, 2, videoDetailBean.articleid);
                } else { //取消收藏
                    addCollect(HttpServicePath.URL_NO_COLLECT, 2, videoDetailBean.articleid);
                }
                break;
            case R.id.settingImg:
                showShareWindow(1);
                break;
            case R.id.shareImg:
                showShareWindow(0);
                break;
            case R.id.lookCommentImg:
                scrollView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.smoothScrollTo(0, 700);
                    }
                }, 200);
                break;
            case R.id.userLayout:
                if (ClickUtil.onClick()) {
                    if(null == videoDetailBean) return;
                    Intent intent = new Intent(VideoDetailActivity.this, UserPagerActivity.class);
                    intent.putExtra("userId", videoDetailBean.userid);
                    startActivity(intent);
                }
                break;
            case R.id.jumpLayout:
            case R.id.advertisingImg:
                if(ClickUtil.onClick()) {
                    if(null == advertiseBean) return;
                    Intent intent = new Intent(VideoDetailActivity.this,CommentWebViewActivity.class);
                    intent.putExtra("url", advertiseBean.jump_url);
                    startActivity(intent);
                }
                break;
            case R.id.shareFriendLayout:
                if(ClickUtil.onClick()) {
                    if (null != videoDetailBean) {
                        showShare(WechatMoments.NAME);
                        totalShare(2, videoDetailBean.articleid);
                    }
                }
                break;
            case R.id.shareWxLayout:
                if(ClickUtil.onClick()) {
                    if (null != videoDetailBean) {
                        showShare(Wechat.NAME);
                        totalShare(2, videoDetailBean.articleid);
                    }
                }
                break;
        }
    }

}
