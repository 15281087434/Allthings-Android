package songqiu.allthings.bean;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/9/10
 *
 *类描述：
 *
 ********/
public class UserCenterBean {
//    	"user_nickname": "",
//                "code": "160535",
//                "avatar": "http:\/\/192.168.0.106:81\/upload\/",
//                "real_coin": 0,
//                "total_coin": 0,
//                "article_num": 0,
//                "friend_num": 0,
//                "fens_num": 0,
//                "today_coin": 0
    public String user_nickname;
    public String code;
    public String avatar;
    public int userid;
    private String level;
    public int article_num;
    public int friend_num;
    public int fens_num;

    public int real_coin;
    public int total_coin;
    public int today_coin;

    public String android_url;

    public String getUser_nickname() {
        return user_nickname;
    }

    public void setUser_nickname(String user_nickname) {
        this.user_nickname = user_nickname;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public int getArticle_num() {
        return article_num;
    }

    public void setArticle_num(int article_num) {
        this.article_num = article_num;
    }

    public int getFriend_num() {
        return friend_num;
    }

    public void setFriend_num(int friend_num) {
        this.friend_num = friend_num;
    }

    public int getFens_num() {
        return fens_num;
    }

    public void setFens_num(int fens_num) {
        this.fens_num = fens_num;
    }

    public int getReal_coin() {
        return real_coin;
    }

    public void setReal_coin(int real_coin) {
        this.real_coin = real_coin;
    }

    public int getTotal_coin() {
        return total_coin;
    }

    public void setTotal_coin(int total_coin) {
        this.total_coin = total_coin;
    }

    public int getToday_coin() {
        return today_coin;
    }

    public void setToday_coin(int today_coin) {
        this.today_coin = today_coin;
    }

    public String getAndroid_url() {
        return android_url;
    }

    public void setAndroid_url(String android_url) {
        this.android_url = android_url;
    }
}
