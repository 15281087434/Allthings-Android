package songqiu.allthings.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import songqiu.allthings.R;
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
public class ImageTextMorePicAdapter extends BaseAdapter {

    Context context;
    String[] item;

    public ImageTextMorePicAdapter(Context context, String[] item) {
        this.context = context;
        this.item = item;
    }
    @Override
    public int getCount() {
        return item.length;
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
            convertView = mInflater.inflate(R.layout.item_image_text_more_pic_gd, null);
            viewHolder.img = convertView.findViewById(R.id.img);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        RequestOptions options = new RequestOptions()
                .error(R.mipmap.pic_default_zhengfangxing)
                .placeholder(R.mipmap.pic_default_zhengfangxing);
        if (!StringUtil.isEmpty(item[position])) {
            if (!item[position].contains("http")) {
                item[position] = HttpServicePath.BasePicUrl + item[position];
            }
        }
        Glide.with(context).load(item[position]).apply(options).into(viewHolder.img);
        return convertView;
    }

    private  class ViewHolder{
        ImageView img;
    }


}
