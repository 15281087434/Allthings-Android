package songqiu.allthings.util.upload;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import songqiu.allthings.BuildConfig;
import songqiu.allthings.R;
import songqiu.allthings.activity.GuideActivity;
import songqiu.allthings.application.MyApplication;
import songqiu.allthings.service.DownloadService;
import songqiu.allthings.util.ClickUtil;
import songqiu.allthings.util.LogUtil;
import songqiu.allthings.util.NetWorkUtil;
import songqiu.allthings.util.ToastUtil;

import static com.mob.MobSDK.getContext;


/**
 * Created by Administrator on 2016/11/18.
 */
public class UpdateManager implements ServiceConnection {

    private Context mContext;

    // 返回的安装包url
    private String ApkDownloadUrl = null;

    /* 下载包安装路径 */
    private String SaveFilePath;

    private Dialog DownloadDialog;
    DownloadService downloadService;
    String version;

    public UpdateManager(String version) {
        this.version = version;
    }

    public static final String UPDATE_PROGRESS_ACTION = "qianxing.taojinke.DownloadService.Update";
    public static final String RESTART_DOANLOAD_ACTION = "qianxing.taojinke.DownloadService.Restart";

    Intent intentService;
    boolean isBound;

    public void UpdateInfo(String url, Context context) {
        ApkDownloadUrl = url;
        SaveFilePath = Environment.getExternalStorageDirectory() + File.separator + "updateTjk" + File.separator + "updateTjk.apk";
        mContext = context;
        isStart = true;
        DownloadDialog = ShowDownLoadDialog();
        DownloadDialog.show();

        registerRev();
        intentService = new Intent(mContext, DownloadService.class);
        intentService.putExtra("downloadUrl", ApkDownloadUrl);
        intentService.putExtra("fileSavePath", SaveFilePath);
        Log.d("SaveFilePath", "SaveFilePath = " + SaveFilePath);
        isBound = mContext.bindService(intentService, this, Context.BIND_AUTO_CREATE);
    }

    private void cancelDownLoad() {
        if (downloadService != null) {
            downloadService.releaseRes();
        }
        unregisterRev();
        if (DownloadDialog != null && DownloadDialog.isShowing()) {
            DownloadDialog.dismiss();
        }
        if (downloadService != null && isBound) {
            mContext.unbindService(UpdateManager.this);
            isBound = false;
        }
        ((GuideActivity) mContext).finish();
    }

    /* 进度条与通知ui刷新 */
    private ProgressBar mProgress;
    private Button btn_switch;
    private Button btn_cancel;
    private TextView versionTv;
    private Dialog ShowDownLoadDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//        builder.setTitle("软件版本更新");
        builder.setCancelable(false);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.progress, null);
        mProgress = v.findViewById(R.id.progress);
        versionTv = v.findViewById(R.id.versionTv);
        versionTv.setText("V "+ version);
        btn_switch = v.findViewById(R.id.btn_switch);
        btn_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ClickUtil.onClick()) {
                    return;
                }
                if (isNetworkConnected && downloadService != null) {
                    if (isStart) {
                        btn_switch.setText("开始");
                        downloadService.pauseDownload();
                        isStart = false;
                    } else {
                        if (downloadService.restartDownLoad()) {
                            btn_switch.setText("暂停");
                            isStart = true;
                        } else {
                            ToastUtil.showToast(mContext, "下载出错，请重新下载！");
                            isStart = false;
                        }
                    }
                } else {
                    ToastUtil.showToast(mContext, "请连接网络后再进行下载更新！");
                }
            }
        });

        btn_cancel = v.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelDownLoad();
            }
        });
        builder.setView(v);
        DownloadDialog = builder.create();
        DownloadDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        return DownloadDialog;
    }

    private NetWorkChangedRev rev;
    private UpdateBroadcastRev updateBroadcastRev;
    private RestartBroadcastRev restartBroadcastRev;

    private void registerRev() {
        rev = new NetWorkChangedRev();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        mContext.registerReceiver(rev, intentFilter);

        updateBroadcastRev = new UpdateBroadcastRev();
        IntentFilter intentFilter1 = new IntentFilter(UPDATE_PROGRESS_ACTION);
        mContext.registerReceiver(updateBroadcastRev, intentFilter1);

        restartBroadcastRev = new RestartBroadcastRev();
        IntentFilter intentFilter2 = new IntentFilter(RESTART_DOANLOAD_ACTION);
        mContext.registerReceiver(restartBroadcastRev, intentFilter2);
    }

    private void unregisterRev() {
        if (rev != null) {
            mContext.unregisterReceiver(rev);
        }
        if (updateBroadcastRev != null) {
            mContext.unregisterReceiver(updateBroadcastRev);
        }

        if (restartBroadcastRev != null) {
            mContext.unregisterReceiver(restartBroadcastRev);
        }
    }

    private boolean isNetworkConnected = false;
    private boolean isStart = false;



    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        downloadService = ((DownloadService.Binder) service).getService();
        if (downloadService != null) {
            downloadService.startDownload(mProgress);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }

    /**
     * 网络监测，没有网络的时候暂停下载，暂停的时候记录下载的位置
     */
    public class NetWorkChangedRev extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (NetWorkUtil.isNetworkConnected(mContext)) {
                isNetworkConnected = true;
            } else {
                isNetworkConnected = false;
                btn_switch.setText("开始");
                downloadService.pauseDownload();
                isStart = false;
            }
        }
    }

    /**
     * 更新ProgressBar,当线程结束，文件大小满足条件时，下载完成
     */
    private int len;

    class UpdateBroadcastRev extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String actionStr = intent.getAction();
            if (TextUtils.equals(actionStr, UPDATE_PROGRESS_ACTION)) {
                len += intent.getIntExtra("progressLen", 0);
//                LogUtil.i("*************len:"+len);
                mProgress.setProgress(len);
                if (downloadService != null && len >= downloadService.getFileSize()) {
                    if (downloadService.isAllThreadFinished()) {
                        ToastUtil.showToast(mContext, "下载完成");
                        InstallApk();
                        cancelDownLoad();
                        Log.d("DownloadThread", "DownloadThread 下载完成 len =" + len);
                    }
                }
            }
        }
    }

    /**
     * 发现错误，去web下载
     */
    class RestartBroadcastRev extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(RESTART_DOANLOAD_ACTION)) {
                new AlertDialog.Builder(mContext).setTitle("提示")
                        .setMessage("出现错误，将会打开浏览器下载安装包，是否继续？")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                toWebDownLoadUrl();
                                dialog.dismiss();
                                ((Activity) mContext).finish();
                            }
                        }).setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();

            }
        }
    }

    private void toWebDownLoadUrl() {
        try {
            Uri uri = Uri.parse(ApkDownloadUrl);
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(uri);
            mContext.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    private  void InstallApk() {
        File Apkfile = new File(SaveFilePath);
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String ext = Apkfile.getName().substring(Apkfile.getName().lastIndexOf(".") + 1);
        String type = mime.getMimeTypeFromExtension(ext);
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri contentUri = FileProvider.getUriForFile(mContext.getApplicationContext(), BuildConfig.APPLICATION_ID + ".fileProvider", Apkfile);
                intent.setDataAndType(contentUri, type);
            } else {
                intent.setDataAndType(Uri.fromFile(Apkfile), type);
            }
            mContext.startActivity(intent);
        } catch (ActivityNotFoundException anfe) {
            Toast.makeText(getContext(), "No activity found to open this attachment.", Toast.LENGTH_LONG).show();
        }
    }
}
