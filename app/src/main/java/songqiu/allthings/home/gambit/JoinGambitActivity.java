package songqiu.allthings.home.gambit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bilibili.boxing.Boxing;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import songqiu.allthings.Event.EventTags;
import songqiu.allthings.R;
import songqiu.allthings.activity.BuddingBoxingActivity;
import songqiu.allthings.adapter.GvAlbumAdapter;
import songqiu.allthings.base.BaseActivity;
import songqiu.allthings.bean.UploadPicBean;
import songqiu.allthings.http.BaseBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.http.OkHttp;
import songqiu.allthings.http.RequestCallBack;
import songqiu.allthings.mine.help.FeedbackAndHelpActivity;
import songqiu.allthings.mine.help.MyQuestionActivity;
import songqiu.allthings.util.BoxingDefaultConfig;
import songqiu.allthings.util.ClickUtil;
import songqiu.allthings.util.DensityUtil;
import songqiu.allthings.util.LogUtil;
import songqiu.allthings.util.SharedPreferencedUtils;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.util.ToastUtil;
import songqiu.allthings.util.statusbar.StatusBarUtils;
import songqiu.allthings.util.theme.ThemeManager;
import songqiu.allthings.view.MyEditText;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/9/19
 *
 *类描述：参与话题、编辑内容
 *
 ********/
public class JoinGambitActivity extends BaseActivity {

    @BindView(R.id.backImg)
    ImageView backImg;
    @BindView(R.id.titleTv)
    TextView titleTv;
    @BindView(R.id.rightTv)
    TextView rightTv;
    @BindView(R.id.line)
    LinearLayout line;
    @BindView(R.id.contentEt)
    EditText contentEt;
    @BindView(R.id.titleGambitTv)
    TextView titleGambitTv;
    @BindView(R.id.numTv)
    TextView numTv;
    @BindView(R.id.gv_album)
    GridView gvAlbum;
    @BindView(R.id.picnumTv)
    TextView picnumTv;
    @BindView(R.id.submitTv)
    TextView submitTv;
    @BindView(R.id.shadowLayout)
    LinearLayout shadowLayout;

    int talk_id;
    int talktype = 1;
    String hintGambit;

    LinkedList<String> linkedList = new LinkedList<>();
    String add_url_tag = R.mipmap.icon_setting_add_img + "";
    GvAlbumAdapter gvAlbumAdapter;
    List<String> arryPic = new ArrayList<String>();


    @Override
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_join_gambit);
    }

    @Override
    public void init() {
        boolean dayModel = SharedPreferencedUtils.getBoolean(this,SharedPreferencedUtils.dayModel,true);
        modeUi(dayModel);
        hintGambit = getIntent().getStringExtra("hintGambit");
        talk_id = getIntent().getIntExtra("talk_id",0);
        talktype = getIntent().getIntExtra("talktype",1);
        if(1==talktype) {
            titleTv.setText("参与话题");
            titleGambitTv.setText(hintGambit);
        }else {
            titleTv.setText("发布动态");
            titleGambitTv.setVisibility(View.GONE);
            contentEt.setHint("请输入内容");
        }
        initEt();
        initGridView();
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
                    numTv.setText(content.length() + "/10000");
                } else {
                    numTv.setText("0/10000");
                }
            }
        });
    }

    public void initGridView() {
        linkedList.add(add_url_tag);
        gvAlbumAdapter = new GvAlbumAdapter(this, linkedList,9);
        gvAlbum.setAdapter(gvAlbumAdapter);
        gvAlbumAdapter.setOnShowDialogListener(new GvAlbumAdapter.ShowDialogListener() {
            @Override
            public void addImage() {
                Boxing.of(BoxingDefaultConfig.getInstance().getMultiConfig(10 - linkedList.size()))
                        .withIntent(JoinGambitActivity.this, BuddingBoxingActivity.class)
                        .start(JoinGambitActivity.this, BoxingDefaultConfig.IMAGE_REQUEST_CODE);
            }

            @Override
            public void deleImage(int position) {
                new AlertDialog.Builder(mContext).setMessage("是否删除该图片？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        linkedList.remove(position);
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
                    picnumTv.setText(linkedList.size()-1+"/9");
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

    public void submit(String description) {
        Map<String, Object> map = new HashMap<>();
        map.put("talktype",talktype);
        if(0!=talk_id) {
            map.put("talk_id",talk_id);
        }
        map.put("description",description);
        map.put("images",arryPic);
        OkHttp.postObject(this, HttpServicePath.URL_JOIN_ADD_TALK, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(1==talktype) {
                            EventBus.getDefault().post(new EventTags.HotGambitDetailRefresh());
                        }else {
                            EventBus.getDefault().post(new EventTags.GambitRefresh());
                        }
                        ToastUtil.showToast(JoinGambitActivity.this,baseBean.msg);
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
                    String description = contentEt.getText().toString().trim();
                    if(StringUtil.isEmpty(description)) {
                        ToastUtil.showToast(this,"请输入内容");
                        return;
                    }
                    StringBuffer sb = new StringBuffer();
                    sb.append(hintGambit);
                    sb.append(description);
                    submit(description);
                }
                break;
        }
    }

}
