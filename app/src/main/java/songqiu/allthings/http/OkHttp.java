package songqiu.allthings.http;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;

import android.util.Base64;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import songqiu.allthings.Event.EventTags;
import songqiu.allthings.application.MyApplication;
import songqiu.allthings.login.LoginActivity;
import songqiu.allthings.mine.AlipaySettingActivity;
import songqiu.allthings.mine.setting.SettingActivity;
import songqiu.allthings.util.DigestUtils;
import songqiu.allthings.util.LogUtil;
import songqiu.allthings.util.Md5Util;
import songqiu.allthings.util.NetWorkUtil;
import songqiu.allthings.util.SharedPreferencedUtils;
import songqiu.allthings.util.ToastUtil;
import songqiu.allthings.util.TokenManager;
import songqiu.allthings.view.DialogFileUploading;
import songqiu.allthings.view.LoadingDialog;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/8/7
 *
 *类描述：
 *
 ********/
public class OkHttp {

    public static void post(final Context context, LoadingDialog mProgressDialog, String requestPath, Map<String, String> params, final RequestCallBack listener) {
        if (!NetWorkUtil.isNetworkConnected(context)) {
            ToastUtil.showToast(context,"网络无连接，请检查网络！");
            return;
        }

        String time = String.valueOf(System.currentTimeMillis()/1000);
        Gson mGson = new Gson();
        String my = mGson.toJson(params);
        LogUtil.i("  传参:"+my);
//        String BaseString = Base64.encodeToString(my.getBytes(), Base64.DEFAULT).replaceAll("\n","");
//        LogUtil.i("************token:"+TokenManager.getRequestToken(context));
        //拼接字符串
        String strMap ="2"+time;
        String strBaseString = Base64.encodeToString(strMap.getBytes(), Base64.DEFAULT).replaceAll("\n","");
        StringBuffer sb = new StringBuffer();
        sb.append(strBaseString);
        sb.append(time);
        sb.append(TokenManager.getRequestToken(context));
        String currentString = sb.toString();
//        LogUtil.i(currentString.trim());
        String strMd5String =  Md5Util.md5(currentString);

//        LogUtil.i("Md5:"+strMd5String);
        Map<String ,String> map = new HashMap<>();
        map.put("data",my);
        map.put("sign",strMd5String);

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String str = mGson.toJson(map);
        RequestBody body = RequestBody.create(JSON, str);

        //构造Request对象
        Request.Builder mBuilder = new Request.Builder();
        Request request = mBuilder.url(requestPath)
                .addHeader("timestamp", time)
                .addHeader("apiversion", MyApplication.getInstance().versionName)
                .addHeader("osversion","2")
                .addHeader("deviceid", MyApplication.getInstance().andoridId)
                .addHeader("channel",MyApplication.getInstance().channel)
                .addHeader("token", TokenManager.getRequestToken(MyApplication.getInstance()))
                .post(body)
                .build();
        //构造Call对象
        Call mCall = OkHttpUtil.getInstance().newCall(request);
        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
//                String error = "系统异常！";
//                Looper.prepare();
//                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
//                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String str = response.body().string();
                LogUtil.i(requestPath+"====>"+ str);
                if(null != mProgressDialog) {
                    mProgressDialog.cancel();
                }
                if(str.contains("DOCTYPE")) {
                    Looper.prepare();
                    Toast.makeText(context, "服务器错误", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                    return;
                }
                Gson gson = new Gson();
                BaseBean baseBean = gson.fromJson(str, BaseBean.class);
                if(null != baseBean) {
                    if(HttpRuslt.NO_LOGIN.equals(baseBean.code)) { //未登录
                        SharedPreferencedUtils.setString(context, "SYSTOKEN","");
                        SharedPreferencedUtils.setString(context,SharedPreferencedUtils.USER_ICON,"");
                        SharedPreferencedUtils.setBoolean(context,SharedPreferencedUtils.LOGIN,false);
                        String result = requestPath.substring(requestPath.length()-7,requestPath.length());
                        if(result.equals("/follow")) {//首页关注路径
                            EventBus.getDefault().post(new EventTags.ToLogin());
                        }else {
//                            Intent intent = new Intent(MyApplication.getInstance(), LoginActivity.class);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//                            MyApplication.getInstance().startActivity(intent);
                        }
                    }else if(HttpRuslt.BIND_PHONE.equals(baseBean.code)) { //去绑定手机号
                        if(requestPath.contains("weixin_login")) {
                            EventBus.getDefault().post(new EventTags.ToBaindPhone(1));
                        }else if(requestPath.contains("qq_login")){
                            EventBus.getDefault().post(new EventTags.ToBaindPhone(2));
                        }
                    }else if(HttpRuslt.BINDED_PHONE.equals(baseBean.code)) { //已绑定
                        EventBus.getDefault().post(new EventTags.BaindedPhone());
                    }else if(HttpRuslt.OK.equals(baseBean.code)) {
                        listener.httpResult(baseBean);
                    }else {
                        Looper.prepare();
                        Toast.makeText(context, baseBean.msg, Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                }
            }
        });
    }


