package songqiu.allthings.iterface;

import songqiu.allthings.bean.HomeSubitemBean;
import songqiu.allthings.bean.UserPagerBean;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/9/17
 *
 *类描述：
 *
 ********/
public interface UserPagerListenner {
    void addLike(String url,int type, int mid,UserPagerBean userPagerBean); //点赞  类型:1=文章，2=视频，3=评论，4=话题  表示id
    void share(int type,int position,int shareType); //1、分享  2、举报
    void delete();
}
