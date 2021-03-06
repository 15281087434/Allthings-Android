package songqiu.allthings.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import songqiu.allthings.R;
import songqiu.allthings.adapter.LabelAdapter;
import songqiu.allthings.bean.UnLikeBean;
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

    Context context;
    OnItemClick onItemClick;
    TextView cancelTv;
    RelativeLayout layout;
    LinearLayout parentLayout;
    RelativeLayout layout1;
    RelativeLayout layout2;
    ImageView img1;
    ImageView img2;
    TextView titleTv1;
    TextView titleTv2;
    TextView contentTv1;
    TextView contentTv2;
    View line1;
    View line2;


    public DialogDeleteAdvertising(Context context) {
        super(context, R.style.noTitleStyle);
        this.context = context;

    }

    public interface  OnItemClick{
        void onWhichItemClick(int pos);
    }

    public void setOnItemClickListener(OnItemClick onItemClick){
        this.onItemClick = onItemClick;
    }

//    public void setView(View view){
//        setContentView(view);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_delete_advertising);
        layout = findViewById(R.id.layout);
        parentLayout = findViewById(R.id.parentLayout);
        cancelTv = findViewById(R.id.cancelTv);
        layout1 = findViewById(R.id.layout1);
        layout2 = findViewById(R.id.layout2);
        img1 = findViewById(R.id.img1);
        img2 = findViewById(R.id.img2);
        titleTv1 = findViewById(R.id.titleTv1);
        titleTv2 = findViewById(R.id.titleTv2);
        contentTv1 = findViewById(R.id.contentTv1);
        contentTv2 = findViewById(R.id.contentTv2);
        line1 = findViewById(R.id.line1);
        line2 = findViewById(R.id.line2);


        boolean dayModel = SharedPreferencedUtils.getBoolean(context,SharedPreferencedUtils.dayModel,true);
        if(dayModel) {
            cancelTv.setBackgroundResource(R.drawable.white_around_radius_24);
            parentLayout.setBackgroundResource(R.drawable.white_around_radius_24);
            img1.setImageResource(R.mipmap.dialog_no_interest);
            img2.setImageResource(R.mipmap.dialog_blacklist);

            titleTv1.setTextColor(context.getResources().getColor(R.color.bottom_tab_tv));
            titleTv2.setTextColor(context.getResources().getColor(R.color.bottom_tab_tv));

            contentTv1.setTextColor(context.getResources().getColor(R.color.FF999999));
            contentTv2.setTextColor(context.getResources().getColor(R.color.FF999999));

            line1.setBackgroundColor(context.getResources().getColor(R.color.line_color));
            line2.setBackgroundColor(context.getResources().getColor(R.color.line_color));

        }else {
            cancelTv.setBackgroundResource(R.drawable.white_around_radius_24_night);
            parentLayout.setBackgroundResource(R.drawable.white_around_radius_24_night);
            img1.setImageResource(R.mipmap.dialog_no_interest_night);
            img2.setImageResource(R.mipmap.dialog_blacklist_night);

            titleTv1.setTextColor(context.getResources().getColor(R.color.bottom_tab_tv_night));
            titleTv2.setTextColor(context.getResources().getColor(R.color.bottom_tab_tv_night));
            contentTv1.setTextColor(context.getResources().getColor(R.color.FF999999_night));
            contentTv2.setTextColor(context.getResources().getColor(R.color.FF999999_night));
            line1.setBackgroundColor(context.getResources().getColor(R.color.line_color_night));
            line2.setBackgroundColor(context.getResources().getColor(R.color.line_color_night));
        }

        layout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClick!=null){
                    onItemClick.onWhichItemClick(1);
                    showSuccese("操作成功");
                    dismiss();
                }
            }
        });

        layout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClick!=null){
                    onItemClick.onWhichItemClick(2);
                    showSuccese("操作成功");
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
        this.setCanceledOnTouchOutside(true);
        this.show();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(lp);
        window.getDecorView().setPadding(0, 0, 0, 0);
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
