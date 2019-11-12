package songqiu.allthings.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
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
import butterknife.OnClick;
import songqiu.allthings.R;
import songqiu.allthings.adapter.TablayoutViewAdapter;
import songqiu.allthings.base.BaseActivity;
import songqiu.allthings.util.KeyBoardUtils;
import songqiu.allthings.util.SharedPreferencedUtils;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.util.statusbar.StatusBarUtils;
import songqiu.allthings.util.theme.ThemeManager;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/8/22
 *
 *类描述：搜索结果列表页
 *
 ********/
public class SearchResultListActivity extends BaseActivity {

    @BindView(R.id.backImg)
    ImageView backImg;
    @BindView(R.id.searchEt)
    EditText searchEt;
    @BindView(R.id.searchTv)
    TextView searchTv;
    @BindView(R.id.searchLayout)
    LinearLayout searchLayout;
    @BindView(R.id.magicIndicator)
    MagicIndicator magicIndicator;
    @BindView(R.id.vp_home)
    ViewPager viewPager;
    @BindView(R.id.shadowLayout)
    LinearLayout shadowLayout;

    List<Fragment> mFragments = new ArrayList<>();
    String keyword;

    @Override
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_search_result_list);
    }

    @Override
    public void init() {
        boolean dayModel = SharedPreferencedUtils.getBoolean(this,SharedPreferencedUtils.dayModel,true);
        modeUi(dayModel);
        keyword = getIntent().getStringExtra("keyword");
        searchEt.setText(keyword);
        if(!StringUtil.isEmpty(keyword)) {
            searchEt .setSelection(keyword.length());
        }
        initSearchEt();
        initTableLayout();
        KeyBoardUtils.closeKeyboard(this);
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

    public void initSearchEt() {
        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable content) {
                //
                Intent intent = new Intent(SearchResultListActivity.this,SearchActivity.class);
                intent.putExtra("keyword",content.toString());
                startActivity(intent);
            }
        });
        searchEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {//搜索按键action
//                    KeyBoardUtils.closeKeyboard(SearchActivity.this);
//                    String content = searchEt.getText().toString();
//                    if(!StringUtil.isEmpty(content)) {
//                        intoData(content);
//                        Intent intent = new Intent(SearchActivity.this,SearchResultListActivity.class);
//                        intent.putExtra("keyword",content);
//                        startActivity(intent);
//                    }
                }
                return false;
            }
        });
    }


    private List<String> getTableTitle() {
        return Arrays.asList("图文", "视频", "话题");
    }

    private void initTableLayout() {
        mFragments.clear();
        List<String> list = getTableTitle();
        if (null == list || 0 == list.size()) return;
        for (int i = 0; i < 3; i++) {
            switch (i) {
                case 0:
                    SearchTxtFragment searchListFragment = new SearchTxtFragment();
                    searchListFragment.keyword = keyword;
                    mFragments.add(searchListFragment);
                    break;
                case 1:
                    SearchVideoFragment searchVideoFragment = new SearchVideoFragment();
                    searchVideoFragment.keyword = keyword;
                    mFragments.add(searchVideoFragment);
                    break;
                case 2:
                    SearchTopicFragment searchTopicFragment = new SearchTopicFragment();
                    searchTopicFragment.keyword = keyword;
                    mFragments.add(searchTopicFragment);
                    break;
                default:
            }
        }
        TablayoutViewAdapter viewAdapter = new TablayoutViewAdapter(getSupportFragmentManager(), mFragments, list);
        viewPager.setAdapter(viewAdapter);
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
        viewPager.setCurrentItem(0);

        magicIndicator.setBackgroundColor(getResources().getColor(R.color.white));
        //新建导航栏
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setEnablePivotScroll(true);
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
                simplePagerTitleView.setDeselectedSize(18);
                TextPaint tp = simplePagerTitleView.getPaint();
                tp.setFakeBoldText(true);

                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewPager.setCurrentItem(index);
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
                indicator.setXOffset(35);
                return indicator;
            }

        });

        //绑定导航栏
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, viewPager);
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
