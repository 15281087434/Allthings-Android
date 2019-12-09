package songqiu.allthings.mine.userpage;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bilibili.boxing.Boxing;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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
import butterknife.OnClick;
import io.reactivex.functions.Consumer;
import songqiu.allthings.BuildConfig;
import songqiu.allthings.R;
import songqiu.allthings.activity.BuddingBoxingActivity;
import songqiu.allthings.base.BaseActivity;
import songqiu.allthings.bean.ProvinceBean;
import songqiu.allthings.bean.UploadPicBean;
import songqiu.allthings.bean.UserInfoBean;
import songqiu.allthings.http.BaseBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.http.OkHttp;
import songqiu.allthings.http.RequestCallBack;
import songqiu.allthings.util.BoxingDefaultConfig;
import songqiu.allthings.util.ClickUtil;
import songqiu.allthings.util.FileUtil;
import songqiu.allthings.util.GlideCircleTransform;
import songqiu.allthings.util.GlideLoadUtils;
import songqiu.allthings.util.JsUtils;
import songqiu.allthings.util.LogUtil;
import songqiu.allthings.util.SharedPreferencedUtils;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.util.ToastUtil;
import songqiu.allthings.util.WindowUtil;
import songqiu.allthings.util.statusbar.StatusBarUtils;
import songqiu.allthings.util.theme.ThemeManager;
import songqiu.allthings.view.ChooseSixDialog;
import songqiu.allthings.view.NoTitleDialog;
import songqiu.allthings.view.ReportPopupWindows;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/8/20
 *
 *类描述：编辑资料页面
 *
 ********/
public class ModificationInfoActivity extends BaseActivity {

    @BindView(R.id.backImg)
    ImageView backImg;
    @BindView(R.id.titleTv)
    TextView titleTv;
    @BindView(R.id.userIcon)
    ImageView userIcon;

    @BindView(R.id.sixTv)
    TextView sixTv;
    @BindView(R.id.addressTv)
    TextView addressTv;
    @BindView(R.id.nichengEt)
    EditText nichengEt;
    @BindView(R.id.introduceeEt)
    EditText introduceeEt;
    @BindView(R.id.numTv)
    TextView numTv;
    @BindView(R.id.sureTv)
    TextView sureTv;
    @BindView(R.id.shadowLayout)
    LinearLayout shadowLayout;

    boolean canCamera;
    final int TAKE_PHOTOS_RESULT = 52;

    ArrayList<ProvinceBean> options1Items = new ArrayList<>();
    ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();
    OptionsPickerView mProvincePv;

    UserInfoBean userInfoBean;
    //编辑
    String mHeadUrl;
    String mSix;


