package songqiu.allthings.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.List;

import songqiu.allthings.R;
import songqiu.allthings.adapter.ReportAdapter;
import songqiu.allthings.articledetail.ArticleDetailActivity;
import songqiu.allthings.bean.ReportBean;
import songqiu.allthings.iterface.WindowShareListener;
import songqiu.allthings.util.SharedPreferencedUtils;
import songqiu.allthings.util.ToastUtil;
import songqiu.allthings.util.statusbar.StatusBarUtils;
import songqiu.allthings.util.theme.ThemeManager;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/8/28
 *
 *类描述：分享弹窗
 *
 ********/

public class SharePopupWindows extends PopupWindow{

    private View mView;
    WindowShareListener windowShareListener;


    public SharePopupWindows(Context context,int type,int positon) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.share_bottom_popup, null);
        TextView cancelTv = mView.findViewById(R.id.cancelTv);
        LinearLayout qqLayout = mView.findViewById(R.id.qqLayout);
        LinearLayout weChatLayout = mView.findViewById(R.id.weChatLayout);
        LinearLayout weChatFriendLayout = mView.findViewById(R.id.weChatFriendLayout);
        LinearLayout linkLayout = mView.findViewById(R.id.linkLayout);
        LinearLayout layout = mView.findViewById(R.id.layout);
        LinearLayout reportLayout = mView.findViewById(R.id.reportLayout);
        LinearLayout daytimeLayout = mView.findViewById(R.id.daytimeLayout);
        LinearLayout nightLayout = mView.findViewById(R.id.nightLayout);
        ImageView repotImg = mView.findViewById(R.id.repotImg);
        TextView modelTv = mView.findViewById(R.id.modelTv);
        ImageView modelImg = mView.findViewById(R.id.modelImg);
        LinearLayout parentLayout = mView.findViewById(R.id.parentLayout);
        View line = mView.findViewById(R.id.line);
        View line1 = mView.findViewById(R.id.line1);

        boolean dayModel = SharedPreferencedUtils.getBoolean(context,SharedPreferencedUtils.dayModel,true);
        if(dayModel) {
            modelTv.setText("夜间模式");
            modelImg.setImageResource(R.mipmap.window_night);
            parentLayout.setBackgroundResource(R.drawable.report_buttom_radius);
            cancelTv.setTextColor(context.getResources().getColor(R.color.FF666666));
            line.setBackgroundColor(context.getResources().getColor(R.color.line_color));
            line1.setBackgroundColor(context.getResources().getColor(R.color.line_color));
            repotImg.setImageResource(R.mipmap.window_report);
        }else {
            modelTv.setText("日间模式");
            modelImg.setImageResource(R.mipmap.window_daytime);
            parentLayout.setBackgroundResource(R.drawable.report_buttom_radius_night);
            cancelTv.setTextColor(context.getResources().getColor(R.color.bottom_tab_tv_night));
            line.setBackgroundColor(context.getResources().getColor(R.color.line_color_night));
            line1.setBackgroundColor(context.getResources().getColor(R.color.line_color_night));
            repotImg.setImageResource(R.mipmap.window_report_night);
        }

        if(0 == type) {
            layout.setVisibility(View.GONE);
        }else {
            layout.setVisibility(View.VISIBLE);
        }
        qqLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                windowShareListener.qqShare(positon);
                dismiss();
            }
        });

        weChatLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                windowShareListener.wechatShare(positon);
                dismiss();
            }
        });

        weChatFriendLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                windowShareListener.wechatFriendShare(positon);
                dismiss();
            }
        });

        linkLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                windowShareListener.link(positon);
                dismiss();
            }
        });

        reportLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                windowShareListener.report();
                dismiss();
            }
        });

        daytimeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                windowShareListener.daytime();
                dismiss();
            }
        });

        nightLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                windowShareListener.night();
                dismiss();
            }
        });

        cancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        //设置PopupWindow的View
        this.setContentView(mView);
        //设置PopupWindow弹出窗体的宽
        this.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        //设置PopupWindow弹出窗体的高
        this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        //设置PopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.Animation);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
    }

    public void setWindowShareListener(WindowShareListener windowShareListener) {
        this.windowShareListener = windowShareListener;
    }


}

