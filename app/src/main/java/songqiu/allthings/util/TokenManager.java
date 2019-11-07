package songqiu.allthings.util;

import android.content.Context;
import android.content.Intent;


/**
 * Created by Administrator on 2018/2/5.
 */

public class TokenManager {
    public static String getRequestToken(Context context) {
        try {
            String token = SharedPreferencedUtils.getString(context,"SYSTOKEN");
            if (!"".equals(token) && null != token && !"null".equals(token)) {
                return token;
            } else {
//                ToastUtil.shortshow(context, "请先登录");
//                Intent intent = new Intent(context, LoginNewActivity.class);
//                context.startActivity(intent);
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
