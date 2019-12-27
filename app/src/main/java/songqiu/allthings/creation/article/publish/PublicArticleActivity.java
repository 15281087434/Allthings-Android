package songqiu.allthings.creation.article.publish;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import songqiu.allthings.R;
import songqiu.allthings.base.BaseActivity;
import songqiu.allthings.bean.SaveArticleBean;
import songqiu.allthings.bean.UserInfoBean;
import songqiu.allthings.http.BaseBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.http.OkHttp;
import songqiu.allthings.http.RequestCallBack;
import songqiu.allthings.iterface.DialogPrivacyListener;
import songqiu.allthings.iterface.IEditTextChangeListener;
import songqiu.allthings.util.ClickUtil;
import songqiu.allthings.util.EditTextCheckUtil;
import songqiu.allthings.util.SharedPreferencedUtils;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.util.ToastUtil;
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

    @BindView(R.id.titleEt)
    EditText titleEt;
    @BindView(R.id.contentEt)
    EditText contentEt;

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
        EditTextCheckUtil.textChangeListener textChangeListener = new EditTextCheckUtil.textChangeListener(rightTv);
        textChangeListener.addAllEditText(titleEt, contentEt);
        EditTextCheckUtil.setChangeListener(new IEditTextChangeListener() {
            @Override
            public void textChange(boolean isHasContent) {
                if (isHasContent) {
                    rightTv.setTextColor(getResources().getColor(R.color.normal_color));
                    rightTv.setClickable(true);
                } else {
                    rightTv.setTextColor(getResources().getColor(R.color.FFD2D2D2));
                    rightTv.setClickable(false);
                }
            }
        });
        getSaveArticle();
    }


    public void modeUi(boolean isDay) {
        if(isDay) {
            shadowLayout.setVisibility(View.GONE);
        }else {
            shadowLayout.setVisibility(View.VISIBLE);
        }
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
                        titleEt.setText(saveArticleBean.title);
                        contentEt.setText(saveArticleBean.content);
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
                String title = titleEt.getText().toString();
                String content = contentEt.getText().toString();
                saveArticle(title,content);
            }
        });
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
                    String title = titleEt.getText().toString();
                    String content = contentEt.getText().toString();
                    if(StringUtil.isEmpty(title) || StringUtil.isEmpty(content)) {
                        ToastUtil.showToast(this,"标题或者正文不能为空");
                        return;
                    }
                    Intent intent = new Intent(this,PublicExplainActivity.class);
                    intent.putExtra("title",title);
                    intent.putExtra("content",content);
                    startActivity(intent);
                }
                break;
        }
    }

}
