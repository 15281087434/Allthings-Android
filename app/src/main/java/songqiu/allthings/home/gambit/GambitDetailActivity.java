package songqiu.allthings.home.gambit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.percent.PercentRelativeLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mob.MobSDK;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
import songqiu.allthings.Event.EventTags;
import songqiu.allthings.R;
import songqiu.allthings.adapter.comment.CommentListAdapter;
import songqiu.allthings.adapter.GambitMorePicAdapter;
import songqiu.allthings.adapter.HeaderViewAdapter;
import songqiu.allthings.adapter.comment.CommentSubitemHolder;
import songqiu.allthings.adapter.comment.HeaderHolder;
import songqiu.allthings.base.BaseActivity;
import songqiu.allthings.bean.CommentSubitemBean;
import songqiu.allthings.bean.DeleteCommentBean;
import songqiu.allthings.bean.GambitDetailBean;
import songqiu.allthings.bean.ReportBean;
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
import songqiu.allthings.photoview.PhotoViewActivity;
import songqiu.allthings.util.ClickUtil;
import songqiu.allthings.util.CopyButtonLibrary;
import songqiu.allthings.util.DateUtil;
import songqiu.allthings.util.GlideCircleTransform;
import songqiu.allthings.util.GlideLoadUtils;
import songqiu.allthings.util.ImageResUtils;
import songqiu.allthings.util.PicParameterUtil;
import songqiu.allthings.util.ScrollLinearLayoutManager;
import songqiu.allthings.util.SharedPreferencedUtils;
import songqiu.allthings.util.ShowNumUtil;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.util.ToastUtil;
import songqiu.allthings.util.ViewProportion;
import songqiu.allthings.util.WindowUtil;
import songqiu.allthings.util.statusbar.StatusBarUtils;
import songqiu.allthings.util.theme.ShareUrl;
import songqiu.allthings.view.CommentWindow;
import songqiu.allthings.view.GridViewInScroll;
import songqiu.allthings.view.LongClickDialog;
import songqiu.allthings.view.MyScrollView;
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


    ImageView userIcon;
    TextView userName;
    TextView timeTv;
    RelativeLayout layout;
    TextView attentionTv;
    TextView deleteTv;
    TextView contentTv;
    PercentRelativeLayout parentLayout;
    ImageView bigPicImg;
    GridViewInScroll gridView;
    TextView commentNumTv;
    TextView lineTv;
    LinearLayout emptyLayout;
    ImageView ivLevel;


    @BindView(R.id.commentRecycl)
    RecyclerView commentRecycl;
    View mHeadView;
    HeaderViewAdapter mHeaderAdapter;

    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout smartRefreshLayout;
    @BindView(R.id.showEdit)
    TextView showEdit;
    @BindView(R.id.lookCommentImg)
    ImageView lookCommentImg;
    @BindView(R.id.collectImg)
    ImageView collectImg;
    @BindView(R.id.shareImg)
    ImageView shareImg;
    @BindView(R.id.likeImg)
    ImageView likeImg;
    @BindView(R.id.bottomLayout)
    LinearLayout bottomLayout;
    @BindView(R.id.shadowLayout)
    LinearLayout shadowLayout;


    int mUserId;
    int talkid;
    int pageNo = 1;
    GambitDetailBean gambitDetailBean;
    //评论
    List<DetailCommentListBean> item1;
    CommentListAdapter videoDetailCommentAdapter;

    @Override
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_gambit_detail);
    }

    @Override
    public void init() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        boolean dayModel = SharedPreferencedUtils.getBoolean(this,SharedPreferencedUtils.dayModel,true);
        modeUi(dayModel);
        talkid = getIntent().getIntExtra("talkid",0);
        mUserId = SharedPreferencedUtils.getInteger(this, "SYSUSERID", 0);
        titleTv.setText("详情");
        initRecly();
        getData();
        getReadlog();
        getComment(talkid, pageNo);
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

    public void initHeadView() {
        userIcon = mHeadView.findViewById(R.id.userIcon);
        userName = mHeadView.findViewById(R.id.userName);
        timeTv = mHeadView.findViewById(R.id.timeTv);
        layout = mHeadView.findViewById(R.id.layout);
        attentionTv = mHeadView.findViewById(R.id.attentionTv);
        deleteTv = mHeadView.findViewById(R.id.deleteTv);
        contentTv = mHeadView.findViewById(R.id.contentTv);
        parentLayout = mHeadView.findViewById(R.id.parentLayout);
        bigPicImg = mHeadView.findViewById(R.id.bigPicImg);
        gridView = mHeadView.findViewById(R.id.gridView);
        commentNumTv = mHeadView.findViewById(R.id.commentNumTv);
        lineTv = mHeadView.findViewById(R.id.lineTv);
        emptyLayout = mHeadView.findViewById(R.id.emptyLayout);
        ivLevel = mHeadView.findViewById(R.id.iv_level);

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ClickUtil.onClick()) {
                    if(null == gambitDetailBean) return;
                    Intent intent = new Intent(GambitDetailActivity.this, UserPagerActivity.class);
                    intent.putExtra("userId", gambitDetailBean.userid);
                    startActivity(intent);
                }
            }
        });

        attentionTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null == gambitDetailBean) return;
                if (0 == gambitDetailBean.is_follow) {//去关注
                    addFollow(gambitDetailBean.userid, 1);
                } else { //取消关注
                    addFollow(gambitDetailBean.userid, 2);
                }
            }
        });

        deleteTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null == gambitDetailBean) return;
                if (gambitDetailBean.userid == mUserId) {
                    delMyselfGambit(talkid);
                }
            }
        });

        bigPicImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null == gambitDetailBean) return;
                Intent intent1 = new Intent(GambitDetailActivity.this, PhotoViewActivity.class);
                intent1.putExtra("photoArray",gambitDetailBean.images);
                intent1.putExtra("clickPhotoPotision",0);
                startActivity(intent1);
            }
        });
    }

    public void initRecly() {
        item1 = new ArrayList<>();
        videoDetailCommentAdapter = new CommentListAdapter(this, item1);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        commentRecycl.setLayoutManager(linearLayoutManager);
        mHeadView = LayoutInflater.from(this).inflate(R.layout.head_gambit_detail, null, false);
        initHeadView();
        mHeaderAdapter = new HeaderViewAdapter(videoDetailCommentAdapter);
        mHeaderAdapter.addHeaderView(mHeadView);
        commentRecycl.setAdapter(mHeaderAdapter);

        smartRefreshLayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                pageNo = pageNo + 1;
                getComment(talkid, pageNo);
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                pageNo = 1;
                getComment(talkid,pageNo);
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
                showPopupwindow(type,grade,pid,"回复@"+name);
            }

            //长按
            @Override
            public void longClick(int id, int commentId, int article_id, int type, int position,int subPosition,String content) {
                int userId = SharedPreferencedUtils.getInteger(GambitDetailActivity.this,"SYSUSERID",0);
                if(userId == id) {
                    //长按删除
                    longClickDialog(GambitDetailActivity.this,true,commentId,article_id,type,position,subPosition,content);
                }else {
                    //长按举报
                    longClickDialog(GambitDetailActivity.this,false,commentId,article_id,type,position,subPosition,content);
                }
            }

            //展开更多回复
            @Override
            public void showMoreComment(int mid) {
                Intent intent = new Intent(GambitDetailActivity.this,CommentDetailActivity.class);
                intent.putExtra("mid",mid);
                intent.putExtra("type",1);
                intent.putExtra("canToReply",true);
                startActivity(intent);
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

            timeTv.setText(DateUtil.fromToday(new Date(time))+ ImageResUtils.getLevelText(gambitDetailBean.level));

        ivLevel.setImageResource(ImageResUtils.getLevelRes(gambitDetailBean.level));
        if (0 == gambitDetailBean.is_follow) {
            attentionTv.setText("关注");
            attentionTv.setBackgroundResource(R.drawable.rectangle_common_attention);
        } else {
            attentionTv.setText("已关注");
            attentionTv.setBackgroundResource(R.drawable.rectangle_common_no_attention);
        }
        commentNumTv.setText(ShowNumUtil.showUnm(gambitDetailBean.comment_num));
        if (0 == gambitDetailBean.is_up) {
            likeImg.setImageResource(R.mipmap.item_like);
        } else {
            likeImg.setImageResource(R.mipmap.item_like_pre);
        }
        if (0 == gambitDetailBean.is_collect) {
            collectImg.setImageResource(R.mipmap.item_collect);
        } else {
            collectImg.setImageResource(R.mipmap.item_collect_pre);
        }
        //举报图标
        if (gambitDetailBean.userid == mUserId) {
            deleteTv.setVisibility(View.VISIBLE);
            attentionTv.setVisibility(View.GONE);
        } else {
            deleteTv.setVisibility(View.GONE);
            attentionTv.setVisibility(View.VISIBLE);
        }

        //大图
        if(gambitDetailBean.num == 1) {
            parentLayout.setVisibility(View.VISIBLE);
            RequestOptions options1 = new RequestOptions()
                    .error(R.mipmap.pic_default_zhengfangxing)
                    .placeholder(R.mipmap.pic_default_zhengfangxing);
            if (null != gambitDetailBean.images && !StringUtil.isEmpty(gambitDetailBean.images[0])) {
                if (!gambitDetailBean.images[0].contains("http")) {
                    gambitDetailBean.images[0] = HttpServicePath.BasePicUrl + gambitDetailBean.images[0];
                }
            }
            GlideLoadUtils.getInstance().glideLoad(this,gambitDetailBean.images[0],options1,bigPicImg);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    int[] imgSize = PicParameterUtil.getImgWH(gambitDetailBean.images[0]);
                    if(null != imgSize) {
                        if(imgSize[0]>imgSize[1]) { //宽大于高
                            bigPicImg.post(new Runnable() {
                                @Override
                                public void run() {
                                    bigPicImg.setLayoutParams(ViewProportion.getRelativeParams(bigPicImg, 1.33));
                                }
                            });
                        }else if(imgSize[0]<imgSize[1]) { //高大于宽
                           bigPicImg.post(new Runnable() {
                                @Override
                                public void run() {
                                    bigPicImg.setLayoutParams(ViewProportion.getRelativeParams(bigPicImg, 0.7));
                                }
                            });
                        }else {
                            bigPicImg.post(new Runnable() {
                                @Override
                                public void run() {
                                    bigPicImg.setLayoutParams(ViewProportion.getRelativeParams(bigPicImg, 1));
                                }
                            });
                        }
                    }
                }
            }).start();
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
        oks.setTitleUrl(ShareUrl.getUrl(gambitDetailBean.id,3,1));
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
            oks.setImageUrl(HttpServicePath.BasePicUrl+"sharelog.png?time="+System.currentTimeMillis());
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
                if (null != gambitDetailBean) {
                    addComment(type, gambitDetailBean.id, comment,grade,pid);
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

    public void getReadlog() {
        Map<String, String> map = new HashMap<>();
        map.put("type", 3 + "");
        map.put("mid", talkid + "");
        OkHttp.post(this, HttpServicePath.URL_MY_READLOG, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {

            }
        });
    }

    public void getComment(int articleid, int page) {
        Map<String, Object> map = new HashMap<>();
        map.put("articleid", articleid);
        map.put("type", 3);
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
                        if (pageNo == 1) {
                            item1.clear();
                            if(null == videoDetailCommentBeanList || 0==videoDetailCommentBeanList.size()) {
                                emptyLayout.setVisibility(View.VISIBLE);
                            }else {
                                emptyLayout.setVisibility(View.GONE);
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
                            attentionTv.setText("关注");
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
                            likeImg.setImageResource(R.mipmap.item_like_pre);
                        }else {
                            gambitDetailBean.is_up = 0;
                            gambitDetailBean.up_num = gambitDetailBean.up_num - 1;
                            likeImg.setImageResource(R.mipmap.item_like);
                        }
                    }
                });
            }
        });
    }

    public void longClickDialog(Context context, boolean isMyself, int commentId, int article_id, int type, int position, int subPosition, String content) {
        LongClickDialog dialog = new LongClickDialog(context,isMyself);
        dialog.showDialog();
        dialog.setOnItemClickListener(new LongClickDialog.OnItemClick() {
            @Override
            public void onWhichItemClick(int pos) {
                switch (pos) {
                    //复制
                    case 0:
                        CopyButtonLibrary copyButtonLibrary = new CopyButtonLibrary(GambitDetailActivity.this,content);
                        copyButtonLibrary.init(content);
                        ToastUtil.showToast(GambitDetailActivity.this,"复制成功");
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
                        EventBus.getDefault().post(new EventTags.GambitRefresh());
                        EventBus.getDefault().post(new EventTags.HotGambitDetailRefresh());
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
                        EventBus.getDefault().post(new EventTags.GambitRefresh());
                        EventBus.getDefault().post(new EventTags.HotGambitDetailRefresh());
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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


    /**
     * 滑动到指定位置
     */
    private void smoothMoveToPosition(RecyclerView mRecyclerView, final int position) {
        // 第一个可见位置
        int firstItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(0));
        // 最后一个可见位置
        int lastItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(mRecyclerView.getChildCount() - 1));
        if (position < firstItem) {
            // 第一种可能:跳转位置在第一个可见位置之前，使用smoothScrollToPosition
            mRecyclerView.smoothScrollToPosition(position);
        } else if (position <= lastItem) {
            // 第二种可能:跳转位置在第一个可见位置之后，最后一个可见项之前
            int movePosition = position - firstItem;
            if (movePosition >= 0 && movePosition < mRecyclerView.getChildCount()) {
                int top = mRecyclerView.getChildAt(movePosition).getTop();
                // smoothScrollToPosition 不会有效果，此时调用smoothScrollBy来滑动到指定位置
                mRecyclerView.smoothScrollBy(0, top);
            }
        } else {
            // 第三种可能:跳转位置在最后可见项之后，则先调用smoothScrollToPosition将要跳转的位置滚动到可见位置
            // 再通过onScrollStateChanged控制再次调用smoothMoveToPosition，执行上一个判断中的方法
            mRecyclerView.smoothScrollToPosition(position);
        }
    }


    @OnClick({R.id.backImg,R.id.likeImg,R.id.showEdit,R.id.collectImg,R.id.shareImg,R.id.lookCommentImg,
                })
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.backImg:
                finish();
                break;
            case R.id.likeImg:
                if (null == gambitDetailBean) return;
                if (0 == gambitDetailBean.is_up) {//去点赞
                    addLike(HttpServicePath.URL_LIKE, 3, gambitDetailBean.id);
                } else { //取消点赞
                    addLike(HttpServicePath.URL_NO_LIKE, 3, gambitDetailBean.id);
                }
                break;
            case R.id.showEdit: //写评论
                if(null != gambitDetailBean) {
                    showPopupwindow(3,0,0,"优质评论会被优先展示哦!");
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
                smoothMoveToPosition(commentRecycl,1);
                break;

        }
    }

}
