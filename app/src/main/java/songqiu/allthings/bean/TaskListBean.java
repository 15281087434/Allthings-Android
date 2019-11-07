package songqiu.allthings.bean;

import java.math.BigDecimal;
import java.util.List;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/9/6
 *
 *类描述：任务页面bean
 *
 ********/
public class TaskListBean {

    public int day;
    public int real_coin;
    public int total_sigin;
    public String real_money;
    public List<CustomTaskBean> task_new;

    public List<CustomTaskBean> day_task;

}
