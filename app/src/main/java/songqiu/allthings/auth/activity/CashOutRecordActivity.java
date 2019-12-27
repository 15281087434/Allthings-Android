package songqiu.allthings.auth.activity;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import songqiu.allthings.R;
import songqiu.allthings.auth.adapter.CashOutRecordAdapter;
import songqiu.allthings.auth.bean.CashOutRecordBean;
import songqiu.allthings.base.BaseActivity;
import songqiu.allthings.http.BaseBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.http.OkHttp;
import songqiu.allthings.http.RequestCallBack;
import songqiu.allthings.util.SharedPreferencedUtils;
import songqiu.allthings.util.ToastUtil;
import songqiu.allthings.util.statusbar.StatusBarUtils;

/**
 * create by: linyinjianying
 * time:
 * e_mail:734090232@qq.com
 * description:稿酬提现列表页
 */
public class CashOutRecordActivity extends BaseActivity {
    @BindView(R.id.backImg)
    ImageView backImg;
    @BindView(R.id.titleTv)
    TextView titleTv;
    @BindView(R.id.rightTv)
    TextView rightTv;
    @BindView(R.id.line)
    LinearLayout line;
    @BindView(R.id.rv_list)
    RecyclerView rvList;
    @BindView(R.id.refresh)
    SmartRefreshLayout srl;
    private CashOutRecordAdapter adapter;
    private ArrayList<CashOutRecordBean> list=new ArrayList<>();
    private int num = 10, page = 1;
    @BindView(R.id.shadowLayout)
    LinearLayout shadowLayout;
    @Override
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_cashout_record);
        ButterKnife.bind(this);
        titleTv.setText("稿酬提现记录");
        boolean dayModel = SharedPreferencedUtils.getBoolean(this,SharedPreferencedUtils.dayModel,true);
        modeUi(dayModel);
        srl.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                page=1;
                getOrderList();
            }
        });
        srl.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                page++;
                getOrderList();
            }
        });
        rvList.setLayoutManager(new LinearLayoutManager(this));
        rvList.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        adapter=new CashOutRecordAdapter(this,list);
        rvList.setAdapter(adapter);
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
    @Override
    public void init() {
        getOrderList();
    }
//{"code":"200","msg":"返回成功","data":[{"order_id":"A884D9DE7AE5F31D44BC2E8BD16006C4","userid":"449","type":"3","num":9,"zfb":"15882205742","zfb_name":"李洪波","created":"1577270807","flag":"1","pay_name":"进行中"}]}

    private void getOrderList() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("page", page + "");
        map.put("num", num + "");
        OkHttp.post(this, srl,HttpServicePath.URL_GC_ORDER, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(baseBean!=null){
                            Gson gson=new Gson();
                            String data=gson.toJson(baseBean.data);
                            Log.e("start",data);

                            JsonParser jsonParser = new JsonParser();
                            JsonArray jsonElements = jsonParser.parse(data).getAsJsonArray();//获取JsonArray对象
                            ArrayList<CashOutRecordBean> beans = new ArrayList<>();
                            for (JsonElement bean : jsonElements) {
                                CashOutRecordBean bean1 = gson.fromJson(bean, CashOutRecordBean.class);//解析
                                beans.add(bean1);
                            }

                            if(beans!=null) {
                                if (beans==null||beans.size() <= num) {
                                    srl.setEnableLoadmore(false);
                                }
                                if (page == 1) {
                                    list.clear();
                                    list.addAll(beans);
                                } else {
                                    list.addAll(beans);
                                }
                                adapter.notifyDataSetChanged();

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
