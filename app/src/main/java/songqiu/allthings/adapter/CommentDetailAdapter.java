package songqiu.allthings.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
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
import songqiu.allthings.adapter.Comment.CommentSubitemHolder;
import songqiu.allthings.bean.CommentDetailCon2Bean;
import songqiu.allthings.bean.CommentSubitemBean;
import songqiu.allthings.bean.DetailCommentListBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.iterface.VideoDetailCommentItemListener;
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
public class CommentDetailAdapter extends RecyclerView.Adapter<CommentDetailAdapter.ViewHolder> {

    Context context;
    List<CommentDetailCon2Bean> item;
    CommentDetailListener commentDetailListener;

    public CommentDetailAdapter(Context context, List<CommentDetailCon2Bean> item) {
        this.context = context;
        this.item = item;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_comment_detail, null);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CommentDetailCon2Bean bean = item.get(position);
        RequestOptions options = new RequestOptions()
                .circleCrop().transforms(new GlideCircleTransform(context))
                .error(R.mipmap.head_default)
                .placeholder(R.mipmap.head_default);
        if(!StringUtil.isEmpty(bean.avatar)) {
            if(!bean.avatar.contains("http")) {
                bean.avatar = HttpServicePath.BasePicUrl+bean.avatar;
            }
        }
        Glide.with(context).load(bean.avatar).apply(options).into((holder).userIcon);
        holder.userName.setText(bean.user_nickname);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(bean.created*1000);
        String  time = simpleDateFormat.format(date);
        holder.timeTv.setText(DateUtil.getTimeFormatText(time, "yyyy-MM-dd HH:mm:ss"));
        if(!StringUtil.isEmpty(bean.nickname)) {
            String content = "回复 "+bean.nickname+" : "+bean.content;
            //变色
            int startIndex = content.indexOf("回复 ");
            int endIndex = content.indexOf(": ");
            SpannableString spannableString = new SpannableString(content);
            spannableString.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.FF999999)), startIndex+3, endIndex, 0);
            spannableString.setSpan(new RelativeSizeSpan(0.95f), startIndex+3, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.contentTv.setText(spannableString);
        }else {
            holder.contentTv.setText(bean.content);
        }
        holder.likeNumTv.setText(ShowNumUtil.showUnm(bean.up_num));
        if(0 == bean.is_up) {
            holder.likeImg.setImageResource(R.mipmap.item_like);
            holder.likeNumTv.setTextColor(context.getResources().getColor(R.color.FF666666));
        }else {
            holder.likeImg.setImageResource(R.mipmap.item_like_pre);
            holder.likeNumTv.setTextColor(context.getResources().getColor(R.color.FFDE5C51));
        }

        holder.likeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int is_up = bean.is_up;
                if(0 == is_up) {
                    if (null != commentDetailListener) {
                        commentDetailListener.addSubitemLike(HttpServicePath.URL_LIKE,4,bean.commentid,bean);
                    }
                }else {
                    if (null != commentDetailListener) {
                        commentDetailListener.addSubitemLike(HttpServicePath.URL_NO_LIKE,4,bean.commentid,bean);
                    }
                }
            }
        });

        //短按回复评论
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(StringUtil.isEmpty(bean.nickname)) {
                    commentDetailListener.toReply(bean.type,bean.grade+1<=2?bean.grade+1:2,bean.commentid,bean.user_nickname);
                }
            }
        });

        //长按
        holder.layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(StringUtil.isEmpty(bean.nickname)) { //二级评论  回复回复
                    commentDetailListener.longClick(bean.user_id,bean.commentid,
                            bean.article_id,bean.type,position,false,bean.content);
                }else { //三级 回复回复的回复
                    commentDetailListener.longClick(bean.user_id,bean.commentid,
                            bean.article_id,bean.type,position,true,bean.content);
                }
                return true;
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
        @BindView(R.id.likeImg)
        ImageView likeImg;
        @BindView(R.id.likeNumTv)
        TextView likeNumTv;
        @BindView(R.id.likeLayout)
        LinearLayout likeLayout;
        @BindView(R.id.layout)
        RelativeLayout layout;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void setCommentDetailListener(CommentDetailListener commentDetailListener) {
        this.commentDetailListener = commentDetailListener;
    }

    public interface CommentDetailListener {
        void addSubitemLike(String url,int type, int subMid, CommentDetailCon2Bean commentDetailCon2Bean); //一级评论点赞  类型:1=文章，2=视频，3=话题，4=评论  表示id
        void toReply(int type,int grade,int pid,String name); //回复评论
        void longClick(int userId,int commentId,int articleid,int type,int position,boolean isSub,String content); //长按  content:回复内容 复制用
    }

}
