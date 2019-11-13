package songqiu.allthings.photoview;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import songqiu.allthings.R;
import songqiu.allthings.activity.MainActivity;
import songqiu.allthings.base.BaseActivity;
import songqiu.allthings.util.LogUtil;
import songqiu.allthings.util.statusbar.StatusBarUtils;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/11/7
 *
 *类描述：图片查看器
 *
 ********/
public class PhotoViewActivity extends BaseActivity {

    @BindView(R.id.viewpager)
    ViewPagerFix viewpager;
    @BindView(R.id.tv_num)
    TextView tvNum;

    ArrayList<String> urlList;

    @Override
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_photo_view);
    }

    @Override
    public void init() {
        StatusBarUtils.with(PhotoViewActivity.this).init().setStatusTextColorWhite(true, PhotoViewActivity.this);
        String [] urls = getIntent().getStringArrayExtra("photoArray");
        int clickPhotoPotision = getIntent().getIntExtra("clickPhotoPotision",0);
        if(null == urls || urls.length == 0) return;
        initParam(urls);
        initPhotoView(clickPhotoPotision);
    }

    private void initParam(String [] urls) {
        //需要加载的网络图片
        urlList = new ArrayList<>();
        Collections.addAll(urlList, urls);
    }

    public void initPhotoView(int clickPhotoPotision) {
        PhotoPagerAdapter viewPagerAdapter = new PhotoPagerAdapter(getSupportFragmentManager(), urlList);
        viewpager.setAdapter(viewPagerAdapter);
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                tvNum.setText(String.valueOf(clickPhotoPotision + 1) + "/" + urlList.size());
            }

            @Override
            public void onPageSelected(int position) {
//                tvNum.setText(String.valueOf(position + 1) + "/" + urlList.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewpager.setCurrentItem(clickPhotoPotision);
    }
}
