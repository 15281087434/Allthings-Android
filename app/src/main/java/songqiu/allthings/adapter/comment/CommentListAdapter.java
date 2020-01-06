package songqiu.allthings.adapter.comment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import songqiu.allthings.R;
import songqiu.allthings.bean.CommentSubitemBean;
import songqiu.allthings.bean.DetailCommentListBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.iterface.VideoDetailCommentItemListener;
import songqiu.allthings.util.ClickUtil;
import songqiu.allthings.util.DateUtil;
import songqiu.allthings.util.GlideCircleTransform;
import songqiu.allthings.util.ImageResUtils;
import songqiu.allthings.util.SharedPreferencedUtils;
import songqiu.allthings.util.ShowNumUtil;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.util.theme.ThemeManager;


/**
 */

public class CommentListAdapter extends SectionedRecyclerViewAdapter<HeaderHolder, RecyclerView.ViewHolder, RecyclerView.ViewHolder> implements ThemeManager.OnThemeChangeListener{


    public List<DetailCommentListBean> item;
    private Context mContext;
    private LayoutInflater mInflater;
    VideoDetailCommentItemListener videoDetailCommentItemListener;

    private SparseBooleanArray mBooleanMap;//记录下哪个section是被打开的

    private int TYPE_NORMAL_ITEM = 1;
    private int TYPE_SHOWMORE_ITEM = 2;

    List<HeaderHolder> viewHolderList = new ArrayList<>();
    List<CommentSubitemHolder> commentSubitemHolderList = new ArrayList<>();

