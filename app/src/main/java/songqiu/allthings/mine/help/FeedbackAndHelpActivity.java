package songqiu.allthings.mine.help;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import songqiu.allthings.R;
import songqiu.allthings.adapter.TablayoutViewAdapter;
import songqiu.allthings.base.BaseActivity;
import songqiu.allthings.mine.attention.AttentionGambitFragment;
import songqiu.allthings.mine.attention.AttentionUserFragment;
import songqiu.allthings.util.CustomViewUtils;
import songqiu.allthings.util.SharedPreferencedUtils;
import songqiu.allthings.util.statusbar.StatusBarUtils;
import songqiu.allthings.util.theme.ThemeManager;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/8/20
 *
 *类描述：反馈和帮助
 *
 ********/
public class FeedbackAndHelpActivity extends BaseActivity {

    @BindView(R.id.backImg)
    ImageView backImg;
    @BindView(R.id.titleTv)
    TextView titleTv;
    @BindView(R.id.tab_home_class)
    TabLayout tabHomeClass;
    @BindView(R.id.vp_home)
    ViewPager vpHome;
    @BindView(R.id.shadowLayout)
    LinearLayout shadowLayout;

    List<Fragment> mFragments = new ArrayList<>();
    AllFeedbackFragment allFeedbackFragment;
    MineFeedbackFragment mineFeedbackFragment;
    int position;

    @Override
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_feedback_help);
    }

    @Override
    public void init() {
        boolean dayModel = SharedPreferencedUtils.getBoolean(this,SharedPreferencedUtils.dayModel,true);
        modeUi(dayModel);
        titleTv.setText("反馈与帮助");
        position = getIntent().getIntExtra("position",0);
        mFragments.clear();
        initTableLayout();
    }

    public void modeUi(boolean isDay) {
        if(isDay) {
            shadowLayout.setVisibility(View.GONE);
            StatusBarUtils.with(this)
                    .setColor(getResources().getColor(ThemeManager.getCurrentThemeRes(this, R.color.FFF9FAFD)))
                    .init()
                    .setStatusTextColorAndPaddingTop(true, this);
        }else {
            shadowLayout.setVisibility(View.VISIBLE);
            StatusBarUtils.with(this)
                    .setColor(getResources().getColor(ThemeManager.getCurrentThemeRes(this, R.color.trans_6)))
                    .init()
                    .setStatusTextColorAndPaddingTop(true, this);
        }
    }

    private List<String> getTableTitle() {
        return Arrays.asList("全部问题", "我的反馈");
    }
    private void initTableLayout() {

        tabHomeClass.setTabMode(TabLayout.MODE_FIXED);//MODE_FIXED
        mFragments = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            switch (i) {//1=用户关注，2=话题关注
                case 0:
                    allFeedbackFragment = new AllFeedbackFragment();
                    break;
                case 1:
                    mineFeedbackFragment = new MineFeedbackFragment();
                    break;
                default:
            }
        }
        mFragments.add(allFeedbackFragment);
        mFragments.add(mineFeedbackFragment);
        TablayoutViewAdapter viewAdapter = new TablayoutViewAdapter(getSupportFragmentManager(), mFragments, getTableTitle());
        vpHome.setAdapter(viewAdapter);
        vpHome.setOffscreenPageLimit(mFragments.size());
        vpHome.setCurrentItem(position);
        tabHomeClass.setupWithViewPager(vpHome);
        tabHomeClass.post(() -> CustomViewUtils.setIndicator(tabHomeClass, 15, 15));

        vpHome.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
//                if(position == 0){
//                    setUi(0);
//                }else {
//                    setUi(1);
//                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
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
