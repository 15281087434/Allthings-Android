package songqiu.allthings.articledetail;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.heartfor.heartvideo.video.HeartVideo;
import com.heartfor.heartvideo.video.HeartVideoInfo;
import com.heartfor.heartvideo.video.HeartVideoManager;
import com.heartfor.heartvideo.video.PlayerStatus;
import com.heartfor.heartvideo.video.VideoControl;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import pl.droidsonroids.gif.GifImageView;
import songqiu.allthings.Event.EventTags;
import songqiu.allthings.R;
import songqiu.allthings.activity.CommentWebViewActivity;
import songqiu.allthings.adapter.ArticleDetailRandAdapter;
import songqiu.allthings.adapter.LookTabClassAdapter;
import songqiu.allthings.adapter.comment.CommentListAdapter;
import songqiu.allthings.adapter.comment.CommentSubitemHolder;
import songqiu.allthings.adapter.comment.HeaderHolder;
import songqiu.allthings.base.BaseActivity;
import songqiu.allthings.bean.AdvertiseBean;
import songqiu.allthings.bean.ArticleDetailBean;
import songqiu.allthings.bean.ArticleDetailRandBean;
import songqiu.allthings.bean.AwardRuleBean;
import songqiu.allthings.bean.CommentSubitemBean;
import songqiu.allthings.bean.DeleteCommentBean;
import songqiu.allthings.bean.ReadAwardBean;
import songqiu.allthings.bean.ReportBean;
import songqiu.allthings.bean.UnLikeBean;
import songqiu.allthings.bean.DetailCommentListBean;
import songqiu.allthings.comment.CommentDetailActivity;
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
import songqiu.allthings.util.WebViewJsUtil;
import songqiu.allthings.util.CopyButtonLibrary;
import songqiu.allthings.util.DateUtil;
import songqiu.allthings.util.GlideLoadUtils;
import songqiu.allthings.util.LogUtil;
import songqiu.allthings.util.ScrollLinearLayoutManager;
import songqiu.allthings.util.SharedPreferencedUtils;
import songqiu.allthings.util.ShowNumUtil;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.util.ToastUtil;
import songqiu.allthings.util.WindowUtil;
import songqiu.allthings.util.statusbar.StatusBarUtils;
import songqiu.allthings.util.theme.ShareUrl;
import songqiu.allthings.util.theme.ThemeManager;
import songqiu.allthings.view.CommentWindow;
import songqiu.allthings.view.CustomCircleProgress;
import songqiu.allthings.view.DialogAwardRule;
import songqiu.allthings.view.DialogDeleteCommon;
import songqiu.allthings.view.LongClickDialog;
import songqiu.allthings.view.MyScrollView;
import songqiu.allthings.view.ReportPopupWindows;
import songqiu.allthings.view.SharePopupWindows;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/8/26
 *
 *类描述：文章详情页
 *
 ********/
public class ArticleDetailActivity extends BaseActivity implements ThemeManager.OnThemeChangeListener {

    @BindView(R.id.parentLayout)
    RelativeLayout parentLayout;
    @BindView(R.id.relativeLayout)
    RelativeLayout relativeLayout;
    @BindView(R.id.backImg)
    ImageView backImg;
    @BindView(R.id.rightImg)
    ImageView rightImg;
    @BindView(R.id.titleLayout)
    RelativeLayout titleLayout;
    @BindView(R.id.line)
    LinearLayout line;
    @BindView(R.id.lookImg)
    ImageView lookImg;
    @BindView(R.id.titleTv)
    TextView titleTv;
    @BindView(R.id.userTitleLayout)
    LinearLayout userTitleLayout;
    @BindView(R.id.userIcon)
    ImageView userIcon;
    @BindView(R.id.userIconTitleIcon)
    ImageView userIconTitleIcon;
    @BindView(R.id.userName)
    TextView userName;
    @BindView(R.id.userTitleName)
    TextView userTitleName;
    @BindView(R.id.timeTv)
    TextView timeTv;
    @BindView(R.id.lookNumTv)
    TextView lookNumTv;
    @BindView(R.id.attentionTv)
    TextView attentionTv;
    @BindView(R.id.attentionTitleTv)
    TextView attentionTitleTv;
    @BindView(R.id.contentWeb)
    WebView contentWeb;
    @BindView(R.id.originalTv)
    TextView originalTv;
    @BindView(R.id.likeNumTv)
    TextView likeNumTv;
    @BindView(R.id.commentNumTv)
    TextView commentNumTv;
    @BindView(R.id.likeImg)
    ImageView likeImg;
    @BindView(R.id.lookCommentImg)
    ImageView lookCommentImg;
    @BindView(R.id.collectImg)
    ImageView collectImg;
    @BindView(R.id.shareImg)
    ImageView shareImg;
    @BindView(R.id.articleRecycle)
    RecyclerView articleRecycle;
    @BindView(R.id.commentRecycl)
    RecyclerView commentRecycl;
    @BindView(R.id.showEdit)
    TextView showEdit;
    @BindView(R.id.bottomLayout)
    LinearLayout bottomLayout;
    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout smartRefreshLayout;
    @BindView(R.id.scrollView)
    MyScrollView scrollView;
    @BindView(R.id.linearLayout)
    LinearLayout linearLayout;
    @BindView(R.id.prestrainLayout)
    RelativeLayout prestrainLayout;
    @BindView(R.id.shadowLayout)
    LinearLayout shadowLayout;
    @BindView(R.id.emptyLayout)
    LinearLayout emptyLayout;
    @BindView(R.id.likeLayout)
    RelativeLayout likeLayout;
    @BindView(R.id.nuLikeLayout)
    RelativeLayout nuLikeLayout;
    @BindView(R.id.shareFriendLayout)
    RelativeLayout shareFriendLayout;
    @BindView(R.id.shareWxLayout)
    RelativeLayout shareWxLayout;
    @BindView(R.id.line1)
    TextView line1;
    @BindView(R.id.line2)
    TextView line2;

    int articleid;
    int pageNo = 1;

    ArticleDetailBean articleDetailBean;
    ArticleDetailRandAdapter articleDetailRandAdapter;
    List<ArticleDetailRandBean> item;

    List<DetailCommentListBean> item1;
    CommentListAdapter videoDetailCommentAdapter;


    //广告
    @BindView(R.id.advertisingImg)
    GifImageView advertisingImg;
    @BindView(R.id.videoView)
    HeartVideo videoView;
    @BindView(R.id.advertisingTitleTv)
    TextView advertisingTitleTv;
    @BindView(R.id.downloadLayout)
    LinearLayout downloadLayout;
    @BindView(R.id.jumpLayout)
    RelativeLayout jumpLayout;
    @BindView(R.id.advertisingLayout)
    LinearLayout advertisingLayout;
    AdvertiseBean advertiseBean;

