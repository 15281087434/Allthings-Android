package songqiu.allthings.view;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import songqiu.allthings.R;

/**
 * Created by Administrator on 2017/3/10.
 */

public class LongClickDialog extends AlertDialog {

    TextView fuzhiTv;
    TextView reportTv;
    TextView cancelTv;
    RelativeLayout layout;

    boolean isMyself;
    OnItemClick onItemClick;

    public interface  OnItemClick{
        void onWhichItemClick(int pos);
    }
    public void setOnItemClickListener(OnItemClick onItemClick){
        this.onItemClick = onItemClick;
    }

    public LongClickDialog(Context context,boolean isMyself) {
        super(context, R.style.noTitleStyle);
        this.isMyself = isMyself;
    }
    public void setView(View view){
        setContentView(view);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_long_click);
        fuzhiTv = findViewById(R.id.fuzhiTv);
        reportTv = findViewById(R.id.reportTv);
        cancelTv = findViewById(R.id.cancelTv);
        layout = findViewById(R.id.layout);
        if(isMyself) {
            reportTv.setText("删除");
        }else {
            reportTv.setText("举报");
        }
        fuzhiTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClick!=null){
                    onItemClick.onWhichItemClick(0);
                    dismiss();
                }
            }
        });
        reportTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClick!=null){
                    onItemClick.onWhichItemClick(1);
                    dismiss();
                }
            }
        });

        cancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
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
