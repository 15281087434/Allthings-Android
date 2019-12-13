package songqiu.allthings.search;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.heartfor.heartvideo.video.HeartVideo;
import com.heartfor.heartvideo.video.HeartVideoInfo;
import com.heartfor.heartvideo.video.HeartVideoManager;
import com.heartfor.heartvideo.video.PlayerStatus;
import com.heartfor.heartvideo.video.VideoControl;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.droidsonroids.gif.GifImageView;
import songqiu.allthings.Event.EventTags;
import songqiu.allthings.R;
import songqiu.allthings.activity.CommentWebViewActivity;
import songqiu.allthings.adapter.SearchHistoryAdapter;
import songqiu.allthings.adapter.SearchHotGambitAdapter;
import songqiu.allthings.adapter.TablayoutViewAdapter;
import songqiu.allthings.base.BaseActivity;
import songqiu.allthings.bean.AdvertiseBean;
import songqiu.allthings.bean.SearchHistoryBean;
import songqiu.allthings.bean.SearchHotGambitBean;
import songqiu.allthings.db.AllthingsSQLiteOpenHelper;
import songqiu.allthings.home.gambit.HotGambitDetailActivity;
import songqiu.allthings.http.BaseBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.http.OkHttp;
import songqiu.allthings.http.RequestCallBack;
import songqiu.allthings.util.ClickUtil;
import songqiu.allthings.util.GlideLoadUtils;
import songqiu.allthings.util.KeyBoardUtils;
import songqiu.allthings.util.LogUtil;
import songqiu.allthings.util.SharedPreferencedUtils;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.util.statusbar.StatusBarUtils;
import songqiu.allthings.util.theme.ThemeManager;
import songqiu.allthings.view.FlowLayout;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/8/16
 *
 *类描述：
 *
 ********/
public class SearchActivity extends BaseActivity {
    @BindView(R.id.backImg)
    ImageView backImg;
    @BindView(R.id.searchEt)
    EditText searchEt;
    @BindView(R.id.searchTv)
    TextView searchTv;
    @BindView(R.id.searchLayout)
    LinearLayout searchLayout;
    @BindView(R.id.hotGambitRecyclerView)
    RecyclerView hotGambitRecyclerView;
    @BindView(R.id.flowLayout)
    FlowLayout flowLayout;
    @BindView(R.id.searchRecordLayout)
    LinearLayout searchRecordLayout;
    @BindView(R.id.historyListView)
    ListView historyListView;
    @BindView(R.id.shadowLayout)
    LinearLayout shadowLayout;

    //广告
    @BindView(R.id.advertisingImg)
    GifImageView advertisingImg;
    @BindView(R.id.videoView)
    HeartVideo videoView;
    @BindView(R.id.titleTv)
    TextView titleTv;
    @BindView(R.id.downloadLayout)
    LinearLayout downloadLayout;
    @BindView(R.id.jumpLayout)
    RelativeLayout jumpLayout;
    @BindView(R.id.advertisingLayout)
    LinearLayout advertisingLayout;
    AdvertiseBean advertiseBean;

    //
    @BindView(R.id.scrollView)
    ScrollView scrollView;
    @BindView(R.id.searchResultLayout)
    LinearLayout searchResultLayout;
    @BindView(R.id.magicIndicator)
    MagicIndicator magicIndicator;
    @BindView(R.id.vp_home)
    ViewPager viewPager;
    List<Fragment> mFragments = new ArrayList<>();
    public String keyword;

    //热门话题
    List<SearchHotGambitBean> item;
    SearchHotGambitAdapter adapter;

    AllthingsSQLiteOpenHelper helper = new AllthingsSQLiteOpenHelper(this);
    SQLiteDatabase db;
    int count;


    List<SearchHistoryBean> historyBeanList;
    SearchHistoryAdapter searchHistoryAdapter;


