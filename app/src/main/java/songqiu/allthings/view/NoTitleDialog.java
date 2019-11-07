package songqiu.allthings.view;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import songqiu.allthings.R;

/**
 * Created by Administrator on 2017/3/10.
 */

public class NoTitleDialog extends AlertDialog {

    TextView tv_choose_photo;
    TextView tv_take_photo;
    TextView photo_cancel;
    OnItemClick onItemClick;
//    boolean showPhotograph;
    public interface  OnItemClick{
        void onWhichItemClick(int pos);
    }
    public void setOnItemClickListener(OnItemClick onItemClick){
        this.onItemClick = onItemClick;
    }

    public NoTitleDialog(Context context) {
        super(context, R.style.noTitleStyle);
    }
    public void setView(View view){
        setContentView(view);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.changeheadpictoast);
        tv_choose_photo = findViewById(R.id.photo);
        tv_take_photo = findViewById(R.id.takepicture);
        photo_cancel = findViewById(R.id.photo_cancel);
        tv_choose_photo.setBackgroundColor(Color.WHITE);
        tv_take_photo.setBackgroundColor(Color.WHITE);
//        if(showPhotograph) {
//            tv_take_photo.setVisibility(View.VISIBLE);
//        }else {
//            tv_take_photo.setVisibility(View.GONE);
//        }
        photo_cancel.setBackgroundColor(Color.WHITE);
        tv_take_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClick!=null){
                    onItemClick.onWhichItemClick(0);
                }
            }
        });
        tv_choose_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClick!=null){
                    onItemClick.onWhichItemClick(1);
                }
            }
        });

        photo_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClick!=null)
                    onItemClick.onWhichItemClick(2);
            }
        });

    }

    public void showDialog(){
        Window window = this.getWindow();
        window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
        window.setWindowAnimations(R.style.mypopwindow_anim_style); // 添加动画
        // 两句的顺序不能调换
        this.show();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(lp);
        window.getDecorView().setPadding(0, 0, 0, 0);
    }
}
