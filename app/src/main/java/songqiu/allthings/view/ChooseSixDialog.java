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

public class ChooseSixDialog extends AlertDialog {

    TextView womanTv;
    TextView manTv;
    TextView cancelTv;
    OnItemClick onItemClick;
//    boolean showPhotograph;
    public interface  OnItemClick{
        void onWhichItemClick(int pos);
    }
    public void setOnItemClickListener(OnItemClick onItemClick){
        this.onItemClick = onItemClick;
    }

    public ChooseSixDialog(Context context) {
        super(context, R.style.noTitleStyle);
    }
    public void setView(View view){
        setContentView(view);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_six_toast);
        womanTv = findViewById(R.id.womanTv);
        manTv = findViewById(R.id.manTv);
        cancelTv = findViewById(R.id.cancelTv);
//        if(showPhotograph) {
//            tv_take_photo.setVisibility(View.VISIBLE);
//        }else {
//            tv_take_photo.setVisibility(View.GONE);
//        }
        manTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClick!=null){
                    onItemClick.onWhichItemClick(0);
                }
            }
        });
        womanTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClick!=null){
                    onItemClick.onWhichItemClick(1);
                }
            }
        });

        cancelTv.setOnClickListener(new View.OnClickListener() {
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
