package songqiu.allthings.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.functions.Consumer;
import songqiu.allthings.BuildConfig;
import songqiu.allthings.Event.EventTags;
import songqiu.allthings.R;
import songqiu.allthings.application.MyApplication;
import songqiu.allthings.base.BaseActivity;
import songqiu.allthings.bean.AdvertiseBean;
import songqiu.allthings.bean.UserInfoBean;
import songqiu.allthings.bean.VersionBean;
import songqiu.allthings.constant.SnsConstants;
import songqiu.allthings.http.BaseBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.http.OkHttp;
import songqiu.allthings.http.RequestCallBack;
import songqiu.allthings.iterface.DialogPrivacyListener;
import songqiu.allthings.iterface.DialogUploadVersionListener;
import songqiu.allthings.util.LocationUtils;
import songqiu.allthings.util.LogUtil;
import songqiu.allthings.util.NetWorkUtil;
import songqiu.allthings.util.SharedPreferencedUtils;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.util.ToastUtil;
import songqiu.allthings.util.statusbar.StatusBarUtils;
import songqiu.allthings.util.upload.UpdateManager;
import songqiu.allthings.view.DialogPermission;
import songqiu.allthings.view.DialogPrivacyExplain;
import songqiu.allthings.view.DialogUploadVersion;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/8/8
 *
 *类描述：启动页
 *
 ********/
public class GuideActivity extends BaseActivity {

    VersionBean versionBean;

