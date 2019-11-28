package songqiu.allthings.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

public class FileUtil {

	public static String getTakePhotoPath(String path) {
		File file = new File(getRootFilePath(path));
		if (!file.exists()) {
			file.mkdir();
		}
		return getRootFilePath(path) + File.separator + ".jpg";
	}

	public static String getRootFilePath(String path) {
		StringBuffer stbPath = new StringBuffer();
		stbPath.append(Environment.getExternalStorageDirectory().getPath());
		stbPath.append(File.separator);
		stbPath.append(path);
		stbPath.append(File.separator);
		return stbPath.toString();
	}


	public static String checkLsength(String file) {
		try {
			File f = new File(file);
			if (f.exists() && f.isFile()) {
				LogUtil.i("********length:"+f.length());
				if (f.length() > 1024 * 500) {
					return FileUtil.convert2Save(file,
							FileUtil.getRootFilePath("songqiu.allthings"));
				}
			}
			return file;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	public static String convert2Save(final String originalFile,
			final String path) throws IOException, FileNotFoundException {

		// 获取当前系统时间
		Calendar cl = Calendar.getInstance();
		cl.setTime(Calendar.getInstance().getTime());
		long milliseconds = cl.getTimeInMillis();
		// 当前系统时间作为文件名
		String fileName = String.valueOf(milliseconds) + ".jpg";
		// 保存图片
		// 创建文件
		File file = new File(path, fileName);
		file.createNewFile();

		//LogUtil.i("************originalFile:"+originalFile);
		// /storage/emulated/0/songqiu.allthings//.jpg
		Bitmap bm = getSmallBitmap(originalFile);

		FileOutputStream fos = new FileOutputStream(file);

		bm.compress(Bitmap.CompressFormat.JPEG, 40, fos);
		// 关闭输出流
		fos.flush();
		fos.close();
		return file.getAbsolutePath();

	}

	/**
	 * 根据路径获得突破并压缩返回bitmap用于显示
	 * 
	 * @param
	 * @return
	 */
	public static Bitmap getSmallBitmap(String filePath) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);

		// Calculate inSampleSize  //calculateInSampleSize(options, 480, 800);
		options.inSampleSize = 1;

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;

		return BitmapFactory.decodeFile(filePath, options);
	}

	/**
	 * 计算图片的缩放值
	 * 
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			// Calculate ratios of height and width to requested height and
			// width
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width.
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize;
	}

}
