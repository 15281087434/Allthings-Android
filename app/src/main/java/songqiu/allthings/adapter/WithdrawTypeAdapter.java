package songqiu.allthings.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import songqiu.allthings.R;
import songqiu.allthings.bean.WithdrawListBean;
import songqiu.allthings.iterface.WithdrawListener;
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
public class WithdrawTypeAdapter extends BaseAdapter {

    Context context;
    List<WithdrawListBean> list;
    WithdrawListener withdrawListener;

    public WithdrawTypeAdapter(Context context, List<WithdrawListBean> list) {
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
            convertView = mInflater.inflate(R.layout.item_withdraw_type, null);
            viewHolder.layout = convertView.findViewById(R.id.layout);
            viewHolder.numTv = convertView.findViewById(R.id.numTv);
            viewHolder.hintTv = convertView.findViewById(R.id.hintTv);
            viewHolder.selectImg = convertView.findViewById(R.id.selectImg);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        viewHolder.numTv.setText(String.valueOf(list.get(position).money_type)+"元");
        if(!StringUtil.isEmpty(list.get(position).hint)) {
            viewHolder.hintTv.setVisibility(View.VISIBLE);
            viewHolder.hintTv.setText(list.get(position).hint);
        }else {
            viewHolder.hintTv.setVisibility(View.GONE);
        }
        if(list.get(position).isClick) {
            viewHolder.numTv.setTextColor(context.getResources().getColor(R.color.normal_color));
            viewHolder.layout.setBackgroundResource(R.drawable.rectangle_normal_color_white);
            viewHolder.selectImg.setVisibility(View.VISIBLE);
            viewHolder.hintTv.setTextColor(context.getResources().getColor(R.color.normal_color));
        }else {
            viewHolder.numTv.setTextColor(context.getResources().getColor(R.color.bottom_tab_tv));
            viewHolder.layout.setBackgroundResource(R.drawable.rectangle_f0f0f0_12px_white);
            viewHolder.selectImg.setVisibility(View.GONE);
            viewHolder.hintTv.setTextColor(context.getResources().getColor(R.color.FF999999));
        }
        viewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                withdrawListener.withDraw(position,list.get(position).money_type);
            }
        });
        return convertView;
    }
    /**
     *保存findViewById获得的子控件索引的内部类
     */
    private  class ViewHolder{
        RelativeLayout layout;
        TextView numTv;
        TextView hintTv;
        ImageView selectImg;
    }

    public void setWithdrawListener(WithdrawListener withdrawListener) {
        this.withdrawListener = withdrawListener;
    }
}
