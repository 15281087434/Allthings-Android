package songqiu.allthings.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import songqiu.allthings.R;
import songqiu.allthings.articledetail.ArticleDetailActivity;
import songqiu.allthings.bean.CommentListBean;
import songqiu.allthings.home.gambit.GambitDetailActivity;
import songqiu.allthings.home.gambit.HotGambitDetailActivity;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.iterface.InteractionListener;
import songqiu.allthings.util.DateUtil;
import songqiu.allthings.util.GlideCircleTransform;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.videodetail.VideoDetailActivity;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/8/22
 *
 *类描述：
 *
 ********/
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    Context context;
    List<CommentListBean> list;

    public CommentAdapter(Context context, List<CommentListBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, viewGroup, false);
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
//
       viewHolder.userName.setText(list.get(position).user_nickname);
        viewHolder.contentTv.setText(list.get(position).content);

//        //时间
        long time = list.get(position).created*1000;
        viewHolder.timeTv.setText(DateUtil.getTimeBig3(time));
        if(2==list.get(position).news_status) {
            viewHolder.dotImg.setVisibility(View.GONE);
        }

        viewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                if(1==list.get(position).type) {
                    intent = new Intent(context, ArticleDetailActivity.class);
                    intent.putExtra("articleid",list.get(position).article_id);
                    context.startActivity(intent);
                }else if(2==list.get(position).type) {
                    intent = new Intent(context, VideoDetailActivity.class);
                    intent.putExtra("articleid",list.get(position).article_id);
                    context.startActivity(intent);
                }else {
                    if(0 == list.get(position).talk_type) {
                        intent = new Intent(context, GambitDetailActivity.class);
                        intent.putExtra("talkid",list.get(position).article_id);
                        context.startActivity(intent);
                    }else {
                        intent = new Intent(context,HotGambitDetailActivity.class);
                        intent.putExtra("talkid",list.get(position).article_id);
                        context.startActivity(intent);
                    }
                }
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
        @BindView(R.id.contentTv)
        TextView contentTv;
        @BindView(R.id.typeTv)
        TextView typeTv;
        @BindView(R.id.timeTv)
        TextView timeTv;
        @BindView(R.id.layout)
        RelativeLayout layout;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
