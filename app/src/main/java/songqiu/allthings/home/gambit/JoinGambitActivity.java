package songqiu.allthings.home.gambit;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
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
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

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
import io.reactivex.functions.Consumer;
import songqiu.allthings.BuildConfig;
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
import songqiu.allthings.mine.userpage.ModificationInfoActivity;
import songqiu.allthings.util.BoxingDefaultConfig;
import songqiu.allthings.util.ClickUtil;
import songqiu.allthings.util.DensityUtil;
import songqiu.allthings.util.FileUtil;
import songqiu.allthings.util.GlideLoadUtils;
import songqiu.allthings.util.LogUtil;
import songqiu.allthings.util.SharedPreferencedUtils;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.util.ToastUtil;
import songqiu.allthings.util.statusbar.StatusBarUtils;
import songqiu.allthings.util.theme.ThemeManager;
import songqiu.allthings.view.MyEditText;
import songqiu.allthings.view.NoTitleDialog;

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
    int maxPicNum = 9;
    boolean canCamera;
    final int TAKE_PHOTOS_RESULT = 52;


    @Override
    protected void onResume() {
        super.onResume();
        applyPermission();
    }

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
            titleGambitTv.setText("#"+hintGambit+"#");
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

    public void applyPermission() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions
                .requestEach(
                        Manifest.permission.CAMERA
                )
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.name.equals(Manifest.permission.CAMERA)) {
                            if (permission.granted) {
                                canCamera = true;
                            } else if (permission.shouldShowRequestPermissionRationale) {
                                // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                                canCamera = false;
                            } else {
                                // 用户拒绝了该权限，并且选中『不再询问』
                                canCamera = false;
                            }
                        }
                    }
                });

    }

    public void initGridView() {
        linkedList.add(add_url_tag);
        gvAlbumAdapter = new GvAlbumAdapter(this, linkedList,maxPicNum);
        gvAlbum.setAdapter(gvAlbumAdapter);
        gvAlbumAdapter.setOnShowDialogListener(new GvAlbumAdapter.ShowDialogListener() {
            @Override
            public void addImage() {
                if(canCamera) {
                    showDialog();
                }else {
                    ToastUtil.showToast(JoinGambitActivity.this,"请在设置-应用-见怪-权限中开启相机权限");
                }
            }

            @Override
            public void deleImage(int position) {
                new AlertDialog.Builder(mContext).setMessage("是否删除该图片？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        linkedList.remove(position);
                        gvAlbumAdapter.notifyDataSetChanged();
                        picnumTv.setText(linkedList.size()-1+"/9");
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

    private void showDialog() {
        NoTitleDialog dialog = new NoTitleDialog(this);
        dialog.showDialog();
        dialog.setOnItemClickListener(new NoTitleDialog.OnItemClick() {
            @Override
            public void onWhichItemClick(int pos) {
                switch (pos) {
                    case 0:
                        takePhotos();
                        break;
                    case 1:
                        Boxing.of(BoxingDefaultConfig.getInstance().getMultiConfig(10 - linkedList.size()))
                            .withIntent(JoinGambitActivity.this, BuddingBoxingActivity.class)
                            .start(JoinGambitActivity.this, BoxingDefaultConfig.IMAGE_REQUEST_CODE);
                        break;
                    case 2:
                        break;
                }
                if (dialog != null && dialog.isShowing())
                    dialog.dismiss();
            }
        });
    }

    // 拍照
    public void takePhotos() {
        Uri contentUri;
        File file = new File(FileUtil.getTakePhotoPath("songqiu.allthings"));
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            contentUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileProvider", file);
        }else {
            contentUri =  Uri.parse("file://"+file.getAbsolutePath());
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
        startActivityForResult(intent, TAKE_PHOTOS_RESULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_PHOTOS_RESULT && resultCode == RESULT_OK) {
            String path = FileUtil.checkLsength(FileUtil
                    .getTakePhotoPath("songqiu.allthings"));
            uploadPic(path);
        }else {
            BoxingDefaultConfig.getCompressedBitmap(this, requestCode, data, new BoxingDefaultConfig.OnLuBanCompressed() {
                @Override
                public void onCompressed(List<File> files) {
                    if (null != files && 0 != files.size()) {
                        for (int i = 0; i < files.size(); i++) {
                            uploadPic(files.get(i).getPath());
                        }
                    }
                }
            });
        }
    }

    public void uploadPic(String path) {
        showLoading();
        File file = new File(path);
        Map<String, String> map = new HashMap<>();
        map.put("file",file.getName());
        OkHttp.postFile(this, mDialog,HttpServicePath.URL_UPLOADS,map,file,new RequestCallBack() {
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
                        //
                        linkedList.add(linkedList.size() - 1, path);
                        picnumTv.setText(linkedList.size()-1+"/9");
                        gvAlbumAdapter.notifyDataSetChanged();
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
