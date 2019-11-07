package songqiu.allthings.location;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.contrarywind.view.WheelView;
import com.google.gson.Gson;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;
import songqiu.allthings.Event.EventTags;
import songqiu.allthings.R;
import songqiu.allthings.activity.MainActivity;
import songqiu.allthings.base.BaseActivity;
import songqiu.allthings.bean.ProvinceBean;
import songqiu.allthings.util.ClickUtil;
import songqiu.allthings.util.JsUtils;
import songqiu.allthings.util.LocationUtils;
import songqiu.allthings.util.LogUtil;
import songqiu.allthings.util.SharedPreferencedUtils;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.util.ToastUtil;
import songqiu.allthings.util.statusbar.StatusBarUtils;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/9/3
 *
 *类描述：
 *
 ********/
public class LocationActivity extends BaseActivity {
    @BindView(R.id.backImg)
    ImageView backImg;
    @BindView(R.id.titleTv)
    TextView titleTv;
    @BindView(R.id.rightTv)
    TextView rightTv;
    @BindView(R.id.line)
    LinearLayout line;
    @BindView(R.id.currentCity)
    TextView currentCity;
    @BindView(R.id.locationTv)
    TextView locationTv;
    @BindView(R.id.chooseTv)
    TextView chooseTv;
    @BindView(R.id.shadowLayout)
     LinearLayout shadowLayout;

    Thread thread;
    ArrayList<ProvinceBean> options1Items = new ArrayList<>();
    ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private OptionsPickerView mPvOptions;
    private OptionsPickerView mProvincePv;
    private String mProvince;
    private String mCity;
    boolean canLocation;

    @Override
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_location);
    }

    @Override
    public void init() {
        boolean dayModel = SharedPreferencedUtils.getBoolean(this,SharedPreferencedUtils.dayModel,true);
        modeUi(dayModel);
        titleTv.setText("地址");
        rightTv.setText("确定");
        rightTv.setVisibility(View.VISIBLE);
        rightTv.setTextColor(getResources().getColor(R.color.normal_color));
        String locationCity = SharedPreferencedUtils.getString(this,"LOCATION_CITY");
        currentCity.setText(locationCity);
        applyPermission();
        initProvincePv();
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

    public void applyPermission() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions
                .requestEach(
                        Manifest.permission.ACCESS_FINE_LOCATION
                )
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.name.equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                            if (permission.granted) {
                                canLocation = true;
                            } else if (permission.shouldShowRequestPermissionRationale) {
                                // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
//                                Log.d(TAG, permission.name + " is denied. More info should be provided.");
//                                alertPermissionDialog("请在设置-应用-利卡-权限中开启定位权限");
                                canLocation = false;
                            } else {
                                // 用户拒绝了该权限，并且选中『不再询问』
//                                Log.d(TAG, permission.name + " is denied.");
//                                alertPermissionDialog("请在设置-应用-利卡-权限中开启定位权限");
                                canLocation = false;
                            }
                        }
                    }
                });

    }

    public void initLocation() {
        LocationUtils locationUtils = new LocationUtils();
        locationUtils.initLocation(this, new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation loc) {
                if (loc != null) {
                    //解析定位结果
                    String cityName = locationUtils.getLocationCity(loc);
                    String city = null;
                    if(!StringUtil.isEmpty(cityName)) {
                        ToastUtil.showToast(LocationActivity.this,"定位成功");
                        if(cityName.contains("市")) {
                            city = cityName.substring(0,cityName.length()-1);
                        }else {
                            city = cityName;
                        }
                        SharedPreferencedUtils.setString(LocationActivity.this, "LOCATION_CITY", city);
                        currentCity.setText(city);
                    }else {
                        ToastUtil.showToast(LocationActivity.this,"定位失败，请手动选择城市");
                    }
                }
            }

        });
    }


    /**
     * 初始化 省 市 选择器
     */
    private void initProvincePv() {
        if (thread == null) {//如果已创建就不再重新创建子线程了
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    // 子线程中解析省市区数据
                    initJsonData();
                }
            });
            thread.start();
        }
    }

    private void goToSetting() {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.setClassName("com.android.settings", "com.android.settings.ManageApplications");
        startActivityForResult(intent,100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100) {
            applyPermission();
        }
    }

    /**
     * 解析省市json数据
     */
    private void initJsonData() {
        String JsonData = JsUtils.getJson(this, "province.json");//获取assets目录下的json文件数据
        ArrayList<ProvinceBean> jsonBean = parseData(JsonData);//用Gson 转成实体
        options1Items = jsonBean;
        for (int i = 0; i < jsonBean.size(); i++) {//遍历省份
            ArrayList<String> CityList = new ArrayList<>();//该省的城市列表（第二级）
            for (int c = 0; c < jsonBean.get(i).getCityList().size(); c++) {//遍历该省份的所有城市
                String CityName = jsonBean.get(i).getCityList().get(c).getName();
                CityList.add(CityName);//添加城市
            }
            options2Items.add(CityList);
        }
    }

    public ArrayList<ProvinceBean> parseData(String result) {//Gson 解析
        ArrayList<ProvinceBean> detail = new ArrayList<>();
        try {
            JSONArray data = new JSONArray(result);
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                ProvinceBean entity = gson.fromJson(data.optJSONObject(i).toString(), ProvinceBean.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }


    private void showProvincePv() {
        if (options1Items == null || options1Items.size() == 0) {
            ToastUtil.showToast("数据解析错误");
            return;
        }
        mProvincePv = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String tx = options1Items.get(options1).getPickerViewText() +
                        options2Items.get(options1).get(options2);
                mProvince = options1Items.get(options1).getPickerViewText();
                mCity = options2Items.get(options1).get(options2);
                if(mCity.contains("市")) {
                    mCity = mCity.substring(0,mCity.length()-1);
                }
                if(!canLocation) {//不允许定位时存放当前位置到本地
                    SharedPreferencedUtils.setString(LocationActivity.this, "LOCATION_CITY", mCity);
                }
                currentCity.setText(mCity);
            }
        })
                .setTitleText("城市选择")
                .setDividerColor(Color.BLACK)
                .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                .setContentTextSize(20)
                .setDividerType(WheelView.DividerType.FILL)
                .build();
        mProvincePv.setPicker(options1Items, options2Items);//三级选择器
        mProvincePv.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @OnClick({R.id.backImg,R.id.chooseTv,R.id.locationTv,R.id.rightTv})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.backImg:
                finish();
                break;
            case R.id.chooseTv:
                if (ClickUtil.onClick()) {
                    showProvincePv();
                }
                break;
            case R.id.locationTv:
                if (ClickUtil.onClick()) {
                    if(canLocation) {
                        initLocation();
                    }else {
                        goToSetting();
                        ToastUtil.showToast(LocationActivity.this,"请在设置-应用-见怪-权限中开启定位权限");
                    }
                }
                break;
            case R.id.rightTv:
                if (ClickUtil.onClick()) {
                    String city = currentCity.getText().toString();
                    EventBus.getDefault().post(new EventTags.ChooseCity(city));
                    EventBus.getDefault().post(new EventTags.TransmitCity(city));
                    finish();
                }
                break;
        }
    }

}
