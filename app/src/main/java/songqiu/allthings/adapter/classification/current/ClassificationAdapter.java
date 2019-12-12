package songqiu.allthings.adapter.classification.current;

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
import songqiu.allthings.adapter.ImageTextMorePicAdapter;
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
public class ClassificationAdapter extends RecyclerView.Adapter {

    Context context;
    List<HomeSubitemBean> item;

    //type 1 图文 判断moudle  type 2 视频
    //设置常量  moudle 1、纯文字  2、大图 3、小图
    private final int TYPE_ONLY_TEXT = 1;
    private final int TYPE_BIG_PIC = 2;
    private final int TYPE_SMALL_PIC = 3;
    private final int TYPE_MORE_PIC = 6;//多图


    ClassificationItemListener classificationItemListener;

    public ClassificationAdapter(Context context, List<HomeSubitemBean> item) {
        this.context = context;
        this.item = item;
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
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        if (item.get(position).module == 1) {
            return TYPE_ONLY_TEXT;
        } else if (item.get(position).module == 2) {
            return TYPE_BIG_PIC;
        } else if (item.get(position).module == 3) {
            return TYPE_SMALL_PIC;
        } else if (item.get(position).module == 4) { //多图
            return TYPE_MORE_PIC;
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
                    classificationItemListener.delete(position);
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
                    classificationItemListener.delete(position);
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
                    classificationItemListener.delete(position);
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
                    classificationItemListener.delete(position);
                }
            }
        });
        if (1 < item.get(position).ranklist) {
            holder.setTopTv.setVisibility(View.VISIBLE);
            holder.deleteImg.setVisibility(View.GONE);
        }

    }



    @Override
    public int getItemCount() {
        return item.size();
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

    public interface ClassificationItemListener {
        void delete(int position);
    }

    public void setClassificationItemListener(ClassificationItemListener classificationItemListener) {
        this.classificationItemListener = classificationItemListener;
    }
}
