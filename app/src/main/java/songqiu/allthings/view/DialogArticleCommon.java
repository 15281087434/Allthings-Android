package songqiu.allthings.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import songqiu.allthings.R;
import songqiu.allthings.activity.CommentWebViewActivity;
import songqiu.allthings.constant.SnsConstants;
import songqiu.allthings.iterface.DialogPrivacyListener;
import songqiu.allthings.util.ClickUtil;
import songqiu.allthings.util.SharedPreferencedUtils;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/8/16
 *
 *类描述：
 *
 ********/
public class DialogArticleCommon extends Dialog{

    private Context context;
    DialogPrivacyListener dialogPrivacyListener;
    String title;
    String content;

    public DialogArticleCommon(Context context,String title,String content) {
        super(context);
        this.context = context;
        this.title = title;
        this.content = content;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    public void init() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_article_common, null);
        setContentView(view);

        LinearLayout parentLayout = view.findViewById(R.id.parentLayout);
        TextView knowTv = view.findViewById(R.id.sureTv);
        knowTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ClickUtil.onClick()) {
                    dialogPrivacyListener.sure();
                    dismiss();
                }
            }
        });

        TextView cancelTv = view.findViewById(R.id.cancelTv);
        cancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ClickUtil.onClick()) {
                    dialogPrivacyListener.cancel();
                    dismiss();
                }

            }
        });

        TextView titleTv = view.findViewById(R.id.titleTv);
        TextView contentTv = view.findViewById(R.id.contentTv);
        ImageView headImg = view.findViewById(R.id.headImg);
        titleTv.setText(title);
        contentTv.setText(content);

        boolean dayModel = SharedPreferencedUtils.getBoolean(context,SharedPreferencedUtils.dayModel,true);
        if(dayModel) {
            parentLayout.setBackgroundResource(R.drawable.white_around_radius);
            headImg.setImageResource(R.mipmap.icon_dialog_head);
            titleTv.setTextColor(context.getResources().getColor(R.color.bottom_tab_tv));
            contentTv.setTextColor(context.getResources().getColor(R.color.FF666666));
            cancelTv.setBackgroundResource(R.drawable.rectangle_common_999999);
        }else {
            titleTv.setTextColor(context.getResources().getColor(R.color.bottom_tab_tv_night));
            contentTv.setTextColor(context.getResources().getColor(R.color.FF666666));
            parentLayout.setBackgroundResource(R.drawable.white_around_radius_night);
            headImg.setImageResource(R.mipmap.icon_dialog_head_night);
            cancelTv.setBackgroundResource(R.drawable.rectangle_common_999999_night);
        }

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        lp.width = (int) (d.widthPixels * 0.8); // 宽度设置为屏幕的0.7
        dialogWindow.setAttributes(lp);
    }

    public void setDialogPrivacyListener(DialogPrivacyListener dialogPrivacyListener) {
        this.dialogPrivacyListener = dialogPrivacyListener;
    }

}
