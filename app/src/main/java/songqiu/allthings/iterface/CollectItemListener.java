package songqiu.allthings.iterface;

import java.util.List;

import songqiu.allthings.bean.HomeSubitemBean;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/8/26
 *
 *类描述：
 *
 ********/
public interface CollectItemListener {
    void addLike(String url, int type, int mid, HomeSubitemBean homeSubitemBean); //点赞  类型:1=文章，2=视频，3=评论，4=话题  表示id
    void cancelCollect(int type,int articleid,int position);
    void addShare(int position,int type);
}
