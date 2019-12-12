package songqiu.allthings.classification;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
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
import songqiu.allthings.adapter.classification.all.AllClassificationAdapter;
import songqiu.allthings.base.BaseActivity;
import songqiu.allthings.bean.AllClassificationBean;
import songqiu.allthings.bean.ClassificationSubitemBean;
import songqiu.allthings.http.BaseBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.http.OkHttp;
import songqiu.allthings.http.RequestCallBack;
import songqiu.allthings.util.ClickUtil;
import songqiu.allthings.util.LogUtil;
import songqiu.allthings.util.SharedPreferencedUtils;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.util.ToastUtil;
import songqiu.allthings.util.statusbar.StatusBarUtils;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/12/11
 *
 *类描述：全部分类
 *
 ********/
public class AllClassificationActivity extends BaseActivity {

    @BindView(R.id.backImg)
    ImageView backImg;
    @BindView(R.id.titleTv)
    TextView titleTv;
    @BindView(R.id.rightTv)
    TextView rightTv;
    @BindView(R.id.line)
    LinearLayout line;
    @BindView(R.id.reyclerView)
    RecyclerView reyclerView;
    @BindView(R.id.shadowLayout)
    LinearLayout shadowLayout;

    List<AllClassificationBean> item;
    AllClassificationAdapter adapter;
    //选中的分类
    ArrayList<String> arryLabels = new ArrayList<String>();

    @Override
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_all_classification);
    }

    @Override
    public void init() {
        boolean dayModel = SharedPreferencedUtils.getBoolean(this, SharedPreferencedUtils.dayModel, true);
        modeUi(dayModel);
        titleTv.setText("全部分类");
        rightTv.setVisibility(View.VISIBLE);
        rightTv.setText("（0/5）确定");
        rightTv.setTextColor(getResources().getColor(R.color.normal_color));
        initRecycle();
        getAllclassification();
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
        item =  new ArrayList<>();
        adapter = new AllClassificationAdapter(this,item);
        //标题title一列显示  子项四列
        final GridLayoutManager manager = new GridLayoutManager(this, 4,GridLayoutManager.VERTICAL,false);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
              @Override
              public int getSpanSize(int position) {
                  return adapter.isSectionHeaderPosition(position) ? 4 : 1;
                  }
              });
        reyclerView.setLayoutManager(manager);
        reyclerView.setAdapter(adapter);

        adapter.setClassificationItemListener(new AllClassificationAdapter.ClassificationItemListener() {
            @Override
            public void isSelect(ClassificationSubitemBean classificationSubitemBean, boolean isSelect,int selectNum) {
               classificationSubitemBean.isClick = isSelect;
               adapter.notifyDataSetChanged();
               rightTv.setText("（"+selectNum+"/5）确定");
               if(classificationSubitemBean.isClick) {
                    arryLabels.add(classificationSubitemBean.name);
                }else {
                   if(null != arryLabels && 0 != arryLabels.size()&& arryLabels.contains(classificationSubitemBean.name)) {
                       arryLabels.remove(classificationSubitemBean.name);
                   }
               }
            }
        });
    }

    public void getAllclassification() {
        Map<String, String> map = new HashMap<>();
        OkHttp.post(this, HttpServicePath.URL_ALL_LABELS, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        String data = gson.toJson(baseBean.data);
                        if (StringUtil.isEmpty(data)) return;
                        List<AllClassificationBean> allClassificationList = gson.fromJson(data, new TypeToken<List<AllClassificationBean>>() {}.getType());
                        item.addAll(allClassificationList);
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    @OnClick({R.id.backImg,R.id.rightTv})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.backImg:
                finish();
                break;
            case R.id.rightTv:
                if(ClickUtil.onClick()) {
                    if(null == arryLabels || 0==arryLabels.size()) {
                        ToastUtil.showToast(this,"请选择分类");
                        return;
                    }
                    Intent intent = new Intent(AllClassificationActivity.this,CurrentClassificationActivity.class);
                    intent.putStringArrayListExtra("arryLabels",arryLabels);
                    startActivity(intent);
                }
                break;
        }
    }

}
