package songqiu.allthings.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import songqiu.allthings.R;
import songqiu.allthings.bean.ArticleLabelsBean;
import songqiu.allthings.http.HttpServicePath;
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
public class ArticleLabelsAdapter extends BaseAdapter {

    Context context;
    List<ArticleLabelsBean> item;

    public ArticleLabelsAdapter(Context context, List<ArticleLabelsBean> item) {
        this.context = context;
        this.item = item;
    }
    @Override
    public int getCount() {
        return item.size();
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
            convertView = mInflater.inflate(R.layout.item_article_labels, null);
            viewHolder.classificationTv = convertView.findViewById(R.id.classificationTv);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        viewHolder.classificationTv.setText(item.get(position).name);
        if(item.get(position).isClick) {
            viewHolder.classificationTv.setBackgroundResource(R.drawable.classification_item_click);
            viewHolder.classificationTv.setTextColor(context.getResources().getColor(R.color.normal_color));
        }else {
            viewHolder.classificationTv.setBackgroundResource(R.drawable.classification_item0_normal);
            viewHolder.classificationTv.setTextColor(context.getResources().getColor(R.color.bottom_tab_tv));
        }
        return convertView;
    }

    private  class ViewHolder{
        TextView classificationTv;
    }

}
