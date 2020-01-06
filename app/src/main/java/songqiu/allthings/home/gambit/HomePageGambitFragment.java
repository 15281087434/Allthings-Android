package songqiu.allthings.home.gambit;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import butterknife.OnClick;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import songqiu.allthings.Event.EventTags;
import songqiu.allthings.R;
import songqiu.allthings.activity.MainActivity;
import songqiu.allthings.adapter.BannerLooperAdapter;
import songqiu.allthings.adapter.GambitCommonAdapter;
import songqiu.allthings.adapter.HeaderViewAdapter;
import songqiu.allthings.adapter.HomeHotGambitAdapter;
import songqiu.allthings.adapter.LookTabClassAdapter;
import songqiu.allthings.adapter.TaskSignAdapter;
import songqiu.allthings.articledetail.ArticleDetailActivity;
import songqiu.allthings.base.BaseFragment;
import songqiu.allthings.bean.BannerBean;
import songqiu.allthings.bean.HomeGambitHotBean;
import songqiu.allthings.bean.HomeSubitemBean;
import songqiu.allthings.bean.HotGambitCommonBean;
import songqiu.allthings.bean.ReportBean;
import songqiu.allthings.bean.TaskSiginListBean;
import songqiu.allthings.bean.UnLikeBean;
import songqiu.allthings.http.BaseBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.http.OkHttp;
import songqiu.allthings.http.RequestCallBack;
import songqiu.allthings.iterface.DialogDeleteListener;
import songqiu.allthings.iterface.GambitItemListener;
import songqiu.allthings.iterface.HomeHotGambitListener;
import songqiu.allthings.iterface.PhotoViewListener;
import songqiu.allthings.iterface.TaskSignListener;
import songqiu.allthings.iterface.WindowShareListener;
import songqiu.allthings.login.LoginActivity;
import songqiu.allthings.photoview.PhotoViewActivity;
import songqiu.allthings.util.CheckLogin;
import songqiu.allthings.util.ClickUtil;
import songqiu.allthings.util.CopyButtonLibrary;
import songqiu.allthings.util.LogUtil;
import songqiu.allthings.util.SharedPreferencedUtils;
import songqiu.allthings.util.ShowNumUtil;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.util.ToastUtil;
import songqiu.allthings.util.WindowUtil;
import songqiu.allthings.util.theme.ShareUrl;
import songqiu.allthings.view.DialogDeleteCommon;
import songqiu.allthings.view.ReportPopupWindows;
import songqiu.allthings.view.SharePopupWindows;
import songqiu.allthings.view.banner.ColorPointHintView;
import songqiu.allthings.view.banner.RollPagerView;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/8/7
 *
 *类描述：首页 tab话题页
 *
 ********/
public class HomePageGambitFragment extends BaseFragment {


    @BindView(R.id.line)
    TextView line;
    @BindView(R.id.reyclerView)
    RecyclerView reyclerView;
    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout smartRefreshLayout;

    RecyclerView hotRecyclerView;
    RollPagerView rollPageHome;
    LinearLayout hotRefreshLayout;
    LinearLayout dynamicRefreshLayout;

    View mHeadView;
    HeaderViewAdapter mHeaderAdapter;

    List<BannerBean> mBannerList;
    BannerLooperAdapter mBannerAdapter;

