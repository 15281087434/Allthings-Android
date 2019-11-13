package songqiu.allthings.iterface;

import android.support.v7.widget.RecyclerView;

import java.util.List;

import songqiu.allthings.bean.HomeAttentionBean;


/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/8/26
 *
 *类描述：
 *
 ********/
public interface HomeAttentionListener {
    void addShare(int position);
    void addLike(String url, int type, int mid, HomeAttentionBean homeAttentionBean,RecyclerView.ViewHolder viewHolder); //点赞  类型:1=文章，2=视频，3=评论，4=话题  表示id
    void cancelFollow(int parentid, List<HomeAttentionBean> item);//用户id 	type、1=添加关注，2=取消关注
}