    @Override
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_modification_info);
    }

    @Override
    public void init() {
        boolean dayModel = SharedPreferencedUtils.getBoolean(this,SharedPreferencedUtils.dayModel,true);
        modeUi(dayModel);
        titleTv.setText("编辑资料");
        setsureTvUi(false);
        initJsonData();
        getUserInfo();
        initNichengEt();
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

    public void initNichengEt() {
        nichengEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(null != userInfoBean) {
                    if(!s.toString().equals(userInfoBean.user_nickname)) {
                        setsureTvUi(true);
                    }
                }

            }
        });

        introduceeEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String content = s.toString();
                if(null != userInfoBean) {
                    if(!content.equals(userInfoBean.signature)) {
                        setsureTvUi(true);
                    }
                }

                if (!TextUtils.isEmpty(content)) {
                    numTv.setText(content.length() + "/140");
                } else {
                    numTv.setText("0/140");
                }
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        applyPermission();
    }

    public void setsureTvUi(boolean isModification) {
        if(isModification) {
            sureTv.setClickable(true);
            sureTv.setBackgroundDrawable(getResources().getDrawable(R.mipmap.common_botton));
        }else {
            sureTv.setClickable(false);
            sureTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.rectangle_common_999999));
        }

    }

    public void initUi(UserInfoBean userInfoBean) {
        if(null == userInfoBean) return;
        RequestOptions options = new RequestOptions()
                .circleCrop().transforms(new GlideCircleTransform(this))
                .error(R.mipmap.head_default)
                .placeholder(R.mipmap.head_default);
        if(!StringUtil.isEmpty(userInfoBean.avatar)) {
            if(!userInfoBean.avatar.contains("http")) {
                userInfoBean.avatar = HttpServicePath.BasePicUrl+userInfoBean.avatar;
            }
        }
        GlideLoadUtils.getInstance().glideLoadHead(this,userInfoBean.avatar,userIcon);
        mHeadUrl = userInfoBean.avatar;
        mSix = String.valueOf(userInfoBean.sex);
        nichengEt.setText(userInfoBean.user_nickname);
        nichengEt.setSelection(nichengEt.getText().length());//将光标移至文字末尾
        addressTv.setText(userInfoBean.location);
        introduceeEt.setText(userInfoBean.signature);
        if("1".equals(mSix)) {
            sixTv.setText("男");
        }else {
            sixTv.setText("女");
        }
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

    private void showPickerView() {// 弹出选择器
        mProvincePv = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String tx;
                if(options1Items.get(options1).getPickerViewText().equals(options2Items.get(options1).get(options2))) {
                    tx =  options1Items.get(options1).getPickerViewText()+options3Items.get(options1).get(options2).get(options3);
                }else {
                    tx = options1Items.get(options1).getPickerViewText() +
                            options2Items.get(options1).get(options2) +
                            options3Items.get(options1).get(options2).get(options3);
                }
                setsureTvUi(true);
                addressTv.setText(tx);
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
                        Boxing.of(BoxingDefaultConfig.getInstance().getMultiConfig(1))
                                .withIntent(ModificationInfoActivity.this, BuddingBoxingActivity.class)
                                .start(ModificationInfoActivity.this, BoxingDefaultConfig.IMAGE_REQUEST_CODE);
                        break;
                    case 2:
                        break;
                }
                if (dialog != null && dialog.isShowing())
                    dialog.dismiss();
            }
        });
    }

    public void showSixDialog() {
        ChooseSixDialog dialog = new ChooseSixDialog(this);
        dialog.showDialog();
        dialog.setOnItemClickListener(new ChooseSixDialog.OnItemClick() {
            @Override
            public void onWhichItemClick(int pos) {
                switch (pos) {
                    case 0:
                        mSix = "1";
                        sixTv.setText("男");
                        setsureTvUi(true);
                        break;
                    case 1:
                        mSix = "2";
                        sixTv.setText("女");
                        setsureTvUi(true);
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
                            String path = files.get(0).getPath();
                            if(StringUtil.isEmpty(path)) return;
                            GlideLoadUtils.getInstance().glideLoadHead(ModificationInfoActivity.this,path,userIcon);
                            uploadPic(path);
                        }
                    }
                }
            });
        }
    }

    public void uploadPic(String path) {
        showLoading();
        setsureTvUi(true);
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
                        if(!StringUtil.isEmpty(uploadPicBean.imgurl)) {
                            if(!uploadPicBean.imgurl.contains("http")) {
                                if(uploadPicBean.imgurl.contains("\"")) {
                                    uploadPicBean.imgurl.replace("\"","");
                                }
                                String headUrl= HttpServicePath.BasePicUrl+uploadPicBean.imgurl;
                                GlideLoadUtils.getInstance().glideLoadHead(ModificationInfoActivity.this,headUrl,userIcon);
                            }else {
                                if(uploadPicBean.imgurl.contains("\"")) {
                                    uploadPicBean.imgurl.replace("\"","");
                                }
                                GlideLoadUtils.getInstance().glideLoadHead(ModificationInfoActivity.this,uploadPicBean.imgurl,userIcon);
                            }
                        }
                        mHeadUrl = uploadPicBean.imgurl;
                    }
                });
            }
        });
    }

    public void getUserInfo() {
        Map<String, String> map = new HashMap<>();
        OkHttp.post(this, HttpServicePath.URL_USER_DETAIL, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        String data = gson.toJson(baseBean.data);
                        if (StringUtil.isEmpty(data)) return;
                        userInfoBean = gson.fromJson(data, UserInfoBean.class);
                        initUi(userInfoBean);
                    }
                });
            }
        });
    }

    public void submitInfo() {
        String nicheng = nichengEt.getText().toString().trim();
        if(StringUtil.isEmpty(nicheng)) {
            ToastUtil.showToast(this,"请输入昵称");
            return;
        }
        String location = addressTv.getText().toString();
        String introducee = introduceeEt.getText().toString().trim();

        Map<String, String> map = new HashMap<>();
        map.put("avatar",mHeadUrl);
        map.put("user_nickname",nicheng);
        map.put("sex",mSix);
        map.put("location",location);
        map.put("signature",introducee);
        OkHttp.post(this, HttpServicePath.URL_EDIT_USER, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(ModificationInfoActivity.this,"资料修改成功");
                        finish();
                    }
                });
            }
        });
    }



    @OnClick({R.id.backImg, R.id.chooseHeadLayout,R.id.sixLayout,R.id.addressLayout,R.id.sureTv})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.backImg:
                finish();
                break;
            case R.id.chooseHeadLayout:
                if (ClickUtil.onClick()) {
                    if(canCamera) {
                        showDialog();
                    }else {
                        ToastUtil.showToast(ModificationInfoActivity.this,"请在设置-应用-见怪-权限中开启相机权限");
                    }
                }
                break;
            case R.id.sixLayout:
                showSixDialog();
                break;
            case R.id.addressLayout:
                if (ClickUtil.onClick()) {
                    showPickerView();
                }
                break;
            case R.id.sureTv:
                if(ClickUtil.onClick()) {
                    submitInfo();
                }
                break;
        }
    }

}
