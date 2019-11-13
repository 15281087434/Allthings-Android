package songqiu.allthings.util.theme;

import songqiu.allthings.constant.SnsConstants;

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
        return SnsConstants.URL_GUANWANG+"?id="+id+"&type="+type;
    }

    public static String getUrl(int id,int type,int isMoment) { //isMoment = 1 ????????
        return  SnsConstants.URL_GUANWANG+"?id="+id+"&type="+type+"&isMoment="+isMoment;
    }


}
