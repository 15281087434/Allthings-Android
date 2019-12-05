package songqiu.allthings.adapter;

import android.content.Context;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import songqiu.allthings.R;
import songqiu.allthings.bean.DetailCommentListBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.iterface.VideoDetailCommentItemListener;
import songqiu.allthings.util.ClickUtil;
import songqiu.allthings.util.DateUtil;
import songqiu.allthings.util.GlideCircleTransform;
import songqiu.allthings.util.SharedPreferencedUtils;
import songqiu.allthings.util.ShowNumUtil;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.util.theme.ThemeManager;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/6/24
 *
 *类描述：
 *
 ********/
public class ArticleDetailCommentAdapter extends RecyclerView.Adapter<ArticleDetailCommentAdapter.ViewHolder> implements ThemeManager.OnThemeChangeListener{

    Context context;
    List<DetailCommentListBean> item;
    VideoDetailCommentItemListener videoDetailCommentItemListener;
    List<ViewHolder> viewHolderList = new ArrayList<>();

    public ArticleDetailCommentAdapter(Context context, List<DetailCommentListBean> item) {
        this.context = context;
        this.item = item;
        ThemeManager.registerThemeChangeListener(this);
    }

    public void setAdapterDayModel(ThemeManager.ThemeMode themeMode) {
        ThemeManager.setThemeMode(themeMode);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment_list, null);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        viewHolderList.add(holder);
        DetailCommentListBean videoDetailCommentBean = item.get(position);
        RequestOptions options = new RequestOptions()
                .circleCrop().transforms(new GlideCircleTransform(context))
                .error(R.mipmap.head_default)
                .placeholder(R.mipmap.head_default);
        if(!StringUtil.isEmpty(item.get(position).avatar)) {
            if(!item.get(position).avatar.contains("http")) {
                item.get(position).avatar = HttpServicePath.BasePicUrl+item.get(position).avatar;
            }
        }
        Glide.with(context).load(videoDetailCommentBean.avatar).apply(options).into(holder.userIcon);
        holder.userName.setText(videoDetailCommentBean.user_nickname);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(videoDetailCommentBean.created*1000);
        String  time = simpleDateFormat.format(date);
        holder.timeTv.setText(DateUtil.getTimeFormatText(time, "yyyy-MM-dd HH:mm:ss"));
        holder.contentTv.setText(videoDetailCommentBean.content);
        holder.likeNumTv.setText(ShowNumUtil.showUnm(videoDetailCommentBean.up_num));
        int userId = SharedPreferencedUtils.getInteger(context,"SYSUSERID",0);
//        if(userId == videoDetailCommentBean.user_id) {
//            holder.reportImg.setImageResource(R.mipmap.item_cancel);
//        }else {
//            holder.reportImg.setImageResource(R.mipmap.item_inform);
//        }
        if(0 == item.get(position).is_up) {
            holder.likeImg.setImageResource(R.mipmap.item_like);
            holder.likeNumTv.setTextColor(context.getResources().getColor(R.color.FF666666));
        }else {
            holder.likeImg.setImageResource(R.mipmap.item_like_pre);
            holder.likeNumTv.setTextColor(context.getResources().getColor(R.color.FFDE5C51));
        }

        holder.likeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int is_up = item.get(position).is_up;
                if(0 == is_up) {
                    if (null != videoDetailCommentItemListener) {
                        videoDetailCommentItemListener.addLike(HttpServicePath.URL_LIKE,4,videoDetailCommentBean.commentid,videoDetailCommentBean);
                    }
                }else {
                    if (null != videoDetailCommentItemListener) {
                        videoDetailCommentItemListener.addLike(HttpServicePath.URL_NO_LIKE,4,videoDetailCommentBean.commentid,videoDetailCommentBean);
                    }
                }
            }
        });
//        holder.reportImg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(ClickUtil.onClick()) {
//                    videoDetailCommentItemListener.toReport(videoDetailCommentBean.user_id,videoDetailCommentBean.commentid,
//                            videoDetailCommentBean.article_id,1,position);
//                }
//            }
//        });
        //
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                videoDetailCommentItemListener.toReply(videoDetailCommentBean.commentid,videoDetailCommentBean.user_nickname);
            }
        });

        holder.layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//                videoDetailCommentItemListener.longClick(videoDetailCommentBean.user_id,videoDetailCommentBean.commentid,
//                            videoDetailCommentBean.article_id,1,position,-1);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return item.size();
    }

    @Override
    public void onThemeChanged() {
        if(null != viewHolderList && 0 != viewHolderList.size()) {
            for (ViewHolder viewHolder:viewHolderList) {
                viewHolder.timeTv.setTextColor(context.getResources().getColor(ThemeManager.getCurrentThemeRes(context, R.color.FF999999)));
                viewHolder.contentTv.setTextColor(context.getResources().getColor(ThemeManager.getCurrentThemeRes(context, R.color.bottom_tab_tv)));
            }
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.layout)
        RelativeLayout layout;
        @BindView(R.id.userIcon)
        ImageView userIcon;
        @BindView(R.id.userName)
        TextView userName;
        @BindView(R.id.timeTv)
        TextView timeTv;
        @BindView(R.id.contentTv)
        TextView contentTv;
        @BindView(R.id.likeNumTv)
        TextView likeNumTv;
        @BindView(R.id.likeImg)
        ImageView likeImg;
        @BindView(R.id.likeLayout)
        LinearLayout likeLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    public void setVideoDetailCommentItemListener(VideoDetailCommentItemListener videoDetailCommentItemListener) {
        this.videoDetailCommentItemListener = videoDetailCommentItemListener;
    }
}
