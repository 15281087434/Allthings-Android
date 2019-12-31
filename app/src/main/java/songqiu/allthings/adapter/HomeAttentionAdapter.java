package songqiu.allthings.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import songqiu.allthings.R;
import songqiu.allthings.articledetail.ArticleDetailActivity;
import songqiu.allthings.bean.HomeAttentionBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.iterface.HomeAttentionListener;
import songqiu.allthings.mine.userpage.UserPagerActivity;
import songqiu.allthings.util.ClickUtil;
import songqiu.allthings.util.DateUtil;
import songqiu.allthings.util.GlideCircleTransform;
import songqiu.allthings.util.ImageResUtils;
import songqiu.allthings.util.NetWorkUtil;
import songqiu.allthings.util.ShowNumUtil;
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
public class HomeAttentionAdapter extends RecyclerView.Adapter {

    Context context;
    List<HomeAttentionBean> item;
    //type 1   type 2 视频
    private final int TYPE_TEXT = 1; //无图
    private final int TYPE_MORE_PIC = 3; //多图
    private final int TYPE_VIDEO = 2;
    private final int TYPE_RIGHT_PIC = 4; //右图1张
    private final int TYPE_BIG_PIC = 5;//大图

    HomeAttentionListener homeAttentionListener;

    public HomeAttentionAdapter(Context context, List<HomeAttentionBean> item) {
        this.context = context;
        this.item = item;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == TYPE_TEXT) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_home_tab_attention_text, null);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(lp);
            return new TextViewholder(view);
        } else if (viewType == TYPE_RIGHT_PIC) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_home_tab_attention_right_pic, null);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(lp);
            return new RightPicViewholder(view);
        } else if (viewType == TYPE_BIG_PIC) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_home_tab_attention_big_pic, null);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(lp);
            return new BigPicViewholder(view);
        } else if (viewType == TYPE_MORE_PIC) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_home_tab_attention_more_pic, null);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(lp);
            return new MorePicViewholder(view);
        } else if (viewType == TYPE_VIDEO) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_home_tab_attention_video_view, null);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(lp);
            return new Viewholder(view);

        }

        return null;
    }

    @Override
    public int getItemViewType(int position) {
        if (item.get(position).type == 1) {
            if (item.get(position).module == 4) { //多图
                return TYPE_MORE_PIC;
            } else if (item.get(position).module == 1) {
                return TYPE_TEXT;
            } else if (item.get(position).module == 2) {
                return TYPE_BIG_PIC;
            }
            return TYPE_RIGHT_PIC;
        }
        return TYPE_VIDEO;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof TextViewholder) {
            setTextData((TextViewholder) holder, position);
        } else if (holder instanceof RightPicViewholder) {
            setRightPicData((RightPicViewholder) holder, position);
        } else if (holder instanceof MorePicViewholder) {
            setMorePicData((MorePicViewholder) holder, position);
        } else if(holder instanceof BigPicViewholder){
            setBigPicData((BigPicViewholder) holder,position);
        }else if (holder instanceof Viewholder) {
            setVideoData((Viewholder) holder, position);
        }
    }


    public void setTextData(TextViewholder holder, int position) {
        HomeAttentionBean homeAttentionBean = item.get(position);
        RequestOptions options = new RequestOptions()
                .circleCrop().transforms(new GlideCircleTransform(context))
                .error(R.mipmap.head_default)
                .placeholder(R.mipmap.head_default);
        if (!StringUtil.isEmpty(homeAttentionBean.avatar)) {
            if (!homeAttentionBean.avatar.contains("http")) {
                homeAttentionBean.avatar = HttpServicePath.BasePicUrl + homeAttentionBean.avatar;
            }
        }
        holder.tvSai.setVisibility(item.get(position).is_match==1?View.VISIBLE:View.GONE);
        Glide.with(context).load(homeAttentionBean.avatar).apply(options).into(holder.userIcon);
        holder.ivLevel.setImageResource(ImageResUtils.getLevelRes(item.get(position).level));
        holder.contentTv.setText(homeAttentionBean.title);
        holder.describeTv.setText(homeAttentionBean.descriptions);
        holder.userName.setText(homeAttentionBean.user_nickname);
        //判断时间 昨天  今天
        long time = homeAttentionBean.created * 1000;
        if (DateUtil.IsToday(time)) {
            holder.timeTv.setText("刚刚");
        } else if (DateUtil.IsYesterday(time)) {
            holder.timeTv.setText("1天前");
        } else {
            holder.timeTv.setText(DateUtil.getTimeBig1(time));
        }
        holder.likeTv.setText(ShowNumUtil.showUnm(homeAttentionBean.up_num));
        holder.commentTv.setText(ShowNumUtil.showUnm(homeAttentionBean.comment_num));
        holder.shareTv.setText(ShowNumUtil.showUnm(homeAttentionBean.share_num));
        if (0 == homeAttentionBean.is_up) {
            holder.likeImg.setImageResource(R.mipmap.item_like);
            holder.likeTv.setTextColor(context.getResources().getColor(R.color.FF666666));
        } else {
            holder.likeImg.setImageResource(R.mipmap.item_like_pre);
            holder.likeTv.setTextColor(context.getResources().getColor(R.color.FFDE5C51));
        }
        holder.attentionImg.setBackgroundResource(R.drawable.rectangle_common_no_attention);

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClickUtil.onClick()) {
                    Intent intent = new Intent(context, ArticleDetailActivity.class);
                    intent.putExtra("articleid", homeAttentionBean.articleid);
                    context.startActivity(intent);
                }
            }
        });

        holder.attentionImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClickUtil.onClick()) {
                    homeAttentionListener.cancelFollow(homeAttentionBean.userid, item);
                }
            }
        });

        holder.likeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClickUtil.onClick()) {
                    int is_up = homeAttentionBean.is_up;
                    if (0 == is_up) {
                        if (null != homeAttentionListener) {
                            homeAttentionListener.addLike(HttpServicePath.URL_LIKE, 1, homeAttentionBean.articleid, homeAttentionBean,holder);
                        }
                    } else {
                        if (null != homeAttentionListener) {
                            homeAttentionListener.addLike(HttpServicePath.URL_NO_LIKE, 1, homeAttentionBean.articleid, homeAttentionBean,holder);
                        }
                    }
                }
            }
        });

        holder.shareLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClickUtil.onClick()) {
                    if (null != homeAttentionListener) {
                        homeAttentionListener.addShare(position);
                    }
                }
            }
        });

        holder.userLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ClickUtil.onClick()) {
                    Intent intent = new Intent(context, UserPagerActivity.class);
                    intent.putExtra("userId", homeAttentionBean.userid);
                    context.startActivity(intent);
                }
            }
        });
    }


    public void setRightPicData(RightPicViewholder holder, int position) {
        HomeAttentionBean homeAttentionBean = item.get(position);
        RequestOptions options = new RequestOptions()
                .circleCrop().transforms(new GlideCircleTransform(context))
                .error(R.mipmap.head_default)
                .placeholder(R.mipmap.head_default);
        if (!StringUtil.isEmpty(homeAttentionBean.avatar)) {
            if (!homeAttentionBean.avatar.contains("http")) {
                homeAttentionBean.avatar = HttpServicePath.BasePicUrl + homeAttentionBean.avatar;
            }
        }
        holder.tvSai.setVisibility(item.get(position).is_match==1?View.VISIBLE:View.GONE);
        Glide.with(context).load(homeAttentionBean.avatar).apply(options).into(holder.userIcon);
        RequestOptions options1 = new RequestOptions()
                .error(R.mipmap.pic_default_small)
                .placeholder(R.mipmap.pic_default_small);
        if (!StringUtil.isEmpty(homeAttentionBean.photo)) {
            if (!homeAttentionBean.photo.contains("http")) {
                homeAttentionBean.photo = HttpServicePath.BasePicUrl + homeAttentionBean.photo;
            }
        }
        Glide.with(context).load(homeAttentionBean.photo).apply(options1).into(holder.rightPic);
        holder.ivLevel.setImageResource(ImageResUtils.getLevelRes(item.get(position).level));
        holder.contentTv.setText(homeAttentionBean.title);
        holder.describeTv.setText(homeAttentionBean.descriptions);
        holder.userName.setText(homeAttentionBean.user_nickname);
        //判断时间 昨天  今天
        long time = homeAttentionBean.created * 1000;
        if (DateUtil.IsToday(time)) {
            holder.timeTv.setText("刚刚");
        } else if (DateUtil.IsYesterday(time)) {
            holder.timeTv.setText("1天前");
        } else {
            holder.timeTv.setText(DateUtil.getTimeBig1(time));
        }
        holder.likeTv.setText(ShowNumUtil.showUnm(homeAttentionBean.up_num));
        holder.commentTv.setText(ShowNumUtil.showUnm(homeAttentionBean.comment_num));
        holder.shareTv.setText(ShowNumUtil.showUnm(homeAttentionBean.share_num));
        if (0 == homeAttentionBean.is_up) {
            holder.likeImg.setImageResource(R.mipmap.item_like);
            holder.likeTv.setTextColor(context.getResources().getColor(R.color.FF666666));
        } else {
            holder.likeImg.setImageResource(R.mipmap.item_like_pre);
            holder.likeTv.setTextColor(context.getResources().getColor(R.color.FFDE5C51));
        }
        holder.attentionImg.setBackgroundResource(R.drawable.rectangle_common_no_attention);

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClickUtil.onClick()) {
                    Intent intent = new Intent(context, ArticleDetailActivity.class);
                    intent.putExtra("articleid", homeAttentionBean.articleid);
                    context.startActivity(intent);
                }
            }
        });

        holder.attentionImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClickUtil.onClick()) {
                    homeAttentionListener.cancelFollow(homeAttentionBean.userid, item);
                }
            }
        });

        holder.likeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClickUtil.onClick()) {
                    int is_up = homeAttentionBean.is_up;
                    if (0 == is_up) {
                        if (null != homeAttentionListener) {
                            homeAttentionListener.addLike(HttpServicePath.URL_LIKE, 1, homeAttentionBean.articleid, homeAttentionBean,holder);
                        }
                    } else {
                        if (null != homeAttentionListener) {
                            homeAttentionListener.addLike(HttpServicePath.URL_NO_LIKE, 1, homeAttentionBean.articleid, homeAttentionBean,holder);
                        }
                    }
                }
            }
        });

        holder.shareLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClickUtil.onClick()) {
                    if (null != homeAttentionListener) {
                        homeAttentionListener.addShare(position);
                    }
                }
            }
        });

        holder.userLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ClickUtil.onClick()) {
                    Intent intent = new Intent(context, UserPagerActivity.class);
                    intent.putExtra("userId", homeAttentionBean.userid);
                    context.startActivity(intent);
                }
            }
        });
    }

    public void setBigPicData(BigPicViewholder holder, int position) {
        //
        holder.bigPicImg.post(new Runnable() {
            @Override
            public void run() {
                holder.bigPicImg.setLayoutParams(ViewProportion.getLinearParams(holder.bigPicImg, 2.2));
            }
        });

        HomeAttentionBean homeAttentionBean = item.get(position);
        RequestOptions options = new RequestOptions()
                .circleCrop().transforms(new GlideCircleTransform(context))
                .error(R.mipmap.head_default)
                .placeholder(R.mipmap.head_default);
        if (!StringUtil.isEmpty(homeAttentionBean.avatar)) {
            if (!homeAttentionBean.avatar.contains("http")) {
                homeAttentionBean.avatar = HttpServicePath.BasePicUrl + homeAttentionBean.avatar;
            }
        }
        Glide.with(context).load(homeAttentionBean.avatar).apply(options).into(holder.userIcon);
        holder.ivLevel.setImageResource(ImageResUtils.getLevelRes(item.get(position).level));
        RequestOptions options1 = new RequestOptions()
                .error(R.mipmap.pic_default)
                .placeholder(R.mipmap.pic_default);
        if (!StringUtil.isEmpty(homeAttentionBean.photo)) {
            if (!homeAttentionBean.photo.contains("http")) {
                homeAttentionBean.photo = HttpServicePath.BasePicUrl + homeAttentionBean.photo;
            }
        }
        holder.tvSai.setVisibility(item.get(position).is_match==1?View.VISIBLE:View.GONE);
        Glide.with(context).load(homeAttentionBean.photo).apply(options1).into(holder.bigPicImg);
        holder.contentTv.setText(homeAttentionBean.title);
        holder.describeTv.setText(homeAttentionBean.descriptions);
        holder.userName.setText(homeAttentionBean.user_nickname);
        //判断时间 昨天  今天
        long time = homeAttentionBean.created * 1000;
        if (DateUtil.IsToday(time)) {
            holder.timeTv.setText("刚刚");
        } else if (DateUtil.IsYesterday(time)) {
            holder.timeTv.setText("1天前");
        } else {
            holder.timeTv.setText(DateUtil.getTimeBig1(time));
        }
        holder.likeTv.setText(ShowNumUtil.showUnm(homeAttentionBean.up_num));
        holder.commentTv.setText(ShowNumUtil.showUnm(homeAttentionBean.comment_num));
        holder.shareTv.setText(ShowNumUtil.showUnm(homeAttentionBean.share_num));
        if (0 == homeAttentionBean.is_up) {
            holder.likeImg.setImageResource(R.mipmap.item_like);
            holder.likeTv.setTextColor(context.getResources().getColor(R.color.FF666666));
        } else {
            holder.likeImg.setImageResource(R.mipmap.item_like_pre);
            holder.likeTv.setTextColor(context.getResources().getColor(R.color.FFDE5C51));
        }
        holder.attentionImg.setBackgroundResource(R.drawable.rectangle_common_no_attention);

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClickUtil.onClick()) {
                    Intent intent = new Intent(context, ArticleDetailActivity.class);
                    intent.putExtra("articleid", homeAttentionBean.articleid);
                    context.startActivity(intent);
                }
            }
        });

        holder.attentionImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClickUtil.onClick()) {
                    homeAttentionListener.cancelFollow(homeAttentionBean.userid, item);
                }
            }
        });

        holder.likeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClickUtil.onClick()) {
                    int is_up = homeAttentionBean.is_up;
                    if (0 == is_up) {
                        if (null != homeAttentionListener) {
                            homeAttentionListener.addLike(HttpServicePath.URL_LIKE, 1, homeAttentionBean.articleid, homeAttentionBean,holder);
                        }
                    } else {
                        if (null != homeAttentionListener) {
                            homeAttentionListener.addLike(HttpServicePath.URL_NO_LIKE, 1, homeAttentionBean.articleid, homeAttentionBean,holder);
                        }
                    }
                }
            }
        });

        holder.shareLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClickUtil.onClick()) {
                    if (null != homeAttentionListener) {
                        homeAttentionListener.addShare(position);
                    }
                }
            }
        });

        holder.userLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ClickUtil.onClick()) {
                    Intent intent = new Intent(context, UserPagerActivity.class);
                    intent.putExtra("userId", homeAttentionBean.userid);
                    context.startActivity(intent);
                }
            }
        });
    }

    public void setMorePicData(MorePicViewholder holder, int position) {
        HomeAttentionBean homeAttentionBean = item.get(position);
        RequestOptions options = new RequestOptions()
                .circleCrop().transforms(new GlideCircleTransform(context))
                .error(R.mipmap.head_default)
                .placeholder(R.mipmap.head_default);
        if (!StringUtil.isEmpty(homeAttentionBean.avatar)) {
            if (!homeAttentionBean.avatar.contains("http")) {
                homeAttentionBean.avatar = HttpServicePath.BasePicUrl + homeAttentionBean.avatar;
            }
        }
        holder.tvSai.setVisibility(item.get(position).is_match==1?View.VISIBLE:View.GONE);
        Glide.with(context).load(homeAttentionBean.avatar).apply(options).into(holder.userIcon);
        holder.ivLevel.setImageResource(ImageResUtils.getLevelRes(item.get(position).level));
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

        holder.titleTv.setText(homeAttentionBean.title);
        holder.userName.setText(homeAttentionBean.user_nickname);
        //判断时间 昨天  今天
        long time = homeAttentionBean.created * 1000;
        if (DateUtil.IsToday(time)) {
            holder.timeTv.setText("刚刚");
        } else if (DateUtil.IsYesterday(time)) {
            holder.timeTv.setText("1天前");
        } else {
            holder.timeTv.setText(DateUtil.getTimeBig1(time));
        }
        holder.likeTv.setText(ShowNumUtil.showUnm(homeAttentionBean.up_num));
        holder.commentTv.setText(ShowNumUtil.showUnm(homeAttentionBean.comment_num));
        holder.shareTv.setText(ShowNumUtil.showUnm(homeAttentionBean.share_num));
        if (0 == homeAttentionBean.is_up) {
            holder.likeImg.setImageResource(R.mipmap.item_like);
            holder.likeTv.setTextColor(context.getResources().getColor(R.color.FF666666));
        } else {
            holder.likeImg.setImageResource(R.mipmap.item_like_pre);
            holder.likeTv.setTextColor(context.getResources().getColor(R.color.FFDE5C51));
        }
        holder.attentionImg.setBackgroundResource(R.drawable.rectangle_common_no_attention);

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClickUtil.onClick()) {
                    Intent intent = new Intent(context, ArticleDetailActivity.class);
                    intent.putExtra("articleid", homeAttentionBean.articleid);
                    context.startActivity(intent);
                }
            }
        });

        holder.attentionImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClickUtil.onClick()) {
                    homeAttentionListener.cancelFollow(homeAttentionBean.userid, item);
                }
            }
        });

        holder.likeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClickUtil.onClick()) {
                    int is_up = homeAttentionBean.is_up;
                    if (0 == is_up) {
                        if (null != homeAttentionListener) {
                            homeAttentionListener.addLike(HttpServicePath.URL_LIKE, 1, homeAttentionBean.articleid, homeAttentionBean,holder);
                        }
                    } else {
                        if (null != homeAttentionListener) {
                            homeAttentionListener.addLike(HttpServicePath.URL_NO_LIKE, 1, homeAttentionBean.articleid, homeAttentionBean,holder);
                        }
                    }
                }
            }
        });

        holder.shareLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClickUtil.onClick()) {
                    if (null != homeAttentionListener) {
                        homeAttentionListener.addShare(position);
                    }
                }
            }
        });

        holder.userLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ClickUtil.onClick()) {
                    Intent intent = new Intent(context, UserPagerActivity.class);
                    intent.putExtra("userId", homeAttentionBean.userid);
                    context.startActivity(intent);
                }
            }
        });
    }


    public void setVideoData(Viewholder holder, int position) {
        HomeAttentionBean homeAttentionBean = item.get(position);
        if(!StringUtil.isEmpty(homeAttentionBean.source_url)) {
            HeartVideoInfo info = HeartVideoInfo.Builder().setTitle("").setPath(homeAttentionBean.source_url).setImagePath(homeAttentionBean.photo).setSaveProgress(false).setVideoId(homeAttentionBean.articleid).builder();
            VideoControl control = new VideoControl(context);
            control.setInfo(info);
            holder.videoView.setHeartVideoContent(control);
            if(item.get(position).state==1&& NetWorkUtil.getConnectedType(context)== ConnectivityManager.TYPE_WIFI){

                holder.videoView.startSlence();

            }

        }
        RequestOptions options = new RequestOptions()
                .circleCrop().transforms(new GlideCircleTransform(context))
                .error(R.mipmap.head_default)
                .placeholder(R.mipmap.head_default);
        if (!StringUtil.isEmpty(homeAttentionBean.avatar)) {
            if (!homeAttentionBean.avatar.contains("http")) {
                homeAttentionBean.avatar = HttpServicePath.BasePicUrl + homeAttentionBean.avatar;
            }
        }
        holder.tvSai.setVisibility(item.get(position).is_match==1?View.VISIBLE:View.GONE);
        Glide.with(context).load(homeAttentionBean.avatar).apply(options).into(holder.userIcon);
        holder.ivLevel.setImageResource(ImageResUtils.getLevelRes(item.get(position).level));
        holder.titleTv.setText(homeAttentionBean.title);
        holder.userName.setText(homeAttentionBean.user_nickname);
        //判断时间 昨天  今天
        long time = homeAttentionBean.created * 1000;
        if (DateUtil.IsToday(time)) {
            holder.timeTv.setText("刚刚");
        } else if (DateUtil.IsYesterday(time)) {
            holder.timeTv.setText("1天前");
        } else {
            holder.timeTv.setText(DateUtil.getTimeBig1(time));
        }
        holder.likeTv.setText(ShowNumUtil.showUnm(homeAttentionBean.up_num));
        holder.commentTv.setText(ShowNumUtil.showUnm(homeAttentionBean.comment_num));
        holder.shareTv.setText(ShowNumUtil.showUnm(homeAttentionBean.share_num));
        if (0 == homeAttentionBean.is_up) {
            holder.likeImg.setImageResource(R.mipmap.item_like);
            holder.likeTv.setTextColor(context.getResources().getColor(R.color.FF666666));
        } else {
            holder.likeImg.setImageResource(R.mipmap.item_like_pre);
            holder.likeTv.setTextColor(context.getResources().getColor(R.color.FFDE5C51));
        }
        holder.attentionImg.setBackgroundResource(R.drawable.rectangle_common_no_attention);
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClickUtil.onClick()) {
                    Intent intent = new Intent(context, VideoDetailActivity.class);
                    intent.putExtra("articleid", homeAttentionBean.articleid);
                    context.startActivity(intent);
                }
            }
        });

        holder.attentionImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClickUtil.onClick()) {
                    homeAttentionListener.cancelFollow(homeAttentionBean.userid, item);
                }
            }
        });

        holder.likeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClickUtil.onClick()) {
                    int is_up = homeAttentionBean.is_up;
                    if (0 == is_up) {
                        if (null != homeAttentionListener) {
                            homeAttentionListener.addLike(HttpServicePath.URL_LIKE, 2, homeAttentionBean.articleid, homeAttentionBean,holder);
                        }
                    } else {
                        if (null != homeAttentionListener) {
                            homeAttentionListener.addLike(HttpServicePath.URL_NO_LIKE, 2, homeAttentionBean.articleid, homeAttentionBean,holder);
                        }
                    }
                }
            }
        });

        holder.shareLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClickUtil.onClick()) {
                    if (null != homeAttentionListener) {
                        homeAttentionListener.addShare(position);
                    }
                }
            }
        });

        holder.userLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ClickUtil.onClick()) {
                    Intent intent = new Intent(context, UserPagerActivity.class);
                    intent.putExtra("userId", homeAttentionBean.userid);
                    context.startActivity(intent);
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
        if (holder instanceof Viewholder) {
            HeartVideo heartVideo = ((Viewholder) holder).videoView;
            if (heartVideo == HeartVideoManager.getInstance().getCurrPlayVideo()) {
                HeartVideoManager.getInstance().release();
            }
        }
    }

    public class TextViewholder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_sai)
        TextView tvSai;
        @BindView(R.id.userIcon)
        ImageView userIcon;
        @BindView(R.id.userName)
        TextView userName;
        @BindView(R.id.timeTv)
        TextView timeTv;
        @BindView(R.id.userLayout)
        RelativeLayout userLayout;
        @BindView(R.id.attentionImg)
        TextView attentionImg;
        @BindView(R.id.contentTv)
        TextView contentTv;
        @BindView(R.id.describeTv)
        TextView describeTv;
        @BindView(R.id.likeImg)
        public ImageView likeImg;
        @BindView(R.id.likeTv)
        public TextView likeTv;
        @BindView(R.id.likeLayout)
        LinearLayout likeLayout;
        @BindView(R.id.commentTv)
        TextView commentTv;
        @BindView(R.id.shareTv)
        TextView shareTv;
        @BindView(R.id.shareLayout)
        LinearLayout shareLayout;
        @BindView(R.id.layout)
        LinearLayout layout;
        @BindView(R.id.iv_level)
        ImageView ivLevel;
        public TextViewholder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class RightPicViewholder extends RecyclerView.ViewHolder {
        @BindView(R.id.userIcon)
        ImageView userIcon;
        @BindView(R.id.userName)
        TextView userName;
        @BindView(R.id.timeTv)
        TextView timeTv;
        @BindView(R.id.attentionImg)
        TextView attentionImg;
        @BindView(R.id.contentTv)
        TextView contentTv;
        @BindView(R.id.describeTv)
        TextView describeTv;
        @BindView(R.id.rightPic)
        ImageView rightPic;
        @BindView(R.id.likeImg)
        public ImageView likeImg;
        @BindView(R.id.likeTv)
        public TextView likeTv;
        @BindView(R.id.likeLayout)
        LinearLayout likeLayout;
        @BindView(R.id.commentTv)
        TextView commentTv;
        @BindView(R.id.shareTv)
        TextView shareTv;
        @BindView(R.id.shareLayout)
        LinearLayout shareLayout;
        @BindView(R.id.layout)
        LinearLayout layout;
        @BindView(R.id.userLayout)
        RelativeLayout userLayout;
        @BindView(R.id.iv_level)
        ImageView ivLevel;
        @BindView(R.id.tv_sai)
        TextView tvSai;
        public RightPicViewholder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class BigPicViewholder extends RecyclerView.ViewHolder {
        @BindView(R.id.userIcon)
        ImageView userIcon;
        @BindView(R.id.userName)
        TextView userName;
        @BindView(R.id.timeTv)
        TextView timeTv;
        @BindView(R.id.userLayout)
        RelativeLayout userLayout;
        @BindView(R.id.attentionImg)
        TextView attentionImg;
        @BindView(R.id.contentTv)
        TextView contentTv;
        @BindView(R.id.bigPicImg)
        ImageView bigPicImg;
        @BindView(R.id.describeTv)
        TextView describeTv;
        @BindView(R.id.likeImg)
        public ImageView likeImg;
        @BindView(R.id.likeTv)
        public TextView likeTv;
        @BindView(R.id.likeLayout)
        LinearLayout likeLayout;
        @BindView(R.id.commentTv)
        TextView commentTv;
        @BindView(R.id.shareTv)
        TextView shareTv;
        @BindView(R.id.shareLayout)
        LinearLayout shareLayout;
        @BindView(R.id.layout)
        LinearLayout layout;
        @BindView(R.id.iv_level)
        ImageView ivLevel;
        @BindView(R.id.tv_sai)
        TextView tvSai;
        public BigPicViewholder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    public class MorePicViewholder extends RecyclerView.ViewHolder {
        @BindView(R.id.userIcon)
        ImageView userIcon;
        @BindView(R.id.userName)
        TextView userName;
        @BindView(R.id.timeTv)
        TextView timeTv;
        @BindView(R.id.userLayout)
        RelativeLayout userLayout;
        @BindView(R.id.attentionImg)
        TextView attentionImg;
        @BindView(R.id.titleTv)
        TextView titleTv;
        @BindView(R.id.gridView)
        GridViewInScroll gridView;
        @BindView(R.id.likeImg)
        public ImageView likeImg;
        @BindView(R.id.likeTv)
        public TextView likeTv;
        @BindView(R.id.likeLayout)
        LinearLayout likeLayout;
        @BindView(R.id.commentTv)
        TextView commentTv;
        @BindView(R.id.shareTv)
        TextView shareTv;
        @BindView(R.id.shareLayout)
        LinearLayout shareLayout;
        @BindView(R.id.layout)
        LinearLayout layout;
        @BindView(R.id.iv_level)
        ImageView ivLevel;
        @BindView(R.id.tv_sai)
        TextView tvSai;
        public MorePicViewholder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    public class Viewholder extends RecyclerView.ViewHolder {
        @BindView(R.id.userIcon)
        ImageView userIcon;
        @BindView(R.id.userName)
        TextView userName;
        @BindView(R.id.timeTv)
        TextView timeTv;
        @BindView(R.id.attentionImg)
        TextView attentionImg;
        @BindView(R.id.titleTv)
        TextView titleTv;
        @BindView(R.id.videoView)
        HeartVideo videoView;
        @BindView(R.id.likeImg)
        public ImageView likeImg;
        @BindView(R.id.likeTv)
        public TextView likeTv;
        @BindView(R.id.likeLayout)
        LinearLayout likeLayout;
        @BindView(R.id.commentTv)
        TextView commentTv;
        @BindView(R.id.shareTv)
        TextView shareTv;
        @BindView(R.id.shareLayout)
        LinearLayout shareLayout;
        @BindView(R.id.layout)
        LinearLayout layout;
        @BindView(R.id.userLayout)
        RelativeLayout userLayout;
        @BindView(R.id.iv_level)
        ImageView ivLevel;
        @BindView(R.id.tv_sai)
        TextView tvSai;
        public Viewholder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    public void setHomeAttentionListener(HomeAttentionListener homeAttentionListener) {
        this.homeAttentionListener = homeAttentionListener;
    }
}
