package songqiu.allthings.home;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextPaint;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import songqiu.allthings.Event.EventTags;
import songqiu.allthings.R;
import songqiu.allthings.activity.CommentWebViewActivity;
import songqiu.allthings.activity.MainActivity;
import songqiu.allthings.adapter.HomeTableViewAdapter;
import songqiu.allthings.base.BaseFragment;
import songqiu.allthings.bean.ChangePage;
import songqiu.allthings.bean.NewRedStateBean;
import songqiu.allthings.bean.TabClassBean;
import songqiu.allthings.bean.UserCenterBean;
import songqiu.allthings.constant.SnsConstants;
import songqiu.allthings.home.gambit.HomePageGambitFragment;
import songqiu.allthings.http.BaseBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.http.OkHttp;
import songqiu.allthings.http.RequestCallBack;
import songqiu.allthings.iterface.DialogNewRedListener;
import songqiu.allthings.iterface.DialogPrivacyListener;
import songqiu.allthings.iterface.TaskDialogSignListener;
import songqiu.allthings.search.SearchActivity;
import songqiu.allthings.util.ClickUtil;
import songqiu.allthings.util.LogUtil;
import songqiu.allthings.util.NetWorkUtil;
import songqiu.allthings.util.SharedPreferencedUtils;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.util.ToastUtil;
import songqiu.allthings.view.DialogNewRedEnvelope;
import songqiu.allthings.view.DialogPrivacyExplain;
import songqiu.allthings.view.DialogSign;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/8/6
 *
 *类描述：
 *
 ********/
public class HomePageFragment extends BaseFragment {

    @BindView(R.id.layout)
    LinearLayout layout;
    @BindView(R.id.line)
    View line;
    @BindView(R.id.headLayout)
    LinearLayout headLayout;
    @BindView(R.id.magicIndicator)
    MagicIndicator magicIndicator;
    @BindView(R.id.vp_home)
    ViewPager viewPager;

    @BindView(R.id.iv_icon)
    ImageView ivIcon;
    @BindView(R.id.tv_message)
    TextView tvMessage;
    @BindView(R.id.tryNetworkLayout)
    LinearLayout tryNetworkLayout;
    @BindView(R.id.emptyLayout)
    LinearLayout emptyLayout;

    List<TabClassBean> list;
    HomeTableViewAdapter viewAdapter;
    CommonNavigator commonNavigator;

    List<Fragment> mFragments = new ArrayList<>();

    //新手红包
    DialogNewRedEnvelope dialog;

