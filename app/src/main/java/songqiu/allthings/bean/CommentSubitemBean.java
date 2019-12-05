package songqiu.allthings.bean;

import java.io.Serializable;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/12/3
 *
 *类描述：
 *
 ********/
public class CommentSubitemBean implements Serializable{
//    	"commentid": "313",
//                "grade": "1",
//                "pid": "310",
//                "ppid": "0",
//                "type": "1",
//                "article_id": "17481",
//                "up_num": "0",
//                "created": "1575342415",
//                "content": "共产党万岁",
//                "user_id": "331",
//                "avatar": "upload\/1575274384738341.jpg",
//                "user_nickname": "晴天",
//                "is_up": 0
    public int commentid;
    public int grade;
    public int pid;
    public int ppid;
    public int type;
    public int article_id;
    public int up_num;
    public long created;
    public String content;
    public int user_id;
    public String avatar;
    public String user_nickname;
    public String nickname;
    public int is_up;
}
