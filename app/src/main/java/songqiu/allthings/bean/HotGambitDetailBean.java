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
public class HotGambitDetailBean {

//    "id": "1",
//            "type": "3",
//            "title": "今天天气怎么样",
//            "description": "天气描述中",
//            "photo": null,
//            "icon": null,
//            "images": null,
//            "hot_num": null,
//            "follow_num": null,
//            "created": "1568612854",
//            "updated": null,
//            "status": "1",
//            "is_follow": 0

    public int id;
    public String type;
    public String title;
    public String descriptions;
    public String photo;
    public String icon;
    public String[] images;
    public int hot_num;
    public int follow_num;
    public long created;
    public String updated;
    public String status;
    public int is_follow;
    public int is_match;//是否为参赛作品
}
