package songqiu.allthings.look;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import songqiu.allthings.Event.EventTags;
import songqiu.allthings.R;
import songqiu.allthings.activity.MainActivity;
import songqiu.allthings.adapter.LookTableViewAdapter;
import songqiu.allthings.base.BaseFragment;
import songqiu.allthings.bean.ChangePage;
import songqiu.allthings.bean.ReadAwardBean;
import songqiu.allthings.bean.TabClassBean;
import songqiu.allthings.classification.AllClassificationActivity;
import songqiu.allthings.http.BaseBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.http.OkHttp;
import songqiu.allthings.http.RequestCallBack;
import songqiu.allthings.search.SearchActivity;
import songqiu.allthings.util.CheckLogin;
import songqiu.allthings.util.ClickUtil;
import songqiu.allthings.util.LogUtil;
import songqiu.allthings.util.NetWorkUtil;
import songqiu.allthings.util.SharedPreferencedUtils;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.videodetail.VideoDetailActivity;
import songqiu.allthings.view.CustomCircleProgress;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/8/6
 *
 *类描述：
 *
 ********/
public class LookPageFragment extends BaseFragment {
    @BindView(R.id.magicIndicator)
    MagicIndicator magicIndicator;
    @BindView(R.id.vp_home)
    ViewPager viewPager;

    @BindView(R.id.layout)
    LinearLayout layout;

    @BindView(R.id.iv_icon)
    ImageView ivIcon;
    @BindView(R.id.tv_message)
    TextView tvMessage;
    @BindView(R.id.tryNetworkLayout)
    LinearLayout tryNetworkLayout;
    @BindView(R.id.emptyLayout)
    LinearLayout emptyLayout;

    List<TabClassBean> list;

    List<Fragment> mFragments = new ArrayList<>();

    MainActivity activity;
    public int indexPosition = 0;

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
        return R.layout.fragment_look_page;
    }

    @Override
    public void init() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        getTabData();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden) { //显示
        }else {
            EventBus.getDefault().post(new EventTags.ColseLookVideo());
        }
    }

    public void getTabData() {
        if (!NetWorkUtil.isNetworkConnected(activity)) {
            layout.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("zone", 2 + "");
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
//                      SystemMessageAllBean systemMessageAllBean = gson.fromJson(data, SystemMessageAllBean.class);
                            list = gson.fromJson(data, new TypeToken<List<TabClassBean>>() {
                            }.getType());
                            if (null == list || 0 == list.size()) return;
                            initTableLayout();
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
            LookPageSubitemFragment lookPageSubitemFragment = new LookPageSubitemFragment();
            mFragments.add(lookPageSubitemFragment);
            lookPageSubitemFragment.name = list.get(i).name;
            lookPageSubitemFragment.tag = list.get(i).tag;
            lookPageSubitemFragment.type = list.get(i).type;
            lookPageSubitemFragment.category = list.get(i).category;
        }
        LookTableViewAdapter viewAdapter = new LookTableViewAdapter(getChildFragmentManager(), mFragments, list);
        viewPager.setAdapter(viewAdapter);
        viewPager.setOffscreenPageLimit(0);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                magicIndicator.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {

                magicIndicator.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                magicIndicator.onPageScrollStateChanged(state);
            }
        });
        if (null != list && 0 != list.size()) {
            for (int i = 0; i < list.size(); i++) {
                if ("recommend".equals(list.get(i).tag)) {
                    viewPager.setCurrentItem(i);
                }
            }
        }
        magicIndicator.setBackgroundColor(getResources().getColor(R.color.FFF9FAFD));
        //新建导航栏
        CommonNavigator commonNavigator = new CommonNavigator(activity);
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
                SimplePagerTitleView simplePagerTitleView = new SimplePagerTitleView(context);
                simplePagerTitleView.setText(list.get(index).name);
                //设置被选中的item颜色
                simplePagerTitleView.setSelectedColor(getResources().getColor(R.color.black));
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


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void noNetwork(EventTags.LookNoNetwork noNetwork) {
        layout.setVisibility(View.GONE);
        emptyLayout.setVisibility(View.VISIBLE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void changePageShow(ChangePage page) {
        if(page.type.equals("look_recommend")) {
            viewPager.setCurrentItem(0);
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
                   Intent intent = new Intent(activity, AllClassificationActivity.class);
                   startActivity(intent);
               }
               break;
       }
    }

    @OnClick(R.id.emptyLayout)
    public void tryNetwork() {
        getTabData();
    }

}
