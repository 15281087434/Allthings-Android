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
import songqiu.allthings.bean.ReportBean;
import songqiu.allthings.bean.SearchHistoryBean;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/8/28
 *
 *类描述：
 *
 ********/
public class SearchHistoryAdapter extends BaseAdapter {
    Context context;
    List<SearchHistoryBean> list;
    OnItemSelectedListener mListener;

    public SearchHistoryAdapter(Context context, List<SearchHistoryBean> list){
        this.context = context;
        this.list = list;
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
            convertView = mInflater.inflate(R.layout.item_history_search, null);
            holder.tv_contents = convertView.findViewById(R.id.tv_contents);
            holder.removeImg = convertView.findViewById(R.id.removeImg);
            holder.layout = convertView.findViewById(R.id.layout);
            convertView.setTag(holder);
        }else {
            holder = (Holder) convertView.getTag();
        }

        holder.tv_contents.setText(list.get(position).content);

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemSelected(list.get(position));
            }
        });

        holder.removeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.detel(list.get(position));
            }
        });
        return convertView;
    }
    private class Holder {
        TextView tv_contents;
        ImageView removeImg;
        LinearLayout layout;
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        mListener = listener;
    }

    public interface OnItemSelectedListener {
        void onItemSelected(SearchHistoryBean searchHistoryBean);
        void detel(SearchHistoryBean searchHistoryBean);
    }

}
