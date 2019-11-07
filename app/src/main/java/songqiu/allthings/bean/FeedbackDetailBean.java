package songqiu.allthings.bean;

import java.util.List;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/9/12
 *
 *类描述：
 *
 ********/
public class FeedbackDetailBean {
//    "feedback_con": "my图JJ",
//            "feedback_img": "[upload\/1568268829508874.jpg]",
//            "official": null,
//            "created": 1568268835,
//            "updated": null,
//            "status": 1,
//            "avatar": "http:\/\/1097.oss-cn-chengdu.aliyuncs.com\/upload\/15682588601714.jpg",
//            "user_nickname": "德玛西亚"
    public String feedback_con;
    public List<String> feedback_img;
    public long created;
    public int status;//1、未回复  2、已回复
    public String avatar;
    public String user_nickname;
    public String official; //已回复的回复内容
}
