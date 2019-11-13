package songqiu.allthings.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/11/13
 *
 *类描述：
 *
 ********/
public class CustomScrollViewPager extends ViewPager {
    //是否可以左右滑动？true 可以，像Android原生ViewPager一样。
    // false 禁止ViewPager左右滑动。
    // 是否禁止 viewpager 左右滑动
    private boolean noScroll = false;

    public CustomScrollViewPager(Context context) {
        super(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        if (noScroll){
            return false;
        }else{
            return super.onTouchEvent(arg0);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        if (noScroll){
            return false;
        }else{
            return super.onInterceptTouchEvent(arg0);
        }
    }

    public void setScrollable(boolean scrollable) {
        noScroll = !scrollable;
    }
}

