package songqiu.allthings.creation.article.publish;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import songqiu.allthings.R;
import songqiu.allthings.activity.CommentWebViewActivity;
import songqiu.allthings.base.BaseActivity;
import songqiu.allthings.bean.SaveArticleBean;
import songqiu.allthings.constant.SnsConstants;
import songqiu.allthings.http.BaseBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.http.OkHttp;
import songqiu.allthings.http.RequestCallBack;
import songqiu.allthings.iterface.DialogPrivacyListener;
import songqiu.allthings.util.ClickUtil;
import songqiu.allthings.util.LogUtil;
import songqiu.allthings.util.SharedPreferencedUtils;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.util.ToastUtil;
import songqiu.allthings.util.statusbar.StatusBarUtils;
import songqiu.allthings.view.DialogArticleCommon;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/12/20
 *
 *类描述：写文章
 *
 ********/
public class PublicArticleActivity extends BaseActivity {
    @BindView(R.id.backImg)
    ImageView backImg;
    @BindView(R.id.titleTv)
    TextView titleTv;
    @BindView(R.id.rightTv)
    TextView rightTv;
    @BindView(R.id.saveTv)
    TextView saveTv;
    @BindView(R.id.line)
    LinearLayout line;
    @BindView(R.id.shadowLayout)
    LinearLayout shadowLayout;

    @BindView(R.id.webview)
    WebView webView;
    WebSettings webSettings;

    SaveArticleBean saveArticleBean;

    int type; //2：从编辑页面进来 此时需要传递articleid
    int articleid;

