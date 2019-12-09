package songqiu.allthings.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import songqiu.allthings.R;
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
public class DialogFileUploading extends Dialog{

    private Activity context;
    ImageView imgView;
    //旋转动画
    Animation rotate;

    public DialogFileUploading(Activity context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @SuppressLint("ResourceType")
    public void init() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_file_uploading, null);
        setContentView(view);

        imgView = view.findViewById(R.id.imgView);
        rotate = AnimationUtils.loadAnimation(context, R.drawable.loading_anim);
        imgView.setAnimation(rotate);
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        lp.width = (int) (d.widthPixels * 0.7); // 宽度设置为屏幕的0.7
        dialogWindow.setAttributes(lp);
    }

    public void showDialog() {
        setCanceledOnTouchOutside(false);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        if(null != imgView) {
            imgView.startAnimation(rotate);
        }
        show();
    }

    public void disMiss() {
        if(null != imgView) {
            imgView.clearAnimation();
        }
        dismiss();
    }

}
