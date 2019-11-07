package songqiu.allthings.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import songqiu.allthings.R;
import songqiu.allthings.bean.MineFeedbackBean;
import songqiu.allthings.iterface.MineFeedbackListener;
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
public class MineFeedbackAdapter extends RecyclerView.Adapter<MineFeedbackAdapter.ViewHolder> {

    Context context;
    List<MineFeedbackBean> item;
    MineFeedbackListener mineFeedbackListener;

    public MineFeedbackAdapter(Context context, List<MineFeedbackBean> item) {
        this.context = context;
        this.item = item;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_mine_feedback, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.contentTv.setText(item.get(position).feedback_con);
        long time = item.get(position).created*1000;
        holder.timeTv.setText(DateUtil.getTimeBig4(time));
        if(1 == item.get(position).status) { //未回复
            holder.stateTv.setBackgroundResource(R.drawable.rectangle_common_999999);
            holder.stateTv.setText("待回复");
        }else { //已回复
            holder.stateTv.setBackgroundResource(R.drawable.rectangle_common_attention);
            holder.stateTv.setText("已回复");
        }
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mineFeedbackListener.lookFeedback(item.get(position).id);
            }
        });

    }


    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }


    @Override
    public int getItemCount() {
        return item.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.contentTv)
        TextView contentTv;
        @BindView(R.id.timeTv)
        TextView timeTv;
        @BindView(R.id.stateTv)
        TextView stateTv;
        @BindView(R.id.layout)
        LinearLayout layout;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void setMineFeedbackListener(MineFeedbackListener mineFeedbackListener) {
        this.mineFeedbackListener = mineFeedbackListener;
    }
}
