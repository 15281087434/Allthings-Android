package songqiu.allthings.iterface;

import songqiu.allthings.bean.TaskSiginListBean;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/9/11
 *
 *类描述：
 *
 ********/
public interface TaskSignListener {
    void sign(TaskSiginListBean taskSiginListBean,int num);
}
