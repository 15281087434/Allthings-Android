package songqiu.allthings.iterface;

import java.util.List;

import songqiu.allthings.bean.HotGambitCommonBean;
import songqiu.allthings.bean.UserPagerBean;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/9/19
 *
 *类描述：
 *
 ********/
public interface GambitItemListener {
    //classType  1、热评  2、最新
    void addLike(String url,int type, int mid); //点赞  类型:1=文章，2=视频，3=评论，4=话题  表示id
    void addFollow(int parentid, int type);//talk_id、话题id
    void delete(int type,int talk_id);//1、删除  2、举报
}
