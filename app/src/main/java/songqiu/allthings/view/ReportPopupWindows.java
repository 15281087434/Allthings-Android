package songqiu.allthings.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import songqiu.allthings.Event.EventTags;
import songqiu.allthings.R;
import songqiu.allthings.adapter.ReportAdapter;
import songqiu.allthings.bean.ReportBean;
import songqiu.allthings.bean.UserInfoBean;
import songqiu.allthings.http.BaseBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.http.OkHttp;
import songqiu.allthings.http.RequestCallBack;
import songqiu.allthings.util.LogUtil;
import songqiu.allthings.util.SharedPreferencedUtils;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.util.ToastUtil;
import songqiu.allthings.videodetail.VideoDetailActivity;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/8/28
 *
 *类描述：举报弹窗
 *
 ********/

public class ReportPopupWindows extends PopupWindow {


    private View mView;
    private List<Integer> labelList;
//    private int mid; //内容id
//    private int type;//1=文章，2=视频，3=话题，4=评论

    public ReportPopupWindows(Context context, List<ReportBean> list,int mid,int type) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.report_bottom_popup, null);
        TextView btnSure = (TextView)mView.findViewById(R.id.btnSure);
        ListView listView = (ListView)mView.findViewById(R.id.listView);
        LinearLayout parentLayout = mView.findViewById(R.id.parentLayout);
        View line = mView.findViewById(R.id.line);
        //
        labelList = new ArrayList<>();
        boolean dayModel = SharedPreferencedUtils.getBoolean(context,SharedPreferencedUtils.dayModel,true);
        if(dayModel) {
            parentLayout.setBackgroundResource(R.drawable.report_buttom_radius);
            line.setBackgroundColor(context.getResources().getColor(R.color.line_color));
            btnSure.setTextColor(context.getResources().getColor(R.color.FF999999));
        }else {
            parentLayout.setBackgroundResource(R.drawable.report_buttom_radius_night);
            line.setBackgroundColor(context.getResources().getColor(R.color.line_color_night));
            btnSure.setTextColor(context.getResources().getColor(R.color.FF999999_night));
        }

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
        btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = btnSure.getText().toString();
                if(text.equals("确定")) {
                    toReport(context,labelList,mid,type);
                    dismiss();
                    new Handler().postDelayed(new Runnable(){
                        public void run() {
                            //execute the task
                            ToastUtil.showToast("举报成功");
                        }
                    }, 500);
                }else {
                    dismiss();
                }
            }
        });

        ReportAdapter adapter = new ReportAdapter(context,list,dayModel);
        listView.setAdapter(adapter);
        adapter.setOnItemSelectedListener(new ReportAdapter.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int position, List<ReportBean> list) {
                boolean isClick = list.get(position).isClick;
                if(isClick) {
                    if(null != labelList && 0!=labelList.size()) {
                        for(int i = 0;i<labelList.size();i++) {
                            if(labelList.get(i) == position) {
                                labelList.remove(i);
                            }
                        }
                    }
                    isClick = false;
                }else {
                    //向数组里添加
                    labelList.add(position);
                    isClick = true;
                }
                list.get(position).isClick = isClick;
                for(int i = 0;i<list.size();i++) {
                    if(list.get(position).isClick) {
                        btnSure.setTextColor(context.getResources().getColor(R.color.normal_color));
                        btnSure.setText("确定");
                    }
                }
                int k = 0;
                for(int j = 0;j<list.size();j++) {
                    if(list.get(j).isClick) {
                        k = k+1;
                    }
                }
                if(k == 0) {
                    if(dayModel) {
                        btnSure.setTextColor(context.getResources().getColor(R.color.FF999999));
                    }else {
                        btnSure.setTextColor(context.getResources().getColor(R.color.FF999999_night));
                    }
                    btnSure.setText("取消");
                }
                adapter.notifyDataSetChanged();
            }
        });
    }


    public void toReport(Context context,List<Integer> labelList,int mid,int type) {
        Map<String, Object> map = new HashMap<>();
        map.put("label",labelList);
        map.put("mid",mid);
        map.put("type",type);
        OkHttp.postObject(context, HttpServicePath.URL_REPORT, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {

            }
        });
    }

}

