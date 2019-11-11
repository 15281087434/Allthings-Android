package songqiu.allthings.home.gambit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mob.MobSDK;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import songqiu.allthings.Event.EventTags;
import songqiu.allthings.R;
import songqiu.allthings.adapter.ArticleDetailCommentAdapter;
import songqiu.allthings.adapter.GambitMorePicAdapter;
import songqiu.allthings.articledetail.ArticleDetailActivity;
import songqiu.allthings.base.BaseActivity;
import songqiu.allthings.bean.GambitDetailBean;
import songqiu.allthings.bean.HomeSubitemBean;
import songqiu.allthings.bean.HotGambitDetailBean;
import songqiu.allthings.bean.ReportBean;
import songqiu.allthings.bean.VideoDetailCommentBean;
import songqiu.allthings.http.BaseBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.http.OkHttp;
import songqiu.allthings.http.RequestCallBack;
import songqiu.allthings.iterface.CommentListener;
import songqiu.allthings.iterface.VideoDetailCommentItemListener;
import songqiu.allthings.iterface.WindowShareListener;
import songqiu.allthings.mine.userpage.UserPagerActivity;
import songqiu.allthings.photoview.PhotoViewActivity;
import songqiu.allthings.util.ClickUtil;
import songqiu.allthings.util.DateUtil;
import songqiu.allthings.util.GlideCircleTransform;
import songqiu.allthings.util.GlideLoadUtils;
import songqiu.allthings.util.KeyBoardUtils;
import songqiu.allthings.util.LogUtil;
import songqiu.allthings.util.SharedPreferencedUtils;
import songqiu.allthings.util.ShowNumUtil;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.util.ToastUtil;
import songqiu.allthings.util.WindowUtil;
import songqiu.allthings.util.statusbar.StatusBarUtils;
import songqiu.allthings.util.theme.ShareUrl;
import songqiu.allthings.util.theme.ThemeManager;
import songqiu.allthings.view.CommentWindow;
import songqiu.allthings.view.GridViewInScroll;
import songqiu.allthings.view.ReportPopupWindows;
import songqiu.allthings.view.SharePopupWindows;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/9/20
 *
 *类描述：详情
 *
 ********/
public class GambitDetailActivity extends BaseActivity {

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
    @BindView(R.id.layout)
    RelativeLayout layout;
    @BindView(R.id.attentionTv)
    TextView attentionTv;
    @BindView(R.id.contentTv)
    TextView contentTv;
    @BindView(R.id.bigPicImg)
    ImageView bigPicImg;
    @BindView(R.id.gridView)
    GridViewInScroll gridView;
    @BindView(R.id.likeImg)
    ImageView likeImg;
    @BindView(R.id.likeTv)
    TextView likeTv;
    @BindView(R.id.likeLayout)
    LinearLayout likeLayout;
    @BindView(R.id.commentTv)
    TextView commentTv;
    @BindView(R.id.reportImg)
    ImageView reportImg;
    @BindView(R.id.reportLayout)
    LinearLayout reportLayout;
    @BindView(R.id.commentNumTv)
    TextView commentNumTv;
    @BindView(R.id.commentRecycl)
    RecyclerView commentRecycl;
    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout smartRefreshLayout;
    @BindView(R.id.scrollView)
    ScrollView scrollView;
    @BindView(R.id.showEdit)
    TextView showEdit;
    @BindView(R.id.lookCommentImg)
    ImageView lookCommentImg;
    @BindView(R.id.lineTv)
    TextView lineTv;
    @BindView(R.id.collectImg)
    ImageView collectImg;
    @BindView(R.id.shareImg)
    ImageView shareImg;
    @BindView(R.id.bottomLayout)
    LinearLayout bottomLayout;
    @BindView(R.id.shadowLayout)
    LinearLayout shadowLayout;
    @BindView(R.id.emptyLayout)
    LinearLayout emptyLayout;
    int mUserId;
    int talkid;
    int pageNo = 1;
    GambitDetailBean gambitDetailBean;
    //评论
    List<VideoDetailCommentBean> item1;
    ArticleDetailCommentAdapter videoDetailCommentAdapter;

