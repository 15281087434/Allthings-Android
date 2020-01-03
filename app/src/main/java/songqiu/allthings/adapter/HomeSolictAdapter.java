package songqiu.allthings.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import songqiu.allthings.R;
import songqiu.allthings.activity.CommentWebViewActivity;
import songqiu.allthings.articledetail.ArticleDetailActivity;
import songqiu.allthings.auth.adapter.BaseViewHolder;
import songqiu.allthings.bean.BannerBean;
import songqiu.allthings.bean.HomeSolictBean;
import songqiu.allthings.bean.HomeSubitemBean;
import songqiu.allthings.http.BaseBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.http.OkHttp;
import songqiu.allthings.http.RequestCallBack;
import songqiu.allthings.login.LoginActivity;
import songqiu.allthings.mine.income.IncomeRecordActivity;
import songqiu.allthings.util.ClickUtil;
import songqiu.allthings.util.GlideCircleTransform;
import songqiu.allthings.util.ImageResUtils;
import songqiu.allthings.util.LogUtil;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.util.TokenManager;
import songqiu.allthings.videodetail.VideoDetailActivity;
import songqiu.allthings.view.banner.ColorPointHintView;
import songqiu.allthings.view.banner.RollPagerView;

/**
 * create by: ADMIN
 * time:2019/12/3017:29
 * e_mail:734090232@qq.com
 * description:
 */
public class HomeSolictAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    Context context;
    List<HomeSolictBean> item;
    private TpCallBack mCallBack;
    SolictListener solictListener;

    public void setmCallBack(TpCallBack mCallBack) {
        this.mCallBack = mCallBack;
    }

    public void setBannerBeans(List<BannerBean> bannerBeans) {
        this.bannerBeans = bannerBeans;
        notifyItemChanged(0);
    }

    List<BannerBean> bannerBeans;

    public HomeSolictAdapter(Context context, List<HomeSolictBean> item) {
        this.context = context;
        this.item = item;

    }

    public interface SolictListener {
        void onSolictListener(List<BannerBean> bannerBeans, int position);
    }

    public void setSolictListener(SolictListener solictListener) {
        this.solictListener = solictListener;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            return new BaseViewHolder(LayoutInflater.from(context).inflate(R.layout.item_home_solicit_banner, parent, false));
        }
        return new BaseViewHolder(LayoutInflater.from(context).inflate(R.layout.item_home_solicit, parent, false));
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (position == 0) {
            RollPagerView roll_page_mine = (RollPagerView) holder.getView(R.id.roll_page_mine);
            BannerMineAdapter mBannerAdapter = new BannerMineAdapter(roll_page_mine, (ArrayList<BannerBean>) bannerBeans);
            roll_page_mine.setAdapter(mBannerAdapter);
            roll_page_mine.setHintView(new ColorPointHintView(context, Color.WHITE, Color.GRAY));
            roll_page_mine.setHintPadding(0, 0, 0, 10);
            roll_page_mine.resume();
            if (null == bannerBeans) return;
            if (1 == bannerBeans.size()) {
                roll_page_mine.pause();
                roll_page_mine.setHintViewVisibility(false);
            }
            //点击事件
            roll_page_mine.setOnItemClickListener(mPosition -> {
                solictListener.onSolictListener(bannerBeans, mPosition);
            });
        } else {
            HomeSolictBean bean = item.get(position - 1);
            holder.getTextView(R.id.tv_title).setText(bean.getTitle());
            holder.getTextView(R.id.tv_contents).setText(bean.getDescriptions());
            holder.getTextView(R.id.tv_tickets).setText(bean.getSupport_num() + "");
            holder.getTextView(R.id.userName).setText(bean.getUser_nickname() + "");
            RequestOptions options = new RequestOptions()
                    .circleCrop().transforms(new GlideCircleTransform(context))
                    .error(R.mipmap.head_default)
                    .placeholder(R.mipmap.head_default);
            if (!StringUtil.isEmpty(bean.getAvatar())) {
                if (!bean.getAvatar().contains("http")) {
                    bean.setAvatar(HttpServicePath.BasePicUrl + bean.getAvatar());
                }
            }
            Glide.with(context).load(bean.getAvatar()).apply(options).into(holder.getImageView(R.id.userIcon));
            holder.getImageView(R.id.iv_level).setImageResource(ImageResUtils.getLevelRes(bean.getLevel()));

            if (position < 4) {
                holder.getTextView(R.id.tv_rank).setText(position + "");
                holder.getTextView(R.id.tv_rank).setVisibility(View.VISIBLE);
            } else {
                holder.getTextView(R.id.tv_rank).setVisibility(View.GONE);
            }

            holder.getView(R.id.tv_tp).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ClickUtil.onClick()) {
                        if (mCallBack != null) {
                            mCallBack.onTp(position - 1);
                        }
                    }
                }
            });


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ClickUtil.onClick()) {
                        String articleid = item.get(position - 1).getArticleid();
                        Intent intent = new Intent(context, ArticleDetailActivity.class);
                        intent.putExtra("articleid", Integer.parseInt(articleid));
                        context.startActivity(intent);
                    }
                }
            });

        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public int getItemCount() {
        return item.size() + 1;
    }

    public interface TpCallBack {
        void onTp(int position);
    }
}
