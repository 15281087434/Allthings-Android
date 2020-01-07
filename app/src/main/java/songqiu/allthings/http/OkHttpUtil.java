package songqiu.allthings.http;

import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/10/31
 *
 *类描述：
 *
 ********/
public class OkHttpUtil {
    private static OkHttpClient okHttpClient = null;
    /**建立单例模式*/
    //HTTPS
    public static OkHttpClient getInstance(){
        synchronized (OkHttpUtil.class) {
            if (okHttpClient == null) {
                //这里是以毫秒为单位，1000 毫秒 = 1秒
                okHttpClient = getUnsafeOkHttpClient().newBuilder()
                        .connectTimeout(30000, TimeUnit.SECONDS)// 设置超时时间
                        .readTimeout(30000, TimeUnit.SECONDS)// 设置读取超时时间
                        .writeTimeout(30000, TimeUnit.SECONDS)// 设置写入超时时间
                        .connectionPool(new ConnectionPool(10,5,TimeUnit.SECONDS))
                        .build();
            }
        }
        return okHttpClient;
    }


    //HTTP
//    public static OkHttpClient getInstance(){
//        synchronized (OkHttpUtil.class) {
//            if (okHttpClient == null) {
//                //这里是以毫秒为单位，1000 毫秒 = 1秒
//                okHttpClient = new OkHttpClient().newBuilder()
//                        .connectTimeout(30000, TimeUnit.SECONDS)// 设置超时时间
//                        .readTimeout(30000, TimeUnit.SECONDS)// 设置读取超时时间
//                        .writeTimeout(30000, TimeUnit.SECONDS)// 设置写入超时时间
//                        .build();
//            }
//        }
//        return okHttpClient;
//    }


    private static OkHttpClient getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            OkHttpClient okHttpClient = builder.build();
            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
