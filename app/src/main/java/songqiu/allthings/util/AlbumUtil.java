package songqiu.allthings.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2017/3/13.
 */

public class AlbumUtil {
    public static int REQUEST_PICK_IMAGE = 0x001;
    public static int REQUEST_TAKE_PHOTO = 0x002;
    public static int REQUEST_CAMERA_PERMISSION = 0x003;

//    /**
//     * 照相并在activity的onActivityResult方法中获取图片，该方法需要在onRequestPermissionsResult中再次进行判断调用dispatchTakePictureIntent
//     * @param context activity的上下文
//     * @param REQUEST_CAMERA_PERMISSION 请求相机的权限标志
//     * @param  REQUEST_TAKE_PHOTO 打开相机页面的标志
//     *
//     */
//    public static String takePhotot(Context context, int REQUEST_CAMERA_PERMISSION, int REQUEST_TAKE_PHOTO){
//        //当Android SDK版本大于或等于23时需要动态请求权限
//        if (android.os.Build.VERSION.SDK_INT >= 23) {
//            int cameraPemission = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA);
//            if (cameraPemission != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
//                return null;
//            } else {
//                return dispatchTakePictureIntent(context,REQUEST_TAKE_PHOTO);
//            }
//        } else {
//            return dispatchTakePictureIntent(context,REQUEST_TAKE_PHOTO);
//        }
//    }

    //ex:
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode) {
//            case REQUEST_CAMERA_PERMISSION:
//                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    mCurrentPhotoPath = AlbumUtil.dispatchTakePictureIntent(VerificationActivity.this,REQUEST_TAKE_PHOTO);
//                } else {
//                    // Permission Denied
//                    ToastUtil.shortshow(getApplicationContext(), "很遗憾你把相机权限禁用了。请务必开启相机权限享受我们提供的服务吧。");
//                }
//                break;
//            default:
//
//        }
//    }

//    public static String dispatchTakePictureIntent(Context context, int REQUEST_TAKE_PHOTO) {
//
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        // Ensure that there's a camera activity to handle the intent
//        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
//            // Create the File where the photo should go
//            File photoFile = null;
//            try {
//                photoFile = createImageFile(context);
//            } catch (IOException ex) {
//                // Error occurred while creating the File
//
//            }
//            // Continue only if the File was successfully created
//            //需要在AndroidMenifest文件中做如下配置,可以通过系统的管理器来打开自己项目的图片
////            <provider
////              android:name="android.support.v4.content.FileProvider"
////              android:authorities="com.example.android.fileprovider"
////              android:exported="false"
////              android:grantUriPermissions="true">
////              <meta-data
////                     android:name="android.support.FILE_PROVIDER_PATHS"
////                     android:resource="@xml/file_paths" />
////           </provider>
//            if (photoFile != null) {
//                Uri photoURI = FileProvider.getUriForFile(context,
//                        context.getPackageName()+".fileProvider",
//                        photoFile);
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//                ((Activity)context).startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
//                return photoFile.getAbsolutePath();
//            }
//
//        }
//        return null;
//    }

//    public static  void setPic(ImageView mImageView, String mPath, int targetW, int targetH) {
//
//		/* There isn't enough memory to open up more than a couple camera photos */
//        /* So pre-scale the target bitmap into which the file is decoded */
//
//
//
//		/* Get the size of the image */
//        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//        bmOptions.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(mPath, bmOptions);
//        int photoW = bmOptions.outWidth;
//        int photoH = bmOptions.outHeight;
//
//		/* Figure out which way needs to be reduced less */
//        int scaleFactor = 1;
//        if ((targetW > 0) || (targetH > 0)) {
//            scaleFactor = Math.min(photoW / targetW, photoH / targetH);
//        }
//
//		/* Set bitmap options to scale the image decode target */
//        bmOptions.inJustDecodeBounds = false;
//        bmOptions.inSampleSize = scaleFactor;
//        bmOptions.inPurgeable = true;
//
//		/* Decode the JPEG file into a Bitmap */
//        Bitmap bitmap = BitmapFactory.decodeFile(mPath, bmOptions);
//
//		/* Associate the Bitmap to the ImageView */
//        mImageView.setImageBitmap(bitmap);
//
//    }

    /**
     * fuction: 设置固定的宽度，//也可以修改一下让高度随之变化，使图片不会变形
     *
     * @param target
     * 需要转化bitmap参数
     * @param newWidth
     * 设置新的宽度
     * @return
     */
    public static Bitmap fitBitmap(Bitmap target, int newWidth, int newHeight)
    {
        int width = target.getWidth();
        int height = target.getHeight();

        Matrix matrix = new Matrix();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float)newHeight) / height;
        matrix.postScale(scaleWidth, scaleHeight);
        // Bitmap result = Bitmap.createBitmap(target,0,0,width,height,
        // matrix,true);

        Bitmap bmp = Bitmap.createBitmap(target, 0, 0, width, height, matrix,
                true);
        if (target != null && !target.equals(bmp) && !target.isRecycled())
        {
            target.recycle();
            target = null;
        }
        return bmp;
    }

    private static File createImageFile(Context context) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return image;
    }

    //广播通知相册更新
    public static  void galleryAddPic(Context context, String mPath) {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(mPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    //选取相册图片
    public static void pickAlbumImage(Context context, int REQUEST_PICK_IMAGE) {
        Intent i = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        ((Activity)context).startActivityForResult(i, REQUEST_PICK_IMAGE);
    }



}
