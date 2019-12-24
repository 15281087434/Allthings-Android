package songqiu.allthings.creation.article;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
import songqiu.allthings.adapter.ArticleCoverAdapter;
import songqiu.allthings.adapter.ArticleLabelsAdapter;
import songqiu.allthings.adapter.GvAlbumAdapter;
import songqiu.allthings.base.BaseActivity;
import songqiu.allthings.bean.ArticleLabelsBean;
import songqiu.allthings.bean.LookVideoBean;
import songqiu.allthings.bean.UserInfoBean;
import songqiu.allthings.http.BaseBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.http.OkHttp;
import songqiu.allthings.http.RequestCallBack;
import songqiu.allthings.mine.help.MyQuestionActivity;
import songqiu.allthings.util.BoxingDefaultConfig;
import songqiu.allthings.util.SharedPreferencedUtils;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.util.statusbar.StatusBarUtils;
import songqiu.allthings.view.GridViewInScroll;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/12/20
 *
 *类描述：写文章
 *
 ********/
public class CreationExplainActivity extends BaseActivity {
    @BindView(R.id.backImg)
    ImageView backImg;
    @BindView(R.id.titleTv)
    TextView titleTv;
    @BindView(R.id.rightTv)
    TextView rightTv;
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

    LinkedList<String> linkedList = new LinkedList<>();
    String add_url_tag = R.mipmap.item_setting_add_img + "";
    ArticleCoverAdapter gvAlbumAdapter;
    List<String> arryPic = new ArrayList<String>();
    int maxNum = 0;
    int coverType; //定义一个封面类型的变量

    //标签
    List<ArticleLabelsBean> articleLabelsList;
    ArticleLabelsAdapter labelsAdapter;

    @Override
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_creation_explain);
    }

    @Override
    public void init() {
        boolean dayModel = SharedPreferencedUtils.getBoolean(this, SharedPreferencedUtils.dayModel, true);
        modeUi(dayModel);
        titleTv.setText("");
        rightTv.setVisibility(View.VISIBLE);
        rightTv.setText("发布");
        coverLayout.setVisibility(View.GONE);
        initLabelGv();
        getLabels();
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

    public void initLabelGv() {
        articleLabelsList = new ArrayList<>();
        labelsAdapter = new ArticleLabelsAdapter(CreationExplainActivity.this,articleLabelsList);
        gridView.setAdapter(labelsAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(articleLabelsList.size()>position) {
                    if(articleLabelsList.get(position).isClick) {
                        articleLabelsList.get(position).isClick = false;
                    }else {
                        for(ArticleLabelsBean articleLabelsBean:articleLabelsList) {
                            articleLabelsBean.isClick = false;
                        }
                        articleLabelsList.get(position).isClick = true;
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
                Boxing.of(BoxingDefaultConfig.getInstance().getMultiConfig((maxNum+1) - linkedList.size()))
                        .withIntent(CreationExplainActivity.this, BuddingBoxingActivity.class)
                        .start(CreationExplainActivity.this, BoxingDefaultConfig.IMAGE_REQUEST_CODE);
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
                    }
                });
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
//                        uploadPic(files.get(i).getPath());
                    }
//                    picnumTv.setText(linkedList.size()-1+"/3");
                    gvAlbumAdapter.notifyDataSetChanged();
                }
            }
        });
    }


    @OnClick({R.id.backImg,R.id.rightTv,R.id.noPicLayout,R.id.bigPicLayout,R.id.rightPicLayout,R.id.morePicLayout})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.backImg:
                finish();
                break;
            case R.id.rightTv:

                break;
            case R.id.noPicLayout:
                coverLayout.setVisibility(View.GONE);
                noPicLayout.setBackgroundResource(R.drawable.rectangle_normal_white);
                bigPicLayout.setBackgroundResource(R.drawable.rectangle_f0f0f0_white);
                rightPicLayout.setBackgroundResource(R.drawable.rectangle_f0f0f0_white);
                morePicLayout.setBackgroundResource(R.drawable.rectangle_f0f0f0_white);
                maxNum = 0;
                coverType = maxNum;
                break;
            case R.id.bigPicLayout:
                coverLayout.setVisibility(View.VISIBLE);
                coverHintTv.setText("（必须上传一张图片）");
                noPicLayout.setBackgroundResource(R.drawable.rectangle_f0f0f0_white);
                bigPicLayout.setBackgroundResource(R.drawable.rectangle_normal_white);
                rightPicLayout.setBackgroundResource(R.drawable.rectangle_f0f0f0_white);
                morePicLayout.setBackgroundResource(R.drawable.rectangle_f0f0f0_white);
                maxNum = 1;
                if(coverType != maxNum) {
                    coverType = maxNum;
                    initGridView();
                }
                break;
            case R.id.rightPicLayout:
                coverLayout.setVisibility(View.VISIBLE);
                coverHintTv.setText("（必须上传一张图片）");
                noPicLayout.setBackgroundResource(R.drawable.rectangle_f0f0f0_white);
                bigPicLayout.setBackgroundResource(R.drawable.rectangle_f0f0f0_white);
                rightPicLayout.setBackgroundResource(R.drawable.rectangle_normal_white);
                morePicLayout.setBackgroundResource(R.drawable.rectangle_f0f0f0_white);
                maxNum = 1;
                if(coverType != maxNum) {
                    coverType = maxNum;
                    initGridView();
                }
                break;
            case R.id.morePicLayout:
                coverLayout.setVisibility(View.VISIBLE);
                coverHintTv.setText("（必须上传三张图片）");
                noPicLayout.setBackgroundResource(R.drawable.rectangle_f0f0f0_white);
                bigPicLayout.setBackgroundResource(R.drawable.rectangle_f0f0f0_white);
                rightPicLayout.setBackgroundResource(R.drawable.rectangle_f0f0f0_white);
                morePicLayout.setBackgroundResource(R.drawable.rectangle_normal_white);
                maxNum = 3;
                if(coverType != maxNum) {
                    coverType = maxNum;
                    initGridView();
                }
                break;
        }
    }

}
