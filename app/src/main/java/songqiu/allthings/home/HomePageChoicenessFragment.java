package songqiu.allthings.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import com.heartfor.heartvideo.video.HeartVideoManager;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import songqiu.allthings.Event.EventTags;
import songqiu.allthings.R;
import songqiu.allthings.activity.MainActivity;
import songqiu.allthings.adapter.BannerLooperAdapter;
import songqiu.allthings.adapter.HeaderViewAdapter;
import songqiu.allthings.adapter.HomePageChooseAdapter;
import songqiu.allthings.adapter.HomeTabClassAdapter;
import songqiu.allthings.articledetail.ArticleDetailActivity;
import songqiu.allthings.base.BaseFragment;
import songqiu.allthings.bean.BannerBean;
import songqiu.allthings.bean.HomeSubitemBean;
import songqiu.allthings.http.BaseBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.http.OkHttp;
import songqiu.allthings.http.RequestCallBack;
import songqiu.allthings.login.LoginActivity;
import songqiu.allthings.mine.attention.AttentionActivity;
import songqiu.allthings.util.CheckLogin;
import songqiu.allthings.util.DateUtil;
import songqiu.allthings.util.LogUtil;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.util.VibratorUtil;
import songqiu.allthings.view.banner.ColorPointHintView;
import songqiu.allthings.view.banner.RollPagerView;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/9/4
 *
 *类描述：精选
 *
 ********/
public class HomePageChoicenessFragment extends BaseFragment {
    @BindView(R.id.recycle)
    RecyclerView recycle;
    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout smartRefreshLayout;
    @BindView(R.id.prestrainImg)
    ImageView prestrainImg;

    List<HomeSubitemBean> item;
    HomePageChooseAdapter adapter;
    HeaderViewAdapter mHeaderAdapter;
    View mHeadView;
    View mFooterView;
    RollPagerView rollPageHome;
    TextView loginTv;
    TextView hintTv;


    List<BannerBean> mBannerList;
    BannerLooperAdapter mBannerAdapter;
    public String tag;
    int pageNo = 1;
    String strTime;

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
        return R.layout.fragment_home_page_choose;
    }

    @Override
    public void init() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        initRecyclerView();
        getData(pageNo,false);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        visible = isVisibleToUser;
    }

    public void initBanner(RollPagerView rollPageHome) {
        mBannerList = new ArrayList<>();
        mBannerAdapter = new BannerLooperAdapter(rollPageHome, (ArrayList<BannerBean>) mBannerList);
        rollPageHome.setAdapter(mBannerAdapter);
        rollPageHome.setHintView(new ColorPointHintView(activity, Color.WHITE, Color.GRAY));
        rollPageHome.setHintPadding(0, 0, 0, 10);
        rollPageHome.resume();
        getBanner();
    }

    public void initRecyclerView() {
        item = new ArrayList<>();
        mHeadView = LayoutInflater.from(activity).inflate(R.layout.fragment_home_pager_choose_head, null, false);
        mFooterView = LayoutInflater.from(activity).inflate(R.layout.fragment_home_pager_choose_footer, null, false);
        rollPageHome = mHeadView.findViewById(R.id.roll_page_home);
        loginTv = mFooterView.findViewById(R.id.loginTv);
        hintTv = mFooterView.findViewById(R.id.hintTv);
        initBanner(rollPageHome);
        initBannerEvent();
        adapter = new HomePageChooseAdapter(activity,item);
        mHeaderAdapter = new HeaderViewAdapter(adapter);
        mHeaderAdapter.addHeaderView(mHeadView);
        mHeaderAdapter.addFooterView(mFooterView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycle.setLayoutManager(linearLayoutManager);
        recycle.setAdapter(mHeaderAdapter);

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

        loginTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(new Intent(activity, LoginActivity.class));
            }
        });
    }

    public void getBanner() {
        Map<String, String> map = new HashMap<>();
        map.put("type",1+""); //1、文章  2、话题
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

    public void initBannerEvent() {
        rollPageHome.setOnItemClickListener(position -> {
            if (mBannerList == null || position >= mBannerList.size()) {
                return;
            }
            Intent intent = new Intent(activity, ArticleDetailActivity.class);
            intent.putExtra("articleid", mBannerList.get(position).url_id);
            startActivity(intent);
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void toLogin(EventTags.ToLogin toLogin) {
        pageNo = 1;
        getData(pageNo,false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loginSucceed(EventTags.LoginSucceed loginSucceed) {
        pageNo = 1;
        getData(pageNo,false);
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
        String url = HttpServicePath.BaseUrl+ tag;
        Map<String,Object> map = new HashMap<>();
        if(StringUtil.isEmpty(tag)) return;
        map.put("num",10);
        map.put("page",page);
        map.put("type",2);
        OkHttp.post(activity, smartRefreshLayout,url, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                if(null != activity) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            prestrainImg.setVisibility(View.GONE);
                            Gson gson = new Gson();
                            String data = gson.toJson(baseBean.data);
                            List<HomeSubitemBean> classifyBeanList = gson.fromJson(data, new TypeToken<List<HomeSubitemBean>>() {}.getType());
                            if(pageNo ==1) {
                                item.clear();
                                if(null == classifyBeanList || 0 == classifyBeanList.size()) {
                                    HomeSubitemBean homeSubitemBean = new HomeSubitemBean();
                                    homeSubitemBean.empty = true;
                                    item.add(homeSubitemBean);
                                    adapter.notifyDataSetChanged();
                                }

                                if(!CheckLogin.isLogin(activity) && null != classifyBeanList && 10>classifyBeanList.size()) {
                                    loginTv.setVisibility(View.VISIBLE);
                                    hintTv.setVisibility(View.VISIBLE);
                                }else {
                                    loginTv.setVisibility(View.GONE);
                                    hintTv.setVisibility(View.GONE);
                                }
                            }
                            if(null != classifyBeanList && 0!=classifyBeanList.size()) {
                                if(pageNo ==1) {
                                    HomeSubitemBean homeSubitemBean = new HomeSubitemBean();
                                    homeSubitemBean.dayTiem = DateUtil.getTimeBig1(classifyBeanList.get(0).created*1000);
                                    strTime = DateUtil.getTimeBig1(classifyBeanList.get(0).created*1000);
                                    item.add(homeSubitemBean);
                                }

                                for(int i = 0;i<classifyBeanList.size();i++) {
                                    String time = DateUtil.getTimeBig1(classifyBeanList.get(i).created*1000);
                                    if(!StringUtil.isEmpty(strTime) && strTime.equals(time)) { //时间相同
                                        item.add(classifyBeanList.get(i));
                                    }else {//时间不同
                                        HomeSubitemBean homeSubitemBean1 = new HomeSubitemBean();
                                        homeSubitemBean1.dayTiem = DateUtil.getTimeBig1(classifyBeanList.get(i).created*1000);
                                        item.add(homeSubitemBean1);
                                        strTime = DateUtil.getTimeBig1(classifyBeanList.get(i).created*1000);
                                        item.add(classifyBeanList.get(i));
                                    }
                                }
                                adapter.notifyDataSetChanged();
                            }

                            if(!CheckLogin.isLogin(activity) && null != classifyBeanList && 0==classifyBeanList.size() && pageNo !=1) {
                                loginTv.setVisibility(View.VISIBLE);
                                hintTv.setVisibility(View.VISIBLE);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
