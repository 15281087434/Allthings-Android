package songqiu.allthings.mine.inform;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextPaint;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.badge.BadgeAnchor;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.badge.BadgePagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.badge.BadgeRule;

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
import songqiu.allthings.Event.EventTags;
import songqiu.allthings.R;
import songqiu.allthings.adapter.TablayoutViewAdapter;
import songqiu.allthings.base.BaseActivity;
import songqiu.allthings.bean.CommentBean;
import songqiu.allthings.http.BaseBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.http.OkHttp;
import songqiu.allthings.http.RequestCallBack;
import songqiu.allthings.mine.attention.AttentionGambitFragment;
import songqiu.allthings.mine.attention.AttentionUserFragment;
import songqiu.allthings.util.LogUtil;
import songqiu.allthings.util.SharedPreferencedUtils;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.util.statusbar.StatusBarUtils;
import songqiu.allthings.util.theme.ThemeManager;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/8/20
 *
 *类描述：通知页面
 *
 ********/
public class InformActivity extends BaseActivity {

    @BindView(R.id.backImg)
    ImageView backImg;
    @BindView(R.id.titleTv)
    TextView titleTv;
    @BindView(R.id.rightTv)
    TextView rightTv;
    @BindView(R.id.line)
    LinearLayout line;

    @BindView(R.id.magicIndicator)
    MagicIndicator magicIndicator;
    @BindView(R.id.vp_home)
    ViewPager viewPager;

    @BindView(R.id.shadowLayout)
    LinearLayout shadowLayout;
//    InteractionAdapter adapter;

    List<Fragment> mFragments = new ArrayList<>();
    InteractionFragment interactionFragment;
    CommentFragment commentFragment;
    SystemFragment systemFragment;

    int lastPosition; //记录上一个位置
    boolean firstShow;
    boolean twoShow;
    boolean threeShow;
    ImageView imageView;
    CommonNavigator commonNavigator;

