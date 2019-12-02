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
import songqiu.allthings.util.ClickUtil;
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
public class LabelAdapter extends BaseAdapter {

    Context context;
    String[] labels;
    LabelListener labelListener;

    public LabelAdapter(Context context, String[] labels) {
        this.context = context;
        this.labels = labels;
    }
    @Override
    public int getCount() {
        return labels.length;
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
            convertView = mInflater.inflate(R.layout.item_unlike_label, null);
            viewHolder.textView = convertView.findViewById(R.id.textView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        viewHolder.textView.setText(labels[position]);
        viewHolder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ClickUtil.onClick()) {
                    labelListener.label();
                }
            }
        });
        return convertView;
    }
    /**
     *保存findViewById获得的子控件索引的内部类
     */
    private  class ViewHolder{
        TextView textView;
    }

    public interface LabelListener {
        void label();
    }

    public void setLabelListener(LabelListener labelListener) {
        this.labelListener = labelListener;
    }

}
