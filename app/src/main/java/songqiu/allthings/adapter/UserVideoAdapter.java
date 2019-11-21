package songqiu.allthings.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.heartfor.heartvideo.video.HeartVideo;
import com.heartfor.heartvideo.video.HeartVideoInfo;
import com.heartfor.heartvideo.video.HeartVideoManager;
import com.heartfor.heartvideo.video.VideoControl;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import songqiu.allthings.R;
import songqiu.allthings.bean.UserPagerBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.iterface.UserPagerListenner;
import songqiu.allthings.util.ClickUtil;
import songqiu.allthings.util.SharedPreferencedUtils;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.util.ViewProportion;
import songqiu.allthings.videodetail.VideoDetailActivity;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/9/17
 *
 *类描述：
 *
 ********/
public class UserVideoAdapter extends RecyclerView.Adapter {

    Context context;
    List<UserPagerBean> item;
    UserPagerListenner userPagerListenner;

    public UserVideoAdapter(Context context, List<UserPagerBean> item) {
        this.context = context;
        this.item = item;
    }

    @Override
    public int getItemCount() {
        return item.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_user_video, null);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new VideoViewholder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        setVideoData((VideoViewholder) holder, position);
    }


    public void setVideoData(VideoViewholder holder, int position) {
        holder.videoView.post(new Runnable() {
            @Override
            public void run() {
                holder.videoView.setLayoutParams(ViewProportion.getLinearParams(holder.videoView, 1.77));
            }
        });

        String path = item.get(position).video_url;
        String image= item.get(position).photo;
        String title = item.get(position).title;
        if(!StringUtil.isEmpty(path)) {
            HeartVideoInfo info= HeartVideoInfo.Builder().setTitle(title).setPath(path).setImagePath(image).setSaveProgress(false).builder();
            VideoControl control=new VideoControl(context);
            control.setInfo(info);
            holder.videoView.setHeartVideoContent(control);
        }
        holder.likeTv.setText(String.valueOf(item.get(position).up_num));
        holder.commentTv.setText(String.valueOf(item.get(position).comment_num));
        if (0 == item.get(position).is_up) {
            holder.likeImg.setImageResource(R.mipmap.item_like);
            holder.likeTv.setTextColor(context.getResources().getColor(R.color.FF666666));
        } else {
            holder.likeImg.setImageResource(R.mipmap.item_like_pre);
            holder.likeTv.setTextColor(context.getResources().getColor(R.color.FFDE5C51));
        }

        int mUserId = SharedPreferencedUtils.getInteger(context, "SYSUSERID", 0);
        if(item.get(position).userid == mUserId) {
            holder.shareImg.setImageDrawable(context.getResources().getDrawable(R.mipmap.icon_cancel));
        }else {
            holder.shareImg.setImageDrawable(context.getResources().getDrawable(R.mipmap.home_ellipsis));
        }

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ClickUtil.onClick()) {
                    Intent intent = new Intent(context, VideoDetailActivity.class);
                    intent.putExtra("articleid",item.get(position).articleid);
                    context.startActivity(intent);
                }
            }
        });

        holder.likeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int is_up = item.get(position).is_up;
                if(0 == is_up) {
                    if (null != userPagerListenner) {
                        userPagerListenner.addLike(HttpServicePath.URL_LIKE,2,item.get(position).articleid,item.get(position));
                    }
                }else {
                    if (null != userPagerListenner) {
                        userPagerListenner.addLike(HttpServicePath.URL_NO_LIKE,2,item.get(position).articleid,item.get(position));
                    }
                }
            }
        });

        holder.shareLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(item.get(position).userid == mUserId) {
                    if (null != userPagerListenner) {
                        userPagerListenner.delete();
                    }
                }else {
                    if (null != userPagerListenner) {
                        userPagerListenner.share(1,position,2);
                    }
                }
            }
        });
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        HeartVideo heartVideo = ((VideoViewholder) holder).videoView;
        if (heartVideo == HeartVideoManager.getInstance().getCurrPlayVideo()) {
            HeartVideoManager.getInstance().release();
        }
    }

    public class VideoViewholder extends RecyclerView.ViewHolder {
        @BindView(R.id.videoView)
        HeartVideo videoView;
        @BindView(R.id.likeImg)
        ImageView likeImg;
        @BindView(R.id.likeTv)
        TextView likeTv;
        @BindView(R.id.likeLayout)
        LinearLayout likeLayout;
        @BindView(R.id.commentTv)
        TextView commentTv;
        @BindView(R.id.shareLayout)
        LinearLayout shareLayout;
        @BindView(R.id.layout)
        LinearLayout layout;
        @BindView(R.id.shareImg)
        ImageView shareImg;

        public VideoViewholder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void setUserPagerListenner(UserPagerListenner userPagerListenner) {
        this.userPagerListenner = userPagerListenner;
    }
}
