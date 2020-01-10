package songqiu.allthings.util;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.PermissionChecker;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import songqiu.allthings.R;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2020/1/10
 *
 *类描述：
 *
 ********/
public class SelectPictureManager {

//    /*
// 定义返回的code值
// */
//    public static final int TAKE_PHOTO_CODE = 1000;//拍照
//    public static final int CHOOSE_PHOTO_CODE = 2000;//选择相册
//    public static final int PICTURE_CROP_CODE = 3000;//剪切图片
//    public static final int REQUEST_PERMISSIONS = 4000;//授权code
//​
//    private boolean isNeedCrop = false;//是否需要裁剪 默认不需要
//    private boolean isScale = true;//是否需要支持缩放 在可裁剪情况下有效
//    private boolean isContinuous = false;//是否是连拍模式 比如连续需要两张以上照片
//    private String fileName;//文件名字
//    private String oldFileName;//拍照裁剪时的原图片 需要删掉
//    private Uri outImageUri;//相机拍照图片保存地址
//    private int aspectX = 1000;//裁剪的宽高比例
//    private int aspectY = 1001;//裁剪的宽高比例 两个比例略微不一样是为了解决部分手机1：1时显示的时圆形裁剪框
//    private int outputX = 400;//裁剪后输出图片的尺寸大小
//    private int outputY = 400;//裁剪后输出图片的尺寸大小
//​
//    Activity activity;//全局上下文 需要startactiity
//​
//    PictureSelectListner pictureSelectListner;//选择之后照片的回调监听
//​
//        ​
//    /**
//     * 构造SelectPictureManager对象
//     *
//     * @param activity 上下文 这样就可以自主的实现
//     */
//    public SelectPictureManager(Activity activity) {
//        this(activity, null);
//    }
//​
//    /**
//     * 构造SelectPictureManager对象
//     *
//     * @param activity             上下文
//     * @param pictureSelectListner 回调监听
//     */
//    public SelectPictureManager(Activity activity, PictureSelectListner pictureSelectListner) {
//        this.activity = activity;
//        this.pictureSelectListner = pictureSelectListner;
//        fileName = System.currentTimeMillis() + ".jpg";//默认使用时间戳作为图片名字
//    }
//​
//    /**
//     * 设置图片回调监听
//     *
//     * @param pictureSelectListner
//     */
//    public void setPictureSelectListner(PictureSelectListner pictureSelectListner) {
//        this.pictureSelectListner = pictureSelectListner;
//    }
//​
//    /**
//     * 设置连拍 这样可以连续排多张不重复的图片
//     *
//     * @param isContinuous
//     * @return
//     */
//    public SelectPictureManager setContinuous(boolean isContinuous) {
//        this.isContinuous = isContinuous;
//        return this;
//    }
//​
//    /**
//     * 设置是否需要裁剪
//     *
//     * @return
//     */
//    public SelectPictureManager setNeedCrop(boolean isNeedCrop) {
//        this.isNeedCrop = isNeedCrop;
//        return this;
//    }
//​
//    /**
//     * 设置裁剪是否需要缩放
//     *
//     * @param isScale
//     * @return
//     */
//    public SelectPictureManager setScaleAble(boolean isScale) {
//        this.isScale = isScale;
//        return this;
//    }
//​
//    /**
//     * 设置裁剪的宽高比例
//     *
//     * @param x 裁剪比例宽
//     * @param y 裁剪比例高
//     * @return
//     */
//    public SelectPictureManager setAspect(int x, int y) {
//        this.aspectX = x;
//        this.aspectY = y;
//        return this;
//    }
//​
//    /**
//     * 设置裁剪后输出的尺寸大小
//     *
//     * @param x 裁剪输入的宽
//     * @param y 裁剪输出的高
//     * @return
//     */
//    public SelectPictureManager setOutPutSize(int x, int y) {
//        this.outputX = x;
//        this.outputY = y;
//        return this;
//    }
//​
//        ​
//    View showView;//使用PopupWindow 显示时需要一个View,如果觉得不便可以使用dialog
//​
//        ​
//    /**
//     * 展示选择拍照的pop框  一个是从相机选择 一个是拍照选择
//     *
//     * @param showView 需要展示view
//     */
//    public void showSelectPicturePopupWindow(View showView) {
//        if (showView == null) {
//            return;
//        }
//        this.showView = showView;
//        if (this.activity == null) {
//            if (this.pictureSelectListner != null) {
//                this.pictureSelectListner.throwError(new NullPointerException("上下文activity不可为空"));
//            } else {
//                throw new NullPointerException("上下文activity不可为空");
//            }
//        }
//        boolean hasPermission = checkPermission();
//        if (hasPermission) {
//            //拥有权限 可以直接打开
//            final PopupWindow popupWindow = new PopupWindow(this.activity);
//            LayoutInflater inflater = (LayoutInflater) this.activity
//                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            assert inflater != null;
//            final View mView = inflater.inflate(R.layout.pop_window_view, null);
//            Button btn_camera = (Button) mView.findViewById(R.id.icon_btn_camera);
//            Button btn_select = (Button) mView.findViewById(R.id.icon_btn_choose);
//            Button btn_cancel = (Button) mView.findViewById(R.id.icon_btn_cancel);
//​
//            btn_select.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    choosePhoto();
//                    popupWindow.dismiss();
//                }
//            });
//            btn_camera.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    takePhoto();
//                    popupWindow.dismiss();
//                }
//            });
//            btn_cancel.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    popupWindow.dismiss();
//                }
//            });
//​
//            // 导入布局
//            popupWindow.setContentView(mView);
//            // 设置动画效果
//            popupWindow.setAnimationStyle(R.anim.in_top);
//            popupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
//            popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
//            // 设置可触
//            popupWindow.setFocusable(true);
//            ColorDrawable dw = new ColorDrawable(0x0000000);
//            popupWindow.setBackgroundDrawable(dw);
//            // 单击弹出窗以外处 关闭弹出窗
//            mView.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    int height = mView.findViewById(R.id.ll_pop).getTop();
//                    int y = (int) event.getY();
//                    if (event.getAction() == MotionEvent.ACTION_UP) {
//                        if (y < height) {
//                            popupWindow.dismiss();
//                        }
//                    }
//                    return true;
//                }
//            });
//            popupWindow.showAtLocation(showView,
//                    Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
//        }
//​
//    }
//​
//    /**
//     * 先初始化好要保存的文件以及对应的uri
//     */
//    private void initSavedFile(boolean isCrop) {
//        if (!isContinuous && outImageUri != null && !TextUtils.isEmpty(fileName)) {//不是连拍 同时已经存在
//            return;
//        }
//        File parentFile = new File(Environment.getExternalStorageDirectory(), activity.getPackageName());//先创建包名对应的文件夹
//        if (!parentFile.exists()) {
//            parentFile.mkdir();
//        } else if (!parentFile.isDirectory()) {
//            parentFile.delete();
//            parentFile.mkdir();
//        }
//​
//        File outputImage = new File(Environment.getExternalStorageDirectory(), activity.getPackageName() + "/" + fileName);
//        try {
//            if (outputImage.exists()) {
//                if (isContinuous || isCrop) {//如果是连拍模式，就需要重新起一个名字
//                    if (isCrop) {//如果是裁剪的话 当这个文件已存在表示是通过相机拍照过来的，所以需要额外指定裁剪之后存储的uri
//                        oldFileName = fileName;
//                    }
//                    fileName = System.currentTimeMillis() + ".jpg";
//                    initSavedFile(isCrop);//重新来一下
//                    return;
//                } else {
//                    outputImage.delete();
//                }
//            }
//            outputImage.createNewFile();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        //以下uri的处理在targetVersion大于23时，同时在7.0版本以上时需要做区分
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N || isCrop) {//裁剪的时候 保存需要使用一般的uri 要不然出现无法保存裁剪的图片的错误
//            outImageUri = Uri.fromFile(outputImage);
//        } else {
//            //Android 7.0系统开始 使用本地真实的Uri路径不安全,使用FileProvider封装共享Uri(相机拍照输出的uri)
//            outImageUri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".fileprovider", outputImage);
//        }
//    }
//​
//        ​
//    /**
//     * 从相机拍照
//     */
//    private void takePhoto() {
//        initSavedFile(false);
//        Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        // 下面这句指定调用相机拍照后的照片存储的路径（7.0以上需要使用privider获取的uri）
//        takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, outImageUri);
//        activity.startActivityForResult(takeIntent, TAKE_PHOTO_CODE);
//    }
//​
//    /**
//     * 从相册选择
//     */
//    private void choosePhoto() {
//        Intent intent = new Intent("android.intent.action.GET_CONTENT");
//        intent.setType("image/*");
//        activity.startActivityForResult(intent, CHOOSE_PHOTO_CODE); // 打开相册
//    }
//​
//        ​
//    /**
//     * 裁剪图片
//     */
//    private void cropPicture(Uri pictureUri) {
//        initSavedFile(true);//从相册先择时，是没有初始化要保存的文件路径以及对应的uri
//        Intent cropIntent = new Intent("com.android.camera.action.CROP");
//        cropIntent.setDataAndType(pictureUri, "image/*");//7.0以上 输入的uri需要是provider提供的
//​
//        // 开启裁剪：打开的Intent所显示的View可裁剪
//        cropIntent.putExtra("crop", "true");
//        // 裁剪宽高比
//        cropIntent.putExtra("aspectX", aspectX);
//        cropIntent.putExtra("aspectY", aspectY);
//        // 裁剪输出大小
//        cropIntent.putExtra("outputX", outputX);
//        cropIntent.putExtra("outputY", outputY);
//        cropIntent.putExtra("scale", isScale);
//        /**
//         * return-data
//         * 这个属性决定onActivityResult 中接收到的是什么数据类型，
//         * true data将会返回一个bitmap
//         * false，则会将图片保存到本地并将我们指定的对应的uri。
//         */
//        cropIntent.putExtra("return-data", false);
//        // 当 return-data 为 false 的时候需要设置输出的uri地址
//        cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, outImageUri);//输出的uri为普通的uri，通过provider提供的uri会出现无法保存的错误
//        // 图片输出格式
//        cropIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
//        cropIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//不加会出现无法加载此图片的错误
//        cropIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);// 这两句是在7.0以上版本当targeVersion大于23时需要
//        activity.startActivityForResult(cropIntent, PICTURE_CROP_CODE);
//    }
//​
//    /**
//     * 收到图片结果后的处理逻辑
//     *
//     * @param requestCode
//     * @param resultCode
//     * @param data
//     */
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (resultCode == Activity.RESULT_OK) {
//            File imageFile = new File(Environment.getExternalStorageDirectory(), activity.getPackageName() + "/" + fileName);
//            Uri inImageUri = null;//需要裁剪时输入的uri
//            switch (requestCode) {
//                case TAKE_PHOTO_CODE:
//                    // TODO: 调用相机拍照
//                    if (imageFile == null && !imageFile.exists()) {
//                        if (pictureSelectListner != null) {
//                            pictureSelectListner.throwError(new NullPointerException("没有找到对应的路径"));
//                        }
//                        return;
//                    }
//                    if (this.isNeedCrop) {
//                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
//                            inImageUri = Uri.fromFile(imageFile);
//                        } else {
//                            //Android 7.0系统开始 使用本地真实的Uri路径不安全,使用FileProvider封装共享Uri
//                            inImageUri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".fileprovider", imageFile);
//                        }
//
//                        cropPicture(inImageUri);
//                    } else {
//                        if (pictureSelectListner != null) {
//                            pictureSelectListner.onPictureSelect(imageFile.getAbsolutePath());
//                        }
//                    }
//                    break;
//                case CHOOSE_PHOTO_CODE:
//                    // TODO: 从相册选择
//                    Uri uri = data.getData();
//                    String filePath = Util.getFilePathByUri(activity, uri);
//                    if (TextUtils.isEmpty(filePath)) {
//                        if (pictureSelectListner != null) {
//                            pictureSelectListner.throwError(new NullPointerException("没有找到对应的路径"));
//                        }
//                        return;
//                    }
//                    if (this.isNeedCrop) {
//                        File cropFile = new File(filePath);
//                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
//                            inImageUri = Uri.fromFile(cropFile);
//                        } else {
//                            //Android 7.0系统开始 使用本地真实的Uri路径不安全,使用FileProvider封装共享Uri
//                            inImageUri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".fileprovider", cropFile);
//                        }
//                        cropPicture(inImageUri);
//                    } else {
//                        if (pictureSelectListner != null) {
//                            pictureSelectListner.onPictureSelect(filePath);
//                        }
//                    }
//                    break;
//                case PICTURE_CROP_CODE:
//                    // TODO: 图片裁剪
//                    String cropFile = Util.getFilePathByUri(activity, outImageUri);
//                    if (!TextUtils.isEmpty(oldFileName)) {//相机拍照裁剪时删掉原来的照片
//                        File oldImageFile = new File(Environment.getExternalStorageDirectory(), activity.getPackageName() + "/" + oldFileName);
//                        oldImageFile.deleteOnExit();
//                    }
//                    if (TextUtils.isEmpty(cropFile)) {
//                        if (pictureSelectListner != null) {
//                            pictureSelectListner.throwError(new NullPointerException("没有找到对应的路径"));
//                        }
//                    } else {
//                        if (pictureSelectListner != null) {
//                            pictureSelectListner.onPictureSelect(cropFile);
//                        }
//                    }
//​
//                    break;
//            }
//        }
//    }
//​
//        ​
//    /**
//     * 6.0版本以上需要检查权限 动态的权限控制 这个地方做了相机、存储权限的模糊检查，虽然单从相册选择时不需要相机权限
//     */
//    private boolean checkPermission() {
//        String[] permissions = new String[]{Manifest.permission.CAMERA,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                Manifest.permission.READ_EXTERNAL_STORAGE};
//​
//        //检查权限
//        if (!checkSelfPermission(activity, Manifest.permission.CAMERA)
//                || !checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                || !checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
//        ) {
//            ActivityCompat.requestPermissions(activity, permissions, REQUEST_PERMISSIONS);//没有权限时进行权限申请
//            return false;
//        }
//        return true;
//    }
//​
//    /**
//     * 根部不同的情况进行权限检查，在targetSdkVersion设置为23以下时，不管权限是否开启，一般的checkSelfPermission都返回true
//     *
//     * @param activity
//     * @param permission
//     * @return
//     */
//    private boolean checkSelfPermission(Activity activity, String permission) {
//        int sdkVersion = activity.getApplicationInfo().targetSdkVersion;
//        boolean ret = true;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (sdkVersion >= Build.VERSION_CODES.M) {
//                ret = activity.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
//            } else {
//                ret = PermissionChecker.checkSelfPermission(activity, permission) == PermissionChecker.PERMISSION_GRANTED;
//            }
//        }
//        return ret;
//​
//    }
//​
//        ​
//    /**
//     * 6。0版本以上的权限申请
//     *
//     * @param requestCode
//     * @param permissions
//     * @param grantResults
//     */
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        switch (requestCode) {
//            case REQUEST_PERMISSIONS:
//                boolean isGrant = true;
//                for (int index = 0; index < grantResults.length; index++) {
//                    if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
//                        isGrant = false;
//                    }
//                }
//                if (isGrant) {
//                    showSelectPicturePopupWindow(showView);
//                } else {
//                    Toast.makeText(activity, "没有获取对应的权限", Toast.LENGTH_SHORT).show();
//                    /**
//                     * 跳转到 APP 详情的权限设置页
//                     */
//                    Intent intent = Util.getSettingIntent(activity);
//                    activity.startActivity(intent);
//                }
//                break;
//            default:
//        }
//    }
//​
//        ​
//    /**
//     * 图片选定之后的回调监听
//     */
//    public interface PictureSelectListner {
//​
//        void onPictureSelect(String imagePath);//返回图片的路径
//​
//        void throwError(Exception e);//当错误时返回对应的异常
//    }
//​
//    /**
//     * 工具类 为了保持使用方便，将该类直接放到这里，如果觉得不便可以单独出来一个类
//     */
//    static class Util {
//        /**
//         * 打开系统的设置界面 让用户自己授权
//         *
//         * @param context
//         * @return
//         */
//        public static Intent getSettingIntent(Context context) {
//            Intent localIntent = new Intent();
//            localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            if (Build.VERSION.SDK_INT >= 9) {
//                localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
//                localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
//            } else if (Build.VERSION.SDK_INT <= 8) {
//                localIntent.setAction(Intent.ACTION_VIEW);
//                localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
//                localIntent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
//            }
//            return localIntent;
//        }
//​
//        /**
//         * 获取uri对应的真是路径
//         *
//         * @param context
//         * @param uri
//         * @param selection
//         * @return
//         */
//        public static String getImagePath(Context context, Uri uri, String selection) {
//            String path = null;
//            // 通过Uri和selection来获取真实的图片路径
//            Cursor cursor = context.getContentResolver().query(uri, null, selection, null, null);
//            if (cursor != null) {
//                if (cursor.moveToFirst()) {
//                    path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
//                }
//                cursor.close();
//            }
//            return path;
//        }
//​
//        /**
//         * 4。4以上获取相册的地址 相册图片返回的uri是经过系统封装过的
//         *
//         * @param data
//         */
//        ​
//        public static String getFilePathByUri(Context context, Uri uri) {
//            String imagePath = null;
//            if (context == null || uri == null) {
//                return imagePath;
//            }
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                if (DocumentsContract.isDocumentUri(context, uri)) {
//                    // 如果是document类型的Uri，则通过document id处理
//                    String docId = DocumentsContract.getDocumentId(uri);
//                    if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
//                        String id = docId.split(":")[1]; // 解析出数字格式的id
//                        String selection = MediaStore.Images.Media._ID + "=" + id;
//                        imagePath = getImagePath(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
//                    } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
//                        Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
//                        imagePath = getImagePath(context, contentUri, null);
//                    }
//                } else if ("content".equalsIgnoreCase(uri.getScheme())) {
//                    // 如果是content类型的Uri，则使用普通方式处理
//                    imagePath = getImagePath(context, uri, null);
//                } else if ("file".equalsIgnoreCase(uri.getScheme())) {
//                    // 如果是file类型的Uri，直接获取图片路径即可
//                    imagePath = uri.getPath();
//                }
//            } else {
//                if ("content".equalsIgnoreCase(uri.getScheme())) {
//                    // 如果是content类型的Uri，则使用普通方式处理
//                    imagePath = getImagePath(context, uri, null);
//                } else if ("file".equalsIgnoreCase(uri.getScheme())) {
//                    // 如果是file类型的Uri，直接获取图片路径即可
//                    imagePath = uri.getPath();
//                }
//            }
//            return imagePath;
//        }
//​0
//    }​
}
