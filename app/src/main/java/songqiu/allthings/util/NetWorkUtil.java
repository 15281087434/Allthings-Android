package songqiu.allthings.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;

/**
 *
 */

public class NetWorkUtil {

    /**
     * 获取ConnectivityManager
     */
    private static ConnectivityManager getConnectivityManager(Context context) {
        return (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    /**
     * 判断是否有网络链接
     */
    public static boolean isNetworkConnected(Context context) {
        boolean netWorkConnected = false;
        //检测API是不是小于23，因为到了API23之后getNetworkInfo(int networkType)方法被弃用
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M) {
            NetworkInfo net = getConnectivityManager(context).getActiveNetworkInfo();
            return net != null && net.isConnected();
        } else {
            //获得ConnectivityManager对象
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            if (connMgr != null) {
                //获取所有网络连接的信息
                Network networks = connMgr.getActiveNetwork();
                if (networks != null) {
                    netWorkConnected = connMgr.getNetworkInfo(networks).isConnected();
                } else {
                    netWorkConnected = false;
                }
            }
        }
        return netWorkConnected;

    }

    /**
     * 判断网络链接类型
     */
    public static int getConnectedType(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (mConnectivityManager!=null) {
                NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
                if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
                    return mNetworkInfo.getType();
                }
            }
        }
        return -1;
    }

}
