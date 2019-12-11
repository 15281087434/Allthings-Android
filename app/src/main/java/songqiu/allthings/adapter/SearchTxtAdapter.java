package songqiu.allthings.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import songqiu.allthings.R;
import songqiu.allthings.application.MyApplication;
import songqiu.allthings.articledetail.ArticleDetailActivity;
import songqiu.allthings.bean.SearchTxtBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.util.ClickUtil;
import songqiu.allthings.util.DateUtil;
import songqiu.allthings.util.ShowNumUtil;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.util.ViewProportion;
import songqiu.allthings.view.GridViewInScroll;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/8/22
 *
 *类描述：
 *
 ********/
public class SearchTxtAdapter extends RecyclerView.Adapter {

    Context context;
    List<SearchTxtBean> item;

    //设置常量  moudle 1、纯文字  2、大图 3、小图 4、多图
    private final int TYPE_ONLY_TEXT = 1;
    private final int TYPE_BIG_PIC = 2;
    private final int TYPE_SMALL_PIC = 3;
    private final int TYPE_MORE_PIC = 4;//多图

    public SearchTxtAdapter(Context context, List<SearchTxtBean> item) {
        this.context = context;
        this.item = item;
    }

    @Override
    public int getItemViewType(int position) {
        if (1 == item.get(position).module) {
            return TYPE_ONLY_TEXT;
        } else if (2 == item.get(position).module) {
            return TYPE_BIG_PIC;
        } else if (4 == item.get(position).module) {
            return TYPE_MORE_PIC;
        } else {
            return TYPE_SMALL_PIC;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == TYPE_ONLY_TEXT) { //文字
            View view = LayoutInflater.from(context).inflate(R.layout.item_search_tx_no_pic, viewGroup, false);
            return new TextViewHolder(view);
        } else if (viewType == TYPE_BIG_PIC) { //大图
            View view = LayoutInflater.from(context).inflate(R.layout.item_search_tx_big_pic, viewGroup, false);
            return new BigPicViewHolder(view);
        } else if (viewType == TYPE_MORE_PIC) { //多图
            View view = LayoutInflater.from(context).inflate(R.layout.item_search_tx_more_pic, viewGroup, false);
            return new MorePicViewHolder(view);
        } else { //小图
            View view = LayoutInflater.from(context).inflate(R.layout.item_search_tx_right_pic, viewGroup, false);
            return new RightPicViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof RightPicViewHolder) {
            setRightPic((RightPicViewHolder)holder, position);
        } else if (holder instanceof TextViewHolder) {
            setText((TextViewHolder)holder, position);
        } else if (holder instanceof BigPicViewHolder) {
            setBigPic((BigPicViewHolder)holder, position);
        } else if (holder instanceof MorePicViewHolder) {
            setMorePic((MorePicViewHolder)holder,position);
        }
    }