    private ValueCallback<Uri[]> uploadMessageAboveL;
    private final static int FILE_CHOOSER_RESULT_CODE = 10000;
    @Override
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_creation_article);
    }

    @Override
    public void init() {
        type = getIntent().getIntExtra("type",0);
        articleid = getIntent().getIntExtra("articleid",0);
        boolean dayModel = SharedPreferencedUtils.getBoolean(this,SharedPreferencedUtils.dayModel,true);
        modeUi(dayModel);
        titleTv.setText("写文章");
        rightTv.setVisibility(View.VISIBLE);
        rightTv.setText("下一步");
    }


    public void modeUi(boolean isDay) {
        if(isDay) {
            shadowLayout.setVisibility(View.GONE);
            StatusBarUtils.with(this)
                    .setColor(getResources().getColor(R.color.FFF9FAFD))
                    .init()
                    .setStatusTextColorAndPaddingTop(true, this);
            initWebView(SnsConstants.RUL_EDIT_FILE);
        }else {
            shadowLayout.setVisibility(View.VISIBLE);
            StatusBarUtils.with(this)
                    .setColor(getResources().getColor(R.color.trans_6))
                    .init()
                    .setStatusTextColorAndPaddingTop(true, this);
            initWebView(SnsConstants.RUL_EDIT_FILE_NIGHT);
        }
    }

    public void initWebView(String url) {
        getSolict();
        webSettings = webView.getSettings();
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.clearView();
        webView.clearCache(true);
        webView.clearHistory();
        webView.clearFormData();
        // 支持javascript
        webView.getSettings().setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setSupportMultipleWindows(true);
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
                getSaveArticle();
            }
        });
        webView.loadUrl(url);
        webView.addJavascriptInterface(new JsInterface(), "android");

        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }

            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                uploadMessageAboveL = filePathCallback;
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_PICK);
                intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, FILE_CHOOSER_RESULT_CODE);
                return true;
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_CHOOSER_RESULT_CODE) { //处理返回的图片，并进行上传
            if (null == uploadMessageAboveL) return;
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (uploadMessageAboveL != null) {
                onActivityResultAboveL(requestCode, resultCode, data);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent intent) {
        if (requestCode != FILE_CHOOSER_RESULT_CODE || uploadMessageAboveL == null)
            return;
        Uri[] results = null;
        if (resultCode == Activity.RESULT_OK) {
            if (intent != null) {
                String dataString = intent.getDataString();
                ClipData clipData = intent.getClipData();
                if (clipData != null) {
                    results = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        results[i] = item.getUri();
                    }
                }
                if (dataString != null)
                    results = new Uri[]{Uri.parse(dataString)};
            }
        }
        uploadMessageAboveL.onReceiveValue(results);
        uploadMessageAboveL = null;
    }

    public void getSaveArticle() {
        Map<String, String> map = new HashMap<>();
        if(2==type) {
            map.put("type",type+"");
            map.put("articleid",articleid+"");
        }
        OkHttp.post(this, HttpServicePath.URL_SAVE_DATA, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        String data = gson.toJson(baseBean.data);
                        if (StringUtil.isEmpty(data)) return;
                        saveArticleBean = gson.fromJson(data, SaveArticleBean.class);
                        if(null == saveArticleBean) return;
                        if(!StringUtil.isEmpty(saveArticleBean.title) && !StringUtil.isEmpty(saveArticleBean.content)) {
                            webView.loadUrl("javascript:sendContent('" + saveArticleBean.title  + "','" + saveArticleBean.content  +"')");
                        }
                    }
                });
            }
        });
    }

    public void saveArticle(String title,String content) {
        Map<String, String> map = new HashMap<>();
        map.put("title",title);
        map.put("content",content);
        OkHttp.post(this, HttpServicePath.URL_SAVES_ARTICLE, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(PublicArticleActivity.this,baseBean.msg);
                    }
                });
            }
        });
    }

    public void initSaveDialog() {
        DialogArticleCommon dialogArticleCommon = new DialogArticleCommon(this,"保存","是否保存已输入内容？");
        dialogArticleCommon.setCanceledOnTouchOutside(true);
        dialogArticleCommon.setCancelable(true);
        dialogArticleCommon.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialogArticleCommon.show();
        dialogArticleCommon.setDialogPrivacyListener(new DialogPrivacyListener() {
            @Override
            public void cancel() {

            }

            @Override
            public void sure() {
                webView.evaluateJavascript("javascript:getContent()", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {

                        if(StringUtil.isEmpty(value))return;
                        String strBaseString = new String(Base64.decode(value.getBytes(), Base64.DEFAULT));
                        if(StringUtil.isEmpty(strBaseString))return;
                        try {
                            JSONObject jsonObject = new JSONObject(strBaseString);
                            String title = jsonObject.getString("title");
                            String content = jsonObject.getString("content");
                            saveArticle(title,content);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }
    public void getSolict() {
        HashMap<String, String> map = new HashMap<>();
        map.put("page", 1+"");
        OkHttp.post(this, HttpServicePath.URL_SOLICIT, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                Gson gson=new Gson();
                String data=gson.toJson(baseBean.data);
                Log.e("solicit",data+"");
            }
        });
    }

    /**
     * JS调用APP接口
     */
    public class JsInterface {
        //编辑文章
        @JavascriptInterface
        public void onEdit() {

        }

    }

    @OnClick({R.id.backImg,R.id.saveTv,R.id.rightTv})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.backImg:
                finish();
                break;
            case R.id.saveTv:
                if(ClickUtil.onClick()) {
                   initSaveDialog();
                }
                break;
            case R.id.rightTv:
                if(ClickUtil.onClick()) {
                    webView.evaluateJavascript("javascript:getContent()", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                            if(StringUtil.isEmpty(value))return;
                            String strBaseString = new String(Base64.decode(value.getBytes(), Base64.DEFAULT));
                            if(StringUtil.isEmpty(strBaseString))return;
                            try {
                                JSONObject jsonObject = new JSONObject(strBaseString);
                                String title = jsonObject.optString("title");
                                String content = jsonObject.optString("content");
                                if(StringUtil.isEmpty(title) || StringUtil.isEmpty(content) || content.equals("<p><br></p>")
                                        || content.equals("<p class=\"info\">请输入正文</p><p><br></p>")) {
                                    ToastUtil.showToast(PublicArticleActivity.this,"标题或者正文不能为空");
                                    return;
                                }
                                Intent intent = new Intent(PublicArticleActivity.this,PublicExplainActivity.class);
                                intent.putExtra("title",title);
                                intent.putExtra("content",content);
                                startActivity(intent);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                }
                break;
        }
    }

}
