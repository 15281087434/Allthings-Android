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
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import songqiu.allthings.R;
import songqiu.allthings.bean.VideoDetailCommentBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.iterface.VideoDetailCommentItemListener;
import songqiu.allthings.util.ClickUtil;
import songqiu.allthings.util.DateUtil;
import songqiu.allthings.util.GlideCircleTransform;
import songqiu.allthings.util.SharedPreferencedUtils;
import songqiu.allthings.util.ShowNumUtil;
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
public class ArticleDetailCommentAdapter extends RecyclerView.Adapter<ArticleDetailCommentAdapter.ViewHolder> {

    //    OnItemClickListener mListener;
    Context context;
    List<VideoDetailCommentBean> item;
    VideoDetailCommentItemListener videoDetailCommentItemListener;

    public ArticleDetailCommentAdapter(Context context, List<VideoDetailCommentBean> item) {
        this.context = context;
        this.item = item;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_detail_comment, null);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        ((MessageViewholder)holder).isReadImg.setImageResource(R.mipmap.home_news);
        VideoDetailCommentBean videoDetailCommentBean = item.get(position);
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
        if(userId == videoDetailCommentBean.user_id) {
            holder.reportImg.setImageResource(R.mipmap.item_cancel);
        }else {
            holder.reportImg.setImageResource(R.mipmap.item_inform);
        }
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
        holder.reportImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ClickUtil.onClick()) {
                    videoDetailCommentItemListener.toReport(videoDetailCommentBean.user_id,videoDetailCommentBean.commentid,
                            videoDetailCommentBean.article_id,1,position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return item.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
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
        @BindView(R.id.reportImg)
        ImageView reportImg;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    public void setVideoDetailCommentItemListener(VideoDetailCommentItemListener videoDetailCommentItemListener) {
        this.videoDetailCommentItemListener = videoDetailCommentItemListener;
    }
}
