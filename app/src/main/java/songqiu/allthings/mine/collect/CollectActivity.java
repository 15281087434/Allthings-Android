package songqiu.allthings.mine.collect;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.heartfor.heartvideo.video.HeartVideo;
import com.heartfor.heartvideo.video.HeartVideoManager;
import com.heartfor.heartvideo.video.PlayerStatus;
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
import butterknife.OnClick;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import songqiu.allthings.Event.EventTags;
import songqiu.allthings.R;
import songqiu.allthings.adapter.CollectAdapter;
import songqiu.allthings.base.BaseActivity;
import songqiu.allthings.bean.HomeSubitemBean;
import songqiu.allthings.http.BaseBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.http.OkHttp;
import songqiu.allthings.http.RequestCallBack;
import songqiu.allthings.iterface.CollectItemListener;
import songqiu.allthings.iterface.WindowShareListener;
import songqiu.allthings.util.CopyButtonLibrary;
import songqiu.allthings.util.LogUtil;
import songqiu.allthings.util.SharedPreferencedUtils;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.util.ToastUtil;
import songqiu.allthings.util.VibratorUtil;
import songqiu.allthings.util.WindowUtil;
import songqiu.allthings.util.statusbar.StatusBarUtils;
import songqiu.allthings.util.theme.ShareUrl;
import songqiu.allthings.util.theme.ThemeManager;
import songqiu.allthings.view.SharePopupWindows;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/8/20
 *
 *类描述：收藏页面
 *
 ********/
public class CollectActivity extends BaseActivity {

    @BindView(R.id.shadowLayout)
    LinearLayout shadowLayout;
    @BindView(R.id.backImg)
    ImageView backImg;
    @BindView(R.id.titleTv)
    TextView titleTv;
    @BindView(R.id.line)
    LinearLayout line;
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
    int pageNo = 1;
    List<HomeSubitemBean> item;
    CollectAdapter adapter;

