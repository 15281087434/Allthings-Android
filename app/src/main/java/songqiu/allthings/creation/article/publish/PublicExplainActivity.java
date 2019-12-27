package songqiu.allthings.creation.article.publish;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bilibili.boxing.Boxing;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
import butterknife.OnClick;
import io.reactivex.functions.Consumer;
import songqiu.allthings.BuildConfig;
import songqiu.allthings.Event.EventTags;
import songqiu.allthings.R;
import songqiu.allthings.activity.BuddingBoxingActivity;
import songqiu.allthings.activity.MainActivity;
import songqiu.allthings.adapter.ArticleCoverAdapter;
import songqiu.allthings.adapter.ArticleLabelsAdapter;
import songqiu.allthings.base.BaseActivity;
import songqiu.allthings.bean.ArticleLabelsBean;
import songqiu.allthings.bean.SaveArticleBean;
import songqiu.allthings.bean.UploadPicBean;
import songqiu.allthings.home.gambit.JoinGambitActivity;
import songqiu.allthings.http.BaseBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.http.OkHttp;
import songqiu.allthings.http.RequestCallBack;
import songqiu.allthings.iterface.DialogPrivacyListener;
import songqiu.allthings.search.SearchActivity;
import songqiu.allthings.util.BoxingDefaultConfig;
import songqiu.allthings.util.ClickUtil;
import songqiu.allthings.util.FileUtil;
import songqiu.allthings.util.KeyBoardUtils;
import songqiu.allthings.util.LogUtil;
import songqiu.allthings.util.SharedPreferencedUtils;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.util.ToastUtil;
import songqiu.allthings.util.statusbar.StatusBarUtils;
import songqiu.allthings.view.DialogArticleCommon;
import songqiu.allthings.view.GridViewInScroll;
import songqiu.allthings.view.NoTitleDialog;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/12/20
 *
 *类描述：写文章标签
 *
 ********/
public class PublicExplainActivity extends BaseActivity {
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

    @BindView(R.id.coverHintTv)
    TextView coverHintTv;
    @BindView(R.id.gridView)
    GridViewInScroll gridView;
    @BindView(R.id.noPicLayout)
    LinearLayout noPicLayout;
    @BindView(R.id.bigPicLayout)
    LinearLayout bigPicLayout;
    @BindView(R.id.rightPicLayout)
    RelativeLayout rightPicLayout;
    @BindView(R.id.morePicLayout)
    RelativeLayout morePicLayout;
    @BindView(R.id.describeEt)
    EditText describeEt;
    @BindView(R.id.coverLayout)
    LinearLayout coverLayout;
    @BindView(R.id.gv_album)
    GridView gvAlbum;

    boolean canCamera;
    final int TAKE_PHOTOS_RESULT = 52;
    LinkedList<String> linkedList = new LinkedList<>();
    String add_url_tag = R.mipmap.item_setting_add_img + "";
    ArticleCoverAdapter gvAlbumAdapter;
    int maxNum = 0;
    int coverType; //定义一个封面类型的变量


    //标签
    List<ArticleLabelsBean> articleLabelsList;
    ArticleLabelsAdapter labelsAdapter;

    //保存参数 、上传文章参数
    String title;
    String content;
    String keywords;
    String descriptions;
    int module = 1; //1 无图 2大图 3左右 4三图
    String img; //单图，单图是字符串
    List<String> arryPic = new ArrayList<String>(); //多图，多图是数组

