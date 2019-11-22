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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.heartfor.heartvideo.video.HeartVideo;
import com.heartfor.heartvideo.video.HeartVideoInfo;
import com.heartfor.heartvideo.video.HeartVideoManager;
import com.heartfor.heartvideo.video.VideoControl;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import songqiu.allthings.R;
import songqiu.allthings.articledetail.ArticleDetailActivity;
import songqiu.allthings.bean.HomeSubitemBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.iterface.HomeItemListener;
import songqiu.allthings.util.ClickUtil;
import songqiu.allthings.util.DateUtil;
import songqiu.allthings.util.GlideCircleTransform;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.util.ViewProportion;
import songqiu.allthings.videodetail.VideoDetailActivity;
import songqiu.allthings.view.GridViewInScroll;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/8/13
 *
 *类描述：
 *
 ********/
public class HomeTabCityAdapter extends RecyclerView.Adapter {

    Context context;
    List<HomeSubitemBean> item;
    Map<String, Object> classMap;
    //type 1  判断moudle  type 2 视频
    //设置常量  moudle 1、纯文字  2、大图 3、小图
    private final int TYPE_ONLY_TEXT = 1;
    private final int TYPE_BIG_PIC = 2;
    private final int TYPE_SMALL_PIC = 3;
    private final int TYPE_MORE_PIC = 6;

    private final int TYPE_CITY_AND_CHOICE_VIDEO = 4; //成都、精选里的视频布局标记
    private final int TYPE_VIDEO_VIDEO = 5; //calss为视频时的视频布局标记

    HomeItemListener homeItemListener;

    public HomeTabCityAdapter(Context context, List<HomeSubitemBean> item) {
        this.context = context;
        this.item = item;
    }

