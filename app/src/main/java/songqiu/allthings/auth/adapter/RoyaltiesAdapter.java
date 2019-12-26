package songqiu.allthings.auth.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import songqiu.allthings.R;
import songqiu.allthings.auth.bean.GcLogBean;
import songqiu.allthings.util.DateUtil;

/**
 * create by: linyinjianying
 * time:
 * e_mail:734090232@qq.com
 * description:
 */
public class RoyaltiesAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    Context context;
    ArrayList<GcLogBean.DataBean> list;

    public RoyaltiesAdapter(Context context, ArrayList<GcLogBean.DataBean> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder(LayoutInflater.from(context).inflate(R.layout.item_royalties,parent,false));
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        GcLogBean.DataBean bean=list.get(position);
        holder.getTextView(R.id.tv_title).setText(bean.getTitle()+"");
        holder.getTextView(R.id.tv_money).setText(bean.getRemuneration()+"元");
        holder.getTextView(R.id.tv_time).setText(DateUtil.getTime(bean.getCreated()+"000","MM-dd HH:mm"));
        holder.getTextView(R.id.tv_stutas).setVisibility((!TextUtils.isEmpty(bean.getStatus())&&bean.getStatus().equals("3"))? View.VISIBLE:View.GONE);
        if(bean.getStatus().equals("3")){
            holder.getTextView(R.id.tv_money).setTextColor(context.getResources().getColor(R.color.FF999999));
        }else {
            holder.getTextView(R.id.tv_money).setTextColor(context.getResources().getColor(R.color.bottom_tab_tv));

        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
