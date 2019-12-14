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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import songqiu.allthings.R;
import songqiu.allthings.bean.HomeGambitHotBean;
import songqiu.allthings.home.gambit.AllHotGambitActivity;
import songqiu.allthings.home.gambit.HotGambitDetailActivity;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.iterface.HomeHotGambitListener;
import songqiu.allthings.util.ClickUtil;
import songqiu.allthings.util.GlideCircleTransform;
import songqiu.allthings.util.StringUtil;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/8/13
 *
 *类描述：
 *
 ********/
public class AllHotGambitAdapter extends RecyclerView.Adapter {

    Context context;
    List<HomeGambitHotBean> list;
    HomeHotGambitListener homeHotGambitListener;

    public AllHotGambitAdapter(Context context, List<HomeGambitHotBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_home_tab_gambit, null);
        return new GambitViewholder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        RequestOptions options = new RequestOptions()
                .circleCrop().transforms(new GlideCircleTransform(context))
                .error(R.mipmap.head_default)
                .placeholder(R.mipmap.head_default);
        if(!StringUtil.isEmpty(list.get(position).icon)) {
            if(!list.get(position).icon.contains("http")) {
                list.get(position).icon = HttpServicePath.BasePicUrl+list.get(position).icon;
            }
        }
        Glide.with(context).load(list.get(position).icon).apply(options).into(((GambitViewholder)viewHolder).userIcon);
        ((GambitViewholder)viewHolder).contentTv.setText(list.get(position).title);
        ((GambitViewholder)viewHolder).hotNumTv.setText(list.get(position).hot_num+" 热度");
        ((GambitViewholder)viewHolder).attentionNumTv.setText(list.get(position).follow_num+" 关注");
        if (0 == list.get(position).is_follow) {
            ((GambitViewholder)viewHolder).attentionTv.setText("关注");
            ((GambitViewholder)viewHolder).attentionTv.setBackgroundResource(R.drawable.rectangle_common_attention);
        }else {
            ((GambitViewholder)viewHolder).attentionTv.setText("已关注");
            ((GambitViewholder)viewHolder).attentionTv.setBackgroundResource(R.drawable.rectangle_common_no_attention);
        }
        //关注、取消关注
        ((GambitViewholder)viewHolder).attentionTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ClickUtil.onClick()) {
                    if(0 == list.get(position).is_follow) {
                        if (null != homeHotGambitListener) {
                            homeHotGambitListener.addFollow(HttpServicePath.URL_FOLLOW_TALK,list.get(position).id,viewHolder);
                        }
                    }else {
                        if (null != homeHotGambitListener) {
                            homeHotGambitListener.addFollow(HttpServicePath.URL_FOLLOW_TALK_NO,list.get(position).id,viewHolder);
                        }
                    }
                }
            }
        });

        ((GambitViewholder)viewHolder).layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,HotGambitDetailActivity.class);
                intent.putExtra("talkid",list.get(position).id);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class GambitViewholder extends RecyclerView.ViewHolder {
        @BindView(R.id.userIcon)
        ImageView userIcon;
        @BindView(R.id.contentTv)
        TextView contentTv;
        @BindView(R.id.hotNumTv)
        TextView hotNumTv;
        @BindView(R.id.attentionNumTv)
        public TextView attentionNumTv;
        @BindView(R.id.attentionTv)
        public TextView attentionTv;
        @BindView(R.id.line)
        TextView line;
        @BindView(R.id.layout)
        RelativeLayout layout;

        public GambitViewholder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void setHomeHotGambitListener(HomeHotGambitListener homeHotGambitListener) {
        this.homeHotGambitListener = homeHotGambitListener;
    }
}