    //热门话题
    HomeHotGambitAdapter hotAdapter;
    List<HomeGambitHotBean> list;
    //动态分享(朋友圈)
    List<HotGambitCommonBean> newList;
    GambitCommonAdapter newGambitAdapter;
    int page = 1;

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
        return R.layout.fragment_home_page_gambit;
    }

    @Override
    public void init() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        smartRefreshLayout.setEnableRefresh(false);
        initRecyclerView();
        getHotData();
        getFriendsData(page);
    }

    public void initBanner() {
        mBannerList = new ArrayList<>();
        mBannerAdapter = new BannerLooperAdapter(rollPageHome, (ArrayList<BannerBean>) mBannerList);
        rollPageHome.setAdapter(mBannerAdapter);
        rollPageHome.setHintView(new ColorPointHintView(activity, Color.WHITE, Color.GRAY));
        rollPageHome.setHintPadding(0, 0, 0, 10);
        rollPageHome.resume();
        getBanner();
    }

    public void initBannerEvent() {
        rollPageHome.setOnItemClickListener(position -> {
            if (mBannerList == null || position >= mBannerList.size()) {
                return;
            }
            Intent intent = new Intent(activity,HotGambitDetailActivity.class);
            intent.putExtra("talkid",mBannerList.get(position).url_id);
            startActivity(intent);
        });
    }

    public void initHotRecyclerView() {
        list = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        hotRecyclerView.setLayoutManager(linearLayoutManager);
        hotAdapter = new HomeHotGambitAdapter(activity,list);
        hotRecyclerView.setAdapter(hotAdapter);
        hotAdapter.setHomeHotGambitListener(new HomeHotGambitListener() {
            @Override
            public void addFollow(String url, int id,RecyclerView.ViewHolder viewHolder) {
                follow(url,id,viewHolder);
            }
        });
    }


    public void initRecyclerView() {
        newList = new ArrayList<>();
        newGambitAdapter = new GambitCommonAdapter(activity,newList);
        mHeadView = LayoutInflater.from(activity).inflate(R.layout.home_page_gambit_headview, null, false);
        rollPageHome = mHeadView.findViewById(R.id.roll_page_home);
        hotRecyclerView = mHeadView.findViewById(R.id.hotRecyclerView);
        hotRefreshLayout = mHeadView.findViewById(R.id.hotRefreshLayout);
        dynamicRefreshLayout = mHeadView.findViewById(R.id.dynamicRefreshLayout);
        initHotRecyclerView();
        initBanner();
        initBannerEvent();
        mHeaderAdapter = new HeaderViewAdapter(newGambitAdapter);
        mHeaderAdapter.addHeaderView(mHeadView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        reyclerView.setLayoutManager(linearLayoutManager);
        reyclerView.setAdapter(mHeaderAdapter);

        hotRefreshLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ClickUtil.onClick()) {
                    getHotData();
                }
            }
        });

        dynamicRefreshLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ClickUtil.onClick()) {
                    page = 1;
                    getFriendsData(page);
                }
            }
        });

        newGambitAdapter.setGambitItemListener(new GambitItemListener() {
            @Override
            public void addLike(String url, int type, int mid,RecyclerView.ViewHolder viewHolder) { //点赞
                like(url,type,mid,viewHolder);
            }

            @Override
            public void addFollow(int parentid,int type,RecyclerView.ViewHolder viewHolder) { //关注
                follow(parentid,type,viewHolder);
            }

            @Override
            public void delete(int type,int talk_id,int userId) { //举报或者删除
                if(1==type) {//删除
                    delMyselfGambit(talk_id);
                }else {//举报
                  getUnLikeParameter(talk_id,3);
//                    initDialog(talk_id,userId);
                }
            }

            @Override
            public void addShare(int position) { //分享
                showShareWindow(0,position);
            }
        });

        //浏览图片
        newGambitAdapter.setPhotoViewListener(new PhotoViewListener() {
            @Override
            public void toPhotoView(int potision,int clickPhotoPotision) {
                if(null != newList && 0!= newList.size()) {
//                    newList.get(potision).images
                    Intent intent = new Intent(activity, PhotoViewActivity.class);
                    intent.putExtra("photoArray",newList.get(potision).images);
                    intent.putExtra("clickPhotoPotision",clickPhotoPotision);
                    startActivity(intent);
//                    activity.overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
                }
            }
        });

        smartRefreshLayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                page = page+1;
                getFriendsData(page);
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {

            }
        });
    }

    public void doDeletel(int talk_id,int bid) {
        unLike(talk_id,bid,3);
        ToastUtil.showToast(activity,"将减少此类内容推荐");
        if(null != newList && 0!=newList.size()) {
                for(int i = 0;i<newList.size();i++) {
                    if(talk_id == newList.get(i).id) {
                        newList.remove(i);
                        newGambitAdapter.notifyDataSetChanged();
                    }
                }
        }
    }


    //不喜欢列表
    public void getUnLikeParameter(int articleid,int type) {
        Map<String, String> map = new HashMap<>();
        map.put("articleid",String.valueOf(articleid));
        map.put("type",type+"");
        OkHttp.post(activity, HttpServicePath.URL_REPORT_LIST, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        Gson gson = new Gson();
//                        String data = gson.toJson(baseBean.data);
//                        if (StringUtil.isEmpty(data)) return;
//                        UnLikeBean unLikeBean = gson.fromJson(data, UnLikeBean.class);
//                        if(null == unLikeBean) return;
                        UnLikeBean unLikeBean = new UnLikeBean();
                        initDialog(unLikeBean,articleid);
                    }
                });
            }
        });
    }

    public void initDialog(UnLikeBean unLikeBean,int talk_id) {
        DialogDeleteCommon dialog = new DialogDeleteCommon(activity,unLikeBean,false);
        dialog.showDialog();
        dialog.setOnItemClickListener(new DialogDeleteCommon.OnItemClick() {
            @Override
            public void onWhichItemClick(int pos) {
                switch (pos) {
                    case 1:
                        doDeletel(talk_id,1);
                        break;
                    case 2:
                        doDeletel(talk_id,2);
                        break;
                    case 3:
                        doDeletel(talk_id,3);
                        break;
                    case 4:
                        doDeletel(talk_id,4);
                        break;
                    case 5:
                        showReportWindow(talk_id,3);
                        break;
                }
            }
        });
    }

    //调用不喜欢接口
    public void unLike(int mid,int bid,int type) {
        //bid 1=不敢兴趣，2=反馈垃圾内容，3=拉黑作者
        //mid 文章视频id
        //type 1=文章，2=视频
        Map<String, String> map = new HashMap<>();
        map.put("bid",bid+"");
        map.put("mid",mid+"");
        map.put("type",type+"");
        OkHttp.post(activity, HttpServicePath.URL_UNLIKE, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {

            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void toGambitRefresh(EventTags.GambitRefresh gambitRefresh) {
        getHotData();
        page = 1;
        getFriendsData(page);
    }

    //分享弹窗
    public void showShareWindow(int type,int position) {
        SharePopupWindows rw = new SharePopupWindows(activity,type,position);
        WindowUtil.windowDeploy(activity,rw,line);
        rw.setWindowShareListener(new WindowShareListener() {
            @Override
            public void qqShare(int positon) {
                if(null == newList || 0 == newList.size()) return;
                showShare(QQ.NAME,position);
                totalShare(3,newList.get(position).id);
                rw.dismiss();
            }

            @Override
            public void wechatShare(int positon) {
                if(null == newList || 0 == newList.size()) return;
                showShare(Wechat.NAME,position);
                totalShare(3,newList.get(position).id);
                rw.dismiss();
            }

            @Override
            public void wechatFriendShare(int positon) {
                if(null == newList || 0 == newList.size()) return;
                showShare(WechatMoments.NAME,position);
                totalShare(3,newList.get(position).id);
                rw.dismiss();
            }

            @Override
            public void link(int position) {
                if(null == newList || 0 == newList.size()) return;
                String link =  ShareUrl.getUrl(newList.get(position).id,3,1);
                CopyButtonLibrary copyButtonLibrary = new CopyButtonLibrary(activity,link);
                copyButtonLibrary.init(link);
                ToastUtil.showToast(activity,"复制成功!");
            }

            @Override
            public void report() {
                if(null == newList || 0 == newList.size()) return;
                showReportWindow(newList.get(position).id,3);
            }

            @Override
            public void daytime() {

            }

            @Override
            public void night() {
                boolean dayModel = SharedPreferencedUtils.getBoolean(activity,SharedPreferencedUtils.dayModel,true);
                if(dayModel) {
                    SharedPreferencedUtils.setBoolean(activity,SharedPreferencedUtils.dayModel,false);
                    EventBus.getDefault().post(new EventTags.DayMoulde(false));
                }else {
                    SharedPreferencedUtils.setBoolean(activity,SharedPreferencedUtils.dayModel,true);
                    EventBus.getDefault().post(new EventTags.DayMoulde(true));
                }
            }
        });
    }

    private void showShare(String platform,int position) {
        final OnekeyShare oks = new OnekeyShare();
        //指定分享的平台，如果为空，还是会调用九宫格的平台列表界面
        if (platform != null) {
            oks.setPlatform(platform);
        }
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(newList.get(position).user_nickname);
        // titleUrl是标题的网络链接，仅在Linked-in,QQ和QQ空间使用
        oks.setTitleUrl(ShareUrl.getUrl(newList.get(position).id,3,1));
        // text是分享文本，所有平台都需要这个字段
        oks.setText(newList.get(position).descriptions);
        if(null != newList.get(position).images && 0!=newList.get(position).images.length) {
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
        //启动分享
        oks.show(MobSDK.getContext());
        shareRefresh(position);
    }

    public void shareRefresh(int position) {
        if(null != newList && 0!=newList.size()) {
            newList.get(position).share_num = newList.get(position).share_num+1;
        }
        newGambitAdapter.notifyDataSetChanged();
    }

    //举报弹窗
    public void showReportWindow(int mid,int type) {
        ReportPopupWindows rw = new ReportPopupWindows(activity, reportList(),mid,type);
        WindowUtil.windowDeploy(activity, rw, reyclerView);
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

    public void getBanner() {
        Map<String, String> map = new HashMap<>();
        map.put("type",2+""); //1、文章  2、话题
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
                                    rollPageHome.pause();
                                    rollPageHome.setHintViewVisibility(false);
                                    rollPageHome.setScrollable(false);
                                }else {
                                    rollPageHome.setScrollable(true);
                                }
                            }
                        }
                    });
                }
            }
        });
    }


    public void getHotData() {
        Map<String, String> map = new HashMap<>();
        OkHttp.post(activity, HttpServicePath.URL_RAND_LIST, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                if(null != activity) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Gson gson = new Gson();
                            String data = gson.toJson(baseBean.data);
                            if (StringUtil.isEmpty(data)) return;
                            List<HomeGambitHotBean> homeGambitHotList = gson.fromJson(data, new TypeToken<List<HomeGambitHotBean>>() {}.getType());
                            if(null != homeGambitHotList && 0!=homeGambitHotList.size()) {
                                list.clear();
                                list.addAll(homeGambitHotList);
                                hotAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                }
            }
        });
    }

    public void getFriendsData(int page) {
        Map<String, Object> map = new HashMap<>();
        map.put("num", 10);
        map.put("page", page);
        OkHttp.post(activity,smartRefreshLayout,HttpServicePath.URL_FRIENDS, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                if(null != activity) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Gson gson = new Gson();
                            String data = gson.toJson(baseBean.data);
                            if (StringUtil.isEmpty(data)) return;
                            List<HotGambitCommonBean> homeGambitHotList = gson.fromJson(data, new TypeToken<List<HotGambitCommonBean>>() {}.getType());
                            if(page == 1) {
                                newList.clear();
                            }
                            if(null != homeGambitHotList && 0!=homeGambitHotList.size()) {
                                newList.addAll(homeGambitHotList);
                                newGambitAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                }
            }
        });
    }

    public void follow(String url,int talk_id,RecyclerView.ViewHolder viewHolder) {
        Map<String, String> map = new HashMap<>();
        map.put("talk_id", talk_id + "");
        OkHttp.post(activity, url, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                if(null != activity) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(url.equals(HttpServicePath.URL_FOLLOW_TALK_NO)) { //取消关注
                                if(null == list) return;
                                for(int i = 0;i<list.size();i++) {
                                    if(talk_id == list.get(i).id) {
                                        list.get(i).is_follow = 0;
                                        list.get(i).follow_num = list.get(i).follow_num -1;
                                        if(viewHolder instanceof HomeHotGambitAdapter.GambitViewholder) {
                                            ((HomeHotGambitAdapter.GambitViewholder)viewHolder).attentionTv.setText("关注");
                                            ((HomeHotGambitAdapter.GambitViewholder)viewHolder).attentionTv.setBackgroundResource(R.drawable.rectangle_common_attention);
                                            ((HomeHotGambitAdapter.GambitViewholder)viewHolder).attentionNumTv.setText(ShowNumUtil.showUnm(list.get(i).follow_num)+" 关注");
                                        }
                                    }
                                }
                            }else {
                                if(null == list) return;
                                for(int i = 0;i<list.size();i++) {
                                    if(talk_id == list.get(i).id) {
                                        list.get(i).is_follow = 1;
                                        list.get(i).follow_num = list.get(i).follow_num +1;
                                        if(viewHolder instanceof HomeHotGambitAdapter.GambitViewholder) {
                                            ((HomeHotGambitAdapter.GambitViewholder)viewHolder).attentionTv.setText("已关注");
                                            ((HomeHotGambitAdapter.GambitViewholder)viewHolder).attentionTv.setBackgroundResource(R.drawable.rectangle_common_no_attention);
                                            ((HomeHotGambitAdapter.GambitViewholder)viewHolder).attentionNumTv.setText(ShowNumUtil.showUnm(list.get(i).follow_num)+" 关注");
                                        }
                                    }
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    //点赞/取消点赞
    public void like(String url,int type,int mid,RecyclerView.ViewHolder viewHolder) {
        Map<String,String> map = new HashMap<>();
        map.put("type",type+"");
        map.put("mid",mid+"");
        OkHttp.post(activity, url, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                if(null != activity) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(url.equals(HttpServicePath.URL_LIKE)) {
                                for(int i = 0;i<newList.size();i++) {
                                    if(mid == newList.get(i).id) {
                                        newList.get(i).is_up = 1;
                                        newList.get(i).up_num = newList.get(i).up_num+1;
                                        if(viewHolder instanceof GambitCommonAdapter.NoPicViewholder) {
                                            ((GambitCommonAdapter.NoPicViewholder) viewHolder).likeTv.setText(String.valueOf(newList.get(i).up_num));
                                            ((GambitCommonAdapter.NoPicViewholder) viewHolder).likeTv.setTextColor(activity.getResources().getColor(R.color.FFDE5C51));
                                            ((GambitCommonAdapter.NoPicViewholder) viewHolder).likeImg.setImageResource(R.mipmap.item_like_pre);
                                        }else if(viewHolder instanceof GambitCommonAdapter.BigPicViewholder) {
                                            ((GambitCommonAdapter.BigPicViewholder) viewHolder).likeTv.setText(String.valueOf(newList.get(i).up_num));
                                            ((GambitCommonAdapter.BigPicViewholder) viewHolder).likeTv.setTextColor(activity.getResources().getColor(R.color.FFDE5C51));
                                            ((GambitCommonAdapter.BigPicViewholder) viewHolder).likeImg.setImageResource(R.mipmap.item_like_pre);
                                        }else if(viewHolder instanceof GambitCommonAdapter.MoreViewholder) {
                                            ((GambitCommonAdapter.MoreViewholder) viewHolder).likeTv.setText(String.valueOf(newList.get(i).up_num));
                                            ((GambitCommonAdapter.MoreViewholder) viewHolder).likeTv.setTextColor(activity.getResources().getColor(R.color.FFDE5C51));
                                            ((GambitCommonAdapter.MoreViewholder) viewHolder).likeImg.setImageResource(R.mipmap.item_like_pre);
                                        }
                                    }
                                }
                            }else {
                                for(int i = 0;i<newList.size();i++) {
                                    if(mid == newList.get(i).id) {
                                        newList.get(i).is_up = 0;
                                        newList.get(i).up_num = newList.get(i).up_num-1;
                                        if(viewHolder instanceof GambitCommonAdapter.NoPicViewholder) {
                                            ((GambitCommonAdapter.NoPicViewholder) viewHolder).likeTv.setText(String.valueOf(newList.get(i).up_num));
                                            ((GambitCommonAdapter.NoPicViewholder) viewHolder).likeTv.setTextColor(activity.getResources().getColor(R.color.FF666666));
                                            ((GambitCommonAdapter.NoPicViewholder) viewHolder).likeImg.setImageResource(R.mipmap.item_like);
                                        }else if(viewHolder instanceof GambitCommonAdapter.BigPicViewholder) {
                                            ((GambitCommonAdapter.BigPicViewholder) viewHolder).likeTv.setText(String.valueOf(newList.get(i).up_num));
                                            ((GambitCommonAdapter.BigPicViewholder) viewHolder).likeTv.setTextColor(activity.getResources().getColor(R.color.FF666666));
                                            ((GambitCommonAdapter.BigPicViewholder) viewHolder).likeImg.setImageResource(R.mipmap.item_like);
                                        }else if(viewHolder instanceof GambitCommonAdapter.MoreViewholder) {
                                            ((GambitCommonAdapter.MoreViewholder) viewHolder).likeTv.setText(String.valueOf(newList.get(i).up_num));
                                            ((GambitCommonAdapter.MoreViewholder) viewHolder).likeTv.setTextColor(activity.getResources().getColor(R.color.FF666666));
                                            ((GambitCommonAdapter.MoreViewholder) viewHolder).likeImg.setImageResource(R.mipmap.item_like);
                                        }
                                    }
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    //用户id 	type、1=添加关注，2=取消关注
    public void follow(int parentid,int type,RecyclerView.ViewHolder viewHolder) {
        Map<String, String> map = new HashMap<>();
        map.put("parentid", parentid + "");
        map.put("type", type + "");
        OkHttp.post(activity, HttpServicePath.URL_ADD_FOLLOW, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                if(null != activity) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(1== type) {
                                for(int i = 0;i<list.size();i++) {
                                    if(parentid == list.get(i).userid) {
                                        list.get(i).is_follow = 1;
                                        list.get(i).follow_num = list.get(i).follow_num +1;
                                        if(viewHolder instanceof HomeHotGambitAdapter.GambitViewholder) {
                                            ((HomeHotGambitAdapter.GambitViewholder)viewHolder).attentionTv.setText("已关注");
                                            ((HomeHotGambitAdapter.GambitViewholder)viewHolder).attentionTv.setBackgroundResource(R.drawable.rectangle_common_no_attention);
                                            ((HomeHotGambitAdapter.GambitViewholder)viewHolder).attentionNumTv.setText(ShowNumUtil.showUnm(list.get(i).follow_num)+" 关注");
                                        }
                                    }
                                }

                                for(int i = 0;i<newList.size();i++) {
                                    if(parentid == newList.get(i).userid) {
                                        newList.get(i).is_follow = 1;
                                        if(viewHolder instanceof GambitCommonAdapter.NoPicViewholder) {
                                            ((GambitCommonAdapter.NoPicViewholder)viewHolder).attentionTv.setText("已关注");
                                            ((GambitCommonAdapter.NoPicViewholder)viewHolder).attentionTv.setBackgroundResource(R.drawable.rectangle_common_no_attention);
                                        }else if(viewHolder instanceof GambitCommonAdapter.BigPicViewholder) {
                                            ((GambitCommonAdapter.BigPicViewholder)viewHolder).attentionTv.setText("已关注");
                                            ((GambitCommonAdapter.BigPicViewholder)viewHolder).attentionTv.setBackgroundResource(R.drawable.rectangle_common_no_attention);
                                        }else if(viewHolder instanceof GambitCommonAdapter.MoreViewholder) {
                                            ((GambitCommonAdapter.MoreViewholder)viewHolder).attentionTv.setText("已关注");
                                            ((GambitCommonAdapter.MoreViewholder)viewHolder).attentionTv.setBackgroundResource(R.drawable.rectangle_common_no_attention);
                                        }
                                    }
                                }
                            }else {
                                for(int i = 0;i<list.size();i++) {
                                    if(parentid == list.get(i).userid) {
                                        list.get(i).is_follow = 0;
                                        list.get(i).follow_num = list.get(i).follow_num-1;
                                        if(viewHolder instanceof HomeHotGambitAdapter.GambitViewholder) {
                                            ((HomeHotGambitAdapter.GambitViewholder)viewHolder).attentionTv.setText("关注");
                                            ((HomeHotGambitAdapter.GambitViewholder)viewHolder).attentionTv.setBackgroundResource(R.drawable.rectangle_common_attention);
                                            ((HomeHotGambitAdapter.GambitViewholder)viewHolder).attentionNumTv.setText(ShowNumUtil.showUnm(list.get(i).follow_num)+" 关注");
                                        }
                                    }
                                }

                                for(int i = 0;i<newList.size();i++) {
                                    if(parentid == newList.get(i).userid) {
                                        newList.get(i).is_follow = 0;
                                        if(viewHolder instanceof GambitCommonAdapter.NoPicViewholder) {
                                            ((GambitCommonAdapter.NoPicViewholder)viewHolder).attentionTv.setText("关注");
                                            ((GambitCommonAdapter.NoPicViewholder)viewHolder).attentionTv.setBackgroundResource(R.drawable.rectangle_common_attention);
                                        }else if(viewHolder instanceof GambitCommonAdapter.BigPicViewholder) {
                                            ((GambitCommonAdapter.BigPicViewholder)viewHolder).attentionTv.setText("关注");
                                            ((GambitCommonAdapter.BigPicViewholder)viewHolder).attentionTv.setBackgroundResource(R.drawable.rectangle_common_attention);
                                        }else if(viewHolder instanceof GambitCommonAdapter.MoreViewholder) {
                                            ((GambitCommonAdapter.MoreViewholder)viewHolder).attentionTv.setText("关注");
                                            ((GambitCommonAdapter.MoreViewholder)viewHolder).attentionTv.setBackgroundResource(R.drawable.rectangle_common_attention);
                                        }
                                    }
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    //删除自己的话题
    public void delMyselfGambit(int talk_id) {
        Map<String, String> map = new HashMap<>();
        map.put("talk_id", talk_id + "");
        OkHttp.post(activity, HttpServicePath.URL_TALK_LIST_DEL, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                if(null != activity) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for(int i = 0;i<newList.size();i++) {
                                if(talk_id == newList.get(i).id) {
                                    if(i<newList.size()) {
                                        newList.remove(i);
                                    }
                                    newGambitAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    @OnClick({R.id.addGambitImg})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.addGambitImg:
                if(ClickUtil.onClick()) {
                    Intent intent;
                    if(CheckLogin.isLogin(activity)) {
                        intent = new Intent(activity,JoinGambitActivity.class);
                        intent.putExtra("talktype",2);
                        startActivity(intent);
                    }else {
                        intent = new Intent(activity,LoginActivity.class);
                        startActivity(intent);
                    }
                }
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void allGambitHotNum(EventTags.AllGambitHotNum allGambitHotNum) {
        if(null != list) {
            for(int i = 0;i<list.size();i++) {
                if(list.get(i).id == allGambitHotNum.getId()) {
                    list.get(i).hot_num = allGambitHotNum.getNum();
                    hotAdapter.notifyDataSetChanged();
                }
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loginSucceed(EventTags.LoginSucceed loginSucceed) {
        getHotData();
        page =1;
        getFriendsData(page);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void toLogin(EventTags.ToLogin toLogin) {
        getHotData();
        page = 1;
        getFriendsData(page);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
