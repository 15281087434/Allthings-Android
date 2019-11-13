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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
import songqiu.allthings.Event.EventTags;
import songqiu.allthings.R;
import songqiu.allthings.activity.MainActivity;
import songqiu.allthings.adapter.BannerLooperAdapter;
import songqiu.allthings.adapter.GambitCommonAdapter;
import songqiu.allthings.adapter.HeaderViewAdapter;
import songqiu.allthings.adapter.HomeHotGambitAdapter;
import songqiu.allthings.adapter.TaskSignAdapter;
import songqiu.allthings.articledetail.ArticleDetailActivity;
import songqiu.allthings.base.BaseFragment;
import songqiu.allthings.bean.BannerBean;
import songqiu.allthings.bean.HomeGambitHotBean;
import songqiu.allthings.bean.HomeSubitemBean;
import songqiu.allthings.bean.HotGambitCommonBean;
import songqiu.allthings.bean.ReportBean;
import songqiu.allthings.bean.TaskSiginListBean;
import songqiu.allthings.http.BaseBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.http.OkHttp;
import songqiu.allthings.http.RequestCallBack;
import songqiu.allthings.iterface.GambitItemListener;
import songqiu.allthings.iterface.HomeHotGambitListener;
import songqiu.allthings.iterface.PhotoViewListener;
import songqiu.allthings.iterface.TaskSignListener;
import songqiu.allthings.login.LoginActivity;
import songqiu.allthings.photoview.PhotoViewActivity;
import songqiu.allthings.util.CheckLogin;
import songqiu.allthings.util.ClickUtil;
import songqiu.allthings.util.LogUtil;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.util.TokenManager;
import songqiu.allthings.util.WindowUtil;
import songqiu.allthings.view.ReportPopupWindows;
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

    @BindView(R.id.reyclerView)
    RecyclerView reyclerView;

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
        initRecyclerView();
        getHotData();
        getFriendsData();
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
            public void addFollow(String url, int id, List<HomeGambitHotBean> item) {
                follow(url,id,item);
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
                    getFriendsData();
                }
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
                    showReportWindow(talk_id,3);
                }
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
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void toGambitRefresh(EventTags.GambitRefresh gambitRefresh) {
        getHotData();
        getFriendsData();
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

    public void getFriendsData() {
        Map<String, String> map = new HashMap<>();
        OkHttp.post(activity, HttpServicePath.URL_FRIENDS, map, new RequestCallBack() {
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
                            if(null != homeGambitHotList && 0!=homeGambitHotList.size()) {
                                newList.clear();
                                newList.addAll(homeGambitHotList);
                                newGambitAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                }
            }
        });
    }

    public void follow(String url,int talk_id,List<HomeGambitHotBean> item) {
        Map<String, String> map = new HashMap<>();
        map.put("talk_id", talk_id + "");
        OkHttp.post(activity, url, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                if(null != activity) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(url.contains("follow_talk_no")) { //取消关注
                                for(int i = 0;i<item.size();i++) {
                                    if(talk_id == item.get(i).id) {
                                        item.get(i).is_follow = 0;
                                    }
                                }
                            }else {
                                for(int i = 0;i<item.size();i++) {
                                    if(talk_id == item.get(i).id) {
                                        item.get(i).is_follow = 1;
                                    }
                                }
                            }
                            hotAdapter.notifyDataSetChanged();
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
                                for(int i = 0;i<newList.size();i++) {
                                    if(mid == newList.get(i).id) {
                                        newList.get(i).is_up = 1;
                                        newList.get(i).up_num = newList.get(i).up_num+1;
                                        newGambitAdapter.notifyDataSetChanged();
                                        LogUtil.i("1");
                                    }
                                }
                            }else {
                                for(int i = 0;i<newList.size();i++) {
                                    if(mid == newList.get(i).id) {
                                        newList.get(i).is_up = 0;
                                        newList.get(i).up_num = newList.get(i).up_num-1;
                                        newGambitAdapter.notifyDataSetChanged();
                                        LogUtil.i("2");
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
                                        hotAdapter.notifyDataSetChanged();
                                    }
                                }

                                for(int i = 0;i<newList.size();i++) {
                                    if(parentid == newList.get(i).userid) {
                                        newList.get(i).is_follow = 1;
                                        newGambitAdapter.notifyDataSetChanged();
                                    }
                                }
                            }else {
                                for(int i = 0;i<list.size();i++) {
                                    if(parentid == list.get(i).userid) {
                                        list.get(i).is_follow = 0;
                                        hotAdapter.notifyDataSetChanged();
                                    }
                                }

                                for(int i = 0;i<newList.size();i++) {
                                    if(parentid == newList.get(i).userid) {
                                        newList.get(i).is_follow = 0;
                                        newGambitAdapter.notifyDataSetChanged();
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
//                    String[] urls = {
//                            "http://a.hiphotos.baidu.com/image/pic/item/00e93901213fb80e3b0a611d3fd12f2eb8389424.jpg",
//                            "http://b.hiphotos.baidu.com/image/pic/item/5243fbf2b2119313999ff97a6c380cd790238d1f.jpg",
//                            "http://f.hiphotos.baidu.com/image/pic/item/43a7d933c895d1430055e4e97af082025baf07dc.jpg",
//                            "http://a.hiphotos.baidu.com/image/pic/item/00e93901213fb80e3b0a611d3fd12f2eb8389424.jpg",
//                            "http://b.hiphotos.baidu.com/image/pic/item/5243fbf2b2119313999ff97a6c380cd790238d1f.jpg",
//                            "http://f.hiphotos.baidu.com/image/pic/item/43a7d933c895d1430055e4e97af082025baf07dc.jpg"
//                    };
//                    Intent intent = new Intent(activity, PhotoViewActivity.class);
//                    intent.putExtra("photoArray",urls);
//                    startActivity(intent);
//                    activity.overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
                }
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
