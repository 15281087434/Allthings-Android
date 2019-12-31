package songqiu.allthings.auth.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bilibili.boxing.Boxing;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;
import songqiu.allthings.BuildConfig;
import songqiu.allthings.R;
import songqiu.allthings.activity.BuddingBoxingActivity;
import songqiu.allthings.activity.GuideActivity;
import songqiu.allthings.base.BaseActivity;
import songqiu.allthings.bean.ProvinceBean;
import songqiu.allthings.bean.UploadPicBean;
import songqiu.allthings.http.BaseBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.http.OkHttp;
import songqiu.allthings.http.RequestCallBack;
import songqiu.allthings.iterface.CommentListener;
import songqiu.allthings.mine.userpage.ModificationInfoActivity;
import songqiu.allthings.util.BoxingDefaultConfig;
import songqiu.allthings.util.ClickUtil;
import songqiu.allthings.util.FileUtil;
import songqiu.allthings.util.GlideLoadUtils;
import songqiu.allthings.util.JsUtils;
import songqiu.allthings.util.KeyBoardUtils;
import songqiu.allthings.util.SharedPreferencedUtils;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.util.ToastUtil;
import songqiu.allthings.util.statusbar.StatusBarUtils;
import songqiu.allthings.view.ChooseSixDialog;
import songqiu.allthings.view.CommentWindow;
import songqiu.allthings.view.NoTitleDialog;
import songqiu.allthings.view.SuperImageView;

/**
 * create by: linyinjianying
 * time:
 * e_mail:734090232@qq.com
 * description:实名认证页
 */
public class RealNameAuthActivity extends BaseActivity {
    public final int REQ_REALNAME = 0x101;
    public final int REQ_CARDID = 0x102;
    public final int REQ_ADREESS_DETAILS = 0x103;
    public final int REQ_EMAIL = 0x104;
    public final int REQ_UPLOADIMG = 0x105;
    ArrayList<ProvinceBean> options1Items = new ArrayList<>();
    ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();
    OptionsPickerView mProvincePv;
    @BindView(R.id.backImg)
    ImageView backImg;
    @BindView(R.id.titleTv)
    TextView titleTv;
    @BindView(R.id.rightTv)
    TextView rightTv;
    @BindView(R.id.line)
    LinearLayout line;
    @BindView(R.id.iv_card_forground)
    SuperImageView ivCardForground;

    @BindView(R.id.iv_card_back)

    SuperImageView ibCardBack;
    @BindView(R.id.iv_card_hand)

    SuperImageView ibCardHand;
    @BindView(R.id.tv_realname)
    TextView tvRealname;
    @BindView(R.id.tv_cardid)
    TextView tvCardid;
    @BindView(R.id.tv_sex)
    TextView tvSex;
    @BindView(R.id.tv_email)
    TextView tvEmail;
    @BindView(R.id.tv_adress)
    TextView tvAdress;
    @BindView(R.id.tv_adress_details)
    TextView tvAdressDetails;
    @BindView(R.id.tv_auth)
    TextView tvAuth;
    @BindView(R.id.ll_authing)
    LinearLayout llAuthing;
    @BindView(R.id.ll_content)
    LinearLayout llContent;
    @BindView(R.id.sv_content)
    ScrollView svContent;
    private String realname, email, address, addressDetails, cardid, cardIdFront, cardIdBack, cardIdHand;
    private String sex;
    boolean isAuthing;
    int imageLoadType = 0;
    @BindView(R.id.shadowLayout)
    LinearLayout shadowLayout;

