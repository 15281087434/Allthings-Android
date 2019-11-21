package songqiu.allthings.home.gambit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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
import songqiu.allthings.adapter.GambitCommonAdapter;
import songqiu.allthings.adapter.GambitHotAdapter;
import songqiu.allthings.base.BaseActivity;
import songqiu.allthings.bean.HotGambitCommonBean;
import songqiu.allthings.bean.HotGambitDetailBean;
import songqiu.allthings.bean.ReportBean;
import songqiu.allthings.http.BaseBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.http.OkHttp;
import songqiu.allthings.http.RequestCallBack;
import songqiu.allthings.iterface.GambitItemListener;
import songqiu.allthings.iterface.PhotoViewListener;
import songqiu.allthings.iterface.WindowShareListener;
import songqiu.allthings.login.LoginActivity;
import songqiu.allthings.photoview.PhotoViewActivity;
import songqiu.allthings.util.CheckLogin;
import songqiu.allthings.util.ClickUtil;
import songqiu.allthings.util.CopyButtonLibrary;
import songqiu.allthings.util.GlideLoadUtils;
import songqiu.allthings.util.LogUtil;
import songqiu.allthings.util.SharedPreferencedUtils;
import songqiu.allthings.util.ShowNumUtil;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.util.ToastUtil;
import songqiu.allthings.util.VibratorUtil;
import songqiu.allthings.util.WindowUtil;
import songqiu.allthings.util.statusbar.StatusBarUtils;
import songqiu.allthings.util.theme.ShareUrl;
import songqiu.allthings.util.theme.ThemeManager;
import songqiu.allthings.view.MyScrollView;
import songqiu.allthings.view.ReportPopupWindows;
import songqiu.allthings.view.SharePopupWindows;
import songqiu.allthings.view.banner.ColorPointHintView;
import songqiu.allthings.view.banner.RollPagerView;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/9/18
 *
 *类描述：热门话题详情
 *
 ********/
public class HotGambitDetailActivity extends BaseActivity{

    @BindView(R.id.scrollView)
    MyScrollView scrollView;
    @BindView(R.id.titleTv)
    TextView titleTv;
    @BindView(R.id.imgView)
    ImageView imgView;
    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout smartRefreshLayout;
    @BindView(R.id.shareImg1)
    ImageView shareImg1;
    @BindView(R.id.backImg)
    ImageView backImg;
    @BindView(R.id.shareImg)
    ImageView shareImg;
    @BindView(R.id.line)
    LinearLayout line;
    @BindView(R.id.titleLayout)
    RelativeLayout titleLayout;
    @BindView(R.id.titleTv1)
    TextView titleTv1;
    @BindView(R.id.contentTv)
    TextView contentTv;
    @BindView(R.id.hotNumTv)
    TextView hotNumTv;
    @BindView(R.id.attentionNumTv)
    TextView attentionNumTv;
    @BindView(R.id.attentionTv)
    TextView attentionTv;
    @BindView(R.id.shadowLayout)
    LinearLayout shadowLayout;
    @BindView(R.id.emptyLayout)
    LinearLayout emptyLayout;

    HotGambitDetailBean hotGambitDetailBean;
    //热评
    @BindView(R.id.hotCommentRecyclerView)
    RecyclerView hotCommentRecyclerView;
    @BindView(R.id.hotCommentLayout)
    LinearLayout hotCommentLayout;
    List<HotGambitCommonBean> hotList;
    GambitHotAdapter hotGambitAdapter;
    //最新
    @BindView(R.id.newLayout)
    LinearLayout newLayout;
    @BindView(R.id.newestRecyclerView)
    RecyclerView newestRecyclerView;
    List<HotGambitCommonBean> newList;
    GambitCommonAdapter newGambitAdapter;

    int talkid;
    int pageNo = 1;

