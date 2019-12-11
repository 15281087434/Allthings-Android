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
//        RequestOptions options = new RequestOptions()
//                .circleCrop().transforms(new GlideCircleTransform(context))
//                .error(R.mipmap.head_default)
//                .placeholder(R.mipmap.head_default);
//        if (!StringUtil.isEmpty(item.get(position).avatar)) {
//            if (!item.get(position).avatar.contains("http")) {
//                item.get(position).avatar = HttpServicePath.BasePicUrl + item.get(position).avatar;
//            }
//        }
//        Glide.with(context).load(item.get(position).avatar).apply(options).into(holder.userIcon);
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
        holder.collectTv.setText(ShowNumUtil.showUnm(item.get(position).collect_num));

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
        holder.collectTv.setText(ShowNumUtil.showUnm(item.get(position).collect_num));

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
        holder.collectTv.setText(ShowNumUtil.showUnm(item.get(position).collect_num));

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
        holder.collectTv.setText(ShowNumUtil.showUnm(item.get(position).collect_num));

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

        public MorePicViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
