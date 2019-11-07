package songqiu.allthings.util;

import android.content.Context;

/*******
 *
 *Created by ???
 *
 *???? 2019/9/30
 *
 *????
 *
 ********/
public class CheckLogin {
    public static boolean isLogin(Context context) {
        if(SharedPreferencedUtils.getBoolean(context,SharedPreferencedUtils.LOGIN,false)) {
            return true;
        }
        return false;
    }
}