    @Override
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_collect);
    }

    @Override
    public void init() {
        boolean dayModel = SharedPreferencedUtils.getBoolean(this,SharedPreferencedUtils.dayModel,true);
        modeUi(dayModel);
        titleTv.setText("收藏");
        initRecycle();
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
                    .setColor(getResources().getColor( R.color.trans_6))
                    .init()
                    .setStatusTextColorAndPaddingTop(true, this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCollect(pageNo,false);
    }

    public void initRecycle() {
        item = new ArrayList<>();
        adapter = new CollectAdapter(this, item);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycle.setLayoutManager(linearLayoutManager);
        recycle.setAdapter(adapter);

        adapter.setCollectItemListener(new CollectItemListener() {
            @Override
            public void addLike(String url, int type, int mid, HomeSubitemBean homeSubitemBean) {
                like(url, type, mid, homeSubitemBean);
            }

            @Override
            public void cancelCollect(int type,int articleid,int position) {
                addCollect(HttpServicePath.URL_NO_COLLECT, type, articleid,position);
            }

            @Override
            public void addShare(int position,int type) {
                showShareWindow(0,position);
            }
        });


        smartRefreshLayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                pageNo = pageNo + 1;
                getCollect(pageNo,false);
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                pageNo = 1;
                getCollect(pageNo,true);
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
        shareRefesh(position);
    }

    public void shareRefesh(int position) {
        if(null != item && 0!=item.size()) {
            item.get(position).share_num = item.get(position).share_num+1;
        }
        adapter.notifyDataSetChanged();
    }
    //分享弹窗
    public void showShareWindow(int type,int position) {
        SharePopupWindows rw = new SharePopupWindows(this, type,position);
        WindowUtil.windowDeploy(this, rw, line);
        rw.setWindowShareListener(new WindowShareListener() {
            @Override
            public void qqShare(int position) {
                if(null == item || 0 == item.size()) return;
                showShare(QQ.NAME,position);
                totalShare(item.get(position).type,item.get(position).articleid);
                rw.dismiss();
            }

            @Override
            public void wechatShare(int position) {
                if(null == item || 0 == item.size()) return;
                showShare(Wechat.NAME,position);
                totalShare(item.get(position).type,item.get(position).articleid);
                rw.dismiss();
            }

            @Override
            public void wechatFriendShare(int position) {
                if(null == item || 0 == item.size()) return;
                showShare(WechatMoments.NAME,position);
                totalShare(item.get(position).type,item.get(position).articleid);
                rw.dismiss();
            }

            @Override
            public void link(int position) {
                if(null == item || 0 == item.size()) return;
                String link =  ShareUrl.getUrl(item.get(position).articleid,item.get(position).type);
                CopyButtonLibrary copyButtonLibrary = new CopyButtonLibrary(CollectActivity.this,link);
                copyButtonLibrary.init(link);
                ToastUtil.showToast(CollectActivity.this,"复制成功!");
            }

            @Override
            public void report() {
            }

            @Override
            public void daytime() {
            }

            @Override
            public void night() {
//                boolean dayModel = SharedPreferencedUtils.getBoolean(CollectActivity.this,SharedPreferencedUtils.dayModel,true);
//                if(dayModel) {
//                    SharedPreferencedUtils.setBoolean(CollectActivity.this,SharedPreferencedUtils.dayModel,false);
//                    EventBus.getDefault().post(new EventTags.DayMoulde(false));
//                }else {
//                    SharedPreferencedUtils.setBoolean(CollectActivity.this,SharedPreferencedUtils.dayModel,true);
//                    EventBus.getDefault().post(new EventTags.DayMoulde(true));
//                }
            }
        });
    }

    public void addCollect(String url, int type, int mid,int position) { //收藏/取消收藏
        Map<String, String> map = new HashMap<>();
        map.put("type", type + "");
        map.put("mid", mid + "");
        OkHttp.post(this, url, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(item.size()>position) {
                            item.remove(position);
                            adapter.notifyDataSetChanged();
                        }
                        if(null == item || 0==item.size()) {
                            emptyLayout.setVisibility(View.VISIBLE);
                            smartRefreshLayout.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
    }

    //点赞/取消点赞
    public void like(String url, int type, int mid, HomeSubitemBean homeSubitemBean) {
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
                            homeSubitemBean.is_up = 1;
                            homeSubitemBean.up_num = homeSubitemBean.up_num + 1;
                        } else {
                            homeSubitemBean.is_up = 0;
                            homeSubitemBean.up_num = homeSubitemBean.up_num - 1;
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    public void getCollect(int pageNo,boolean ringDown) {
        Map<String, String> map = new HashMap<>();
        map.put("num", 10 + "");
        map.put("page", pageNo + "");
        OkHttp.post(this, smartRefreshLayout, HttpServicePath.URL_COLLECT_LIST, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        String data = gson.toJson(baseBean.data);
                        List<HomeSubitemBean> homeAttentionList = gson.fromJson(data, new TypeToken<List<HomeSubitemBean>>() {
                        }.getType());
                        if (pageNo == 1) {
                            item.clear();
                            if (null == homeAttentionList || 0 == homeAttentionList.size()) {
                                emptyLayout.setVisibility(View.VISIBLE);
                                smartRefreshLayout.setVisibility(View.GONE);
                            } else {
                                emptyLayout.setVisibility(View.GONE);
                                smartRefreshLayout.setVisibility(View.VISIBLE);
                            }
                        }
                        if (null != homeAttentionList && 0 != homeAttentionList.size()) {
                            item.addAll(homeAttentionList);
                            adapter.notifyDataSetChanged();
                        }
                        if(ringDown) {
                            VibratorUtil.ringDown(CollectActivity.this);
                        }
                    }
                });
            }
        });

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

    @Override
    protected void onPause() {
        super.onPause();
        HeartVideoManager.getInstance().pause();
    }

    @OnClick({R.id.backImg})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.backImg:
                finish();
                break;
        }
    }

}
