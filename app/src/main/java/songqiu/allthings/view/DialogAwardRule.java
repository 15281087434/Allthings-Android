package songqiu.allthings.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import songqiu.allthings.R;
import songqiu.allthings.adapter.AwardRuleAdapter;
import songqiu.allthings.adapter.HomeTabClassAdapter;
import songqiu.allthings.bean.AwardRuleBean;
import songqiu.allthings.iterface.DialogDeleteListener;
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
public class DialogAwardRule extends Dialog{

    private Context context;
    private List<AwardRuleBean> awardRuleListBean;
    AwardRuleAdapter awardRuleAdapter;

    public DialogAwardRule(Context context,List<AwardRuleBean> awardRuleListBean) {
        super(context);
        this.context = context;
        this.awardRuleListBean = awardRuleListBean;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    public void init() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_award_rule, null);
        setContentView(view);

        ImageView closeImg = view.findViewById(R.id.closeImg);
        LinearLayout parentLayout = view.findViewById(R.id.parentLayout);
        ImageView headImg = view.findViewById(R.id.headImg);
        TextView tv1 = view.findViewById(R.id.tv1);
        RecyclerView reyclerView = view.findViewById(R.id.reyclerView);
        //
        awardRuleAdapter = new AwardRuleAdapter(context,awardRuleListBean);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        reyclerView.setLayoutManager(linearLayoutManager);
        reyclerView.setAdapter(awardRuleAdapter);

        boolean dayModel = SharedPreferencedUtils.getBoolean(context,SharedPreferencedUtils.dayModel,true);
        if(dayModel) {
            parentLayout.setBackgroundResource(R.drawable.white_around_radius);
            headImg.setImageResource(R.mipmap.icon_dialog_head);
            closeImg.setImageResource(R.mipmap.icon_dialog_close);
            tv1.setTextColor(context.getResources().getColor(R.color.bottom_tab_tv));
        }else {
            tv1.setTextColor(context.getResources().getColor(R.color.bottom_tab_tv_night));
            parentLayout.setBackgroundResource(R.drawable.white_around_radius_night);
            headImg.setImageResource(R.mipmap.icon_dialog_head_night);
            closeImg.setImageResource(R.mipmap.icon_dialog_close_night);
        }

        closeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        lp.width = (int) (d.widthPixels * 0.8); // 宽度设置为屏幕的0.7
        dialogWindow.setAttributes(lp);
    }



}
