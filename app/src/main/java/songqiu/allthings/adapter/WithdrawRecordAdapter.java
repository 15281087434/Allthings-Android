package songqiu.allthings.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import songqiu.allthings.R;
import songqiu.allthings.bean.WithdrawRecordBean;
import songqiu.allthings.util.DateUtil;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/8/22
 *
 *类描述：
 *
 ********/
public class WithdrawRecordAdapter extends RecyclerView.Adapter<WithdrawRecordAdapter.ViewHolder> {

    Context context;
    List<WithdrawRecordBean> list;

    public WithdrawRecordAdapter(Context context, List<WithdrawRecordBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_withdraw_record, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.nameTv.setText(list.get(position).take_type);
        viewHolder.stateTv.setText(list.get(position).pay_name);
        viewHolder.numTv.setText("+"+list.get(position).num+"元");
        //时间
        long time = list.get(position).created*1000;
        viewHolder.timeTv.setText(DateUtil.getTimeBig4(time));

        //状态
        if(list.get(position).flag == 1 || list.get(position).flag == 2) { //进行中
            viewHolder.numTv.setTextColor(context.getResources().getColor(R.color.bottom_tab_tv));
            viewHolder.stateTv.setTextColor(context.getResources().getColor(R.color.FF666666));
        }else if(list.get(position).flag == 3) { //已到账
            viewHolder.numTv.setTextColor(context.getResources().getColor(R.color.FFDE5C51));
            viewHolder.stateTv.setTextColor(context.getResources().getColor(R.color.FFDE5C51));
        }else { // 4、5 未成功
            viewHolder.numTv.setTextColor(context.getResources().getColor(R.color.FF999999));
            viewHolder.stateTv.setTextColor(context.getResources().getColor(R.color.FF999999));
        }

    }


    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }


    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.nameTv)
        TextView nameTv;
        @BindView(R.id.timeTv)
        TextView timeTv;
        @BindView(R.id.stateTv)
        TextView stateTv;
        @BindView(R.id.numTv)
        TextView numTv;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