    public static void postObject(final Context context, String requestPath, Map<String, Object> params, final RequestCallBack listener) {
        String time = String.valueOf(System.currentTimeMillis()/1000);
        Gson mGson = new Gson();
        String my = mGson.toJson(params);
        LogUtil.i("  传参:"+my);
        //拼接字符串
        String strMap ="2"+time;
        String strBaseString = Base64.encodeToString(strMap.getBytes(), Base64.DEFAULT).replaceAll("\n","");
        StringBuffer sb = new StringBuffer();
        sb.append(strBaseString);
        sb.append(time);
        sb.append(TokenManager.getRequestToken(context));
        String currentString = sb.toString();
//        LogUtil.i(currentString.trim());
        String strMd5String =  Md5Util.md5(currentString);

//        LogUtil.i("Md5:"+strMd5String);
        Map<String ,String> map = new HashMap<>();
        map.put("data",my);
        map.put("sign",strMd5String);

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String str = mGson.toJson(map);
        RequestBody body = RequestBody.create(JSON, str);

        //构造Request对象
        Request.Builder mBuilder = new Request.Builder();
        Request request = mBuilder.url(requestPath)
                .addHeader("timestamp", time)
                .addHeader("apiversion", MyApplication.getInstance().versionName)
                .addHeader("osversion","2")
                .addHeader("deviceid", MyApplication.getInstance().andoridId)
                .addHeader("channel",MyApplication.getInstance().channel)
                .addHeader("token", TokenManager.getRequestToken(MyApplication.getInstance()))
                .post(body)
                .build();
        //构造Call对象
        Call mCall = OkHttpUtil.getInstance().newCall(request);
        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                String result = requestPath.substring(requestPath.length()-4,requestPath.length());
                if(result.equals("/nav")) {
                    if(my.equals("{\"zone\":\"1\"}")) {//首页
                        EventBus.getDefault().post(new EventTags.HomeNoNetwork());
                    }else if(my.equals("{\"zone\":\"2\"}")) { //快看
                        EventBus.getDefault().post(new EventTags.LookNoNetwork());
                    }
                    return;
                }else if (requestPath.substring(requestPath.length()-9,requestPath.length()).equals("/tasklist")) { //任务
                    EventBus.getDefault().post(new EventTags.TaskNoNetwork(false));
                    return;
                }
//                String error = "系统异常！";
//                Looper.prepare();
//                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
//                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String str = response.body().string();
                LogUtil.i(requestPath+"====>"+ str);
                if(str.contains("DOCTYPE")) {
                    Looper.prepare();
                    Toast.makeText(context, "服务器错误", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                    return;
                }
                Gson gson = new Gson();
                BaseBean baseBean = gson.fromJson(str, BaseBean.class);
                if(null != baseBean) {
                    if (requestPath.substring(requestPath.length()-9,requestPath.length()).equals("/tasklist")) { //任务
                        EventBus.getDefault().post(new EventTags.TaskNoNetwork(true));
                    }
                    if(HttpRuslt.NO_LOGIN.equals(baseBean.code)) { //未登录
                        SharedPreferencedUtils.setString(context, "SYSTOKEN","");
                        SharedPreferencedUtils.setString(context,SharedPreferencedUtils.USER_ICON,"");
                        SharedPreferencedUtils.setBoolean(context,SharedPreferencedUtils.LOGIN,false);
                        String result = requestPath.substring(requestPath.length()-7,requestPath.length());
                        if(result.equals("/follow")) {//首页关注路径
                            EventBus.getDefault().post(new EventTags.ToLogin());
                        }else {
                            Intent intent = new Intent(MyApplication.getInstance(), LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            MyApplication.getInstance().startActivity(intent);
                        }
                    }else if(HttpRuslt.BIND_PHONE.equals(baseBean.code)) { //去绑定手机号
                        if(requestPath.contains("weixin_login")) {
                            EventBus.getDefault().post(new EventTags.ToBaindPhone(1));
                        }else if(requestPath.contains("qq_login")){
                            EventBus.getDefault().post(new EventTags.ToBaindPhone(2));
                        }
                    }else if(HttpRuslt.BINDED_PHONE.equals(baseBean.code)) { //已绑定
                        EventBus.getDefault().post(new EventTags.BaindedPhone());
                    }else if(HttpRuslt.OK.equals(baseBean.code)) {
                        listener.httpResult(baseBean);
                    }else {
                        Looper.prepare();
                        Toast.makeText(context, baseBean.msg, Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                }
            }
        });
    }

    public static void post(final Context context, String requestPath, Map<String, String> params, final RequestCallBack listener) {
        //请求开始时间
        long startTime = System.currentTimeMillis();
        String time = String.valueOf(System.currentTimeMillis()/1000);
        Gson mGson = new Gson();
        String my = mGson.toJson(params);
        LogUtil.i("  传参:"+my);
//        String BaseString = Base64.encodeToString(my.getBytes(), Base64.DEFAULT).replaceAll("\n","");

//        LogUtil.i("************token:"+TokenManager.getRequestToken(context));
        //拼接字符串
        String strMap ="2"+time;
        String strBaseString = Base64.encodeToString(strMap.getBytes(), Base64.DEFAULT).replaceAll("\n","");
        StringBuffer sb = new StringBuffer();
        sb.append(strBaseString);
        sb.append(time);
        sb.append(TokenManager.getRequestToken(context));
        String currentString = sb.toString();
//        LogUtil.i(currentString.trim());
        String strMd5String =  Md5Util.md5(currentString);

        Map<String ,String> map = new HashMap<>();
        map.put("data",my);
        map.put("sign",strMd5String);

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String str = mGson.toJson(map);
        RequestBody body = RequestBody.create(JSON, str);

//        LogUtil.i("base64:"+strBaseString+"   sign:"+strMd5String+"  timestamp:"+time+"  apiversion:"+MyApplication.getInstance().versionName);
        //构造Request对象
        Request.Builder mBuilder = new Request.Builder();
        Request request = mBuilder.url(requestPath)
                .addHeader("timestamp", time)
                .addHeader("apiversion", MyApplication.getInstance().versionName)
                .addHeader("osversion","2")
                .addHeader("deviceid", MyApplication.getInstance().andoridId)
                .addHeader("channel",MyApplication.getInstance().channel)
                .addHeader("token", TokenManager.getRequestToken(MyApplication.getInstance()))
                .post(body)
                .build();
        //构造Call对象
        Call mCall = OkHttpUtil.getInstance().newCall(request);
        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.i("异常信息:"+e.getMessage()+"  "+e.toString());
                String result = requestPath.substring(requestPath.length()-4,requestPath.length());
                if(result.equals("/nav")) {
                    if(my.equals("{\"zone\":\"1\"}")) {//首页
                        EventBus.getDefault().post(new EventTags.HomeNoNetwork());
                    }else if(my.equals("{\"zone\":\"2\"}")) { //快看
                        EventBus.getDefault().post(new EventTags.LookNoNetwork());
                    }
                    return;
                }else if (requestPath.substring(requestPath.length()-9,requestPath.length()).equals("/tasklist")) { //任务
                    EventBus.getDefault().post(new EventTags.TaskNoNetwork(false));
                    return;
                }else if(requestPath.substring(requestPath.length()-9,requestPath.length()).equals("advertise")) {
                    EventBus.getDefault().post(new EventTags.Advertising());
                    return;
                }
//                String error = "系统异常！";
//                Looper.prepare();
//                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
//                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                long endTime = System.currentTimeMillis();
                String str = response.body().string();
                long duration = endTime-startTime;
                LogUtil.i(requestPath+"====>"+duration+" "+ str);
                if(str.contains("DOCTYPE")) {
                    if(requestPath.substring(requestPath.length()-9,requestPath.length()).equals("/tasklist")) {
                        EventBus.getDefault().post(new EventTags.TaskNoNetwork(true));
                    }
                    if(requestPath.substring(requestPath.length()-9,requestPath.length()).equals("advertise")) {
                        EventBus.getDefault().post(new EventTags.Advertising());
                        return;
                    }
                    Looper.prepare();
                    Toast.makeText(context, "服务器错误", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                    return;
                }
                Gson gson = new Gson();
                BaseBean baseBean = gson.fromJson(str, BaseBean.class);
                if(null != baseBean) {
                    if (requestPath.substring(requestPath.length()-9,requestPath.length()).equals("/tasklist")) { //任务
                        EventBus.getDefault().post(new EventTags.TaskNoNetwork(true));
                    }
                    if(HttpRuslt.NO_LOGIN.equals(baseBean.code)) { //未登录
                        SharedPreferencedUtils.setString(context, "SYSTOKEN","");
                        SharedPreferencedUtils.setString(context,SharedPreferencedUtils.USER_ICON,"");
                        SharedPreferencedUtils.setBoolean(context,SharedPreferencedUtils.LOGIN,false);
                        String result = requestPath.substring(requestPath.length()-7,requestPath.length());
                        if(result.equals("/follow")) {//首页关注路径
                            EventBus.getDefault().post(new EventTags.ToLogin());
                        }else if(result.equals("rcenter") || result.equals("ad_conf") || result.equals("ed_news")) { //我的页面3个接口
                            EventBus.getDefault().post(new EventTags.LoginLose());
                        }else if(result.equals("e_total")){
                            return;
                        }else {
                            Intent intent = new Intent(MyApplication.getInstance(), LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            MyApplication.getInstance().startActivity(intent);
                        }
                    }else if(HttpRuslt.BIND_PHONE.equals(baseBean.code)) { //去绑定手机号
                        if(requestPath.contains("weixin_login")) {
                            EventBus.getDefault().post(new EventTags.ToBaindPhone(1));
                        }else if(requestPath.contains("qq_login")){
                            EventBus.getDefault().post(new EventTags.ToBaindPhone(2));
                        }
                    }else if(HttpRuslt.BINDED_PHONE.equals(baseBean.code)) { //已绑定
                        EventBus.getDefault().post(new EventTags.BaindedPhone());
                    }else if(HttpRuslt.BINDED_ALIPAY.equals(baseBean.code)){ //绑定支付宝
                        Intent intent = new Intent(MyApplication.getInstance(), AlipaySettingActivity.class);
                        MyApplication.getInstance().startActivity(intent);
                    }else if(HttpRuslt.REND_LIMIT.equals(baseBean.code)){ //阅读上限

                    }else if(HttpRuslt.OK.equals(baseBean.code)) {
                        listener.httpResult(baseBean);
                    }else {
                        Looper.prepare();
                        Toast.makeText(context, baseBean.msg, Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                }
            }
        });
    }


    public static void post(final Context context, SmartRefreshLayout smartRefreshLayout,String requestPath, Map<String, Object> params, final RequestCallBack listener) {
        if (!NetWorkUtil.isNetworkConnected(context)) {
            EventBus.getDefault().post(new EventTags.HidePrestrain());
            ToastUtil.showToast(context,"网络无连接，请检查网络！");
            if (smartRefreshLayout != null) {
                smartRefreshLayout.finishLoadmore();
                smartRefreshLayout.finishRefresh();
            }
            return;
        }
        //请求开始时间
        long startTime = System.currentTimeMillis();
        String time = String.valueOf(System.currentTimeMillis()/1000);
        Gson mGson = new Gson();
        String my = mGson.toJson(params);
//        LogUtil.i("  传参=========>:"+ my);
//        LogUtil.i("请求接口=========>:"+requestPath);
//        String BaseString = Base64.encodeToString(my.getBytes(), Base64.DEFAULT).replaceAll("\n","");
//        LogUtil.i("************token:"+TokenManager.getRequestToken(context));
        //拼接字符串
        String strMap ="2"+time;
        String strBaseString = Base64.encodeToString(strMap.getBytes(), Base64.DEFAULT).replaceAll("\n","");
        StringBuffer sb = new StringBuffer();
        sb.append(strBaseString);
        sb.append(time);
        sb.append(TokenManager.getRequestToken(context));
        String currentString = sb.toString();
//        LogUtil.i(currentString.trim());
        String strMd5String =  Md5Util.md5(currentString);

//        LogUtil.i("Md5:"+strMd5String);
        Map<String ,String> map = new HashMap<>();
        map.put("data",my);
        map.put("sign",strMd5String);

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String str = mGson.toJson(map);
        RequestBody body = RequestBody.create(JSON, str);

        //构造Request对象
        Request.Builder mBuilder = new Request.Builder();
        Request request = mBuilder.url(requestPath)
                .addHeader("timestamp", time)
                .addHeader("apiversion", MyApplication.getInstance().versionName)
                .addHeader("osversion","2")
                .addHeader("deviceid", MyApplication.getInstance().andoridId)
                .addHeader("channel",MyApplication.getInstance().channel)
                .addHeader("token", TokenManager.getRequestToken(MyApplication.getInstance()))
                .post(body)
                .build();
        //构造Call对象
        Call mCall = OkHttpUtil.getInstance().newCall(request);
        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                String error = "系统异常！";
                if (smartRefreshLayout != null) {
                    smartRefreshLayout.finishLoadmore();
                    smartRefreshLayout.finishRefresh();
                }
                EventBus.getDefault().post(new EventTags.HidePrestrain());
//                Looper.prepare();
//                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
//                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (smartRefreshLayout != null) {
                    smartRefreshLayout.finishLoadmore();
                    smartRefreshLayout.finishRefresh();
                }
                EventBus.getDefault().post(new EventTags.HidePrestrain());
                String str = response.body().string();
                long endTime = System.currentTimeMillis();
                long duration = endTime-startTime;
                LogUtil.i(requestPath+"====>"+duration+" "+ str);
                if(str.contains("DOCTYPE")|| str.contains("<html>")) {
                    Looper.prepare();
                    Toast.makeText(context, "服务器错误", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                    return;
                }
                Gson gson = new Gson();
                BaseBean baseBean = gson.fromJson(str, BaseBean.class);
                if(null != baseBean) {
                    if(HttpRuslt.NO_LOGIN.equals(baseBean.code)) { //未登录
                        SharedPreferencedUtils.setString(context, "SYSTOKEN","");
                        SharedPreferencedUtils.setString(context,SharedPreferencedUtils.USER_ICON,"");
                        SharedPreferencedUtils.setBoolean(context,SharedPreferencedUtils.LOGIN,false);
                        String result = requestPath.substring(requestPath.length()-6,requestPath.length());
                        if(result.equals("follow")) {//首页关注路径
                            EventBus.getDefault().post(new EventTags.ToLogin());
                        }
//                        Intent intent = new Intent(MyApplication.getInstance(), LoginNewActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        MyApplication.getInstance().startActivity(intent);
                    }else if(HttpRuslt.BIND_PHONE.equals(baseBean.code)) { //去绑定手机号
                        if(requestPath.contains("in_login")) {
                            EventBus.getDefault().post(new EventTags.ToBaindPhone(1));
                        }else if(requestPath.contains("qq_login")){
                            EventBus.getDefault().post(new EventTags.ToBaindPhone(2));
                        }
                    }else if(HttpRuslt.OK.equals(baseBean.code)) {
                        listener.httpResult(baseBean);
                    }else {
                        Looper.prepare();
                        Toast.makeText(context, baseBean.msg, Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                }
            }
        });
    }



    public static void postFile(final Activity context, DialogFileUploading dialog, String requestPath, Map<String, String> params, File file, final RequestCallBack listener) {
        String time = String.valueOf(System.currentTimeMillis()/1000);
        Gson mGson = new Gson();
        String my = mGson.toJson(params);
        LogUtil.i("  传参:"+my);
//        String BaseString = Base64.encodeToString(my.getBytes(), Base64.DEFAULT).replaceAll("\n","");

//        LogUtil.i("************token:"+TokenManager.getRequestToken(context));
        //拼接字符串
        String strMap ="2"+time;
        String strBaseString = Base64.encodeToString(strMap.getBytes(), Base64.DEFAULT).replaceAll("\n","");
        StringBuffer sb = new StringBuffer();
        sb.append(strBaseString);
        sb.append(time);
        sb.append(TokenManager.getRequestToken(context));
        String currentString = sb.toString();
//        LogUtil.i(currentString.trim());
        String strMd5String =  Md5Util.md5(currentString);

//        使用MultipartBody同时传递键值对参数和File对象
//        MediaType.parse("File/*")
//        MediaType MEDIA_TYPE_PNG = MediaType.parse("image/jpg");
        MultipartBody multipartBody =new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file",file.getName(),RequestBody.create(MediaType.parse("File/*"), file))//添加文件
                .addFormDataPart("data",my)
                .addFormDataPart("sign",strMd5String)
                .build();

        //构造Request对象
        Request.Builder mBuilder = new Request.Builder();
        Request request = mBuilder.url(requestPath)
                .addHeader("timestamp", time)
                .addHeader("apiversion", MyApplication.getInstance().versionName)
                .addHeader("osversion","2")
                .addHeader("deviceid", MyApplication.getInstance().andoridId)
                .addHeader("channel",MyApplication.getInstance().channel)
                .addHeader("token", TokenManager.getRequestToken(MyApplication.getInstance()))
                .post(multipartBody)
                .build();
        //构造Call对象
        Call mCall = OkHttpUtil.getInstance().newCall(request);
        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if(context==null){
                    return;
                }
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String error = "";
                        if (e instanceof ConnectException) {
                            error = "网络状况不佳!";
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                        } else {
//                    error = "系统异常！";
                        }
                        if(null != dialog) {
                            dialog.disMiss();
                        }
                    }
                });


            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String str = response.body().string();
                LogUtil.i(str);
                if(context==null){
                    return;
                }
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(null != dialog) {
                            dialog.disMiss();
                        }
                        Gson gson = new Gson();
                        BaseBean baseBean = gson.fromJson(str, BaseBean.class);
                        if(null != baseBean) {
                            if(HttpRuslt.OK.equals(baseBean.code)) {
                                listener.httpResult(baseBean);
                            }else {
                                Toast.makeText(context, baseBean.msg, Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                });

            }
        });
    }

//    public static SSLSocketFactory createSSLSocketFactory() {
//        SSLSocketFactory ssfFactory = null;
//
//        try {
//            SSLContext sc = SSLContext.getInstance("TLS");
//            sc.init(null, new TrustManager[]{new TrustAllCerts()}, new SecureRandom());
//
//            ssfFactory = sc.getSocketFactory();
//        } catch (Exception e) {
//        }
//
//        return ssfFactory;
//    }



}
