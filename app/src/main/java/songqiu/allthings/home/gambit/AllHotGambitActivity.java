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
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
import songqiu.allthings.util.ShowNumUtil;
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
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        boolean dayModel = SharedPreferencedUtils.getBoolean(this,SharedPreferencedUtils.dayModel,true);
        modeUi(dayModel);
        titleTv.setText("全部话题");
        initRecyclerView();
        getHotData(pageNo,false);
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

    public void initRecyclerView() {
        list = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycle.setLayoutManager(linearLayoutManager);
        hotAdapter = new AllHotGambitAdapter(this,list);
        recycle.setAdapter(hotAdapter);

        hotAdapter.setHomeHotGambitListener(new HomeHotGambitListener() {
            @Override
            public void addFollow(String url, int id,RecyclerView.ViewHolder viewHolder) {
                follow(url,id,viewHolder);
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
        Map<String, Object> map = new HashMap<>();
        map.put("num",10);
        map.put("page",page);
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

    public void follow(String url,int talk_id,RecyclerView.ViewHolder viewHolder) {
        Map<String, String> map = new HashMap<>();
        map.put("talk_id", talk_id + "");
        OkHttp.post(this, url, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(url.contains("follow_talk_no")) { //取消关注
                            for(int i = 0;i<list.size();i++) {
                                if(talk_id == list.get(i).id) {
                                    list.get(i).is_follow = 0;
                                    list.get(i).follow_num = list.get(i).follow_num -1;
                                    if(viewHolder instanceof AllHotGambitAdapter.GambitViewholder) {
                                        ((AllHotGambitAdapter.GambitViewholder)viewHolder).attentionTv.setText("关注");
                                        ((AllHotGambitAdapter.GambitViewholder)viewHolder).attentionTv.setBackgroundResource(R.drawable.rectangle_common_attention);
                                        ((AllHotGambitAdapter.GambitViewholder)viewHolder).attentionNumTv.setText(ShowNumUtil.showUnm(list.get(i).follow_num)+" 关注");
                                    }
                                }
                            }
                        }else {
                            for(int i = 0;i<list.size();i++) {
                                if(talk_id == list.get(i).id) {
                                    list.get(i).is_follow = 1;
                                    list.get(i).follow_num = list.get(i).follow_num +1;
                                    if(viewHolder instanceof AllHotGambitAdapter.GambitViewholder) {
                                        ((AllHotGambitAdapter.GambitViewholder)viewHolder).attentionTv.setText("已关注");
                                        ((AllHotGambitAdapter.GambitViewholder)viewHolder).attentionTv.setBackgroundResource(R.drawable.rectangle_common_no_attention);
                                        ((AllHotGambitAdapter.GambitViewholder)viewHolder).attentionNumTv.setText(ShowNumUtil.showUnm(list.get(i).follow_num)+" 关注");
                                    }
                                }
                            }
                        }
                        EventBus.getDefault().post(new EventTags.GambitRefresh());
                    }
                });
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void allGambitAttention(EventTags.AllGambitAttention allGambitAttention) {
        if(null != list) {
            for(int i = 0;i<list.size();i++) {
                if(list.get(i).id == allGambitAttention.getId()) {
                    if(allGambitAttention.getAttention()) { //关注
                        list.get(i).is_follow = 1;
                        list.get(i).follow_num = allGambitAttention.getAttentionNum();
                    }else { //取消关注
                        list.get(i).is_follow = 0;
                        list.get(i).follow_num = allGambitAttention.getAttentionNum();
                    }
                    hotAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void allGambitHotNum(EventTags.AllGambitHotNum allGambitHotNum) {
        if(null != list) {
             for(int i = 0;i<list.size();i++) {
                 if(list.get(i).id == allGambitHotNum.getId()) {
                     list.get(i).hot_num = allGambitHotNum.getNum();
                     hotAdapter.notifyDataSetChanged();
                 }
             }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @OnClick(R.id.backImg)
    public void onViewClick() {
        finish();
    }

}
