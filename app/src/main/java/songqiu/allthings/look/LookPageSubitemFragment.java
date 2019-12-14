package songqiu.allthings.look;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import songqiu.allthings.Event.EventTags;
import songqiu.allthings.R;
import songqiu.allthings.activity.MainActivity;
import songqiu.allthings.adapter.HomeTabClassAdapter;
import songqiu.allthings.adapter.LookTabClassAdapter;
import songqiu.allthings.base.BaseFragment;
import songqiu.allthings.bean.HomeSubitemBean;
import songqiu.allthings.bean.LookVideoBean;
import songqiu.allthings.bean.ReportBean;
import songqiu.allthings.bean.TabClassBean;
import songqiu.allthings.http.BaseBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.http.OkHttp;
import songqiu.allthings.http.RequestCallBack;
import songqiu.allthings.iterface.DialogDeleteListener;
import songqiu.allthings.iterface.LookItemListener;
import songqiu.allthings.iterface.WindowShareListener;
import songqiu.allthings.util.CopyButtonLibrary;
import songqiu.allthings.util.LogUtil;
import songqiu.allthings.util.SharedPreferencedUtils;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.util.ToastUtil;
import songqiu.allthings.util.VibratorUtil;
import songqiu.allthings.util.WindowUtil;
import songqiu.allthings.util.theme.ShareUrl;
import songqiu.allthings.view.DialogDeleteAdvertising;
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
public class LookPageSubitemFragment extends BaseFragment {

    @BindView(R.id.line)
    TextView line;
    @BindView(R.id.recycle)
    RecyclerView recycle;
    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout smartRefreshLayout;
    @BindView(R.id.prestrainImg)
    ImageView prestrainImg;
    @BindView(R.id.hintLayout)
    RelativeLayout hintLayout;

    @BindView(R.id.emptyLayout)
    LinearLayout emptyLayout;
    int pageNo = 1;
    public String name;
    public String tag;
    public int type;
    public int category;

    List<LookVideoBean> item ;
    LookTabClassAdapter adapter;

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
        return R.layout.fragment_look_page_subitem;
    }

    @Override
    public void init() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
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

    public void initRecycle() {
        item = new ArrayList<>();
        adapter = new LookTabClassAdapter(activity,item);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycle.setLayoutManager(linearLayoutManager);
        recycle.setAdapter(adapter);
        adapter.setLomeItemListener(new LookItemListener() {
            @Override
            public void addSetting(int position) {
                showShareWindow(1,position);
            }

            @Override
            public void addLike(String url, int type, int mid, LookVideoBean lookVideoBean,RecyclerView.ViewHolder viewHolder) {
                like(url,type,mid,lookVideoBean,viewHolder);
            }

            @Override
            public void addFollow(int parentid, int type, List<LookVideoBean> item) {
                follow(parentid,type,item);
            }

            @Override
            public void delete(int position) {
                initDialog(position);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void hidePrestrain(EventTags.HidePrestrain hidePrestrain) {
        prestrainImg.setVisibility(View.GONE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void homeRefresh(EventTags.HomeRefresh homeRefresh) {
        if(visible) {
            pageNo = 1;
            smartRefreshLayout.autoRefresh();
        }
    }

    public void getData(int page,boolean ringDown) {
        Map<String,Object> map = new HashMap<>();
        String url;
        if(2==type) {
            url = HttpServicePath.URL_VIDEO;
            map.put("category",category);
            map.put("type",type);
            map.put("page",page);
        }else {
            url = HttpServicePath.URL_STRING+tag;
            map.put("page",page);
        }
        OkHttp.post(activity,smartRefreshLayout,url , map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                if(null != activity) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            prestrainImg.setVisibility(View.GONE);
                            Gson gson = new Gson();
                            String data = gson.toJson(baseBean.data);
                            if(StringUtil.isEmpty(data)) return;
                            List<LookVideoBean> classifyBeanList = gson.fromJson(data, new TypeToken<List<LookVideoBean>>() {}.getType());
                            if(pageNo == 1) {
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
                            }else {
//                            ToastUtil.showToast(activity,"暂无更多数据");
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
    public void colseLookVideo(EventTags.ColseLookVideo colseLookVideo) {
        HeartVideoManager.getInstance().pause();
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
    public void like(String url,int type,int mid,LookVideoBean lookVideoBean,RecyclerView.ViewHolder viewHolder) {
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
                                lookVideoBean.is_up = 1;
                                lookVideoBean.up_num = lookVideoBean.up_num+1;
                                if(viewHolder instanceof LookTabClassAdapter.VideoHolder) {
                                    ((LookTabClassAdapter.VideoHolder) viewHolder).likeTv.setText(String.valueOf(lookVideoBean.up_num));
                                    ((LookTabClassAdapter.VideoHolder) viewHolder).likeTv.setTextColor(activity.getResources().getColor(R.color.FFDE5C51));
                                    ((LookTabClassAdapter.VideoHolder) viewHolder).likeImg.setImageResource(R.mipmap.item_like_pre);
                                }
                            }else {
                                lookVideoBean.is_up = 0;
                                lookVideoBean.up_num = lookVideoBean.up_num-1;
                                if(viewHolder instanceof LookTabClassAdapter.VideoHolder) {
                                    ((LookTabClassAdapter.VideoHolder) viewHolder).likeTv.setText(String.valueOf(lookVideoBean.up_num));
                                    ((LookTabClassAdapter.VideoHolder) viewHolder).likeTv.setTextColor(activity.getResources().getColor(R.color.FF666666));
                                    ((LookTabClassAdapter.VideoHolder) viewHolder).likeImg.setImageResource(R.mipmap.item_like);
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    //用户id 	type、1=添加关注，2=取消关注
    public void follow(int parentid,int type,List<LookVideoBean> item) {
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
        oks.setTitleUrl(ShareUrl.getUrl(item.get(position).articleid,2));
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
        oks.setUrl(ShareUrl.getUrl(item.get(position).articleid,2));
        //启动分享
        oks.show(MobSDK.getContext());
    }

    //分享弹窗
    public void showShareWindow(int type,int position) {
        SharePopupWindows rw = new SharePopupWindows(activity,type,position);
        WindowUtil.windowDeploy(activity,rw,line);
        rw.setWindowShareListener(new WindowShareListener() {
            @Override
            public void qqShare(int position) {
                if(null == item || 0 == item.size()) return;
                showShare(QQ.NAME,position);
                totalShare(2,item.get(position).articleid);
                rw.dismiss();
            }

            @Override
            public void wechatShare(int position) {
                if(null == item || 0 == item.size()) return;
                showShare(Wechat.NAME,position);
                totalShare(2,item.get(position).articleid);
                rw.dismiss();
            }

            @Override
            public void wechatFriendShare(int position) {
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
                showReportWindow(item.get(position).articleid,2);
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

    public void initDialog(int position) {
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

    //    @Override
//    public void onPause() {
//        super.onPause();
//        HeartVideoManager.getInstance().release();
//    }

    //    @Override
//    public void onBackPressed() {
//        if (HeartVideoManager.getInstance().onBackPressd()) return;
//        super.onBackPressed();
//    }

}
