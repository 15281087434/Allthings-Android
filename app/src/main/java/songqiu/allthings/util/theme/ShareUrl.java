package songqiu.allthings.util.theme;

/*******
 *
 *Created by ???
 *
 *???? 2019/9/25
 *
 *????
 *
 ********/
public class ShareUrl {
    public static String getUrl(int id,int type) { //1?? 2??  3??
        return  "http://h5.jianguaiapp.com/download.html?id="+id+"&type="+type;
    }

    public static String getUrl(int id,int type,int isMoment) { //isMoment = 1 ????????
        return  "http://h5.jianguaiapp.com/download.html?id="+id+"&type="+type+"&isMoment="+isMoment;
    }


}
