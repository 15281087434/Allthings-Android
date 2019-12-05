package songqiu.allthings.adapter.Comment;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import songqiu.allthings.R;

/**
 */

public class CommentSubitemShowMoreHolder extends RecyclerView.ViewHolder {
    public LinearLayout layout;

    public CommentSubitemShowMoreHolder(View itemView) {
        super(itemView);
        initView();
    }

    private void initView() {
        layout = (LinearLayout) itemView.findViewById(R.id.layout);
    }
}