    @Override
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_hot_gambit_detail);
    }

    @Override
    public void init() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        StatusBarUtils.with(HotGambitDetailActivity.this).init().setStatusTextColorWhite(true, HotGambitDetailActivity.this);
        boolean dayModel = SharedPreferencedUtils.getBoolean(this,SharedPreferencedUtils.dayModel,true);
        modeUi(dayModel);
        talkid = getIntent().getIntExtra("talkid", 0);
        //scrollview滚动监听
        scrollViewListener();
        initRecyclerView();
        getData();
    }

    public void modeUi(boolean isDay) {
        if(isDay) {
            shadowLayout.setVisibility(View.GONE);
        }else {
            shadowLayout.setVisibility(View.VISIBLE);
        }
    }

    public void scrollViewListener() {
        scrollView.setOnScrollListener(new MyScrollView.OnScrollListener() {
            @Override
            public void onScroll(int scrollY) {
                if (scrollY > 350) {
                    titleLayout.setVisibility(View.VISIBLE);
                } else {
                    titleLayout.setVisibility(View.GONE);
                }
            }
        });
    }

    public void initRecyclerView() {
        newList = new ArrayList<>();
        hotList = new ArrayList<>();
        newGambitAdapter = new GambitCommonAdapter(this,newList);
        hotGambitAdapter = new GambitHotAdapter(this,hotList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        hotCommentRecyclerView.setLayoutManager(linearLayoutManager);
        hotCommentRecyclerView.setAdapter(hotGambitAdapter);
        hotCommentRecyclerView.setNestedScrollingEnabled(false);

        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this);
        linearLayoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
        newestRecyclerView.setLayoutManager(linearLayoutManager1);
        newestRecyclerView.setAdapter(newGambitAdapter);
        newestRecyclerView.setNestedScrollingEnabled(false);

        smartRefreshLayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                pageNo = pageNo+1;
                getNewstList(pageNo,false);
                getHotList();
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                pageNo = 1;
                getNewstList(pageNo,true);
            }
        });

        newGambitAdapter.setGambitItemListener(new GambitItemListener() {
            @Override
            public void addLike(String url, int type, int mid) {
                like(url,type,mid);
            }

            @Override
            public void addFollow(int parentid,int type) {
                follow(parentid,type);
            }

            @Override
            public void delete(int type,int talk_id) {
                if(1==type) {//删除
                    delMyselfGambit(talk_id);
                }else {//举报
                    showReportWindow(talk_id,4);
                }
            }

            @Override
            public void addShare(int position) {
                showShareWindow(0,position,3);
            }
        });

        newGambitAdapter.setPhotoViewListener(new PhotoViewListener() {
            @Override
            public void toPhotoView(int potision, int clickPhotoPotision) {
                if(null != newList && 0!= newList.size()) {
                    Intent intent = new Intent(HotGambitDetailActivity.this, PhotoViewActivity.class);
                    intent.putExtra("photoArray",newList.get(potision).images);
                    intent.putExtra("clickPhotoPotision",clickPhotoPotision);
                    startActivity(intent);
                }
            }
        });

        hotGambitAdapter.setGambitItemListener(new GambitItemListener() {
            @Override
            public void addLike(String url, int type, int mid) {
                like(url,type,mid);
            }

            @Override
            public void addFollow(int parentid,int type) {
                follow(parentid,type);
            }

            @Override
            public void delete(int type,int talk_id) {
                if(1==type) {//删除
                    delMyselfGambit(talk_id);
                }else {//举报
                    showReportWindow(talk_id,4);
                }
            }

            @Override
            public void addShare(int position) {
                showShareWindow(0,position,2);
            }
        });

        hotGambitAdapter.setPhotoViewListener(new PhotoViewListener() {
            @Override
            public void toPhotoView(int potision, int clickPhotoPotision) {
                if(null != hotList && 0!= hotList.size()) {
                    Intent intent = new Intent(HotGambitDetailActivity.this, PhotoViewActivity.class);
                    intent.putExtra("photoArray",hotList.get(potision).images);
                    intent.putExtra("clickPhotoPotision",clickPhotoPotision);
                    startActivity(intent);
                }
            }
        });

    }

    public void initUi(HotGambitDetailBean hotGambitDetailBean) {
        if (null == hotGambitDetailBean) return;
        contentTv.setText(hotGambitDetailBean.descriptions);
        hotNumTv.setText(ShowNumUtil.showUnm(hotGambitDetailBean.hot_num) + " 热度");
        attentionNumTv.setText(ShowNumUtil.showUnm(hotGambitDetailBean.follow_num) + " 关注");
        if (0 == hotGambitDetailBean.is_follow) { //未关注
            attentionTv.setText("+ 关注");
            attentionTv.setBackgroundResource(R.drawable.rectangle_common_attention);
        } else { //已关注
            attentionTv.setText("已关注");
            attentionTv.setBackgroundResource(R.drawable.rectangle_common_no_attention);
        }
       titleTv.setText(hotGambitDetailBean.title);
        titleTv1.setText(hotGambitDetailBean.title);
        RequestOptions options = new RequestOptions()
                .error(R.mipmap.pic_default_small)
                .placeholder(R.mipmap.pic_default_small);
        if(!StringUtil.isEmpty(hotGambitDetailBean.photo)) {
            if(!hotGambitDetailBean.photo.contains("http")) {
                hotGambitDetailBean.photo = HttpServicePath.BasePicUrl+hotGambitDetailBean.photo;
            }
        }
        GlideLoadUtils.getInstance().glideLoad(this,hotGambitDetailBean.photo,options,imgView);
    }

    private void showShare(String platform,int position,int shareType) {
        final OnekeyShare oks = new OnekeyShare();
        //指定分享的平台，如果为空，还是会调用九宫格的平台列表界面
        if (platform != null) {
            oks.setPlatform(platform);
        }
        if(1==shareType) {
            // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
            oks.setTitle(hotGambitDetailBean.title);
            // titleUrl是标题的网络链接，仅在Linked-in,QQ和QQ空间使用
            oks.setTitleUrl(ShareUrl.getUrl(hotGambitDetailBean.id,3));
            // text是分享文本，所有平台都需要这个字段
            oks.setText(hotGambitDetailBean.descriptions);
            if(!StringUtil.isEmpty(hotGambitDetailBean.photo)) {
                if(!hotGambitDetailBean.photo.contains("http")) {
                    if(!hotGambitDetailBean.photo.contains("http")) {
                        hotGambitDetailBean.photo = HttpServicePath.BasePicUrl + hotGambitDetailBean.photo;
                    }
                }
                oks.setImageUrl(hotGambitDetailBean.photo);
            }else {
                oks.setImageUrl(HttpServicePath.BasePicUrl+"sharelog.png?time="+System.currentTimeMillis());
            }
            // url仅在微信（包括好友和朋友圈）中使用
            oks.setUrl(ShareUrl.getUrl(hotGambitDetailBean.id,3));
        }else if(2==shareType) {
            // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
            oks.setTitle(hotList.get(position).user_nickname);
            // titleUrl是标题的网络链接，仅在Linked-in,QQ和QQ空间使用
            oks.setTitleUrl(ShareUrl.getUrl(hotList.get(position).id,3,1));
            // text是分享文本，所有平台都需要这个字段
            oks.setText(hotList.get(position).descriptions);
            if(null!=hotList.get(position).images && 0 !=hotList.get(position).images.length) {
                if(!StringUtil.isEmpty(hotList.get(position).images[0])) {
                    if(!hotList.get(position).images[0].contains("http")) {
                        hotList.get(position).images[0] = HttpServicePath.BasePicUrl + hotList.get(position).images[0];
                    }
                    oks.setImageUrl(hotList.get(position).images[0]);
                }else {
                    oks.setImageUrl(HttpServicePath.BasePicUrl+"sharelog.png?time="+System.currentTimeMillis());
                }
            }else {
                oks.setImageUrl(HttpServicePath.BasePicUrl+"sharelog.png?time="+System.currentTimeMillis());
            }
            // url仅在微信（包括好友和朋友圈）中使用
            oks.setUrl(ShareUrl.getUrl(hotList.get(position).id,3,1));
            shareRefresh(hotList.get(position).id);
        }else if(3==shareType) {
            // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
            oks.setTitle(newList.get(position).user_nickname);
            // titleUrl是标题的网络链接，仅在Linked-in,QQ和QQ空间使用
            oks.setTitleUrl(ShareUrl.getUrl(newList.get(position).id,3,1));
            // text是分享文本，所有平台都需要这个字段
            oks.setText(newList.get(position).descriptions);
            if(null!=newList.get(position).images && 0 !=newList.get(position).images.length) {
                if(!StringUtil.isEmpty(newList.get(position).images[0])) {
                    if(!newList.get(position).images[0].contains("http")) {
                        newList.get(position).images[0] = HttpServicePath.BasePicUrl + newList.get(position).images[0];
                    }
                    oks.setImageUrl(newList.get(position).images[0]);
                }else {
                    oks.setImageUrl(HttpServicePath.BasePicUrl+"sharelog.png?time="+System.currentTimeMillis());
                }
            }else {
                oks.setImageUrl(HttpServicePath.BasePicUrl+"sharelog.png?time="+System.currentTimeMillis());
            }
            // url仅在微信（包括好友和朋友圈）中使用
            oks.setUrl(ShareUrl.getUrl(newList.get(position).id,3,1));
            shareRefresh(newList.get(position).id);
        }
        //启动分享
        oks.show(MobSDK.getContext());
    }

    public void shareRefresh(int talkid) {
        LogUtil.i("talkid:"+talkid);
        if(null != hotList && 0!= hotList.size()) {
            for(int i = 0;i<hotList.size();i++) {
                if(hotList.get(i).id ==  talkid) {
                    LogUtil.i("i:"+i);
                    hotList.get(i).share_num = hotList.get(i).share_num+1;
                    hotGambitAdapter.notifyDataSetChanged();
                }
            }
        }

        if(null != newList && 0!= newList.size()) {
            for(int j = 0;j<newList.size();j++) {
                if(newList.get(j).id ==  talkid) {
                    LogUtil.i("j:"+j);
                    newList.get(j).share_num = newList.get(j).share_num+1;
                    newGambitAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    //分享弹窗 //定义一个shareType  1:此话题  2：热评 3：最新
    public void showShareWindow(int type,int position,int shareType) {
        SharePopupWindows rw = new SharePopupWindows(this, type,0);
        WindowUtil.windowDeploy(this, rw, line);
        rw.setWindowShareListener(new WindowShareListener() {
            @Override
            public void qqShare(int positon) {
                if(1 == shareType) {
                    if(null == hotGambitDetailBean) return;
                    showShare(QQ.NAME,position,shareType);
                    totalShare(3,hotGambitDetailBean.id);
                }else if(2 == shareType) {
                    if(null == hotList || 0 == hotList.size()) return;
                    showShare(QQ.NAME,position,shareType);
                    totalShare(3,hotList.get(position).id);
                }else if(3 == shareType) {
                    if(null == newList || 0 == newList.size()) return;
                    showShare(QQ.NAME,position,shareType);
                    totalShare(3,newList.get(position).id);
                }
                rw.dismiss();
            }

            @Override
            public void wechatShare(int positon) {
                if(1 == shareType) {
                    if(null == hotGambitDetailBean) return;
                    showShare(Wechat.NAME,position,shareType);
                    totalShare(3,hotGambitDetailBean.id);
                }else if(2 == shareType) {
                    if(null == hotList || 0 == hotList.size()) return;
                    showShare(Wechat.NAME,position,shareType);
                    totalShare(3,hotList.get(position).id);
                }else if(3 == shareType) {
                    if(null == newList || 0 == newList.size()) return;
                    showShare(Wechat.NAME,position,shareType);
                    totalShare(3,newList.get(position).id);
                }
                rw.dismiss();
            }

            @Override
            public void wechatFriendShare(int positon) {
                if(1 == shareType) {
                    if(null == hotGambitDetailBean) return;
                    showShare(WechatMoments.NAME,position,shareType);
                    totalShare(3,hotGambitDetailBean.id);
                }else if(2 == shareType) {
                    if(null == hotList || 0 == hotList.size()) return;
                    showShare(WechatMoments.NAME,position,shareType);
                    totalShare(3,hotList.get(position).id);
                }else if(3 == shareType) {
                    if(null == newList || 0 == newList.size()) return;
                    showShare(WechatMoments.NAME,position,shareType);
                    totalShare(3,newList.get(position).id);
                }
                rw.dismiss();
            }

            @Override
            public void link(int positon) {
                if(1 == shareType) {
                    if(null == hotGambitDetailBean) return;
                    String link =  ShareUrl.getUrl(hotGambitDetailBean.id,3);
                    CopyButtonLibrary copyButtonLibrary = new CopyButtonLibrary(HotGambitDetailActivity.this,link);
                    copyButtonLibrary.init(link);
                }else if(2 == shareType) {
                    if(null == hotList || 0==hotList.size()) return;
                    String link =  ShareUrl.getUrl(hotList.get(position).id,3,1);
                    CopyButtonLibrary copyButtonLibrary = new CopyButtonLibrary(HotGambitDetailActivity.this,link);
                    copyButtonLibrary.init(link);
                }else if(3 == shareType) {
                    if(null == newList || 0==newList.size()) return;
                    String link =  ShareUrl.getUrl(newList.get(position).id,3,1);
                    CopyButtonLibrary copyButtonLibrary = new CopyButtonLibrary(HotGambitDetailActivity.this,link);
                    copyButtonLibrary.init(link);
                }

                ToastUtil.showToast(HotGambitDetailActivity.this,"复制成功!");
            }

            @Override
            public void report() {
            }

            @Override
            public void daytime() {
                rw.dismiss();
            }

            @Override
            public void night() {
                boolean dayModel = SharedPreferencedUtils.getBoolean(HotGambitDetailActivity.this,SharedPreferencedUtils.dayModel,true);
                if(dayModel) {
                    SharedPreferencedUtils.setBoolean(HotGambitDetailActivity.this,SharedPreferencedUtils.dayModel,false);
                    EventBus.getDefault().post(new EventTags.DayMoulde(false));
                }else {
                    SharedPreferencedUtils.setBoolean(HotGambitDetailActivity.this,SharedPreferencedUtils.dayModel,true);
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

    public void getData() {
        Map<String, String> map = new HashMap<>();
        map.put("talk_id", talkid + "");
        OkHttp.post(this, HttpServicePath.URL_TALK_DETAIL, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        String data = gson.toJson(baseBean.data);
                        if (StringUtil.isEmpty(data)) return;
                        HotGambitDetailBean hotGambitDetailBean = gson.fromJson(data, HotGambitDetailBean.class);
                        HotGambitDetailActivity.this.hotGambitDetailBean = hotGambitDetailBean;
                        initUi(hotGambitDetailBean);

                        //获取详情成功后再调用热评和最新，确保hotGambitDetailBean不为空
                        pageNo = 1;
                        getNewstList(pageNo,false); //获取最新列表
                        getHotList();
                    }
                });
            }
        });
    }

    public void follow(String url, int talk_id) {
        Map<String, String> map = new HashMap<>();
        map.put("talk_id", talk_id + "");
        OkHttp.post(this, url, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        EventBus.getDefault().post(new EventTags.GambitRefresh());
                        if (url.contains("follow_talk_no")) { //取消关注
                            attentionTv.setText("+ 关注");
                            attentionTv.setBackgroundResource(R.drawable.rectangle_common_attention);
                            hotGambitDetailBean.is_follow = 0;
                        } else {
                            attentionTv.setText("已关注");
                            attentionTv.setBackgroundResource(R.drawable.rectangle_common_no_attention);
                            hotGambitDetailBean.is_follow = 1;
                        }
                    }
                });
            }
        });
    }

    public void getHotList() {
        hotList.clear();
        Map<String, String> map = new HashMap<>();
        map.put("talk_id", talkid + "");
        OkHttp.post(this,HttpServicePath.URL_TALK_LIST_HOT, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        String data = gson.toJson(baseBean.data);
                        if (StringUtil.isEmpty(data)) return;
                        List<HotGambitCommonBean> hotGambitCommonList = gson.fromJson(data, new TypeToken<List<HotGambitCommonBean>>() {}.getType());
                        if(null == hotGambitCommonList || 0 == hotGambitCommonList.size()) {
                            //没有热评则隐藏包括最新的列表  显示没有评论
                            hotCommentLayout.setVisibility(View.GONE);
                            newLayout.setVisibility(View.GONE);
                            emptyLayout.setVisibility(View.VISIBLE);
                        }else {
                            if(null != hotGambitDetailBean) {
                                String title = "#"+hotGambitDetailBean.title+"#";
                                StringBuffer sb = new StringBuffer();
                                sb.append(title);
                                for(HotGambitCommonBean hotGambitCommonBean:hotGambitCommonList) {
                                    hotGambitCommonBean.descriptions = sb.append(hotGambitCommonBean.descriptions).toString();
                                }
                            }
                            hotCommentLayout.setVisibility(View.VISIBLE);
                            hotList.addAll(hotGambitCommonList);
                            hotGambitAdapter.notifyDataSetChanged();
                            emptyLayout.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
    }

    public void getNewstList(int pageNo,boolean ringDown) {
        Map<String, String> map = new HashMap<>();
        map.put("talk_id", talkid + "");
        map.put("num", 10 + "");
        map.put("page", pageNo + "");
        OkHttp.post(this,smartRefreshLayout, HttpServicePath.URL_TALK_LIST_LIST, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        String data = gson.toJson(baseBean.data);
                        if (StringUtil.isEmpty(data)) return;
                        List<HotGambitCommonBean> hotGambitCommonList = gson.fromJson(data, new TypeToken<List<HotGambitCommonBean>>() {}.getType());
                        if(pageNo ==1) {
                            newList.clear();
                            if(null == hotGambitCommonList || 0 == hotGambitCommonList.size()) {
                                newLayout.setVisibility(View.GONE);
                            }
                        }
                        if(null != hotGambitCommonList && 0!=hotGambitCommonList.size()) {
                            if(null != hotGambitDetailBean) {
                                String title = "#"+hotGambitDetailBean.title+"#";
                                StringBuffer sb = new StringBuffer();
                                sb.append(title);
                                for(HotGambitCommonBean hotGambitCommonBean:hotGambitCommonList) {
                                    hotGambitCommonBean.descriptions = sb.append(hotGambitCommonBean.descriptions).toString();
                                }
                            }
                            newLayout.setVisibility(View.VISIBLE);
                            newList.addAll(hotGambitCommonList);
                            newGambitAdapter.notifyDataSetChanged();
                        }

                        if(ringDown) {
                            VibratorUtil.ringDown(HotGambitDetailActivity.this);
                        }
                    }
                });
            }
        });
    }

    //点赞/取消点赞
    public void like(String url,int type,int mid) {
        Map<String,String> map = new HashMap<>();
        map.put("type",type+"");
        map.put("mid",mid+"");
        OkHttp.post(this, url, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(url.equals(HttpServicePath.URL_LIKE)) {
                            for(int i = 0;i<hotList.size();i++) {
                                if(mid == hotList.get(i).id) {
                                    hotList.get(i).is_up = 1;
                                    hotList.get(i).up_num = hotList.get(i).up_num+1;
                                }
                            }

                            for(int i = 0;i<newList.size();i++) {
                                if(mid == newList.get(i).id) {
                                    newList.get(i).is_up = 1;
                                    newList.get(i).up_num = newList.get(i).up_num+1;
                                }
                            }
                        }else {
                            for(int i = 0;i<hotList.size();i++) {
                                if(mid == hotList.get(i).id) {
                                    hotList.get(i).is_up = 0;
                                    hotList.get(i).up_num = hotList.get(i).up_num-1;
                                }
                            }

                            for(int i = 0;i<newList.size();i++) {
                                if(mid == newList.get(i).id) {
                                    newList.get(i).is_up = 0;
                                    newList.get(i).up_num = newList.get(i).up_num-1;
                                }
                            }
                        }
                        hotGambitAdapter.notifyDataSetChanged();
                        newGambitAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    //用户id 	type、1=添加关注，2=取消关注
    public void follow(int parentid,int type) {
        Map<String, String> map = new HashMap<>();
        map.put("parentid", parentid + "");
        map.put("type", type + "");
        OkHttp.post(this, HttpServicePath.URL_ADD_FOLLOW, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(1== type) {
                            for(int i = 0;i<hotList.size();i++) {
                                if(parentid == hotList.get(i).userid) {
                                    hotList.get(i).is_follow = 1;
                                }
                            }

                            for(int i = 0;i<newList.size();i++) {
                                if(parentid == newList.get(i).userid) {
                                    newList.get(i).is_follow = 1;
                                }
                            }
                        }else {
                            for(int i = 0;i<hotList.size();i++) {
                                if(parentid == hotList.get(i).userid) {
                                    hotList.get(i).is_follow = 0;
                                }
                            }

                            for(int i = 0;i<newList.size();i++) {
                                if(parentid == newList.get(i).userid) {
                                    newList.get(i).is_follow = 0;
                                }
                            }
                        }
                        hotGambitAdapter.notifyDataSetChanged();
                        newGambitAdapter.notifyDataSetChanged();
                        EventBus.getDefault().post(new EventTags.GambitRefresh());
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
                        for(int i = 0;i<hotList.size();i++) {
                            if(talk_id == hotList.get(i).id) {
                                if(i<hotList.size()) {
                                    hotList.remove(i);
                                }
                            }
                        }
                        hotGambitAdapter.notifyDataSetChanged();

                        for(int i = 0;i<newList.size();i++) {
                            if(talk_id == newList.get(i).id) {
                                if(i<newList.size()) {
                                    newList.remove(i);
                                }
                            }
                        }
                        newGambitAdapter.notifyDataSetChanged();

                        if(null == hotList || 0 == hotList.size()) {
                            //没有热评则隐藏包括最新的列表  显示没有评论
                            hotCommentLayout.setVisibility(View.GONE);
                            newLayout.setVisibility(View.GONE);
                            emptyLayout.setVisibility(View.VISIBLE);
                        }
                        if(null == newList || 0 == newList.size()) {
                            newLayout.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void toHotGambitDetailRefresh(EventTags.HotGambitDetailRefresh hotGambitDetailRefresh) {
        pageNo = 1;
        getNewstList(pageNo,false); //获取最新列表
        getHotList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @OnClick({R.id.backImg1,R.id.backImg,R.id.shareImg, R.id.shareImg1, R.id.attentionTv,R.id.showEdit})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.backImg:
            case R.id.backImg1:
                finish();
                break;
            case R.id.shareImg:
            case R.id.shareImg1:
                showShareWindow(0,0,1);
                break;
            case R.id.attentionTv:
                if (null == hotGambitDetailBean) return;
                if (0 == hotGambitDetailBean.is_follow) {
                    follow(HttpServicePath.URL_FOLLOW_TALK, hotGambitDetailBean.id);
                } else {
                    follow(HttpServicePath.URL_FOLLOW_TALK_NO, hotGambitDetailBean.id);
                }
                break;
            case R.id.showEdit: //参与话题
                if (null == hotGambitDetailBean) return;
                if(ClickUtil.onClick()) {
                    Intent intent;
                    if(CheckLogin.isLogin(this)) {
                        intent = new Intent(HotGambitDetailActivity.this,JoinGambitActivity.class);
                        intent.putExtra("hintGambit",hotGambitDetailBean.title);
                        intent.putExtra("talk_id",hotGambitDetailBean.id);
                        intent.putExtra("talktype",1);
                        startActivity(intent);
                    }else {
                        intent = new Intent(this,LoginActivity.class);
                        startActivity(intent);
                    }
                }
                break;
        }
    }

}
