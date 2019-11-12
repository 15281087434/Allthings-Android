package songqiu.allthings.mine.qrcode;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import songqiu.allthings.R;
import songqiu.allthings.base.BaseActivity;
import songqiu.allthings.constant.SnsConstants;
import songqiu.allthings.util.SharedPreferencedUtils;
import songqiu.allthings.util.ZXingUtils;
import songqiu.allthings.util.statusbar.StatusBarUtils;
import songqiu.allthings.util.theme.ThemeManager;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/10/15
 *
 *类描述：面对面扫码领取红包
 *
 ********/
public class EwmRedEnvelopeActivity extends BaseActivity {

    @BindView(R.id.backImg)
    ImageView backImg;
    @BindView(R.id.titleTv)
    TextView titleTv;
    @BindView(R.id.rightTv)
    TextView rightTv;
    @BindView(R.id.line)
    LinearLayout line;
    @BindView(R.id.qrcodeImg)
    ImageView qrcodeImg;
    @BindView(R.id.shadowLayout)
    LinearLayout shadowLayout;


    @Override
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_ewm_redenvelope);
    }

    @Override
    public void init() {
        boolean dayModel = SharedPreferencedUtils.getBoolean(this, SharedPreferencedUtils.dayModel, true);
        modeUi(dayModel);
        titleTv.setText("面对面扫码领红包");
        Bitmap bitmap = ZXingUtils.createQRImage(SnsConstants.URL_DOWNLOAD,332 ,  332,true);
        qrcodeImg.setImageBitmap(bitmap);
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

    @OnClick(R.id.backImg)
    public void onViewClick() {
        finish();
    }
}
