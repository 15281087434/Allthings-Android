package songqiu.allthings.home.gambit;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import songqiu.allthings.Event.EventTags;
import songqiu.allthings.R;
import songqiu.allthings.adapter.AllHotGambitAdapter;
import songqiu.allthings.adapter.HomeHotGambitAdapter;
import songqiu.allthings.base.BaseActivity;
import songqiu.allthings.bean.HomeGambitHotBean;
import songqiu.allthings.http.BaseBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.http.OkHttp;
import songqiu.allthings.http.RequestCallBack;
import songqiu.allthings.iterface.HomeHotGambitListener;
import songqiu.allthings.util.SharedPreferencedUtils;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.util.VibratorUtil;
import songqiu.allthings.util.statusbar.StatusBarUtils;
import songqiu.allthings.util.theme.ThemeManager;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/9/18
 *
 *类描述： 全部话题
 *
 ********/
public class AllHotGambitActivity extends BaseActivity {
    @BindView(R.id.backImg)
    ImageView backImg;
    @BindView(R.id.titleTv)
    TextView titleTv;
    @BindView(R.id.rightTv)
    TextView rightTv;
    @BindView(R.id.line)
    LinearLayout line;
    @BindView(R.id.recycle)
    RecyclerView recycle;
    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout smartRefreshLayout;
    @BindView(R.id.shadowLayout)
    LinearLayout shadowLayout;

    int pageNo = 1;

    AllHotGambitAdapter hotAdapter;
    List<HomeGambitHotBean> list;

    @Override
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_all_hot_gambit);
    }

    @Override
    public void init() {
        boolean dayModel = SharedPreferencedUtils.getBoolean(this,SharedPreferencedUtils.dayModel,true);
        modeUi(dayModel);
        titleTv.setText("全部话题");
        initRecyclerView();
    }

    public void modeUi(boolean isDay) {
        if(isDay) {
            shadowLayout.setVisibility(View.GONE);
            StatusBarUtils.with(this)
                    .setColor(getResources().getColor( R.color.FFF9FAFD))
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

    @Override
    protected void onResume() {
        super.onResume();
        getHotData(pageNo,false);
    }

    public void initRecyclerView() {
        list = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycle.setLayoutManager(linearLayoutManager);
        hotAdapter = new AllHotGambitAdapter(this,list);
        recycle.setAdapter(hotAdapter);

        hotAdapter.setHomeHotGambitListener(new HomeHotGambitListener() {
            @Override
            public void addFollow(String url, int id, List<HomeGambitHotBean> item) {
                follow(url,id,item);
            }
        });

        smartRefreshLayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                pageNo = pageNo+1;
                getHotData(pageNo,false);
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                pageNo = 1;
                getHotData(pageNo,true);
            }
        });
    }

    public void getHotData(int page,boolean ringDown) {
        Map<String, String> map = new HashMap<>();
        map.put("num",10+"");
        map.put("page",page+"");
        OkHttp.post(this,smartRefreshLayout, HttpServicePath.URL_TALKLIST, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        String data = gson.toJson(baseBean.data);
                        if (StringUtil.isEmpty(data)) return;
                        List<HomeGambitHotBean> homeGambitHotList = gson.fromJson(data, new TypeToken<List<HomeGambitHotBean>>() {}.getType());
                            if(1==page) {
                                list.clear();
                            }
                            list.addAll(homeGambitHotList);
                            hotAdapter.notifyDataSetChanged();
                        if(ringDown) {
                            VibratorUtil.ringDown(AllHotGambitActivity.this);
                        }
                    }
                });
            }
        });
    }

    public void follow(String url,int talk_id,List<HomeGambitHotBean> item) {
        Map<String, String> map = new HashMap<>();
        map.put("talk_id", talk_id + "");
        OkHttp.post(this, url, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(url.contains("follow_talk_no")) { //取消关注
                            for(int i = 0;i<item.size();i++) {
                                if(talk_id == item.get(i).id) {
                                    item.get(i).is_follow = 0;
                                    item.get(i).follow_num = item.get(i).follow_num -1;
                                }
                            }
                        }else {
                            for(int i = 0;i<item.size();i++) {
                                if(talk_id == item.get(i).id) {
                                    item.get(i).is_follow = 1;
                                    item.get(i).follow_num = item.get(i).follow_num +1;
                                }
                            }
                        }
                        hotAdapter.notifyDataSetChanged();
                        EventBus.getDefault().post(new EventTags.GambitRefresh());
                    }
                });
            }
        });
    }

    @OnClick(R.id.backImg)
    public void onViewClick() {
        finish();
    }

}
