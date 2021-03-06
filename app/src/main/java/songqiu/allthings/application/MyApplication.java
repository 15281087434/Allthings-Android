package songqiu.allthings.application;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.multidex.MultiDex;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.bilibili.boxing.BoxingCrop;
import com.bilibili.boxing.BoxingMediaLoader;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.mob.MobSDK;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreater;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.tencent.bugly.crashreport.CrashReport;

import songqiu.allthings.R;
import songqiu.allthings.util.GlideIBoxingMediaLoader;
import songqiu.allthings.util.LogUtil;
import songqiu.allthings.util.SharedPreferencedUtils;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.util.UCropIBoxingCrop;


/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/8/6
 *
 *类描述：
 *
 ********/
public class MyApplication extends Application {

    public static MyApplication myApplication;

//    public OkHttpClient okHttpClient;
    //设备id
    public String andoridId="";
    //版本号
    public String versionName;
    //渠道号 官方安卓 1、OPPO 2、vivo 3、华为 4、应用宝 5 、UC 6、360 7、百度 8、小米 9 、魅族 10
    public String channel="2";


    public static MyApplication getInstance() {
        return myApplication;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = this;

        String strCity = SharedPreferencedUtils.getString(this,"LOCATION_CITY");
        if(StringUtil.isEmpty(strCity)) {
            SharedPreferencedUtils.setString(this,"LOCATION_CITY","北京");
        }
        getAndroidId();
        getVersion();

        SmartRefreshLayout.setDefaultRefreshHeaderCreater((context, layout) -> {
            layout.setPrimaryColorsId(R.color.white, R.color.black);
            return new ClassicsHeader(context);
        });

        SmartRefreshLayout.setDefaultRefreshFooterCreater((context, layout) -> new ClassicsFooter(context));
//        SmartRefreshLayout.setDefaultRefreshFooterCreater(new DefaultRefreshFooterCreater() {
//            @Override
//            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
//                //指定为经典Footer，默认是 BallPulseFooter
//                //设置脚的属性
//                ClassicsFooter footer = new ClassicsFooter(context);
//                // 设置背景颜色
//                footer.setPrimaryColorId(R.color.transparency);
//                // 设置字体颜色
//                footer.setAccentColorId(R.color.normal_color);
//                return footer;
//            }
//        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                Fresco.initialize(myApplication);
                //初始化图片选择参数
                BoxingMediaLoader.getInstance().init(new GlideIBoxingMediaLoader());
                BoxingCrop.getInstance().init(new UCropIBoxingCrop());
            }
        }).start();

        MobSDK.init(myApplication);
        //推送
        initCloudChannel(getApplicationContext());
        //bugly
        CrashReport.initCrashReport(getApplicationContext(), "8283ddee60", false);
        getChannel();
    }


    /**
     * 初始化云推送通道
     * @param applicationContext
     */
    private void initCloudChannel(Context applicationContext) {
        this.createNotificationChannel();
        PushServiceFactory.init(applicationContext);
        CloudPushService pushService = PushServiceFactory.getCloudPushService();
        pushService.register(applicationContext, new CommonCallback() {
            @Override
            public void onSuccess(String response) {
//                Log.d("way", "init cloudchannel success");
            }
            @Override
            public void onFailed(String errorCode, String errorMessage) {
//                Log.d("way", "init cloudchannel failed -- errorcode:" + errorCode + " -- errorMessage:" + errorMessage);
            }
        });
        //904dadafaed2476b91cbc386b4c8d154
//        LogUtil.i("**********andoridId:"+pushService.getDeviceId());

//        MiPushRegister.register(applicationContext, "XIAOMI_ID", "XIAOMI_KEY"); // 初始化小米辅助推送
//        HuaWeiRegister.register(this); // 接入华为辅助推送
//        VivoRegister.register(applicationContext);
//        OppoRegister.register(applicationContext, "OPPO_KEY", "OPPO_SECRET");
//        MeizuRegister.register(applicationContext, "MEIZU_ID", "MEIZU_KEY");
//        GcmRegister.register(applicationContext, "send_id", "application_id"); // 接入FCM/GCM初始化推送
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            // 通知渠道的id
            String id = "123";
            // 用户可以看到的通知渠道的名字.
            CharSequence name = "见怪";
            // 用户可以看到的通知渠道的描述
            String description = "channeldescription";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(id, name, importance);
            // 配置通知渠道的属性
            mChannel.setDescription(description);
            // 设置通知出现时的闪灯（如果 android 设备支持的话）
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            // 设置通知出现时的震动（如果 android 设备支持的话）
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            //最后在notificationmanager中创建该通知渠道
            mNotificationManager.createNotificationChannel(mChannel);
        }
    }


    private void getAndroidId() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
            andoridId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        }
//        LogUtil.i("andoridId："+andoridId);
    }

    /**
     * 获取渠道信息
     */
    public void getChannel(){

        PackageManager pm=getPackageManager();
        try {
            ApplicationInfo info =pm.getApplicationInfo(getPackageName(),
                    PackageManager.GET_META_DATA);
            String str=info.metaData.getCharSequence("CHANNEL").toString();


            switch (str){
                 case "guanfang":
                     channel="1";
                     break;
                 case "oppo":
                     channel="2";
                     break;
                 case "vivo":
                     channel="3";
                     break;
                 case "huawei":
                     channel="4";
                     break;
                 case "yingyongbao":
                     channel="5";
                     break;
                 case "uc":
                     channel="6";
                     break;
                 case "qh360":
                     channel="7";
                     break;
                 case "baidu":
                     channel="8";
                     break;
                 case "xiaomi":
                     channel="9";
                     break;
                 case "meizhu":
                     channel="10";
                     break;
             }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }finally {
            LogUtil.e("channel",channel+"==============================");
        }
    }

    /**
     * 获取版本号
     */
    public void getVersion() {
        try {
            PackageManager manager = MyApplication.getInstance().getPackageManager();
            PackageInfo info = manager.getPackageInfo(MyApplication.getInstance().getPackageName(), 0);
            versionName = info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            versionName = "1.0.0";
        }
    }

    public ArrayMap<Integer, Integer> colorBackGroundMap = new ArrayMap<Integer, Integer>() {
        {
            put(0, R.drawable.label_item1_normal);
            put(1, R.drawable.label_item1_normal);
            put(2, R.drawable.label_item2_normal);
            put(3, R.drawable.label_item3_normal);
            put(4, R.drawable.label_item4_normal);
            put(5, R.drawable.label_item5_normal);
            put(6, R.drawable.label_item6_normal);
            put(7, R.drawable.label_item7_normal);
        }
    };

    public ArrayMap<Integer, Integer> colorTextViewMap = new ArrayMap<Integer, Integer>() {
        {
            put(0, R.color.FF755FCF);
            put(1, R.color.FF755FCF);
            put(2, R.color.FF9B820D);
            put(3, R.color.FF0E909E);
            put(4, R.color.FF648854);
            put(5, R.color.FFB45D6B);
            put(6, R.color.FF206EDB);
            put(7, R.color.FFA47858);
        }
    };



}
