package songqiu.allthings.bean;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/8/20
 *
 *类描述：
 *
 ********/
public class HomeSubitemBean {
//"articleid": 743,
//        "title": "20米长的大卡车倒车入库一气呵成！这才是真正的老司机",
//        "video_url": "https:\/\/flv2.bn.netease.com\/videolib1\/1810\/21\/caytg682T\/SD\/caytg682T-mobile.mp4",
//        "descriptions": "20米长的大卡车倒车入库一气呵成！这才是真正的老司机",
//        "view_num": 14,
//        "up_num": 6,
//        "comment_num": 12,
//        "length": 82,
//        "photo": "http:\/\/vimg.nosdn.127.net\/snapshot\/20181021\/caytg682T_cover.jpg",
//        "author": "小丸子娱乐快报",
//        "created": 1565866642,
//        "userid": 8,
//        "avatar": "http:\/\/192.168.0.106:81\/upload\/member\/20190731\/60a470c15d3aa7939fee5410dbb3925d.jpg",
//        "user_nickname": "鸡你太美",
//        "is_up": 1,
//        "is_follow": 0

//"id": "7",
//        "title": "大图无下载",
//        "category": "4",
//        "type": "1",
//        "url": "upload\/1571297009946.jpg",
//        "change_type": "2",
//        "jump_url": "http:\/\/www.jianguaiapp.com\/",
//        "start_time": "1571296980",
//        "end_time": "1571673300",
//        "video_url": "",
//        "status": "1",
//        "ad": "1"

    public int ad;
    public String jump_url;
    public String url;
    public int change_type;
    public String head_url;
    public String ad_name;

    public int id;
    public int type;
    public String video_url;
    public int articleid;
    public String title;
    public String descriptions;
    public int view_num;
    public int module; //1、纯文字  2、大图 3、小图
    public String author;
    public String photo;
    public long created;
    public int userid;
    public String avatar;
    public String user_nickname;
    public int like_count;
    public String source_url;
    public int is_up; //0未点赞  1已点赞
    public int up_num;
    public int comment_num;
    public int is_follow;//0未关注 1已关注
    public String dayTiem;
    public int ranklist;
    public String[] photos;
    public int share_num;
    //自定义一个变量
    public boolean empty;

    //标签
    public String keywords; //第一个标签
    public String[] labels; //标签组

    public int push_icon; //1：荐标签
    public int popular_icon; //1：热标签
    public int new_icon; //1：新标签
}
