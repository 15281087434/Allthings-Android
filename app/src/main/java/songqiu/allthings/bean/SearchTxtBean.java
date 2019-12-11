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
public class SearchTxtBean {

//    "articleid": 154.0,
//            "title": "被鬼追的经历",
//            "descriptions": "我来到这个城市已经20年了，作为一个北漂我在这个城市里无依无靠的，除了我的事业其他的就没了，经过20年的打拼，如今我已经是一个身家上亿的成功人士了，其实梦想并不",
//            "view_num": 14667.0,
//            "module": 2.0,
//            "author": "",
//            "photo": "http://192.168.0.106:81/upload/cover/20190815/49e5a613a58cac82de98654c441ddc0e.png",
//            "created": 1.565744546E9,
//            "userid": 9.0,
//            "avatar": "http://192.168.0.106:81/upload/member/20190731/60a470c15d3aa7939fee5410dbb3925d.jpg",
//            "user_nickname": "niuniu"


    public int articleid;
    public String title;
    public String descriptions;
    public int view_num;
    public int module;
    public String author;
    public String photo;
    public long created;
    public int userid;
    public String avatar;
    public String user_nickname;
    public String video_url;
    public String[] photos;

    //收藏数
    public int collect_num;

    //标签
    public String keywords; //第一个标签
    public String [] labels; //标签组
    //标签颜色
    public int color;

    public int push_icon; //1：荐标签
    public int popular_icon; //1：热标签
    public int new_icon; //1：新标签

}
