package songqiu.allthings.util.upload;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import static songqiu.allthings.util.upload.UpdateManager.RESTART_DOANLOAD_ACTION;
import static songqiu.allthings.util.upload.UpdateManager.UPDATE_PROGRESS_ACTION;


/**
 * Created by Administrator on 2017/6/19.
 * 开启下载任务进行下载，可以在startDownload之前设置下载的线程数
 */

public class DownloadTask {

    private ExecutorService executorService;
    static DownloadTask task;
    private Context context;
    private String downloadUrl;
    private String saveUrl;


    private DownloadTask(Context context, String downloadUrl, String saveUrl) {
        this.context = context;
        this.downloadUrl = downloadUrl;
        this.saveUrl = saveUrl;
        executorService = new ScheduledThreadPoolExecutor(5);
    }

    public synchronized static DownloadTask getInstance(Context context, String downloadUrl, String saveUrl) {
        if (task == null) {
            task = new DownloadTask(context, downloadUrl, saveUrl);
        }
        return task;
    }

    private int fileSize;
    private int threadCount = 5;
    private DownloadThread[] downloadThreads;

    /**
     * 可以在startDownload之前设置下载的线程数目，并对下载文件路径进行校验，清空该DOWNLOAD URL的数据库，
     * 在该方法执行结束之后filesize才会有正值，可以根据fileSize判断下载是否正常开始。
     */
    public void startDownLoad(final ProgressBar pb) {

        if (TextUtils.isEmpty(saveUrl)) {
            Toast.makeText(context, "保存路径异常！", Toast.LENGTH_LONG).show();
            fileSize = -1;
            return ;
        }
        final File file = new File(saveUrl);
        Log.d("saveUrl","saveUrl "+file.getParent());
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdir();
        }
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            fileSize = -1;

            //出现异常时引导去浏览器下载
            Intent intent = new Intent();
            intent.setAction(UPDATE_PROGRESS_ACTION);
            context.sendBroadcast(intent);

            return ;
        }

        downloadThreads = new DownloadThread[threadCount];

        DownloadSqlLite.getInstance(context).deleteDownloadInfo(downloadUrl);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                HttpURLConnection urlConnection = null;
                try {
                    URL url = new URL(downloadUrl);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setConnectTimeout(5 * 1000);
                    urlConnection.setReadTimeout(5 * 1000);
                    urlConnection.connect();
                    if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        fileSize = urlConnection.getContentLength();
                    } else {
                        fileSize = -1;
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    fileSize = -1;

                    //出现异常时引导去浏览器下载
                    Intent intent = new Intent();
                    intent.setAction(RESTART_DOANLOAD_ACTION);
                    context.sendBroadcast(intent);

                } catch (IOException e) {
                    e.printStackTrace();
                    fileSize = -1;

                    //出现异常时引导去浏览器下载
                    Intent intent = new Intent();
                    intent.setAction(RESTART_DOANLOAD_ACTION);
                    context.sendBroadcast(intent);

                } finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }
                if (pb != null) {
                    pb.setMax(fileSize);
                }
                Log.d("DownloadThread", "DownloadThread fileSize = " + fileSize);
                if(fileSize>0){
                    int blockSize = fileSize % threadCount == 0 ? fileSize / threadCount : fileSize / threadCount + 1;
                    for (int i = 0; i < threadCount; i++) {
                        DownloadInfo info = new DownloadInfo();
                        if (i == threadCount - 1) {
                            info.setEndPos(fileSize - 1);
                        } else {
                            info.setEndPos((i + 1) * (blockSize) - 1);
                        }
                        info.setThreadId(i);
                        info.setDownloadUrl(downloadUrl);
                        info.setStartPos(i * blockSize);
                        info.setBlockSize(blockSize);
                        info.setDownBlock(0);
                        info.setSaveFilePath(saveUrl);
                        DownloadSqlLite.getInstance(context).insertDownloadInfo(info);
                        downloadThreads[i] = new DownloadThread(context, info);
                        executorService.execute(downloadThreads[i]);
                    }
                }

            }
        };
        executorService.execute(runnable);
    }

    /**
     * 终止线程，记录线程下载位置，防止记录位置丢失
     */
    public void pauseDownload() {
        for (int i = 0; i < threadCount; i++) {
            if (downloadThreads != null && downloadThreads[i] != null) {
                downloadThreads[i].setDownstate(DownloadThread.PAUSE);
                DownloadSqlLite.getInstance(context).updateDownpos(downloadThreads[i].downloadInfo);
            }
        }
    }

    /**
     * 重新开启线程加入线程池进行下载,剔除已经下载完的线程,重新设置线程数目完成下载
     * @return
     */
    public boolean  restartDownLoad() {
        List<DownloadInfo> downloadInfos = DownloadSqlLite.getInstance(context).queryDownloadInfo(downloadUrl);
        if (downloadInfos == null || downloadInfos.size() <= 0) {
            Log.d("DownloadTask","DownloadTask 数据库中的数据不存在或是取数错误！");
            return false;
        }

        Iterator iterator = downloadInfos.iterator();
        while (iterator.hasNext()) {
            DownloadInfo downloadInfo = (DownloadInfo) iterator.next();
            if (downloadInfo.getStartPos() + downloadInfo.getDownBlock() >= downloadInfo.getEndPos()) {
                iterator.remove();
            }
        }
        if(downloadInfos.size()>0){
            downloadThreads = new DownloadThread[downloadInfos.size()];
            task.setThreadCount(downloadInfos.size());
            for (int i = 0; i < downloadInfos.size(); i++) {
                downloadThreads[i] = new DownloadThread(context, downloadInfos.get(i));
                executorService.execute(downloadThreads[i]);
            }
            return true;
        }else{
            return false;
        }


    }

    //改方法在执行了startDownload之后才有值
    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public DownloadThread[] getDownloadThreads() {
        return downloadThreads;
    }

    public void setDownloadThreads(DownloadThread[] downloadThreads) {
        this.downloadThreads = downloadThreads;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }


    public void releaseRes() {
        if (executorService != null) {
            executorService.shutdownNow();
        }
        DownloadSqlLite.getInstance(context).deleteDownloadInfo(downloadUrl);
        downloadThreads = null;
        task = null;
        System.gc();
    }

    public boolean isAllThreadFinished() {
        if (task != null) {
            if (downloadThreads == null) {
                return true;
            }
            boolean over = true;
            for (int i = 0; i < downloadThreads.length; i++) {

                if (downloadThreads[i]!=null&&!downloadThreads[i].isFinish()) {
                    over = false;
                    break;
                }
            }
            return over;
        } else {
            return true;
        }
    }
}
