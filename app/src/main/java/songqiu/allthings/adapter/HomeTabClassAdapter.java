package songqiu.allthings.adapter;

import android.annotation.SuppressLint;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.droidsonroids.gif.GifImageView;
import songqiu.allthings.R;
import songqiu.allthings.activity.CommentWebViewActivity;
import songqiu.allthings.application.MyApplication;
import songqiu.allthings.articledetail.ArticleDetailActivity;
import songqiu.allthings.bean.HomeSubitemBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.iterface.HomeItemListener;
import songqiu.allthings.mine.userpage.UserPagerActivity;
import songqiu.allthings.util.ClickUtil;
import songqiu.allthings.util.DateUtil;
import songqiu.allthings.util.GlideCircleTransform;
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
public class HomeTabClassAdapter extends RecyclerView.Adapter {

    Context context;
    List<HomeSubitemBean> item;
    String classType;
    //type 1 图文 判断moudle  type 2 视频
    //设置常量  moudle 1、纯文字  2、大图 3、小图
    private final int TYPE_ONLY_TEXT = 1;
    private final int TYPE_BIG_PIC = 2;
    private final int TYPE_SMALL_PIC = 3;
    private final int TYPE_MORE_PIC = 6;//多图

    private final int TYPE_CITY_AND_CHOICE_VIDEO = 4; //成都、精选里的视频布局标记
    private final int TYPE_VIDEO_VIDEO = 5; //calss为视频时的视频布局标记

    //广告 推荐
    private final int TYPE_ADVERTISE_BIG_PIC = 7; //广告大图
    private final int TYPE_ADVERTISE_VIDEO = 8; //广告视频
    private final int TYPE_ADVERTISE_SAMLL_PIC = 9; //广告小图

    //广告 视频
    private final int TYPE_BIG_PIC_ADVERTISE = 10; //广告大图
    private final int TYPE_VIDEO_ADVERTISE = 11; //广告视频

    HomeItemListener homeItemListener;

