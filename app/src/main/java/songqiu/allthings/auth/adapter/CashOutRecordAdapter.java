package songqiu.allthings.auth.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import songqiu.allthings.R;
import songqiu.allthings.auth.bean.CashOutRecordBean;
import songqiu.allthings.auth.bean.GcLogBean;
import songqiu.allthings.util.DateUtil;

/**
 * create by: linyinjianying
 * time:
 * e_mail:734090232@qq.com
 * description:提现记录适配器
 */
public class CashOutRecordAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    Context context;
    ArrayList<CashOutRecordBean> list;

    public CashOutRecordAdapter(Context context, ArrayList<CashOutRecordBean> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder(LayoutInflater.from(context).inflate(R.layout.item_cashout_record,parent,false));
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        CashOutRecordBean bean=list.get(position);

        holder.getTextView(R.id.tv_money).setText(bean.getNum()+"元");
        holder.getTextView(R.id.tv_time).setText(DateUtil.getTime(bean.getCreated()+"000","MM-dd HH:mm"));
        holder.getTextView(R.id.tv_stutas).setText(bean.getPay_name()+"");
        if(bean.getFlag().equals("3")){
            holder.getTextView(R.id.tv_money).setTextColor(context.getResources().getColor(R.color.FFDE5C51));
            holder.getTextView(R.id.tv_stutas).setTextColor(context.getResources().getColor(R.color.FFDE5C51));
        }else if(bean.getFlag().equals("5")){
            holder.getTextView(R.id.tv_money).setTextColor(context.getResources().getColor(R.color.FF999999));
            holder.getTextView(R.id.tv_stutas).setTextColor(context.getResources().getColor(R.color.FF999999));
        }else {
            holder.getTextView(R.id.tv_money).setTextColor(context.getResources().getColor(R.color.FF666666));
            holder.getTextView(R.id.tv_stutas).setTextColor(context.getResources().getColor(R.color.FF666666));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
