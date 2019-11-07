package songqiu.allthings.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import songqiu.allthings.R;
import songqiu.allthings.iterface.DialogNewRedListener;
import songqiu.allthings.iterface.TaskDialogSignListener;
import songqiu.allthings.util.ClickUtil;
import songqiu.allthings.util.SharedPreferencedUtils;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/8/16
 *
 *类描述：新人红包弹窗
 *
 ********/
public class DialogNewRedEnvelope extends Dialog{

    private Context context;
    DialogNewRedListener dialogNewRedListener;

    public DialogNewRedEnvelope(Context context) {
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
        View view = inflater.inflate(R.layout.dialog_new_redenvelope, null);
        setContentView(view);
        ImageView closeImg = view.findViewById(R.id.closeImg);

//        boolean dayModel = SharedPreferencedUtils.getBoolean(context,SharedPreferencedUtils.dayModel,true);
//        if(dayModel) {
//            closeImg.setImageResource(R.mipmap.icon_dialog_close);
//            headImg.setImageResource(R.mipmap.icon_dialog_head);
//            linearLayout1.setBackgroundColor(context.getResources().getColor(R.color.main_bg_white));
//            linearLayout2.setBackgroundResource(R.drawable.white_below_radius);
//            img.setImageResource(R.mipmap.icon_dialog_sign);
//        }else {
//            closeImg.setImageResource(R.mipmap.icon_dialog_close_night);
//            headImg.setImageResource(R.mipmap.icon_dialog_head_night);
//            linearLayout1.setBackgroundColor(context.getResources().getColor(R.color.FF3B3B3B));
//            linearLayout2.setBackgroundResource(R.drawable.white_below_radius_night);
//            img.setImageResource(R.mipmap.icon_dialog_sign_night);
//        }


        closeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });


        ImageView img = view.findViewById(R.id.img);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(null != dialogNewRedListener) {
                   if(ClickUtil.onClick()) {
                       dialogNewRedListener.toReceive();
                   }
                }
            }
        });

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        lp.width = (int) (d.widthPixels * 0.9); // 宽度设置为屏幕的0.7
        dialogWindow.setAttributes(lp);
    }

    public void setDialogNewRedListener(DialogNewRedListener dialogNewRedListener) {
        this.dialogNewRedListener = dialogNewRedListener;
    }


}
