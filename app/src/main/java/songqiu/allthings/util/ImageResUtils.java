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

    /**
     * 获取等级名称
     * @param level
     * @return
     */
    public static String getLevelText(String level) {
        if(TextUtils.isEmpty(level)){
            return "";
        }
        String res;
        switch (level) {

            case "1":
                res = "·官方认证原创作者";
                break;
            case "2":
                res = "·官方认证签约作者";
                break;
            case "3":
                res = "·官方";
                break;
            default:
                res = "";
        }
        return res;
    }
    /**
     * 获取等级名称 吧带点
     * @param level
     * @return
     */
    public static String getLevelTextNoPoint(String level) {
        if(TextUtils.isEmpty(level)){
            return "";
        }
        String res;
        switch (level) {

            case "1":
                res = "官方认证原创作者";
                break;
            case "2":
                res = "官方认证签约作者";
                break;
            case "3":
                res = "官方";
                break;
            default:
                res = "";
        }
        return res;
    }
}