    public HomeTabClassAdapter(Context context, List<HomeSubitemBean> item, String classType) {
        this.context = context;
        this.item = item;
        this.classType = classType;
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
        } else if (viewType == TYPE_ADVERTISE_BIG_PIC) { //广告大图
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_advertising_big_pic, null);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(lp);
            return new AdvertiseBigPicViewholder(view);
        } else if (viewType == TYPE_ADVERTISE_VIDEO) { //广告视频
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_advertising_video, null);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(lp);
            return new AdvertiseVideoViewholder(view);
        } else if (viewType == TYPE_ADVERTISE_SAMLL_PIC) { //广告小图
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_advertising_small_pic, null);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(lp);
            return new AdvertiseRightPicViewholder(view);
        } else if (viewType == TYPE_BIG_PIC_ADVERTISE) { //视频 大图广告
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_big_pic_advertise, null);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(lp);
            return new BigPicAdvertiseVideoHolder(view);
        } else if (viewType == TYPE_VIDEO_ADVERTISE) { //视频 视频广告
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_video_advertise, null);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(lp);
            return new VideoAdvertiseVideoHolder(view);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        if (classType.equals("recommend") || classType.equals("ghost")) { //推荐  鬼话
            if (1 == item.get(position).ad) { //广告
                if (1 == item.get(position).type) { //图片广告
                    if (3 == item.get(position).change_type) { //小图片广告
                        return TYPE_ADVERTISE_SAMLL_PIC;
                    } else { //大图片广告
                        return TYPE_ADVERTISE_BIG_PIC;
                    }
                } else { //视频广告
                    return TYPE_ADVERTISE_VIDEO;
                }
            } else {
                if (item.get(position).type == 1) { //推荐下的文章
                    if (item.get(position).module == 1) {
                        return TYPE_ONLY_TEXT;
                    } else if (item.get(position).module == 2) {
                        return TYPE_BIG_PIC;
                    } else if (item.get(position).module == 3) {
                        return TYPE_SMALL_PIC;
                    } else if (item.get(position).module == 4) { //多图
                        return TYPE_MORE_PIC;
                    }
                } else if (item.get(position).type == 2) { //推荐下的视频
                    return TYPE_CITY_AND_CHOICE_VIDEO;
                }
            }
        } else if (classType.equals("home_vintro")) { //class为视频
            if (1 == item.get(position).ad) { //广告
                if (1 == item.get(position).type) { //图片广告
                    return TYPE_BIG_PIC_ADVERTISE;
                } else { //视频广告
                    return TYPE_VIDEO_ADVERTISE;
                }
            } else {
                return TYPE_VIDEO_VIDEO;
            }
        } else {
            if (item.get(position).type == 2) { //视频
                return TYPE_CITY_AND_CHOICE_VIDEO;
            } else { //非视频
                if (item.get(position).module == 1) {
                    return TYPE_ONLY_TEXT;
                } else if (item.get(position).module == 2) {
                    return TYPE_BIG_PIC;
                } else if (item.get(position).module == 3) {
                    return TYPE_SMALL_PIC;
                }
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
        } else if (holder instanceof AdvertiseBigPicViewholder) {
            setAdvertiseBigPic((AdvertiseBigPicViewholder) holder, position);
        } else if (holder instanceof AdvertiseVideoViewholder) {
            setAdvertiseVideo((AdvertiseVideoViewholder) holder, position);
        } else if (holder instanceof AdvertiseRightPicViewholder) {
            setAdvertiseRightPic((AdvertiseRightPicViewholder) holder, position);
        } else if (holder instanceof VideoAdvertiseVideoHolder) {
            setVideoAdvertise((VideoAdvertiseVideoHolder) holder, position);
        } else if (holder instanceof BigPicAdvertiseVideoHolder) {
            setBigPicAdvertise((BigPicAdvertiseVideoHolder) holder, position);
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
        //标签
        if(!StringUtil.isEmpty(item.get(position).keywords)) {
            holder.keywordsTv.setText(item.get(position).keywords);
            int colorIndex = item.get(position).color;
            holder.keywordsTv.setTextColor(context.getResources().getColor(MyApplication.getInstance().colorTextViewMap.get(colorIndex)));
            holder.keywordsTv.setBackgroundResource(MyApplication.getInstance().colorBackGroundMap.get(colorIndex));
        }else {
            holder.keywordsTv.setVisibility(View.GONE);
        }
        //多标签
        if(null != item.get(position).labels && 0!=item.get(position).labels.length) {
            if(1==item.get(position).labels.length) {
                holder.labels1.setVisibility(View.VISIBLE);
                holder.labels1.setText(item.get(position).labels[0]);
                holder.labels2.setVisibility(View.GONE);
            }else {
                holder.labels1.setVisibility(View.VISIBLE);
                holder.labels1.setText(item.get(position).labels[0]);
                holder.labels2.setVisibility(View.VISIBLE);
                holder.labels2.setText(item.get(position).labels[1]);
            }
        }else {
            holder.labels1.setVisibility(View.GONE);
            holder.labels2.setVisibility(View.GONE);
        }
        holder.collectTv.setText(ShowNumUtil.showUnm(item.get(position).collect_num));
        if(0!=item.get(position).popular_icon) {
            holder.hotTv.setVisibility(View.VISIBLE);
        }else {
            holder.hotTv.setVisibility(View.GONE);
        }
        if(0!=item.get(position).push_icon) {
            holder.recommendTv.setVisibility(View.VISIBLE);
        }else {
            holder.recommendTv.setVisibility(View.GONE);
        }
        if(0!=item.get(position).new_icon) {
            holder.newTv.setVisibility(View.VISIBLE);
        }else {
            holder.newTv.setVisibility(View.GONE);
        }
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
                if(ClickUtil.onClick()) {
                    homeItemListener.delete(position,1);
                }
            }
        });

        if (1 < item.get(position).ranklist) {
            holder.setTopTv.setVisibility(View.VISIBLE);
            holder.deleteImg.setVisibility(View.GONE);
        }else {
            holder.setTopTv.setVisibility(View.GONE);
        }
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
        //标签
        if(!StringUtil.isEmpty(item.get(position).keywords)) {
            holder.keywordsTv.setText(item.get(position).keywords);
            int colorIndex = item.get(position).color;
            holder.keywordsTv.setTextColor(context.getResources().getColor(MyApplication.getInstance().colorTextViewMap.get(colorIndex)));
            holder.keywordsTv.setBackgroundResource(MyApplication.getInstance().colorBackGroundMap.get(colorIndex));
        }else {
            holder.keywordsTv.setVisibility(View.GONE);
        }
        //多标签
        if(null != item.get(position).labels && 0!=item.get(position).labels.length) {
            if(1==item.get(position).labels.length) {
                holder.labels1.setVisibility(View.VISIBLE);
                holder.labels1.setText(item.get(position).labels[0]);
                holder.labels2.setVisibility(View.GONE);
            }else {
                holder.labels1.setVisibility(View.VISIBLE);
                holder.labels1.setText(item.get(position).labels[0]);
                holder.labels2.setVisibility(View.VISIBLE);
                holder.labels2.setText(item.get(position).labels[1]);
            }
        }else {
            holder.labels1.setVisibility(View.GONE);
            holder.labels2.setVisibility(View.GONE);
        }

        holder.collectTv.setText(ShowNumUtil.showUnm(item.get(position).collect_num));
        if(0!=item.get(position).popular_icon) {
            holder.hotTv.setVisibility(View.VISIBLE);
        }else {
            holder.hotTv.setVisibility(View.GONE);
        }
        if(0!=item.get(position).push_icon) {
            holder.recommendTv.setVisibility(View.VISIBLE);
        }else {
            holder.recommendTv.setVisibility(View.GONE);
        }
        if(0!=item.get(position).new_icon) {
            holder.newTv.setVisibility(View.VISIBLE);
        }else {
            holder.newTv.setVisibility(View.GONE);
        }
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
                if(ClickUtil.onClick()) {
                    homeItemListener.delete(position,1);
                }
            }
        });
        if (1 < item.get(position).ranklist) {
            holder.setTopTv.setVisibility(View.VISIBLE);
            holder.deleteImg.setVisibility(View.GONE);
        }else {
            holder.setTopTv.setVisibility(View.GONE);
        }
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
        //标签
        if(!StringUtil.isEmpty(item.get(position).keywords)) {
            holder.keywordsTv.setText(item.get(position).keywords);
            int colorIndex = item.get(position).color;
            holder.keywordsTv.setTextColor(context.getResources().getColor(MyApplication.getInstance().colorTextViewMap.get(colorIndex)));
            holder.keywordsTv.setBackgroundResource(MyApplication.getInstance().colorBackGroundMap.get(colorIndex));
        }else {
            holder.keywordsTv.setVisibility(View.GONE);
        }
        //多标签
        if(null != item.get(position).labels && 0!=item.get(position).labels.length) {
            if(1==item.get(position).labels.length) {
                holder.labels1.setVisibility(View.VISIBLE);
                holder.labels1.setText(item.get(position).labels[0]);
                holder.labels2.setVisibility(View.GONE);
            }else {
                holder.labels1.setVisibility(View.VISIBLE);
                holder.labels1.setText(item.get(position).labels[0]);
                holder.labels2.setVisibility(View.VISIBLE);
                holder.labels2.setText(item.get(position).labels[1]);
            }
        }else {
            holder.labels1.setVisibility(View.GONE);
            holder.labels2.setVisibility(View.GONE);
        }
        holder.collectTv.setText(ShowNumUtil.showUnm(item.get(position).collect_num));
        if(0!=item.get(position).popular_icon) {
            holder.hotTv.setVisibility(View.VISIBLE);
        }else {
            holder.hotTv.setVisibility(View.GONE);
        }
        if(0!=item.get(position).push_icon) {
            holder.recommendTv.setVisibility(View.VISIBLE);
        }else {
            holder.recommendTv.setVisibility(View.GONE);
        }
        if(0!=item.get(position).new_icon) {
            holder.newTv.setVisibility(View.VISIBLE);
        }else {
            holder.newTv.setVisibility(View.GONE);
        }

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
                if(ClickUtil.onClick()) {
                    homeItemListener.delete(position,1);
                }
            }
        });
        if (1 < item.get(position).ranklist) {
            holder.setTopTv.setVisibility(View.VISIBLE);
            holder.deleteImg.setVisibility(View.GONE);
        }else {
            holder.setTopTv.setVisibility(View.GONE);
        }
    }


    public void setMorePicData(MorePicViewholder holder, int position) {
        holder.titleTv.setText(item.get(position).title);
        holder.userName.setText(item.get(position).user_nickname);
        //标签
        if(!StringUtil.isEmpty(item.get(position).keywords)) {
            holder.keywordsTv.setText(item.get(position).keywords);
            int colorIndex = item.get(position).color;
            holder.keywordsTv.setTextColor(context.getResources().getColor(MyApplication.getInstance().colorTextViewMap.get(colorIndex)));
            holder.keywordsTv.setBackgroundResource(MyApplication.getInstance().colorBackGroundMap.get(colorIndex));
        }else {
            holder.keywordsTv.setVisibility(View.GONE);
        }
        //多标签
        if(null != item.get(position).labels && 0!=item.get(position).labels.length) {
            if(1==item.get(position).labels.length) {
                holder.labels1.setVisibility(View.VISIBLE);
                holder.labels1.setText(item.get(position).labels[0]);
                holder.labels2.setVisibility(View.GONE);
            }else {
                holder.labels1.setVisibility(View.VISIBLE);
                holder.labels1.setText(item.get(position).labels[0]);
                holder.labels2.setVisibility(View.VISIBLE);
                holder.labels2.setText(item.get(position).labels[1]);
            }
        }else {
            holder.labels1.setVisibility(View.GONE);
            holder.labels2.setVisibility(View.GONE);
        }
        holder.collectTv.setText(ShowNumUtil.showUnm(item.get(position).collect_num));
        if(0!=item.get(position).popular_icon) {
            holder.hotTv.setVisibility(View.VISIBLE);
        }else {
            holder.hotTv.setVisibility(View.GONE);
        }
        if(0!=item.get(position).push_icon) {
            holder.recommendTv.setVisibility(View.VISIBLE);
        }else {
            holder.recommendTv.setVisibility(View.GONE);
        }
        if(0!=item.get(position).new_icon) {
            holder.newTv.setVisibility(View.VISIBLE);
        }else {
            holder.newTv.setVisibility(View.GONE);
        }

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
        holder.deleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ClickUtil.onClick()) {
                    homeItemListener.delete(position,1);
                }
            }
        });
        if (1 < item.get(position).ranklist) {
            holder.setTopTv.setVisibility(View.VISIBLE);
            holder.deleteImg.setVisibility(View.GONE);
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
        holder.collectTv.setText(ShowNumUtil.showUnm(item.get(position).collect_num));

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
            HeartVideoInfo info = HeartVideoInfo.Builder().setTitle("").setPath(item.get(position).source_url)
                    .setImagePath(item.get(position).photo).setSaveProgress(false).setVideoId(item.get(position).articleid).builder();
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
                if(ClickUtil.onClick()) {
                    homeItemListener.delete(position,1);
                }
            }
        });

        if (1 < item.get(position).ranklist) {
            holder.setTopTv.setVisibility(View.VISIBLE);
            holder.deleteImg.setVisibility(View.GONE);
        }else {
            holder.setTopTv.setVisibility(View.GONE);
        }

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
            HeartVideoInfo info = HeartVideoInfo.Builder().setTitle(item.get(position).title).setPath(item.get(position).video_url)
                    .setImagePath(item.get(position).photo).setSaveProgress(false).setVideoId(item.get(position).articleid).builder();
            VideoControl control = new VideoControl(context);
            control.setInfo(info);
            holder.videoView.setHeartVideoContent(control);
        }

        holder.userName.setText(item.get(position).user_nickname);
        holder.likeTv.setText(item.get(position).up_num + "");
        if (1 < item.get(position).ranklist) {
            holder.setTopTv.setVisibility(View.VISIBLE);
        }else {
            holder.setTopTv.setVisibility(View.GONE);
        }

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
                if(ClickUtil.onClick()) {
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
            }
        });
        //点点点
        holder.settingImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ClickUtil.onClick()) {
                    homeItemListener.addSetting(position);
                }
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
        //去个人中心
        holder.userLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ClickUtil.onClick()) {
                    Intent intent = new Intent(context, UserPagerActivity.class);
                    intent.putExtra("userId", item.get(position).userid);
                    context.startActivity(intent);
                }
            }
        });
    }


    public void setAdvertiseBigPic(AdvertiseBigPicViewholder holder, int position) {
        RequestOptions options1 = new RequestOptions()
                .error(R.mipmap.pic_default)
                .placeholder(R.mipmap.pic_default);
        holder.bigPicImg.post(new Runnable() {
            @Override
            public void run() {
                holder.bigPicImg.setLayoutParams(ViewProportion.getLinearParams(holder.bigPicImg, 1.7));
            }
        });

        if (!StringUtil.isEmpty(item.get(position).url)) {
            if (!item.get(position).url.contains("http")) {
                item.get(position).url = HttpServicePath.BasePicUrl + item.get(position).url;
            }
        }
        Glide.with(context).load(item.get(position).url.replaceAll("\"","")).apply(options1).into(holder.bigPicImg);
        holder.titleTv.setText(item.get(position).title);
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
        holder.deleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ClickUtil.onClick()) {
                    homeItemListener.delete(position,2);
                }
            }
        });
    }

    public void setAdvertiseVideo(AdvertiseVideoViewholder holder, int position) {
        holder.videoView.post(new Runnable() {
            @Override
            public void run() {
                holder.videoView.setLayoutParams(ViewProportion.getLinearParams(holder.videoView, 1.7));
            }
        });

        String path = item.get(position).video_url;//
        if (!StringUtil.isEmpty(item.get(position).url)) {
            if (!item.get(position).url.contains("http")) {
                item.get(position).url = HttpServicePath.BasePicUrl + item.get(position).url;
            }
        }
        String image = item.get(position).url;
        HeartVideoInfo info = HeartVideoInfo.Builder().setTitle("").setPath(path).setImagePath(image).setSaveProgress(false).builder();
        VideoControl control = new VideoControl(context);
        control.setInfo(info);
        holder.videoView.setHeartVideoContent(control);

        holder.titleTv.setText(item.get(position).title);
        if (4 == item.get(position).change_type) {
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
        holder.deleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ClickUtil.onClick()) {
                    homeItemListener.delete(position,2);
                }
            }
        });
    }


    public void setAdvertiseRightPic(AdvertiseRightPicViewholder holder, int position) {
        RequestOptions options1 = new RequestOptions()
                .error(R.mipmap.pic_default)
                .placeholder(R.mipmap.pic_default);

        if (!StringUtil.isEmpty(item.get(position).url)) {
            if (!item.get(position).url.contains("http")) {
                item.get(position).url = HttpServicePath.BasePicUrl + item.get(position).url;
            }
        }
        Glide.with(context).load(item.get(position).url.replaceAll("\"","")).apply(options1).into(holder.rightPicImg);
        holder.titleTv.setText(item.get(position).title);
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
        holder.deleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ClickUtil.onClick()) {
                    homeItemListener.delete(position,2);
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
                    homeItemListener.delete(position,2);
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
        holder.titleTv.setText(item.get(position).title);
        //图片
        RequestOptions options1 = new RequestOptions()
                .error(R.mipmap.pic_default)
                .placeholder(R.mipmap.pic_default);
        if (!StringUtil.isEmpty(item.get(position).url)) {
            if (!item.get(position).url.contains("http")) {
                item.get(position).url = HttpServicePath.BasePicUrl + item.get(position).url;
            }
        }
        Glide.with(context).load(item.get(position).url.replaceAll("\"","")).apply(options1).into(holder.bigPicImg);

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
                    homeItemListener.delete(position,2);
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

        if(holder instanceof AdvertiseVideoViewholder) {
            HeartVideo heartVideo = ((AdvertiseVideoViewholder) holder).videoView;
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
        @BindView(R.id.collectTv)
        TextView collectTv;
        @BindView(R.id.hotTv)
        TextView hotTv;
        @BindView(R.id.recommendTv)
        TextView recommendTv;
        @BindView(R.id.newTv)
        TextView newTv;
        //标签
        @BindView(R.id.keywordsTv)
        TextView keywordsTv;
        @BindView(R.id.labels1)
        TextView labels1;
        @BindView(R.id.labels2)
        TextView labels2;

        public MorePicViewholder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
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
        @BindView(R.id.setTopTv)
        TextView setTopTv;
        @BindView(R.id.collectTv)
        TextView collectTv;
        @BindView(R.id.hotTv)
        TextView hotTv;
        @BindView(R.id.recommendTv)
        TextView recommendTv;
        @BindView(R.id.newTv)
        TextView newTv;
        //标签
        @BindView(R.id.keywordsTv)
        TextView keywordsTv;
        @BindView(R.id.labels1)
        TextView labels1;
        @BindView(R.id.labels2)
        TextView labels2;

        public RightPicViewholder(View itemView) {
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
        @BindView(R.id.setTopTv)
        TextView setTopTv;
        @BindView(R.id.collectTv)
        TextView collectTv;
        @BindView(R.id.hotTv)
        TextView hotTv;
        @BindView(R.id.recommendTv)
        TextView recommendTv;
        @BindView(R.id.newTv)
        TextView newTv;
        //标签
        @BindView(R.id.keywordsTv)
        TextView keywordsTv;
        @BindView(R.id.labels1)
        TextView labels1;
        @BindView(R.id.labels2)
        TextView labels2;

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
        @BindView(R.id.setTopTv)
        TextView setTopTv;
        @BindView(R.id.collectTv)
        TextView collectTv;
        @BindView(R.id.hotTv)
        TextView hotTv;
        @BindView(R.id.recommendTv)
        TextView recommendTv;
        @BindView(R.id.newTv)
        TextView newTv;
        //标签
        @BindView(R.id.keywordsTv)
        TextView keywordsTv;
        @BindView(R.id.labels1)
        TextView labels1;
        @BindView(R.id.labels2)
        TextView labels2;


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
        @BindView(R.id.setTopTv)
        TextView setTopTv;
        @BindView(R.id.collectTv)
        TextView collectTv;

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
        public TextView likeTv;
        @BindView(R.id.toDetailLayout)
        RelativeLayout toDetailLayout;
        @BindView(R.id.likeLayout)
        LinearLayout likeLayout;
        @BindView(R.id.setTopTv)
        TextView setTopTv;

        @BindView(R.id.attentionTv)
        TextView attentionTv;
        @BindView(R.id.likeImg)
        public ImageView likeImg;
        @BindView(R.id.settingImg)
        ImageView settingImg;
        @BindView(R.id.userLayout)
        LinearLayout userLayout;

        public LookVideoHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class AdvertiseBigPicViewholder extends RecyclerView.ViewHolder {
        @BindView(R.id.titleTv)
        TextView titleTv;
        @BindView(R.id.bigPicImg)
        GifImageView bigPicImg;
        @BindView(R.id.downloadLayout)
        LinearLayout downloadLayout;
        @BindView(R.id.deleteImg)
        ImageView deleteImg;
        @BindView(R.id.layout)
        LinearLayout layout;

        public AdvertiseBigPicViewholder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class AdvertiseVideoViewholder extends RecyclerView.ViewHolder {
        @BindView(R.id.titleTv)
        TextView titleTv;
        @BindView(R.id.videoView)
        HeartVideo videoView;
        @BindView(R.id.downloadLayout)
        LinearLayout downloadLayout;
        @BindView(R.id.deleteImg)
        ImageView deleteImg;
        @BindView(R.id.layout)
        LinearLayout layout;

        public AdvertiseVideoViewholder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class AdvertiseRightPicViewholder extends RecyclerView.ViewHolder {
        @BindView(R.id.titleTv)
        TextView titleTv;
        @BindView(R.id.deleteImg)
        ImageView deleteImg;
        @BindView(R.id.rightPicImg)
        GifImageView rightPicImg;
        @BindView(R.id.layout)
        LinearLayout layout;

        public AdvertiseRightPicViewholder(View itemView) {
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


    public void setHomeItemListener(HomeItemListener homeItemListener) {
        this.homeItemListener = homeItemListener;
    }
}
