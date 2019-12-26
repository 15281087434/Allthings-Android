package songqiu.allthings.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
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
import com.heartfor.heartvideo.video.HeartVideo;
import com.heartfor.heartvideo.video.HeartVideoInfo;
import com.heartfor.heartvideo.video.HeartVideoManager;
import com.heartfor.heartvideo.video.VideoControl;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.droidsonroids.gif.GifImageView;
import songqiu.allthings.R;
import songqiu.allthings.activity.CommentWebViewActivity;
import songqiu.allthings.bean.LookVideoBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.iterface.LookItemListener;
import songqiu.allthings.mine.userpage.UserPagerActivity;
import songqiu.allthings.util.ClickUtil;
import songqiu.allthings.util.GlideCircleTransform;
import songqiu.allthings.util.ImageResUtils;
import songqiu.allthings.util.NetWorkUtil;
import songqiu.allthings.util.ShowNumUtil;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.util.ViewProportion;
import songqiu.allthings.videodetail.VideoDetailActivity;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/8/13
 *
 *类描述：
 *
 ********/
public class LookTabClassAdapter extends RecyclerView.Adapter {

    Context context;
    List<LookVideoBean> item;
    LookItemListener lookItemListener;

    //视频
    private final int TYPE_VIDEO = 1;
    //广告视频
    private final int TYPE_ADVERTISE_VIDEO = 2;
    //广告图片
    private final int TYPE_ADVERTISE_PIC = 3;


