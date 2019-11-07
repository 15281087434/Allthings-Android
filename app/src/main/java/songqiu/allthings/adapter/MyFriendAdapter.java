package songqiu.allthings.adapter;

import android.content.Context;
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
import songqiu.allthings.bean.MyFriendBean;
import songqiu.allthings.bean.SystemListBean;
import songqiu.allthings.http.HttpServicePath;
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
public class MyFriendAdapter extends RecyclerView.Adapter<MyFriendAdapter.ViewHolder> {

    Context context;
    List<MyFriendBean> list;

    public MyFriendAdapter(Context context, List<MyFriendBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_my_friend, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.userName.setText(list.get(position).user_nickname);
        viewHolder.moneyTv.setText("+"+list.get(position).money+"元");
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
        @BindView(R.id.moneyTv)
        TextView moneyTv;
        @BindView(R.id.layout)
        LinearLayout layout;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
