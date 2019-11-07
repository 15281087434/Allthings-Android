package songqiu.allthings.mine.userpage;

import android.content.Context;
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

import java.util.ArrayList;
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
import songqiu.allthings.adapter.LookTabClassAdapter;
import songqiu.allthings.adapter.UserImageTextAdapter;
import songqiu.allthings.adapter.UserVideoAdapter;
import songqiu.allthings.base.BaseFragment;
import songqiu.allthings.bean.UserPagerBean;
import songqiu.allthings.http.BaseBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.http.OkHttp;
import songqiu.allthings.http.RequestCallBack;
import songqiu.allthings.iterface.UserPagerListenner;
import songqiu.allthings.iterface.WindowShareListener;
import songqiu.allthings.util.CopyButtonLibrary;
import songqiu.allthings.util.LogUtil;
import songqiu.allthings.util.SharedPreferencedUtils;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.util.ToastUtil;
import songqiu.allthings.util.VibratorUtil;
import songqiu.allthings.util.WindowUtil;
import songqiu.allthings.util.theme.ShareUrl;
import songqiu.allthings.view.SharePopupWindows;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/9/17
 *
 *类描述：视频
 *
 ********/
public class UserVideoFragment extends BaseFragment {

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
    int page = 1;
    List<UserPagerBean> item;
    UserVideoAdapter adapter;

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
        initRecycle();
        getData(page,false);
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
        adapter = new UserVideoAdapter(activity,item);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycle.setLayoutManager(linearLayoutManager);
        recycle.setAdapter(adapter);

        adapter.setUserPagerListenner(new UserPagerListenner() {
            @Override
            public void addLike(String url, int type, int mid, UserPagerBean userPagerBean) {
                like(url, type, mid, userPagerBean);
            }

            @Override
            public void share(int type,int position,int shareType) {
                showShareWindow(0,position);
            }

            @Override
            public void delete() {

            }
        });

        smartRefreshLayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                page = page+1;
                getData(page,false);
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                page = 1;
                getData(page,true);
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
            oks.setImageUrl(HttpServicePath.BasePicUrl+"sharelog.png");
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

    public void getData(int page,boolean ringDown) {
        Map<String,String> map = new HashMap<>();
        map.put("type",2+"");
        map.put("userid",userId+"");
        map.put("num",10+"");
        map.put("page",page+"");
        OkHttp.post(activity, smartRefreshLayout, HttpServicePath.URL_CENTER, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                if(null != activity) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Gson gson = new Gson();
                            String data = gson.toJson(baseBean.data);
                            List<UserPagerBean> userPagerList = gson.fromJson(data, new TypeToken<List<UserPagerBean>>() {}.getType());
                            if(page ==1) {
                                item.clear();
                                if(null == userPagerList || 0 == userPagerList.size()) {
                                    emptyLayout.setVisibility(View.VISIBLE);
                                    recycle.setVisibility(View.GONE);
                                }else {
                                    emptyLayout.setVisibility(View.GONE);
                                    recycle.setVisibility(View.VISIBLE);
                                }
                            }
                            if(null != userPagerList && 0!=userPagerList.size()) {
                                item.addAll(userPagerList);
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
    public void like(String url,int type,int mid,UserPagerBean userPagerBean) {
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
                                userPagerBean.is_up = 1;
                                userPagerBean.up_num = userPagerBean.up_num+1;
                            }else {
                                userPagerBean.is_up = 0;
                                userPagerBean.up_num = userPagerBean.up_num-1;
                            }
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }

}
