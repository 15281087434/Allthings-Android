package songqiu.allthings.util;

import android.text.TextUtils;

import songqiu.allthings.R;

/**
 * create by: ADMIN
 * time:2019/12/2510:21
 * e_mail:734090232@qq.com
 * description:
 */
public class ImageResUtils {
    public static int getLevelRes(String level) {
        if(TextUtils.isEmpty(level)){
            return 0;
        }
        int res;
        switch (level) {

            case "1":
                res = R.mipmap.auth_original;
                break;
            case "2":
                res = R.mipmap.auth_sign;
                break;
            case "3":
                res = R.mipmap.auth_official;
                break;
            default:
                res = 0;
        }
        return res;
    }
}
