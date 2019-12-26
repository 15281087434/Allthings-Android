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
import songqiu.allthings.bean.ArticleUnPutawayBean;
import songqiu.allthings.util.ClickUtil;
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
public class ArticleUnPutawayAdapter extends RecyclerView.Adapter<ArticleUnPutawayAdapter.ViewHolder> {

    Context context;
    List<ArticleUnPutawayBean> list;
    DeleteListener deleteListener;

    public ArticleUnPutawayAdapter(Context context, List<ArticleUnPutawayBean> list) {
        this.context = context;
        this.list = list;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_article_unputaway, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.titleTv.setText(list.get(position).title);
        long time = list.get(position).created * 1000;
        viewHolder.timeTv.setText("发布时间:"+DateUtil.getTimeBig4(time));
        //0审核中  3未审核  4已下架
        if(list.get(position).status == 0 ) {
            viewHolder.stateTv.setText("审核中");
            viewHolder.stateTv.setTextColor(context.getResources().getColor(R.color.normal_color));
        }else if(list.get(position).status == 3 ) {
            viewHolder.stateTv.setText("未过审");
            viewHolder.stateTv.setTextColor(context.getResources().getColor(R.color.FF5098FC));
        }else if(list.get(position).status == 4 ) {
            viewHolder.stateTv.setText("已下架");
            viewHolder.stateTv.setTextColor(context.getResources().getColor(R.color.FF999999));
        }
        viewHolder.deleteTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ClickUtil.onClick()) {
                    deleteListener.delete(list.get(position).id);
                }
            }
        });
    }


    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface DeleteListener {
        void delete(int articleid);
    }

    public void setDeleteListener(DeleteListener deleteListener) {
        this.deleteListener = deleteListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.titleTv)
        TextView titleTv;
        @BindView(R.id.timeTv)
        TextView timeTv;
        @BindView(R.id.stateTv)
        TextView stateTv;
        @BindView(R.id.deleteTv)
        TextView deleteTv;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
