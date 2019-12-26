package songqiu.allthings.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.heartfor.heartvideo.video.HeartVideo;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;
import songqiu.allthings.R;
import songqiu.allthings.base.BaseActivity;
import songqiu.allthings.bean.AdvertiseBean;
import songqiu.allthings.util.ClickUtil;
import songqiu.allthings.util.FileUtil;
import songqiu.allthings.util.statusbar.StatusBarUtils;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/8/8
 *
 *类描述：启动页广告
 *
 ********/
public class GuideAdvertisingActivity extends BaseActivity {

    @BindView(R.id.img)
    GifImageView img;

    AdvertiseBean advertiseBean;
    boolean enterable = true;
    File file;

    @BindView(R.id.jumpTv)
    TextView jumpTv;
    private MediaPlayer player;
    @BindView(R.id.surface)
    SurfaceView surfaceView;

    @Override
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_guide_advertising);
    }

    @Override
    public void init() {
        StatusBarUtils.with(GuideAdvertisingActivity.this).init().setStatusTextColorWhite(true, GuideAdvertisingActivity.this);
        advertiseBean = (AdvertiseBean) getIntent().getSerializableExtra("advertiseBean");
        if (null != advertiseBean) {
            setAdvertising(advertiseBean);
        }
        toMainActivity();
    }


    public void toMainActivity() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                //execute the task
                if (enterable) {
                    Intent intent = new Intent(GuideAdvertisingActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 5000);
    }

    public void setAdvertising(AdvertiseBean advertiseBean) {

        //获取缓存的广告文件
        File ads = FileUtil.getAdsFile(this, advertiseBean.url);

        if (ads.exists()) {
            if (advertiseBean.type == 2) {
                //当广告类型为视频文件时播放缓存视频
                player = new MediaPlayer();
                player.setVolume(0, 0);

                try {
                    player.setDataSource(ads.getAbsolutePath());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        if (surfaceView != null) {
                            surfaceView.setVisibility(View.GONE);
                        }
                        return false;
                    }
                });
                player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        int videoWidth = mp.getVideoWidth();
                        int videoHeight = mp.getVideoHeight();

                        DisplayMetrics dm = new DisplayMetrics();
                        getWindowManager().getDefaultDisplay().getMetrics(dm);
                        int mSurfaceViewWidth = dm.widthPixels;
                        int mSurfaceViewHeight = dm.heightPixels;

                        int w = mSurfaceViewHeight * videoWidth / videoHeight;
                        int margin = (mSurfaceViewWidth - w) / 2;
                        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                        lp.setMargins(margin, 0, margin, 0);
                        surfaceView.setLayoutParams(lp);
                        player.start();
                    }
                });

                surfaceView.setVisibility(View.VISIBLE);
                surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
                    @Override
                    public void surfaceCreated(SurfaceHolder holder) {
                        player.setDisplay(holder);
                        player.prepareAsync();
                    }

                    @Override
                    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                    }

                    @Override
                    public void surfaceDestroyed(SurfaceHolder holder) {

                    }
                });
            } else if (advertiseBean.url.endsWith("gif")) {
                try {
                    GifDrawable gifDrawable = new GifDrawable(FileUtil.getAdsFile(this, advertiseBean.url).getAbsolutePath());
                    img.setImageDrawable(gifDrawable);
                } catch (IOException e) {
                    e.printStackTrace();
                    img.setImageBitmap(BitmapFactory.decodeFile(FileUtil.getAdsFile(this, advertiseBean.url).getAbsolutePath()));
                }



            } else {
                img.setImageBitmap(BitmapFactory.decodeFile(FileUtil.getAdsFile(this, advertiseBean.url).getAbsolutePath()));
            }
        } else {
            Intent intent = new Intent(GuideAdvertisingActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.stop();
            player.release();
        }

    }

    @OnClick(R.id.jumpTv)
    public void onViewClick() {
        if (ClickUtil.onClick()) {
            enterable = false;
            Intent intent = new Intent(GuideAdvertisingActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @OnClick(R.id.img)
    public void onJump() {
        if (ClickUtil.onClick()) {
            if (null == advertiseBean) return;
            Intent intent = new Intent(this, CommentWebViewActivity.class);
            intent.putExtra("url", advertiseBean.jump_url);
            startActivity(intent);
            finish();
        }
    }
}