    MainActivity activity;
    DialogPrivacyExplain dialogPrivacyExplain;
    public int indexPosition = 1;
    SimplePagerTitleView simplePagerTitleView;
    boolean isGhost;

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
        return R.layout.fragment_home_page;
    }

    @Override
    public void init() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        getTabData();
        getNewRedSate();
        decideFirst();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden) { //显示
        }else {
            EventBus.getDefault().post(new EventTags.ColseLookVideo());
        }
    }


    public void getNewRedSate() {
        Map<String, String> map = new HashMap<>();
        OkHttp.post(activity, HttpServicePath.URL_NEW_RED_STATE, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                if(null != activity) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Gson gson = new Gson();
                            String data = gson.toJson(baseBean.data);
                            if (StringUtil.isEmpty(data)) return;
                            NewRedStateBean newRedStateBean = gson.fromJson(data, NewRedStateBean.class);
                            if(null != newRedStateBean && newRedStateBean.is_red == 0) {
                                initDialog();
                                decideFirst();
                            }
                        }
                    });
                }
            }
        });
    }

    public void reviceNewRed() {
        Map<String, String> map = new HashMap<>();
        OkHttp.post(activity, HttpServicePath.URL_NEW_RED_RECEIVE, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                if(null != activity) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(null != dialog) {
                                ToastUtil.showToast(activity,baseBean.msg);
                                dialog.dismiss();
                            }
                        }
                    });
                }
            }
        });
    }

    public void getTabData() {
        if (!NetWorkUtil.isNetworkConnected(activity)) {
            layout.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("zone", 1+"");
        OkHttp.post(activity, HttpServicePath.URL_NAVIGATION, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                if(null != activity) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            layout.setVisibility(View.VISIBLE);
                            emptyLayout.setVisibility(View.GONE);
                            Gson gson = new Gson();
                            String data = gson.toJson(baseBean.data);
                            list = gson.fromJson(data, new TypeToken<List<TabClassBean>>() {
                            }.getType());
                            if (null == list || 0 == list.size()) return;
                            String locationCity = SharedPreferencedUtils.getString(activity, "LOCATION_CITY");
                            for (int i = 0; i < list.size(); i++) {
                                if (list.get(i).name.equals("本地")) {
                                    list.get(i).name = locationCity;
                                }
                            }
                            initTableLayout();
                            if (null != list && 0 != list.size()) {
                                for (int i = 0; i < list.size(); i++) {
                                    if ("recommend".equals(list.get(i).tag)) {
                                        viewPager.setCurrentItem(i);
                                    }
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    private void initTableLayout() {
        mFragments.clear();
        if (null == list || 0 == list.size()) return;
        for (int i = 0; i < list.size(); i++) {
             if (list.get(i).tag.equals("follow")) { //关注
                HomePageAttentionFragment homePageAttentionFragment = new HomePageAttentionFragment();
                mFragments.add(i, homePageAttentionFragment);
                homePageAttentionFragment.tag = list.get(i).tag;
            } else if (list.get(i).tag.equals("location")) {
                HomePageCityFragment homePageCityFragment = new HomePageCityFragment();
                mFragments.add(i, homePageCityFragment);
                homePageCityFragment.tag = list.get(i).tag;
            } else if (list.get(i).tag.equals("seleted")) { //精选
                HomePageChoicenessFragment homePageChoicenessFragment = new HomePageChoicenessFragment();
                mFragments.add(i, homePageChoicenessFragment);
                homePageChoicenessFragment.tag = list.get(i).tag;
            } else if (list.get(i).tag.equals("ghost")) { //鬼话
                 HomePageGhostFragment homePageGhostFragment = new HomePageGhostFragment();
                 mFragments.add(i, homePageGhostFragment);
                 homePageGhostFragment.tag = list.get(i).tag;
             }else if (list.get(i).tag.equals("topic")) { //话题 标签
                 HomePageGambitFragment homePageGambitFragment = new HomePageGambitFragment();
                 mFragments.add(i, homePageGambitFragment);
             } else {
                HomePageSubitemFragment homePageSubitemFragment = new HomePageSubitemFragment();
                mFragments.add(homePageSubitemFragment);
                homePageSubitemFragment.name = list.get(i).name;
                homePageSubitemFragment.tag = list.get(i).tag;
                homePageSubitemFragment.category = list.get(i).category;
                homePageSubitemFragment.type = list.get(i).type;
            }
        }
        viewAdapter = new HomeTableViewAdapter(getChildFragmentManager(), mFragments, list);
        viewPager.setAdapter(viewAdapter);
        viewPager.setOffscreenPageLimit(0);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                LogUtil.i("*********onPageScrolled:"+position);
                magicIndicator.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                magicIndicator.onPageSelected(position);
                if (null == list || 0 == list.size()) return;
                if(list.get(position).tag.equals("ghost")) {
                    isGhost = true;
                    headLayout.setBackgroundResource(R.mipmap.home_tab_ghost_bg);
                    line.setBackgroundResource(R.color.FF0F1012);
                    EventBus.getDefault().post(new EventTags.Ghost(true));
                }else {
                    isGhost = false;
                    headLayout.setBackgroundResource(R.color.FFF9FAFD);
                    line.setBackgroundResource(R.color.line_color);
                    EventBus.getDefault().post(new EventTags.Ghost(false));
                }
                commonNavigator.notifyDataSetChanged();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
//                LogUtil.i("*********onPageScrollStateChanged:"+state);
                magicIndicator.onPageScrollStateChanged(state);
            }
        });
        //新建导航栏
        commonNavigator = new CommonNavigator(activity);
        commonNavigator.setEnablePivotScroll(true);
//        commonNavigator.setRightPadding(100);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return list == null ? 0 : list.size();
            }
            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                //设置Magicindicator的一种标题模式， 标题模式有很多种，这是最基本的一种
                simplePagerTitleView = new SimplePagerTitleView(context);
                simplePagerTitleView.setText(list.get(index).name);
                //设置被选中的item颜色
                if(isGhost) {
                    simplePagerTitleView.setSelectedColor(getResources().getColor(R.color.normal_color));
                }else {
                    simplePagerTitleView.setSelectedColor(getResources().getColor(R.color.black));
                }
                //设置为被选中item颜色
                simplePagerTitleView.setNormalColor(getResources().getColor(R.color.FFA2A2A2));
                simplePagerTitleView.setSelectedSize(19);
                simplePagerTitleView.setDeselectedSize(18);
                TextPaint tp = simplePagerTitleView.getPaint();
                tp.setFakeBoldText(true);

                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewPager.setCurrentItem(index);
                        if(indexPosition == index) { //点同一列表项则发送更新通知
                            EventBus.getDefault().post(new EventTags.HomeRefresh());
                        }else { //
                            indexPosition = index;
                        }
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                //设置标题指示器，也有多种,可不选，即没有标题指示器。
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setColors(getResources().getColor(R.color.normal_color));
                indicator.setYOffset(13);
                indicator.setXOffset(47);
                return indicator;
            }

        });

        //绑定导航栏
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, viewPager);
    }


    public void initDialog() {
        dialog = new DialogNewRedEnvelope(activity);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        dialog.setDialogNewRedListener(new DialogNewRedListener() {
            @Override
            public void toReceive() {
                //去领取新人红包
                reviceNewRed();
            }
        });
    }

    public void decideFirst() {
        //判断是否第一次进入应用
        boolean first = SharedPreferencedUtils.getBoolean(activity,SharedPreferencedUtils.FIRST_ENTER,true);
        if(first) {
            if(null == dialogPrivacyExplain) {
                dialogPrivacyExplain = new DialogPrivacyExplain(activity);
                dialogPrivacyExplain.setCanceledOnTouchOutside(false);
                dialogPrivacyExplain.setCancelable(false);
                dialogPrivacyExplain.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialogPrivacyExplain.show();
                dialogPrivacyExplain.setDialogPrivacyListener(new DialogPrivacyListener() {
                    @Override
                    public void cancel() {
                        activity.finish();
                    }

                    @Override
                    public void sure() {
                        SharedPreferencedUtils.setBoolean(activity,SharedPreferencedUtils.FIRST_ENTER,false);
                    }
                });
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void transmitCity(EventTags.TransmitCity transmitCity) {
        String city = transmitCity.getCity();
        if (null == list || 0 == list.size()) return;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).tag.equals("location")) {
                if (list.get(i).name.equals(city)) {
                    return;
                }else {
                    list.get(i).name = city;
                }
            }
        }
        commonNavigator.getAdapter().notifyDataSetChanged();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void noNetwork(EventTags.HomeNoNetwork noNetwork) {
        layout.setVisibility(View.GONE);
        emptyLayout.setVisibility(View.VISIBLE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void changePageShow(ChangePage page) {
        if(page.type.equals("home_recommend")) {
            if (null != list && 0 != list.size()) {
                for (int i = 0; i < list.size(); i++) {
                    if ("recommend".equals(list.get(i).tag)) {
                        viewPager.setCurrentItem(i);
                    }
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    @OnClick({R.id.searchImg,R.id.classification})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.searchImg:
                if (ClickUtil.onClick()) {
                    Intent intent = new Intent(activity, SearchActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.classification:
                if (ClickUtil.onClick()) {

                }
                break;
        }
    }

    @OnClick(R.id.emptyLayout)
    public void tryNetwork() {
        getTabData();
    }

}
