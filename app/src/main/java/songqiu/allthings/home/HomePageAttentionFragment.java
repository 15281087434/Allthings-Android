package songqiu.allthings.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import songqiu.allthings.activity.MainActivity;
import songqiu.allthings.adapter.HomeAttentionAdapter;
import songqiu.allthings.base.BaseFragment;
import songqiu.allthings.bean.HomeAttentionBean;
import songqiu.allthings.bean.HomeSubitemBean;
import songqiu.allthings.bean.ReportBean;
import songqiu.allthings.http.BaseBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.http.OkHttp;
import songqiu.allthings.http.RequestCallBack;
import songqiu.allthings.iterface.HomeAttentionListener;
import songqiu.allthings.iterface.HomeItemListener;
import songqiu.allthings.iterface.WindowShareListener;
import songqiu.allthings.login.LoginActivity;
import songqiu.allthings.util.CopyButtonLibrary;
import songqiu.allthings.util.LogUtil;
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
 *创建时间 2019/8/7
 *
 *类描述：首页 关注
 *
 ********/
public class HomePageAttentionFragment extends BaseFragment {

    @BindView(R.id.line)
    TextView line;
    @BindView(R.id.recycle)
    RecyclerView recycle;
    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout smartRefreshLayout;
    @BindView(R.id.iv_icon)
    ImageView ivIcon;
    @BindView(R.id.tv_message)
    TextView tvMessage;
    @BindView(R.id.emptyLayout)
    LinearLayout emptyLayout;
    @BindView(R.id.loginTv)
    TextView loginTv;
    @BindView(R.id.loginLayout)
    LinearLayout loginLayout;
    @BindView(R.id.prestrainImg)
    ImageView prestrainImg;

    List<HomeAttentionBean> item;
    HomeAttentionAdapter adapter;

    public String tag;
    public int pageNo = 1;

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
        return R.layout.fragment_home_page_attention;
    }

    @Override
    public void init() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        initRecycle();
    }

    @Override
    public void onResume() {
        super.onResume();
        pageNo =1;
        getData(pageNo,false);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(!isVisibleToUser) {
            HeartVideoManager.getInstance().pause();
        }
    }

    public void initRecycle() {
        item = new ArrayList<>();
        adapter = new HomeAttentionAdapter(activity, item);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycle.setLayoutManager(linearLayoutManager);
        recycle.setAdapter(adapter);

        adapter.setHomeAttentionListener(new HomeAttentionListener() {
            @Override
            public void addShare(int position) {
                showShareWindow(1,position);
            }

            @Override
            public void addLike(String url, int type, int mid, HomeAttentionBean homeAttentionBean) {
                like(url, type, mid, homeAttentionBean);
            }

            @Override
            public void cancelFollow(int parentid, List<HomeAttentionBean> item) {
                follow(parentid, item);
            }

        });

        smartRefreshLayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                pageNo = pageNo + 1;
                getData(pageNo,false);
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                pageNo = 1;
                getData(pageNo,true);
            }
        });
    }


    public void getData(int page,boolean ringDown) {
        String url = HttpServicePath.BaseUrl + tag;
        Map<String, String> map = new HashMap<>();
        if (StringUtil.isEmpty(tag)) return;
        map.put("num", 10 + "");
        map.put("page", page + "");
        OkHttp.post(activity, smartRefreshLayout, url, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                if(null !=activity) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            prestrainImg.setVisibility(View.GONE);
                            Gson gson = new Gson();
                            String data = gson.toJson(baseBean.data);
                            List<HomeAttentionBean> homeAttentionList = gson.fromJson(data, new TypeToken<List<HomeAttentionBean>>() {
                            }.getType());
                            if (pageNo == 1) {
                                item.clear();
                                if (null == homeAttentionList || 0 == homeAttentionList.size()) {
                                    emptyLayout.setVisibility(View.VISIBLE);
                                    smartRefreshLayout.setVisibility(View.GONE);
                                }else {
                                    emptyLayout.setVisibility(View.GONE);
                                    smartRefreshLayout.setVisibility(View.VISIBLE);
                                }
                            }
                            if (null != homeAttentionList && 0 != homeAttentionList.size()) {
                                item.addAll(homeAttentionList);
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
            oks.setImageUrl(HttpServicePath.BasePicUrl+"sharelog.png");
        }
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(ShareUrl.getUrl(item.get(position).articleid,item.get(position).type));
        //启动分享
        oks.show(MobSDK.getContext());
        shareRefresh(position);
    }

    public void shareRefresh(int position) {
        if(null != item && 0!=item.size()) {
            item.get(position).share_num = item.get(position).share_num+1;
        }
        adapter.notifyDataSetChanged();
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
                totalShare(item.get(position).type,item.get(position).articleid);
                rw.dismiss();
            }

            @Override
            public void wechatShare(int positon) {
                if(null == item || 0 == item.size()) return;
                showShare(Wechat.NAME,position);
                totalShare(item.get(position).type,item.get(position).articleid);
                rw.dismiss();
            }

            @Override
            public void wechatFriendShare(int positon) {
                if(null == item || 0 == item.size()) return;
                showShare(WechatMoments.NAME,position);
                totalShare(item.get(position).type,item.get(position).articleid);
                rw.dismiss();
            }

            @Override
            public void link(int position) {
                if(null == item || 0 == item.size()) return;
                String link =  ShareUrl.getUrl(item.get(position).articleid,item.get(position).type);
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

    //点赞/取消点赞
    public void like(String url,int type,int mid,HomeAttentionBean homeAttentionBean) {
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
                                homeAttentionBean.is_up = 1;
                                homeAttentionBean.up_num = homeAttentionBean.up_num+1;
                            }else {
                                homeAttentionBean.is_up = 0;
                                homeAttentionBean.up_num = homeAttentionBean.up_num-1;
                            }
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }


    public void follow(int parentid, List<HomeAttentionBean> item) {
        Map<String, String> map = new HashMap<>();
        map.put("parentid", parentid + "");
        map.put("type", 2 + ""); //关注页面只有取消关注
        OkHttp.post(activity, HttpServicePath.URL_ADD_FOLLOW, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                if(null != activity) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pageNo = 1;
                            getData(pageNo,false);
                            EventBus.getDefault().post(new EventTags.Attention(parentid,0));
                        }
                    });
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void attention(EventTags.Attention attention) {
       pageNo = 1;
       getData(pageNo,false);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void toLogin(EventTags.ToLogin toLogin) {
        loginLayout.setVisibility(View.VISIBLE);
        smartRefreshLayout.setVisibility(View.GONE);
        prestrainImg.setVisibility(View.GONE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loginSucceed(EventTags.LoginSucceed loginSucceed) {
        prestrainImg.setVisibility(View.GONE);
        loginLayout.setVisibility(View.GONE);
        smartRefreshLayout.setVisibility(View.VISIBLE);
        pageNo = 1;
        getData(pageNo,false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @OnClick({R.id.loginTv})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.loginTv:
                Intent intent = new Intent(activity, LoginActivity.class);
                startActivity(intent);
                break;
        }
    }

}
