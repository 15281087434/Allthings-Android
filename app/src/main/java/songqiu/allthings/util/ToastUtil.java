package songqiu.allthings.util;

import android.content.Context;
import android.widget.Toast;

import songqiu.allthings.application.MyApplication;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/6/24
 *
 *类描述：
 *
 ********/
public class ToastUtil {
    public static void showToast(Context context, String mgs) {
        if(StringUtil.isEmpty(mgs)) return;
        Toast.makeText(context,mgs,Toast.LENGTH_SHORT).show();
    }

    public static void showToast(String mgs) {
        if(StringUtil.isEmpty(mgs)) return;
        Toast.makeText(MyApplication.getInstance(),mgs,Toast.LENGTH_SHORT).show();
    }
}
