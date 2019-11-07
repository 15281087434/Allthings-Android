package songqiu.allthings.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import com.bilibili.boxing.loader.IBoxingCrop;
import com.bilibili.boxing.model.config.BoxingCropOption;
import com.yalantis.ucrop.UCrop;

import songqiu.allthings.R;


/**
 * B站图片选择器裁剪设置类
 * Created by cc on 2018/5/24.
 */

public class UCropIBoxingCrop implements IBoxingCrop {
    private boolean isUseSingleProportion;
    private float ratioX;
    private float ratioY;

    public UCropIBoxingCrop() {

    }

    public UCropIBoxingCrop(float x, float y) {
        isUseSingleProportion = true;
        ratioX = x;
        ratioY = y;
    }

    @Override
    public void onStartCrop(Context context, Fragment fragment, @NonNull BoxingCropOption cropConfig, @NonNull String path, int requestCode) {
        Uri uri = new Uri.Builder()
                .scheme("file")
                .appendPath(path)
                .build();
        UCrop.Options crop = new UCrop.Options();
        crop.setStatusBarColor(ActivityCompat.getColor(context, R.color.normal_color));
        crop.setToolbarColor(ActivityCompat.getColor(context, R.color.normal_color));
        crop.setLogoColor(ActivityCompat.getColor(context, R.color.normal_color));
        crop.setCropFrameColor(ActivityCompat.getColor(context, R.color.normal_color));
        crop.setCompressionFormat(Bitmap.CompressFormat.PNG);
        if (isUseSingleProportion) {
            crop.withAspectRatio(ratioX, ratioY);
        }else {
            crop.withAspectRatio(cropConfig.getAspectRatioX(), cropConfig.getAspectRatioY());
        }
        crop.withMaxResultSize(cropConfig.getMaxWidth(), cropConfig.getMaxHeight());
        UCrop.of(uri, cropConfig.getDestination())
                .withOptions(crop)
                .start(context, fragment, requestCode);
    }

    @Override
    public Uri onCropFinish(int resultCode, Intent data) {
        if (data == null) {
            return null;
        }
        Throwable throwable = UCrop.getError(data);
        if (throwable != null) {
            return null;
        }
        return UCrop.getOutput(data);
    }
}