    MyBroadcastReceiver myBroadcastReceiver;
    //转圈
    @BindView(R.id.propressLayout)
    LinearLayout propressLayout;
    @BindView(R.id.goldTv)
    TextView goldTv;
    @BindView(R.id.circleProgress)
    CustomCircleProgress circleProgress;


    private String method = WebViewJsUtil.JSCALLJAVA;


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
                        if(null != articleDetailBean) {
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
        setContentView(R.layout.activity_article_detail);
    }

    @Override
    public void init() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        ThemeManager.registerThemeChangeListener(this);

        articleid = getIntent().getIntExtra("articleid", 1);
        smartRefreshLayout.setEnableRefresh(false);
        initTaskDetailView();
        initRecycl();
        //scrollview滚动监听
        scrollViewListener();
        getData();
        getRankArticle();
        getComment(articleid, pageNo);
        getAdvertise();
        getReadlog();
        getAddres();
        initBroadcastReceiver();

        boolean dayModel = SharedPreferencedUtils.getBoolean(this, SharedPreferencedUtils.dayModel, true);
//        modeUi(dayModel);
        if(dayModel) {
            ThemeManager.setThemeMode(ThemeManager.ThemeMode.DAY);
        }else {
            ThemeManager.setThemeMode(ThemeManager.ThemeMode.NIGHT);
        }
    }

    public void initBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("enterFullScreen");
        intentFilter.addAction("exitFullScreen");
        myBroadcastReceiver = new MyBroadcastReceiver();
        registerReceiver(myBroadcastReceiver, intentFilter);
    }

    public void scrollViewListener() {
        scrollView.setOnScrollListener(new MyScrollView.OnScrollListener() {
            @Override
            public void onScroll(int scrollY) {
                if (scrollY > 350) {
                    userTitleLayout.setVisibility(View.VISIBLE);
                } else {
                    userTitleLayout.setVisibility(View.GONE);
                }
            }
        });
    }

    //白天夜间切换
    public void initTheme() {
        StatusBarUtils.with(this)
                .setColor(getResources().getColor(ThemeManager.getCurrentThemeRes(this, R.color.FFF9FAFD)))
                .init()
                .setStatusTextColorAndPaddingTop(true, this);
        parentLayout.setBackgroundColor(getResources().getColor(ThemeManager.getCurrentThemeRes(this, R.color.main_bg_white)));
        line.setBackgroundColor(getResources().getColor(ThemeManager.getCurrentThemeRes(this, R.color.line_color)));
        titleLayout.setBackgroundColor(getResources().getColor(ThemeManager.getCurrentThemeRes(this, R.color.FFF9FAFD)));
        titleTv.setTextColor(getResources().getColor(ThemeManager.getCurrentThemeRes(this, R.color.bottom_tab_tv)));
        timeTv.setTextColor(getResources().getColor(ThemeManager.getCurrentThemeRes(this, R.color.FF999999)));
        lookCommentImg.setImageDrawable(getResources().getDrawable(ThemeManager.getCurrentThemeRes(this,R.mipmap.item_comment)));
        shareImg.setImageDrawable(getResources().getDrawable(ThemeManager.getCurrentThemeRes(this,R.mipmap.item_share)));
        lookImg.setImageDrawable(getResources().getDrawable(ThemeManager.getCurrentThemeRes(this,R.mipmap.icon_look)));
        lookNumTv.setTextColor(getResources().getColor(ThemeManager.getCurrentThemeRes(this, R.color.FF999999)));
        contentWeb.setBackgroundColor(getResources().getColor(ThemeManager.getCurrentThemeRes(this, R.color.main_bg_white)));
        prestrainLayout.setBackgroundColor(getResources().getColor(ThemeManager.getCurrentThemeRes(this, R.color.main_bg_white)));
        if (SharedPreferencedUtils.getBoolean(this, SharedPreferencedUtils.dayModel, true)) {
            if (null != articleDetailBean) {
                contentWeb.loadDataWithBaseURL(null, getHtmlData(articleDetailBean.content), "text/html", "utf-8", null);
            }
        }else {
            if (null != articleDetailBean) {
                contentWeb.loadDataWithBaseURL(null, getHtmlDataNight(articleDetailBean.content), "text/html", "utf-8", null);
            }
        }
        //
        likeLayout.setBackgroundDrawable(getResources().getDrawable(ThemeManager.getCurrentThemeRes(this,R.drawable.article_tv_bg)));
        nuLikeLayout.setBackgroundDrawable(getResources().getDrawable(ThemeManager.getCurrentThemeRes(this,R.drawable.article_tv_bg)));
        shareFriendLayout.setBackgroundDrawable(getResources().getDrawable(ThemeManager.getCurrentThemeRes(this,R.drawable.article_tv_bg)));
        shareWxLayout.setBackgroundDrawable(getResources().getDrawable(ThemeManager.getCurrentThemeRes(this,R.drawable.article_tv_bg)));
        jumpLayout.setBackgroundDrawable(getResources().getDrawable(ThemeManager.getCurrentThemeRes(this,R.drawable.rectangle_f0f0f0_2px_white)));
        advertisingTitleTv.setTextColor(getResources().getColor(ThemeManager.getCurrentThemeRes(this, R.color.bottom_tab_tv)));
        line1.setBackgroundDrawable(getResources().getDrawable(ThemeManager.getCurrentThemeRes(this, R.color.line_color)));
        line2.setBackgroundDrawable(getResources().getDrawable(ThemeManager.getCurrentThemeRes(this, R.color.line_color)));
        bottomLayout.setBackgroundDrawable(getResources().getDrawable(ThemeManager.getCurrentThemeRes(this, R.color.FFF9FAFD)));
        showEdit.setBackgroundDrawable(getResources().getDrawable(ThemeManager.getCurrentThemeRes(this, R.drawable.bg_search_gary)));
        if(null != articleDetailBean && 0!= articleDetailBean.is_collect) {
            //对收藏图标不做处理
        }else {
            collectImg.setImageDrawable(getResources().getDrawable(ThemeManager.getCurrentThemeRes(this,R.mipmap.item_collect)));
        }
    }

    //    @SuppressLint({"SetJavaScriptEnabled", "ClickableViewAccessibility"})
    private void initTaskDetailView() {
        contentWeb.getSettings().setJavaScriptEnabled(true);
        contentWeb.getSettings().setDomStorageEnabled(true);
        contentWeb.getSettings().setBuiltInZoomControls(true);
        contentWeb.getSettings().setDisplayZoomControls(false);
        contentWeb.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY); //取消滚动条白边效果
        contentWeb.getSettings().setDefaultTextEncodingName("UTF-8");
        contentWeb.getSettings().setBlockNetworkImage(false);
        contentWeb.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        contentWeb.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        contentWeb.getSettings().setAllowFileAccess(true);
        contentWeb.getSettings().setSupportZoom(false);
        contentWeb.getSettings().setLoadWithOverviewMode(true);
        contentWeb.getSettings().setUseWideViewPort(true);
        contentWeb.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);  //设置 缓存模式
        contentWeb.setVerticalScrollBarEnabled(false); //垂直不显示
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            contentWeb.setNestedScrollingEnabled(false);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            contentWeb.getSettings().setMixedContentMode(WebSettings
                    .MIXED_CONTENT_ALWAYS_ALLOW);  //注意安卓5.0以上的权限
        }
        contentWeb.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                prestrainLayout.setVisibility(View.GONE);
                WebViewJsUtil.setWebImageClick(view,method);
            }
        });

        contentWeb.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return true;
            }
        });
        contentWeb.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
    }


    public void initRecycl() {
        item = new ArrayList<>();
        articleDetailRandAdapter = new ArticleDetailRandAdapter(this, item);
        ScrollLinearLayoutManager linearLayoutManager = new ScrollLinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        linearLayoutManager.setmCanVerticalScroll(false);
        articleRecycle.setLayoutManager(linearLayoutManager);
        articleRecycle.setAdapter(articleDetailRandAdapter);

        item1 = new ArrayList<>();
        videoDetailCommentAdapter = new CommentListAdapter(this,item1);

        ScrollLinearLayoutManager linearLayoutManager1 = new ScrollLinearLayoutManager(this);
        linearLayoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
        linearLayoutManager1.setmCanVerticalScroll(false);
        commentRecycl.setLayoutManager(linearLayoutManager1);
        commentRecycl.setAdapter(videoDetailCommentAdapter);

        smartRefreshLayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                pageNo = pageNo + 1;
                getComment(articleid, pageNo);
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
//                pageNo = 1;
//                getComment(videoId,pageNo);
            }
        });


        videoDetailCommentAdapter.setVideoDetailCommentItemListener(new VideoDetailCommentItemListener() {
            //一级评论点赞、取消点赞
            @Override
            public void addLike(String url, int type, int mid, DetailCommentListBean videoDetailCommentBean,RecyclerView.ViewHolder viewHolder) {
                like(url, type, mid, videoDetailCommentBean,viewHolder);
            }
            //二级评论点赞、取消点赞
            @Override
            public void addSubitemLike(String url, int type, int mid, CommentSubitemBean commentSubitemBean,RecyclerView.ViewHolder viewHolder) {
                like(url, type, mid, commentSubitemBean,viewHolder);
            }

            //回复评论
            @Override
            public void toReply(int type,int grade,int pid,String name) {
                if (null != articleDetailBean) {
                    if (1 == articleDetailBean.is_comment) {
                        showPopupwindow(type,grade,pid,"回复@"+name);
                    } else {
                        ToastUtil.showToast(ArticleDetailActivity.this, "暂时不能评论!");
                    }
                }
            }

            //长按
            @Override
            public void longClick(int id, int commentId, int article_id, int type, int position,int subPosition,String content) {
                int userId = SharedPreferencedUtils.getInteger(ArticleDetailActivity.this,"SYSUSERID",0);
                if(userId == id) {
                    //长按删除
                    longClickDialog(ArticleDetailActivity.this,true,commentId,article_id,type,position,subPosition,content);
                }else {
                    //长按举报
                    longClickDialog(ArticleDetailActivity.this,false,commentId,article_id,type,position,subPosition,content);
                }
            }

            //展开更多回复
            @Override
            public void showMoreComment(int mid) {
                Intent intent = new Intent(ArticleDetailActivity.this,CommentDetailActivity.class);
                intent.putExtra("mid",mid);
                intent.putExtra("type",1);
                if(null != articleDetailBean) {
                    if (1 == articleDetailBean.is_comment) {
                        intent.putExtra("canToReply",true);
                    }else {
                        intent.putExtra("canToReply",false);
                    }
                }else {
                    intent.putExtra("canToReply",true);
                }
                startActivity(intent);
            }
        });
    }

    public void longClickDialog(Context context,boolean isMyself,int commentId,int article_id,int type,int position,int subPosition,String content) {
        LongClickDialog dialog = new LongClickDialog(context,isMyself);
        dialog.showDialog();
        dialog.setOnItemClickListener(new LongClickDialog.OnItemClick() {
            @Override
            public void onWhichItemClick(int pos) {
                switch (pos) {
                    //复制
                    case 0:
                        CopyButtonLibrary copyButtonLibrary = new CopyButtonLibrary(ArticleDetailActivity.this,content);
                        copyButtonLibrary.init(content);
                        ToastUtil.showToast(ArticleDetailActivity.this,"复制成功");
                        break;
                    case 1:
                        if(isMyself) { //删除自己的评论
                            delComment(commentId, article_id, type, position,subPosition);
                        }else { //举报他人的评论
                            showReportWindow(commentId,4);
                        }
                        break;
                }

            }
        });
    }

    private String getHtmlData(String bodyHTML) {
        String head = "<head>" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\"> " +
                "<style>img{max-width: 100%; width:auto; height:auto!important;}p{color:#333333;}</style>" +
                "</head>";
        return "<html>" + head + "<body style='font-size:18px !important;line-height:35px;'>" + bodyHTML + "</body></html>";
    }

    private String getHtmlDataNight(String bodyHTML) {
        String head = "<head>" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\"> " +
                "<style>img{max-width: 100%; width:auto; height:auto!important;}p{color:#ACACAC;}</style>" +
                "</head>";
        return "<html>" + head + "<body style='font-size:18px !important;line-height:35px;'>" + bodyHTML + "</body></html>";
    }


    public void getData() {
        Map<String, String> map = new HashMap<>();
        map.put("articleid", articleid + "");
        map.put("type", 1 + "");
        OkHttp.post(this, HttpServicePath.URL_DETAILS, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        String data = gson.toJson(baseBean.data);
                        if (StringUtil.isEmpty(data)) return;
                        articleDetailBean = gson.fromJson(data, ArticleDetailBean.class);
                        setUi(articleDetailBean);
                    }
                });
            }
        });
    }


    public void setUi(ArticleDetailBean articleDetailBean) {
        if (null == articleDetailBean) return; //articleDetailBean.title
        titleTv.setText(articleDetailBean.title);
        if (!StringUtil.isEmpty(articleDetailBean.avatar)) {
            if (!articleDetailBean.avatar.contains("http")) {
                articleDetailBean.avatar = HttpServicePath.BasePicUrl + articleDetailBean.avatar;
            }
        }
        GlideLoadUtils.getInstance().glideLoadHead(this,articleDetailBean.avatar,userIcon);
        GlideLoadUtils.getInstance().glideLoadHead(this,articleDetailBean.avatar,userIconTitleIcon);
        userName.setText(articleDetailBean.user_nickname);
        userTitleName.setText(articleDetailBean.user_nickname);
        long time = articleDetailBean.created * 1000;
        if (DateUtil.IsToday(time)) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date(time);
            timeTv.setText(DateUtil.getTimeFormatText(simpleDateFormat.format(date), "yyyy-MM-dd HH:mm:ss"));
        } else if (DateUtil.IsYesterday(time)) {
            timeTv.setText("发布于1天前");
        } else {
            timeTv.setText("发布于"+DateUtil.getTimeBig1(time));
        }
        lookNumTv.setText(ShowNumUtil.showUnm1(articleDetailBean.view_num)+"阅读");
        //评论数
        commentNumTv.setText(String.valueOf(articleDetailBean.comment_num));
        if (0 == articleDetailBean.is_follow) {
            attentionTv.setText("关注");
            attentionTv.setBackgroundResource(R.drawable.rectangle_common_attention);
            attentionTitleTv.setText("关注");
            attentionTitleTv.setBackgroundResource(R.drawable.rectangle_common_attention);

        } else {
            attentionTv.setText("已关注");
            attentionTv.setBackgroundResource(R.drawable.rectangle_common_no_attention);
            attentionTitleTv.setText("已关注");
            attentionTitleTv.setBackgroundResource(R.drawable.rectangle_common_no_attention);
        }
        //
