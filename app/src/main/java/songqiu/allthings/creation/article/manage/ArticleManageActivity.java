package songqiu.allthings.creation.article.manage;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextPaint;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import songqiu.allthings.R;
import songqiu.allthings.adapter.TablayoutViewAdapter;
import songqiu.allthings.base.BaseActivity;
import songqiu.allthings.mine.help.AllFeedbackFragment;
import songqiu.allthings.mine.help.MineFeedbackFragment;
import songqiu.allthings.util.SharedPreferencedUtils;
import songqiu.allthings.util.statusbar.StatusBarUtils;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/12/20
 *
 *类描述：作品管理
 *
 ********/
public class ArticleManageActivity extends BaseActivity {
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
    ViewPager vpHome;
    @BindView(R.id.shadowLayout)
    LinearLayout shadowLayout;

    List<Fragment> mFragments = new ArrayList<>();
    ArticleUnPutawayFragment articleUnPutawayFragment;
    ArticlePutawayFragment articlePutawayFragment;


    @Override
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_article_manage);
    }

    @Override
    public void init() {
        boolean dayModel = SharedPreferencedUtils.getBoolean(this,SharedPreferencedUtils.dayModel,true);
        modeUi(dayModel);
        titleTv.setText("作品管理");
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
                    .setColor(getResources().getColor(R.color.trans_6))
                    .init()
                    .setStatusTextColorAndPaddingTop(true, this);
        }
    }

    private List<String> getTableTitle() {
        return Arrays.asList("未上架", "已上架");
    }

    private void initTableLayout() {
        List<String> list = getTableTitle();
        if (null == list || 0 == list.size()) return;
        for (int i = 0; i < 2; i++) {
            switch (i) {//1=用户关注，2=话题关注
                case 0:
                    articleUnPutawayFragment = new ArticleUnPutawayFragment();
                    break;
                case 1:
                    articlePutawayFragment = new ArticlePutawayFragment();
                    break;
                default:
            }
        }
        mFragments.add(articleUnPutawayFragment);
        mFragments.add(articlePutawayFragment);
        TablayoutViewAdapter viewAdapter = new TablayoutViewAdapter(getSupportFragmentManager(), mFragments, list);
        vpHome.setAdapter(viewAdapter);
        vpHome.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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
        vpHome.setCurrentItem(0);

        magicIndicator.setBackgroundColor(getResources().getColor(R.color.white));
        //新建导航栏
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setEnablePivotScroll(true);
        commonNavigator.setAdjustMode(true);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return list == null ? 0 : list.size();
            }
            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                //设置Magicindicator的一种标题模式， 标题模式有很多种，这是最基本的一种
                SimplePagerTitleView simplePagerTitleView = new SimplePagerTitleView(context);
                simplePagerTitleView.setText(list.get(index));
                //设置被选中的item颜色
                simplePagerTitleView.setSelectedColor(getResources().getColor(R.color.normal_color));
                //设置为被选中item颜色
                simplePagerTitleView.setNormalColor(getResources().getColor(R.color.FFA2A2A2));
                simplePagerTitleView.setSelectedSize(19);
                simplePagerTitleView.setDeselectedSize(17);
                TextPaint tp = simplePagerTitleView.getPaint();
                tp.setFakeBoldText(true);

                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vpHome.setCurrentItem(index);
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                //设置标题指示器，也有多种,可不选，即没有标题指示器。
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setColors(getResources().getColor(R.color.normal_color));
                indicator.setYOffset(20);
                indicator.setXOffset(60);
                return indicator;
            }

        });

        //绑定导航栏
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, vpHome);
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
