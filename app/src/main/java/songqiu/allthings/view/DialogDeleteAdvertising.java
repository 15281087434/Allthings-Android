package songqiu.allthings.view;

import android.app.Dialog;
import android.content.Context;
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
import songqiu.allthings.iterface.DialogDeleteListener;
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
public class DialogDeleteAdvertising extends Dialog{

    private Context context;
    DialogDeleteListener dialogDeleteListener;

    public DialogDeleteAdvertising(Context context) {
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
        View view = inflater.inflate(R.layout.dialog_delete_advertising, null);
        setContentView(view);

        ImageView closeImg = view.findViewById(R.id.closeImg);
        LinearLayout parentLayout = view.findViewById(R.id.parentLayout);
        ImageView headImg = view.findViewById(R.id.headImg);
        TextView tv1 = view.findViewById(R.id.tv1);
        TextView tv2 = view.findViewById(R.id.tv2);
        TextView tv3 = view.findViewById(R.id.tv3);
        TextView tv4 = view.findViewById(R.id.tv4);
        View line1 = view.findViewById(R.id.line1);

        boolean dayModel = SharedPreferencedUtils.getBoolean(context,SharedPreferencedUtils.dayModel,true);
        if(dayModel) {
            parentLayout.setBackgroundResource(R.drawable.white_around_radius);
            headImg.setImageResource(R.mipmap.icon_dialog_head);
            closeImg.setImageResource(R.mipmap.icon_dialog_close);
            tv1.setTextColor(context.getResources().getColor(R.color.bottom_tab_tv));
            tv2.setTextColor(context.getResources().getColor(R.color.FF999999));
            tv3.setTextColor(context.getResources().getColor(R.color.bottom_tab_tv));
            tv4.setTextColor(context.getResources().getColor(R.color.FF999999));
            line1.setBackgroundColor(context.getResources().getColor(R.color.line_color));
        }else {
            parentLayout.setBackgroundResource(R.drawable.white_around_radius_night);
            headImg.setImageResource(R.mipmap.icon_dialog_head_night);
            closeImg.setImageResource(R.mipmap.icon_dialog_close_night);
            tv1.setTextColor(context.getResources().getColor(R.color.bottom_tab_tv_night));
            tv2.setTextColor(context.getResources().getColor(R.color.FF999999_night));
            tv3.setTextColor(context.getResources().getColor(R.color.bottom_tab_tv_night));
            tv4.setTextColor(context.getResources().getColor(R.color.FF999999_night));
            line1.setBackgroundColor(context.getResources().getColor(R.color.line_color_night));
        }

        closeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        LinearLayout layout1 = view.findViewById(R.id.layout1);
        layout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                dialogDeleteListener.delete1();
                showSuccese("操作成功");
            }
        });
        LinearLayout layout2 = view.findViewById(R.id.layout2);
        layout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                dialogDeleteListener.delete2();
                showSuccese("操作成功");
            }
        });
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        lp.width = (int) (d.widthPixels * 0.8); // 宽度设置为屏幕的0.7
        dialogWindow.setAttributes(lp);
    }

    public void setDialogDeleteListener(DialogDeleteListener dialogDeleteListener) {
        this.dialogDeleteListener = dialogDeleteListener;
    }
    public void showSuccese(String text) {
        new Handler().postDelayed(new Runnable(){
            public void run() {
                //execute the task
                ToastUtil.showToast(text);
            }
        }, 500);
    }

}