    @Override
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_search);
    }

    @Override
    public void init() {
        boolean dayModel = SharedPreferencedUtils.getBoolean(this, SharedPreferencedUtils.dayModel, true);
        modeUi(dayModel);
        initSearchEt();
        initTableLayout();
        initRecyclerView();
        queryData("");
        getHotGambit();
        getHotLabel();
        getAdvertise();
    }

    public void modeUi(boolean isDay) {
        if (isDay) {
            shadowLayout.setVisibility(View.GONE);
            StatusBarUtils.with(this)
                    .setColor(getResources().getColor( R.color.FFF9FAFD))
                    .init()
                    .setStatusTextColorAndPaddingTop(true, this);
        } else {
            shadowLayout.setVisibility(View.VISIBLE);
            StatusBarUtils.with(this)
                    .setColor(getResources().getColor( R.color.trans_6))
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
                if(StringUtil.isEmpty(content.toString())) {
                    scrollView.setVisibility(View.VISIBLE);
                    searchResultLayout.setVisibility(View.GONE);
                }
            }
        });
        searchEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {//搜索按键action
                    KeyBoardUtils.closeKeyboard(SearchActivity.this);
                    String content = searchEt.getText().toString().trim();
                    if (!StringUtil.isEmpty(content)) {
                        intoData(content);
                        keyword = content;
                        scrollView.setVisibility(View.GONE);
                        searchResultLayout.setVisibility(View.VISIBLE);
                        if(null != viewPager) {
                            viewPager.setCurrentItem(0);
                        }
                        EventBus.getDefault().post(new EventTags.SearchKeyword(content));
                    }
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
                    mFragments.add(searchListFragment);
                    break;
                case 1:
                    SearchVideoFragment searchVideoFragment = new SearchVideoFragment();
                    mFragments.add(searchVideoFragment);
                    break;
                case 2:
                    SearchTopicFragment searchTopicFragment = new SearchTopicFragment();
                    mFragments.add(searchTopicFragment);
                    break;
                default:
            }
        }
        TablayoutViewAdapter viewAdapter = new TablayoutViewAdapter(getSupportFragmentManager(), mFragments, list);
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


    public void initRecyclerView() {
        item = new ArrayList<>();
        adapter = new SearchHotGambitAdapter(this, item);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        hotGambitRecyclerView.setLayoutManager(linearLayoutManager);
        hotGambitRecyclerView.setAdapter(adapter);
        adapter.setAdapterListener(new SearchHotGambitAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(SearchHotGambitBean searchHotGambitBean) {
                Intent intent = new Intent(SearchActivity.this, HotGambitDetailActivity.class);
                intent.putExtra("talkid", searchHotGambitBean.id);
                startActivity(intent);
            }
        });

        //搜索历史
        historyBeanList = new ArrayList<>();
        searchHistoryAdapter = new SearchHistoryAdapter(this, historyBeanList);
        historyListView.setAdapter(searchHistoryAdapter);
        searchHistoryAdapter.setOnItemSelectedListener(new SearchHistoryAdapter.OnItemSelectedListener() {
            @Override
            public void onItemSelected(SearchHistoryBean searchHistoryBean) {
                keyword = searchHistoryBean.content;
                EventBus.getDefault().post(new EventTags.SearchKeyword(searchHistoryBean.content));
                scrollView.setVisibility(View.GONE);
                searchResultLayout.setVisibility(View.VISIBLE);
                if(null != viewPager) {
                    viewPager.setCurrentItem(0);
                }

            }

            @Override
            public void detel(SearchHistoryBean searchHistoryBean) {
                deleteData(searchHistoryBean.content);
                historyBeanList.remove(searchHistoryBean);
                searchHistoryAdapter.notifyDataSetChanged();
            }
        });
    }

    public void initFlowLayout(List<String> searchHotLabelList) {
        for (int i = 0; i < searchHotLabelList.size(); i++) {
            LayoutInflater inflater = SearchActivity.this.getLayoutInflater();
            final View view = inflater.inflate(R.layout.item_search_hot_label, null);
            TextView textView = view.findViewById(R.id.labelTv);
            textView.setText(searchHotLabelList.get(i));
            flowLayout.addView(view);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String searchLabel = textView.getText().toString().trim();
                    searchEt.setText(searchLabel);
                    searchEt.setSelection(searchLabel.length());
                    intoData(searchLabel);
                    keyword = searchLabel;
                    EventBus.getDefault().post(new EventTags.SearchKeyword(searchLabel));
                    scrollView.setVisibility(View.GONE);
                    searchResultLayout.setVisibility(View.VISIBLE);
                    if(null != viewPager) {
                        viewPager.setCurrentItem(0);
                    }
                }
            });
        }
    }


    //将数据插入数据库
    public void intoData(String content) {
        if (!StringUtil.isEmpty(content)) {
            content = content.replaceAll(" ","").replaceAll("'","");
            boolean hasData = hasData(content);
            if (!hasData) {
                if (count >= 5) {
                    //删除第一条数据
                    db = helper.getWritableDatabase();
                    db.execSQL("delete from records where id=(select min(id) from records)");
                    db.close();
                }
                insertData(content);
                queryData("");
            }
        }
    }

    /**
     * 模糊查询数据
     */
    public void queryData(String tempName) {
        historyBeanList.clear();
        Cursor cursor = helper.getReadableDatabase().rawQuery(
                "select id as _id,name from records where name like '%" + tempName + "%' order by id desc ", null);
        count = cursor.getCount();
        if (count > 0) {
            searchRecordLayout.setVisibility(View.VISIBLE);
        } else {
            searchRecordLayout.setVisibility(View.GONE);
        }
        if (null != cursor && 0 != cursor.getCount()) {
            while (cursor.moveToNext()) {
                SearchHistoryBean searchHistoryBean = new SearchHistoryBean();
                searchHistoryBean.content = cursor.getString(cursor
                        .getColumnIndex("name"));
                historyBeanList.add(searchHistoryBean);
            }
            searchHistoryAdapter.notifyDataSetChanged();
        }
    }


    /**
     * 检查数据库中是否已经有该条记录
     */
    public boolean hasData(String tempName) {
        Cursor cursor = helper.getReadableDatabase().rawQuery(
                "select id as _id,name from records where name =?", new String[]{tempName});
        //判断是否有下一个
        return cursor.moveToNext();
    }


    /**
     * 插入数据
     */
    public void insertData(String tempName) {
        db = helper.getWritableDatabase();
        db.execSQL("insert into records(name) values('" + tempName + "')");
        db.close();
    }

    /**
     * 清空数据
     */
    private void deleteData(String tempName) {
        db = helper.getWritableDatabase();
        db.execSQL("delete from records where name='" + tempName + "'");
        db.close();
    }

    private void deleteData() {
        db = helper.getWritableDatabase();
        db.execSQL("delete from records");
        db.close();
    }

    @Override
    protected void onPause() {
        super.onPause();
        KeyBoardUtils.closeKeyboard(this);
        HeartVideoManager.getInstance().pause();
    }

    public void getHotGambit() {
        Map<String, String> map = new HashMap<>();
        OkHttp.post(this, HttpServicePath.URL_SEARCH_TALK, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        String data = gson.toJson(baseBean.data);
                        if (StringUtil.isEmpty(data)) return;
                        List<SearchHotGambitBean> searchHotGambitList = gson.fromJson(data, new TypeToken<List<SearchHotGambitBean>>() {
                        }.getType());
                        item.addAll(searchHotGambitList);
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    public void getHotLabel() {
        Map<String, String> map = new HashMap<>();
        OkHttp.post(this, HttpServicePath.URL_SEARCH_LABEL, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        String data = gson.toJson(baseBean.data);
                        if (StringUtil.isEmpty(data)) return;
                        List<String> searchHotLabelList = gson.fromJson(data, new TypeToken<List<String>>() {
                        }.getType());
                        if (null != searchHotLabelList && 0 != searchHotLabelList.size()) {
                            initFlowLayout(searchHotLabelList);
                        }
                    }
                });
            }
        });
    }


    public void getAdvertise() {
        Map<String, String> map = new HashMap<>();
        map.put("category", 7 + "");
        OkHttp.post(this, HttpServicePath.URL_ADVERTISE, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        String data = gson.toJson(baseBean.data);
                        if (StringUtil.isEmpty(data)) return;
                        List<AdvertiseBean> advertiseBeanListBean = gson.fromJson(data, new TypeToken<List<AdvertiseBean>>() {}.getType());
                        if(null==advertiseBeanListBean || 0==advertiseBeanListBean.size()) return;
                        advertiseBean = advertiseBeanListBean.get(0);
                        if (null != advertiseBean) {
                            String url = advertiseBean.url.replaceAll("\"","");
                            if (!StringUtil.isEmpty(url)) {
                                if (!url.contains("http")) {
                                    url = HttpServicePath.BasePicUrl + url;
                                }
                            }
                            advertisingLayout.setVisibility(View.VISIBLE);
                            titleTv.setText(advertiseBean.title);
                            if (1 == advertiseBean.type) { //广告图片
                                advertisingImg.setVisibility(View.VISIBLE);
                                RequestOptions options = new RequestOptions()
                                        .error(R.mipmap.pic_default)
                                        .placeholder(R.mipmap.pic_default);
                                GlideLoadUtils.getInstance().glideLoad(SearchActivity.this,url,options,advertisingImg);
                                if (2 == advertiseBean.change_type) { //大图无下载
                                    downloadLayout.setVisibility(View.GONE);
                                }
                            } else { //广告视频
                                videoView.setVisibility(View.VISIBLE);
                                String path = advertiseBean.video_url;//
                                if(!StringUtil.isEmpty(path)) {
                                    HeartVideoInfo info = HeartVideoInfo.Builder().setTitle("").setPath(path).setImagePath(url).setSaveProgress(false).builder();
                                    VideoControl control = new VideoControl(SearchActivity.this);
                                    control.setInfo(info);
                                    videoView.setHeartVideoContent(control);
                                }
                                if (5 == advertiseBean.change_type) { //大图无下载
                                    downloadLayout.setVisibility(View.GONE);
                                }
                            }
                        }
                    }
                });
            }
        });
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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

    @OnClick({R.id.backImg, R.id.searchTv,R.id.jumpLayout,R.id.advertisingImg})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.backImg:
                finish();
                break;
            case R.id.searchTv:
                if (ClickUtil.onClick()) {
                    String content = searchEt.getText().toString().trim();
                    if (StringUtil.isEmpty(content)) return;
                    //android.database.sqlite.SQLiteException: near "hy。": syntax error (code 1 SQLITE_ERROR): , while compiling: insert into records(name) values('醋8$,qkto。的0v'hy。 喔iezg @":关于。下班。 热￥@—、？门*')
                    intoData(content);
//                    toSearch(content);
                    keyword = content;
                    EventBus.getDefault().post(new EventTags.SearchKeyword(content));
                    scrollView.setVisibility(View.GONE);
                    searchResultLayout.setVisibility(View.VISIBLE);
                    if(null != viewPager) {
                        viewPager.setCurrentItem(0);
                    }
                }
                break;
            case R.id.jumpLayout:
            case R.id.advertisingImg:
                if(ClickUtil.onClick()) {
                    if(null == advertiseBean) return;
                    Intent intent = new Intent(SearchActivity.this,CommentWebViewActivity.class);
                    intent.putExtra("url", advertiseBean.jump_url);
                    startActivity(intent);
                }
                break;
        }
    }

}
