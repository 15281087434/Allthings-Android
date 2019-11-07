package songqiu.allthings.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import songqiu.allthings.R;
import songqiu.allthings.iterface.DialogProgressListener;
import songqiu.allthings.iterface.DialogUploadVersionListener;
import songqiu.allthings.util.ClickUtil;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/8/16
 *
 *类描述：
 *
 ********/
public class DialogProgress extends Dialog{

    private Context context;
    DialogProgressListener dialogProgressListener;

    public DialogProgress(Context context) {
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
        View view = inflater.inflate(R.layout.progress, null);
        setContentView(view);
        Button btn_cancel = view.findViewById(R.id.btn_cancel);
        Button btn_switch = view.findViewById(R.id.btn_switch);
//        ImageView headImg = view.findViewById(R.id.headImg);
//        LinearLayout linearLayout1 = view.findViewById(R.id.linearLayout1);
//        LinearLayout linearLayout2 = view.findViewById(R.id.linearLayout2);
//        ImageView img = view.findViewById(R.id.img);

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


        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ClickUtil.onClick()) {
                    if(null != dialogProgressListener) {
                        dialogProgressListener.cancel();
                    }
                    dismiss();
                }
            }
        });

        btn_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ClickUtil.onClick()) {
                    if(null != dialogProgressListener) {
                        dialogProgressListener.toUpload();
                    }
                    dismiss();
                }
            }
        });

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        lp.width = (int) (d.widthPixels * 0.7); // 宽度设置为屏幕的0.7
        dialogWindow.setAttributes(lp);
    }

    public void setDialogProgressListener(DialogProgressListener dialogProgressListener) {
        this.dialogProgressListener = dialogProgressListener;
    }


}
