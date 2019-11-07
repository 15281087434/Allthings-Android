package songqiu.allthings.mine;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import songqiu.allthings.R;
import songqiu.allthings.adapter.WithdrawRecordAdapter;
import songqiu.allthings.base.BaseActivity;
import songqiu.allthings.bean.WithdrawRecordBean;
import songqiu.allthings.http.BaseBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.http.OkHttp;
import songqiu.allthings.http.RequestCallBack;
import songqiu.allthings.util.SharedPreferencedUtils;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.util.statusbar.StatusBarUtils;
import songqiu.allthings.util.theme.ThemeManager;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/8/23
 *
 *类描述：提现记录 1、金币  2、现金
 *
 ********/
public class WithdrawRecordActivity extends BaseActivity {


    @BindView(R.id.backImg)
    ImageView backImg;
    @BindView(R.id.titleTv)
    TextView titleTv;
    @BindView(R.id.recycl)
    RecyclerView recycl;
    @BindView(R.id.shadowLayout)
    LinearLayout shadowLayout;
    int withdrawType;
    WithdrawRecordAdapter adapter;
    List<WithdrawRecordBean> list;
    @BindView(R.id.iv_icon)
    ImageView ivIcon;
    @BindView(R.id.tv_message)
    TextView tvMessage;
    @BindView(R.id.emptyLayout)
    LinearLayout emptyLayout;

    @Override
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_withdraw_record);
    }

    @Override
    public void init() {
        boolean dayModel = SharedPreferencedUtils.getBoolean(this, SharedPreferencedUtils.dayModel, true);
        modeUi(dayModel);
        withdrawType = getIntent().getIntExtra("withdrawType", 0);
        if (withdrawType == 1) {
            titleTv.setText("金币提现记录");
        } else {
            titleTv.setText("现金提现记录");
        }
        initRecycl();
        getData();
    }

    public void modeUi(boolean isDay) {
        if (isDay) {
            shadowLayout.setVisibility(View.GONE);
            StatusBarUtils.with(this)
                    .setColor(getResources().getColor(ThemeManager.getCurrentThemeRes(this, R.color.FFF9FAFD)))
                    .init()
                    .setStatusTextColorAndPaddingTop(true, this);
        } else {
            shadowLayout.setVisibility(View.VISIBLE);
            StatusBarUtils.with(this)
                    .setColor(getResources().getColor(ThemeManager.getCurrentThemeRes(this, R.color.trans_6)))
                    .init()
                    .setStatusTextColorAndPaddingTop(true, this);
        }
    }

    public void initRecycl() {
        list = new ArrayList<>();
        adapter = new WithdrawRecordAdapter(this, list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycl.setLayoutManager(linearLayoutManager);
        recycl.setAdapter(adapter);
    }

    public void getData() {
        list.clear();
        Map<String, String> map = new HashMap<>();
        map.put("type", withdrawType + "");
        OkHttp.post(this, HttpServicePath.URL_TAKE_LOG, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        String data = gson.toJson(baseBean.data);
                        if (StringUtil.isEmpty(data)) return;
                        List<WithdrawRecordBean> withdrawRecordList = gson.fromJson(data, new TypeToken<List<WithdrawRecordBean>>() {
                        }.getType());
                        if(null != withdrawRecordList && 0!= withdrawRecordList.size()) {
                            emptyLayout.setVisibility(View.GONE);
                            recycl.setVisibility(View.VISIBLE);
                            list.addAll(withdrawRecordList);
                            adapter.notifyDataSetChanged();
                        }else {
                            tvMessage.setText("暂时没有提现记录哦!");
                            emptyLayout.setVisibility(View.VISIBLE);
                            recycl.setVisibility(View.GONE);
                            ivIcon.setImageResource(R.mipmap.state_empty_withdrawrecord);
                        }
                    }
                });
            }
        });
    }

    @OnClick({R.id.backImg, R.id.rightTv})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.backImg:
                finish();
                break;
        }

    }

}
