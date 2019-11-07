package songqiu.allthings.util;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bilibili.boxing.loader.IBoxingCallback;
import com.bilibili.boxing.loader.IBoxingMediaLoader;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import songqiu.allthings.R;

/**
 * B站图片选择器加载设置类
 *
 */

public class GlideIBoxingMediaLoader implements IBoxingMediaLoader {
    @Override
    public void displayThumbnail(@NonNull ImageView img, @NonNull String absPath, int width, int height) {
        String path = "file://" + absPath;
        try {
            RequestOptions options = new RequestOptions()
                    .error(R.mipmap.icon_image_default)
                    .placeholder(R.mipmap.icon_image_default);
            Glide.with(img.getContext()).load(path).apply(options).into(img);
        } catch (IllegalArgumentException ignore) {
        }
    }

    @SuppressLint("CheckResult")
    @Override
    public void displayRaw(@NonNull final ImageView img, @NonNull String absPath, int width, int height, final IBoxingCallback callback) {
        String path = "file://" + absPath;
        Glide.with(img.getContext()).load(path).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                if (callback != null) {
                            callback.onFail(e);
                            return true;
                        }
                        return false;
            }

            @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        if (resource != null && callback != null) {
                            img.setImageDrawable(resource);
                            callback.onSuccess();
                            return true;
                        }
                        return false;
                    }
                });
//        GlideApp.with(img.getContext())
//                .load(path)
//                .listener(new RequestListener<Drawable>() {
//                    @Override
//                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                        if (callback != null) {
//                            callback.onFail(e);
//                            return true;
//                        }
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                        if (resource != null && callback != null) {
//                            img.setImageDrawable(resource);
//                            callback.onSuccess();
//                            return true;
//                        }
//                        return false;
//                    }
//                });
    }
}
