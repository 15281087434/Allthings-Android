package songqiu.allthings.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.heartfor.heartvideo.video.HeartVideo;
import com.heartfor.heartvideo.video.HeartVideoManager;
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
import butterknife.Unbinder;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import songqiu.allthings.Event.EventTags;
import songqiu.allthings.R;
import songqiu.allthings.activity.CommentWebViewActivity;
import songqiu.allthings.activity.MainActivity;
import songqiu.allthings.adapter.HomeTabClassAdapter;
import songqiu.allthings.adapter.LookTabClassAdapter;
import songqiu.allthings.base.BaseFragment;
import songqiu.allthings.bean.HomeSubitemBean;
import songqiu.allthings.bean.LookVideoBean;
import songqiu.allthings.bean.ReportBean;
import songqiu.allthings.bean.TabClassBean;
import songqiu.allthings.bean.UnLikeBean;
import songqiu.allthings.bean.UserInfoBean;
import songqiu.allthings.constant.SnsConstants;
import songqiu.allthings.http.BaseBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.http.OkHttp;
import songqiu.allthings.http.RequestCallBack;
import songqiu.allthings.iterface.DialogDeleteListener;
import songqiu.allthings.iterface.HomeItemListener;
import songqiu.allthings.iterface.TaskDialogSignListener;
import songqiu.allthings.iterface.WindowShareListener;
import songqiu.allthings.mine.BindingPhoneActivity;
import songqiu.allthings.util.CheckLogin;
import songqiu.allthings.util.CopyButtonLibrary;
import songqiu.allthings.util.LogUtil;
import songqiu.allthings.util.ScreenUtils;
import songqiu.allthings.util.SharedPreferencedUtils;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.util.ToastUtil;
import songqiu.allthings.util.VibratorUtil;
import songqiu.allthings.util.WindowUtil;
import songqiu.allthings.util.theme.ShareUrl;
import songqiu.allthings.view.DialogDeleteAdvertising;
import songqiu.allthings.view.DialogDeleteCommon;
import songqiu.allthings.view.DialogSign;
import songqiu.allthings.view.ReportPopupWindows;
import songqiu.allthings.view.SharePopupWindows;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/8/7
 *
 *类描述：
 *
 ********/
public class HomePageSubitemFragment extends BaseFragment {

    @BindView(R.id.recycle)
    RecyclerView recycle;
    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout smartRefreshLayout;
    @BindView(R.id.emptyLayout)
    LinearLayout emptyLayout;
    @BindView(R.id.line)
    TextView line;
    @BindView(R.id.prestrainImg)
    ImageView prestrainImg;
    @BindView(R.id.prestrainlookImg)
    ImageView prestrainlookImg;
    @BindView(R.id.hintLayout)
    RelativeLayout hintLayout;

    public String name;
    public String tag;
    public int category;
    public int type;

    List<HomeSubitemBean> item ;
    HomeTabClassAdapter adapter;
    int pageNo = 1;

