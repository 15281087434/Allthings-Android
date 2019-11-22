package songqiu.allthings.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import songqiu.allthings.iterface.DialogDeleteListener;
import songqiu.allthings.iterface.DialogPrivacyListener;
import songqiu.allthings.util.SharedPreferencedUtils;
import songqiu.allthings.util.ToastUtil;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/8/16
 *
 *类描述：
 *
 ********/
public class DialogPrivacyExplain extends Dialog{

    private Context context;
    DialogPrivacyListener dialogPrivacyListener;

    public DialogPrivacyExplain(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    public void init() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_privacy_explain, null);
        setContentView(view);

        TextView knowTv = view.findViewById(R.id.knowTv);
        knowTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogPrivacyListener.sure();
                dismiss();
            }
        });

        TextView cancelTv = view.findViewById(R.id.cancelTv);
        cancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogPrivacyListener.cancel();
                dismiss();
            }
        });

        TextView tv = view.findViewById(R.id.tv);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,CommentWebViewActivity.class);
                intent.putExtra("url", SnsConstants.URL_PRIVACY);
                context.startActivity(intent);
//                dismiss();
            }
        });
//        LinearLayout parentLayout = view.findViewById(R.id.parentLayout);
//        ImageView headImg = view.findViewById(R.id.headImg);
//        TextView tv1 = view.findViewById(R.id.tv1);
//        TextView tv2 = view.findViewById(R.id.tv2);
//        TextView tv3 = view.findViewById(R.id.tv3);
//        TextView tv4 = view.findViewById(R.id.tv4);
//        TextView tv5 = view.findViewById(R.id.tv5);
//        TextView tv6 = view.findViewById(R.id.tv6);
//        TextView tv7 = view.findViewById(R.id.tv7);
//
//        View line1 = view.findViewById(R.id.line1);
//        View line2 = view.findViewById(R.id.line2);
//        View line3 = view.findViewById(R.id.line3);
//
//        boolean dayModel = SharedPreferencedUtils.getBoolean(context,SharedPreferencedUtils.dayModel,true);
//        if(dayModel) {
//            parentLayout.setBackgroundResource(R.drawable.white_around_radius);
//            headImg.setImageResource(R.mipmap.icon_dialog_head);
//            closeImg.setImageResource(R.mipmap.icon_dialog_close);
//            tv1.setTextColor(context.getResources().getColor(R.color.bottom_tab_tv));
//            tv2.setTextColor(context.getResources().getColor(R.color.FF999999));
//            tv3.setTextColor(context.getResources().getColor(R.color.bottom_tab_tv));
//            tv4.setTextColor(context.getResources().getColor(R.color.FF999999));
//            tv5.setTextColor(context.getResources().getColor(R.color.bottom_tab_tv));
//            tv6.setTextColor(context.getResources().getColor(R.color.FF999999));
//            tv7.setTextColor(context.getResources().getColor(R.color.bottom_tab_tv));
//            line1.setBackgroundColor(context.getResources().getColor(R.color.line_color));
//            line2.setBackgroundColor(context.getResources().getColor(R.color.line_color));
//            line3.setBackgroundColor(context.getResources().getColor(R.color.line_color));
//        }else {
//            parentLayout.setBackgroundResource(R.drawable.white_around_radius_night);
//            headImg.setImageResource(R.mipmap.icon_dialog_head_night);
//            closeImg.setImageResource(R.mipmap.icon_dialog_close_night);
//            tv1.setTextColor(context.getResources().getColor(R.color.bottom_tab_tv_night));
//            tv2.setTextColor(context.getResources().getColor(R.color.FF999999_night));
//            tv3.setTextColor(context.getResources().getColor(R.color.bottom_tab_tv_night));
//            tv4.setTextColor(context.getResources().getColor(R.color.FF999999_night));
//            tv5.setTextColor(context.getResources().getColor(R.color.bottom_tab_tv_night));
//            tv6.setTextColor(context.getResources().getColor(R.color.FF999999_night));
//            tv7.setTextColor(context.getResources().getColor(R.color.bottom_tab_tv_night));
//            line1.setBackgroundColor(context.getResources().getColor(R.color.line_color_night));
//            line2.setBackgroundColor(context.getResources().getColor(R.color.line_color_night));
//            line3.setBackgroundColor(context.getResources().getColor(R.color.line_color_night));
//        }

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
