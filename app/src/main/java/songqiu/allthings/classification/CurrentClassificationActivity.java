package songqiu.allthings.classification;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

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
import songqiu.allthings.adapter.classification.current.ClassificationAdapter;
import songqiu.allthings.adapter.classification.current.LabelsAdapter;
import songqiu.allthings.base.BaseActivity;
import songqiu.allthings.bean.ClassificationSubitemBean;
import songqiu.allthings.bean.HomeSubitemBean;
import songqiu.allthings.bean.SearchHistoryBean;
import songqiu.allthings.bean.UnLikeBean;
import songqiu.allthings.db.LabelsSQLiteHelper;
import songqiu.allthings.http.BaseBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.http.OkHttp;
import songqiu.allthings.http.RequestCallBack;
import songqiu.allthings.util.SharedPreferencedUtils;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.util.ToastUtil;
import songqiu.allthings.util.VibratorUtil;
import songqiu.allthings.util.statusbar.StatusBarUtils;
import songqiu.allthings.view.DialogDeleteCommon;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/12/12
 *
 *类描述：
 *
 ********/
public class CurrentClassificationActivity extends BaseActivity {

    @BindView(R.id.titleTv)
    TextView titleTv;
    @BindView(R.id.rightTv)
    TextView rightTv;
    @BindView(R.id.line)
    LinearLayout line;
    @BindView(R.id.labelsRv)
    RecyclerView labelsRv;
    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout smartRefreshLayout;
    @BindView(R.id.reyclerView)
    RecyclerView reyclerView;
    @BindView(R.id.shadowLayout)
    LinearLayout shadowLayout;
    @BindView(R.id.emptyLayout)
    LinearLayout emptyLayout;

    ArrayList<String> arryLabels;
    LabelsAdapter labelsAdapter;

    int pageNo = 1;
    List<HomeSubitemBean> item ;
    ClassificationAdapter adapter;

    @Override
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_current_classification);
    }

    @Override
    public void init() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        boolean dayModel = SharedPreferencedUtils.getBoolean(this, SharedPreferencedUtils.dayModel, true);
        modeUi(dayModel);
        titleTv.setText("当前分类");
        line.setVisibility(View.GONE);
        arryLabels = getIntent().getStringArrayListExtra("arryLabels");
        initRecycle();
        getData(pageNo,false);
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
                    .setColor(getResources().getColor(R.color.trans_6))
                    .init()
                    .setStatusTextColorAndPaddingTop(true, this);
        }
    }

    public void initRecycle() {
        //标签
        labelsAdapter = new LabelsAdapter(this,arryLabels);
        GridLayoutManager manager = new GridLayoutManager(this, 5,GridLayoutManager.VERTICAL,false);
        labelsRv.setLayoutManager(manager);
        labelsRv.setAdapter(labelsAdapter);
        //内容
        item = new ArrayList<>();
        adapter = new ClassificationAdapter(this,item);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        reyclerView.setLayoutManager(linearLayoutManager);
        reyclerView.setAdapter(adapter);
        adapter.setClassificationItemListener(new ClassificationAdapter.ClassificationItemListener() {
            @Override
            public void delete(int position) {
                if(null != item && 0!= item.size()) {
                    getUnLikeParameter(item.get(position).articleid,item.get(position).type,position);
                }
            }
        });
    }

    public void getData(int page,boolean ringDown) {
        Map<String,Object> map = new HashMap<>();
        map.put("labels",arryLabels);
        map.put("num",10);
        map.put("page",page);
        OkHttp.post(this,smartRefreshLayout,HttpServicePath.URL_LABELS_SEARCH, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Gson gson = new Gson();
                            String data = gson.toJson(baseBean.data);
                            if(StringUtil.isEmpty(data)) return;
                            List<HomeSubitemBean> labelSearchList = gson.fromJson(data, new TypeToken<List<HomeSubitemBean>>() {}.getType());
                            if(pageNo ==1) {
                                item.clear();
                                if(null == labelSearchList || 0 == labelSearchList.size()) {
                                    emptyLayout.setVisibility(View.VISIBLE);
                                    reyclerView.setVisibility(View.GONE);
                                }else {
                                    emptyLayout.setVisibility(View.GONE);
                                    reyclerView.setVisibility(View.VISIBLE);
                                }
                            }
                            if(null != labelSearchList && 0!=labelSearchList.size()) {
                                item.addAll(labelSearchList);
                                adapter.notifyDataSetChanged();
                            }
                            if(ringDown) {
                                new Handler().postDelayed(new Runnable(){
                                    public void run() {
                                        //execute the task
                                        VibratorUtil.ringDown(CurrentClassificationActivity.this);
                                    }
                                }, 500);
                            }
                        }
                    });
            }
        });
    }

    //不喜欢列表
    public void getUnLikeParameter(int articleid,int classType,int position) {
        Map<String, String> map = new HashMap<>();
        map.put("articleid",String.valueOf(articleid));
        map.put("type",classType+"");
        OkHttp.post(this, HttpServicePath.URL_REPORT_LIST, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        String data = gson.toJson(baseBean.data);
                        if (StringUtil.isEmpty(data)) return;
                        UnLikeBean unLikeBean = gson.fromJson(data, UnLikeBean.class);
                        if(null == unLikeBean) return;
                        initDialog(unLikeBean,position);
                    }
                });
            }
        });
    }

    public void doDeletel(int position,int bid,int mid) {
        unLike(bid,mid,item.get(position).type);
        item.remove(position);
        adapter.notifyDataSetChanged();
        ToastUtil.showToast(this,"将减少推荐类似内容");
    }

    //调用不喜欢接口
    public void unLike(int bid,int mid,int type) {
        //bid 1=不敢兴趣，2=反馈垃圾内容，3=拉黑作者
        //mid 文章视频id
        //type 1=文章，2=视频
        Map<String, String> map = new HashMap<>();
        map.put("bid",bid+"");
        map.put("mid",mid+"");
        map.put("type",type+"");
        OkHttp.post(this, HttpServicePath.URL_UNLIKE, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {

            }
        });
    }

    public void initDialog(UnLikeBean unLikeBean,int position) {
            DialogDeleteCommon dialog = new DialogDeleteCommon(this,unLikeBean,true);
            dialog.showDialog();
            dialog.setOnItemClickListener(new DialogDeleteCommon.OnItemClick() {
                @Override
                public void onWhichItemClick(int pos) {
                    switch (pos) {
                        case 1:
                            if(null == item || 0 == item.size()) return;
                            doDeletel(position,1,item.get(position).articleid);
                            break;
                        case 2:
                            if(null == item || 0 == item.size()) return;
                            doDeletel(position,2,item.get(position).articleid);
                            break;
                        case 3:
                            if(null == item || 0 == item.size()) return;
                            doDeletel(position,3,item.get(position).articleid);
                            break;
                        case 4:
                            if(null == item || 0 == item.size()) return;
                            doDeletel(position,4,item.get(position).articleid);
                            break;
                        case 5:

                            break;
                    }
                }
            });
    }

    //接受到收藏/取消收藏的通知
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void collectEvent(EventTags.CollectEvent collectEvent) {
        if(null == item || 0 == item.size()) return;
        for(int i = 0;i<item.size();i++) {
            if(item.get(i).articleid == collectEvent.getArticleid()) {
                if(collectEvent.getCollect()) {
                    item.get(i).collect_num =  item.get(i).collect_num + 1;
                }else {
                    item.get(i).collect_num =  item.get(i).collect_num - 1>0?item.get(i).collect_num:0;
                }
                adapter.notifyDataSetChanged();
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
