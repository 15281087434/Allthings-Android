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
import songqiu.allthings.iterface.TaskDialogSignListener;
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
public class DialogSign extends Dialog{

    private Context context;
    private String content;
    TaskDialogSignListener taskDialogSignListener;

    public DialogSign(Context context,String content) {
        super(context);
        this.context = context;
        this.content = content;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    public void init() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_sign, null);
        setContentView(view);
        ImageView closeImg = view.findViewById(R.id.closeImg);
        ImageView headImg = view.findViewById(R.id.headImg);
        LinearLayout linearLayout1 = view.findViewById(R.id.linearLayout1);
        LinearLayout linearLayout2 = view.findViewById(R.id.linearLayout2);
        ImageView img = view.findViewById(R.id.img);

        boolean dayModel = SharedPreferencedUtils.getBoolean(context,SharedPreferencedUtils.dayModel,true);
        if(dayModel) {
            closeImg.setImageResource(R.mipmap.icon_dialog_close);
            headImg.setImageResource(R.mipmap.icon_dialog_head);
            linearLayout1.setBackgroundColor(context.getResources().getColor(R.color.main_bg_white));
            linearLayout2.setBackgroundResource(R.drawable.white_below_radius);
            img.setImageResource(R.mipmap.icon_dialog_sign);
        }else {
            closeImg.setImageResource(R.mipmap.icon_dialog_close_night);
            headImg.setImageResource(R.mipmap.icon_dialog_head_night);
            linearLayout1.setBackgroundColor(context.getResources().getColor(R.color.FF3B3B3B));
            linearLayout2.setBackgroundResource(R.drawable.white_below_radius_night);
            img.setImageResource(R.mipmap.icon_dialog_sign_night);
        }


        closeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        TextView contentTv = view.findViewById(R.id.contentTv);
        contentTv.setText(content);

        TextView sureTv = view.findViewById(R.id.sureTv);
        sureTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(null != taskDialogSignListener) {
                    taskDialogSignListener.toInvite();
                }
            }
        });

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        lp.width = (int) (d.widthPixels * 0.7); // 宽度设置为屏幕的0.7
        dialogWindow.setAttributes(lp);
    }

    public void setTaskDialogSignListener(TaskDialogSignListener taskDialogSignListener) {
        this.taskDialogSignListener = taskDialogSignListener;
    }


}
