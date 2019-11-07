package songqiu.allthings.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import songqiu.allthings.R;
import songqiu.allthings.bean.SearchHotGambitBean;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/6/24
 *
 *类描述：
 *
 ********/
public class SearchHotGambitAdapter extends RecyclerView.Adapter {

    Context context;
    List<SearchHotGambitBean> item;
    OnItemClickListener mListener;

    public SearchHotGambitAdapter(Context context, List<SearchHotGambitBean> item) {
        this.context = context;
        this.item = item;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_hot_gambit, null);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new IntroViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((IntroViewholder) holder).contentTv.setText(item.get(position).title);
        ((IntroViewholder) holder).hotNumTv.setText(String.valueOf(item.get(position).hot_num));
        ((IntroViewholder) holder).layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onItemClick(item.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return item.size();
    }

    public class IntroViewholder extends RecyclerView.ViewHolder {

        @BindView(R.id.contentTv)
        TextView contentTv;
        @BindView(R.id.hotNumTv)
        TextView hotNumTv;
        @BindView(R.id.layout)
        LinearLayout layout;

        public IntroViewholder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    public interface OnItemClickListener {
        void onItemClick(SearchHotGambitBean searchHotGambitBean);
    }

    public void setAdapterListener(OnItemClickListener listener) {
        this.mListener = listener;
    }
}
