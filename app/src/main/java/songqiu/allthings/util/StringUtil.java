package songqiu.allthings.util;

import java.text.DecimalFormat;

/**
 * ***********************************************
 * Created by 杨延辉
 * 创建时间：2019/6/20.
 * 类描述：
 * ***********************************************
 */
public class StringUtil {
    /**
     * 判断字符串是否为null 或者""
     * @param msg
     * @return
     */
    public static Boolean isEmpty(String msg) {
        return "".equals(msg) || null == msg || "null".equals(msg)  ? true : false;
    }

    public static String checkNull(String content){
        return null==content||"null".equals(content)?"":content;
    }

    public static boolean isEmptyString(String s) {
        if (s == null) {
            return true;
        }
        int length = s.length();
        for (int i = 0; i < length; i++) {
            if (s.charAt(i) != ' ') {
                return false;
            }
        }
        return true;
    }

}
