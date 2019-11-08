package songqiu.allthings.articledetail;

import android.content.Context;
import android.content.Intent;
import android.webkit.JavascriptInterface;

import songqiu.allthings.home.gambit.HotGambitDetailActivity;
import songqiu.allthings.photoview.PhotoViewActivity;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/11/8
 *
 *类描述：文章详情 h5图片点击
 *
 ********/
public class ImageJavascriptInterface {

    private Context context;
    private String[] imageUrls;

    public ImageJavascriptInterface(Context context, String[] imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
    }

    @JavascriptInterface
    public void openImage(String img,int pos) {
        if(null != imageUrls && 0!= imageUrls.length) {
            Intent intent = new Intent(context, PhotoViewActivity.class);
            intent.putExtra("photoArray",imageUrls);
            intent.putExtra("clickPhotoPotision",pos);
            context.startActivity(intent);
        }
    }
}
