package songqiu.allthings.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ProgressBar;

import songqiu.allthings.util.upload.DownloadTask;


/**
 * Created by Administrator on 2017/6/22.
 */

public class DownloadService extends Service {
    private DownloadTask task;

    @Override
    public void onCreate() {
        Log.d("DownloadService","DownloadService onCreate");
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("DownloadService","DownloadService onBind");
        task = DownloadTask.getInstance(this, intent.getStringExtra("downloadUrl"), intent.getStringExtra("fileSavePath"));
        return new Binder();
    }

    public class Binder extends android.os.Binder{
        public DownloadService getService(){
            return DownloadService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("DownloadService","DownloadService onStartCommand");

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d("DownloadService","DownloadService onDestroy");
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("DownloadService","DownloadService onUnbind");
        return super.onUnbind(intent);
    }

    public void startDownload(@Nullable ProgressBar pb){
        if(task!=null){
            task.startDownLoad(pb);
        }
    }

    public  boolean restartDownLoad(){
        if(task!=null){
            return task.restartDownLoad();
        }
        return false;
    }

    public void pauseDownload(){
        if(task!=null){
            task.pauseDownload();
        }
    }

    public boolean isAllThreadFinished(){
        if(task!=null){
           return task.isAllThreadFinished();
        }
        return true;
    }

    public void releaseRes(){
        if(task!=null){
            task.releaseRes();
        }
    }

    public int getFileSize(){
        if(task!=null){
            return task.getFileSize();
        }
        return 0;
    }
}