    @Override
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_realname_auth);
        ButterKnife.bind(this);
        boolean dayModel = SharedPreferencedUtils.getBoolean(this, SharedPreferencedUtils.dayModel, true);
        modeUi(dayModel);
        titleTv.setText("实名认证");
        isAuthing = getIntent().getIntExtra("V_status", 0) == 1;
        if (isAuthing) {
            svContent.setVisibility(View.GONE);
            llAuthing.setVisibility(View.VISIBLE);
        } else {
            svContent.setVisibility(View.VISIBLE);
            llAuthing.setVisibility(View.GONE);
        }


    }

    @Override
    public void init() {
        initJsonData();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

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

    int type = 0;

    @OnClick({R.id.tv_realname, R.id.tv_adress_details, R.id.tv_cardid, R.id.tv_email})
    public void showEdit(View view) {
        type = 0;
        int inputType = 0;
        switch (view.getId()) {
            case R.id.tv_realname:
                type = REQ_REALNAME;
                break;
            case R.id.tv_adress_details:
                type = REQ_ADREESS_DETAILS;
                break;
            case R.id.tv_email:
                type = REQ_EMAIL;
                break;
            case R.id.tv_cardid:

                type = REQ_CARDID;
                break;
        }
        if (type != 0) {

            CommentWindow fw = new CommentWindow(this, "", "完成");
            fw.showAtLocation(titleTv, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            fw.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    WindowManager.LayoutParams lp = getWindow().getAttributes();
                    lp.alpha = 1.0f;
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                    getWindow().setAttributes(lp);
                }
            });
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.alpha = 0.6f;
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            getWindow().setAttributes(lp);
            fw.setCommentListener(new CommentListener() {
                @Override
                public void publishComment(String comment) {
                    switch (type) {
                        case REQ_REALNAME:
                            realname = comment;
                            tvRealname.setText(realname);

                            break;
                        case REQ_CARDID:
                            cardid = comment;
                            tvCardid.setText(cardid);

                            break;
                        case REQ_ADREESS_DETAILS:
                            addressDetails = comment;
                            tvAdressDetails.setText(addressDetails);

                            break;
                        case REQ_EMAIL:
                            email = comment;
                            tvEmail.setText(email);

                            break;

                    }
                    fw.dismiss();
                }
            });
        }
    }

    @OnClick(R.id.backImg)
    public void onBack() {
        finish();
    }

    @OnClick({R.id.iv_card_forground, R.id.iv_card_back, R.id.iv_card_hand})
    public void upLoadCardIdPic(View view) {
        imageLoadType = 0;
        switch (view.getId()) {
            case R.id.iv_card_forground:
                imageLoadType = 1;
                break;
            case R.id.iv_card_back:
                imageLoadType = 2;
                break;
            case R.id.iv_card_hand:
                imageLoadType = 3;
                break;
        }
        if (imageLoadType == 0) {
            return;
        }
        showDialog();
    }

    public void uploadPic(String path, int imageLoadType) {
        showLoading();

        File file = new File(path);
        Map<String, String> map = new HashMap<>();
        map.put("file", file.getName());
        OkHttp.postFile(this, mDialog, HttpServicePath.URL_UPLOADS, map, file, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        String data = gson.toJson(baseBean.data);
                        UploadPicBean uploadPicBean = gson.fromJson(data, UploadPicBean.class);
                        if (null == uploadPicBean) return;
                        if (!StringUtil.isEmpty(uploadPicBean.imgurl)) {
                            if (!uploadPicBean.imgurl.contains("http")) {
                                if (uploadPicBean.imgurl.contains("\"")) {
                                    uploadPicBean.imgurl.replace("\"", "");
                                }
                                uploadPicBean.imgurl = HttpServicePath.BasePicUrl + uploadPicBean.imgurl;

                            } else {
                                if (uploadPicBean.imgurl.contains("\"")) {
                                    uploadPicBean.imgurl.replace("\"", "");
                                }

                            }
                        }
                        if (imageLoadType == 1) {
                            cardIdFront = uploadPicBean.imgurl;
                            GlideLoadUtils.getInstance().glideLoadNoDefault(RealNameAuthActivity.this, cardIdFront, ivCardForground);
                        } else if (imageLoadType == 2) {
                            cardIdBack = uploadPicBean.imgurl;
                            GlideLoadUtils.getInstance().glideLoadNoDefault(RealNameAuthActivity.this, cardIdBack, ibCardBack);
                        } else if (imageLoadType == 3) {
                            cardIdHand = uploadPicBean.imgurl;
                            GlideLoadUtils.getInstance().glideLoadNoDefault(RealNameAuthActivity.this, cardIdHand, ibCardHand);
                        }

                    }
                });
            }
        });
    }

    @OnClick(R.id.tv_sex)
    public void showSexDialog() {
        ChooseSixDialog dialog = new ChooseSixDialog(this);
        dialog.showDialog();
        dialog.setOnItemClickListener(new ChooseSixDialog.OnItemClick() {
            @Override
            public void onWhichItemClick(int pos) {
                switch (pos) {
                    case 0:
                        sex = "1";
                        tvSex.setText("男");
                        break;
                    case 1:
                        sex = "2";
                        tvSex.setText("女");
                        break;
                    case 2:
                        break;
                }
                if (dialog != null && dialog.isShowing())
                    dialog.dismiss();
            }
        });
    }

    /**
     * 解析省市json数据
     */
    private void initJsonData() {
        /**
         * 注意：assets 目录下的Json文件仅供参考，实际使用可自行替换文件
         * 关键逻辑在于循环体
         *
         * */
        String JsonData = JsUtils.getJson(this, "province.json");//获取assets目录下的json文件数据

        ArrayList<ProvinceBean> jsonBean = parseData(JsonData);//用Gson 转成实体

        /**
         * 添加省份数据
         *
         * 注意：如果是添加的JavaBean实体，则实体类需要实现 IPickerViewData 接口，
         * PickerView会通过getPickerViewText方法获取字符串显示出来。
         */
        options1Items = jsonBean;

        for (int i = 0; i < jsonBean.size(); i++) {//遍历省份
            ArrayList<String> CityList = new ArrayList<>();//该省的城市列表（第二级）
            ArrayList<ArrayList<String>> Province_AreaList = new ArrayList<>();//该省的所有地区列表（第三极）

            for (int c = 0; c < jsonBean.get(i).getCityList().size(); c++) {//遍历该省份的所有城市
                String CityName = jsonBean.get(i).getCityList().get(c).getName();
                CityList.add(CityName);//添加城市
                ArrayList<String> City_AreaList = new ArrayList<>();//该城市的所有地区列表

                //如果无地区数据，建议添加空字符串，防止数据为null 导致三个选项长度不匹配造成崩溃
                if (jsonBean.get(i).getCityList().get(c).getArea() == null
                        || jsonBean.get(i).getCityList().get(c).getArea().size() == 0) {
                    City_AreaList.add("");
                } else {
                    City_AreaList.addAll(jsonBean.get(i).getCityList().get(c).getArea());
                }
                Province_AreaList.add(City_AreaList);//添加该省所有地区数据
            }

            /**
             * 添加城市数据
             */
            options2Items.add(CityList);

            /**
             * 添加地区数据
             */
            options3Items.add(Province_AreaList);
        }
    }

    public ArrayList<ProvinceBean> parseData(String result) {//Gson 解析
        ArrayList<ProvinceBean> detail = new ArrayList<>();
        try {
            JSONArray data = new JSONArray(result);
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                ProvinceBean entity = gson.fromJson(data.optJSONObject(i).toString(), ProvinceBean.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }

    @OnClick(R.id.tv_adress)
    public void showCityDialog() {
        if (ClickUtil.onClick()) {
            mProvincePv = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int options1, int options2, int options3, View v) {
                    //返回的分别是三个级别的选中位置
                    String tx;
                    if (options1Items.get(options1).getPickerViewText().equals(options2Items.get(options1).get(options2))) {
                        tx = options1Items.get(options1).getPickerViewText() + "," + options3Items.get(options1).get(options2).get(options3);
                    } else {
                        tx = options1Items.get(options1).getPickerViewText() + "," +
                                options2Items.get(options1).get(options2) + "," +
                                options3Items.get(options1).get(options2).get(options3);
                    }
                    address = tx;
                    tvAdress.setText(tx);
                }
            })
                    .setTitleText("城市选择")
                    .setDividerColor(Color.BLACK)
                    .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                    .setContentTextSize(15)
                    .build();
            mProvincePv.setPicker(options1Items, options2Items, options3Items);//三级选择器
            mProvincePv.show();
        }
    }

    @OnClick(R.id.tv_auth)
    public void onAuth() {
        if (TextUtils.isEmpty(realname)) {
            ToastUtil.showToast("请填写真实姓名");
            return;
        }
        if (TextUtils.isEmpty(cardid)) {
            ToastUtil.showToast("请填写身份证号");
            return;
        }
        if (TextUtils.isEmpty(sex)) {
            ToastUtil.showToast("请选择性别");
            return;
        }
        if (TextUtils.isEmpty(address)) {
            ToastUtil.showToast("请选择常住地址");
            return;
        }
        if (TextUtils.isEmpty(addressDetails)) {
            ToastUtil.showToast("请填写详细地址");
            return;
        }
        if (TextUtils.isEmpty(cardIdFront)) {
            ToastUtil.showToast("请上传身份证正面");
            return;
        }
        if (TextUtils.isEmpty(cardIdBack)) {
            ToastUtil.showToast("请上传身份证国徽面");
            return;
        }
        if (TextUtils.isEmpty(cardIdHand)) {
            ToastUtil.showToast("请上传手持身份证正面照");
            return;
        }

        HashMap<String, String> map = new HashMap<>();
        map.put("zone", address);
        map.put("address", addressDetails);
        map.put("sfz_name", realname);
        map.put("sex", sex);
        map.put("email", email);
        map.put("idcard", cardid);
        map.put("id_front", cardIdFront);
        map.put("id_back", cardIdBack);
        map.put("face_front", cardIdHand);
        OkHttp.post(this, HttpServicePath.URL_REALNAME_AUTH, map, new RequestCallBack() {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_UPLOADIMG && resultCode == RESULT_OK) {
            String path = FileUtil.checkLsength(FileUtil
                    .getTakePhotoPath("songqiu.allthings"));
            uploadPic(path, imageLoadType);
        } else {

            BoxingDefaultConfig.getCompressedBitmap(this, requestCode, data, new BoxingDefaultConfig.OnLuBanCompressed() {
                @Override
                public void onCompressed(List<File> files) {
                    if (null != files && 0 != files.size()) {
                        for (int i = 0; i < files.size(); i++) {
                            String path = files.get(0).getPath();
                            if (StringUtil.isEmpty(path)) return;
                            uploadPic(path, imageLoadType);
                        }
                    }
                }
            });
        }
    }


    // 拍照
    public void takePhotos() {
        Uri contentUri;
        File file = new File(FileUtil.getTakePhotoPath("songqiu.allthings"));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            contentUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileProvider", file);
        } else {
            contentUri = Uri.parse("file://" + file.getAbsolutePath());
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
        //设置宽高比
        intent.putExtra("aspectX", 16);
        intent.putExtra("aspectY", 9);

        startActivityForResult(intent, REQ_UPLOADIMG);
    }


    private void showDialog() {
        NoTitleDialog dialog = new NoTitleDialog(this);
        dialog.showDialog();
        dialog.setOnItemClickListener(new NoTitleDialog.OnItemClick() {
            @Override
            public void onWhichItemClick(int pos) {
                switch (pos) {
                    case 0:
                        applyPermission();
                        break;
                    case 1:
                        Boxing.of(BoxingDefaultConfig.getInstance().getMultiConfig(1))

                                .withIntent(RealNameAuthActivity.this, BuddingBoxingActivity.class)
                                .start(RealNameAuthActivity.this, BoxingDefaultConfig.IMAGE_REQUEST_CODE);
                        break;
                    case 2:
                        break;
                }
                if (dialog != null && dialog.isShowing())
                    dialog.dismiss();
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
                                takePhotos();
                            } else if (permission.shouldShowRequestPermissionRationale) {
                                // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
//                                Log.d(TAG, permission.name + " is denied. More info should be provided.");
//                                alertPermissionDialog("请在设置-应用-利卡-权限中开启定位权限");
                                ToastUtil.showToast("请在权限设置中允许应用访问相机");
                            } else {
                                // 用户拒绝了该权限，并且选中『不再询问』
//                                Log.d(TAG, permission.name + " is denied.");
                                ToastUtil.showToast("请在权限设置中允许应用访问相机");
                            }
                        }
                    }
                });
    }

    }