    public void setRightPic(RightPicViewHolder holder, int position) {
        RequestOptions options1 = new RequestOptions()
                .error(R.mipmap.pic_default_small)
                .placeholder(R.mipmap.pic_default_small);
        if (!StringUtil.isEmpty(item.get(position).photo)) {
            if (!item.get(position).photo.contains("http")) {
                item.get(position).photo = HttpServicePath.BasePicUrl + item.get(position).photo;
            }
        }
        Glide.with(context).load(item.get(position).photo).apply(options1).into(holder.rightPic);

        holder.titleTv.setText(item.get(position).title);
        holder.lookTv.setText(String.valueOf(item.get(position).view_num) + "次");
        holder.contentTv.setText(item.get(position).descriptions);
        holder.userName.setText(item.get(position).user_nickname);
        //标签
        if(!StringUtil.isEmpty(item.get(position).keywords)) {
            holder.keywordsTv.setText(item.get(position).keywords);
            int colorIndex = item.get(position).color;
            holder.keywordsTv.setTextColor(context.getResources().getColor(MyApplication.getInstance().colorTextViewMap.get(colorIndex)));
            holder.keywordsTv.setBackgroundResource(MyApplication.getInstance().colorBackGroundMap.get(colorIndex));
        }else {
            holder.keywordsTv.setVisibility(View.GONE);
        }
        //多标签
        if(null != item.get(position).labels && 0!=item.get(position).labels.length) {
            if(1==item.get(position).labels.length) {
                holder.labels1.setVisibility(View.VISIBLE);
                holder.labels1.setText(item.get(position).labels[0]);
                holder.labels2.setVisibility(View.GONE);
            }else {
                holder.labels1.setVisibility(View.VISIBLE);
                holder.labels1.setText(item.get(position).labels[0]);
                holder.labels2.setVisibility(View.VISIBLE);
                holder.labels2.setText(item.get(position).labels[1]);
            }
        }else {
            holder.labels1.setVisibility(View.GONE);
            holder.labels2.setVisibility(View.GONE);
        }
        holder.collectTv.setText(ShowNumUtil.showUnm(item.get(position).collect_num));
        if(0!=item.get(position).popular_icon) {
            holder.hotTv.setVisibility(View.VISIBLE);
        }else {
            holder.hotTv.setVisibility(View.GONE);
        }
        if(0!=item.get(position).push_icon) {
            holder.recommendTv.setVisibility(View.VISIBLE);
        }else {
            holder.recommendTv.setVisibility(View.GONE);
        }
        if(0!=item.get(position).new_icon) {
            holder.newTv.setVisibility(View.VISIBLE);
        }else {
            holder.newTv.setVisibility(View.GONE);
        }
        //判断时间 昨天  今天
        long time = item.get(position).created * 1000;
        if (DateUtil.IsToday(time)) {
            holder.tiemTv.setText("刚刚");
        } else if (DateUtil.IsYesterday(time)) {
            holder.tiemTv.setText("1天前");
        } else {
            holder.tiemTv.setText(DateUtil.getTimeBig1(time));
        }
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClickUtil.onClick()) {
                    Intent intent = new Intent(context, ArticleDetailActivity.class);
                    intent.putExtra("articleid", item.get(position).articleid);
                    context.startActivity(intent);
                }
            }
        });
    }

    public void setText(TextViewHolder holder, int position) {
        holder.titleTv.setText(item.get(position).title);
        holder.contentTv.setText(item.get(position).descriptions);
        holder.userName.setText(item.get(position).user_nickname);
        //标签
        if(!StringUtil.isEmpty(item.get(position).keywords)) {
            holder.keywordsTv.setText(item.get(position).keywords);
            int colorIndex = item.get(position).color;
            holder.keywordsTv.setTextColor(context.getResources().getColor(MyApplication.getInstance().colorTextViewMap.get(colorIndex)));
            holder.keywordsTv.setBackgroundResource(MyApplication.getInstance().colorBackGroundMap.get(colorIndex));
        }else {
            holder.keywordsTv.setVisibility(View.GONE);
        }
        //多标签
        if(null != item.get(position).labels && 0!=item.get(position).labels.length) {
            if(1==item.get(position).labels.length) {
                holder.labels1.setVisibility(View.VISIBLE);
                holder.labels1.setText(item.get(position).labels[0]);
                holder.labels2.setVisibility(View.GONE);
            }else {
                holder.labels1.setVisibility(View.VISIBLE);
                holder.labels1.setText(item.get(position).labels[0]);
                holder.labels2.setVisibility(View.VISIBLE);
                holder.labels2.setText(item.get(position).labels[1]);
            }
        }else {
            holder.labels1.setVisibility(View.GONE);
            holder.labels2.setVisibility(View.GONE);
        }
        holder.collectTv.setText(ShowNumUtil.showUnm(item.get(position).collect_num));
        if(0!=item.get(position).popular_icon) {
            holder.hotTv.setVisibility(View.VISIBLE);
        }else {
            holder.hotTv.setVisibility(View.GONE);
        }
        if(0!=item.get(position).push_icon) {
            holder.recommendTv.setVisibility(View.VISIBLE);
        }else {
            holder.recommendTv.setVisibility(View.GONE);
        }
        if(0!=item.get(position).new_icon) {
            holder.newTv.setVisibility(View.VISIBLE);
        }else {
            holder.newTv.setVisibility(View.GONE);
        }

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClickUtil.onClick()) {
                    Intent intent = new Intent(context, ArticleDetailActivity.class);
                    intent.putExtra("articleid", item.get(position).articleid);
                    context.startActivity(intent);
                }
            }
        });
    }

    public void setBigPic(BigPicViewHolder holder, int position) {
        holder.bigPicImg.post(new Runnable() {
            @Override
            public void run() {
                holder.bigPicImg.setLayoutParams(ViewProportion.getLinearParams(holder.bigPicImg, 2.2));
            }
        });
        RequestOptions options1 = new RequestOptions()
                .error(R.mipmap.pic_default)
                .placeholder(R.mipmap.pic_default);
        if (!StringUtil.isEmpty(item.get(position).photo)) {
            if (!item.get(position).photo.contains("http")) {
                item.get(position).photo = HttpServicePath.BasePicUrl + item.get(position).photo;
            }
        }
        Glide.with(context).load(item.get(position).photo).apply(options1).into(holder.bigPicImg);
        holder.titleTv.setText(item.get(position).title);
        holder.contentTv.setText(item.get(position).descriptions);
        holder.userName.setText(item.get(position).user_nickname);
        //标签
        if(!StringUtil.isEmpty(item.get(position).keywords)) {
            holder.keywordsTv.setText(item.get(position).keywords);
            int colorIndex = item.get(position).color;
            holder.keywordsTv.setTextColor(context.getResources().getColor(MyApplication.getInstance().colorTextViewMap.get(colorIndex)));
            holder.keywordsTv.setBackgroundResource(MyApplication.getInstance().colorBackGroundMap.get(colorIndex));
        }else {
            holder.keywordsTv.setVisibility(View.GONE);
        }
        //多标签
        if(null != item.get(position).labels && 0!=item.get(position).labels.length) {
            if(1==item.get(position).labels.length) {
                holder.labels1.setVisibility(View.VISIBLE);
                holder.labels1.setText(item.get(position).labels[0]);
                holder.labels2.setVisibility(View.GONE);
            }else {
                holder.labels1.setVisibility(View.VISIBLE);
                holder.labels1.setText(item.get(position).labels[0]);
                holder.labels2.setVisibility(View.VISIBLE);
                holder.labels2.setText(item.get(position).labels[1]);
            }
        }else {
            holder.labels1.setVisibility(View.GONE);
            holder.labels2.setVisibility(View.GONE);
        }
        holder.collectTv.setText(ShowNumUtil.showUnm(item.get(position).collect_num));
        if(0!=item.get(position).popular_icon) {
            holder.hotTv.setVisibility(View.VISIBLE);
        }else {
            holder.hotTv.setVisibility(View.GONE);
        }
        if(0!=item.get(position).push_icon) {
            holder.recommendTv.setVisibility(View.VISIBLE);
        }else {
            holder.recommendTv.setVisibility(View.GONE);
        }
        if(0!=item.get(position).new_icon) {
            holder.newTv.setVisibility(View.VISIBLE);
        }else {
            holder.newTv.setVisibility(View.GONE);
        }

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClickUtil.onClick()) {
                    Intent intent = new Intent(context, ArticleDetailActivity.class);
                    intent.putExtra("articleid", item.get(position).articleid);
                    context.startActivity(intent);
                }
            }
        });
    }

    public void setMorePic(MorePicViewHolder holder,int position) {
        holder.titleTv.setText(item.get(position).title);
        holder.userName.setText(item.get(position).user_nickname);
        //标签
        if(!StringUtil.isEmpty(item.get(position).keywords)) {
            holder.keywordsTv.setText(item.get(position).keywords);
            int colorIndex = item.get(position).color;
            holder.keywordsTv.setTextColor(context.getResources().getColor(MyApplication.getInstance().colorTextViewMap.get(colorIndex)));
            holder.keywordsTv.setBackgroundResource(MyApplication.getInstance().colorBackGroundMap.get(colorIndex));
        }else {
            holder.keywordsTv.setVisibility(View.GONE);
        }
        //多标签
        if(null != item.get(position).labels && 0!=item.get(position).labels.length) {
            if(1==item.get(position).labels.length) {
                holder.labels1.setVisibility(View.VISIBLE);
                holder.labels1.setText(item.get(position).labels[0]);
                holder.labels2.setVisibility(View.GONE);
            }else {
                holder.labels1.setVisibility(View.VISIBLE);
                holder.labels1.setText(item.get(position).labels[0]);
                holder.labels2.setVisibility(View.VISIBLE);
                holder.labels2.setText(item.get(position).labels[1]);
            }
        }else {
            holder.labels1.setVisibility(View.GONE);
            holder.labels2.setVisibility(View.GONE);
        }
        holder.collectTv.setText(ShowNumUtil.showUnm(item.get(position).collect_num));
        if(0!=item.get(position).popular_icon) {
            holder.hotTv.setVisibility(View.VISIBLE);
        }else {
            holder.hotTv.setVisibility(View.GONE);
        }
        if(0!=item.get(position).push_icon) {
            holder.recommendTv.setVisibility(View.VISIBLE);
        }else {
            holder.recommendTv.setVisibility(View.GONE);
        }
        if(0!=item.get(position).new_icon) {
            holder.newTv.setVisibility(View.VISIBLE);
        }else {
            holder.newTv.setVisibility(View.GONE);
        }
        if (null != item.get(position).photos) {
            ImageTextMorePicAdapter gambitMorePicAdapter = new ImageTextMorePicAdapter(context, item.get(position).photos);
            holder.gridView.setAdapter(gambitMorePicAdapter);
            holder.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(context, ArticleDetailActivity.class);
                    intent.putExtra("articleid", item.get(position).articleid);
                    context.startActivity(intent);
                }
            });
        }
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClickUtil.onClick()) {
                    Intent intent = new Intent(context, ArticleDetailActivity.class);
                    intent.putExtra("articleid", item.get(position).articleid);
                    context.startActivity(intent);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return item.size();
    }

    public class TextViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.titleTv)
        TextView titleTv;
        @BindView(R.id.contentTv)
        TextView contentTv;
        @BindView(R.id.userName)
        TextView userName;
        @BindView(R.id.layout)
        LinearLayout layout;
        @BindView(R.id.collectTv)
        TextView collectTv;
        @BindView(R.id.hotTv)
        TextView hotTv;
        @BindView(R.id.recommendTv)
        TextView recommendTv;
        @BindView(R.id.newTv)
        TextView newTv;
        //标签
        @BindView(R.id.keywordsTv)
        TextView keywordsTv;
        @BindView(R.id.labels1)
        TextView labels1;
        @BindView(R.id.labels2)
        TextView labels2;

        public TextViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class RightPicViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.titleTv)
        TextView titleTv;
        @BindView(R.id.tiemTv)
        TextView tiemTv;
        @BindView(R.id.lookTv)
        TextView lookTv;
        @BindView(R.id.contentTv)
        TextView contentTv;
        @BindView(R.id.rightPic)
        ImageView rightPic;
        @BindView(R.id.userIcon)
        ImageView userIcon;
        @BindView(R.id.userName)
        TextView userName;
        @BindView(R.id.layout)
        LinearLayout layout;
        @BindView(R.id.collectTv)
        TextView collectTv;
        @BindView(R.id.hotTv)
        TextView hotTv;
        @BindView(R.id.recommendTv)
        TextView recommendTv;
        @BindView(R.id.newTv)
        TextView newTv;
        //标签
        @BindView(R.id.keywordsTv)
        TextView keywordsTv;
        @BindView(R.id.labels1)
        TextView labels1;
        @BindView(R.id.labels2)
        TextView labels2;

        public RightPicViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class BigPicViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.titleTv)
        TextView titleTv;
        @BindView(R.id.bigPicImg)
        ImageView bigPicImg;
        @BindView(R.id.contentTv)
        TextView contentTv;
        @BindView(R.id.userName)
        TextView userName;
        @BindView(R.id.layout)
        LinearLayout layout;
        @BindView(R.id.collectTv)
        TextView collectTv;
        @BindView(R.id.hotTv)
        TextView hotTv;
        @BindView(R.id.recommendTv)
        TextView recommendTv;
        @BindView(R.id.newTv)
        TextView newTv;
        //标签
        @BindView(R.id.keywordsTv)
        TextView keywordsTv;
        @BindView(R.id.labels1)
        TextView labels1;
        @BindView(R.id.labels2)
        TextView labels2;

        public BigPicViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class MorePicViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.titleTv)
        TextView titleTv;
        @BindView(R.id.gridView)
        GridViewInScroll gridView;
        @BindView(R.id.userName)
        TextView userName;
        @BindView(R.id.layout)
        LinearLayout layout;
        @BindView(R.id.collectTv)
        TextView collectTv;
        @BindView(R.id.hotTv)
        TextView hotTv;
        @BindView(R.id.recommendTv)
        TextView recommendTv;
        @BindView(R.id.newTv)
        TextView newTv;
        //标签
        @BindView(R.id.keywordsTv)
        TextView keywordsTv;
        @BindView(R.id.labels1)
        TextView labels1;
        @BindView(R.id.labels2)
        TextView labels2;

        public MorePicViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
