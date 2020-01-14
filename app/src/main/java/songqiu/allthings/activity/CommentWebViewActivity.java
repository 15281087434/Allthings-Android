package songqiu.allthings.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import songqiu.allthings.R;
import songqiu.allthings.base.BaseActivity;
import songqiu.allthings.bean.ShowArticleBean;
import songqiu.allthings.bean.UserInfoBean;
import songqiu.allthings.constant.SnsConstants;
import songqiu.allthings.creation.article.publish.PublicArticleActivity;
import songqiu.allthings.http.BaseBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.http.OkHttp;
import songqiu.allthings.http.RequestCallBack;
import songqiu.allthings.iterface.WindowShareListener;
import songqiu.allthings.mine.WithdrawActivity;
import songqiu.allthings.mine.invite.MyFriendActivity;
import songqiu.allthings.mine.qrcode.EwmRedEnvelopeActivity;
import songqiu.allthings.util.ClickUtil;
import songqiu.allthings.util.CopyButtonLibrary;
import songqiu.allthings.util.LogUtil;
import songqiu.allthings.util.SharedPreferencedUtils;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.util.ToastUtil;
import songqiu.allthings.util.WindowUtil;
import songqiu.allthings.util.statusbar.StatusBarUtils;
import songqiu.allthings.util.theme.ShareUrl;
import songqiu.allthings.view.SharePopupWindows;

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
    int type=0;
    int articleid;
    @Override
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_webview);
    }

    @Override
    public void init() {
        url = getIntent().getStringExtra("url");
        articleid = getIntent().getIntExtra("articleid",0);
        if(getIntent().hasExtra("authType")){
            type=getIntent().getIntExtra("authType",0);
//            boolean dayModel = SharedPreferencedUtils.getBoolean(this,SharedPreferencedUtils.dayModel,true);
//            setModelUi(dayModel);
        //认证页不使用沉浸式
        }
        StatusBarUtils.with(CommentWebViewActivity.this).init().setStatusTextColorWhite(true, CommentWebViewActivity.this);
        initWebView(url);
    }

    public void initWebView(String url) {
        webView.setVerticalScrollBarEnabled(false);
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

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if(url.contains("artical/preview")) {
                    getShowData(articleid);
                }
            }
        });
