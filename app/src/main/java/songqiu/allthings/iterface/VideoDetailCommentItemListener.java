package songqiu.allthings.iterface;

import songqiu.allthings.bean.HomeSubitemBean;
import songqiu.allthings.bean.VideoDetailCommentBean;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/8/26
 *
 *类描述：
 *
 ********/
public interface VideoDetailCommentItemListener {
    void addLike(String url,int type, int mid, VideoDetailCommentBean videoDetailCommentBean); //点赞  类型:1=文章，2=视频，3=话题，4=评论  表示id
    void toReport(int userId,int commentId,int articleid,int type,int position);//1=文章，2=视频，3=话题
}
