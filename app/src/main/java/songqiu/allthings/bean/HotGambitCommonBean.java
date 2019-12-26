package songqiu.allthings.bean;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/9/18
 *
 *类描述：
 *
 ********/
public class HotGambitCommonBean {
//    "userid": "19",
//            "description": "今天是星期二，天气雨",
//            "images": null,
//            "up_num": "0",
//            "comment_num": "0",
//            "created": "1568616950",
//            "avatar": "member\/20190828\/9f1a638d3c50e86518b22f726bab52cd.jpg",
//            "user_nickname": "我是09",
//            "num": 0,
//            "is_follow": 0

    public int id;
    public int userid;
    public String descriptions;
    public String[] images;
    public int up_num;
    public int comment_num;
    public long created;
    public String avatar;
    public String user_nickname;
    public int num; //0、无图  1、大图 2、多图
    public int is_follow;
    public int is_up;
    public int share_num; //分享数
      public String level;
}