//        webView.loadUrl("file:///android_asset/share/about.html");
        webView.loadUrl(url);
        webView.addJavascriptInterface(new JsInterface(), "android");
    }

    public void setModelUi(boolean dayModel) {
        if(dayModel) {
            StatusBarUtils.with(this)
                    .setColor(getResources().getColor( R.color.FFF9FAFD))
                    .init()
                    .setStatusTextColorAndPaddingTop(true, this);
        }else {
            StatusBarUtils.with(this)
                    .setColor(getResources().getColor(R.color.trans_6))
                    .init()
                    .setStatusTextColorAndPaddingTop(true, this);
        }
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
            showShare(Wechat.NAME,0);
        }

        @JavascriptInterface
        public void onFaceInvite() { //面对面邀请
            Intent intent = new Intent(CommentWebViewActivity.this, EwmRedEnvelopeActivity.class);
            startActivity(intent);
        }

        @JavascriptInterface
        public void onQQInvite() { //QQ分享
            showShare(QQ.NAME,0);
        }


        @JavascriptInterface
        public void onAuth(){
                HashMap<String,String> map=new HashMap<>();
                map.put("type",type+"");
                OkHttp.post(CommentWebViewActivity.this, HttpServicePath.URL_STATE_APPLY,map,new RequestCallBack(){

                    @Override
                    public void httpResult(BaseBean baseBean) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showToast(baseBean.msg);
                                finish();
                            }
                        });

                    }
                });

        }

        //编辑文章
        @JavascriptInterface
        public void onEdit() {
            Intent intent = new Intent(CommentWebViewActivity.this, PublicArticleActivity.class);
            intent.putExtra("type",2);
            intent.putExtra("articleid",articleid);
            startActivity(intent);
        }

        //编辑文章
        @JavascriptInterface
        public void onInit() {

        }

        //复制
        @JavascriptInterface
        public void onCopy(String value) {
            CopyButtonLibrary copyButtonLibrary = new CopyButtonLibrary(CommentWebViewActivity.this, value);
            copyButtonLibrary.init(value);
            ToastUtil.showToast(CommentWebViewActivity.this,"复制成功!");
        }

        //征文
        @JavascriptInterface
        public void onShare() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showShareWindow(0);
                }
            });
        }

    }

    private void showShare(String platform,int type) { //type = 1 一字千金的分享
        final OnekeyShare oks = new OnekeyShare();
        //指定分享的平台，如果为空，还是会调用九宫格的平台列表界面
        if (platform != null) {
            oks.setPlatform(platform);
        }
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        if(1==type) {
            oks.setTitle("见怪“一字千金”征文大赛正式开启!");
        }else {
            oks.setTitle("见怪APP");
        }
        // titleUrl是标题的网络链接，仅在Linked-in,QQ和QQ空间使用
        if(type ==1) {
            oks.setTitleUrl(url);
        }else {
            if(!StringUtil.isEmpty(SnsConstants.URL_DOWNLOAD)) {
                oks.setTitleUrl(SnsConstants.URL_DOWNLOAD);
            }else {
                oks.setTitleUrl(SnsConstants.URL_GUANWANG);
            }
        }
        // text是分享文本，所有平台都需要这个字段
        StringBuffer sb = new StringBuffer();
        if(type ==1){
            sb.append("全新平台，高额奖金，多方位宣传，“一字千金”等你来参加!");
        }else {
            sb.append("看看这个应用，可以边看文章边赚钱，当天提现哦，记得输我的邀请码~");
            sb.append(SharedPreferencedUtils.getString(this,"SYSINVITATIONCODE"));
        }
        oks.setText(sb.toString());
        oks.setImageUrl(HttpServicePath.BasePicUrl+"sharelog.png?time="+System.currentTimeMillis());
        // url仅在微信（包括好友和朋友圈）中使用
       if(type==1) {
           oks.setUrl(url);
       }else {
           if(!StringUtil.isEmpty(SnsConstants.URL_DOWNLOAD)) {
               oks.setUrl(SnsConstants.URL_DOWNLOAD);
           }else {
               oks.setUrl(SnsConstants.URL_GUANWANG);
           }
       }
        //启动分享
        oks.show(this);
    }

    public void getShowData(int articleid) {
        Map<String, String> map = new HashMap<>();
        map.put("articleid",articleid+"");
        OkHttp.post(this, HttpServicePath.URL_SHOW_DATA, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        String data = gson.toJson(baseBean.data);
                        if (StringUtil.isEmpty(data)) return;
                        ShowArticleBean showArticleBean = gson.fromJson(data, ShowArticleBean.class);
                        if(null == showArticleBean) return;
                        if (!showArticleBean.avatar.contains("http")) {
                            showArticleBean.avatar = HttpServicePath.BasePicUrl + showArticleBean.avatar;
                         }
                        webView.loadUrl("javascript:sendEditContent('" + showArticleBean.title + "','" + showArticleBean.user_nickname + "','"
                                + showArticleBean.avatar+ "','" + showArticleBean.content +"')");
                    }
                });
            }
        });
    }

    //分享弹窗
    public void showShareWindow(int type) {
        SharePopupWindows rw = new SharePopupWindows(this, type, 0);
        WindowUtil.windowDeploy(this, rw, webView);
        rw.setWindowShareListener(new WindowShareListener() {
            @Override
            public void qqShare(int positon) {
                if(ClickUtil.onClick()) {
                    showShare(QQ.NAME,1);
                }
                rw.dismiss();
            }

            @Override
            public void wechatShare(int positon) {
                if(ClickUtil.onClick()) {
                    showShare(Wechat.NAME,1);
                }
                rw.dismiss();
            }

            @Override
            public void wechatFriendShare(int positon) {
                if(ClickUtil.onClick()) {
                    showShare(WechatMoments.NAME,1);
                }
                rw.dismiss();
            }

            @Override
            public void link(int position) {
                if(ClickUtil.onClick()) {
                    CopyButtonLibrary copyButtonLibrary = new CopyButtonLibrary(CommentWebViewActivity.this, url);
                    copyButtonLibrary.init(url);
                    ToastUtil.showToast(CommentWebViewActivity.this,"复制成功!");
                }
                rw.dismiss();
            }

            @Override
            public void report() {

            }

            @Override
            public void daytime() {

            }

            @Override
            public void night() {

            }
        });
    }
}
