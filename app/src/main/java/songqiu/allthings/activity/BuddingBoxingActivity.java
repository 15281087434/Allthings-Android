package songqiu.allthings.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.bilibili.boxing.AbsBoxingActivity;
import com.bilibili.boxing.AbsBoxingViewFragment;
import com.bilibili.boxing.model.config.BoxingConfig;
import com.bilibili.boxing.model.entity.BaseMedia;

import java.util.ArrayList;
import java.util.List;

import songqiu.allthings.fragment.BuddingBoxingViewFragment;


/**
 * 自定义B站图片选择UI
 * Created by cc on 2018/5/24.
 */

public class BuddingBoxingActivity extends AbsBoxingActivity {
    private BuddingBoxingViewFragment mPickerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.bilibili.boxing_impl.R.layout.activity_boxing);
        createToolbar();
        setTitleTxt(getBoxingConfig());
    }

    @NonNull
    @Override
    public AbsBoxingViewFragment onCreateBoxingView(ArrayList<BaseMedia> medias) {
        mPickerFragment = (BuddingBoxingViewFragment) getSupportFragmentManager().findFragmentByTag(BuddingBoxingViewFragment.TAG);
        if (mPickerFragment == null) {
            mPickerFragment = (BuddingBoxingViewFragment) BuddingBoxingViewFragment.newInstance().setSelectedBundle(medias);
            getSupportFragmentManager().beginTransaction().replace(com.bilibili.boxing_impl.R.id.content_layout, mPickerFragment, BuddingBoxingViewFragment.TAG).commit();
        }
        return mPickerFragment;
    }

    private void createToolbar() {
        Toolbar bar = findViewById(com.bilibili.boxing_impl.R.id.nav_top_bar);
        setSupportActionBar(bar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setTitleTxt(BoxingConfig config) {
        TextView titleTxt = findViewById(com.bilibili.boxing_impl.R.id.pick_album_txt);
        if (config.getMode() == BoxingConfig.Mode.VIDEO) {
            titleTxt.setText(com.bilibili.boxing_impl.R.string.boxing_video_title);
            titleTxt.setCompoundDrawables(null, null, null, null);
            return;
        }
        mPickerFragment.setTitleTxt(titleTxt);
    }

    @Override
    public void onBoxingFinish(Intent intent, @Nullable List<BaseMedia> medias) {
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (permissions.length > 0 && grantResults.length > 0) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            mPickerFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
