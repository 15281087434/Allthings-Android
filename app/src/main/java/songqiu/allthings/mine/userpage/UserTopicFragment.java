package songqiu.allthings.mine.userpage;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import songqiu.allthings.Event.EventTags;
import songqiu.allthings.R;
import songqiu.allthings.adapter.GambitCommonAdapter;
import songqiu.allthings.adapter.UserToppicAdapter;
import songqiu.allthings.base.BaseFragment;
import songqiu.allthings.bean.HotGambitCommonBean;
import songqiu.allthings.bean.ReportBean;
import songqiu.allthings.bean.UserPagerBean;
import songqiu.allthings.http.BaseBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.http.OkHttp;
import songqiu.allthings.http.RequestCallBack;
import songqiu.allthings.iterface.GambitItemListener;
import songqiu.allthings.iterface.WindowShareListener;
import songqiu.allthings.util.CopyButtonLibrary;
import songqiu.allthings.util.SharedPreferencedUtils;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.util.ToastUtil;
import songqiu.allthings.util.VibratorUtil;
import songqiu.allthings.util.WindowUtil;
import songqiu.allthings.util.theme.ShareUrl;
import songqiu.allthings.view.ReportPopupWindows;
import songqiu.allthings.view.SharePopupWindows;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/9/17
 *
 *类描述：
 *
 ********/
public class UserTopicFragment extends BaseFragment {
    @BindView(R.id.line)
    TextView line;
    @BindView(R.id.recycle)
    RecyclerView recycle;
    @BindView(R.id.iv_icon)
    ImageView ivIcon;
    @BindView(R.id.tv_message)
    TextView tvMessage;
    @BindView(R.id.emptyLayout)
    LinearLayout emptyLayout;
    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout smartRefreshLayout;

    public int userId;
    int pageNo = 1;
    UserToppicAdapter adapter;
    List<HotGambitCommonBean> list;

