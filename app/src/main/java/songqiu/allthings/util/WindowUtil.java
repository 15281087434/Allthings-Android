package songqiu.allthings.util;
import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/8/28
 *
 *类描述：
 *
 ********/
public class WindowUtil {
    public static void windowDeploy (Activity context, PopupWindow rw, View view) {
        if(context.isFinishing()){
            return;
        }
        rw.showAtLocation(view, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        rw.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = context.getWindow().getAttributes();
                lp.alpha = 1.0f;
                context.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                context.getWindow().setAttributes(lp);
            }
        });
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = 0.3f;
        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        context.getWindow().setAttributes(lp);
    }
}
