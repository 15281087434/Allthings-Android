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
import songqiu.allthings.bean.EarningsListBean;
import songqiu.allthings.util.DateUtil;
import songqiu.allthings.util.LogUtil;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/8/22
 *
 *类描述：
 *
 ********/
public class EarningsAdapter extends RecyclerView.Adapter {

    Context context;
    List<EarningsListBean> list;
    private final int TYPE_HAVE_DATA= 1;
    private final int TYPE_NO_DATA = 2;

    public EarningsAdapter(Context context, List<EarningsListBean> list) {
        this.context = context;
        this.list = list;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == TYPE_HAVE_DATA) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_earnings, viewGroup, false);
            return new ViewHolder(view);
        }else if(viewType == TYPE_NO_DATA){
            View view = LayoutInflater.from(context).inflate(R.layout.item_earnings_empty, viewGroup, false);
            return new ViewEmptyHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if(viewHolder instanceof ViewHolder) {
            ((ViewHolder)viewHolder).nameTv.setText(list.get(position).type_name);
            if(1 == list.get(position).type) {
                if(0>list.get(position).change_num) {
                    ((ViewHolder)viewHolder).numTv.setText(list.get(position).change_num+"金币");
                }else {
                    ((ViewHolder)viewHolder).numTv.setText("+"+list.get(position).change_num+"金币");
                }
            }else {
                if(0>list.get(position).change_num) {
                    ((ViewHolder)viewHolder).numTv.setText(list.get(position).change_num+"元");
                }else {
                    ((ViewHolder)viewHolder).numTv.setText("+"+list.get(position).change_num+"元");
                }
            }
            //时间
            long time = list.get(position).created*1000;
            ((ViewHolder)viewHolder).timeTv.setText(DateUtil.getTimeBig4(time));
        }else if(viewHolder instanceof ViewEmptyHolder) {

        }
    }

    @Override
    public int getItemViewType(int position) {
        if(list.get(position).isEmpty) {
            return TYPE_NO_DATA;
        }else {
           return TYPE_HAVE_DATA;
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.nameTv)
        TextView nameTv;
        @BindView(R.id.numTv)
        TextView numTv;
        @BindView(R.id.timeTv)
        TextView timeTv;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class ViewEmptyHolder extends RecyclerView.ViewHolder{
        public ViewEmptyHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
