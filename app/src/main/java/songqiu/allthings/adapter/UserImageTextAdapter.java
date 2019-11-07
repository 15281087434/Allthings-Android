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

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import songqiu.allthings.R;
import songqiu.allthings.articledetail.ArticleDetailActivity;
import songqiu.allthings.bean.UserPagerBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.iterface.UserPagerListenner;
import songqiu.allthings.util.ClickUtil;
import songqiu.allthings.util.SharedPreferencedUtils;
import songqiu.allthings.util.ShowNumUtil;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.util.TokenManager;
import songqiu.allthings.view.GridViewInScroll;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/9/17
 *
 *类描述：
 *
 ********/
public class UserImageTextAdapter extends RecyclerView.Adapter {

    //设置常量  moudle 1、纯文字  2、大图 3、小图
    private final int TYPE_ONLY_TEXT = 1;
    private final int TYPE_BIG_PIC = 2;
    private final int TYPE_SMALL_PIC = 3;
    private final int TYPE_MORE_PIC = 4;

    Context context;
    List<UserPagerBean> item;

    UserPagerListenner userPagerListenner;


    public UserImageTextAdapter(Context context, List<UserPagerBean> item) {
        this.context = context;
        this.item = item;
    }

    @Override
    public int getItemViewType(int position) {
        if (item.get(position).module == 1) {
            return TYPE_ONLY_TEXT;
        } else if (item.get(position).module == 2) {
            return TYPE_BIG_PIC;
        } else if (item.get(position).module == 3) {
            return TYPE_SMALL_PIC;
        } else if(item.get(position).module == 4) {
            return TYPE_MORE_PIC;
        }
        return TYPE_ONLY_TEXT;
    }

