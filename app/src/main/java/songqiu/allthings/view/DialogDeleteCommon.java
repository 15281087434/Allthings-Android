package songqiu.allthings.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
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
import songqiu.allthings.util.SharedPreferencedUtils;
import songqiu.allthings.util.StringUtil;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/11/29
 *
 *类描述：
 *
 ********/
public class DialogDeleteCommon extends Dialog {

    Context context;
    UnLikeBean unLikeBean;
    boolean hideReport;
    OnItemClick onItemClick;
    TextView cancelTv;
    RelativeLayout layout;
    LinearLayout parentLayout;
    RelativeLayout layout1;
    RelativeLayout layout2;
    RelativeLayout layout3;
    RelativeLayout layout4;
    LinearLayout layout5;
    ImageView img1;
    ImageView img2;
    ImageView img3;
    ImageView img4;
    ImageView img5;
    ImageView arrowImg;
    TextView titleTv1;
    TextView titleTv2;
    TextView titleTv3;
    TextView titleTv4;
    TextView titleTv5;
    TextView contentTv1;
    TextView contentTv2;
    TextView contentTv3;
    TextView contentTv4;
    GridView gridView;
    View line1;
    View line2;
    View line3;
    View line4;

    public DialogDeleteCommon(Context context,UnLikeBean unLikeBean,boolean hideReport) {
        super(context, R.style.noTitleStyle);
        this.context = context;
        this.unLikeBean = unLikeBean;
        this.hideReport = hideReport;
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
        setContentView(R.layout.common_delete_dialog);
        layout = findViewById(R.id.layout);
        parentLayout = findViewById(R.id.parentLayout);
        cancelTv = findViewById(R.id.cancelTv);
        layout1 = findViewById(R.id.layout1);
        layout2 = findViewById(R.id.layout2);
        layout3 = findViewById(R.id.layout3);
        layout4 = findViewById(R.id.layout4);
        layout5 = findViewById(R.id.layout5);
        img1 = findViewById(R.id.img1);
        img2 = findViewById(R.id.img2);
        img3 = findViewById(R.id.img3);
        img4 = findViewById(R.id.img4);
        img5 = findViewById(R.id.img5);
        arrowImg = findViewById(R.id.arrowImg);
        titleTv1 = findViewById(R.id.titleTv1);
        titleTv2 = findViewById(R.id.titleTv2);
        titleTv3 = findViewById(R.id.titleTv3);
        titleTv4 = findViewById(R.id.titleTv4);
        titleTv5 = findViewById(R.id.titleTv5);
        contentTv1 = findViewById(R.id.contentTv1);
        contentTv2 = findViewById(R.id.contentTv2);
        contentTv3 = findViewById(R.id.contentTv3);
        contentTv4 = findViewById(R.id.contentTv4);
        gridView = findViewById(R.id.gridView);
        line1 = findViewById(R.id.line1);
        line2 = findViewById(R.id.line2);
        line3 = findViewById(R.id.line3);
        line4 = findViewById(R.id.line4);

        boolean dayModel = SharedPreferencedUtils.getBoolean(context,SharedPreferencedUtils.dayModel,true);
        if(dayModel) {
            cancelTv.setBackgroundResource(R.drawable.white_around_radius_24);
            parentLayout.setBackgroundResource(R.drawable.white_around_radius_24);
            img1.setImageResource(R.mipmap.dialog_no_interest);
            img2.setImageResource(R.mipmap.dialog_blacklist);
            img3.setImageResource(R.mipmap.dialog_feedback);
            img4.setImageResource(R.mipmap.dialog_label);
            img5.setImageResource(R.mipmap.icon_window_inform);
            arrowImg.setImageResource(R.mipmap.arrow_next);
            titleTv1.setTextColor(context.getResources().getColor(R.color.bottom_tab_tv));
            titleTv2.setTextColor(context.getResources().getColor(R.color.bottom_tab_tv));
            titleTv3.setTextColor(context.getResources().getColor(R.color.bottom_tab_tv));
            titleTv4.setTextColor(context.getResources().getColor(R.color.bottom_tab_tv));
            titleTv5.setTextColor(context.getResources().getColor(R.color.bottom_tab_tv));
            contentTv1.setTextColor(context.getResources().getColor(R.color.FF999999));
            contentTv2.setTextColor(context.getResources().getColor(R.color.FF999999));
            contentTv3.setTextColor(context.getResources().getColor(R.color.FF999999));
            contentTv4.setTextColor(context.getResources().getColor(R.color.FF999999));
            line1.setBackgroundColor(context.getResources().getColor(R.color.line_color));
            line2.setBackgroundColor(context.getResources().getColor(R.color.line_color));
            line3.setBackgroundColor(context.getResources().getColor(R.color.line_color));
            line4.setBackgroundColor(context.getResources().getColor(R.color.line_color));
        }else {
            cancelTv.setBackgroundResource(R.drawable.white_around_radius_24_night);
            parentLayout.setBackgroundResource(R.drawable.white_around_radius_24_night);
            img1.setImageResource(R.mipmap.dialog_no_interest_night);
            img2.setImageResource(R.mipmap.dialog_blacklist_night);
            img3.setImageResource(R.mipmap.dialog_feedback_night);
            img4.setImageResource(R.mipmap.dialog_label_night);
            img5.setImageResource(R.mipmap.icon_window_inform_night);
            arrowImg.setImageResource(R.mipmap.arrow_next_night);
            titleTv1.setTextColor(context.getResources().getColor(R.color.bottom_tab_tv_night));
            titleTv2.setTextColor(context.getResources().getColor(R.color.bottom_tab_tv_night));
            titleTv3.setTextColor(context.getResources().getColor(R.color.bottom_tab_tv_night));
            titleTv4.setTextColor(context.getResources().getColor(R.color.bottom_tab_tv_night));
            titleTv5.setTextColor(context.getResources().getColor(R.color.bottom_tab_tv_night));
            contentTv1.setTextColor(context.getResources().getColor(R.color.FF999999_night));
            contentTv2.setTextColor(context.getResources().getColor(R.color.FF999999_night));
            contentTv3.setTextColor(context.getResources().getColor(R.color.FF999999_night));
            contentTv4.setTextColor(context.getResources().getColor(R.color.FF999999_night));
            line1.setBackgroundColor(context.getResources().getColor(R.color.line_color_night));
            line2.setBackgroundColor(context.getResources().getColor(R.color.line_color_night));
            line3.setBackgroundColor(context.getResources().getColor(R.color.line_color_night));
            line4.setBackgroundColor(context.getResources().getColor(R.color.line_color_night));
        }

        //是否隐藏举报
        if(hideReport) {
            layout5.setVisibility(View.GONE);
        }
        if(null != unLikeBean) {
            //设置文本
            if(0 == unLikeBean.type) { //抓取的数据，显示最上面两条
                layout3.setVisibility(View.GONE);
                layout4.setVisibility(View.GONE);
            }else {
                if(null == unLikeBean.labels || 0 ==unLikeBean.labels.length) {
                    layout4.setVisibility(View.GONE);
                }
            }

            if(StringUtil.isEmpty(unLikeBean.title) && StringUtil.isEmpty(unLikeBean.user_nickname)) { //话题写死
                titleTv1.setText("不感兴趣");
                titleTv2.setText("屏蔽用户");
                contentTv1.setText("不喜欢这条动态内容");
                contentTv2.setText("减少该用户动态内容推送");
            }else { //话题以外
                titleTv1.setText("不喜欢："+unLikeBean.title);
                titleTv2.setText("不喜欢："+unLikeBean.user_nickname);
                titleTv3.setText("不喜欢："+unLikeBean.keywords+"的内容");
            }
            //
            if(null != unLikeBean.labels) {
                LabelAdapter adapter = new LabelAdapter(context,unLikeBean.labels);
                gridView.setAdapter(adapter);
                adapter.setLabelListener(new LabelAdapter.LabelListener() {
                    @Override
                    public void label() {
                        onItemClick.onWhichItemClick(4);
                        dismiss();
                    }
                });
            }
        }

        layout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClick!=null){
                    onItemClick.onWhichItemClick(1);
                    dismiss();
                }
            }
        });

        layout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClick!=null){
                    onItemClick.onWhichItemClick(2);
                    dismiss();
                }
            }
        });
        layout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClick!=null){
                    onItemClick.onWhichItemClick(3);
                    dismiss();
                }
            }
        });

        layout4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        layout5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClick!=null){
                    onItemClick.onWhichItemClick(5);
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
}
