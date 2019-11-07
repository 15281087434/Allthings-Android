package songqiu.allthings.bean;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/9/23
 *
 *类描述：
 *
 ********/
public class InteractionListBean {
//    "userid": "326",
//            "type": "1",    //1、查看（点赞） 2、关注
//            "mid": "81",
//            "change_type": "3",   //1 文章  2视频  3话题
//            "changeid": "2",
//            "parentid": "0",
//            "created": "1568975669",
//            "status": "1",  // 1、未查看  2、查看
//            "avatar": "upload\/1568630286993622.jpg",
//            "user_nickname": "◆夜雨",
//            "name": "取消了赞"

    public int userid;
    public int type;//1、查看（点赞） 2、关注 3、评论
    public int mid;
    public int change_type; //当type为1 则查看跳转 //1 文章  2视频  3话题
    public int changeid;
    public int parentid;
    public long created;
    public int status;// 1、未查看  2、查看 //未查看显示红点
    public String avatar;
    public String user_nickname;
    public String name;
    public int talk_type;
    public int is_follow;
}
