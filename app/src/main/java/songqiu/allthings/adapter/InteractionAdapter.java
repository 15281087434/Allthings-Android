package songqiu.allthings.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import songqiu.allthings.R;
import songqiu.allthings.bean.InteractionListBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.iterface.InteractionListener;
import songqiu.allthings.util.DateUtil;
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
public class InteractionAdapter extends RecyclerView.Adapter<InteractionAdapter.ViewHolder> {

    Context context;
    List<InteractionListBean> list;
    InteractionListener listener;

    public InteractionAdapter(Context context, List<InteractionListBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_interaction, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        RequestOptions options = new RequestOptions()
                .circleCrop().transforms(new GlideCircleTransform(context))
                .error(R.mipmap.head_default)
                .placeholder(R.mipmap.head_default);
        if(!StringUtil.isEmpty(list.get(position).avatar)) {
            if(!list.get(position).avatar.contains("http")) {
                list.get(position).avatar = HttpServicePath.BasePicUrl+list.get(position).avatar;
            }
        }
        Glide.with(context).load(list.get(position).avatar).apply(options).into(viewHolder.userIcon);

       viewHolder.userName.setText(list.get(position).user_nickname);
       viewHolder.typeTv.setText(list.get(position).name);
        //时间
        long time = list.get(position).created*1000;
        viewHolder.timeTv.setText(DateUtil.getTimeBig3(time));
        if(1==list.get(position).type) { //1、查看（点赞） 2、关注
            viewHolder.stateTv.setText("查看");
            viewHolder.stateTv.setBackgroundResource(R.drawable.rectangle_common_attention);
        }else {
            if(0==list.get(position).is_follow) {
                viewHolder.stateTv.setText("+ 关注");
                viewHolder.stateTv.setBackgroundResource(R.drawable.rectangle_common_attention);
            }else {
                viewHolder.stateTv.setText("已关注");
                viewHolder.stateTv.setBackgroundResource(R.drawable.rectangle_common_no_attention);
            }
        }
        if(2==list.get(position).status) {
            viewHolder.dotImg.setVisibility(View.GONE);
        }
        viewHolder.stateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.toLookAndAttention(list.get(position));
            }
        });
    }


    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }


    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.userIcon)
        ImageView userIcon;
        @BindView(R.id.userName)
        TextView userName;
        @BindView(R.id.dotImg)
        ImageView dotImg;
        @BindView(R.id.typeTv)
        TextView typeTv;
        @BindView(R.id.timeTv)
        TextView timeTv;
        @BindView(R.id.stateTv)
        TextView stateTv;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void setInteractionListener(InteractionListener listener) {
        this.listener = listener;
    }
}
