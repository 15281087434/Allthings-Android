package songqiu.allthings.util;

import android.arch.lifecycle.Lifecycle;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import com.bilibili.boxing.Boxing;
import com.bilibili.boxing.model.config.BoxingConfig;
import com.bilibili.boxing.model.config.BoxingCropOption;
import com.bilibili.boxing.model.entity.BaseMedia;
import com.bilibili.boxing.utils.BoxingFileHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.Flowable;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import songqiu.allthings.R;
import songqiu.allthings.api.AndroidScheduler;
import top.zibin.luban.Luban;

/**
 * b站图片选择类默认选择配置
 * Created by cc on 2018/5/24.
 */

public class BoxingDefaultConfig {
    //图片请求码
    public static final int IMAGE_REQUEST_CODE = 0X9595;

    private static BoxingDefaultConfig mInstance;

    private BoxingDefaultConfig() {
    }

    public static BoxingDefaultConfig getInstance() {
        if (mInstance == null) {
            synchronized (BoxingDefaultConfig.class) {
                if (mInstance == null) {
                    mInstance = new BoxingDefaultConfig();
                }
            }
        }
        return mInstance;
    }

    /**
     * 获取单张图片配置
     */
    public BoxingConfig getSingleConfig() {
        return new BoxingConfig(BoxingConfig.Mode.SINGLE_IMG)
                .withAlbumPlaceHolderRes(R.mipmap.icon_image_default)
                .withMediaPlaceHolderRes(R.mipmap.icon_image_default);
    }
    /**
     * 打开相机裁剪图片配置
     */
    public BoxingConfig getCameraCropConfig(Context context) {
        String cachePath = BoxingFileHelper.getCacheDir(context);
        if (TextUtils.isEmpty(cachePath)) {
            Toast.makeText(context.getApplicationContext(), R.string.boxing_storage_deny, Toast.LENGTH_SHORT).show();
            return null;
        }
        Uri destUri = new Uri.Builder()
                .scheme("file")
                .appendPath(cachePath)
                .appendPath(String.format(Locale.US, "%s.png", System.currentTimeMillis()))
                .build();
        return new BoxingConfig(BoxingConfig.Mode.SINGLE_IMG)
                .needCamera(R.mipmap.camera_bg)
                .withCropOption(new BoxingCropOption(destUri))
                .withAlbumPlaceHolderRes(R.mipmap.icon_image_default)
                .withMediaPlaceHolderRes(R.mipmap.icon_image_default);
    }
    /**
     * 打开相机裁剪图片配置
     */
    public BoxingConfig getCameraConfig(Context context) {
        String cachePath = BoxingFileHelper.getCacheDir(context);
        if (TextUtils.isEmpty(cachePath)) {
            Toast.makeText(context.getApplicationContext(), R.string.boxing_storage_deny, Toast.LENGTH_SHORT).show();
            return null;
        }
        return new BoxingConfig(BoxingConfig.Mode.SINGLE_IMG)
                .needCamera(R.mipmap.camera_bg)
                .withAlbumPlaceHolderRes(R.mipmap.icon_image_default)
                .withMediaPlaceHolderRes(R.mipmap.icon_image_default);
    }
    /**
     * 获取单张裁剪图片配置
     */
    public BoxingConfig getSingleCropConfig(Context context) {
        String cachePath = BoxingFileHelper.getCacheDir(context);
        if (TextUtils.isEmpty(cachePath)) {
            Toast.makeText(context.getApplicationContext(), R.string.boxing_storage_deny, Toast.LENGTH_SHORT).show();
            return null;
        }
        Uri destUri = new Uri.Builder()
                .scheme("file")
                .appendPath(cachePath)
                .appendPath(String.format(Locale.US, "%s.png", System.currentTimeMillis()))
                .build();
        return new BoxingConfig(BoxingConfig.Mode.SINGLE_IMG)
                .withCropOption(new BoxingCropOption(destUri))
                .withAlbumPlaceHolderRes(R.mipmap.icon_image_default)
                .withMediaPlaceHolderRes(R.mipmap.icon_image_default);
    }

    /**
     * 获取多张图片配置
     */
    public BoxingConfig getMultiConfig(int pickNum) {
        return new BoxingConfig(BoxingConfig.Mode.MULTI_IMG)
                .withMaxCount(pickNum)
                .withAlbumPlaceHolderRes(R.mipmap.icon_image_default)
                .withMediaPlaceHolderRes(R.mipmap.icon_image_default);
    }


    public static List<String> getCompressedBitmap(Context context,int requestCode, Intent data){
        if (requestCode == BoxingDefaultConfig.IMAGE_REQUEST_CODE) {
            List<BaseMedia> medias = Boxing.getResult(data);
            if (medias != null && medias.size() > 0) {
                List<String> list = new ArrayList<>();
                for(BaseMedia baseMedia:medias){
                    String compressPath = baseMedia.getPath();
                    if(compressPath!=null){
                        list.add(compressPath);
                    }
                }
                return list;
            }
        }
        return null;
    }


    /**
     * 获取压缩后的图片
     * @param requestCode  请求码
     * @param data          返回数据
//     * @param onLuBanCompressed  返回接口
     */
    public static void getCompressedBitmap(final Context context, int requestCode, Intent data, final OnLuBanCompressed onLuBanCompressed){
        if (requestCode == BoxingDefaultConfig.IMAGE_REQUEST_CODE) {
            List<BaseMedia> medias = Boxing.getResult(data);
            if (medias != null && medias.size() > 0) {
                List<String> list = new ArrayList<>();
                for(BaseMedia baseMedia:medias){
                    String compressPath = baseMedia.getPath();
                    if(compressPath!=null){
                        list.add(compressPath);
                    }
                }
                LuBanCompress(list,context,onLuBanCompressed);
            }
        }
    }

    public interface OnLuBanCompressed{
        void onCompressed(List<File> files);
    }

    /**
     *  使用鲁班压缩
     * @param stringList  压缩目标List集合
     */
    public static void LuBanCompress(List<String> stringList, final Context context, final OnLuBanCompressed onLuBanCompressed){
        Flowable.just(stringList)
                .observeOn(Schedulers.io())
                .map(new Function<List<String>, List<File>>() {
                    @Override
                    public List<File> apply(List<String> stringList) throws Exception {
                        return Luban.with(context).load(stringList).get();
                    }
                })
                .observeOn(AndroidScheduler.mainThread())
                .subscribe(new Consumer<List<File>>() {
                    @Override
                    public void accept(List<File> files) {
                        if(files==null||files.size()==0){
                            return;
                        }
                        onLuBanCompressed.onCompressed(files);
                    }
                });
    }



}
