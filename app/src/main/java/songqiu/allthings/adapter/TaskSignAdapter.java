package songqiu.allthings.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import songqiu.allthings.R;
import songqiu.allthings.bean.TaskSiginListBean;
import songqiu.allthings.iterface.TaskSignListener;
import songqiu.allthings.util.DateUtil;
import songqiu.allthings.util.StringUtil;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/9/11
 *
 *类描述：
 *
 ********/
public class TaskSignAdapter extends BaseAdapter {

    Context context;
    List<TaskSiginListBean> signList;
    TaskSignListener taskSignListener;

    public TaskSignAdapter(Context context, List<TaskSiginListBean> signList) {
        this.context = context;
        this.signList = signList;
    }
    @Override
    public int getCount() {
        return signList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(null == convertView) {
            viewHolder = new ViewHolder();
            LayoutInflater mInflater = LayoutInflater.from(context);
            convertView = mInflater.inflate(R.layout.item_task_sign, null);
            viewHolder.taskSignLayout = convertView.findViewById(R.id.taskSignLayout);
            viewHolder.taskSignImg = convertView.findViewById(R.id.taskSignImg);
            viewHolder.taskSignTv = convertView.findViewById(R.id.taskSignTv);
            viewHolder.taskSignDayTv = convertView.findViewById(R.id.taskSignDayTv);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        TaskSiginListBean taskSiginListBean = signList.get(position);
        if(taskSiginListBean.signHighlight) {
            viewHolder.taskSignImg.setImageDrawable(context.getResources().getDrawable(R.mipmap.task_sign));
            viewHolder.taskSignTv.setTextColor(context.getResources().getColor(R.color.FFFFF8B0));
            viewHolder.taskSignDayTv.setTextColor(context.getResources().getColor(R.color.bottom_tab_tv));
        }else {
            if(1==taskSiginListBean.is_sign) { //已签
                viewHolder.taskSignImg.setImageDrawable(context.getResources().getDrawable(R.mipmap.task_signed));
                viewHolder.taskSignTv.setTextColor(context.getResources().getColor(R.color.FFFFF8B0));
                viewHolder.taskSignDayTv.setTextColor(context.getResources().getColor(R.color.FF666666));
            }else {
                int dayOfWeek = DateUtil.getDayOfWeek();
                if(taskSiginListBean.day == dayOfWeek) {
                    viewHolder.taskSignImg.setImageDrawable(context.getResources().getDrawable(R.mipmap.task_sign));
                    viewHolder.taskSignTv.setTextColor(context.getResources().getColor(R.color.FFFFF8B0));
                    viewHolder.taskSignDayTv.setTextColor(context.getResources().getColor(R.color.bottom_tab_tv));
                }else {
                    viewHolder.taskSignImg.setImageDrawable(context.getResources().getDrawable(R.mipmap.task_sign_no));
                    viewHolder.taskSignDayTv.setTextColor(context.getResources().getColor(R.color.FF999999));
                    viewHolder.taskSignTv.setTextColor(context.getResources().getColor(R.color.FFD5D5D5));
                }

            }
        }

        viewHolder.taskSignTv.setText(String.valueOf(taskSiginListBean.num));
        if(StringUtil.isEmpty(taskSiginListBean.signed)) {
            viewHolder.taskSignDayTv.setText(taskSiginListBean.day+"天");
        }else {
            viewHolder.taskSignDayTv.setText(taskSiginListBean.signed);
        }

        if(taskSiginListBean.signAble) {
            viewHolder.taskSignLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    taskSignListener.sign(taskSiginListBean,taskSiginListBean.num);
                }
            });
        }

        return convertView;
    }
    /**
     *保存findViewById获得的子控件索引的内部类
     */
    private  class ViewHolder{
        LinearLayout taskSignLayout;
        ImageView taskSignImg;
        TextView taskSignTv;
        TextView taskSignDayTv;
    }

    public void setTaskSignListener(TaskSignListener taskSignListener) {
        this.taskSignListener = taskSignListener;
    }
}
