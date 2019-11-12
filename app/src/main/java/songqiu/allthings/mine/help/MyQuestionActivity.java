package songqiu.allthings.mine.help;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bilibili.boxing.Boxing;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import songqiu.allthings.R;
import songqiu.allthings.activity.BuddingBoxingActivity;
import songqiu.allthings.adapter.GvAlbumAdapter;
import songqiu.allthings.base.BaseActivity;
import songqiu.allthings.bean.UploadPicBean;
import songqiu.allthings.http.BaseBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.http.OkHttp;
import songqiu.allthings.http.RequestCallBack;
import songqiu.allthings.iterface.IEditTextChangeListener;
import songqiu.allthings.mine.userpage.ModificationInfoActivity;
import songqiu.allthings.util.BoxingDefaultConfig;
import songqiu.allthings.util.ClickUtil;
import songqiu.allthings.util.EditTextCheckUtil;
import songqiu.allthings.util.GlideCircleTransform;
import songqiu.allthings.util.LogUtil;
import songqiu.allthings.util.SharedPreferencedUtils;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.util.TextVerifyUtil;
import songqiu.allthings.util.ToastUtil;
import songqiu.allthings.util.statusbar.StatusBarUtils;
import songqiu.allthings.util.theme.ThemeManager;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/8/30
 *
 *类描述：我的提问反馈
 *
 ********/
public class MyQuestionActivity extends BaseActivity {
    @BindView(R.id.backImg)
    ImageView backImg;
    @BindView(R.id.titleTv)
    TextView titleTv;
    @BindView(R.id.contentEt)
    EditText contentEt;
    @BindView(R.id.numTv)
    TextView numTv;
    @BindView(R.id.gv_album)
    GridView gvAlbum;
    @BindView(R.id.phoneEt)
    EditText phoneEt;
    @BindView(R.id.submitTv)
    TextView submitTv;
    @BindView(R.id.picnumTv)
    TextView picnumTv;
    @BindView(R.id.hintTv)
    TextView hintTv;
    @BindView(R.id.shadowLayout)
    LinearLayout shadowLayout;


    LinkedList<String> linkedList = new LinkedList<>();
    String add_url_tag = R.mipmap.icon_setting_add_img + "";
    GvAlbumAdapter gvAlbumAdapter;
    List<String> arryPic = new ArrayList<String>();

    @Override
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_my_question);
    }

    @Override
    public void init() {
        boolean dayModel = SharedPreferencedUtils.getBoolean(this,SharedPreferencedUtils.dayModel,true);
        modeUi(dayModel);
        titleTv.setText("我的反馈");
        initEt();
        initGridView();
        initTv();
        EditTextCheckUtil.textChangeListener textChangeListener = new EditTextCheckUtil.textChangeListener(submitTv);
        textChangeListener.addAllEditText(contentEt, phoneEt);
        EditTextCheckUtil.setChangeListener(new IEditTextChangeListener() {
            @Override
            public void textChange(boolean isHasContent) {
                if (isHasContent) {
                    submitTv.setBackgroundResource(R.mipmap.common_botton);
                } else {
                    submitTv.setBackgroundResource(R.drawable.rectangle_common_login_999999);
                }
            }
        });
    }


    public void modeUi(boolean isDay) {
        if(isDay) {
            shadowLayout.setVisibility(View.GONE);
            StatusBarUtils.with(this)
                    .setColor(getResources().getColor(R.color.FFF9FAFD))
                    .init()
                    .setStatusTextColorAndPaddingTop(true, this);
        }else {
            shadowLayout.setVisibility(View.VISIBLE);
            StatusBarUtils.with(this)
                    .setColor(getResources().getColor(R.color.trans_6))
                    .init()
                    .setStatusTextColorAndPaddingTop(true, this);
        }
    }

    public void initTv() {
        SpannableString spannableString = new SpannableString(hintTv.getText().toString());
        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.FFDE5C51)), 4, 5, 0);
        hintTv.setText(spannableString);
    }

    public void initEt() {
        contentEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence content, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String content = s.toString();
                if (!TextUtils.isEmpty(content)) {
                    numTv.setText(content.length() + "/300");
                } else {
                    numTv.setText("0/300");
                }
            }
        });
    }

    public void initGridView() {
        linkedList.add(add_url_tag);
        gvAlbumAdapter = new GvAlbumAdapter(this, linkedList,3);
        gvAlbum.setAdapter(gvAlbumAdapter);
        gvAlbumAdapter.setOnShowDialogListener(new GvAlbumAdapter.ShowDialogListener() {
            @Override
            public void addImage() {
                Boxing.of(BoxingDefaultConfig.getInstance().getMultiConfig(4 - linkedList.size()))
                        .withIntent(MyQuestionActivity.this, BuddingBoxingActivity.class)
                        .start(MyQuestionActivity.this, BoxingDefaultConfig.IMAGE_REQUEST_CODE);
            }

            @Override
            public void deleImage(int position) {
                new AlertDialog.Builder(mContext).setMessage("是否删除该图片？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        linkedList.remove(position);
//                       EventBus.getDefault().post(new EventTags.RefreshGroupVoiceNumber(position));
                        gvAlbumAdapter.notifyDataSetChanged();
                        if(null != arryPic && arryPic.size()>= position) {
                            arryPic.remove(position);
                        }
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        BoxingDefaultConfig.getCompressedBitmap(this, requestCode, data, new BoxingDefaultConfig.OnLuBanCompressed() {
            @Override
            public void onCompressed(List<File> files) {
                if (null != files && 0 != files.size()) {
                    for (int i = 0; i < files.size(); i++) {
                        linkedList.add(linkedList.size() - 1, files.get(i).getPath());
                        uploadPic(files.get(i).getPath());
                      }
                    picnumTv.setText(linkedList.size()-1+"/3");
                    gvAlbumAdapter.notifyDataSetChanged();
                    }
                }
        });
    }

    public void uploadPic(String path) {
        File file = new File(path);
        Map<String, String> map = new HashMap<>();
        map.put("file",file.getName());
        OkHttp.postFile(this, HttpServicePath.URL_UPLOADS,map,file,new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        String data = gson.toJson(baseBean.data);
                        UploadPicBean uploadPicBean = gson.fromJson(data, UploadPicBean.class);
                        if(null == uploadPicBean) return;
                        arryPic.add(uploadPicBean.imgurl);
                    }
                });
            }
        });
    }

    public void submitQuestion(String content,String phone) {
        Map<String, Object> map = new HashMap<>();
        map.put("feedback_con",content);
        map.put("feedback_img",arryPic);
        map.put("mobile",phone);
        OkHttp.postObject(this, HttpServicePath.URL_FEED_BACK, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(MyQuestionActivity.this,FeedbackAndHelpActivity.class);
                        intent.putExtra("position",1);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });
    }

    @OnClick({R.id.backImg,R.id.submitTv})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.backImg:
                finish();
                break;
            case R.id.submitTv:
                if(ClickUtil.onClick()) {
                    String content = contentEt.getText().toString().trim();
                    if(TextUtils.isEmpty(content)) {
                        ToastUtil.showToast(this,"请输入问题或意见");
                        return;
                    }
                    String phone = phoneEt.getText().toString().trim();
                    if(TextUtils.isEmpty(phone)) {
                        ToastUtil.showToast(this,"手机号不能为空");
                        break;
                    }
                    if (!TextVerifyUtil.checkMobile(phone)) return;
                    submitQuestion(content,phone);
                }
        }
    }

}
