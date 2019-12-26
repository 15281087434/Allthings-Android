package songqiu.allthings.adapter;

import android.content.Context;
import android.content.Intent;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import songqiu.allthings.R;
import songqiu.allthings.articledetail.ArticleDetailActivity;
import songqiu.allthings.bean.HomeSubitemBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.iterface.CollectItemListener;
import songqiu.allthings.mine.userpage.UserPagerActivity;
import songqiu.allthings.util.ClickUtil;
import songqiu.allthings.util.DateUtil;
import songqiu.allthings.util.GlideCircleTransform;
import songqiu.allthings.util.ImageResUtils;
import songqiu.allthings.util.ShowNumUtil;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.videodetail.VideoDetailActivity;
import songqiu.allthings.view.GridViewInScroll;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/9/10
 *
 *类描述：
 *
 ********/
public class CollectAdapter extends RecyclerView.Adapter {
    Context context;
    List<HomeSubitemBean> item;

    CollectItemListener listener;

    private final int TYPE_ONLY_TEXT = 1;
    private final int TYPE_BIG_PIC = 2;
    private final int TYPE_SMALL_PIC = 3;
    private final int TYPE_MORE_PIC = 5;
    private final int TYPE_VIDEO = 4;

    @Override
    public int getItemCount() {
        return item.size();
    }

    public CollectAdapter(Context context, List<HomeSubitemBean> item) {
        this.context = context;
        this.item = item;
    }


