package songqiu.allthings.creation.article.publish;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.Surface;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.heartfor.heartvideo.video.PlayerStatus;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import songqiu.allthings.R;
import songqiu.allthings.activity.BuddingBoxingActivity;
import songqiu.allthings.base.BaseActivity;
import songqiu.allthings.bean.SaveArticleBean;
import songqiu.allthings.bean.UserInfoBean;
import songqiu.allthings.http.BaseBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.http.OkHttp;
import songqiu.allthings.http.RequestCallBack;
import songqiu.allthings.iterface.DialogPrivacyListener;
import songqiu.allthings.iterface.IEditTextChangeListener;
import songqiu.allthings.mine.userpage.ModificationInfoActivity;
import songqiu.allthings.util.BoxingDefaultConfig;
import songqiu.allthings.util.ClickUtil;
import songqiu.allthings.util.EditTextCheckUtil;
import songqiu.allthings.util.LogUtil;
import songqiu.allthings.util.SharedPreferencedUtils;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.util.ToastUtil;
import songqiu.allthings.view.DialogArticleCommon;
import songqiu.allthings.view.NoTitleDialog;

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


    private ValueCallback<Uri[]> uploadMessageAboveL;
    private final static int FILE_CHOOSER_RESULT_CODE = 10000;
    @Override
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_creation_article);
    }

    @Override
    public void init() {
        boolean dayModel = SharedPreferencedUtils.getBoolean(this,SharedPreferencedUtils.dayModel,true);
        modeUi(dayModel);
        titleTv.setText("写文章");
        rightTv.setVisibility(View.VISIBLE);
        rightTv.setText("下一步");

        getSaveArticle();
        initWebView("http://192.168.0.175/share/test/example/demo/test-sperate.html");
    }


    public void modeUi(boolean isDay) {
        if(isDay) {
            shadowLayout.setVisibility(View.GONE);
        }else {
            shadowLayout.setVisibility(View.VISIBLE);
        }
    }

    public void initWebView(String url) {
        webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);//支持JavaScript脚本
//        webView.setWebChromeClient(new MyWebChromeClient());
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
                LogUtil.i("-----------shouldOverrideUrlLoading:"+url);
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                LogUtil.i("-----------onPageFinished:"+url);
                super.onPageFinished(view, url);
            }
        });
        webView.loadUrl(url);
//        webView.addJavascriptInterface(new JsInterface(), "android");

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
        OkHttp.post(this, HttpServicePath.URL_SAVE_DATA, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        String data = gson.toJson(baseBean.data);
                        if (StringUtil.isEmpty(data)) return;
                        SaveArticleBean saveArticleBean = gson.fromJson(data, SaveArticleBean.class);
                        if(null == saveArticleBean) return;

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
//                String title = titleEt.getText().toString();
//                String content = contentEt.getText().toString();
//                saveArticle(title,content);
//                String ss = "?title=**&content=**";


                webView.evaluateJavascript("javascript:getContent()", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        LogUtil.i("=========:"+value);
                        String ss = new String(value+"");
                        try {
                            JSONObject jsonObject = new JSONObject(ss);
                            LogUtil.i("---------:"+jsonObject.getString("title"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


//                        String ss = value.substring(1);
//                        String ss1 = ss.substring(0,ss.length()-1);
//                        String sss = getUnicode(ss1);
//                        String s1= "{\"title\":\"近距离\",\"content\":\"\u003Cp>all哦OK了\u003Cbr>\u003C/p>\"}";
//                        getUnicode(s1);


//                        JSONObject jsonObject = JSONObject.parseObject(value);
//                        String title = jsonObject.getString("title");
//                        String content = jsonObject.getString("content");
//                        LogUtil.i("*********:"+title +" content:"+content);
                    }
                });
            }
        });
    }


    /**
     * JS调用APP接口
     */
    public class JsInterface {
//        //编辑文章
//        @JavascriptInterface
//        public void onEdit() {
//
//        }

    }

    public  String getUnicode(String s){
        String result = "";
        char[] c = s.toCharArray();
        for(char tmp:c){
          result += tmp;
        }
        System.out.println(result);
        return result;
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
//                    String title = titleEt.getText().toString();
//                    String content = contentEt.getText().toString();
//                    if(StringUtil.isEmpty(title) || StringUtil.isEmpty(content)) {
//                        ToastUtil.showToast(this,"标题或者正文不能为空");
//                        return;
//                    }
//                    Intent intent = new Intent(this,PublicExplainActivity.class);
//                    intent.putExtra("title",title);
//                    intent.putExtra("content",content);
//                    startActivity(intent);
                }
                break;
        }
    }

}
