package songqiu.allthings.base.swipe;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/11/12
 *
 *类描述：右滑退出相关接口
 *
 ********/
public interface SwipeBackActivityBase {
    /**
     *the SwipeBackLayout associated with this activity.
     */
    public abstract SwipeBackLayout getSwipeBackLayout();


    public abstract void setSwipeBackEnable(boolean enable);

    /**
     * Scroll out contentView and finish the activity
     */
    public abstract void scrollToFinishActivity();
}
