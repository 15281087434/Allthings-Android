package songqiu.allthings.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import songqiu.allthings.R;
import songqiu.allthings.bean.TaskSiginListBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.iterface.TaskSignListener;
import songqiu.allthings.util.GlideCircleTransform;
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
public class FeedbackDetailAdapter extends BaseAdapter {

    Context context;
    List<String> list;

    public FeedbackDetailAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
    }
    @Override
    public int getCount() {
        return list.size();
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
            convertView = mInflater.inflate(R.layout.item_feedback_detail, null);
            viewHolder.img = convertView.findViewById(R.id.img);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        String path;
        RequestOptions options = new RequestOptions()
                .error(R.mipmap.pic_default_small)
                .placeholder(R.mipmap.pic_default_small);
        if(!list.get(position).contains("http")) {
            path = HttpServicePath.BasePicUrl+list.get(position).replace(" ","");
        }else {
            path = list.get(position).replace(" ","");
        }
        Glide.with(context).load(path).apply(options).into(viewHolder.img);
        return convertView;
    }
    /**
     *保存findViewById获得的子控件索引的内部类
     */
    private  class ViewHolder{
        ImageView img;
    }

}
