package songqiu.allthings.iterface;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/8/28
 *
 *类描述：分享弹窗相关方法
 *
 ********/
public interface WindowShareListener {
    void qqShare(int positon);
    void wechatShare(int positon);
    void wechatFriendShare(int positon);
    void link(int position);
    void report();
    void daytime();
    void night();
}
