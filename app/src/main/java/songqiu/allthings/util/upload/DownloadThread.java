package songqiu.allthings.util.upload;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static songqiu.allthings.util.upload.UpdateManager.RESTART_DOANLOAD_ACTION;
import static songqiu.allthings.util.upload.UpdateManager.UPDATE_PROGRESS_ACTION;


/**
 * Created by Administrator on 2017/6/14.
 */

public class DownloadThread extends Thread {


    private boolean finish = false; // 是否已经下载完成
    private int off;

    public boolean isFinish() {
        return finish;
    }


    private Context context;


    public void setDownstate(int downstate) {
        this.downstate = downstate;
    }

    private int downstate;//下载状态
    public static final int PAUSE = 2;//暂停
    public static final int RUNNING = 1;//正在下载


    public DownloadInfo downloadInfo;

    public DownloadThread(Context context, DownloadInfo downloadInfo) {
        this.context = context;
        this.downstate = RUNNING;
        this.downloadInfo = downloadInfo;
        finish = false;
    }

    @Override
    public void run() {
        super.run();
        long startPos = downloadInfo.getStartPos() + downloadInfo.getDownBlock();
        long endPos = downloadInfo.getEndPos();
        if (startPos < endPos) {
            File file = null;
            RandomAccessFile aaf = null;
            InputStream is = null;
            HttpURLConnection conn = null;
            try {
                URL url = new URL(downloadInfo.getDownloadUrl());
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5 * 1000000);
                conn.setReadTimeout(5 * 1000000);
                conn.setRequestProperty("Referer", url.toString());
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setAllowUserInteraction(true);


                conn.setRequestProperty("Range", "bytes=" + startPos + "-" + endPos);
                conn.connect();
                file = new File(downloadInfo.getSaveFilePath());
                aaf = new RandomAccessFile(file, "rwd");
                Log.d("DownloadThread", "DownloadThread -----------------------------------------------------------------------------------------------------------------------");
                Log.d("DownloadThread", "DownloadThread thread start download!apkurl = " + downloadInfo.getDownloadUrl() + ";apkpath = " + downloadInfo.getSaveFilePath());
                Log.d("DownloadThread", "DownloadThread bytes = " + startPos + "-" + endPos);

                aaf.seek(startPos);

                is = conn.getInputStream();
                int len;

                byte[] bytes = new byte[5120];
                while ((len = is.read(bytes, 0, bytes.length)) != -1) {
                    if (PAUSE == downstate) {
                        Log.d("DownloadThread", "PAUSE downloadInfo getThreadId = " + downloadInfo.getThreadId() + " ;getDownBlock = " + downloadInfo.getDownBlock());

                        break;
                    }
                    aaf.write(bytes, off, len);

                    downloadInfo.setDownBlock(downloadInfo.getDownBlock() + len);
                    Intent intent = new Intent();
                    intent.setAction(UPDATE_PROGRESS_ACTION);
                    intent.putExtra("progressLen", len);
                    context.sendBroadcast(intent);

                }
                finish = PAUSE != downstate;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                finish = false;

                Intent intent = new Intent();
                intent.setAction(RESTART_DOANLOAD_ACTION);
                context.sendBroadcast(intent);

            } catch (IOException e) {
                e.printStackTrace();

                finish = false;
                StackTraceElement[] stackTraceElements = e.getStackTrace();
                for(int i = 0 ; i<e.getStackTrace().length;i++){
                }

                Intent intent = new Intent();
                intent.setAction(RESTART_DOANLOAD_ACTION);
                context.sendBroadcast(intent);

            } finally {
                closeIO(aaf, is);
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }
    }

    private static void closeIO(Closeable... closeables) {
        if (closeables == null || closeables.length <= 0) {
            return;
        }
        for (Closeable cb : closeables) {
            try {
                if (null == cb) {
                    continue;
                }
                cb.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
