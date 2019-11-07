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
import songqiu.allthings.bean.SearchTxtBean;
import songqiu.allthings.http.HttpServicePath;
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
public class SearchVideoAdapter extends RecyclerView.Adapter<SearchVideoAdapter.ViewHolder> {

    Context context;
    List<SearchTxtBean> item;

    public SearchVideoAdapter(Context context, List<SearchTxtBean> item) {
        this.context = context;
        this.item = item;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_video_detail_intro, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        viewHolder.descriptionsTv.setText(item.get(position).descriptions);
        RequestOptions options1 = new RequestOptions()
                .error(R.mipmap.pic_default_small)
                .placeholder(R.mipmap.pic_default_small);
        if(!StringUtil.isEmpty(item.get(position).photo)) {
            if(!item.get(position).photo.contains("http")) {
                item.get(position).photo = HttpServicePath.BasePicUrl+item.get(position).photo;
            }
        }
        Glide.with(context).load(item.get(position).photo).apply(options1).into(holder.photoImg);

        holder.titleTv.setText(item.get(position).title);
        holder.lookNumTv.setText(String.valueOf(item.get(position).view_num) + "次观看");
        holder.userName.setText(item.get(position).user_nickname);
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,VideoDetailActivity.class);
                intent.putExtra("articleid",item.get(position).articleid);
                context.startActivity(intent);
            }
        });

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

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
