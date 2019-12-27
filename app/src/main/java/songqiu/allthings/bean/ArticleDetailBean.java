package songqiu.allthings.bean;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/8/28
 *
 *类描述：
 *
 ********/
public class ArticleDetailBean {

//"articleid": 157,
//        "description": "　　小李考虑买车，但他的钱不多，又好面子。\r\n　　他在网上查了一些二手车的信息，联系了一个车贩子。车贩子向他推荐了几辆车，但他都不满意，车贩子看出了他的心思，于是",
//        "up_num": 0,
//        "comment_num": 0,
//        "author": "",
//        "title": "成都二手车",
//        "photo": "cover\/20190815\/0219d43cac153cefa116b492b49a9cfc.png",
//        "view_num": 5050,
//        "userid": 6,
//        "avatar": "http:\/\/192.168.0.106:81\/upload\/member\/20190731\/60a470c15d3aa7939fee5410dbb3925d.jpg",
//        "user_nickname": "飞龙在天",
//        "is_up": 0,
//        "is_collect": 0,
//        "is_follow": 0
//         "content": "&lt;p&gt;　　江萝婷意外地得到一位
    public int articleid;
    public String descriptions;
    public int up_num;
    public String author;
    public String title;
    public int view_num;
    public String photo;
    public String content;
    public int comment_num;
    public int userid;
    public String avatar;
    public String user_nickname;
    public int is_up;
    public int is_collect;
    public int is_follow;
    public long created;
    public int is_comment; // 1可以评论  0不能评论
    public int is_coin;//0显示转圈  1领取奖励已上限不显示转圈

    public int is_original;  //0原创 1转载
      public String level;

}
