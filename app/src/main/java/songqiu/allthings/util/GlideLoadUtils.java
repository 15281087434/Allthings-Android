package songqiu.allthings.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import songqiu.allthings.R;

/*******
 *
 *Created by ???
 *
 *???? 2019/10/31
 *
 *????
 *
 ********/
public class GlideLoadUtils {

    private String TAG = "ImageLoader";
    /**
     * ????? ???????????
     * ??????????Java????????SingletonHolder???getInstance()
     * ???????????????????lazy????????????????
     * ?????????????instance?
     */
    public GlideLoadUtils() {
    }

    private static class GlideLoadUtilsHolder {
        private final static GlideLoadUtils INSTANCE = new GlideLoadUtils();
    }

    public static GlideLoadUtils getInstance() {
        return GlideLoadUtilsHolder.INSTANCE;
    }

//    /**
//     * Glide ?? ?????? ???????????Glide ????
//     *
//     * @param context
//     * @param url           ?????url??  String
//     * @param imageView     ?????ImageView ??
//     * @param default_image ??????????? id
//     */
//    public void glideLoad(Context context, String url, ImageView imageView, int default_image) {
//        if (context != null) {
//            Glide.with(context).load(url).centerCrop().error(default_image).crossFade
//                    ().into(imageView);
//        }
//    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void glideLoadHead(Activity activity, String url, ImageView imageView) {
        if (!activity.isDestroyed()) {
            RequestOptions options = new RequestOptions()
                    .circleCrop().transforms(new GlideCircleTransform(activity))
                    .error(R.mipmap.head_default)
                    .placeholder(R.mipmap.head_default);
            Glide.with(activity).load(url).apply(options).into(imageView);
        }
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void glideLoad(Activity activity, String url, RequestOptions options ,ImageView imageView) {
        if (!activity.isDestroyed()) {
            Glide.with(activity).load(url).apply(options).into(imageView);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void glideLoadNoDefault(Activity activity, String url, ImageView imageView) {
        if (!activity.isDestroyed()) {
            Glide.with(activity).load(url).into(imageView);
        }
    }


//    public void glideLoad(Fragment fragment, String url, ImageView imageView, int default_image) {
//        if (fragment != null && fragment.getActivity() != null) {
//            Glide.with(fragment).load(url).centerCrop().error(default_image).crossFade
//                    ().into(imageView);
//        }
//    }
//
//    public void glideLoad(android.app.Fragment fragment, String url, ImageView imageView, int default_image) {
//        if (fragment != null && fragment.getActivity() != null) {
//            Glide.with(fragment).load(url).centerCrop().error(default_image).crossFade
//                    ().into(imageView);
//        }
//    }
}
