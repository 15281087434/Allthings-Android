package songqiu.allthings.api;

import android.content.Context;
import android.os.Looper;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import songqiu.allthings.BuildConfig;
import songqiu.allthings.bean.BaseBean;
import songqiu.allthings.util.LogJsonFormat;
import songqiu.allthings.util.LogUtil;
import songqiu.allthings.util.ToastUtil;

/**
 * rxJava 中基础的 observer
 * 封装处理一些错误处理
 * Created by cc on 2018/5/3.
 */
public class BaseSubscriber<T> implements Observer<T> {
    private Context mContext;
    private Gson gson = new Gson();

    public BaseSubscriber() {
    }

    public BaseSubscriber(Context context) {
        this.mContext = context;
    }

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(T value) {
        try {
            onNextWithCatchError(value);
        } catch (Exception e) {
            LogUtil.e("onNext error", "onNext error:" + e.getMessage());
            onError(e);
        }
    }

    @Override
    public void onError(Throwable e) {
        if (BuildConfig.DEBUG) {
            ToastUtil.showToast(e.getMessage());
        }
        if (e instanceof ApiException) {
            Looper.prepare();
            // Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
            LogUtil.e("error", e.getMessage());
            Looper.loop();
        }
        //网络请求连接超时
        if (e instanceof SocketTimeoutException) {
            onErrorTimeOut();
        }
        //网络请求断开
        if (e instanceof ConnectException) {
            onErrorConnect();
        }
        if (e instanceof IllegalStateException) {
            ToastUtil.showToast(e.getMessage());
        }
    }

    public void onErrorTimeOut() {
        LogUtil.d("onErrorTimeOut", "onErrorTimeOut");
        ToastUtil.showToast("网络请求失败，请重试！");
//        LogToFile.d("onErrorTimeOut", "onErrorTimeOut");
    }

    public void onErrorConnect() {
        LogUtil.d("onErrorConnect", "onErrorConnect");
        ToastUtil.showToast("网络无连接，请检查网络连接！");
    }

    @Override
    public void onComplete() {

    }

    /**
     * 加入异常捕获
     */
    public void onNextWithCatchError(T value) {
        if (value instanceof BaseBean) {
            if (((BaseBean) value).isSuccess() || ((BaseBean) value).isStatus()) {
                LogUtil.i("Data", LogJsonFormat.format(gson.toJson(((BaseBean) value).getData())));
            } else {
                if (mClose) {
                    return;
                }
                String msg = ((BaseBean) value).getMsg();
                if (!TextUtils.isEmpty(msg)) {
//                    if("请先登录".equals(msg)) {
//                        Intent in = new Intent(mContext,LoginNewActivity.class);
//                        mContext.startActivity(in);
//                    }
                    ToastUtil.showToast(((BaseBean) value).getMsg() + "");
                    LogUtil.e("Error:", "" + ((BaseBean) value).getMsg());
//                    LogToFile.d("Error", "" + ((BaseBean) value).getMsg());
                } else if (!TextUtils.isEmpty(((BaseBean) value).getMessage())) {
                    ToastUtil.showToast(((BaseBean) value).getMessage());
                    LogUtil.e("Error:", "" + ((BaseBean) value).getMessage());
//                    LogToFile.d("Error", "" + ((BaseBean) value).getMessage());
                }
                onError(null);
            }
        }
    }

    private boolean mClose;

    public void setCloseToast(boolean close) {
        this.mClose = close;
    }
}