//        contentWeb.loadDataWithBaseURL(null, getHtmlData(articleDetailBean.content), "text/html", "utf-8", null);
        if(SharedPreferencedUtils.getBoolean(this,SharedPreferencedUtils.dayModel,true)) {
            contentWeb.loadDataWithBaseURL(null, getHtmlData(articleDetailBean.content), "text/html", "utf-8", null);
        }else {
            contentWeb.loadDataWithBaseURL(null, getHtmlDataNight(articleDetailBean.content), "text/html", "utf-8", null);
        }
        likeNumTv.setText(ShowNumUtil.showUnm(articleDetailBean.up_num));
        if(0==articleDetailBean.is_original) {
            originalTv.setText("原创");
            originalTv.setTextColor(getResources().getColor(R.color.FFDE5C51));
        }else {
            originalTv.setText("转载");
        }
        if (0 == articleDetailBean.is_up) {
            likeImg.setImageResource(R.mipmap.item_like);
            likeNumTv.setTextColor(getResources().getColor(R.color.FF666666));
        } else {
            likeImg.setImageResource(R.mipmap.item_like_pre);
            likeNumTv.setTextColor(getResources().getColor(R.color.FFDE5C51));
        }

        if (0 == articleDetailBean.is_collect) {
            boolean dayModel = SharedPreferencedUtils.getBoolean(ArticleDetailActivity.this, SharedPreferencedUtils.dayModel, true);
            if(dayModel) {
                collectImg.setImageResource(R.mipmap.item_collect);
            }else {
                collectImg.setImageResource(R.mipmap.item_collect_night);
            }
        } else {
            collectImg.setImageResource(R.mipmap.item_collect_pre);
        }
        //是否显示转圈
        if(0 == articleDetailBean.is_coin) {
            //登录之后开始开始转圈
            if (CheckLogin.isLogin(this)) {
                propressLayout.setVisibility(View.VISIBLE);
                circleProgress.setProgress(SharedPreferencedUtils.getInteger(this,SharedPreferencedUtils.ARTICLE_READ_TIME,0));
                //延迟100ms后继续发消息，实现循环，直到progress=100
                handler.sendEmptyMessageDelayed(PROGRESS_CIRCLE_STARTING, circleTime);
                LogUtil.i("init:"+SharedPreferencedUtils.getInteger(ArticleDetailActivity.this,SharedPreferencedUtils.ARTICLE_READ_TIME,0));
            }
        }

        String[] imgs = WebViewJsUtil.returnImageUrlsFromHtml(articleDetailBean.content);
        contentWeb.addJavascriptInterface(new ImageJavascriptInterface(this,imgs), method);
    }

    public void getReadlog() {
        Map<String, String> map = new HashMap<>();
        map.put("type", 1 + "");
        map.put("mid", articleid + "");
        OkHttp.post(this, HttpServicePath.URL_MY_READLOG, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {

            }
        });
    }

    public void getAddres() {
        Map<String, String> map = new HashMap<>();
        map.put("article_id", articleid + "");
        OkHttp.post(this, HttpServicePath.URL_ADD_RES, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {

            }
        });
    }

    @SuppressLint("NewApi")
    public void getAdvertise() {
        Map<String, String> map = new HashMap<>();
        map.put("category", 5 + "");
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
                                GlideLoadUtils.getInstance().glideLoad(ArticleDetailActivity.this,url,options,advertisingImg);
                                if (2 == advertiseBean.change_type) { //大图无下载
                                    downloadLayout.setVisibility(View.GONE);
                                }
                            } else { //广告视频
                                videoView.setVisibility(View.VISIBLE);
                                String path = advertiseBean.video_url;//
                                if(!ArticleDetailActivity.this.isDestroyed()) {
                                    if(!StringUtil.isEmpty(path)) {
                                        HeartVideoInfo info = HeartVideoInfo.Builder().setTitle("").setPath(path).setImagePath(url).setSaveProgress(false).builder();
                                        VideoControl control = new VideoControl(ArticleDetailActivity.this);
                                        control.setInfo(info);
                                        videoView.setHeartVideoContent(control);
                                    }
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

    //奖励规则
    public void getRewardRule() {
        Map<String, String> map = new HashMap<>();
        OkHttp.post(this, HttpServicePath.URL_AWARD_RULES, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        String data = gson.toJson(baseBean.data);
                        if (StringUtil.isEmpty(data)) return;
                        List<AwardRuleBean> awardRuleListBean = gson.fromJson(data, new TypeToken<List<AwardRuleBean>>() {}.getType());
                        if(null == awardRuleListBean || 0==awardRuleListBean.size()) return;
                        DialogAwardRule dialogAwardRule= new DialogAwardRule(ArticleDetailActivity.this,awardRuleListBean);
                        dialogAwardRule.setCanceledOnTouchOutside(true);
                        dialogAwardRule.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        dialogAwardRule.show();
                    }
                });
            }
        });
    }

    public void getRankArticle() {
        Map<String, String> map = new HashMap<>();
        map.put("type", 1 + "");
        OkHttp.post(this, HttpServicePath.URL_RAND, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        String data = gson.toJson(baseBean.data);
                        if (StringUtil.isEmpty(data)) return;
                        List<ArticleDetailRandBean> articleDetailRandBean = gson.fromJson(data, new TypeToken<List<ArticleDetailRandBean>>() {
                        }.getType());
                        if (null != articleDetailRandBean && 0 != articleDetailRandBean.size()) {
                            item.addAll(articleDetailRandBean);
                            articleDetailRandAdapter.notifyDataSetChanged();

                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    boolean dayModel = SharedPreferencedUtils.getBoolean(ArticleDetailActivity.this, SharedPreferencedUtils.dayModel, true);
                                    if(dayModel) {
                                        articleDetailRandAdapter.setAdapterDayModel(ThemeManager.ThemeMode.DAY);
                                    }else {
                                        articleDetailRandAdapter.setAdapterDayModel(ThemeManager.ThemeMode.NIGHT);
                                    }
                                }
                            }, 2000);
                        }
                    }
                });
            }
        });
    }

    public void getComment(int articleid, int page) {
        Map<String, Object> map = new HashMap<>();
        map.put("articleid", articleid);
        map.put("type", 1);
        map.put("page", page);
        map.put("num", 10);
        OkHttp.post(this, smartRefreshLayout, HttpServicePath.URL_COMMENT, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        String data = gson.toJson(baseBean.data);
                        if (StringUtil.isEmpty(data)) return;
                        List<DetailCommentListBean> videoDetailCommentBeanList = gson.fromJson(data, new TypeToken<List<DetailCommentListBean>>() {
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
                        //
                        new Handler().postDelayed(new Runnable(){
                            public void run() {
                                boolean dayModel = SharedPreferencedUtils.getBoolean(ArticleDetailActivity.this, SharedPreferencedUtils.dayModel, true);
                                if(dayModel) {
                                    videoDetailCommentAdapter.setAdapterDayModel(ThemeManager.ThemeMode.DAY);
                                }else {
                                    videoDetailCommentAdapter.setAdapterDayModel(ThemeManager.ThemeMode.NIGHT);
                                }
                            }
                        }, 500);
                    }
                });
            }
        });
    }

    //一级评论点赞/取消点赞
    public void like(String url, int type, int mid, DetailCommentListBean videoDetailCommentBean,RecyclerView.ViewHolder viewHolder) {
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
                            if(viewHolder instanceof HeaderHolder) {
                                ((HeaderHolder) viewHolder).likeNumTv.setText(String.valueOf(videoDetailCommentBean.up_num));
                                ((HeaderHolder) viewHolder).likeNumTv.setTextColor(getResources().getColor(R.color.FFDE5C51));
                                ((HeaderHolder) viewHolder).likeImg.setImageResource(R.mipmap.item_like_pre);
                            }
                        } else {
                            videoDetailCommentBean.up_num = videoDetailCommentBean.up_num - 1;
                            videoDetailCommentBean.is_up = 0;
                            if(viewHolder instanceof HeaderHolder) {
                                ((HeaderHolder) viewHolder).likeNumTv.setText(String.valueOf(videoDetailCommentBean.up_num));
                                ((HeaderHolder) viewHolder).likeNumTv.setTextColor(getResources().getColor(R.color.FF666666));
                                ((HeaderHolder) viewHolder).likeImg.setImageResource(R.mipmap.item_like);
                            }
                        }
                    }
                });
            }
        });
    }

    //二级评论点赞/取消点赞
    public void like(String url, int type, int mid, CommentSubitemBean commentSubitemBean,RecyclerView.ViewHolder viewHolder) {
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
                            commentSubitemBean.up_num = commentSubitemBean.up_num + 1;
                            commentSubitemBean.is_up = 1;
                            if(viewHolder instanceof CommentSubitemHolder) {
                                ((CommentSubitemHolder) viewHolder).likeNumTv.setText(String.valueOf(commentSubitemBean.up_num));
                                ((CommentSubitemHolder) viewHolder).likeNumTv.setTextColor(getResources().getColor(R.color.FFDE5C51));
                                ((CommentSubitemHolder) viewHolder).likeImg.setImageResource(R.mipmap.item_like_pre);
                            }
                        } else {
                            commentSubitemBean.up_num = commentSubitemBean.up_num - 1;
                            commentSubitemBean.is_up = 0;
                            if(viewHolder instanceof CommentSubitemHolder) {
                                ((CommentSubitemHolder) viewHolder).likeNumTv.setText(String.valueOf(commentSubitemBean.up_num));
                                ((CommentSubitemHolder) viewHolder).likeNumTv.setTextColor(getResources().getColor(R.color.FF666666));
                                ((CommentSubitemHolder) viewHolder).likeImg.setImageResource(R.mipmap.item_like);
                            }
                        }
                    }
                });
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
                            articleDetailBean.is_follow = 1;
                            attentionTv.setText("已关注");
                            attentionTv.setBackgroundResource(R.drawable.rectangle_common_no_attention);
                            attentionTitleTv.setText("已关注");
                            attentionTitleTv.setBackgroundResource(R.drawable.rectangle_common_no_attention);
                            EventBus.getDefault().post(new EventTags.Attention(parentid, 1));
                        } else {
                            articleDetailBean.is_follow = 0;
                            attentionTv.setText("关注");
                            attentionTv.setBackgroundResource(R.drawable.rectangle_common_attention);
                            attentionTitleTv.setText("关注");
                            attentionTitleTv.setBackgroundResource(R.drawable.rectangle_common_attention);
                            EventBus.getDefault().post(new EventTags.Attention(parentid, 0));
                        }
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
                        if (url.equals(HttpServicePath.URL_LIKE)) {
                            articleDetailBean.is_up = 1;
                            articleDetailBean.up_num = articleDetailBean.up_num + 1;
                            likeNumTv.setText(String.valueOf(articleDetailBean.up_num));
                            likeNumTv.setTextColor(getResources().getColor(R.color.FFDE5C51));
                            likeImg.setImageResource(R.mipmap.item_like_pre);
                        } else {
                            articleDetailBean.is_up = 0;
                            articleDetailBean.up_num = articleDetailBean.up_num - 1;
                            likeNumTv.setText(String.valueOf(articleDetailBean.up_num));
                            likeNumTv.setTextColor(getResources().getColor(R.color.FF666666));
                            likeImg.setImageResource(R.mipmap.item_like);
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
                            articleDetailBean.is_collect = 1;
                            collectImg.setImageResource(R.mipmap.item_collect_pre);
                            //发送收藏成功通知上一个页面数量增加1
                            EventBus.getDefault().post(new EventTags.CollectEvent(mid,true));
                        } else {
                            articleDetailBean.is_collect = 0;
                            boolean dayModel = SharedPreferencedUtils.getBoolean(ArticleDetailActivity.this, SharedPreferencedUtils.dayModel, true);
                            if(dayModel) {
                                collectImg.setImageResource(R.mipmap.item_collect);
                            }else {
                                collectImg.setImageResource(R.mipmap.item_collect_night);
                            }
                            //发送取消收藏成功通知上一个页面数量减少1
                            EventBus.getDefault().post(new EventTags.CollectEvent(mid,false));
                        }
                    }
                });
            }
        });
    }

    //添加评论
    //type评论类型,1=文章，2=视频，3=话题
    //grade 评论等级，0=一级评论，1=二级评论，2=三级评论
    //pid 上级id,0=没有上级
    public void addComment(int type, int articleid, String content,int grade,int pid) {
        Map<String, String> map = new HashMap<>();
        map.put("type", type + "");
        map.put("articleid", articleid + "");
        map.put("content", content);
        map.put("grade", grade + "");
        map.put("pid", pid + "");
        OkHttp.post(this, HttpServicePath.URL_ADD_COMMENT, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pageNo = 1;
                        getComment(articleid, pageNo);
                        //评论数
                        commentNumTv.setText(ShowNumUtil.showUnm(Integer.valueOf(commentNumTv.getText().toString())+1));
                    }
                });
            }
        });
    }

    //删除评论
    public void delComment(int commentId, int article_id, int type, int position,int subPosition) {
        Map<String, String> map = new HashMap<>();
        map.put("commentid", commentId + "");
        map.put("articleid", article_id + "");
        map.put("type", type + "");
        OkHttp.post(this, HttpServicePath.URL_DEL_COMMENT, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //
                        Gson gson = new Gson();
                        String data = gson.toJson(baseBean.data);
                        if (StringUtil.isEmpty(data)) return;
                        DeleteCommentBean deleteCommentBean = gson.fromJson(data, DeleteCommentBean.class);
                        if(null != deleteCommentBean) {
                            commentNumTv.setText(ShowNumUtil.showUnm(Integer.valueOf(commentNumTv.getText().toString())-deleteCommentBean.num));
                        }

                        if(null == item1) return;
                        if(subPosition<0) { //长按一级评论的删除
                            item1.remove(position);
                        }else { //删除二级或者三级评论的回复
                            item1.get(position).num = item1.get(position).num - deleteCommentBean.num;
                            if(null == item1.get(position).cdata1) return;
                            for(int i = 0;i<item1.get(position).cdata1.size();i++) {
                                if(commentId == item1.get(position).cdata1.get(i).commentid
                                        || commentId == item1.get(position).cdata1.get(i).pid
                                        ) {
                                    item1.get(position).cdata1.remove(i);
                                    i--;
                                }
                            }
                        }
                        videoDetailCommentAdapter.notifyDataSetChanged();
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
        map.put("articleid", articleDetailBean.articleid + "");
        OkHttp.post(this, HttpServicePath.URL_READ_ARTICLE, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        String data = gson.toJson(baseBean.data);
                        if (!StringUtil.isEmpty(data)) {
                            ReadAwardBean readAwardBean = gson.fromJson(data, ReadAwardBean.class);
                            if (null == readAwardBean) return;
                            goldTv.setVisibility(View.VISIBLE);
                            goldTv.setText("+"+ readAwardBean.coin+"金币");
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    //execute the task
                                    goldTv.setVisibility(View.INVISIBLE);
                                }
                            }, 1500);
                            handler.sendEmptyMessageDelayed(PROGRESS_CIRCLE_STARTING,circleTime);
                        }
                    }
                });
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

    public void showPopupwindow(int type,int grade,int pid,String hint) {
        CommentWindow fw = new CommentWindow(this,hint);
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
                if (null != articleDetailBean) {
                    addComment(type, articleDetailBean.articleid, comment,grade,pid);
                }
                fw.dismiss();
            }
        });
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
        oks.setTitle(articleDetailBean.title);
        // titleUrl是标题的网络链接，仅在Linked-in,QQ和QQ空间使用
        oks.setTitleUrl(ShareUrl.getUrl(articleDetailBean.articleid, 1));
        // text是分享文本，所有平台都需要这个字段
        if (articleDetailBean.descriptions.length() > 20) {
            oks.setText(articleDetailBean.descriptions.substring(20));
        } else {
            oks.setText(articleDetailBean.descriptions);
        }
        if (!StringUtil.isEmpty(articleDetailBean.photo)) {
            if (!articleDetailBean.photo.contains("http")) {
                articleDetailBean.photo = HttpServicePath.BasePicUrl + articleDetailBean.photo;
            }
            oks.setImageUrl(articleDetailBean.photo);
        } else {
            oks.setImageUrl(HttpServicePath.BasePicUrl+"sharelog.png?time="+System.currentTimeMillis());
        }
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(ShareUrl.getUrl(articleDetailBean.articleid, 1));
        //启动分享
        oks.show(this);
    }


    //分享弹窗
    public void showShareWindow(int type) {
        SharePopupWindows rw = new SharePopupWindows(this, type, 0);
        WindowUtil.windowDeploy(this, rw, backImg);
        rw.setWindowShareListener(new WindowShareListener() {
            @Override
            public void qqShare(int positon) {
                if (null != articleDetailBean) {
                    showShare(QQ.NAME);
                    totalShare(1, articleDetailBean.articleid);
                }
                rw.dismiss();
            }

            @Override
            public void wechatShare(int positon) {
                if (null != articleDetailBean) {
                    showShare(Wechat.NAME);
                    totalShare(1, articleDetailBean.articleid);
                }
                rw.dismiss();
            }

            @Override
            public void wechatFriendShare(int positon) {
                if (null != articleDetailBean) {
                    showShare(WechatMoments.NAME);
                    totalShare(1, articleDetailBean.articleid);
                }
                rw.dismiss();
            }

            @Override
            public void link(int position) {
                if(null != articleDetailBean) {
                    String link = ShareUrl.getUrl(articleDetailBean.articleid, 1);
                    CopyButtonLibrary copyButtonLibrary = new CopyButtonLibrary(ArticleDetailActivity.this, link);
                    copyButtonLibrary.init(link);
                    ToastUtil.showToast(ArticleDetailActivity.this, "复制成功!");
                }
            }

            @Override
            public void report() {
                if(null != articleDetailBean) {
                    showReportWindow(articleDetailBean.articleid,1);
                    rw.dismiss();
                }
            }

            @Override
            public void daytime() {
                rw.dismiss();
            }

            @Override
            public void night() {
//                SharedPreferencedUtils.setBoolean(ArticleDetailActivity.this,SharedPreferencedUtils.dayModel,false);
//                EventBus.getDefault().post(new EventTags.DayMoulde(false));
                boolean dayModel = SharedPreferencedUtils.getBoolean(ArticleDetailActivity.this, SharedPreferencedUtils.dayModel, true);
                if (dayModel) {
                    SharedPreferencedUtils.setBoolean(ArticleDetailActivity.this, SharedPreferencedUtils.dayModel, false);
                    EventBus.getDefault().post(new EventTags.DayMoulde(false));
                } else {
                    SharedPreferencedUtils.setBoolean(ArticleDetailActivity.this, SharedPreferencedUtils.dayModel, true);
                    EventBus.getDefault().post(new EventTags.DayMoulde(true));
                }
                rw.dismiss();
            }
        });
    }

    @Override
    public void onThemeChanged() {
        initTheme();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //点击则变成关闭暂停状态
        circleProgress.setStatus(CustomCircleProgress.Status.End);
        //注意，当我们暂停时，同时还要移除消息，不然的话进度不会被停止
        handler.removeMessages(PROGRESS_CIRCLE_STARTING);
        //将当前进度存入本地
        SharedPreferencedUtils.setInteger(this,SharedPreferencedUtils.ARTICLE_READ_TIME,circleProgress.getProgress());
        HeartVideoManager.getInstance().pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //是否显示转圈
        if(null != articleDetailBean) {
            if(0 == articleDetailBean.is_coin) {
                //登录之后开始开始转圈
                if (CheckLogin.isLogin(this)) {
                    propressLayout.setVisibility(View.VISIBLE);
                    circleProgress.setProgress(SharedPreferencedUtils.getInteger(this,SharedPreferencedUtils.ARTICLE_READ_TIME,0));
                    //延迟100ms后继续发消息，实现循环，直到progress=100
                    handler.sendEmptyMessageDelayed(PROGRESS_CIRCLE_STARTING, circleTime);
                }
            }
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        ThemeManager.unregisterThemeChangeListener(this);
        EventBus.getDefault().unregister(this);
        unregisterReceiver(myBroadcastReceiver);
        articleDetailBean = null;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void dayMoulde(EventTags.DayMoulde dayMoulde) {
//        modeUi(dayMoulde.getMoulde());
        if (dayMoulde.getMoulde()) {
            ThemeManager.setThemeMode(ThemeManager.ThemeMode.DAY);
            if(null != articleDetailRandAdapter) {
                articleDetailRandAdapter.setAdapterDayModel(ThemeManager.ThemeMode.DAY);
            }

        } else {
            ThemeManager.setThemeMode(ThemeManager.ThemeMode.NIGHT);
            if(null != articleDetailRandAdapter) {
                articleDetailRandAdapter.setAdapterDayModel(ThemeManager.ThemeMode.NIGHT);
            }
        }
    }

    //评论详情一级评论点赞通知
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void LikeComment(EventTags.LikeComment likeComment) {
        if(null == item1) return;
        for(int i = 0;i<item1.size();i++) {
            if(likeComment.getMid() == item1.get(i).commentid) {
                if(likeComment.getLike()) { //评论详情点赞
                    item1.get(i).up_num = item1.get(i).up_num + 1;
                    item1.get(i).is_up = 1;
                }else {//评论详情取消点赞
                    item1.get(i).up_num = item1.get(i).up_num - 1;
                    item1.get(i).is_up = 0;
                }
                videoDetailCommentAdapter.notifyDataSetChanged();
            }
        }
    }

    //评论详情二级评论点赞通知
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void LikeSubComment(EventTags.LikeSubComment likeSubComment) {
        if(null == item1) return;
        for(int i = 0;i<item1.size();i++) {
            if(likeSubComment.getMid() == item1.get(i).commentid) {
                if(null == item1.get(i).cdata1) return;
                for(int j = 0;j<item1.get(i).cdata1.size();j++) {
                    if(likeSubComment.getSubMid() == item1.get(i).cdata1.get(j).commentid) {
                        if(likeSubComment.getLike()) {
                            item1.get(i).cdata1.get(j).up_num = item1.get(i).cdata1.get(j).up_num + 1;
                            item1.get(i).cdata1.get(j).is_up = 1;
                        }else {
                            item1.get(i).cdata1.get(j).up_num = item1.get(i).cdata1.get(j).up_num - 1;
                            item1.get(i).cdata1.get(j).is_up = 0;
                        }
                        videoDetailCommentAdapter.notifyDataSetChanged();
                    }
                }
            }
        }
    }

    //评论详情增加评论通知
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void AddComment(EventTags.AddComment addComment) {
        if(null == item1) return;
        for(int i = 0;i<item1.size();i++) {
            if(addComment.getMid() == item1.get(i).commentid) {
                item1.get(i).cdata1.add(0,addComment.getCommentSubitemBean());
                item1.get(i).num = item1.get(i).num + 1;
                videoDetailCommentAdapter.notifyDataSetChanged();
                int commentNum = Integer.valueOf(commentNumTv.getText().toString())+1;
                commentNumTv.setText(ShowNumUtil.showUnm(commentNum));
            }
        }
    }

    //评论详情删除评论通知
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void DeleteComment(EventTags.DeleteComment deleteComment) {
        if(null == item1) return;
        for(int i = 0;i<item1.size();i++) {
            if(deleteComment.getMid() == item1.get(i).commentid) {
                if(null == item1.get(i).cdata1) return;
                for(int k = 0;k<item1.get(i).cdata1.size();k++) {
                    if((deleteComment.getSubMid()== item1.get(i).cdata1.get(k).commentid) ||
                            (deleteComment.getSubMid()== item1.get(i).cdata1.get(k).pid)) {
                        item1.get(i).cdata1.remove(k);
                        k--;
                    }
                }
                item1.get(i).num = item1.get(i).num - deleteComment.getDeleteCommentNum();
                int commentNum = Integer.valueOf(commentNumTv.getText().toString())-deleteComment.getDeleteCommentNum();
                commentNumTv.setText(ShowNumUtil.showUnm(commentNum));
                videoDetailCommentAdapter.notifyDataSetChanged();
            }
        }
    }

    public void modeUi(boolean isDay) {
        if (isDay) {
            shadowLayout.setVisibility(View.GONE);
            StatusBarUtils.with(this)
                    .setColor(getResources().getColor(ThemeManager.getCurrentThemeRes(this, R.color.FFF9FAFD)))
                    .init()
                    .setStatusTextColorAndPaddingTop(true, this);
        } else {
            shadowLayout.setVisibility(View.VISIBLE);
            StatusBarUtils.with(this)
                    .setColor(getResources().getColor(ThemeManager.getCurrentThemeRes(this, R.color.trans_6)))
                    .init()
                    .setStatusTextColorAndPaddingTop(true, this);
        }
    }

    //调用不喜欢接口
    public void unLike(int bid,int mid,int type) {
        //bid 1=不敢兴趣，2=反馈垃圾内容，3=拉黑作者
        //mid 文章视频id
        //type 1=文章，2=视频
        Map<String, String> map = new HashMap<>();
        map.put("bid",bid+"");
        map.put("mid",mid+"");
        map.put("type",type+"");
        OkHttp.post(this, HttpServicePath.URL_UNLIKE, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {

            }
        });
    }

    //不喜欢列表
    public void getUnLikeParameter(int articleid) {
        Map<String, String> map = new HashMap<>();
        map.put("articleid",String.valueOf(articleid));
        map.put("type",1+"");
        OkHttp.post(this, HttpServicePath.URL_REPORT_LIST, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        String data = gson.toJson(baseBean.data);
                        if (StringUtil.isEmpty(data)) return;
                        UnLikeBean unLikeBean = gson.fromJson(data, UnLikeBean.class);
                        if(null == unLikeBean) return;
                        initDialog(unLikeBean);
                    }
                });
            }
        });
    }

    public void doDeletel(int bid) {
        if(null == articleDetailBean) return;
        unLike(bid,articleDetailBean.articleid,1);
        //发送通知让列表删除对应item
        EventBus.getDefault().post(new EventTags.DeleteItemById(articleDetailBean.articleid));
        ToastUtil.showToast(this,"将减少推荐类似内容");
    }

    public void initDialog(UnLikeBean unLikeBean) {
        DialogDeleteCommon dialog = new DialogDeleteCommon(this,unLikeBean,false);
        dialog.showDialog();
        dialog.setOnItemClickListener(new DialogDeleteCommon.OnItemClick() {
            @Override
            public void onWhichItemClick(int pos) {
                switch (pos) {
                    case 1:
                        doDeletel(1);
                        break;
                    case 2:
                        doDeletel(2);
                        break;
                    case 3:
                        doDeletel(3);
                        break;
                    case 4:
                        doDeletel(4);
                        break;
                    case 5:
                        if(null == articleDetailBean) return;
                        showReportWindow(articleDetailBean.articleid,1);
                        break;
                }
            }
        });
    }

    private class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if("enterFullScreen".equals(intent.getAction())) { //进入全屏
                StatusBarUtils.with(ArticleDetailActivity.this).init().setStatusTextColorWhite(true, ArticleDetailActivity.this);
            }else if("exitFullScreen".equals(intent.getAction())) {//退出全屏
                boolean dayModel = SharedPreferencedUtils.getBoolean(ArticleDetailActivity.this,SharedPreferencedUtils.dayModel,true);
                modeUi(dayModel);
            }
        }
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

    @OnClick({R.id.backImg, R.id.rightImg, R.id.attentionTv, R.id.attentionTitleTv, R.id.likeLayout, R.id.collectImg, R.id.shareImg,
            R.id.lookCommentImg, R.id.showEdit, R.id.layout, R.id.titleToUserLayout,R.id.jumpLayout,R.id.advertisingImg,R.id.propressLayout,
            R.id.nuLikeLayout,R.id.shareFriendLayout,R.id.shareWxLayout,R.id.prestrainLayout})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.backImg:
                finish();
                break;
            case R.id.rightImg:
                if(ClickUtil.onClick()) {
                    showShareWindow(1);
                }
                break;
            case R.id.attentionTv:
            case R.id.attentionTitleTv:
                if(ClickUtil.onClick()) {
                    if (null == articleDetailBean) return;
                    if (0 == articleDetailBean.is_follow) {//去关注
                        addFollow(articleDetailBean.userid, 1);
                    } else { //取消关注
                        addFollow(articleDetailBean.userid, 2);
                    }
                }
                break;
            case R.id.likeLayout:
                if(ClickUtil.onClick()) {
                    if (null == articleDetailBean) return;
                    if (0 == articleDetailBean.is_up) {//去点赞
                        addLike(HttpServicePath.URL_LIKE, 1, articleDetailBean.articleid);
                    } else { //取消点赞
                        addLike(HttpServicePath.URL_NO_LIKE, 1, articleDetailBean.articleid);
                    }
                }
                break;
            case R.id.showEdit:
                if(ClickUtil.onClick()) {
                    if (null != articleDetailBean) {
                        if (1 == articleDetailBean.is_comment) {
                            showPopupwindow(1,0,0,"优质评论会被优先展示哦!");
                        } else {
                            ToastUtil.showToast(this, "暂时不能评论!");
                        }
                    }
                }
                break;
            case R.id.collectImg:
                if(ClickUtil.onClick()) {
                    if (null == articleDetailBean) return;
                    if (0 == articleDetailBean.is_collect) {//去收藏
                        addCollect(HttpServicePath.URL_COLLECT, 1, articleDetailBean.articleid);
                    } else { //取消收藏
                        addCollect(HttpServicePath.URL_NO_COLLECT, 1, articleDetailBean.articleid);
                    }
                }
                break;
            case R.id.shareImg:
                if(ClickUtil.onClick()) {
                    showShareWindow(0);
                }
                break;
            case R.id.lookCommentImg:
                int hight = Math.round(linearLayout.getY());
                scrollView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.smoothScrollTo(0, hight);
                    }
                }, 200);
                break;
            case R.id.layout:
            case R.id.titleToUserLayout:
                if (ClickUtil.onClick()) {
                    if (null == articleDetailBean) return;
                    Intent intent = new Intent(ArticleDetailActivity.this, UserPagerActivity.class);
                    intent.putExtra("userId", articleDetailBean.userid);
                    startActivity(intent);
                }
                break;
            case R.id.jumpLayout:
            case R.id.advertisingImg:
                if(ClickUtil.onClick()) {
                    if(null == advertiseBean) return;
                    Intent intent = new Intent(ArticleDetailActivity.this,CommentWebViewActivity.class);
                    intent.putExtra("url", advertiseBean.jump_url);
                    startActivity(intent);
                }
                break;
            case R.id.propressLayout: //转圈布局调用
                if(ClickUtil.onClick()) {
                    getRewardRule();
                }
                break;
            case R.id.nuLikeLayout:
                if(ClickUtil.onClick()) {
                    getUnLikeParameter(articleid);
                }
                break;
            case R.id.shareFriendLayout:
                if(ClickUtil.onClick()) {
                    if (null != articleDetailBean) {
                        showShare(WechatMoments.NAME);
                        totalShare(1, articleDetailBean.articleid);
                    }
                }
                break;
            case R.id.shareWxLayout:
                if(ClickUtil.onClick()) {
                    if (null != articleDetailBean) {
                        showShare(Wechat.NAME);
                        totalShare(1, articleDetailBean.articleid);
                    }
                }
                break;
            case R.id.prestrainLayout:
                break;
        }
    }


}