    MainActivity activity;
    boolean visible;

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
        return R.layout.fragment_home_page_subitem;
    }

    @Override
    public void init() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        if("home_vintro".equals(tag)) {//视频
            prestrainlookImg.setVisibility(View.VISIBLE);
            prestrainImg.setVisibility(View.GONE);
        }else {
            prestrainlookImg.setVisibility(View.GONE);
            prestrainImg.setVisibility(View.VISIBLE);
        }
        initRecycle();
        getData(pageNo,false);
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        visible = isVisibleToUser;
        if(!isVisibleToUser) {
            HeartVideoManager.getInstance().pause();
        }
    }

    private int position_play = 0;//播放的位置

    private boolean isLooper = true;
    private int looperFlag = 0;//0,无自动播放，1.自动播放上一个，2自动播放下一个
    public void initRecycle() {
        item = new ArrayList<>();
        adapter = new HomeTabClassAdapter(activity,item,tag);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycle.setLayoutManager(linearLayoutManager);
        recycle.setAdapter(adapter);

        recycle.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //滑动停止后，
                if (newState == RecyclerView.SCROLL_STATE_IDLE && isLooper && looperFlag != 0) {
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                    switch (looperFlag) {

                        case 1:
                            int position_lastVisible=layoutManager.findLastVisibleItemPosition();
                            if (position_lastVisible==position_play){
                                //自动播放上一个
                                position_play-=1;
                            }else {
                                //最后一个可见的item和滑出去的上次播放的view隔了N(N>=1)个Item,所以自动播放倒数第2个可见的item
                                position_play=position_lastVisible-1;
                            }

                            break;
                        case 2:
                            int position_firstVisible=layoutManager.findFirstVisibleItemPosition();
                            if (position_firstVisible==position_play){
                                //自动播放下一个

                                position_play+=1;
                            }else {
                                //第一个可见的item和滑出去的上次播放的view隔了N(N>=1)个Item,所以自动播放第2个可见的item
                                position_play=position_firstVisible+1;

                            }

                            break;
                    }
                    item.get(position_play).state=1;

                    recyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            View childAt = layoutManager.findViewByPosition((position_play));
                                  if (childAt!=null&&item.get(position_play).ad==1&&item.get(position_play).type!=1){
                                //视频开始播放
                                HeartVideo video=childAt.findViewById(R.id.videoView);
                                if(video!=null) {
                                    if (video == HeartVideoManager.getInstance().getCurrPlayVideo()) {
                                      video.startSlence();
                                    }
                                }
                            }
                            looperFlag = 0;//自动播放上一个
                        }
                    });

                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (!isLooper) return;
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                View view = layoutManager.findViewByPosition(position_play);
                //说明播放的view还未完全消失
                if (view != null) {

                    int y_t_rv = ScreenUtils.getViewScreenLocation(recyclerView)[1];//RV顶部Y坐标
                    int y_b_rv = y_t_rv + recyclerView.getHeight();//RV底部Y坐标

                    int y_t_view = ScreenUtils.getViewScreenLocation(view)[1];//播放的View顶部Y坐标
                    int height_view = view.getHeight();
                    int y_b_view = y_t_view + height_view;//播放的View底部Y坐标

                    //上滑
                    if (dy > 0) {
                        //播放的View上滑，消失了3分之2了,停止播放，
                        if ((y_t_rv > y_t_view) && ((y_t_rv - y_t_view) > height_view * 2f / 3)) {

                            item.get(position_play).state=0;
                            recyclerView.post(new Runnable() {
                                @Override
                                public void run() {
                                    View childAt = layoutManager.findViewByPosition((position_play));
                                          if (childAt!=null&&item.get(position_play).ad==1&&item.get(position_play).type!=1){
                                        //视频开始播放
                                        HeartVideo video=childAt.findViewById(R.id.videoView);
                                        if(video!=null) {
                                            if (video == HeartVideoManager.getInstance().getCurrPlayVideo()) {
                                                HeartVideoManager.getInstance().release();
                                            }
                                        }
                                    }
                                    looperFlag = 2;//自动播放上一个
                                }
                            });
                        }

                    } else if (dy < 0) {
                        //下滑

//                        LogUtils.log("y_t_rv", y_t_rv);
//                        LogUtils.log("y_b_rv", y_b_rv);
                        //播放的View下滑，消失了一半了,停止播放
                        if ((y_b_view > y_b_rv) && ((y_b_view - y_b_rv) > height_view * 2f / 3)) {

                            item.get(position_play).state=0;
                            recyclerView.post(new Runnable() {
                                @Override
                                public void run() {
                                    View childAt = layoutManager.findViewByPosition((position_play));
                                          if (childAt!=null&&item.get(position_play).ad==1&&item.get(position_play).type!=1){
                                        //视频开始播放
                                        HeartVideo video=childAt.findViewById(R.id.videoView);
                                        if(video!=null) {
                                            if (video == HeartVideoManager.getInstance().getCurrPlayVideo()) {
                                                HeartVideoManager.getInstance().release();
                                            }
                                        }
                                    }
                                    looperFlag = 1;//自动播放上一个
                                }
                            });


                        }
                    }
                }


            }
        });

        adapter.setHomeItemListener(new HomeItemListener() {
           @Override
           public void addSetting(int position) {
               showShareWindow(1,position);
           }

           @Override
           public void addLike(String url, int type, int mid,HomeSubitemBean homeSubitemBean,RecyclerView.ViewHolder viewHolder) {
               like(url,type,mid,homeSubitemBean,viewHolder);
           }

           @Override
           public void addFollow(int parentid, int type,List<HomeSubitemBean> item) {
                follow(parentid,type,item);
           }

            @Override
            public void delete(int position,int type) { //1= 普通内容  2 = 广告
                if(null != item && 0!= item.size()) {
                    getUnLikeParameter(item.get(position).articleid,item.get(position).type,type,position);
                }
            }
        });

        smartRefreshLayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                pageNo = pageNo+1;
                getData(pageNo,false);
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                pageNo = 1;
                getData(pageNo,true);
            }
        });
    }

    //分享弹窗
    public void showShareWindow(int type,int position) {
        SharePopupWindows rw = new SharePopupWindows(activity,type,position);
        WindowUtil.windowDeploy(activity,rw,line);
        rw.setWindowShareListener(new WindowShareListener() {
            @Override
            public void qqShare(int positon) {
                if(null == item || 0 == item.size()) return;
                showShare(QQ.NAME,position);
                totalShare(2,item.get(position).articleid);
                rw.dismiss();
            }

            @Override
            public void wechatShare(int positon) {
                if(null == item || 0 == item.size()) return;
                showShare(Wechat.NAME,position);
                totalShare(2,item.get(position).articleid);
                rw.dismiss();
            }

            @Override
            public void wechatFriendShare(int positon) {
                if(null == item || 0 == item.size()) return;
                showShare(WechatMoments.NAME,position);
                totalShare(2,item.get(position).articleid);
                rw.dismiss();
            }

            @Override
            public void link(int position) {
                if(null == item || 0 == item.size()) return;
                String link =  ShareUrl.getUrl(item.get(position).articleid,2);
                CopyButtonLibrary copyButtonLibrary = new CopyButtonLibrary(activity,link);
                copyButtonLibrary.init(link);
                ToastUtil.showToast(activity,"复制成功!");
            }

            @Override
            public void report() {
                if(null == item || 0 == item.size()) return;
                showReportWindow(item.get(position).articleid,item.get(position).type);
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

    private List<String> getReportTitle() {
        return Arrays.asList("内容低劣", "广告软文", "政治敏感","色情低俗", "违法信息", "错误观念引导","人身攻击", "涉嫌侵犯");
    }

    public List<ReportBean> reportList() {
        List<ReportBean> list = new ArrayList<>();
        List<String> titleList = getReportTitle();
        for(String title:titleList) {
            ReportBean reportBean = new ReportBean();
            reportBean.title = title;
            list.add(reportBean);
        }
        return list;
    }
    //举报弹窗
    public void showReportWindow(int mid,int type) {
        ReportPopupWindows rw = new ReportPopupWindows(activity, reportList(),mid,type);
        WindowUtil.windowDeploy(activity,rw,line);
    }

    private void showShare(String platform,int position) {
        final OnekeyShare oks = new OnekeyShare();
        //指定分享的平台，如果为空，还是会调用九宫格的平台列表界面
        if (platform != null) {
            oks.setPlatform(platform);
        }
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(item.get(position).title);
        // titleUrl是标题的网络链接，仅在Linked-in,QQ和QQ空间使用
        oks.setTitleUrl(ShareUrl.getUrl(item.get(position).articleid,item.get(position).type));
        // text是分享文本，所有平台都需要这个字段
        oks.setText(item.get(position).descriptions);
        if(!StringUtil.isEmpty(item.get(position).photo)) {
            if(!item.get(position).photo.contains("http")) {
                if(!item.get(position).photo.contains("http")) {
                    item.get(position).photo = HttpServicePath.BasePicUrl + item.get(position).photo;
                }
            }
            oks.setImageUrl(item.get(position).photo);
        }else {
            oks.setImageUrl(HttpServicePath.BasePicUrl+"sharelog.png?time="+System.currentTimeMillis());
        }
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(ShareUrl.getUrl(item.get(position).articleid,item.get(position).type));
        //启动分享
        oks.show(MobSDK.getContext());
    }


    //不喜欢列表
    public void getUnLikeParameter(int articleid,int classType,int type,int position) {
        Map<String, String> map = new HashMap<>();
        map.put("articleid",String.valueOf(articleid));
        map.put("type",classType+"");
        OkHttp.post(activity, HttpServicePath.URL_REPORT_LIST, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        String data = gson.toJson(baseBean.data);
                        if (StringUtil.isEmpty(data)) return;
                        UnLikeBean unLikeBean = gson.fromJson(data, UnLikeBean.class);
                        if(null == unLikeBean) return;
                        initDialog(unLikeBean,type,position);
                    }
                });
            }
        });
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
        OkHttp.post(activity, HttpServicePath.URL_UNLIKE, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {

            }
        });
    }

    public void doDeletel(int position,int bid,int mid) {
        if(1 == item.get(position).type) {
            unLike(bid,mid,1);
        }else {
            unLike(bid,mid,2);
        }
        item.remove(position);
        adapter.notifyDataSetChanged();
        ToastUtil.showToast(activity,"将减少推荐类似内容");
    }


    public void initDialog(UnLikeBean unLikeBean,int type,int position) {
        if(1== type) {
            DialogDeleteCommon dialog = new DialogDeleteCommon(activity,unLikeBean,true);
            dialog.showDialog();
            dialog.setOnItemClickListener(new DialogDeleteCommon.OnItemClick() {
                @Override
                public void onWhichItemClick(int pos) {
                    switch (pos) {
                        case 1:
                            if(null == item || 0 == item.size()) return;
                            doDeletel(position,1,item.get(position).articleid);
                            break;
                        case 2:
                            if(null == item || 0 == item.size()) return;
                            doDeletel(position,2,item.get(position).articleid);
                            break;
                        case 3:
                            if(null == item || 0 == item.size()) return;
                            doDeletel(position,3,item.get(position).articleid);
                            break;
                        case 4:
                            if(null == item || 0 == item.size()) return;
                            doDeletel(position,4,item.get(position).articleid);
                            break;
                        case 5:

                            break;
                    }
                }
            });
        }else {
            DialogDeleteAdvertising dialog = new DialogDeleteAdvertising(activity);
            dialog.showDialog();
            dialog.setOnItemClickListener(new DialogDeleteAdvertising.OnItemClick() {
                @Override
                public void onWhichItemClick(int pos) {
                    switch (pos) {
                        case 1:
                            if(null == item || 0 == item.size()) return;
                            item.remove(position);
                            adapter.notifyDataSetChanged();
                            break;
                        case 2:
                            if(null == item || 0 == item.size()) return;
                            item.remove(position);
                            adapter.notifyDataSetChanged();
                            break;
                    }
                }
            });
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void hidePrestrain(EventTags.HidePrestrain hidePrestrain) {
        prestrainImg.setVisibility(View.GONE);
        prestrainlookImg.setVisibility(View.GONE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void homeRefresh(EventTags.HomeRefresh homeRefresh) {
        if(visible) {
            pageNo = 1;
            smartRefreshLayout.autoRefresh();
        }
    }


    public void getData(int page,boolean ringDown) {
        String url = HttpServicePath.BaseUrl+ tag;
        Map<String,Object> map = new HashMap<>();
        if(StringUtil.isEmpty(tag)) return;
            map.put("category",category);
            map.put("num",10);
            map.put("page",page);
            OkHttp.post(activity, smartRefreshLayout,url, map, new RequestCallBack() {
                @Override
                public void httpResult(BaseBean baseBean) {
                    if(null != activity) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                prestrainImg.setVisibility(View.GONE);
                                prestrainlookImg.setVisibility(View.GONE);
                                Gson gson = new Gson();
                                String data = gson.toJson(baseBean.data);
                                if(StringUtil.isEmpty(data)) return;
                                List<HomeSubitemBean> classifyBeanList = gson.fromJson(data, new TypeToken<List<HomeSubitemBean>>() {}.getType());
                                if(pageNo ==1) {
                                    item.clear();
                                    if(null == classifyBeanList || 0 == classifyBeanList.size()) {
                                        emptyLayout.setVisibility(View.VISIBLE);
                                        recycle.setVisibility(View.GONE);
                                    }else {
                                        emptyLayout.setVisibility(View.GONE);
                                        recycle.setVisibility(View.VISIBLE);
                                    }
                                }
                                if(null != classifyBeanList && 0!=classifyBeanList.size()) {
                                    item.addAll(classifyBeanList);
                                    adapter.notifyDataSetChanged();
                                }
                                if(ringDown) {
                                    hintLayout.setVisibility(View.VISIBLE);
                                    new Handler().postDelayed(new Runnable(){
                                        public void run() {
                                            //execute the task
                                            hintLayout.setVisibility(View.GONE);
                                        }
                                    }, 2000);
                                    new Handler().postDelayed(new Runnable(){
                                        public void run() {
                                            //execute the task
                                            VibratorUtil.ringDown(activity);
                                        }
                                    }, 500);
                                }
                            }
                        });
                    }
                }
            });
    }

    //接受到收藏/取消收藏的通知
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void collectEvent(EventTags.CollectEvent collectEvent) {
        if(null == item || 0 == item.size()) return;
        for(int i = 0;i<item.size();i++) {
            if(item.get(i).articleid == collectEvent.getArticleid()) {
                if(collectEvent.getCollect()) {
                    item.get(i).collect_num =  item.get(i).collect_num + 1;
                }else {
                    item.get(i).collect_num =  item.get(i).collect_num - 1>0?item.get(i).collect_num:0;
                }
                adapter.notifyDataSetChanged();
            }
        }
    }

    //视频评论数
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void videoCommentNum(EventTags.VideoCommentNum videoCommentNum) {
        if(null == item || 0 == item.size()) return;
        for(int i = 0;i<item.size();i++) {
            if(item.get(i).articleid == videoCommentNum.getId()) {
                if(!StringUtil.isEmpty(videoCommentNum.getNum())) {
                    item.get(i).comment_num = Integer.valueOf(videoCommentNum.getNum());
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void toDeleteItemById(EventTags.DeleteItemById deleteItemById) {
        if(null == item || 0 == item.size()) return;
        for(int i = 0;i<item.size();i++) {
            if(item.get(i).articleid == deleteItemById.getId()) {
                item.remove(i);
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void attention(EventTags.Attention attention) {
        if(null == item || 0 == item.size()) return;
        if(null == adapter) return;
        for(int i = 0;i<item.size();i++) {
            if(item.get(i).userid == attention.getUserId()) {
                item.get(i).is_follow = attention.getFollow();
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshLook(EventTags.RefreshLook refreshLook) {
        if(null == item || 0 == item.size()) return;
        if(refreshLook.url.equals(HttpServicePath.URL_LIKE)) {
            for(int i =0;i<item.size();i++) {
                if(item.get(i).articleid == refreshLook.mid) {
                    item.get(i).is_up = 1;
                    item.get(i).up_num = item.get(i).up_num+1;
                    adapter.notifyDataSetChanged();
                }
            }
        }else {
            for(int i =0;i<item.size();i++) {
                if(item.get(i).articleid == refreshLook.mid) {
                    item.get(i).is_up = 0;
                    item.get(i).up_num = item.get(i).up_num-1;
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    //点赞/取消点赞
    public void like(String url,int type,int mid,HomeSubitemBean homeSubitemBean,RecyclerView.ViewHolder viewHolder) {
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
                                homeSubitemBean.is_up = 1;
                                homeSubitemBean.up_num = homeSubitemBean.up_num+1;
                                if(viewHolder instanceof HomeTabClassAdapter.LookVideoHolder) {
                                    ((HomeTabClassAdapter.LookVideoHolder) viewHolder).likeTv.setText(String.valueOf(homeSubitemBean.up_num));
                                    ((HomeTabClassAdapter.LookVideoHolder) viewHolder).likeTv.setTextColor(activity.getResources().getColor(R.color.FFDE5C51));
                                    ((HomeTabClassAdapter.LookVideoHolder) viewHolder).likeImg.setImageResource(R.mipmap.item_like_pre);
                                }
                            }else {
                                homeSubitemBean.is_up = 0;
                                homeSubitemBean.up_num = homeSubitemBean.up_num-1;
                                if(viewHolder instanceof HomeTabClassAdapter.LookVideoHolder) {
                                    ((HomeTabClassAdapter.LookVideoHolder) viewHolder).likeTv.setText(String.valueOf(homeSubitemBean.up_num));
                                    ((HomeTabClassAdapter.LookVideoHolder) viewHolder).likeTv.setTextColor(activity.getResources().getColor(R.color.FF666666));
                                    ((HomeTabClassAdapter.LookVideoHolder) viewHolder).likeImg.setImageResource(R.mipmap.item_like);
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    //用户id 	type、1=添加关注，2=取消关注
    public void follow(int parentid,int type,List<HomeSubitemBean> item) {
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
                            if(1 == type) {
                                for(int i = 0;i<item.size();i++) {
                                    if(parentid == item.get(i).userid) {
                                        item.get(i).is_follow = 1;
                                    }
                                }
                                EventBus.getDefault().post(new EventTags.Attention(parentid,1));
                            }else {
                                for(int i = 0;i<item.size();i++) {
                                    if(parentid == item.get(i).userid) {
                                        item.get(i).is_follow = 0;
                                    }
                                }
                                EventBus.getDefault().post(new EventTags.Attention(parentid,0));
                            }
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void colseLookVideo(EventTags.ColseLookVideo colseLookVideo) {
        HeartVideoManager.getInstance().pause();
    }

    @Override
    public void onStop() {
        super.onStop();
        HeartVideoManager.getInstance().release();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
