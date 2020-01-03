package songqiu.allthings.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
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

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import songqiu.allthings.R;
import songqiu.allthings.bean.HotGambitCommonBean;
import songqiu.allthings.home.gambit.GambitDetailActivity;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.iterface.GambitItemListener;
import songqiu.allthings.iterface.PhotoViewListener;
import songqiu.allthings.mine.userpage.UserPagerActivity;
import songqiu.allthings.util.ClickUtil;
import songqiu.allthings.util.DateUtil;
import songqiu.allthings.util.GlideCircleTransform;
import songqiu.allthings.util.ImageResUtils;
import songqiu.allthings.util.SharedPreferencedUtils;
import songqiu.allthings.util.ShowNumUtil;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.view.GridViewInScroll;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/9/18
 *
 *类描述：
 *
 ********/
public class GambitHotAdapter extends RecyclerView.Adapter {

    Context context;
    List<HotGambitCommonBean> item;
    //0、无图 1、大图  2、多图
    private final int TYPE_NO_PIC = 0;
    private final int TYPE_BIG_PIC = 1;
    private final int TYPE_MORE_PIC = 2;
    GambitItemListener gambitItemListener;
    PhotoViewListener photoViewListener;

    public GambitHotAdapter(Context context, List<HotGambitCommonBean> item) {
        this.context = context;
        this.item = item;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == TYPE_NO_PIC) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_gambit_no_pic, null);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(lp);
            return new NoPicViewholder(view);
        } else if (viewType == TYPE_BIG_PIC) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_gambit_big_pic, null);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(lp);
            return new BigPicViewholder(view);

        } else if (viewType == TYPE_MORE_PIC) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_gambit_more_pic, null);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(lp);
            return new MoreViewholder(view);

        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        if (item.get(position).num == 0) {
            return TYPE_NO_PIC;
        } else if (item.get(position).num == 1) {
            return TYPE_BIG_PIC;
        } else {
            return TYPE_MORE_PIC;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof NoPicViewholder) {
            setNoPicData((NoPicViewholder) holder, position);
        } else if (holder instanceof BigPicViewholder) {
            setBigPicData((BigPicViewholder) holder, position);
        } else if (holder instanceof MoreViewholder) {
            setMorePicData((MoreViewholder) holder, position);
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
        holder.ivLevel.setImageResource(ImageResUtils.getLevelRes(item.get(position).level));
        holder.userName.setText(item.get(position).user_nickname);
        //变色
        if (item.get(position).descriptions.contains("#")) {
            int startIndex = item.get(position).descriptions.indexOf("#");
            //根据第一个#的位置 获得第二个点的位置
            int endIndex = item.get(position).descriptions.lastIndexOf("#");
            if(startIndex != endIndex) {
                SpannableString spannableString = new SpannableString(item.get(position).descriptions);
                spannableString.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.normal_color)), startIndex, endIndex + 1, 0);
                holder.contentTv.setText(spannableString);
            }else {
                holder.contentTv.setText(item.get(position).descriptions);
            }
        } else {
            holder.contentTv.setText(item.get(position).descriptions);
        }
        long time = item.get(position).created * 1000;
        holder.timeTv.setText(DateUtil.fromToday(new Date(time))+ImageResUtils.getLevelText(item.get(position).level));
        if (0 == item.get(position).is_follow) { //未关注
            holder.attentionTv.setText("关注");
            holder.attentionTv.setBackgroundResource(R.drawable.rectangle_common_attention);
        } else {
            holder.attentionTv.setText("已关注");
            holder.attentionTv.setBackgroundResource(R.drawable.rectangle_common_no_attention);
        }
        //点赞 评论数
        holder.likeTv.setText(ShowNumUtil.showUnm(item.get(position).up_num));
        holder.commentTv.setText(ShowNumUtil.showUnm(item.get(position).comment_num));
        if (0 == item.get(position).is_up) {
            holder.likeImg.setImageResource(R.mipmap.item_like);
            holder.likeTv.setTextColor(context.getResources().getColor(R.color.FF666666));
        } else {
            holder.likeImg.setImageResource(R.mipmap.item_like_pre);
            holder.likeTv.setTextColor(context.getResources().getColor(R.color.FFDE5C51));
        }
        //如果是自己显示删除按钮，别人则显示关注 举报
        int mUserId = SharedPreferencedUtils.getInteger(context, "SYSUSERID", 0);
        if(item.get(position).userid == mUserId) { //自己
            holder.otherLayout.setVisibility(View.GONE);
            holder.myDeleteTv.setVisibility(View.VISIBLE);
        }else {
            holder.otherLayout.setVisibility(View.VISIBLE);
            holder.myDeleteTv.setVisibility(View.GONE);
        }
        holder.shareTv.setText(String.valueOf(item.get(position).share_num));

        //点赞、取消点赞
        holder.likeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ClickUtil.onClick()) {
                    if(null != item && 0 != item.size()) {
                        if (0 == item.get(position).is_up) {//去点赞
                            gambitItemListener.addLike(HttpServicePath.URL_LIKE, 3, item.get(position).id,holder);
                        } else {//取消点赞
                            gambitItemListener.addLike(HttpServicePath.URL_NO_LIKE, 3, item.get(position).id,holder);
                        }
                    }
                }
            }
        });
        //关注、取消关注
        holder.attentionTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ClickUtil.onClick()) {
                    if(null != item && 0 != item.size()) {
                        if (0 == item.get(position).is_follow) { //去关注
                            gambitItemListener.addFollow(item.get(position).userid, 1,holder);
                        } else { //取消关注
                            gambitItemListener.addFollow(item.get(position).userid, 2,holder);
                        }
                    }
                }
            }
        });
        //分享
        holder.shareLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ClickUtil.onClick()) {
                    gambitItemListener.addShare(position);
                }
            }
        });
        //举报
        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gambitItemListener.delete(2, item.get(position).id,item.get(position).userid);
            }
        });
        //删除
        holder.myDeleteTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gambitItemListener.delete(1, item.get(position).id,item.get(position).userid);
            }
        });
        //去主页
        holder.userlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(null != item && 0 != item.size()) {
                    Intent intent = new Intent(context, UserPagerActivity.class);
                    intent.putExtra("userId", item.get(position).userid);
                    context.startActivity(intent);
                }
            }
        });
        //去详情
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(null != item && 0 != item.size()) {
                    Intent intent = new Intent(context, GambitDetailActivity.class);
                    intent.putExtra("talkid",item.get(position).id);
                    context.startActivity(intent);
                }
            }
        });
    }

    public void setBigPicData(BigPicViewholder holder, int position) {
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
        holder.ivLevel.setImageResource(ImageResUtils.getLevelRes(item.get(position).level));
        RequestOptions options1 = new RequestOptions()
//                .circleCrop().transforms(new GlideCircleTransform(context))
                .error(R.mipmap.pic_default_zhengfangxing)
                .placeholder(R.mipmap.pic_default_zhengfangxing);
        if (null != item.get(position).images && !StringUtil.isEmpty(item.get(position).images[0])) {
            if (!item.get(position).images[0].contains("http")) {
                item.get(position).images[0] = HttpServicePath.BasePicUrl + item.get(position).images[0];
            }
        }
        Glide.with(context).load(item.get(position).images[0]).apply(options1).into(holder.bigPicImg);

        holder.userName.setText(item.get(position).user_nickname);
        //变色
        if (item.get(position).descriptions.contains("#")) {
            int startIndex = item.get(position).descriptions.indexOf("#");
            //根据第一个#的位置 获得第二个点的位置
            int endIndex = item.get(position).descriptions.lastIndexOf("#");
            if(startIndex != endIndex) {
                SpannableString spannableString = new SpannableString(item.get(position).descriptions);
                spannableString.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.normal_color)), startIndex, endIndex + 1, 0);
                holder.contentTv.setText(spannableString);
            }else {
                holder.contentTv.setText(item.get(position).descriptions);
            }
        } else {
            holder.contentTv.setText(item.get(position).descriptions);
        }
        long time = item.get(position).created * 1000;
        holder.timeTv.setText(DateUtil.fromToday(new Date(time))+ImageResUtils.getLevelText(item.get(position).level));
        if (0 == item.get(position).is_follow) { //未关注
            holder.attentionTv.setText("关注");
            holder.attentionTv.setBackgroundResource(R.drawable.rectangle_common_attention);
        } else {
            holder.attentionTv.setText("已关注");
            holder.attentionTv.setBackgroundResource(R.drawable.rectangle_common_no_attention);
        }
        //点赞 评论数
        holder.likeTv.setText(ShowNumUtil.showUnm(item.get(position).up_num));
        holder.commentTv.setText(ShowNumUtil.showUnm(item.get(position).comment_num));
        if (0 == item.get(position).is_up) {
            holder.likeImg.setImageResource(R.mipmap.item_like);
            holder.likeTv.setTextColor(context.getResources().getColor(R.color.FF666666));
        } else {
            holder.likeImg.setImageResource(R.mipmap.item_like_pre);
            holder.likeTv.setTextColor(context.getResources().getColor(R.color.FFDE5C51));
        }
        //如果是自己显示删除按钮，别人则显示关注 举报
        int mUserId = SharedPreferencedUtils.getInteger(context, "SYSUSERID", 0);
        if(item.get(position).userid == mUserId) { //自己
            holder.otherLayout.setVisibility(View.GONE);
            holder.myDeleteTv.setVisibility(View.VISIBLE);
        }else {
            holder.otherLayout.setVisibility(View.VISIBLE);
            holder.myDeleteTv.setVisibility(View.GONE);
        }
        holder.shareTv.setText(String.valueOf(item.get(position).share_num));

        //点赞、取消点赞
        holder.likeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ClickUtil.onClick()) {
                    if(null != item && 0 != item.size()) {
                        if (0 == item.get(position).is_up) {//去点赞
                            gambitItemListener.addLike(HttpServicePath.URL_LIKE, 3, item.get(position).id,holder);
                        } else {//取消点赞
                            gambitItemListener.addLike(HttpServicePath.URL_NO_LIKE, 3, item.get(position).id,holder);
                        }
                    }
                }
            }
        });
        //关注、取消关注
        holder.attentionTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ClickUtil.onClick()) {
                    if(null != item && 0 != item.size()) {
                        if (0 == item.get(position).is_follow) { //去关注
                            gambitItemListener.addFollow(item.get(position).userid, 1,holder);
                        } else { //取消关注
                            gambitItemListener.addFollow(item.get(position).userid, 2,holder);
                        }
                    }
                }
            }
        });

        //分享
        holder.shareLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ClickUtil.onClick()) {
                    gambitItemListener.addShare(position);
                }
            }
        });

        //举报
        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gambitItemListener.delete(2, item.get(position).id,item.get(position).userid);
            }
        });
        //删除
        holder.myDeleteTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gambitItemListener.delete(1, item.get(position).id,item.get(position).userid);
            }
        });
        //去主页
        holder.userlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(null != item && 0 != item.size()) {
                    Intent intent = new Intent(context, UserPagerActivity.class);
                    intent.putExtra("userId", item.get(position).userid);
                    context.startActivity(intent);
                }
            }
        });
        //去详情
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(null != item && 0 != item.size()) {
                    Intent intent = new Intent(context, GambitDetailActivity.class);
                    intent.putExtra("talkid",item.get(position).id);
                    context.startActivity(intent);
                }
            }
        });

        holder.bigPicImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ClickUtil.onClick()) {
                    photoViewListener.toPhotoView(position,0);
                }
            }
        });
    }


    public void setMorePicData(MoreViewholder holder, int position) {
        RequestOptions options = new RequestOptions()
                .circleCrop().transforms(new GlideCircleTransform(context))
                .error(R.mipmap.head_default)
                .placeholder(R.mipmap.head_default);
        if (!StringUtil.isEmpty(item.get(position).avatar)) {
            if (!item.get(position).avatar.contains("http")) {
                item.get(position).avatar = HttpServicePath.BasePicUrl + item.get(position).avatar;
            }
        }
        holder.ivLevel.setImageResource(ImageResUtils.getLevelRes(item.get(position).level));
        Glide.with(context).load(item.get(position).avatar).apply(options).into(holder.userIcon);
        GambitMorePicAdapter gambitMorePicAdapter = new GambitMorePicAdapter(context,item.get(position).images);
        holder.gridView.setAdapter(gambitMorePicAdapter);
        holder.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(ClickUtil.onClick()) {
                    photoViewListener.toPhotoView(position,i);
                }
            }
        });

        holder.userName.setText(item.get(position).user_nickname);
        //变色
        if (item.get(position).descriptions.contains("#")) {
            int startIndex = item.get(position).descriptions.indexOf("#");
            //根据第一个#的位置 获得第二个点的位置
            int endIndex = item.get(position).descriptions.lastIndexOf("#");
            if(startIndex != endIndex) {
                SpannableString spannableString = new SpannableString(item.get(position).descriptions);
                spannableString.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.normal_color)), startIndex, endIndex + 1, 0);
                holder.contentTv.setText(spannableString);
            }else {
                holder.contentTv.setText(item.get(position).descriptions);
            }
        } else {
            holder.contentTv.setText(item.get(position).descriptions);
        }
        long time = item.get(position).created * 1000;
        holder.timeTv.setText(DateUtil.fromToday(new Date(time))+ImageResUtils.getLevelText(item.get(position).level));
        if (0 == item.get(position).is_follow) { //未关注
            holder.attentionTv.setText("关注");
            holder.attentionTv.setBackgroundResource(R.drawable.rectangle_common_attention);
        } else {
            holder.attentionTv.setText("已关注");
            holder.attentionTv.setBackgroundResource(R.drawable.rectangle_common_no_attention);
        }
        //点赞 评论数
        holder.likeTv.setText(ShowNumUtil.showUnm(item.get(position).up_num));
        holder.commentTv.setText(ShowNumUtil.showUnm(item.get(position).comment_num));
        if (0 == item.get(position).is_up) {
            holder.likeImg.setImageResource(R.mipmap.item_like);
            holder.likeTv.setTextColor(context.getResources().getColor(R.color.FF666666));
        } else {
            holder.likeImg.setImageResource(R.mipmap.item_like_pre);
            holder.likeTv.setTextColor(context.getResources().getColor(R.color.FFDE5C51));
        }
        //如果是自己显示删除按钮，别人则显示关注 举报
        int mUserId = SharedPreferencedUtils.getInteger(context, "SYSUSERID", 0);
        if(item.get(position).userid == mUserId) { //自己
            holder.otherLayout.setVisibility(View.GONE);
            holder.myDeleteTv.setVisibility(View.VISIBLE);
        }else {
            holder.otherLayout.setVisibility(View.VISIBLE);
            holder.myDeleteTv.setVisibility(View.GONE);
        }
        holder.shareTv.setText(String.valueOf(item.get(position).share_num));
        //点赞、取消点赞
        holder.likeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ClickUtil.onClick()) {
                    if(null != item && 0 != item.size()) {
                        if (0 == item.get(position).is_up) {//去点赞
                            gambitItemListener.addLike(HttpServicePath.URL_LIKE, 3, item.get(position).id,holder);
                        } else {//取消点赞
                            gambitItemListener.addLike(HttpServicePath.URL_NO_LIKE, 3, item.get(position).id,holder);
                        }
                    }
                }
            }
        });
        //关注、取消关注
        holder.attentionTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ClickUtil.onClick()) {
                    if(null != item && 0 != item.size()) {
                        if (0 == item.get(position).is_follow) { //去关注
                            gambitItemListener.addFollow(item.get(position).userid, 1,holder);
                        } else { //取消关注
                            gambitItemListener.addFollow(item.get(position).userid, 2,holder);
                        }
                    }
                }
            }
        });
        //分享
        holder.shareLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ClickUtil.onClick()) {
                    gambitItemListener.addShare(position);
                }
            }
        });
        //举报
        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gambitItemListener.delete(2, item.get(position).id,item.get(position).userid);
            }
        });
        //删除
        holder.myDeleteTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gambitItemListener.delete(1, item.get(position).id,item.get(position).userid);
            }
        });
        //去主页
        holder.userlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(null != item && 0 != item.size()) {
                    Intent intent = new Intent(context, UserPagerActivity.class);
                    intent.putExtra("userId", item.get(position).userid);
                    context.startActivity(intent);
                }
            }
        });
        //去详情
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(null != item && 0 != item.size()) {
                    Intent intent = new Intent(context, GambitDetailActivity.class);
                    intent.putExtra("talkid",item.get(position).id);
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return item.size();
    }


    public class NoPicViewholder extends RecyclerView.ViewHolder {
        @BindView(R.id.userIcon)
        ImageView userIcon;
        @BindView(R.id.userName)
        TextView userName;
        @BindView(R.id.timeTv)
        TextView timeTv;
        @BindView(R.id.contentTv)
        TextView contentTv;
        @BindView(R.id.userlayout)
        RelativeLayout userlayout;
        @BindView(R.id.layout)
        LinearLayout layout;
        @BindView(R.id.attentionTv)
        public TextView attentionTv;
        @BindView(R.id.likeImg)
        public ImageView likeImg;
        @BindView(R.id.likeTv)
        public TextView likeTv;
        @BindView(R.id.likeLayout)
        LinearLayout likeLayout;
        @BindView(R.id.commentTv)
        TextView commentTv;
        @BindView(R.id.iv_level)
        ImageView ivLevel;
        @BindView(R.id.otherLayout)
        LinearLayout otherLayout;
        @BindView(R.id.img)
        ImageView img;
        @BindView(R.id.myDeleteTv)
        TextView myDeleteTv;
        @BindView(R.id.shareLayout)
        LinearLayout shareLayout;
        @BindView(R.id.shareTv)
        TextView shareTv;


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
        @BindView(R.id.userlayout)
        RelativeLayout userlayout;
        @BindView(R.id.attentionTv)
        public TextView attentionTv;
        @BindView(R.id.contentTv)
        TextView contentTv;
        @BindView(R.id.bigPicImg)
        ImageView bigPicImg;
        @BindView(R.id.likeImg)
        public ImageView likeImg;
        @BindView(R.id.likeTv)
        public TextView likeTv;
        @BindView(R.id.likeLayout)
        LinearLayout likeLayout;
        @BindView(R.id.commentTv)
        TextView commentTv;
        @BindView(R.id.layout)
        LinearLayout layout;
        @BindView(R.id.iv_level)
        ImageView ivLevel;
        @BindView(R.id.otherLayout)
        LinearLayout otherLayout;
        @BindView(R.id.img)
        ImageView img;
        @BindView(R.id.myDeleteTv)
        TextView myDeleteTv;
        @BindView(R.id.shareLayout)
        LinearLayout shareLayout;
        @BindView(R.id.shareTv)
        TextView shareTv;

        public BigPicViewholder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class MoreViewholder extends RecyclerView.ViewHolder {
        @BindView(R.id.userIcon)
        ImageView userIcon;
        @BindView(R.id.userName)
        TextView userName;
        @BindView(R.id.timeTv)
        TextView timeTv;
        @BindView(R.id.userlayout)
        RelativeLayout userlayout;
        @BindView(R.id.attentionTv)
        public TextView attentionTv;
        @BindView(R.id.contentTv)
        TextView contentTv;
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
        @BindView(R.id.layout)
        LinearLayout layout;
        @BindView(R.id.iv_level)
        ImageView ivLevel;
        @BindView(R.id.otherLayout)
        LinearLayout otherLayout;
        @BindView(R.id.img)
        ImageView img;
        @BindView(R.id.myDeleteTv)
        TextView myDeleteTv;
        @BindView(R.id.shareLayout)
        LinearLayout shareLayout;
        @BindView(R.id.shareTv)
        TextView shareTv;

        public MoreViewholder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    public void setGambitItemListener(GambitItemListener gambitItemListener) {
        this.gambitItemListener = gambitItemListener;
    }
    public void setPhotoViewListener(PhotoViewListener photoViewListener) {
        this.photoViewListener = photoViewListener;
    }

}
