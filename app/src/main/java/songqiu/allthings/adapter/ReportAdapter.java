package songqiu.allthings.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import songqiu.allthings.R;
import songqiu.allthings.bean.ReportBean;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/8/28
 *
 *类描述：
 *
 ********/
public class ReportAdapter extends BaseAdapter {
    Context context;
    List<ReportBean> list;
    boolean isDay;
    OnItemSelectedListener mListener;

    public ReportAdapter(Context context, List<ReportBean> list,boolean isDay){
        this.context = context;
        this.list = list;
        this.isDay = isDay;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if(convertView == null || convertView.getTag() == null){
            holder = new Holder();
            LayoutInflater mInflater = LayoutInflater.from(context);
            convertView = mInflater.inflate(R.layout.item_dialog_report, null);
            holder.tv = convertView.findViewById(R.id.tv);
            holder.layout = convertView.findViewById(R.id.layout);
            holder.line = convertView.findViewById(R.id.line);
            convertView.setTag(holder);
        }else {
            holder = (Holder) convertView.getTag();
        }
        if(list.get(position).isClick) {
            holder.tv.setTextColor(context.getResources().getColor(R.color.normal_color));
        }else {
            if(isDay) {
                holder.tv.setTextColor(context.getResources().getColor(R.color.bottom_tab_tv));
                holder.line.setBackgroundColor(context.getResources().getColor(R.color.line_color));
            }else {
                holder.tv.setTextColor(context.getResources().getColor(R.color.bottom_tab_tv_night));
                holder.line.setBackgroundColor(context.getResources().getColor(R.color.line_color_night));
            }
        }
        if(position == (list.size()-1)) {
            holder.line.setVisibility(View.GONE);
        }else {
            holder.line.setVisibility(View.VISIBLE);
        }
        holder.tv.setText(list.get(position).title);
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemSelected(position, list);
            }
        });
        return convertView;
    }
    private class Holder {
        TextView tv;
        LinearLayout layout;
        View line;
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        mListener = listener;
    }

    public interface OnItemSelectedListener {
        void onItemSelected(int position, List<ReportBean> list);
    }

}