    UserPagerActivity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (UserPagerActivity) context;//保存Context引用
    }
    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }

    @Override
    public int initView() {
        return R.layout.fragment_user_all;
    }

    @Override
    public void init() {
        initRecyclerView();
    }

    @Override
    public void onResume() {
        super.onResume();
        getData(pageNo,false);
    }

    public void initRecyclerView() {
        list = new ArrayList<>();
        adapter = new UserToppicAdapter(activity,list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycle.setLayoutManager(linearLayoutManager);
        recycle.setAdapter(adapter);

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
        //事件
        adapter.setGambitItemListener(new GambitItemListener() {
            @Override
            public void addLike(String url, int type, int mid) {
                like(url,type,mid);
            }

            @Override
            public void addFollow(int parentid,int type) {
                follow(parentid,type);
            }

            @Override
            public void delete(int type,int talk_id,int userId) {
                if(1==type) {//删除
                    delMyselfGambit(talk_id);
                }else {//举报
                    if(null == list || 0 == list.size()) return;
                    showReportWindow(talk_id,3);
                }
            }

            @Override
            public void addShare(int position) {
                showShareWindow(0,position);
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
                if(null == list || 0 == list.size()) return;
                showShare(QQ.NAME,position);
                totalShare(3,list.get(position).id);
                rw.dismiss();
            }

            @Override
            public void wechatShare(int positon) {
                if(null == list || 0 == list.size()) return;
                showShare(Wechat.NAME,position);
                totalShare(3,list.get(position).id);
                rw.dismiss();
            }

            @Override
            public void wechatFriendShare(int positon) {
                if(null == list || 0 == list.size()) return;
                showShare(WechatMoments.NAME,position);
                totalShare(3,list.get(position).id);
                rw.dismiss();
            }

            @Override
            public void link(int position) {
                if(null == list || 0 == list.size()) return;
                String link =  ShareUrl.getUrl(list.get(position).id,3,1);
                CopyButtonLibrary copyButtonLibrary = new CopyButtonLibrary(activity,link);
                copyButtonLibrary.init(link);
                ToastUtil.showToast(activity,"复制成功!");
            }

            @Override
            public void report() {
                if(null == list || 0 == list.size()) return;
                showReportWindow(list.get(position).id,3);
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
        oks.setTitle(list.get(position).user_nickname);
        // titleUrl是标题的网络链接，仅在Linked-in,QQ和QQ空间使用
        oks.setTitleUrl(ShareUrl.getUrl(list.get(position).id,3,1));
        // text是分享文本，所有平台都需要这个字段
        oks.setText(list.get(position).descriptions);
        if(null != list.get(position).images && 0!=list.get(position).images.length) {
            if(!StringUtil.isEmpty(list.get(position).images[0])) {
                if(!list.get(position).images[0].contains("http")) {
                    list.get(position).images[0] = HttpServicePath.BasePicUrl + list.get(position).images[0];
                }
                oks.setImageUrl(list.get(position).images[0]);
            }else {
                oks.setImageUrl(HttpServicePath.BasePicUrl+"sharelog.png?time="+System.currentTimeMillis());
            }
        }else {
            oks.setImageUrl(HttpServicePath.BasePicUrl+"sharelog.png?time="+System.currentTimeMillis());
        }
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(ShareUrl.getUrl(list.get(position).id,3,1));
        //启动分享
        oks.show(MobSDK.getContext());
        shareRefresh(position);
    }

    public void shareRefresh(int position) {
        if(null != list && 0!=list.size()) {
            list.get(position).share_num = list.get(position).share_num+1;
        }
        adapter.notifyDataSetChanged();
    }

    //举报弹窗
    public void showReportWindow(int mid,int type) {
        ReportPopupWindows rw = new ReportPopupWindows(activity, reportList(),mid,type);
        WindowUtil.windowDeploy(activity, rw, line);
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

    public void getData(int page,boolean ringDown) {
        Map<String, String> map = new HashMap<>();
        map.put("type", 3 + "");
        map.put("userid", userId + "");
        map.put("num", 10 + "");
        map.put("page", page + "");
        OkHttp.post(activity, smartRefreshLayout, HttpServicePath.URL_CENTER, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                if(null != activity) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Gson gson = new Gson();
                            String data = gson.toJson(baseBean.data);
                            if(StringUtil.isEmpty(data))return;
                            List<HotGambitCommonBean> hotGambitCommonList = gson.fromJson(data, new TypeToken<List<HotGambitCommonBean>>() {}.getType());
                            if (page == 1) {
                                list.clear();
                                if (null == hotGambitCommonList || 0 == hotGambitCommonList.size()) {
                                    emptyLayout.setVisibility(View.VISIBLE);
                                    recycle.setVisibility(View.GONE);
                                }else {
                                    emptyLayout.setVisibility(View.GONE);
                                    recycle.setVisibility(View.VISIBLE);
                                }
                            }
                            if (null != hotGambitCommonList && 0 != hotGambitCommonList.size()) {
                                list.addAll(hotGambitCommonList);
                                adapter.notifyDataSetChanged();
                            }

                            if(ringDown) {
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

    //点赞/取消点赞
    public void like(String url,int type,int mid) {
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
                                for(int i = 0;i<list.size();i++) {
                                    if(mid == list.get(i).id) {
                                        list.get(i).is_up = 1;
                                        list.get(i).up_num = list.get(i).up_num+1;
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            }else {
                                for(int i = 0;i<list.size();i++) {
                                    if(mid == list.get(i).id) {
                                        list.get(i).is_up = 0;
                                        list.get(i).up_num = list.get(i).up_num-1;
                                        adapter.notifyDataSetChanged();
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
    public void follow(int parentid,int type) {
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
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            }else {
                                for(int i = 0;i<list.size();i++) {
                                    if(parentid == list.get(i).userid) {
                                        list.get(i).is_follow = 0;
                                        adapter.notifyDataSetChanged();
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
                            for(int i = 0;i<list.size();i++) {
                                if(talk_id == list.get(i).id) {
                                    if(i<list.size()) {
                                        list.remove(i);
                                    }
                                    adapter.notifyDataSetChanged();
                                }
                            }
                            if(null == list || 0 == list.size()) {
                                emptyLayout.setVisibility(View.VISIBLE);
                                recycle.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        });
    }


}