    @Override
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_guide);
    }

    @Override
    public void init() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        StatusBarUtils.with(GuideActivity.this).init().setStatusTextColorWhite(true, GuideActivity.this);
        getDelRd();
        decidePrivacyExplainFirst();
        SharedPreferencedUtils.setString(this,SharedPreferencedUtils.USER_ICON,"");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void getDelRd() {
        Map<String, String> map = new HashMap<>();
        OkHttp.post(this, HttpServicePath.URL_DEL_RD, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
            }
        });
    }

    public void getVersion() {
        if (!NetWorkUtil.isNetworkConnected(this)) {
            getAdvertise();
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("osversion","2");
        map.put("update_version",MyApplication.getInstance().versionName);//MyApplication.getInstance().versionName
        OkHttp.post(this, HttpServicePath.URL_VERSION, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        String data = gson.toJson(baseBean.data);
                        //{"code":"200","msg":"返回成功","data":null}
                        if (StringUtil.isEmpty(data)) {
                            getAdvertise();
//                            toMainActivity();
                        }else {
                            versionBean = gson.fromJson(data, VersionBean.class);
                            initUploadVersionDialog(versionBean);
                        }
                    }
                });
            }
        });
    }

    public void initUploadVersionDialog(VersionBean versionBean) {
        if(null == versionBean) return;
        String version = "V "+versionBean.current_version;
        DialogUploadVersion dialog = new DialogUploadVersion(this,versionBean.content,version,versionBean.type);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        dialog.setDialogUploadVersionListener(new DialogUploadVersionListener() {
            @Override
            public void toUpload() {
             UpdateManager um = new UpdateManager(versionBean.current_version);
             um.UpdateInfo(versionBean.url, GuideActivity.this);
            }

            @Override
            public void close() {
                getAdvertise();
            }
        });

    }

    public void toGuideAdvertising(AdvertiseBean advertiseBean) {
        new Handler().postDelayed(new Runnable(){
            public void run() {
                //execute the task
                Intent intent = new Intent(GuideActivity.this,GuideAdvertisingActivity.class);
                intent.putExtra("advertiseBean",advertiseBean);
                startActivity(intent);
                finish();
            }
        }, 1500);
    }

    public void toMainActivity() {
        new Handler().postDelayed(new Runnable(){
            public void run() {
                //execute the task
                Intent intent = new Intent(GuideActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000);
    }


    public void getAdvertise() {
        if (!NetWorkUtil.isNetworkConnected(this)) {
            toMainActivity();
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("category",1+"");
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
                        if(null==advertiseBeanListBean || 0==advertiseBeanListBean.size()) {
                            toMainActivity();
                            return;
                        }
                        AdvertiseBean advertiseBean = advertiseBeanListBean.get(0);
                        if(null == advertiseBean) {
                            toMainActivity();
                            return;
                        }
                        toGuideAdvertising(advertiseBean);
                    }
                });
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void attention(EventTags.Advertising advertising) {
        toMainActivity();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void decidePrivacyExplainFirst() {
        //判断是否第一次进入应用
        boolean first = SharedPreferencedUtils.getBoolean(this,SharedPreferencedUtils.FIRST_ENTER,true);
        if(first) {
            DialogPrivacyExplain dialogPrivacyExplain = new DialogPrivacyExplain(this);
            dialogPrivacyExplain.setCanceledOnTouchOutside(false);
            dialogPrivacyExplain.setCancelable(false);
            dialogPrivacyExplain.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialogPrivacyExplain.show();
            dialogPrivacyExplain.setDialogPrivacyListener(new DialogPrivacyListener() {
                    @Override
                    public void cancel() {
                        finish();
                    }

                    @Override
                    public void sure() {
                        SharedPreferencedUtils.setBoolean(GuideActivity.this,SharedPreferencedUtils.FIRST_ENTER,false);
                        decidePermissionFirst();
                    }
                });
        }else {
            decidePermissionFirst();
        }
    }

    public void decidePermissionFirst() {
        boolean first = SharedPreferencedUtils.getBoolean(this,SharedPreferencedUtils.FIRST_ENTER_GUIDE,true);
        if(first) {
            //判断是否第一次进入应用
            DialogPermission dialogPermission = new DialogPermission(this);
            dialogPermission.setCanceledOnTouchOutside(false);
            dialogPermission.setCancelable(false);
            dialogPermission.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialogPermission.show();
            dialogPermission.setDialogPrivacyListener(new DialogPrivacyListener() {
                @Override
                public void cancel() {
                    finish();
                }

                @Override
                public void sure() {
                    SharedPreferencedUtils.setBoolean(GuideActivity.this,SharedPreferencedUtils.FIRST_ENTER_GUIDE,false);
                    applyPermission();
                }
            });
        }else {
            applyPermission();
        }
    }

    public void applyPermission() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions
                .requestEach(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.name.equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                            if (permission.granted) {
                                initLocation();
                            } else if (permission.shouldShowRequestPermissionRationale) {
                                // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
//                                Log.d(TAG, permission.name + " is denied. More info should be provided.");
//                                alertPermissionDialog("请在设置-应用-利卡-权限中开启定位权限");
                            } else {
                                // 用户拒绝了该权限，并且选中『不再询问』
//                                Log.d(TAG, permission.name + " is denied.");
//                                alertPermissionDialog("请在设置-应用-利卡-权限中开启定位权限");
                            }
                        }else  if (permission.name.equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            if (permission.granted) {
                                getVersion();
                            } else if (permission.shouldShowRequestPermissionRationale) {
                                // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
//                                Log.d(TAG, permission.name + " is denied. More info should be provided.");
//                                alertPermissionDialog("请在设置-应用-利卡-权限中开启定位权限");
                                ToastUtil.showToast(GuideActivity.this,"请在设置-应用-见怪-权限中开启读写手机储存权限");
                            } else {
                                // 用户拒绝了该权限，并且选中『不再询问』
//                                Log.d(TAG, permission.name + " is denied.");
//                                alertPermissionDialog("请在设置-应用-利卡-权限中开启定位权限");
                                ToastUtil.showToast(GuideActivity.this,"请在设置-应用-见怪-权限中开启读写手机储存权限");
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
                        if(cityName.contains("市")) {
                            city = cityName.substring(0,cityName.length()-1);
                        }else {
                            city = cityName;
                        }
                        SharedPreferencedUtils.setString(GuideActivity.this, "LOCATION_CITY", city);
                        EventBus.getDefault().post(new EventTags.TransmitCity(city));
                    }
                }
            }

        });
    }


}