    @Override
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_gambit_detail);
    }

    @Override
    public void init() {
        boolean dayModel = SharedPreferencedUtils.getBoolean(this,SharedPreferencedUtils.dayModel,true);
        modeUi(dayModel);
        talkid = getIntent().getIntExtra("talkid",0);
        mUserId = SharedPreferencedUtils.getInteger(this, "SYSUSERID", 0);
        titleTv.setText("详情");
        initRecly();
        getData();
        getComment(talkid, pageNo);
    }

    public void modeUi(boolean isDay) {
        if(isDay) {
            shadowLayout.setVisibility(View.GONE);
            StatusBarUtils.with(this)
                    .setColor(getResources().getColor(ThemeManager.getCurrentThemeRes(this, R.color.FFF9FAFD)))
                    .init()
                    .setStatusTextColorAndPaddingTop(true, this);
        }else {
            shadowLayout.setVisibility(View.VISIBLE);
            StatusBarUtils.with(this)
                    .setColor(getResources().getColor(ThemeManager.getCurrentThemeRes(this, R.color.trans_6)))
                    .init()
                    .setStatusTextColorAndPaddingTop(true, this);
        }
    }

    public void initRecly() {
        item1 = new ArrayList<>();
        videoDetailCommentAdapter = new ArticleDetailCommentAdapter(this, item1);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        commentRecycl.setLayoutManager(linearLayoutManager);
        commentRecycl.setAdapter(videoDetailCommentAdapter);

        videoDetailCommentAdapter.setVideoDetailCommentItemListener(new VideoDetailCommentItemListener() {
            @Override
            public void addLike(String url, int type, int mid, VideoDetailCommentBean videoDetailCommentBean) {
                like(url, type, mid, videoDetailCommentBean);
            }

            @Override
            public void toReport(int id, int commentId, int article_id, int type, int position) {
                if (id == mUserId) {
                    delComment(commentId, article_id, type, position);
                } else {
                    showReportWindow(commentId,4);
                }
            }
        });
    }

    public void initUi(GambitDetailBean gambitDetailBean) {
        if(null == gambitDetailBean) return;
        RequestOptions options = new RequestOptions()
                .circleCrop().transforms(new GlideCircleTransform(this))
                .error(R.mipmap.head_default)
                .placeholder(R.mipmap.head_default);
        if(!StringUtil.isEmpty(gambitDetailBean.avatar)) {
            if(!gambitDetailBean.avatar.contains("http")) {
                gambitDetailBean.avatar = HttpServicePath.BasePicUrl+gambitDetailBean.avatar;
            }
        }
        GlideLoadUtils.getInstance().glideLoadHead(this,gambitDetailBean.avatar,userIcon);
        userName.setText(gambitDetailBean.user_nickname);
        //变色
        if (gambitDetailBean.descriptions.contains("#")) {
            int startIndex = gambitDetailBean.descriptions.indexOf("#");
            //根据第一个#的位置 获得#最后的位置
            int endIndex = gambitDetailBean.descriptions.lastIndexOf("#");
            if(startIndex != endIndex) {
                SpannableString spannableString = new SpannableString(gambitDetailBean.descriptions);
                spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.normal_color)), startIndex, endIndex + 1, 0);
                contentTv.setText(spannableString);
            }else {
                contentTv.setText(gambitDetailBean.descriptions);
            }
        } else {
            contentTv.setText(gambitDetailBean.descriptions);
        }

        long time = gambitDetailBean.created * 1000;
        if (DateUtil.IsToday(time)) {
            timeTv.setText("刚刚");
        } else if (DateUtil.IsYesterday(time)) {
            timeTv.setText("1天前");
        } else {
            timeTv.setText(DateUtil.getTimeBig1(time));
        }

        if (0 == gambitDetailBean.is_follow) {
            attentionTv.setText("+ 关注");
            attentionTv.setBackgroundResource(R.drawable.rectangle_common_attention);
        } else {
            attentionTv.setText("已关注");
            attentionTv.setBackgroundResource(R.drawable.rectangle_common_no_attention);
        }
        likeTv.setText(ShowNumUtil.showUnm(gambitDetailBean.up_num));
        commentNumTv.setText(ShowNumUtil.showUnm(gambitDetailBean.comment_num));
        if (0 == gambitDetailBean.is_up) {
            likeImg.setImageResource(R.mipmap.item_like);
            likeTv.setTextColor(getResources().getColor(R.color.FF666666));
        } else {
            likeImg.setImageResource(R.mipmap.item_like_pre);
            likeTv.setTextColor(getResources().getColor(R.color.FFDE5C51));
        }
        if (0 == gambitDetailBean.is_collect) {
            collectImg.setImageResource(R.mipmap.item_collect);
        } else {
            collectImg.setImageResource(R.mipmap.item_collect_pre);
        }
        //举报图标
        if (gambitDetailBean.userid == mUserId) {
            reportImg.setImageDrawable(getResources().getDrawable(R.mipmap.icon_cancel));
            attentionTv.setVisibility(View.GONE);
        } else {
            reportImg.setImageDrawable(getResources().getDrawable(R.mipmap.item_inform));
            attentionTv.setVisibility(View.VISIBLE);
        }

        //大图
        if(gambitDetailBean.num == 1) {
            bigPicImg.setVisibility(View.VISIBLE);
            RequestOptions options1 = new RequestOptions()
                    .error(R.mipmap.pic_default_zhengfangxing)
                    .placeholder(R.mipmap.pic_default_zhengfangxing);
            if (null != gambitDetailBean.images && !StringUtil.isEmpty(gambitDetailBean.images[0])) {
                if (!gambitDetailBean.images[0].contains("http")) {
                    gambitDetailBean.images[0] = HttpServicePath.BasePicUrl + gambitDetailBean.images[0];
                }
            }
            Glide.with(this).load(gambitDetailBean.images[0]).apply(options1).into(bigPicImg);
            GlideLoadUtils.getInstance().glideLoad(this,gambitDetailBean.images[0],options1,bigPicImg);
        }
        //多图
        if(gambitDetailBean.num >= 2) {
            gridView.setVisibility(View.VISIBLE);
            GambitMorePicAdapter gambitMorePicAdapter = new GambitMorePicAdapter(this,gambitDetailBean.images);
            gridView.setAdapter(gambitMorePicAdapter);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if(null == gambitDetailBean) return;
                    Intent intent = new Intent(GambitDetailActivity.this, PhotoViewActivity.class);
                    intent.putExtra("photoArray",gambitDetailBean.images);
                    intent.putExtra("clickPhotoPotision",i);
                    startActivity(intent);
                }
            });
        }
    }

    private void showShare(String platform,int position) {
        final OnekeyShare oks = new OnekeyShare();
        //指定分享的平台，如果为空，还是会调用九宫格的平台列表界面
        if (platform != null) {
            oks.setPlatform(platform);
        }
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(gambitDetailBean.user_nickname);
        // titleUrl是标题的网络链接，仅在Linked-in,QQ和QQ空间使用
        oks.setTitleUrl(ShareUrl.getUrl(gambitDetailBean.id,3));
        // text是分享文本，所有平台都需要这个字段
        oks.setText(gambitDetailBean.descriptions);
        if(null != gambitDetailBean.images && 0!=gambitDetailBean.images.length) {
            if(!gambitDetailBean.images[0].contains("http")) {
                if(!gambitDetailBean.images[0].contains("http")) {
                    gambitDetailBean.images[0] = HttpServicePath.BasePicUrl + gambitDetailBean.images[0];
                }
            }
            oks.setImageUrl(gambitDetailBean.images[0]);
        }else {
            oks.setImageUrl(HttpServicePath.BasePicUrl+"sharelog.png");
        }
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(ShareUrl.getUrl(gambitDetailBean.id,3,1));
        //启动分享
        oks.show(MobSDK.getContext());
    }

    //分享弹窗
    public void showShareWindow(int type) {
        SharePopupWindows rw = new SharePopupWindows(this, type,0);
        WindowUtil.windowDeploy(this, rw, backImg);
        rw.setWindowShareListener(new WindowShareListener() {
            @Override
            public void qqShare(int position) {
                if(null == gambitDetailBean) return;
                showShare(QQ.NAME,position);
                totalShare(3,gambitDetailBean.id);
                rw.dismiss();
            }

            @Override
            public void wechatShare(int position) {
                if(null == gambitDetailBean) return;
                showShare(Wechat.NAME,position);
                totalShare(3,gambitDetailBean.id);
                rw.dismiss();
            }

            @Override
            public void wechatFriendShare(int position) {
                if(null == gambitDetailBean) return;
                showShare(WechatMoments.NAME,position);
                totalShare(3,gambitDetailBean.id);
                rw.dismiss();
            }

            @Override
            public void link(int position) {
                if(null == gambitDetailBean) return;
            }

            @Override
            public void report() {
            }

            @Override
            public void daytime() {
//                SharedPreferencedUtils.setBoolean(ArticleDetailActivity.this,SharedPreferencedUtils.dayModel,true);
//                EventBus.getDefault().post(new EventTags.DayMoulde(true));
                rw.dismiss();
            }

            @Override
            public void night() {
//                SharedPreferencedUtils.setBoolean(ArticleDetailActivity.this,SharedPreferencedUtils.dayModel,false);
//                EventBus.getDefault().post(new EventTags.DayMoulde(false));
                boolean dayModel = SharedPreferencedUtils.getBoolean(GambitDetailActivity.this,SharedPreferencedUtils.dayModel,true);
                if(dayModel) {
                    SharedPreferencedUtils.setBoolean(GambitDetailActivity.this,SharedPreferencedUtils.dayModel,false);
                    EventBus.getDefault().post(new EventTags.DayMoulde(false));
                }else {
                    SharedPreferencedUtils.setBoolean(GambitDetailActivity.this,SharedPreferencedUtils.dayModel,true);
                    EventBus.getDefault().post(new EventTags.DayMoulde(true));
                }
                rw.dismiss();
            }
        });
    }

    //举报弹窗
    public void showReportWindow(int mid,int type) {
        ReportPopupWindows rw = new ReportPopupWindows(this, reportList(),mid,type);
        WindowUtil.windowDeploy(this, rw, line);
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
                if(null != gambitDetailBean) {
                    addComment(3, gambitDetailBean.id, comment);
                }
                fw.dismiss();
            }
        });
    }

    public void getData() {
        Map<String, String> map = new HashMap<>();
        map.put("talkid", talkid + "");
        OkHttp.post(this, HttpServicePath.URL_TALKLIST_DETAIL, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        String data = gson.toJson(baseBean.data);
                        if (StringUtil.isEmpty(data)) return;
                        List<GambitDetailBean> gambitDetailList = gson.fromJson(data, new TypeToken<List<GambitDetailBean>>() {}.getType());
                        if(null == gambitDetailList || 0 == gambitDetailList.size()) return;
                        gambitDetailBean = gambitDetailList.get(0);
                        initUi(gambitDetailBean);
                    }
                });
            }
        });
    }

    public void getComment(int articleid, int page) {
        Map<String, String> map = new HashMap<>();
        map.put("articleid", articleid + "");
        map.put("type", 3 + "");
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
                        if (pageNo == 1) {
                            item1.clear();
                            if(null != videoDetailCommentBeanList && 0!=videoDetailCommentBeanList.size()) {
                                emptyLayout.setVisibility(View.GONE);
                                commentRecycl.setVisibility(View.VISIBLE);
                            }else {
                                emptyLayout.setVisibility(View.VISIBLE);
                                commentRecycl.setVisibility(View.GONE);
                            }
                        }
                        if (null != videoDetailCommentBeanList && 0 != videoDetailCommentBeanList.size()) {
                            //评论数
                            item1.addAll(videoDetailCommentBeanList);
                            videoDetailCommentAdapter.notifyDataSetChanged();
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
                        EventBus.getDefault().post(new EventTags.GambitRefresh());
                        EventBus.getDefault().post(new EventTags.HotGambitDetailRefresh());
                        if (1 == type) {
                            gambitDetailBean.is_follow = 1;
                            attentionTv.setText("已关注");
                            attentionTv.setBackgroundResource(R.drawable.rectangle_common_no_attention);
                        } else {
                            gambitDetailBean.is_follow = 0;
                            attentionTv.setText("+ 关注");
                            attentionTv.setBackgroundResource(R.drawable.rectangle_common_attention);
                        }
                    }
                });
            }
        });
    }

    //点赞/取消点赞
    public void addLike(String url,int type,int mid) {
        Map<String,String> map = new HashMap<>();
        map.put("type",type+"");
        map.put("mid",mid+"");
        OkHttp.post(this, url, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        EventBus.getDefault().post(new EventTags.GambitRefresh());
                        EventBus.getDefault().post(new EventTags.HotGambitDetailRefresh());
                        if(url.equals(HttpServicePath.URL_LIKE)) {
                            gambitDetailBean.is_up = 1;
                            gambitDetailBean.up_num = gambitDetailBean.up_num + 1;
                            likeTv.setText(String.valueOf(gambitDetailBean.up_num));
                            likeTv.setTextColor(getResources().getColor(R.color.FFDE5C51));
                            likeImg.setImageResource(R.mipmap.item_like_pre);
                        }else {
                            gambitDetailBean.is_up = 0;
                            gambitDetailBean.up_num = gambitDetailBean.up_num - 1;
                            likeTv.setText(String.valueOf(gambitDetailBean.up_num));
                            likeTv.setTextColor(getResources().getColor(R.color.FF666666));
                            likeImg.setImageResource(R.mipmap.item_like);
                        }
                    }
                });
            }
        });
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

    //删除自己的话题
    public void delMyselfGambit(int talk_id) {
        Map<String, String> map = new HashMap<>();
        map.put("talk_id", talk_id + "");
        OkHttp.post(this, HttpServicePath.URL_TALK_LIST_DEL, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        EventBus.getDefault().post(new EventTags.GambitRefresh());
                        EventBus.getDefault().post(new EventTags.HotGambitDetailRefresh());
                        finish();
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
                        getComment(articleid, pageNo);
                        //评论数
                        gambitDetailBean.comment_num = gambitDetailBean.comment_num+1;
                        commentNumTv.setText(String.valueOf(gambitDetailBean.comment_num));
                        EventBus.getDefault().post(new EventTags.GambitRefresh());
                        EventBus.getDefault().post(new EventTags.HotGambitDetailRefresh());
                    }
                });
            }
        });
    }

    //删除评论
    public void delComment(int commentId, int article_id, int type, int position) {
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
                        EventBus.getDefault().post(new EventTags.GambitRefresh());
                        EventBus.getDefault().post(new EventTags.HotGambitDetailRefresh());
                        item1.remove(position);
                        videoDetailCommentAdapter.notifyDataSetChanged();
                        //评论数
                        gambitDetailBean.comment_num = gambitDetailBean.comment_num-1;
                        commentNumTv.setText(String.valueOf(gambitDetailBean.comment_num));
                        if(null == item1 || 0 == item1.size()) {
                            emptyLayout.setVisibility(View.VISIBLE);
                            commentRecycl.setVisibility(View.GONE);
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
                            gambitDetailBean.is_collect = 1;
                            collectImg.setImageResource(R.mipmap.item_collect_pre);
                        } else {
                            gambitDetailBean.is_collect = 0;
                            collectImg.setImageResource(R.mipmap.item_collect);
                        }
                    }
                });
            }
        });
    }

    @OnClick({R.id.backImg,R.id.attentionTv,R.id.likeLayout,R.id.reportLayout,R.id.layout,R.id.showEdit,R.id.collectImg,R.id.shareImg,R.id.lookCommentImg,
                R.id.bigPicImg})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.backImg:
                finish();
                break;
            case R.id.attentionTv:
                if (null == gambitDetailBean) return;
                if (0 == gambitDetailBean.is_follow) {//去关注
                    addFollow(gambitDetailBean.userid, 1);
                } else { //取消关注
                    addFollow(gambitDetailBean.userid, 2);
                }
                break;
            case R.id.likeLayout:
                if (null == gambitDetailBean) return;
                if (0 == gambitDetailBean.is_up) {//去点赞
                    addLike(HttpServicePath.URL_LIKE, 3, gambitDetailBean.id);
                } else { //取消点赞
                    addLike(HttpServicePath.URL_NO_LIKE, 3, gambitDetailBean.id);
                }
                break;
            case R.id.reportLayout:
                if(null == gambitDetailBean) return;
                if (gambitDetailBean.userid == mUserId) {
                    delMyselfGambit(talkid);
                }else {
                    showReportWindow(gambitDetailBean.id,3);
                }
                break;
            case R.id.layout:
                if(null == gambitDetailBean) return;
                Intent intent = new Intent(this, UserPagerActivity.class);
                intent.putExtra("userId", gambitDetailBean.userid);
                startActivity(intent);
                break;
            case R.id.showEdit: //写评论
                if(null != gambitDetailBean) {
                    showPopupwindow();
                }
                break;
            case R.id.collectImg:
                if (null == gambitDetailBean) return;
                if (0 == gambitDetailBean.is_collect) {//去收藏
                    addCollect(HttpServicePath.URL_COLLECT, 3, gambitDetailBean.id);
                } else { //取消收藏
                    addCollect(HttpServicePath.URL_NO_COLLECT, 3, gambitDetailBean.id);
                }
                break;
            case R.id.shareImg:
                showShareWindow(0);
                break;
            case R.id.lookCommentImg:
                int hight = Math.round(lineTv.getY());
                scrollView.postDelayed(new Runnable(){
                    @Override
                    public void run(){
                        scrollView.smoothScrollTo(0, hight);
                    }
                }, 200);
                break;
            case R.id.bigPicImg:
                if(null == gambitDetailBean) return;
                Intent intent1 = new Intent(this, PhotoViewActivity.class);
                intent1.putExtra("photoArray",gambitDetailBean.images);
                intent1.putExtra("clickPhotoPotision",0);
                startActivity(intent1);
                break;

        }
    }

}
