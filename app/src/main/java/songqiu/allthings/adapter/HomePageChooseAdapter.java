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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import songqiu.allthings.R;
import songqiu.allthings.articledetail.ArticleDetailActivity;
import songqiu.allthings.bean.HomeSubitemBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.util.ClickUtil;
import songqiu.allthings.util.StringUtil;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/6/24
 *
 *类描述：
 *
 ********/
public class HomePageChooseAdapter extends RecyclerView.Adapter {

    Context context;
    List<HomeSubitemBean> item;
    private final int TYPE_EMPTY = 3;
    private final int TYPE_TITLE = 1;
    private final int TYPE_CONTENT = 2;

    public HomePageChooseAdapter(Context context, List<HomeSubitemBean> item) {
        this.context = context;
        this.item = item;
    }

    @Override
    public int getItemViewType(int position) {
        if (item.get(position).empty) {
            return TYPE_EMPTY;
        } else {
            if (!StringUtil.isEmpty(item.get(position).dayTiem)) {
                return TYPE_TITLE;
            }
            return TYPE_CONTENT;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == TYPE_TITLE) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_home_page_choose_title, null);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(lp);
            return new TitleViewholder(view);
        } else if (viewType == TYPE_CONTENT) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_home_page_choose_content, null);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(lp);
            return new ContentViewholder(view);
        } else if (viewType == TYPE_EMPTY) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_empty_content, null);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.topMargin = 200;
            view.setLayoutParams(lp);
            return new EmptyViewholder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof TitleViewholder) {
            setTitleData((TitleViewholder) holder, position);
        }else if (holder instanceof ContentViewholder) {
            setContentData((ContentViewholder) holder, position);
        }else if(holder instanceof EmptyViewholder) {
            setEmpty((EmptyViewholder)holder);
        }
    }

    public void setEmpty(EmptyViewholder holder) {
        holder.emptyLayout.setVisibility(View.VISIBLE);
    }

    public void setTitleData(TitleViewholder holder, int position) {
        holder.timeTv.setText(item.get(position).dayTiem + "精选  每天多读一点");
        if (0 == position) {
            holder.layout.setVisibility(View.GONE);
        } else {
            holder.layout.setVisibility(View.VISIBLE);
        }
//        if(position == 0) {
//            holder.line.setVisibility(View.GONE);
//        }else {
//            holder.line.setVisibility(View.VISIBLE);
//        }
    }

    public void setContentData(ContentViewholder holder, int position) {
        HomeSubitemBean homeSubitemBean = item.get(position);
        RequestOptions options1 = new RequestOptions()
                .error(R.mipmap.pic_default_small)
                .placeholder(R.mipmap.pic_default_small);
        if (homeSubitemBean.module == 4) { //多图取第一张
            if (null != homeSubitemBean.photos && 0 != homeSubitemBean.photos.length) {
                homeSubitemBean.photo = homeSubitemBean.photos[0];
            }
        }
        if (!StringUtil.isEmpty(homeSubitemBean.photo)) {
            if (!homeSubitemBean.photo.contains("http")) {
                homeSubitemBean.photo = HttpServicePath.BasePicUrl + homeSubitemBean.photo;
            }
        }
        Glide.with(context).load(homeSubitemBean.photo).apply(options1).into(holder.rightPic);

        holder.contentTv.setText(homeSubitemBean.title);
        holder.describeTv.setText(homeSubitemBean.descriptions);
        holder.lookNumTv.setText(String.valueOf(homeSubitemBean.view_num) + "次观看");
        holder.userName.setText(homeSubitemBean.user_nickname);
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClickUtil.onClick()) {
                    Intent intent = new Intent(context, ArticleDetailActivity.class);
                    intent.putExtra("articleid", homeSubitemBean.articleid);
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return item.size();
    }

    public class TitleViewholder extends RecyclerView.ViewHolder {
        //        @BindView(R.id.line)
//        LinearLayout line;
        @BindView(R.id.layout)
        RelativeLayout layout;
        @BindView(R.id.timeTv)
        TextView timeTv;

        public TitleViewholder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    public class ContentViewholder extends RecyclerView.ViewHolder {
        @BindView(R.id.line)
        View line;
        @BindView(R.id.contentTv)
        TextView contentTv;
        @BindView(R.id.describeTv)
        TextView describeTv;
        @BindView(R.id.userName)
        TextView userName;
        @BindView(R.id.lookNumTv)
        TextView lookNumTv;
        @BindView(R.id.rightPic)
        ImageView rightPic;
        @BindView(R.id.layout)
        LinearLayout layout;

        public ContentViewholder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class EmptyViewholder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_icon)
        ImageView ivIcon;
        @BindView(R.id.tv_message)
        TextView tvMessage;
        @BindView(R.id.emptyLayout)
        LinearLayout emptyLayout;
        public EmptyViewholder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
