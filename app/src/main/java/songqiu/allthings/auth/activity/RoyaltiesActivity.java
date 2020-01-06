package songqiu.allthings.auth.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import songqiu.allthings.R;
import songqiu.allthings.auth.adapter.RoyaltiesAdapter;
import songqiu.allthings.auth.bean.GcLogBean;
import songqiu.allthings.base.BaseActivity;
import songqiu.allthings.http.BaseBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.http.OkHttp;
import songqiu.allthings.http.RequestCallBack;
import songqiu.allthings.util.ClickUtil;
import songqiu.allthings.util.LogUtil;
import songqiu.allthings.util.SharedPreferencedUtils;
import songqiu.allthings.util.statusbar.StatusBarUtils;

/**
 * create by: linyinjianying
 * time:
 * e_mail:734090232@qq.com
 * description:稿酬收入列表
 */
public class RoyaltiesActivity extends BaseActivity {
    @BindView(R.id.backImg)
    ImageView backImg;
    @BindView(R.id.titleTv)
    TextView titleTv;
    @BindView(R.id.rightTv)
    TextView rightTv;
    @BindView(R.id.line)
    LinearLayout line;
    @BindView(R.id.emptyLayout)
    LinearLayout emptyLayout;
    @BindView(R.id.rv_list)
    RecyclerView rvList;
    @BindView(R.id.tv_time_choose)
    TextView tvTimeChoose;
    @BindView(R.id.tv_money_all)
    TextView tvMoneyAll;
    ArrayList<GcLogBean.DataBean> list = new ArrayList<>();
    RoyaltiesAdapter adapter;
    @BindView(R.id.refresh)
    SmartRefreshLayout refresh;
    private String start="";
    private int num = 10, page = 1;
    ArrayList<String> years;
    ArrayList<String> mouths;
    TimePickerView timePicker;
    Calendar startTime,endTime;
    @BindView(R.id.shadowLayout)
    LinearLayout shadowLayout;
    @Override
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_royalties_list);
        ButterKnife.bind(this);
        titleTv.setText("稿酬收入记录");
        initRecyclerView();
        startTime=Calendar.getInstance();
        startTime.set(2018,1,0);
        endTime=Calendar.getInstance();
        boolean dayModel = SharedPreferencedUtils.getBoolean(this,SharedPreferencedUtils.dayModel,true);
        modeUi(dayModel);
        tvTimeChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClickUtil.onClick()) {
                    timePicker = new TimePickerBuilder(RoyaltiesActivity.this, new OnTimeSelectListener() {
                        @Override
                        public void onTimeSelect(Date date, View v) {
                            start=(date.getYear()+1900)+"-"+(date.getMonth()+1);
                            tvTimeChoose.setText(start);
                            timePicker.dismiss();
                            page=1;
                            getGcLog();
                        }
                    })
                            .setTitleText("")
                            .setDividerColor(Color.BLACK)
                            .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                            .setContentTextSize(15)
                            .setRangDate(startTime,endTime)
                            .setType(new boolean[]{true, true, false, false, false, false})
                            .build();

                    timePicker.show();
                }
            }
        });
    }

    private void initRecyclerView() {
        rvList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RoyaltiesAdapter(this, list);
        rvList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rvList.setAdapter(adapter);
        refresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                page=1;
                getGcLog();
            }
        });
        refresh.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                page++;
                getGcLog();
            }
        });
    }

    @Override
    public void init() {
        Date date= new Date();
        start=(date.getYear()+1900)+"-"+(date.getMonth()+1);
        tvTimeChoose.setText(start);

        getGcLog();
    }
    public void modeUi(boolean isDay) {
        if (isDay) {
            shadowLayout.setVisibility(View.GONE);
            StatusBarUtils.with(this)
                    .setColor(getResources().getColor(R.color.FFF9FAFD))
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
    //获取稿酬列表
    private void getGcLog() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("start", start);
        map.put("num", num);
        map.put("page", page);
        OkHttp.post(this, refresh,HttpServicePath.URL_GCLOGS, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(baseBean!=null){
                            Gson gson=new Gson();
                            String data=gson.toJson(baseBean.data);
                            Log.e("start",data);
                            if(TextUtils.isEmpty(data)){
                                return;
                            }
                            GcLogBean gcLogBean=gson.fromJson(data,GcLogBean.class);
                            if(gcLogBean!=null) {
                                if (gcLogBean.getLogdata()==null||gcLogBean.getLogdata().size() <= num) {
                                    refresh.setEnableLoadmore(false);
                                }
                                if (page == 1) {
                                    list.clear();
                                    if(null == gcLogBean.getLogdata() || gcLogBean.getLogdata().size() == 0) {
                                        refresh.setVisibility(View.GONE);
                                        emptyLayout.setVisibility(View.VISIBLE);
                                    }else {
                                        refresh.setVisibility(View.VISIBLE);
                                        emptyLayout.setVisibility(View.GONE);
                                    }
                                    list.addAll(gcLogBean.getLogdata());
                                } else {
                                    list.addAll(gcLogBean.getLogdata());
                                }
                                adapter.notifyDataSetChanged();
                                tvMoneyAll.setText("收入￥" + gcLogBean.getNum() + "元");
                            }
                        }
                    }
                });

            }
        });
    }



    @OnClick(R.id.backImg)
    public void onBack(){
        finish();
    }
}