    public CommentListAdapter(Context context,List<DetailCommentListBean> item) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mBooleanMap = new SparseBooleanArray();
        this.item = item;
        for(int i = 0;i<item.size();i++) {
            mBooleanMap.put(i, true);
        }
        ThemeManager.registerThemeChangeListener(this);
    }

    /**
     * 一共有多少个section需要展示， 返回值是我们最外称list的大小，在我们的示例图中，
     * 对应的为热门品牌—商业区—热门景点 等，对应的数据是我们的allTagList
     *
     * @Override
     */

    protected int getSectionCount() {
        return item.size();
    }

    /**
     * 来展示content内容区域，返回值是我们需要展示多少内容，在本例中，我们超过8条数据只展示8条，
     * 点击展开后就会展示全部数据，逻辑就在这里控制。 对应数据结构为tagInfoList
     *
     * @param section
     * @return
     */
    @Override
    protected int getItemCountForSection(int section) {
        if(null != item.get(section)) {
            int  count = item.get(section).num;
            if(count<4) {
                return count;
            }else {
                return 4;
            }
        }
        return 0;
    }

    //是否有footer布局

    /**
     * 判断是否需要底部footer布局，在该例中，我们并不需要显示footer，所以默认返回false就可以，
     * 如果你对应的section需要展示footer布局，那么就在对应的section返回true就行了
     *
     * @param section
     * @return
     */
    @Override
    protected boolean hasFooterInSection(int section) {
        return false;
    }

    @Override
    protected HeaderHolder onCreateSectionHeaderViewHolder(ViewGroup parent, int viewType) {
        return new HeaderHolder(mInflater.inflate(R.layout.item_comment_list, parent, false));
    }


    @Override
    protected RecyclerView.ViewHolder onCreateSectionFooterViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    protected int getSectionItemViewType(int section, int position) {
      if(position >= 3) {
          return TYPE_SHOWMORE_ITEM;
      }else {
          return TYPE_NORMAL_ITEM;
      }
    }



    @Override
    protected RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_NORMAL_ITEM) {
            return new CommentSubitemHolder(mInflater.inflate(R.layout.item_comment_subitem, parent, false));
        }else {
            return new CommentSubitemShowMoreHolder(mInflater.inflate(R.layout.hotel_show_more_item, parent, false));
        }
    }


    @Override
    protected void onBindSectionHeaderViewHolder(final HeaderHolder holder, final int position) {
        viewHolderList.add(holder);
        DetailCommentListBean videoDetailCommentBean = item.get(position);
        RequestOptions options = new RequestOptions()
                .circleCrop().transforms(new GlideCircleTransform(mContext))
                .error(R.mipmap.head_default)
                .placeholder(R.mipmap.head_default);
        if(!StringUtil.isEmpty(item.get(position).avatar)) {
            if(!item.get(position).avatar.contains("http")) {
                item.get(position).avatar = HttpServicePath.BasePicUrl+item.get(position).avatar;
            }
        }
        Glide.with(mContext).load(videoDetailCommentBean.avatar).apply(options).into(holder.userIcon);
        holder.userName.setText(videoDetailCommentBean.user_nickname);
        holder.ivLevel.setImageResource(ImageResUtils.getLevelRes(videoDetailCommentBean.level));
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(videoDetailCommentBean.created*1000);
//        String  time = simpleDateFormat.format(date);
        holder.timeTv.setText(DateUtil.fromToday(date)+ ImageResUtils.getLevelText(videoDetailCommentBean.level));
        holder.contentTv.setText(videoDetailCommentBean.content);
        holder.likeNumTv.setText(ShowNumUtil.showUnm(videoDetailCommentBean.up_num));
        int userId = SharedPreferencedUtils.getInteger(mContext,"SYSUSERID",0);
        if(0 == item.get(position).is_up) {
            holder.likeImg.setImageResource(R.mipmap.item_like);
            holder.likeNumTv.setTextColor(mContext.getResources().getColor(R.color.FF666666));
        }else {
            holder.likeImg.setImageResource(R.mipmap.item_like_pre);
            holder.likeNumTv.setTextColor(mContext.getResources().getColor(R.color.FFDE5C51));
        }

        holder.likeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ClickUtil.onClick()) {
                    int is_up = item.get(position).is_up;
                    if(0 == is_up) {
                        if (null != videoDetailCommentItemListener) {
                            videoDetailCommentItemListener.addLike(HttpServicePath.URL_LIKE,4,videoDetailCommentBean.commentid,videoDetailCommentBean,holder);
                        }
                    }else {
                        if (null != videoDetailCommentItemListener) {
                            videoDetailCommentItemListener.addLike(HttpServicePath.URL_NO_LIKE,4,videoDetailCommentBean.commentid,videoDetailCommentBean,holder);
                        }
                    }
                }
            }
        });

        //短按回复评论
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoDetailCommentItemListener.toReply(videoDetailCommentBean.type,videoDetailCommentBean.grade+1<=1?videoDetailCommentBean.grade+1:1,videoDetailCommentBean.commentid,videoDetailCommentBean.user_nickname);
            }
        });

        //长按
        holder.layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                videoDetailCommentItemListener.longClick(videoDetailCommentBean.user_id,videoDetailCommentBean.commentid,
                        videoDetailCommentBean.article_id,1,position,-1,videoDetailCommentBean.content); //长按一级评论 二级设为-1方便处理
                return true;
            }
        });

    }


    @Override
    protected void onBindSectionFooterViewHolder(RecyclerView.ViewHolder holder, int section) {

    }

    /**
     * 这里有一个section和position ，有些人可能会弄混
     * section是区域，也就是我们最外层的index，position是每个section对应的内容数据的position
     *
     * @param holder
     * @param section
     * @param position
     */
    @Override
    protected void onBindItemViewHolder(RecyclerView.ViewHolder holder, final int section, final int position) {
        if(holder instanceof CommentSubitemHolder) {
            commentSubitemHolderList.add((CommentSubitemHolder)holder);
            CommentSubitemBean commentSubitemBean = item.get(section).cdata1.get(position);
            RequestOptions options = new RequestOptions()
                    .circleCrop().transforms(new GlideCircleTransform(mContext))
                    .error(R.mipmap.head_default)
                    .placeholder(R.mipmap.head_default);
            if(!StringUtil.isEmpty(commentSubitemBean.avatar)) {
                if(!commentSubitemBean.avatar.contains("http")) {
                    commentSubitemBean.avatar = HttpServicePath.BasePicUrl+commentSubitemBean.avatar;
                }
            }
            Glide.with(mContext).load(commentSubitemBean.avatar).apply(options).into(((CommentSubitemHolder)holder).userIcon);
            ((CommentSubitemHolder)holder).userName.setText(commentSubitemBean.user_nickname);
            ((CommentSubitemHolder) holder).ivLevel.setImageResource(ImageResUtils.getLevelRes(commentSubitemBean.level));

            Date date = new Date(commentSubitemBean.created*1000);
//        String  time = simpleDateFormat.format(date);
            ((CommentSubitemHolder) holder).timeTv.setText(DateUtil.fromToday(date)+ ImageResUtils.getLevelText(commentSubitemBean.level));


            if(!StringUtil.isEmpty(commentSubitemBean.nickname)) {
                String content = "回复 "+commentSubitemBean.nickname+" : "+commentSubitemBean.content;
                //变色
                int startIndex = content.indexOf("回复 ");
                int endIndex = content.indexOf(": ");
                SpannableString spannableString = new SpannableString(content);
                spannableString.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.FF999999)), startIndex+3, endIndex, 0);
                spannableString.setSpan(new RelativeSizeSpan(0.95f), startIndex+3, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ((CommentSubitemHolder)holder).contentTv.setText(spannableString);
            }else {
                ((CommentSubitemHolder)holder).contentTv.setText(commentSubitemBean.content);
            }
            ((CommentSubitemHolder)holder).likeNumTv.setText(ShowNumUtil.showUnm(commentSubitemBean.up_num));
            int userId = SharedPreferencedUtils.getInteger(mContext,"SYSUSERID",0);
            if(0 == commentSubitemBean.is_up) {
                ((CommentSubitemHolder)holder).likeImg.setImageResource(R.mipmap.item_like);
                ((CommentSubitemHolder)holder).likeNumTv.setTextColor(mContext.getResources().getColor(R.color.FF666666));
            }else {
                ((CommentSubitemHolder)holder).likeImg.setImageResource(R.mipmap.item_like_pre);
                ((CommentSubitemHolder)holder).likeNumTv.setTextColor(mContext.getResources().getColor(R.color.FFDE5C51));
            }

            ((CommentSubitemHolder)holder).likeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(ClickUtil.onClick()) {
                        int is_up = commentSubitemBean.is_up;
                        if(0 == is_up) {
                            if (null != videoDetailCommentItemListener) {
                                videoDetailCommentItemListener.addSubitemLike(HttpServicePath.URL_LIKE,4,commentSubitemBean.commentid,commentSubitemBean,holder);
                            }
                        }else {
                            if (null != videoDetailCommentItemListener) {
                                videoDetailCommentItemListener.addSubitemLike(HttpServicePath.URL_NO_LIKE,4,commentSubitemBean.commentid,commentSubitemBean,holder);
                            }
                        }
                    }
                }
            });

            //短按回复评论
            ((CommentSubitemHolder)holder).layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(StringUtil.isEmpty(commentSubitemBean.nickname)) {
                        videoDetailCommentItemListener.toReply(commentSubitemBean.type,commentSubitemBean.grade+1<=2?commentSubitemBean.grade+1:2,commentSubitemBean.commentid,commentSubitemBean.user_nickname);
                    }
                }
            });

            //长按
            ((CommentSubitemHolder)holder).layout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    videoDetailCommentItemListener.longClick(commentSubitemBean.user_id,commentSubitemBean.commentid,
                            commentSubitemBean.article_id,commentSubitemBean.type,section,position,commentSubitemBean.content);
                    return true;
                }
            });
        }else {
            ((CommentSubitemShowMoreHolder)holder).layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(ClickUtil.onClick()) {
                        videoDetailCommentItemListener.showMoreComment(item.get(section).commentid);
                    }
                }
            });
        }
    }

    public void setVideoDetailCommentItemListener(VideoDetailCommentItemListener videoDetailCommentItemListener) {
        this.videoDetailCommentItemListener = videoDetailCommentItemListener;
    }

    public void setAdapterDayModel(ThemeManager.ThemeMode themeMode) {
        ThemeManager.setThemeMode(themeMode);
    }
    @Override
    public void onThemeChanged() {
        if(null != viewHolderList && 0 != viewHolderList.size()) {
            for (HeaderHolder viewHolder:viewHolderList) {
                viewHolder.timeTv.setTextColor(mContext.getResources().getColor(ThemeManager.getCurrentThemeRes(mContext, R.color.FF999999)));
                viewHolder.contentTv.setTextColor(mContext.getResources().getColor(ThemeManager.getCurrentThemeRes(mContext, R.color.bottom_tab_tv)));
            }
        }

        if(null != commentSubitemHolderList && 0 != commentSubitemHolderList.size()) {
            for (CommentSubitemHolder viewHolder:commentSubitemHolderList) {
                viewHolder.timeTv.setTextColor(mContext.getResources().getColor(ThemeManager.getCurrentThemeRes(mContext, R.color.FF999999)));
                viewHolder.contentTv.setTextColor(mContext.getResources().getColor(ThemeManager.getCurrentThemeRes(mContext, R.color.bottom_tab_tv)));
            }
        }
    }
}
