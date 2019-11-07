package songqiu.allthings.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import songqiu.allthings.R;
import songqiu.allthings.bean.CustomTaskBean;
import songqiu.allthings.iterface.TaskItemListenter;
import songqiu.allthings.util.ClickUtil;
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
public class TaskPageAdapter extends RecyclerView.Adapter {

    Context context;
    List<CustomTaskBean> item;

    private final int TYPE_TITLE = 1;
    private final int TYPE_CONTENT = 2;

    TaskItemListenter taskItemListenter;


    public TaskPageAdapter(Context context, List<CustomTaskBean> item) {
        this.context = context;
        this.item = item;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == TYPE_TITLE) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_task_page_title, null);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(lp);
            return new TitleViewholder(view);
        } else if (viewType == TYPE_CONTENT) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_task_page_content, null);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(lp);
            return new ContentViewholder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof TaskPageAdapter.TitleViewholder) {
            setTitleData((TaskPageAdapter.TitleViewholder) holder, position);
        }else if(holder instanceof TaskPageAdapter.ContentViewholder) {
            setContentData((TaskPageAdapter.ContentViewholder) holder, position);
        }
    }

    public void setTitleData(TaskPageAdapter.TitleViewholder holder, int position) {
        String headline = item.get(position).headline;
        if(StringUtil.isEmpty(headline)) return;
        if(headline.equals("task_new")) {
            holder.titleImg.setText("新手任务");
        }else {
            holder.titleImg.setText("日常任务");
        }
        String headlineTag = item.get(position).headlineTag;
        if(!StringUtil.isEmpty(headlineTag)) {
            holder.titleTv.setText(headlineTag);
            holder.titleTv.setVisibility(View.VISIBLE);
        }else {
            holder.titleTv.setVisibility(View.GONE);
        }
    }

    public void setContentData(TaskPageAdapter.ContentViewholder holder, int position) {
        if(item.get(position).gettype == 1) {
            holder.img.setImageResource(R.mipmap.money_yellow);
            holder.numTv.setTextColor(context.getResources().getColor(R.color.FFE7A722));
        }else {
            holder.img.setImageResource(R.mipmap.money_red);
            holder.numTv.setTextColor(context.getResources().getColor(R.color.FFDE5C51));
        }
        holder.titleTv.setText(item.get(position).title);
        holder.numTv.setText(item.get(position).num);
        holder.descriptionTv.setText(item.get(position).task_description);
        if(1==item.get(position).is_over) {
            holder.buttonttTv.setText("已完成");
            holder.buttonttTv.setBackgroundResource(R.drawable.rectangle_f0f0f0_8px_white);
            holder.buttonttTv.setTextColor(context.getResources().getColor(R.color.FF999999));
        }else {
            holder.buttonttTv.setText(item.get(position).button);
            holder.buttonttTv.setBackgroundResource(R.drawable.rectangle_0fc189_8px_white);
            holder.buttonttTv.setTextColor(context.getResources().getColor(R.color.normal_color));
        }
        holder.buttonttTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ClickUtil.onClick()) {
                    if(null != taskItemListenter) {
                        if(0==item.get(position).is_over) {
                            taskItemListenter.toTask(item.get(position).taskname);
                        }
                    }
                }
            }
        });
    }


    @Override
    public int getItemViewType(int position) {
        if (!StringUtil.isEmpty(item.get(position).headline)) {
            return TYPE_TITLE;
        }
        return TYPE_CONTENT;
    }


    @Override
    public int getItemCount() {
        return item.size();
    }

    public class TitleViewholder extends RecyclerView.ViewHolder {
        @BindView(R.id.titleImg)
        TextView titleImg;
        @BindView(R.id.titleTv)
        TextView titleTv;

        public TitleViewholder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class ContentViewholder extends RecyclerView.ViewHolder {
        @BindView(R.id.titleTv)
        TextView titleTv;
        @BindView(R.id.numTv)
        TextView numTv;
        @BindView(R.id.descriptionTv)
        TextView descriptionTv;
        @BindView(R.id.buttonttTv)
        TextView buttonttTv;
        @BindView(R.id.img)
        ImageView img;

        public ContentViewholder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void settaskItemListenter(TaskItemListenter taskItemListenter) {
        this.taskItemListenter = taskItemListenter;
    }

}
