package songqiu.allthings.adapter;

import android.content.Context;
import android.content.Intent;
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
import songqiu.allthings.articledetail.ArticleDetailActivity;
import songqiu.allthings.bean.ArticlePutawayBean;
import songqiu.allthings.util.ClickUtil;
import songqiu.allthings.util.DateUtil;
import songqiu.allthings.util.ShowNumUtil;

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
    List<ArticlePutawayBean> list;
    ArticlePutawayListener articlePutawayListener;

    public ArticlePutawayAdapter(Context context, List<ArticlePutawayBean> list) {
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
        viewHolder.titleTv.setText(list.get(position).title);
        long time = list.get(position).created * 1000;
        viewHolder.timeTv.setText("发布时间:"+ DateUtil.getTimeBig4(time));
        viewHolder.likeTv.setText(ShowNumUtil.showUnm(list.get(position).collect_num));
        viewHolder.lookTv.setText(ShowNumUtil.showUnm(list.get(position).view_num));
        //已申请稿酬
        if(list.get(position).is_apply == 1) {
            viewHolder.applyedTv.setVisibility(View.VISIBLE);
            viewHolder.applyTv.setVisibility(View.GONE);
            viewHolder.soldoutTv.setVisibility(View.GONE);
        }else {
            viewHolder.applyedTv.setVisibility(View.GONE);
            viewHolder.applyTv.setVisibility(View.VISIBLE);
            viewHolder.soldoutTv.setVisibility(View.VISIBLE);
            if(list.get(position).level==0) {
                viewHolder.applyTv.setVisibility(View.GONE);
            }else {
                if(list.get(position).is_show ==0 ) {
                    viewHolder.applyTv.setVisibility(View.GONE);
                    viewHolder.soldoutTv.setVisibility(View.GONE);
                }else {
                    viewHolder.applyTv.setVisibility(View.VISIBLE);
                }
            }
        }
        if(list.get(position).article_level == 0) {
            viewHolder.articleleveImg.setVisibility(View.GONE);
        }else {
            viewHolder.articleleveImg.setVisibility(View.VISIBLE);
            if(list.get(position).article_level == 1) {
                viewHolder.articleleveImg.setImageResource(R.mipmap.article_level_a);
            }else if(list.get(position).article_level == 2) {
                viewHolder.articleleveImg.setImageResource(R.mipmap.article_level_b);
            }else if(list.get(position).article_level == 3) {
                viewHolder.articleleveImg.setImageResource(R.mipmap.article_level_c);
            }else if(list.get(position).article_level == 4) {
                viewHolder.articleleveImg.setImageResource(R.mipmap.article_level_d);
            }else if(list.get(position).article_level == 5) {
                viewHolder.articleleveImg.setImageResource(R.mipmap.article_level_t);
            }
        }

        viewHolder.applyTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ClickUtil.onClick()) {
                    articlePutawayListener.apply(list.get(position).id);
                }
            }
        });

        viewHolder.soldoutTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ClickUtil.onClick()) {
                    articlePutawayListener.soldout(list.get(position).id);
                }
            }
        });

        viewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClickUtil.onClick()) {
                    Intent intent = new Intent(context, ArticleDetailActivity.class);
                    intent.putExtra("articleid", list.get(position).id);
                    context.startActivity(intent);
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

    public void setArticlePutawayListener(ArticlePutawayListener articlePutawayListener) {
        this.articlePutawayListener = articlePutawayListener;
    }

    public interface ArticlePutawayListener{
        void soldout(int id);
        void apply(int id);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.articleleveImg)
        ImageView articleleveImg;
        @BindView(R.id.titleTv)
        TextView titleTv;
        @BindView(R.id.timeTv)
        TextView timeTv;
        @BindView(R.id.likeTv)
        TextView likeTv;
        @BindView(R.id.lookTv)
        TextView lookTv;
        @BindView(R.id.applyTv)
        TextView applyTv;
        @BindView(R.id.soldoutTv)
        TextView soldoutTv;
        @BindView(R.id.applyedTv)
        TextView applyedTv;
        @BindView(R.id.layout)
        LinearLayout layout;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
