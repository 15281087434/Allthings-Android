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
public class CommentListBean {

//	"article_id": "116",
//            "type": "1",
//            "content": "哦兔兔",
//            "created": "1568962437",
//            "user_id": "326",
//            "avatar": "upload\/1568630286993622.jpg",
//            "user_nickname": "◆夜雨",
//            "news_status": 1

    public int article_id;
    public int type;
    public String content;
    public long created;
    public int user_id;
    public String avatar;
    public String user_nickname;
    public int news_status;
    public int talk_type; //type为3时，0 朋友圈  1跟进话题详情

}
