package songqiu.allthings.photoview;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import songqiu.allthings.R;
import songqiu.allthings.base.BaseActivity;

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
    ViewPager viewpager;
    @BindView(R.id.tv_num)
    TextView tvNum;

    ArrayList<String> urlList;

    @Override
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_photo_view);
    }

    @Override
    public void init() {
        initParam();
        initPhotoView();
    }

    private void initParam() {
        //需要加载的网络图片
        String[] urls = {
                "http://a.hiphotos.baidu.com/image/pic/item/00e93901213fb80e3b0a611d3fd12f2eb8389424.jpg",
                "http://b.hiphotos.baidu.com/image/pic/item/5243fbf2b2119313999ff97a6c380cd790238d1f.jpg",
                "http://f.hiphotos.baidu.com/image/pic/item/43a7d933c895d1430055e4e97af082025baf07dc.jpg",
                "http://a.hiphotos.baidu.com/image/pic/item/00e93901213fb80e3b0a611d3fd12f2eb8389424.jpg",
                "http://b.hiphotos.baidu.com/image/pic/item/5243fbf2b2119313999ff97a6c380cd790238d1f.jpg",
                "http://f.hiphotos.baidu.com/image/pic/item/43a7d933c895d1430055e4e97af082025baf07dc.jpg"
        };

        urlList = new ArrayList<>();
        Collections.addAll(urlList, urls);
    }

    public void initPhotoView() {
        PhotoPagerAdapter viewPagerAdapter = new PhotoPagerAdapter(getSupportFragmentManager(), urlList);
        viewpager.setAdapter(viewPagerAdapter);
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                tvNum.setText(String.valueOf(position + 1) + "/" + urlList.size());
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}
