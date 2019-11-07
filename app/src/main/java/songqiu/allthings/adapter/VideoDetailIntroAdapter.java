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
import songqiu.allthings.activity.CommentWebViewActivity;
import songqiu.allthings.bean.VideoDetailIntroBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.util.ClickUtil;
import songqiu.allthings.util.LogUtil;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.videodetail.VideoDetailActivity;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/6/24
 *
 *类描述：
 *
 ********/
public class VideoDetailIntroAdapter extends RecyclerView.Adapter<VideoDetailIntroAdapter.ViewHolder> {

    //    OnItemClickListener mListener;
    Context context;
    List<VideoDetailIntroBean> item;


    public VideoDetailIntroAdapter(Context context, List<VideoDetailIntroBean> item) {
        this.context = context;
        this.item = item;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_detail_intro, null);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        ((MessageViewholder)holder).isReadImg.setImageResource(R.mipmap.home_news);
        if(0==position) {
            holder.line.setVisibility(View.GONE);
        }else {
            holder.line.setVisibility(View.VISIBLE);
        }
        RequestOptions options1 = new RequestOptions()
                .error(R.mipmap.pic_default_small)
                .placeholder(R.mipmap.pic_default_small);

        if(1 == item.get(position).ad) {
            if(!StringUtil.isEmpty(item.get(position).url)) {
                if(!item.get(position).url.contains("http")) {
                    item.get(position).url = HttpServicePath.BasePicUrl+item.get(position).url;
                }
            }
            Glide.with(context).load(item.get(position).url).apply(options1).into(holder.photoImg);
            holder.relativeLayout.setVisibility(View.GONE);
            holder.advertisingTv.setVisibility(View.VISIBLE);
        }else {
            if(!StringUtil.isEmpty(item.get(position).photo)) {
                if(!item.get(position).photo.contains("http")) {
                    item.get(position).photo = HttpServicePath.BasePicUrl+item.get(position).photo;
                }
            }
            Glide.with(context).load(item.get(position).photo).apply(options1).into(holder.photoImg);
            holder.relativeLayout.setVisibility(View.VISIBLE);
            holder.advertisingTv.setVisibility(View.GONE);
        }
        holder.titleTv.setText(item.get(position).title);
        holder.lookNumTv.setText(String.valueOf(item.get(position).view_num) + "次观看");
        holder.userName.setText(item.get(position).user_nickname);
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ClickUtil.onClick()) {
                    if(1 == item.get(position).ad) {
                        Intent intent = new Intent(context, CommentWebViewActivity.class);
                        intent.putExtra("url", item.get(position).jump_url);
                        context.startActivity(intent);
                    }else {
                        Intent intent = new Intent(context,VideoDetailActivity.class);
                        intent.putExtra("articleid",item.get(position).articleid);
                        context.startActivity(intent);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return item.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.photoImg)
        ImageView photoImg;
        @BindView(R.id.titleTv)
        TextView titleTv;
        @BindView(R.id.userName)
        TextView userName;
        @BindView(R.id.lookNumTv)
        TextView lookNumTv;
        @BindView(R.id.layout)
        LinearLayout layout;
        @BindView(R.id.relativeLayout)
        RelativeLayout relativeLayout;
        @BindView(R.id.advertisingTv)
        TextView advertisingTv;
        @BindView(R.id.line)
        View line;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