    @Override
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_inform);
    }

    @Override
    public void init() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        boolean dayModel = SharedPreferencedUtils.getBoolean(this,SharedPreferencedUtils.dayModel,true);
        modeUi(dayModel);
        titleTv.setText("消息");
        mFragments.clear();
        initTableLayout();
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

    private List<String> getTableTitle() {
    return Arrays.asList("互动","评论","系统");
}
    private void initTableLayout() {
        mFragments.clear();
        List<String> list = getTableTitle();
        if (null == list || 0 == list.size()) return;
        for (int i = 0; i < 3; i++) {
            switch (i) {//
                case 0:
                    interactionFragment = new InteractionFragment();
                    break;
                case 1:
                    commentFragment = new CommentFragment();
                    break;
                case 2:
                    systemFragment = new SystemFragment();
                    break;
                default:
            }
        }
        mFragments.add(interactionFragment);
        mFragments.add(commentFragment);
        mFragments.add(systemFragment);
        TablayoutViewAdapter viewAdapter = new TablayoutViewAdapter(getSupportFragmentManager(), mFragments, list);
        viewPager.setAdapter(viewAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                magicIndicator.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                if(lastPosition != position) {
                    if(lastPosition == 0 && position ==1 && firstShow ) {  //调用互动页面去除红点
                        firstShow = false;
                        commonNavigator.getAdapter().notifyDataSetChanged();
                        exitMessage(1);
                    }
                    if(lastPosition == 0 && position ==2  && firstShow) { //调用互动页面去除红点
                        firstShow = false;
                        commonNavigator.getAdapter().notifyDataSetChanged();
                        exitMessage(1);
                    }
                    if(lastPosition == 1 && position ==2 && twoShow) {//调用评论页面去除红点
                        twoShow = false;
                        commonNavigator.getAdapter().notifyDataSetChanged();
                        exitMessage(2);
                    }
                    if(lastPosition == 1 && position == 0 && twoShow) {//调用评论页面去除红点
                        twoShow = false;
                        commonNavigator.getAdapter().notifyDataSetChanged();
                        exitMessage(2);
                    }
                    if(lastPosition == 2 && position ==1 && threeShow) {//调用系统页面去除红点
                        threeShow = false;
                        commonNavigator.getAdapter().notifyDataSetChanged();
                        exitMessage(3);
                    }
                    if(lastPosition == 2 && position ==0 && threeShow) {//调用系统页面去除红点
                        threeShow = false;
                        commonNavigator.getAdapter().notifyDataSetChanged();
                        exitMessage(3);
                    }
                    lastPosition = position;
                }
                magicIndicator.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                magicIndicator.onPageScrollStateChanged(state);
            }
        });
        viewPager.setCurrentItem(0);
        viewPager.setOffscreenPageLimit(list.size());
        magicIndicator.setBackgroundColor(getResources().getColor(R.color.white));
        //新建导航栏
        commonNavigator = new CommonNavigator(this);
        commonNavigator.setEnablePivotScroll(true);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return list == null ? 0 : list.size();
            }
            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView colorTransitionPagerTitleView = new SimplePagerTitleView(context);
                colorTransitionPagerTitleView.setNormalColor(ContextCompat.getColor(context, R.color.FFA2A2A2));
                colorTransitionPagerTitleView.setSelectedColor(ContextCompat.getColor(context, R.color.normal_color));
                colorTransitionPagerTitleView.setSelectedSize(18);
                colorTransitionPagerTitleView.setDeselectedSize(17);
                TextPaint tp = colorTransitionPagerTitleView.getPaint();
                tp.setFakeBoldText(true);
                colorTransitionPagerTitleView.setText(list.get(index));
                colorTransitionPagerTitleView.setOnClickListener(view -> viewPager.setCurrentItem(index));
                //创建一个角标注入到当前Tab
                final BadgePagerTitleView badgePagerTitleView = new BadgePagerTitleView(context);
                badgePagerTitleView.setInnerPagerTitleView(colorTransitionPagerTitleView);
                if (index == 0) {
                    circle(context,badgePagerTitleView,index);
                }
                if (index == 1) {
                    circle(context,badgePagerTitleView,index);
                }
                if (index == 2) {
                    circle(context,badgePagerTitleView,index);
                }
                badgePagerTitleView.setAutoCancelBadge(false);
                return badgePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                //设置标题指示器，也有多种,可不选，即没有标题指示器。
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setColors(getResources().getColor(R.color.normal_color));
                indicator.setYOffset(13);
                indicator.setXOffset(35);
                return indicator;
            }

        });

        //绑定导航栏
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, viewPager);
    }

    public void circle(Context context,BadgePagerTitleView badgePagerTitleView,int position) {
        imageView = new ImageView(context);
        if(0==position) {
            if(firstShow) {
                imageView.setVisibility(View.VISIBLE);
            }else {
                imageView.setVisibility(View.GONE);
            }
        }else if(1==position) {
            if(twoShow) {
                imageView.setVisibility(View.VISIBLE);
            }else {
                imageView.setVisibility(View.GONE);
            }
        }else if(2==position) {
            if(threeShow) {
                imageView.setVisibility(View.VISIBLE);
            }else {
                imageView.setVisibility(View.GONE);
            }
        }

        imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.bg_circle_de5c51));
        badgePagerTitleView.setBadgeView(imageView);
        //定位角标位于Tab的X，Y轴的位置
        badgePagerTitleView.setXBadgeRule(new BadgeRule(BadgeAnchor.CONTENT_RIGHT, UIUtil.dip2px(context, 1)));
        badgePagerTitleView.setYBadgeRule(new BadgeRule(BadgeAnchor.CONTENT_TOP, 0));
    }

    public void exitMessage(int type) {
        Map<String, String> map = new HashMap<>();
        map.put("type",type+""); //1=互动，2=评论，3=系统
        OkHttp.post(this,HttpServicePath.URL_NEW_LAYOUT, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(type ==1) {
                            EventBus.getDefault().post(new EventTags.RefreshDot(0));
                        }else if(type==2) {
                            EventBus.getDefault().post(new EventTags.RefreshDot(1));
                        }else {
                            EventBus.getDefault().post(new EventTags.RefreshDot(2));
                        }
                    }
                });
            }
        });

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void attention(EventTags.ShowDot showDot) {
       int position = showDot.getPosition();
       switch (position){
           case 0:
                firstShow = true;
                commonNavigator.getAdapter().notifyDataSetChanged();
               break;
           case 1:
               twoShow =true;
               commonNavigator.getAdapter().notifyDataSetChanged();
               break;
           case 2:
               threeShow = true;
               commonNavigator.getAdapter().notifyDataSetChanged();
               break;
       }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        switch (lastPosition){
            case 0:
                if(firstShow) {
                    exitMessage(1);
                }
                break;
            case 1:
                if(twoShow) {
                    exitMessage(2);
                }
                break;
            case 2:
                if(threeShow) {
                    exitMessage(3);
                }
                break;
        }
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