    public void setLikeNum(TextView tv, int like) {
        tv.setText(String.valueOf(like));
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == TYPE_ONLY_TEXT) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_home_tab_recommend_no_pic, null);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(lp);
            return new NoPicViewholder(view);
        } else if (viewType == TYPE_BIG_PIC) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_home_tab_recommend_big_pic, null);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(lp);
            return new BigPicViewholder(view);
        } else if (viewType == TYPE_SMALL_PIC) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_home_tab_recommend_right_pic, null);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(lp);
            return new RightPicViewholder(view);
        } else if (viewType == TYPE_MORE_PIC) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_home_more_pic, null);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(lp);
            return new MorePicViewholder(view);
        } else if (viewType == TYPE_VIDEO_VIDEO) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_home_tab_video_video_view, null);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(lp);
            return new LookVideoHolder(view);
        } else if (viewType == TYPE_CITY_AND_CHOICE_VIDEO) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_home_tab_recommend_video_view, null);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(lp);
            return new CityAndChoiseVideoHolder(view);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        if (item.get(position).type == 2) { //视频
            return TYPE_CITY_AND_CHOICE_VIDEO;
        } else { //非视频
            if (item.get(position).module == 1) {
                return TYPE_ONLY_TEXT;
            } else if (item.get(position).module == 2) {
                return TYPE_BIG_PIC;
            } else if (item.get(position).module == 3) {
                return TYPE_SMALL_PIC;
            } else if (item.get(position).module == 4) {
                return TYPE_MORE_PIC;
            }
        }
        return TYPE_ONLY_TEXT;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof NoPicViewholder) {
            setNoPicData((NoPicViewholder) holder, position);
        } else if (holder instanceof BigPicViewholder) {
            setBigPicData((BigPicViewholder) holder, position);
        } else if (holder instanceof RightPicViewholder) {
            setRightPicData((RightPicViewholder) holder, position);
        } else if (holder instanceof MorePicViewholder) {
            setMorePicData((MorePicViewholder) holder, position);
        } else if (holder instanceof CityAndChoiseVideoHolder) {
            setCityAndChoiseData((CityAndChoiseVideoHolder) holder, position);
        } else if (holder instanceof LookVideoHolder) {
            setLookVideoData((LookVideoHolder) holder, position);
        }

    }

    public void setNoPicData(NoPicViewholder holder, int position) {
        RequestOptions options = new RequestOptions()
                .circleCrop().transforms(new GlideCircleTransform(context))
                .error(R.mipmap.head_default)
                .placeholder(R.mipmap.head_default);
        if (!StringUtil.isEmpty(item.get(position).avatar)) {
            if (!item.get(position).avatar.contains("http")) {
                item.get(position).avatar = HttpServicePath.BasePicUrl + item.get(position).avatar;
            }
        }
        Glide.with(context).load(item.get(position).avatar).apply(options).into(holder.userIcon);
        holder.titleTv.setText(item.get(position).title);
        holder.lookTv.setText(String.valueOf(item.get(position).view_num) + "次");
        holder.contentTv.setText(item.get(position).descriptions);
        holder.userName.setText(item.get(position).user_nickname);
        //判断时间 昨天  今天
        long time = item.get(position).created * 1000;
        if (DateUtil.IsToday(time)) {
            holder.timeTv.setText("刚刚");
        } else if (DateUtil.IsYesterday(time)) {
            holder.timeTv.setText("1天前");
        } else {
            holder.timeTv.setText(DateUtil.getTimeBig1(time));
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
        holder.deleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeItemListener.delete(position,1);
            }
        });
    }


    public void setBigPicData(BigPicViewholder holder, int position) {
        RequestOptions options = new RequestOptions()
                .circleCrop().transforms(new GlideCircleTransform(context))
                .error(R.mipmap.head_default)
                .placeholder(R.mipmap.head_default);
        RequestOptions options1 = new RequestOptions()
                .error(R.mipmap.pic_default)
                .placeholder(R.mipmap.pic_default);
        //
        holder.bigPicImg.post(new Runnable() {
            @Override
            public void run() {
                holder.bigPicImg.setLayoutParams(ViewProportion.getLinearParams(holder.bigPicImg, 2));
            }
        });
        if (!StringUtil.isEmpty(item.get(position).avatar)) {
            if (!item.get(position).avatar.contains("http")) {
                item.get(position).avatar = HttpServicePath.BasePicUrl + item.get(position).avatar;
            }
        }

        if (!StringUtil.isEmpty(item.get(position).photo)) {
            if (!item.get(position).photo.contains("http")) {
                item.get(position).photo = HttpServicePath.BasePicUrl + item.get(position).photo;
            }
        }
        Glide.with(context).load(item.get(position).avatar).apply(options).into(holder.userIcon);
        Glide.with(context).load(item.get(position).photo).apply(options1).into(holder.bigPicImg);
        holder.titleTv.setText(item.get(position).title);
        holder.lookTv.setText(String.valueOf(item.get(position).view_num) + "次");
        holder.contentTv.setText(item.get(position).descriptions);
        holder.userName.setText(item.get(position).user_nickname);
        //判断时间 昨天  今天
        long time = item.get(position).created * 1000;
        if (DateUtil.IsToday(time)) {
            holder.timeTv.setText("刚刚");
        } else if (DateUtil.IsYesterday(time)) {
            holder.timeTv.setText("1天前");
        } else {
            holder.timeTv.setText(DateUtil.getTimeBig1(time));
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
        holder.deleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeItemListener.delete(position,1);
            }
        });
    }

    public void setRightPicData(RightPicViewholder holder, int position) {
        RequestOptions options = new RequestOptions()
                .circleCrop().transforms(new GlideCircleTransform(context))
                .error(R.mipmap.head_default)
                .placeholder(R.mipmap.head_default);
        if (!StringUtil.isEmpty(item.get(position).avatar)) {
            if (!item.get(position).avatar.contains("http")) {
                item.get(position).avatar = HttpServicePath.BasePicUrl + item.get(position).avatar;
            }
        }
        Glide.with(context).load(item.get(position).avatar).apply(options).into(holder.userIcon);
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
        holder.deleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeItemListener.delete(position,1);
            }
        });
    }

    public void setMorePicData(MorePicViewholder holder, int position) {
        holder.titleTv.setText(item.get(position).title);
        holder.userName.setText(item.get(position).user_nickname);

        if(null != item.get(position).photos) {
            ImageTextMorePicAdapter gambitMorePicAdapter = new ImageTextMorePicAdapter(context,item.get(position).photos);
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
        holder.deleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeItemListener.delete(position,1);
            }
        });
        if (1 < item.get(position).ranklist) {
            holder.setTopTv.setVisibility(View.VISIBLE);
        }

    }

    public void setCityAndChoiseData(CityAndChoiseVideoHolder holder, int position) {
        holder.videoView.post(new Runnable() {
            @Override
            public void run() {
                holder.videoView.setLayoutParams(ViewProportion.getLinearParams(holder.videoView, 1.77));
            }
        });
        RequestOptions options = new RequestOptions()
                .circleCrop().transforms(new GlideCircleTransform(context))
                .error(R.mipmap.head_default)
                .placeholder(R.mipmap.head_default);
        if (!StringUtil.isEmpty(item.get(position).avatar)) {
            if (!item.get(position).avatar.contains("http")) {
                item.get(position).avatar = HttpServicePath.BasePicUrl + item.get(position).avatar;
            }
        }
        Glide.with(context).load(item.get(position).avatar).apply(options).into(holder.userIcon);
        holder.titleTv.setText(item.get(position).title);
        holder.lookTv.setText(String.valueOf(item.get(position).view_num) + "次");
        holder.userName.setText(item.get(position).user_nickname);
        //判断时间 昨天  今天
        long time = item.get(position).created * 1000;
        if (DateUtil.IsToday(time)) {
            holder.timeTv.setText("刚刚");
        } else if (DateUtil.IsYesterday(time)) {
            holder.timeTv.setText("1天前");
        } else {
            holder.timeTv.setText(DateUtil.getTimeBig1(time));
        }
        if (!StringUtil.isEmpty(item.get(position).photo)) {
            if (!item.get(position).photo.contains("http")) {
                item.get(position).photo = HttpServicePath.BasePicUrl + item.get(position).photo;
            }
        }
        if(!StringUtil.isEmpty(item.get(position).source_url)) {
            HeartVideoInfo info = HeartVideoInfo.Builder().setTitle("").setPath(item.get(position).source_url).setImagePath(item.get(position).photo).setSaveProgress(false)
                    .setVideoId(item.get(position).articleid).builder();
            VideoControl control = new VideoControl(context);
            control.setInfo(info);
            holder.videoView.setHeartVideoContent(control);
        }

        holder.toDetailLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClickUtil.onClick()) {
                    int articleid = item.get(position).articleid;
                    Intent intent = new Intent(context, VideoDetailActivity.class);
                    intent.putExtra("articleid", articleid);
                    context.startActivity(intent);
                }
            }
        });
        holder.deleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeItemListener.delete(position,1);
            }
        });

    }

    public void setLookVideoData(LookVideoHolder holder, int position) {
        holder.videoView.post(new Runnable() {
            @Override
            public void run() {
                holder.videoView.setLayoutParams(ViewProportion.getLinearParams(holder.videoView, 1.77));
            }
        });
        RequestOptions options = new RequestOptions()
                .circleCrop().transforms(new GlideCircleTransform(context))
                .error(R.mipmap.head_default)
                .placeholder(R.mipmap.head_default);
        if (!StringUtil.isEmpty(item.get(position).avatar)) {
            if (!item.get(position).avatar.contains("http")) {
                item.get(position).avatar = HttpServicePath.BasePicUrl + item.get(position).avatar;
            }
        }
        Glide.with(context).load(item.get(position).avatar).apply(options).into(holder.userIcon);
        if (!StringUtil.isEmpty(item.get(position).photo)) {
            if (!item.get(position).photo.contains("http")) {
                item.get(position).photo = HttpServicePath.BasePicUrl + item.get(position).photo;
            }
        }
        holder.commentTv.setText(String.valueOf(item.get(position).comment_num));
        if(!StringUtil.isEmpty(item.get(position).video_url)) {
            HeartVideoInfo info = HeartVideoInfo.Builder().setTitle(item.get(position).title).setPath(item.get(position).video_url).setImagePath(item.get(position).photo).setSaveProgress(false)
                    .setVideoId(item.get(position).articleid).builder();
            VideoControl control = new VideoControl(context);
            control.setInfo(info);
            holder.videoView.setHeartVideoContent(control);
        }
        holder.userName.setText(item.get(position).user_nickname);
        holder.likeTv.setText(item.get(position).up_num + "");
        if (0 == item.get(position).is_up) {
            holder.likeImg.setImageResource(R.mipmap.item_like);
            holder.likeTv.setTextColor(context.getResources().getColor(R.color.FF666666));
        } else {
            holder.likeImg.setImageResource(R.mipmap.item_like_pre);
            holder.likeTv.setTextColor(context.getResources().getColor(R.color.FFDE5C51));
        }
        if (0 == item.get(position).is_follow) {
            holder.attentionTv.setText("关注");
            holder.attentionTv.setBackgroundResource(R.drawable.rectangle_common_attention);
        } else {
            holder.attentionTv.setText("已关注");
            holder.attentionTv.setBackgroundResource(R.drawable.rectangle_common_no_attention);
        }
        holder.toDetailLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClickUtil.onClick()) {
                    int articleid = item.get(position).articleid;
                    Intent intent = new Intent(context, VideoDetailActivity.class);
                    intent.putExtra("articleid", articleid);
                    context.startActivity(intent);
                }
            }
        });
        //点赞
        holder.likeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int is_up = item.get(position).is_up;
                if (0 == is_up) {
                    if (null != homeItemListener) {
                        homeItemListener.addLike(HttpServicePath.URL_LIKE, 2, item.get(position).articleid, item.get(position),holder);
                    }
                } else {
                    if (null != homeItemListener) {
                        homeItemListener.addLike(HttpServicePath.URL_NO_LIKE, 2, item.get(position).articleid, item.get(position),holder);
                    }
                }
            }
        });
        //点点点
        holder.settingImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeItemListener.addSetting(position);
            }
        });
        //关注
        holder.attentionTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClickUtil.onClick()) {
                    int is_follow = item.get(position).is_follow;
                    if (0 == is_follow) {
                        if (null != homeItemListener) {//用户id 	type、1=添加关注，2=取消关注
                            homeItemListener.addFollow(item.get(position).userid, 1, item);
                        }
                    } else {
                        if (null != homeItemListener) {
                            homeItemListener.addFollow(item.get(position).userid, 2, item);
                        }
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return item.size();
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if(holder instanceof CityAndChoiseVideoHolder) {
            HeartVideo heartVideo = ((CityAndChoiseVideoHolder) holder).videoView;
            if (heartVideo == HeartVideoManager.getInstance().getCurrPlayVideo()) {
                HeartVideoManager.getInstance().release();
            }
        }

        if(holder instanceof LookVideoHolder) {
            HeartVideo heartVideo = ((LookVideoHolder) holder).videoView;
            if (heartVideo == HeartVideoManager.getInstance().getCurrPlayVideo()) {
                HeartVideoManager.getInstance().release();
            }
        }
    }

    public class RightPicViewholder extends RecyclerView.ViewHolder {
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
        @BindView(R.id.deleteImg)
        ImageView deleteImg;

        public RightPicViewholder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class MorePicViewholder extends RecyclerView.ViewHolder {
        @BindView(R.id.titleTv)
        TextView titleTv;
        @BindView(R.id.gridView)
        GridViewInScroll gridView;
        @BindView(R.id.userIcon)
        ImageView userIcon;
        @BindView(R.id.userName)
        TextView userName;
        @BindView(R.id.setTopTv)
        TextView setTopTv;
        @BindView(R.id.deleteImg)
        ImageView deleteImg;
        @BindView(R.id.layout)
        LinearLayout layout;
        public MorePicViewholder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class BigPicViewholder extends RecyclerView.ViewHolder {
        @BindView(R.id.titleTv)
        TextView titleTv;
        @BindView(R.id.timeTv)
        TextView timeTv;
        @BindView(R.id.lookTv)
        TextView lookTv;
        @BindView(R.id.bigPicImg)
        ImageView bigPicImg;
        @BindView(R.id.contentTv)
        TextView contentTv;
        @BindView(R.id.userIcon)
        ImageView userIcon;
        @BindView(R.id.userName)
        TextView userName;
        @BindView(R.id.layout)
        LinearLayout layout;
        @BindView(R.id.deleteImg)
        ImageView deleteImg;

        public BigPicViewholder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class NoPicViewholder extends RecyclerView.ViewHolder {
        @BindView(R.id.titleTv)
        TextView titleTv;
        @BindView(R.id.timeTv)
        TextView timeTv;
        @BindView(R.id.lookTv)
        TextView lookTv;
        @BindView(R.id.contentTv)
        TextView contentTv;
        @BindView(R.id.userIcon)
        ImageView userIcon;
        @BindView(R.id.userName)
        TextView userName;
        @BindView(R.id.layout)
        LinearLayout layout;
        @BindView(R.id.deleteImg)
        ImageView deleteImg;


        public NoPicViewholder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    public class CityAndChoiseVideoHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.videoView)
        HeartVideo videoView;
        @BindView(R.id.userIcon)
        ImageView userIcon;
        @BindView(R.id.titleTv)
        TextView titleTv;
        @BindView(R.id.timeTv)
        TextView timeTv;
        @BindView(R.id.lookTv)
        TextView lookTv;
        @BindView(R.id.userName)
        TextView userName;
        @BindView(R.id.toDetailLayout)
        LinearLayout toDetailLayout;
        @BindView(R.id.deleteImg)
        ImageView deleteImg;

        public CityAndChoiseVideoHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    public class LookVideoHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.videoView)
        HeartVideo videoView;
        @BindView(R.id.userIcon)
        ImageView userIcon;
        @BindView(R.id.userName)
        TextView userName;
        @BindView(R.id.commentTv)
        TextView commentTv;
        @BindView(R.id.likeTv)
        TextView likeTv;
        @BindView(R.id.toDetailLayout)
        RelativeLayout toDetailLayout;
        @BindView(R.id.likeLayout)
        LinearLayout likeLayout;

        @BindView(R.id.attentionTv)
        TextView attentionTv;
        @BindView(R.id.likeImg)
        ImageView likeImg;
        @BindView(R.id.settingImg)
        ImageView settingImg;

        public LookVideoHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void setHomeItemListener(HomeItemListener homeItemListener) {
        this.homeItemListener = homeItemListener;
    }
}