    @Override
    public int getItemCount() {
        return item.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType == TYPE_ONLY_TEXT) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_user_imagetext_no_pic, null);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(lp);
            return new NoPicViewholder(view);
        } else if (viewType == TYPE_BIG_PIC) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_user_imagetext_big_pic, null);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(lp);
            return new BigPicViewholder(view);
        } else if (viewType == TYPE_SMALL_PIC) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_user_imagetext_right_pic, null);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(lp);
            return new RightPicViewholder(view);
        } else if (viewType == TYPE_MORE_PIC) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_user_more_pic, null);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(lp);
            return new MorePicViewholder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
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
        holder.titleTv.setText(item.get(position).title);
        holder.contentTv.setText(item.get(position).descriptions);

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
            holder.shareImg.setImageDrawable(context.getResources().getDrawable(R.mipmap.item_inform));
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

        holder.likeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int is_up = item.get(position).is_up;
                if(0 == is_up) {
                    if (null != userPagerListenner) {
                        userPagerListenner.addLike(HttpServicePath.URL_LIKE,1,item.get(position).articleid,item.get(position));
                    }
                }else {
                    if (null != userPagerListenner) {
                        userPagerListenner.addLike(HttpServicePath.URL_NO_LIKE,1,item.get(position).articleid,item.get(position));
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
                        userPagerListenner.share(2,position,1);
                    }
                }
            }
        });
    }

    public void setBigPicData(BigPicViewholder holder, int position) {
        holder.titleTv.setText(item.get(position).title);
        holder.contentTv.setText(item.get(position).descriptions);
        RequestOptions options = new RequestOptions()
                .error(R.mipmap.pic_default)
                .placeholder(R.mipmap.pic_default);
        if(!StringUtil.isEmpty(item.get(position).photo)) {
            if (!item.get(position).photo.contains("http")) {
                item.get(position).photo = HttpServicePath.BasePicUrl + item.get(position).photo;
            }
        }
        Glide.with(context).load(item.get(position).photo).apply(options).into(holder.bigPicImg);

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
            holder.shareImg.setImageDrawable(context.getResources().getDrawable(R.mipmap.item_inform));
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

        holder.likeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int is_up = item.get(position).is_up;
                if(0 == is_up) {
                    if (null != userPagerListenner) {
                        userPagerListenner.addLike(HttpServicePath.URL_LIKE,1,item.get(position).articleid,item.get(position));
                    }
                }else {
                    if (null != userPagerListenner) {
                        userPagerListenner.addLike(HttpServicePath.URL_NO_LIKE,1,item.get(position).articleid,item.get(position));
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
                        userPagerListenner.share(2,position,1);
                    }
                }
            }
        });
    }

    public void setRightPicData(RightPicViewholder holder, int position) {
        holder.titleTv.setText(item.get(position).title);
        holder.contentTv.setText(item.get(position).descriptions);
        RequestOptions options = new RequestOptions()
                .error(R.mipmap.pic_default)
                .placeholder(R.mipmap.pic_default);
        if(!StringUtil.isEmpty(item.get(position).photo)) {
            if (!item.get(position).photo.contains("http")) {
                item.get(position).photo = HttpServicePath.BasePicUrl + item.get(position).photo;
            }
        }
        Glide.with(context).load(item.get(position).photo).apply(options).into(holder.rightPic);

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
            holder.shareImg.setImageDrawable(context.getResources().getDrawable(R.mipmap.item_inform));
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

        holder.likeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int is_up = item.get(position).is_up;
                if(0 == is_up) {
                    if (null != userPagerListenner) {
                        userPagerListenner.addLike(HttpServicePath.URL_LIKE,1,item.get(position).articleid,item.get(position));
                    }
                }else {
                    if (null != userPagerListenner) {
                        userPagerListenner.addLike(HttpServicePath.URL_NO_LIKE,1,item.get(position).articleid,item.get(position));
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
                        userPagerListenner.share(2,position,1);
                    }
                }
            }
        });
    }

    public void setMorePicData(MorePicViewholder holder, int position) {
        holder.titleTv.setText(item.get(position).title);
//        holder.contentTv.setText(item.get(position).descriptions);
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

        holder.likeTv.setText(ShowNumUtil.showUnm(item.get(position).up_num));
        holder.commentTv.setText(ShowNumUtil.showUnm(item.get(position).comment_num));
        if (0 == item.get(position).is_up) {
            holder.likeImg.setImageResource(R.mipmap.item_like);
            holder.likeTv.setTextColor(context.getResources().getColor(R.color.FF666666));
        } else {
            holder.likeImg.setImageResource(R.mipmap.item_like_pre);
            holder.likeTv.setTextColor(context.getResources().getColor(R.color.FFDE5C51));
        }

        int mUserId = SharedPreferencedUtils.getInteger(context, "SYSUSERID", 0);
        if (item.get(position).userid == mUserId) {
            holder.shareImg.setImageDrawable(context.getResources().getDrawable(R.mipmap.icon_cancel));
        } else {
            holder.shareImg.setImageDrawable(context.getResources().getDrawable(R.mipmap.item_inform));
        }

        holder.likeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int is_up = item.get(position).is_up;
                if (0 == is_up) {
                    if (null != userPagerListenner) {
                        userPagerListenner.addLike(HttpServicePath.URL_LIKE, 1, item.get(position).articleid, item.get(position));
                    }
                } else {
                    if (null != userPagerListenner) {
                        userPagerListenner.addLike(HttpServicePath.URL_NO_LIKE, 1, item.get(position).articleid, item.get(position));
                    }
                }
            }
        });

        holder.shareLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (item.get(position).userid == mUserId) {
                    if (null != userPagerListenner) {
                        userPagerListenner.delete();
                    }
                } else {
                    if (null != userPagerListenner) {
                        userPagerListenner.share(2, position, 1);
                    }
                }
            }
        });

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
    }


    public class NoPicViewholder extends RecyclerView.ViewHolder {
        @BindView(R.id.titleTv)
        TextView titleTv;
        @BindView(R.id.contentTv)
        TextView contentTv;
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

        public NoPicViewholder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    public class BigPicViewholder extends RecyclerView.ViewHolder {
        @BindView(R.id.titleTv)
        TextView titleTv;
        @BindView(R.id.bigPicImg)
        ImageView bigPicImg;
        @BindView(R.id.contentTv)
        TextView contentTv;
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

        public BigPicViewholder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class RightPicViewholder extends RecyclerView.ViewHolder {
        @BindView(R.id.titleTv)
        TextView titleTv;
        @BindView(R.id.contentTv)
        TextView contentTv;
        @BindView(R.id.rightPic)
        ImageView rightPic;
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
        @BindView(R.id.likeImg)
        ImageView likeImg;
        @BindView(R.id.likeTv)
        TextView likeTv;
        @BindView(R.id.likeLayout)
        LinearLayout likeLayout;
        @BindView(R.id.commentTv)
        TextView commentTv;
        @BindView(R.id.shareImg)
        ImageView shareImg;
        @BindView(R.id.shareLayout)
        LinearLayout shareLayout;
        @BindView(R.id.layout)
        LinearLayout layout;
        public MorePicViewholder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void setUserPagerListenner(UserPagerListenner userPagerListenner) {
        this.userPagerListenner = userPagerListenner;
    }
}
