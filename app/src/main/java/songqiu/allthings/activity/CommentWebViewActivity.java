package songqiu.allthings.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import songqiu.allthings.R;
import songqiu.allthings.base.BaseActivity;
import songqiu.allthings.constant.SnsConstants;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.mine.WithdrawActivity;
import songqiu.allthings.mine.invite.MyFriendActivity;
import songqiu.allthings.mine.qrcode.EwmRedEnvelopeActivity;
import songqiu.allthings.util.LogUtil;
import songqiu.allthings.util.SharedPreferencedUtils;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.util.statusbar.StatusBarUtils;
import songqiu.allthings.util.theme.ShareUrl;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/9/24
 *
 *类描述：
 *
 ********/
public class CommentWebViewActivity extends BaseActivity {

    @BindView(R.id.webview)
    WebView webView;
    String url;

    @Override
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_webview);
    }

    @Override
    public void init() {
        StatusBarUtils.with(CommentWebViewActivity.this).init().setStatusTextColorWhite(true, CommentWebViewActivity.this);
        url = getIntent().getStringExtra("url");
        initWebView(url);
    }

    public void initWebView(String url) {
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.clearView();
        webView.clearCache(true);
        webView.clearHistory();
        webView.clearFormData();
        // 支持javascript
        webView.getSettings().setJavaScriptEnabled(true);
        // 设置可以支持缩放
        webView.getSettings().setSupportZoom(true);
        // 设置出现缩放工具
//		webView.getSettings().setBuiltInZoomControls(true);
        // 扩大比例的缩放
        webView.getSettings().setUseWideViewPort(true);
        // 自适应屏幕
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器
                view.loadUrl(url);
                return true;
            }
        });
//        webView.loadUrl("file:///android_asset/share/about.html");
        webView.loadUrl(url);
        webView.addJavascriptInterface(new JsInterface(), "android");
    }

    /**
     * JS调用APP接口
     */
    public class JsInterface {
        @JavascriptInterface
        public void onWithDraw() { //去提现
            Intent intent = new Intent(CommentWebViewActivity.this, WithdrawActivity.class);
            intent.putExtra("withdrawType",2);
            startActivity(intent);
        }

        @JavascriptInterface
        public void onBack() { //退出
            finish();
        }

//        @JavascriptInterface
//        public void onInvite() { //立即邀请好友
//
//        }

        @JavascriptInterface
        public void onMyFriends() { //我的好友
            Intent intent = new Intent(CommentWebViewActivity.this, MyFriendActivity.class);
            startActivity(intent);
        }

        @JavascriptInterface
        public void onWeChatInvite() { //微信分享
            showShare(Wechat.NAME);
        }

        @JavascriptInterface
        public void onFaceInvite() { //面对面邀请
            Intent intent = new Intent(CommentWebViewActivity.this, EwmRedEnvelopeActivity.class);
            startActivity(intent);
        }

        @JavascriptInterface
        public void onQQInvite() { //QQ分享
            showShare(QQ.NAME);
        }

    }

    private void showShare(String platform) {
        final OnekeyShare oks = new OnekeyShare();
        //指定分享的平台，如果为空，还是会调用九宫格的平台列表界面
        if (platform != null) {
            oks.setPlatform(platform);
        }
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle("见怪APP");
        // titleUrl是标题的网络链接，仅在Linked-in,QQ和QQ空间使用
        if(!StringUtil.isEmpty(SnsConstants.URL_DOWNLOAD)) {
            oks.setTitleUrl(SnsConstants.URL_DOWNLOAD);
        }else {
            oks.setTitleUrl("https://jianguaiapp.com/download.html");
        }
        // text是分享文本，所有平台都需要这个字段
        StringBuffer sb = new StringBuffer();
        sb.append("看看这个应用，可以边看文章边赚钱，当天提现哦，记得输我的邀请码~");
        sb.append(SharedPreferencedUtils.getString(this,"SYSINVITATIONCODE"));
        oks.setText(sb.toString());
        oks.setImageUrl(HttpServicePath.BasePicUrl+"sharelog.png");
        // url仅在微信（包括好友和朋友圈）中使用
        if(!StringUtil.isEmpty(SnsConstants.URL_DOWNLOAD)) {
            oks.setUrl(SnsConstants.URL_DOWNLOAD);
        }else {
            oks.setUrl("https://jianguaiapp.com/download.html");
        }
        //启动分享
        oks.show(this);
    }

}