    @Override
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_creation_explain);
    }

    @Override
    protected void onResume() {
        super.onResume();
        applyPermission();
    }

    @Override
    public void init() {
        boolean dayModel = SharedPreferencedUtils.getBoolean(this, SharedPreferencedUtils.dayModel, true);
        modeUi(dayModel);
        titleTv.setText("");
        rightTv.setVisibility(View.VISIBLE);
        rightTv.setText("发布");
        coverLayout.setVisibility(View.GONE);
        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        initLabelGv();
        getLabels();
        initdescribeEt();
    }

    public void modeUi(boolean isDay) {
        if (isDay) {
            shadowLayout.setVisibility(View.GONE);
            StatusBarUtils.with(this)
                    .setColor(getResources().getColor(R.color.FFF9FAFD))
                    .init()
                    .setStatusTextColorAndPaddingTop(true, this);
        } else {
            shadowLayout.setVisibility(View.VISIBLE);
            StatusBarUtils.with(this)
                    .setColor(getResources().getColor(R.color.trans_6))
                    .init()
                    .setStatusTextColorAndPaddingTop(true, this);
        }
    }

    public void initdescribeEt() {
        describeEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable content) {
                if(!StringUtil.isEmpty(content.toString())) {
                    descriptions = content.toString();
                    setRightTvBg();
                }
            }
        });
    }

    public void initLabelGv() {
        articleLabelsList = new ArrayList<>();
        labelsAdapter = new ArticleLabelsAdapter(PublicExplainActivity.this,articleLabelsList);
        gridView.setAdapter(labelsAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(articleLabelsList.size()>position) {
                    if(articleLabelsList.get(position).isClick) {
                        articleLabelsList.get(position).isClick = false;
                        keywords = "";
                        setRightTvBg();
                    }else {
                        for(ArticleLabelsBean articleLabelsBean:articleLabelsList) {
                            articleLabelsBean.isClick = false;
                        }
                        articleLabelsList.get(position).isClick = true;
                        keywords = articleLabelsList.get(position).name;
                        setRightTvBg();
                    }
                }
                labelsAdapter.notifyDataSetChanged();
            }
        });
    }

    public void initGridView() {
        linkedList.clear();
        linkedList.add(add_url_tag);
        gvAlbumAdapter = new ArticleCoverAdapter(this, linkedList,maxNum);
        gvAlbum.setAdapter(gvAlbumAdapter);
        gvAlbumAdapter.setOnShowDialogListener(new ArticleCoverAdapter.ShowDialogListener() {
            @Override
            public void addImage() {
                if(canCamera) {
                    showDialog();
                }else {
                    ToastUtil.showToast(PublicExplainActivity.this,"请在设置-应用-见怪-权限中开启相机权限");
                }
            }

            @Override
            public void deleImage(int position) {
                new AlertDialog.Builder(mContext).setMessage("是否删除该图片？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        linkedList.remove(position);
                        gvAlbumAdapter.notifyDataSetChanged();
                        if(null != arryPic && arryPic.size()> position) {
                            arryPic.remove(position);
                            setRightTvBg();
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
                        Boxing.of(BoxingDefaultConfig.getInstance().getMultiConfig(maxNum+1 - linkedList.size()))
                                .withIntent(PublicExplainActivity.this, BuddingBoxingActivity.class)
                                .start(PublicExplainActivity.this, BoxingDefaultConfig.IMAGE_REQUEST_CODE);
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

    public void getLabels() {
        Map<String, String> map = new HashMap<>();
        OkHttp.post(this, HttpServicePath.URL_ARTICLE_LABEL, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        String data = gson.toJson(baseBean.data);
                        if (StringUtil.isEmpty(data)) return;
                        List<ArticleLabelsBean> list = gson.fromJson(data, new TypeToken<List<ArticleLabelsBean>>() {}.getType());
                        articleLabelsList.addAll(list);
                        labelsAdapter.notifyDataSetChanged();
                        getSaveArticle();
                    }
                });
            }
        });
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
                        //内容描述
                        if(!StringUtil.isEmpty(saveArticleBean.description)) {
                            describeEt.setText(saveArticleBean.description);
                        }
                        //作品标签
                        if(!StringUtil.isEmpty(saveArticleBean.keywords)) {
                            for(int i = 0;i<articleLabelsList.size();i++) {
                                if(articleLabelsList.get(i).name.equals(saveArticleBean.keywords)) {
                                    keywords = saveArticleBean.keywords;
                                    articleLabelsList.get(i).isClick = true;
                                    labelsAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                        //封面
                        if(saveArticleBean.module == 1) {
                            noPicClick();
                            setRightTvBg();
                        }else if(saveArticleBean.module == 2) {
                            bigPicClick();
                            if(!StringUtil.isEmpty(saveArticleBean.photo)) {
                                img = saveArticleBean.photo;
                                if(!saveArticleBean.photo.contains("http")) {
                                    saveArticleBean.photo = HttpServicePath.BasePicUrl + saveArticleBean.photo;
                                }
                                linkedList.add(linkedList.size() - 1, saveArticleBean.photo);
                                gvAlbumAdapter.notifyDataSetChanged();
                            }
                            setRightTvBg();
                        }else if(saveArticleBean.module == 3) {
                            rightPicClick();
                            if(!StringUtil.isEmpty(saveArticleBean.photo)) {
                                img = saveArticleBean.photo;
                                if(!saveArticleBean.photo.contains("http")) {
                                    saveArticleBean.photo = HttpServicePath.BasePicUrl + saveArticleBean.photo;
                                }
                                linkedList.add(linkedList.size() - 1, saveArticleBean.photo);
                                gvAlbumAdapter.notifyDataSetChanged();
                            }
                            setRightTvBg();
                        }else if(saveArticleBean.module == 4) {
                            morePicClick();
                            if(null != saveArticleBean.photos && 0 != saveArticleBean.photos.length) {
                                for(int i = 0;i<saveArticleBean.photos.length;i++) {
                                    arryPic.add(saveArticleBean.photos[i]);
                                    if(!saveArticleBean.photos[i].contains("http")) {
                                        saveArticleBean.photos[i] = HttpServicePath.BasePicUrl + saveArticleBean.photos[i];
                                        linkedList.add(linkedList.size() - 1, saveArticleBean.photos[i]);
                                    }
                                    gvAlbumAdapter.notifyDataSetChanged();
                                }
                            }
                            setRightTvBg();
                        }
                    }
                });
            }
        });
    }

    public void saveArticle() {
        Map<String, Object> map = new HashMap<>();
        map.put("title",title);
        map.put("descriptions",descriptions);
        map.put("content",content);
        map.put("keywords",keywords);
        map.put("module",module);
        if(module==2 || module ==3) {
            map.put("img",img);
        }else if(module==4) {
            map.put("imgs",arryPic);
        }
        OkHttp.postObject(this, HttpServicePath.URL_SAVES_ARTICLE, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(PublicExplainActivity.this,baseBean.msg);
                    }
                });
            }
        });
    }

    public void addArticle() {
        Map<String, Object> map = new HashMap<>();
        map.put("title",title);
        map.put("descriptions",descriptions);
        map.put("content",content);
        map.put("keywords",keywords);
        map.put("module",module);
        if(module==2 || module ==3) {
            map.put("img",img);
        }else if(module==4) {
            map.put("imgs",arryPic);
        }
        OkHttp.postObject(this, HttpServicePath.URL_ADD_ARTICLE, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(PublicExplainActivity.this,"已提交审核，审核通过后发布");
                        Intent intent = new Intent(PublicExplainActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
            }
        });
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
                        gvAlbumAdapter.notifyDataSetChanged();

                        //
                        if(module == 2 || module == 3) {
                            img = uploadPicBean.imgurl;
                        }
                        setRightTvBg();
                    }
                });
            }
        });
    }

    public void noPicClick() {
        coverLayout.setVisibility(View.GONE);
        noPicLayout.setBackgroundResource(R.drawable.rectangle_normal_white);
        bigPicLayout.setBackgroundResource(R.drawable.rectangle_f0f0f0_white);
        rightPicLayout.setBackgroundResource(R.drawable.rectangle_f0f0f0_white);
        morePicLayout.setBackgroundResource(R.drawable.rectangle_f0f0f0_white);
        maxNum = 0;
        module = 1;
        coverType = maxNum;
    }

    public void bigPicClick() {
        coverLayout.setVisibility(View.VISIBLE);
        coverHintTv.setText("（必须上传一张图片）");
        noPicLayout.setBackgroundResource(R.drawable.rectangle_f0f0f0_white);
        bigPicLayout.setBackgroundResource(R.drawable.rectangle_normal_white);
        rightPicLayout.setBackgroundResource(R.drawable.rectangle_f0f0f0_white);
        morePicLayout.setBackgroundResource(R.drawable.rectangle_f0f0f0_white);
        maxNum = 1;
        module = 2;
        arryPic.clear();
        if(coverType != maxNum) {
            coverType = maxNum;
            img = "";
            initGridView();
        }
    }

    public void rightPicClick() {
        coverLayout.setVisibility(View.VISIBLE);
        coverHintTv.setText("（必须上传一张图片）");
        noPicLayout.setBackgroundResource(R.drawable.rectangle_f0f0f0_white);
        bigPicLayout.setBackgroundResource(R.drawable.rectangle_f0f0f0_white);
        rightPicLayout.setBackgroundResource(R.drawable.rectangle_normal_white);
        morePicLayout.setBackgroundResource(R.drawable.rectangle_f0f0f0_white);
        maxNum = 1;
        module = 3;
        arryPic.clear();
        if(coverType != maxNum) {
            coverType = maxNum;
            img = "";
            initGridView();
        }
    }

    public void morePicClick() {
        coverLayout.setVisibility(View.VISIBLE);
        coverHintTv.setText("（必须上传三张图片）");
        noPicLayout.setBackgroundResource(R.drawable.rectangle_f0f0f0_white);
        bigPicLayout.setBackgroundResource(R.drawable.rectangle_f0f0f0_white);
        rightPicLayout.setBackgroundResource(R.drawable.rectangle_f0f0f0_white);
        morePicLayout.setBackgroundResource(R.drawable.rectangle_normal_white);
        maxNum = 3;
        module = 4;
        if(coverType != maxNum) {
            coverType = maxNum;
            img = "";
            initGridView();
        }
    }

    public boolean canPublic() {
        if(StringUtil.isEmpty(keywords))
            return false;
        if(module == 2 || module == 3) {
            if(StringUtil.isEmpty(img))
                return false;
        }
        if(module == 4) {
            if(arryPic.size() != 3) {
                return  false;
            }
        }

        if(StringUtil.isEmpty(descriptions) || descriptions.length()<20)
            return false;
        return true;
    }

    public void setRightTvBg() {
        if(canPublic()) {
            rightTv.setClickable(true);
            rightTv.setTextColor(getResources().getColor(R.color.normal_color));
        }else {
            rightTv.setClickable(false);
            rightTv.setTextColor(getResources().getColor(R.color.FFD2D2D2));
        }
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
                saveArticle();
            }
        });
    }

    @OnClick({R.id.backImg,R.id.saveTv,R.id.rightTv,R.id.noPicLayout,R.id.bigPicLayout,R.id.rightPicLayout,R.id.morePicLayout})
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
                    addArticle();
                }
                break;
            case R.id.noPicLayout:
                noPicClick();
                setRightTvBg();
                break;
            case R.id.bigPicLayout:
                bigPicClick();
                setRightTvBg();
                break;
            case R.id.rightPicLayout:
                rightPicClick();
                setRightTvBg();
                break;
            case R.id.morePicLayout:
                morePicClick();
                setRightTvBg();
                break;
        }
    }

}
