package songqiu.allthings.adapter.Comment;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import songqiu.allthings.R;

/**
 */

public class CommentSubitemHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.layout)
    RelativeLayout layout;
    @BindView(R.id.userIcon)
    ImageView userIcon;
    @BindView(R.id.userName)
    TextView userName;
    @BindView(R.id.timeTv)
    TextView timeTv;
    @BindView(R.id.contentTv)
    TextView contentTv;
    @BindView(R.id.likeNumTv)
    TextView likeNumTv;
    @BindView(R.id.likeImg)
    ImageView likeImg;
    @BindView(R.id.likeLayout)
    LinearLayout likeLayout;

    public CommentSubitemHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
