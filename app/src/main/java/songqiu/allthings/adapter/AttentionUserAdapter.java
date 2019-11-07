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
import songqiu.allthings.bean.AttentionUserBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.iterface.CancelAttentionListener;
import songqiu.allthings.mine.userpage.UserPagerActivity;
import songqiu.allthings.util.ClickUtil;
import songqiu.allthings.util.GlideCircleTransform;
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
public class AttentionUserAdapter extends RecyclerView.Adapter<AttentionUserAdapter.ViewHolder> {

    Context context;
    List<AttentionUserBean> item;
    CancelAttentionListener cancelAttentionListener;

    public AttentionUserAdapter(Context context, List<AttentionUserBean> item) {
        this.context = context;
        this.item = item;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_attention, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AttentionUserBean attentionUserBean = item.get(position);
        RequestOptions options = new RequestOptions()
                .circleCrop().transforms(new GlideCircleTransform(context))
                .error(R.mipmap.head_default)
                .placeholder(R.mipmap.head_default);
        if (!StringUtil.isEmpty(attentionUserBean.avatar)) {
            if (!attentionUserBean.avatar.contains("http")) {
                attentionUserBean.avatar = HttpServicePath.BasePicUrl + attentionUserBean.avatar;
            }
        }
        Glide.with(context).load(attentionUserBean.avatar).apply(options).into(holder.userIcon);
        holder.userName.setText(attentionUserBean.user_nickname);
        holder.contentTv.setText(attentionUserBean.signature);
        holder.stateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelAttentionListener.cancelAttention(attentionUserBean.userid);
            }
        });
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ClickUtil.onClick()) {
                    Intent intent = new Intent(context, UserPagerActivity.class);
                    intent.putExtra("userId", attentionUserBean.userid);
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
        @BindView(R.id.contentTv)
        TextView contentTv;
        @BindView(R.id.stateTv)
        TextView stateTv;
        @BindView(R.id.line)
        View line;
        @BindView(R.id.layout)
        LinearLayout layout;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void setCancelAttentionListener(CancelAttentionListener cancelAttentionListener) {
        this.cancelAttentionListener = cancelAttentionListener;
    }
}