    public LookTabClassAdapter(Context context, List<LookVideoBean> item) {
        this.context = context;
        this.item = item;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if(viewType == TYPE_VIDEO) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_home_tab_video_video_view,viewGroup,false);
            return new VideoHolder(view);
        }else if(viewType == TYPE_ADVERTISE_PIC) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_big_pic_advertise, null);
            return new BigPicAdvertiseVideoHolder(view);
        }else if(viewType == TYPE_ADVERTISE_VIDEO) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_video_advertise, null);
            return new VideoAdvertiseVideoHolder(view);
        }
        return  null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof VideoHolder) {
            setTypeVideo((VideoHolder)viewHolder,position);
        }else if(viewHolder instanceof VideoAdvertiseVideoHolder) {
            setVideoAdvertise((VideoAdvertiseVideoHolder) viewHolder, position);
        }else if(viewHolder instanceof BigPicAdvertiseVideoHolder) {
            setBigPicAdvertise((BigPicAdvertiseVideoHolder) viewHolder, position);
        }
    }


    public void setTypeVideo(VideoHolder viewHolder,int position) {
        viewHolder.videoView.post(new Runnable() {
            @Override
            public void run() {
                viewHolder.videoView.setLayoutParams(ViewProportion.getLinearParams(viewHolder.videoView, 1.77));
            }
        });
        RequestOptions options = new RequestOptions()
                .circleCrop().transforms(new GlideCircleTransform(context))
                .error(R.mipmap.head_default)
                .placeholder(R.mipmap.head_default);
        if(!StringUtil.isEmpty(item.get(position).avatar)) {
            if(!item.get(position).avatar.contains("http")) {
                item.get(position).avatar = HttpServicePath.BasePicUrl+item.get(position).avatar;
            }
        }
        Glide.with(context).load(item.get(position).avatar).apply(options).into(viewHolder.userIcon);
        viewHolder.userName.setText(item.get(position).user_nickname);
        viewHolder.commentTv.setText(ShowNumUtil.showUnm(item.get(position).comment_num));
        if(!StringUtil.isEmpty(item.get(position).photo)) {
            if(!item.get(position).photo.contains("http")) {
                item.get(position).photo = HttpServicePath.BasePicUrl+item.get(position).photo;
            }
        }
        viewHolder.ivLevel.setImageResource(ImageResUtils.getLevelRes(item.get(position).level));
        String path = item.get(position).video_url;
        String image= item.get(position).photo;
        String title = item.get(position).title;
        int id = item.get(position).articleid;
        if(!StringUtil.isEmpty(path)) {
            HeartVideoInfo info= HeartVideoInfo.Builder().setTitle(title).setPath(path).setImagePath(image).setSaveProgress(false).setVideoId(id).builder();
            VideoControl control=new VideoControl(context);
            control.setInfo(info);
            viewHolder.videoView.setHeartVideoContent(control);
        }
        viewHolder.likeTv.setText(ShowNumUtil.showUnm(item.get(position).up_num));
        if(0 == item.get(position).is_up) {
            viewHolder.likeImg.setImageResource(R.mipmap.item_like);
            viewHolder.likeTv.setTextColor(context.getResources().getColor(R.color.FF666666));
        }else {
            viewHolder.likeImg.setImageResource(R.mipmap.item_like_pre);
            viewHolder.likeTv.setTextColor(context.getResources().getColor(R.color.FFDE5C51));
        }
        if(0 == item.get(position).is_follow) {
            viewHolder.attentionTv.setText("关注");
            viewHolder.attentionTv.setTextColor(context.getResources().getColor(R.color.white));
            viewHolder.attentionTv.setBackgroundResource(R.drawable.rectangle_common_attention);
        }else {
            viewHolder.attentionTv.setText("已关注");
            viewHolder.attentionTv.setTextColor(context.getResources().getColor(R.color.white));
            viewHolder.attentionTv.setBackgroundResource(R.drawable.rectangle_common_no_attention);
        }
        viewHolder.toDetailLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ClickUtil.onClick()) {
                    Intent intent = new Intent(context, VideoDetailActivity.class);
                    intent.putExtra("articleid",item.get(position).articleid);
                    context.startActivity(intent);
                }
            }
        });

        //点赞
        viewHolder.likeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ClickUtil.onClick()) {
                    int is_up = item.get(position).is_up;
                    if(0 == is_up) {
                        if (null != lookItemListener) {
                            lookItemListener.addLike(HttpServicePath.URL_LIKE,2,item.get(position).articleid,item.get(position),viewHolder);
                        }
                    }else {
                        if (null != lookItemListener) {
                            lookItemListener.addLike(HttpServicePath.URL_NO_LIKE,2,item.get(position).articleid,item.get(position),viewHolder);
                        }
                    }
                }
            }
        });
        //点点点
        viewHolder.settingImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ClickUtil.onClick()) {
                    lookItemListener.addSetting(position);
                }
            }
        });
        //关注
        viewHolder.attentionTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ClickUtil.onClick()) {
                    int is_follow = item.get(position).is_follow;
                    if(0 == is_follow) {
                        if (null != lookItemListener) {//用户id 	type、1=添加关注，2=取消关注
                            lookItemListener.addFollow(item.get(position).userid,1,item);
                        }
                    }else {
                        if (null != lookItemListener) {
                            lookItemListener.addFollow(item.get(position).userid,2,item);
                        }
                    }
                }
            }
        });

        //去个人中心
        viewHolder.userLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ClickUtil.onClick()) {
                    Intent intent = new Intent(context, UserPagerActivity.class);
                    intent.putExtra("userId",item.get(position).userid);
                    context.startActivity(intent);
                }
            }
        });
    }


    public void setVideoAdvertise(VideoAdvertiseVideoHolder holder, int position) {
        holder.videoView.post(new Runnable() {
            @Override
            public void run() {
                holder.videoView.setLayoutParams(ViewProportion.getLinearParams(holder.videoView, 1.7));
            }
        });
        //头像
        RequestOptions options = new RequestOptions()
                .circleCrop().transforms(new GlideCircleTransform(context))
                .error(R.mipmap.head_default)
                .placeholder(R.mipmap.head_default);
        if (!StringUtil.isEmpty(item.get(position).head_url)) {
            if (!item.get(position).head_url.contains("http")) {
                item.get(position).head_url = HttpServicePath.BasePicUrl + item.get(position).head_url;
            }
        }

        Glide.with(context).load(item.get(position).head_url).apply(options).into(holder.userIcon);
        //
        holder.userName.setText(item.get(position).ad_name);
        //视频
        String path = item.get(position).video_url;//
        if (!StringUtil.isEmpty(item.get(position).url)) {
            if (!item.get(position).url.contains("http")) {
                item.get(position).url = HttpServicePath.BasePicUrl + item.get(position).url;
            }
        }
        String image = item.get(position).url;
        String title = item.get(position).title;
        if(!StringUtil.isEmpty(path)) {
            HeartVideoInfo info = HeartVideoInfo.Builder().setTitle(title).setPath(path).setImagePath(image).setSaveProgress(false).builder();
            VideoControl control = new VideoControl(context);
            control.setInfo(info);
            holder.videoView.setHeartVideoContent(control);
            if(item.get(position).state==1&& NetWorkUtil.getConnectedType(context)== ConnectivityManager.TYPE_WIFI){
                holder.videoView.startSlence();
            }
        }

        if (4 == item.get(position).change_type) {
            holder.downloadLayout.setVisibility(View.VISIBLE);
        } else {
            holder.downloadLayout.setVisibility(View.GONE);
        }
        holder.toDetailLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClickUtil.onClick()) {
                    Intent intent = new Intent(context, CommentWebViewActivity.class);
                    intent.putExtra("url", item.get(position).jump_url);
                    context.startActivity(intent);
                }
            }
        });
        holder.settingImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ClickUtil.onClick()) {
                    lookItemListener.delete(position);
                }
            }
        });
    }

    public void setBigPicAdvertise(BigPicAdvertiseVideoHolder holder, int position) {
        holder.bigPicImg.post(new Runnable() {
            @Override
            public void run() {
                holder.bigPicImg.setLayoutParams(ViewProportion.getRelativeParams(holder.bigPicImg, 1.7));
            }
        });
        //头像
        RequestOptions options = new RequestOptions()
                .circleCrop().transforms(new GlideCircleTransform(context))
                .error(R.mipmap.head_default)
                .placeholder(R.mipmap.head_default);
        if (!StringUtil.isEmpty(item.get(position).head_url)) {
            if (!item.get(position).head_url.contains("http")) {
                item.get(position).head_url = HttpServicePath.BasePicUrl + item.get(position).head_url;
            }
        }
        Glide.with(context).load(item.get(position).head_url).apply(options).into(holder.userIcon);
        //
        holder.userName.setText(item.get(position).ad_name);
        //图片
        RequestOptions options1 = new RequestOptions()
                .error(R.mipmap.pic_default)
                .placeholder(R.mipmap.pic_default);
        if (!StringUtil.isEmpty(item.get(position).url.replaceAll("\"",""))) {
            if (!item.get(position).url.contains("http")) {
                item.get(position).url = HttpServicePath.BasePicUrl + item.get(position).url;
            }
        }
        Glide.with(context).load(item.get(position).url).apply(options1).into(holder.bigPicImg);

        if (1 == item.get(position).change_type) {
            holder.downloadLayout.setVisibility(View.VISIBLE);
        } else {
            holder.downloadLayout.setVisibility(View.GONE);
        }
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClickUtil.onClick()) {
                    Intent intent = new Intent(context, CommentWebViewActivity.class);
                    intent.putExtra("url", item.get(position).jump_url);
                    context.startActivity(intent);
                }
            }
        });
        holder.settingImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ClickUtil.onClick()) {
                    lookItemListener.delete(position);
                }
            }
        });
    }


    @Override
    public int getItemViewType(int position) {
       if(1==item.get(position).ad) {
           if(1==item.get(position).type) {
               return TYPE_ADVERTISE_PIC;
           }else {
               return TYPE_ADVERTISE_VIDEO;
           }
       }else {
           return TYPE_VIDEO;
       }
    }


    @Override
    public int getItemCount() {
        return item.size();
    }



    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if(holder instanceof VideoHolder) {
            HeartVideo heartVideo = ((VideoHolder) holder).videoView;
            if (heartVideo == HeartVideoManager.getInstance().getCurrPlayVideo()) {
                HeartVideoManager.getInstance().release();
            }
        }
        if(holder instanceof VideoAdvertiseVideoHolder) {
            HeartVideo heartVideo = ((VideoAdvertiseVideoHolder) holder).videoView;
            if (heartVideo == HeartVideoManager.getInstance().getCurrPlayVideo()) {
                HeartVideoManager.getInstance().release();
            }
        }
    }

    public class VideoHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.videoView)
        HeartVideo videoView;
        @BindView(R.id.userIcon)
        ImageView userIcon;
        @BindView(R.id.userName)
        TextView userName;
        @BindView(R.id.commentTv)
        TextView commentTv;
        @BindView(R.id.toDetailLayout)
        RelativeLayout toDetailLayout;
        @BindView(R.id.likeTv)
        public TextView likeTv;
        @BindView(R.id.attentionTv)
        TextView attentionTv;
        @BindView(R.id.likeLayout)
        LinearLayout likeLayout;
        @BindView(R.id.likeImg)
        public ImageView likeImg;
        @BindView(R.id.settingImg)
        ImageView settingImg;
        @BindView(R.id.userLayout)
        LinearLayout userLayout;
        @BindView(R.id.iv_level)
        ImageView ivLevel;

        public VideoHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class VideoAdvertiseVideoHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.videoView)
        HeartVideo videoView;
        @BindView(R.id.userIcon)
        ImageView userIcon;
        @BindView(R.id.userName)
        TextView userName;
        @BindView(R.id.downloadLayout)
        LinearLayout downloadLayout;
        @BindView(R.id.settingImg)
        ImageView settingImg;
        @BindView(R.id.toDetailLayout)
        RelativeLayout toDetailLayout;

        public VideoAdvertiseVideoHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    public class BigPicAdvertiseVideoHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.bigPicImg)
        GifImageView bigPicImg;
        @BindView(R.id.titleTv)
        TextView titleTv;
        @BindView(R.id.userIcon)
        ImageView userIcon;
        @BindView(R.id.userName)
        TextView userName;
        @BindView(R.id.downloadLayout)
        LinearLayout downloadLayout;
        @BindView(R.id.settingImg)
        ImageView settingImg;
        @BindView(R.id.toDetailLayout)
        RelativeLayout toDetailLayout;
        @BindView(R.id.layout)
        LinearLayout layout;

        public BigPicAdvertiseVideoHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void setLomeItemListener(LookItemListener lookItemListener) {
        this.lookItemListener = lookItemListener;
    }
}
