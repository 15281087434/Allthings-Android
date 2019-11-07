package songqiu.allthings.util;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/6/25
 *
 *类描述：
 *
 ********/
public class ViewProportion {
    public static LinearLayout.LayoutParams getLinearParams(View view,double proportion) {
        LinearLayout.LayoutParams linearParams =(LinearLayout.LayoutParams) view.getLayoutParams();
        int width = view.getWidth();
        long height = Math.round(width/proportion);
        linearParams.height = (int)height;
        return linearParams;
    }

    public static RelativeLayout.LayoutParams getRelativeParams(View view, double proportion) {
        RelativeLayout.LayoutParams linearParams =(RelativeLayout.LayoutParams) view.getLayoutParams();
        int width = view.getWidth();
        long height = Math.round(width/proportion);
        linearParams.height = (int)height;
        return linearParams;
    }

    public static FrameLayout.LayoutParams getFrameParams(View view, double proportion) {
        FrameLayout.LayoutParams linearParams =(FrameLayout.LayoutParams) view.getLayoutParams();
        int width = view.getWidth();
        long height = Math.round(width/proportion);
        linearParams.height = (int)height;
        return linearParams;
    }
}
