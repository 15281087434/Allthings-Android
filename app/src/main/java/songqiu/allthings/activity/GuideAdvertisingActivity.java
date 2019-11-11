package songqiu.allthings.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import butterknife.BindView;
import butterknife.OnClick;
import pl.droidsonroids.gif.GifImageView;
import songqiu.allthings.R;
import songqiu.allthings.base.BaseActivity;
import songqiu.allthings.bean.AdvertiseBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.util.ClickUtil;
import songqiu.allthings.util.GlideLoadUtils;
import songqiu.allthings.util.StringUtil;
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

    @Override
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_guide_advertising);
    }

    @Override
    public void init() {
        StatusBarUtils.with(GuideAdvertisingActivity.this).init().setStatusTextColorWhite(true, GuideAdvertisingActivity.this);
        advertiseBean = (AdvertiseBean)getIntent().getSerializableExtra("advertiseBean");
        if(null != advertiseBean) {
            setAdvertising(advertiseBean);
        }
        toMainActivity();
    }

    public void toMainActivity() {
        new Handler().postDelayed(new Runnable(){
            public void run() {
                //execute the task
                Intent intent = new Intent(GuideAdvertisingActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 5000);
    }

    public void setAdvertising(AdvertiseBean advertiseBean) {
        if(StringUtil.isEmpty(advertiseBean.url)) return;
        String url = advertiseBean.url.replaceAll("\"","");;
        if(!StringUtil.isEmpty(url)) {
            if (!url.contains("http")) {
                url = HttpServicePath.BasePicUrl + url;
            }
        }
        if(1==advertiseBean.type) { //广告图片
//            RequestOptions options = new RequestOptions()
//                    .error(R.mipmap.pic_default_zhengfangxing)
//                    .placeholder(R.mipmap.pic_default_zhengfangxing);
            //.apply(options)
            GlideLoadUtils.getInstance().glideLoadNoDefault(GuideAdvertisingActivity.this,url,img);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @OnClick(R.id.jumpTv)
    public void onViewClick() {
        if(ClickUtil.onClick()) {
            Intent intent = new Intent(GuideAdvertisingActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @OnClick(R.id.img)
    public void onJump() {
        if(ClickUtil.onClick()) {
            if(null == advertiseBean) return;
            Intent intent = new Intent(this,CommentWebViewActivity.class);
            intent.putExtra("url", advertiseBean.jump_url);
            startActivity(intent);
        }
    }
}
