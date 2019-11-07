package songqiu.allthings.util;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.badge.BadgeAnchor;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.badge.BadgePagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.badge.BadgeRule;

import songqiu.allthings.R;

/*******
 *
 *Created by ???
 *
 *???? 2019/9/24
 *
 *??????????????
 *
 ********/
public class TabCreateUtils {
    public static void setOrangeTab(Context context, MagicIndicator magicIndicator, ViewPager viewPager, String[] tabNames) {
        CommonNavigator commonNavigator = new CommonNavigator(context);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {

            @Override
            public int getCount() {
                return tabNames == null ? 0 : tabNames.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                ColorTransitionPagerTitleView colorTransitionPagerTitleView = new ColorTransitionPagerTitleView(context);
                colorTransitionPagerTitleView.setNormalColor(ContextCompat.getColor(context, R.color.bottom_tab_tv));
                colorTransitionPagerTitleView.setSelectedColor(ContextCompat.getColor(context, R.color.normal_color));
                colorTransitionPagerTitleView.setTextSize(16);
                colorTransitionPagerTitleView.setText(tabNames[index]);
                colorTransitionPagerTitleView.setOnClickListener(view -> viewPager.setCurrentItem(index));
                //???????????Tab
                final BadgePagerTitleView badgePagerTitleView = new BadgePagerTitleView(context);
                badgePagerTitleView.setInnerPagerTitleView(colorTransitionPagerTitleView);
                if (index == 0) {
                    //????TextView?????
                    TextView badgeTextView = new TextView(context);
//                    badgeTextView.setText("99" );
//                    badgeTextView.setTextColor(Color.WHITE);
//                    badgeTextView.setTextSize(10);
//                    badgeTextView.setPadding(UIUtil.dip2px(context, 4),0,UIUtil.dip2px(context, 4),0);
                    badgeTextView.setBackgroundResource(R.drawable.bg_circle_de5c51);
                    badgePagerTitleView.setBadgeView(badgeTextView);
                    //??????Tab?X?Y????
                    badgePagerTitleView.setXBadgeRule(new BadgeRule(BadgeAnchor.CONTENT_RIGHT, UIUtil.dip2px(context, 6)));
                    badgePagerTitleView.setYBadgeRule(new BadgeRule(BadgeAnchor.CONTENT_TOP, 0));
                }
                badgePagerTitleView.setAutoCancelBadge(false);
                return badgePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
                indicator.setColors(ContextCompat.getColor(context, R.color.FFDE5C51));
                indicator.setRoundRadius(3);
                return indicator;
            }
        });
        commonNavigator.setAdjustMode(true);
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, viewPager);
    }
}
