package songqiu.allthings.util;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Vibrator;

import java.io.IOException;

/*******
 *
 *Created by ???
 *
 *???? 2019/10/16
 *
 *????
 *
 ********/
public class VibratorUtil {

    //??
    public static void vibrate(final Activity activity, long milliseconds) {
        Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(milliseconds);
    }

    //??
    public static void ringDown(Context mContext) {
        MediaPlayer mPlayer = null;
        AssetManager am = mContext.getAssets();
        AssetFileDescriptor afd = null;
        try {
            afd = am.openFd("load.mp3");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (mPlayer == null)
                mPlayer = new MediaPlayer();

            if (afd != null) {
                mPlayer.reset();
                mPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                mPlayer.prepare();
                MediaPlayer finalMPlayer = mPlayer;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finalMPlayer.start();
                    }
                }, 150);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
