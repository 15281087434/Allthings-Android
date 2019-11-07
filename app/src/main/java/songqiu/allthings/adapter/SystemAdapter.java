package songqiu.allthings.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import songqiu.allthings.R;
import songqiu.allthings.bean.SystemListBean;
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
public class SystemAdapter extends RecyclerView.Adapter<SystemAdapter.ViewHolder> {

    Context context;
    List<SystemListBean> list;

    public SystemAdapter(Context context, List<SystemListBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_system, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.userName.setText(list.get(position).title);
        viewHolder.contentTv.setText(list.get(position).content);

//        //时间
        long time = list.get(position).created*1000;
        viewHolder.timeTv.setText(DateUtil.getTimeBig3(time));
        if(2==list.get(position).news_status) {
            viewHolder.dotImg.setVisibility(View.GONE);
        }
        if(2==list.get(position).news_status) {
            viewHolder.dotImg.setVisibility(View.GONE);
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
        @BindView(R.id.userName)
        TextView userName;
        @BindView(R.id.dotImg)
        ImageView dotImg;
        @BindView(R.id.timeTv)
        TextView timeTv;
        @BindView(R.id.contentTv)
        TextView contentTv;
        @BindView(R.id.layout)
        LinearLayout layout;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
