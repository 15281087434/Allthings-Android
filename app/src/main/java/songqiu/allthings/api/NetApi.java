package songqiu.allthings.api;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import okio.BufferedSource;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import songqiu.allthings.BuildConfig;
import songqiu.allthings.application.MyApplication;
import songqiu.allthings.bean.BaseBean;
import songqiu.allthings.util.LogUtil;


/**
 * 封装网络请求
 * Created by cc on 2018/5/3.
 */

public class NetApi {
    private static volatile NetApi Instance;
    private ApiService mService;
    private String TAG = getClass().getSimpleName();

    private String url = null;

    /**
     * 网络请求相关构造
     */
    private NetApi() {
        /*设置打印日志信息*/
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(@NonNull String message) {
                LogUtil.d("Interceptor", "OkHttp====message:" + message);
            }
        });
        if (BuildConfig.DEBUG) {
            //显示日志
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            interceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        }
        /*okHttpClient客户端*/
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addInterceptor(new AddCookiesInterceptor())
                .retryOnConnectionFailure(true)
                .build();
        /*gson转换器*/
        GsonBuilder builder = new GsonBuilder()
                .serializeNulls();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(UrlManager.BASE_URL)
                //retrofit 使用CallAdapter将Call转换为RxJava中的Observable
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                //retrofit 使用Converter将返回的使用Gson转换为自定义的数据格式 ResponseBody-->BaseBean
                .addConverterFactory(GsonConverterFactory.create(builder.create()))
                .build();
        mService = retrofit.create(ApiService.class);

    }

    public static NetApi getInstance() {
        if (Instance == null) {
            synchronized (NetApi.class) {
                if (Instance == null) {
                    Instance = new NetApi();
                }
            }
        }
        return Instance;
    }

    public ApiService getApiService() {
        return mService;
    }

    /**
     * 添加cookies过滤
     */
    public static class AddCookiesInterceptor implements Interceptor {

        @Override
        public okhttp3.Response intercept(@NonNull Chain chain) throws IOException {
            Request original = chain.request();
            String method = original.method();
//            LogToFile.d("TAG", "request method :" + method);
//            LogToFile.d("TAG", "request url :" + original.url());
            Response response = chain.proceed(chain.request());
            ResponseBody responseBody = response.body();

            BufferedSource source = responseBody.source();
            // Buffer the entire body.
            source.request(Long.MAX_VALUE);
            Buffer buffer = source.buffer();
            Charset charset = Charset.forName("UTF-8");
            MediaType contentType = responseBody.contentType();
            if (contentType != null) {
                charset = contentType.charset(charset);
            }
            String bodyString = buffer.clone().readString(charset);

            Log.d("TAG", "request body :" + bodyString);
            BaseBean baseBean = JSON.parseObject(bodyString, BaseBean.class);
            if (!baseBean.isSuccess() && !baseBean.isStatus()) {
//                LogToFile.d("TAG", "URL:===========" + original.url().toString() +
//                        "request body :" + bodyString);
                //
                if (baseBean.getData() != null && baseBean.getData() instanceof Integer) {
                    if ((int) baseBean.getData() == 201) { //重新登录
//                        Intent intent = new Intent(MyApplication.getInstance(), LoginNewActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        MyApplication.getInstance().startActivity(intent);
//                        SharedPreferenceUtils.clearisLoginPerference(MyApplication.getInstance());
                    }
                }
            }
            Request.Builder builder = chain.request().newBuilder();

//            builder.addHeader("app-version", VersionUtils.getVersionCode(TaojinkeApplication.getInstance().getContext()) + "");
            //重建新的HttpUrl，修改需要修改的url部分
            return response;
        }
    }

    /**
     * 获取通话群组
     */
    public Observable<BaseBean> getMyGroup(
            Map<String, String> req) {
        return mService.getMyGroup(req);
    }




}
