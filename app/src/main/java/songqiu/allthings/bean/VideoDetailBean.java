package songqiu.allthings.bean;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/8/27
 *
 *类描述：
 *
 ********/
public class VideoDetailBean {
//    "articleid": 743,
//            "description": "20米长的大卡车倒车入库一气呵成！这才是真正的老司机",
//            "like_count": 2,
//            "author": "小丸子娱乐快报",
//            "title": "20米长的大卡车倒车入库一气呵成！这才是真正的老司机",
//            "length": 82,
//            "video_url": "https:\/\/flv2.bn.netease.com\/videolib1\/1810\/21\/caytg682T\/SD\/caytg682T-mobile.mp4",
//            "photo": "http:\/\/vimg.nosdn.127.net\/snapshot\/20181021\/caytg682T_cover.jpg",
//            "view_num": 14,
//            "userid": 8,
//            "avatar": "http:\/\/192.168.0.106:81\/upload\/member\/20190731\/60a470c15d3aa7939fee5410dbb3925d.jpg",
//            "user_nickname": "鸡你太美",
//            "is_up": 0,
//            "is_collect": 0,
//            "is_follow": 0,
//            "comment_num": 2,
//            "up_num": 0

    public int articleid;
    public String description;
    public int like_count;
    public String  author;
    public String title;
    public int length;
    public String video_url;
    public String photo;
    public int view_num;
    public int userid;
    public String avatar;
    public String user_nickname;
    public int is_up;
    public int is_collect;
    public int is_follow;
    public int comment_num;
    public int up_num;
    public int is_comment;
    public int is_coin;//0显示转圈  1领取奖励已上限不显示转圈
    public int is_original;
}
