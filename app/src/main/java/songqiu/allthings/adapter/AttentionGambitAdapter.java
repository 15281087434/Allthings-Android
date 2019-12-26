package songqiu.allthings.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import songqiu.allthings.R;
import songqiu.allthings.bean.AttentionGambitBean;
import songqiu.allthings.bean.AttentionUserBean;
import songqiu.allthings.home.gambit.HotGambitDetailActivity;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.iterface.CancelAttentionGambitListener;
import songqiu.allthings.iterface.CancelAttentionListener;
import songqiu.allthings.util.ClickUtil;
import songqiu.allthings.util.GlideCircleTransform;
import songqiu.allthings.util.ImageResUtils;
import songqiu.allthings.util.ShowNumUtil;
import songqiu.allthings.util.StringUtil;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/8/22
 *
 *类描述：
 *
 ********/
public class AttentionGambitAdapter extends RecyclerView.Adapter<AttentionGambitAdapter.ViewHolder> {

    Context context;
    List<AttentionGambitBean> item;
    CancelAttentionGambitListener cancelAttentionListener;

    public AttentionGambitAdapter(Context context, List<AttentionGambitBean> item) {
        this.context = context;
        this.item = item;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_attention_gambit, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AttentionGambitBean attentionGambitBean = item.get(position);
        RequestOptions options = new RequestOptions()
                .circleCrop().transforms(new GlideCircleTransform(context))
                .error(R.mipmap.head_default)
                .placeholder(R.mipmap.head_default);
        if (!StringUtil.isEmpty(attentionGambitBean.icon)) {
            if (!attentionGambitBean.icon.contains("http")) {
                attentionGambitBean.icon = HttpServicePath.BasePicUrl + attentionGambitBean.icon;
            }
        }
        Glide.with(context).load(attentionGambitBean.icon).apply(options).into(holder.userIcon);
        holder.ivLevel.setImageResource(ImageResUtils.getLevelRes(item.get(position).level));
        holder.userName.setText(attentionGambitBean.title);
        holder.hotTv.setText(ShowNumUtil.showUnm(attentionGambitBean.hot_num)+" 热度");
        holder.attentionTv.setText(ShowNumUtil.showUnm(attentionGambitBean.follow_num)+" 关注");
        holder.stateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelAttentionListener.cancelAttention(HttpServicePath.URL_FOLLOW_TALK_NO,attentionGambitBean.id,position);
            }
        });

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ClickUtil.onClick()) {
                    Intent intent = new Intent(context,HotGambitDetailActivity.class);
                    intent.putExtra("talkid",attentionGambitBean.id);
                    context.startActivity(intent);
                }
            }
        });

        if(position == item.size()-1) {
            holder.line.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }


    @Override
    public int getItemCount() {
        return item.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.userIcon)
        ImageView userIcon;
        @BindView(R.id.userName)
        TextView userName;
        @BindView(R.id.hotTv)
        TextView hotTv;
        @BindView(R.id.attentionTv)
        TextView attentionTv;
        @BindView(R.id.stateTv)
        TextView stateTv;
        @BindView(R.id.layout)
        LinearLayout layout;
        @BindView(R.id.line)
        View line;
        @BindView(R.id.iv_level)
        ImageView ivLevel;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void setCancelAttentionListener(CancelAttentionGambitListener cancelAttentionListener) {
        this.cancelAttentionListener = cancelAttentionListener;
    }
}
