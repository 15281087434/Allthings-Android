package songqiu.allthings.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/*******
 *
 *Created by ???
 *
 *???? 2019/11/14
 *
 *????????????
 *
 ********/
public class PicParameterUtil {
    /**
     * ???????????
     */
    public static int[] getImgWH(String urls){
        int[] imgSize = new int[2];
        URL url = null;
        try {
            url = new URL(urls);
            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.connect();
                InputStream is = conn.getInputStream();
                Bitmap image = BitmapFactory.decodeStream(is);
                int srcWidth = image.getWidth();      // ????
                int srcHeight = image.getHeight();    // ????
                imgSize[0] = srcWidth;
                imgSize[1] = srcHeight;
                //????
                image.recycle();
                is.close();
                conn.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return imgSize;

    }
}