    @Override
    public int getItemViewType(int position) {
        if (item.get(position).type == 2) {
            //视频
            return TYPE_VIDEO;
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
            return TYPE_SMALL_PIC;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType == TYPE_ONLY_TEXT) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_collect_only_text, null);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(lp);
            return new NoPicViewholder(view);
        } else if (viewType == TYPE_BIG_PIC) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_collect_big_pic, null);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(lp);
            return new BigPicViewholder(view);
        } else if (viewType == TYPE_SMALL_PIC) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_home_tab_attention_right_pic, null);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(lp);
            return new RightPicViewholder(view);
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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof RightPicViewholder) {
            setRightPicData((RightPicViewholder) holder, position);
        } else if (holder instanceof Viewholder) {
            setVideoData((Viewholder) holder, position);
        } else if (holder instanceof NoPicViewholder) {
            setNoPicData((NoPicViewholder) holder, position);
        } else if (holder instanceof MorePicViewholder) {
            setMorePicData((MorePicViewholder) holder, position);
        } else if (holder instanceof BigPicViewholder) {
            setBigPicData((BigPicViewholder) holder, position);
        }


    }

    public void setNoPicData(NoPicViewholder holder, int position) {
        HomeSubitemBean homeSubitemBean = item.get(position);
        RequestOptions options = new RequestOptions()
                .circleCrop().transforms(new GlideCircleTransform(context))
                .error(R.mipmap.head_default)
                .placeholder(R.mipmap.head_default);
        if (!StringUtil.isEmpty(homeSubitemBean.avatar)) {
            if (!homeSubitemBean.avatar.contains("http")) {
                homeSubitemBean.avatar = HttpServicePath.BasePicUrl + homeSubitemBean.avatar;
            }
        }
        Glide.with(context).load(homeSubitemBean.avatar).apply(options).into(holder.userIcon);


        holder.ivLevel.setImageResource(ImageResUtils.getLevelRes(homeSubitemBean.level));


        holder.titleTv.setText(homeSubitemBean.title);
        holder.describeTv.setText(homeSubitemBean.descriptions);
        holder.userName.setText(homeSubitemBean.user_nickname);
        //判断时间 昨天  今天
        long time = homeSubitemBean.created * 1000;
        if (DateUtil.IsToday(time)) {
            holder.timeTv.setText("刚刚");
        } else if (DateUtil.IsYesterday(time)) {
            holder.timeTv.setText("1天前");
        } else {
            holder.timeTv.setText(DateUtil.getTimeBig1(time));
        }

        holder.shareTv.setText(ShowNumUtil.showUnm(homeSubitemBean.share_num));
        holder.likeTv.setText(ShowNumUtil.showUnm(homeSubitemBean.up_num));
        holder.commentTv.setText(ShowNumUtil.showUnm(homeSubitemBean.comment_num));
        if (0 == homeSubitemBean.is_up) {
            holder.likeImg.setImageResource(R.mipmap.item_like);
            holder.likeTv.setTextColor(context.getResources().getColor(R.color.FF666666));
        } else {
            holder.likeImg.setImageResource(R.mipmap.item_like_pre);
            holder.likeTv.setTextColor(context.getResources().getColor(R.color.FFDE5C51));
        }
        holder.attentionImg.setBackgroundResource(R.drawable.rectangle_common_no_attention);
        holder.attentionImg.setText("已收藏");

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

        holder.attentionImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClickUtil.onClick()) {
                    listener.cancelCollect(1, homeSubitemBean.articleid, position);
                }
            }
        });

        holder.likeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClickUtil.onClick()) {
                    int is_up = homeSubitemBean.is_up;
                    if (0 == is_up) {
                        if (null != listener) {
                            listener.addLike(HttpServicePath.URL_LIKE, 1, homeSubitemBean.articleid, homeSubitemBean, holder);
                        }
                    } else {
                        if (null != listener) {
                            listener.addLike(HttpServicePath.URL_NO_LIKE, 1, homeSubitemBean.articleid, homeSubitemBean, holder);
                        }
                    }
                }
            }
        });

        holder.shareLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClickUtil.onClick()) {
                    if (null != listener) {
                        listener.addShare(position, 1);
                    }
                }
            }
        });

        holder.userLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ClickUtil.onClick()) {
                    Intent intent = new Intent(context, UserPagerActivity.class);
                    intent.putExtra("userId", homeSubitemBean.userid);
                    context.startActivity(intent);
                }
            }
        });
    }

    public void setBigPicData(BigPicViewholder holder, int position) {
        HomeSubitemBean homeSubitemBean = item.get(position);
        RequestOptions options = new RequestOptions()
                .circleCrop().transforms(new GlideCircleTransform(context))
                .error(R.mipmap.head_default)
                .placeholder(R.mipmap.head_default);
        if (!StringUtil.isEmpty(homeSubitemBean.avatar)) {
            if (!homeSubitemBean.avatar.contains("http")) {
                homeSubitemBean.avatar = HttpServicePath.BasePicUrl + homeSubitemBean.avatar;
            }
        }
        Glide.with(context).load(homeSubitemBean.avatar).apply(options).into(holder.userIcon);


        holder.ivLevel.setImageResource(ImageResUtils.getLevelRes(homeSubitemBean.level));


        RequestOptions options1 = new RequestOptions()
                .error(R.mipmap.pic_default_small)
                .placeholder(R.mipmap.pic_default_small);
        if (!StringUtil.isEmpty(homeSubitemBean.photo)) {
            if (!homeSubitemBean.photo.contains("http")) {
                homeSubitemBean.photo = HttpServicePath.BasePicUrl + homeSubitemBean.photo;
            }
        }
        Glide.with(context).load(homeSubitemBean.photo).apply(options1).into(holder.bigPicImg);

        holder.titleTv.setText(homeSubitemBean.title);
        holder.describeTv.setText(homeSubitemBean.descriptions);
        holder.userName.setText(homeSubitemBean.user_nickname);
        //判断时间 昨天  今天
        long time = homeSubitemBean.created * 1000;
        if (DateUtil.IsToday(time)) {
            holder.timeTv.setText("刚刚");
        } else if (DateUtil.IsYesterday(time)) {
            holder.timeTv.setText("1天前");
        } else {
            holder.timeTv.setText(DateUtil.getTimeBig1(time));
        }

        holder.shareTv.setText(ShowNumUtil.showUnm(homeSubitemBean.share_num));
        holder.likeTv.setText(ShowNumUtil.showUnm(homeSubitemBean.up_num));
        holder.commentTv.setText(ShowNumUtil.showUnm(homeSubitemBean.comment_num));
        if (0 == homeSubitemBean.is_up) {
            holder.likeImg.setImageResource(R.mipmap.item_like);
            holder.likeTv.setTextColor(context.getResources().getColor(R.color.FF666666));
        } else {
            holder.likeImg.setImageResource(R.mipmap.item_like_pre);
            holder.likeTv.setTextColor(context.getResources().getColor(R.color.FFDE5C51));
        }
        holder.attentionImg.setBackgroundResource(R.drawable.rectangle_common_no_attention);
        holder.attentionImg.setText("已收藏");

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

        holder.attentionImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClickUtil.onClick()) {
                    listener.cancelCollect(1, homeSubitemBean.articleid, position);
                }
            }
        });

        holder.likeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClickUtil.onClick()) {
                    int is_up = homeSubitemBean.is_up;
                    if (0 == is_up) {
                        if (null != listener) {
                            listener.addLike(HttpServicePath.URL_LIKE, 1, homeSubitemBean.articleid, homeSubitemBean, holder);
                        }
                    } else {
                        if (null != listener) {
                            listener.addLike(HttpServicePath.URL_NO_LIKE, 1, homeSubitemBean.articleid, homeSubitemBean, holder);
                        }
                    }
                }
            }
        });

        holder.shareLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClickUtil.onClick()) {
                    if (null != listener) {
                        listener.addShare(position, 1);
                    }
                }
            }
        });

        holder.userLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ClickUtil.onClick()) {
                    Intent intent = new Intent(context, UserPagerActivity.class);
                    intent.putExtra("userId", homeSubitemBean.userid);
                    context.startActivity(intent);
                }
            }
        });
    }

    public void setRightPicData(RightPicViewholder holder, int position) {
        HomeSubitemBean homeSubitemBean = item.get(position);
        RequestOptions options = new RequestOptions()
                .circleCrop().transforms(new GlideCircleTransform(context))
                .error(R.mipmap.head_default)
                .placeholder(R.mipmap.head_default);
        if (!StringUtil.isEmpty(homeSubitemBean.avatar)) {
            if (!homeSubitemBean.avatar.contains("http")) {
                homeSubitemBean.avatar = HttpServicePath.BasePicUrl + homeSubitemBean.avatar;
            }
        }
        Glide.with(context).load(homeSubitemBean.avatar).apply(options).into(holder.userIcon);


        holder.ivLevel.setImageResource(ImageResUtils.getLevelRes(homeSubitemBean.level));

        RequestOptions options1 = new RequestOptions()
                .error(R.mipmap.pic_default_small)
                .placeholder(R.mipmap.pic_default_small);
        if (!StringUtil.isEmpty(homeSubitemBean.photo)) {
            if (!homeSubitemBean.photo.contains("http")) {
                homeSubitemBean.photo = HttpServicePath.BasePicUrl + homeSubitemBean.photo;
            }
        }
        Glide.with(context).load(homeSubitemBean.photo).apply(options1).into(holder.rightPic);

        holder.contentTv.setText(homeSubitemBean.title);
        holder.describeTv.setText(homeSubitemBean.descriptions);
        holder.userName.setText(homeSubitemBean.user_nickname);
        //判断时间 昨天  今天
        long time = homeSubitemBean.created * 1000;
        if (DateUtil.IsToday(time)) {
            holder.timeTv.setText("刚刚");
        } else if (DateUtil.IsYesterday(time)) {
            holder.timeTv.setText("1天前");
        } else {
            holder.timeTv.setText(DateUtil.getTimeBig1(time));
        }

        holder.shareTv.setText(ShowNumUtil.showUnm(homeSubitemBean.share_num));
        holder.likeTv.setText(ShowNumUtil.showUnm(homeSubitemBean.up_num));
        holder.commentTv.setText(ShowNumUtil.showUnm(homeSubitemBean.comment_num));
        if (0 == homeSubitemBean.is_up) {
            holder.likeImg.setImageResource(R.mipmap.item_like);
            holder.likeTv.setTextColor(context.getResources().getColor(R.color.FF666666));
        } else {
            holder.likeImg.setImageResource(R.mipmap.item_like_pre);
            holder.likeTv.setTextColor(context.getResources().getColor(R.color.FFDE5C51));
        }
        holder.attentionImg.setBackgroundResource(R.drawable.rectangle_common_no_attention);
        holder.attentionImg.setText("已收藏");

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

        holder.attentionImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClickUtil.onClick()) {
                    listener.cancelCollect(1, homeSubitemBean.articleid, position);
                }
            }
        });

        holder.likeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClickUtil.onClick()) {
                    int is_up = homeSubitemBean.is_up;
                    if (0 == is_up) {
                        if (null != listener) {
                            listener.addLike(HttpServicePath.URL_LIKE, 1, homeSubitemBean.articleid, homeSubitemBean, holder);
                        }
                    } else {
                        if (null != listener) {
                            listener.addLike(HttpServicePath.URL_NO_LIKE, 1, homeSubitemBean.articleid, homeSubitemBean, holder);
                        }
                    }
                }
            }
        });

        holder.shareLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClickUtil.onClick()) {
                    if (null != listener) {
                        listener.addShare(position, 1);
                    }
                }
            }
        });

        holder.userLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ClickUtil.onClick()) {
                    Intent intent = new Intent(context, UserPagerActivity.class);
                    intent.putExtra("userId", homeSubitemBean.userid);
                    context.startActivity(intent);
                }
            }
        });
    }


    public void setMorePicData(MorePicViewholder holder, int position) {
        HomeSubitemBean homeSubitemBean = item.get(position);
        RequestOptions options = new RequestOptions()
                .circleCrop().transforms(new GlideCircleTransform(context))
                .error(R.mipmap.head_default)
                .placeholder(R.mipmap.head_default);
        if (!StringUtil.isEmpty(homeSubitemBean.avatar)) {
            if (!homeSubitemBean.avatar.contains("http")) {
                homeSubitemBean.avatar = HttpServicePath.BasePicUrl + homeSubitemBean.avatar;
            }
        }
        Glide.with(context).load(homeSubitemBean.avatar).apply(options).into(holder.userIcon);


        holder.ivLevel.setImageResource(ImageResUtils.getLevelRes(homeSubitemBean.level));

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

        holder.titleTv.setText(homeSubitemBean.title);
        holder.userName.setText(homeSubitemBean.user_nickname);
        //判断时间 昨天  今天
        long time = homeSubitemBean.created * 1000;
        if (DateUtil.IsToday(time)) {
            holder.timeTv.setText("刚刚");
        } else if (DateUtil.IsYesterday(time)) {
            holder.timeTv.setText("1天前");
        } else {
            holder.timeTv.setText(DateUtil.getTimeBig1(time));
        }

        holder.shareTv.setText(ShowNumUtil.showUnm(homeSubitemBean.share_num));
        holder.likeTv.setText(ShowNumUtil.showUnm(homeSubitemBean.up_num));
        holder.commentTv.setText(ShowNumUtil.showUnm(homeSubitemBean.comment_num));
        if (0 == homeSubitemBean.is_up) {
            holder.likeImg.setImageResource(R.mipmap.item_like);
            holder.likeTv.setTextColor(context.getResources().getColor(R.color.FF666666));
        } else {
            holder.likeImg.setImageResource(R.mipmap.item_like_pre);
            holder.likeTv.setTextColor(context.getResources().getColor(R.color.FFDE5C51));
        }
        holder.attentionImg.setBackgroundResource(R.drawable.rectangle_common_no_attention);
        holder.attentionImg.setText("已收藏");

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

        holder.attentionImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClickUtil.onClick()) {
                    listener.cancelCollect(1, homeSubitemBean.articleid, position);
                }
            }
        });

        holder.likeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClickUtil.onClick()) {
                    int is_up = homeSubitemBean.is_up;
                    if (0 == is_up) {
                        if (null != listener) {
                            listener.addLike(HttpServicePath.URL_LIKE, 1, homeSubitemBean.articleid, homeSubitemBean, holder);
                        }
                    } else {
                        if (null != listener) {
                            listener.addLike(HttpServicePath.URL_NO_LIKE, 1, homeSubitemBean.articleid, homeSubitemBean, holder);
                        }
                    }
                }
            }
        });

        holder.shareLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClickUtil.onClick()) {
                    if (null != listener) {
                        listener.addShare(position, 1);
                    }
                }
            }
        });

        holder.userLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ClickUtil.onClick()) {
                    Intent intent = new Intent(context, UserPagerActivity.class);
                    intent.putExtra("userId", homeSubitemBean.userid);
                    context.startActivity(intent);
                }
            }
        });
    }


    public void setVideoData(Viewholder holder, int position) {
        HomeSubitemBean homeSubitemBean = item.get(position);
        String path = homeSubitemBean.video_url;
        String image = homeSubitemBean.photo;
        String descriptions = homeSubitemBean.descriptions;
        if (!StringUtil.isEmpty(path)) {
            HeartVideoInfo info = HeartVideoInfo.Builder().setTitle(descriptions).setPath(path).setImagePath(image).setSaveProgress(false).builder();
            VideoControl control = new VideoControl(context);
            control.setInfo(info);
            holder.videoView.setHeartVideoContent(control);
        }
        RequestOptions options = new RequestOptions()
                .circleCrop().transforms(new GlideCircleTransform(context))
                .error(R.mipmap.head_default)
                .placeholder(R.mipmap.head_default);
        Glide.with(context).load(homeSubitemBean.avatar).apply(options).into(holder.userIcon);


        holder.ivLevel.setImageResource(ImageResUtils.getLevelRes(homeSubitemBean.level));

        holder.titleTv.setText(homeSubitemBean.title);
        holder.userName.setText(homeSubitemBean.user_nickname);
        //判断时间 昨天  今天
        long time = homeSubitemBean.created * 1000;
        if (DateUtil.IsToday(time)) {
            holder.timeTv.setText("刚刚");
        } else if (DateUtil.IsYesterday(time)) {
            holder.timeTv.setText("1天前");
        } else {
            holder.timeTv.setText(DateUtil.getTimeBig1(time));
        }
        holder.shareTv.setText(ShowNumUtil.showUnm(homeSubitemBean.share_num));
        holder.likeTv.setText(ShowNumUtil.showUnm(homeSubitemBean.up_num));
        holder.commentTv.setText(ShowNumUtil.showUnm(homeSubitemBean.comment_num));
        if (0 == homeSubitemBean.is_up) {
            holder.likeImg.setImageResource(R.mipmap.item_like);
            holder.likeTv.setTextColor(context.getResources().getColor(R.color.FF666666));
        } else {
            holder.likeImg.setImageResource(R.mipmap.item_like_pre);
            holder.likeTv.setTextColor(context.getResources().getColor(R.color.FFDE5C51));
        }
        holder.attentionImg.setBackgroundResource(R.drawable.rectangle_common_no_attention);
        holder.attentionImg.setText("已收藏");
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClickUtil.onClick()) {
                    Intent intent = new Intent(context, VideoDetailActivity.class);
                    intent.putExtra("articleid", homeSubitemBean.articleid);
                    context.startActivity(intent);
                }
            }
        });

        holder.attentionImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClickUtil.onClick()) {
                    listener.cancelCollect(2, homeSubitemBean.articleid, position);
                }
            }
        });

        holder.likeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClickUtil.onClick()) {
                    int is_up = homeSubitemBean.is_up;
                    if (0 == is_up) {
                        if (null != listener) {
                            listener.addLike(HttpServicePath.URL_LIKE, 2, homeSubitemBean.articleid, homeSubitemBean, holder);
                        }
                    } else {
                        if (null != listener) {
                            listener.addLike(HttpServicePath.URL_NO_LIKE, 2, homeSubitemBean.articleid, homeSubitemBean, holder);
                        }
                    }
                }
            }
        });

        holder.shareLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClickUtil.onClick()) {
                    if (null != listener) {
                        listener.addShare(position, 2);
                    }
                }
            }
        });

        holder.userLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ClickUtil.onClick()) {
                    Intent intent = new Intent(context, UserPagerActivity.class);
                    intent.putExtra("userId", homeSubitemBean.userid);
                    context.startActivity(intent);
                }
            }
        });
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

    public class NoPicViewholder extends RecyclerView.ViewHolder {
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

        public NoPicViewholder(View itemView) {
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
        @BindView(R.id.titleTv)
        TextView titleTv;
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

        public BigPicViewholder(View itemView) {
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

        public RightPicViewholder(View itemView) {
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

        public Viewholder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void setCollectItemListener(CollectItemListener collectItemListener) {
        this.listener = collectItemListener;
    }

}
