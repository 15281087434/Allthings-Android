package songqiu.allthings.iterface;

import songqiu.allthings.bean.CommentSubitemBean;
import songqiu.allthings.bean.DetailCommentListBean;

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
    void addLike(String url,int type, int mid, DetailCommentListBean videoDetailCommentBean); //一级评论点赞  类型:1=文章，2=视频，3=话题，4=评论  表示id
    void addSubitemLike(String url,int type, int mid, CommentSubitemBean commentSubitemBean); //二级评论点赞  类型:1=文章，2=视频，3=话题，4=评论  表示id
    void toReply(int type,int grade,int pid,String name); //回复评论
    void longClick(int userId,int commentId,int articleid,int type,int position,int subPosition,String content); //长按  position为父项位置 subPosition为子项位置 content:回复内容 复制用
    void showMoreComment(int mid);
}
