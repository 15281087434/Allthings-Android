package songqiu.allthings.bean;

import java.io.Serializable;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/12/26
 *
 *类描述：
 *
 ********/
public class SaveArticleBean implements Serializable{
//    "id": "5",
//            "title": "测试标题",
//            "description": "",
//            "photo": "",
//            "photos": null,
//            "userid": "340",
//            "content": "测试内容",
//            "keywords": "",
//            "module": "1",
//            "status": "1"
    public int id;
    public String title;
    public String description;
    public String photo;
    public String[] photos;
    public int userid;
    public String content;
    public String keywords;
    public int module;
    public int status;
}
