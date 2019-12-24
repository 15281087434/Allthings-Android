package songqiu.allthings.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.ButterKnife;
import songqiu.allthings.R;
import songqiu.allthings.bean.SystemListBean;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/8/22
 *
 *类描述：
 *
 ********/
public class ArticlePutawayAdapter extends RecyclerView.Adapter<ArticlePutawayAdapter.ViewHolder> {

    Context context;
    List<SystemListBean> list;

    public ArticlePutawayAdapter(Context context, List<SystemListBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_article_putaway, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
//        viewHolder.userName.setText(list.get(position).title);
//        viewHolder.contentTv.setText(list.get(position).content);

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


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
