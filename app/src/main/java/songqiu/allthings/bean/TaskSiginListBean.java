package songqiu.allthings.bean;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/9/10
 *
 *类描述：
 *
 ********/
public class TaskSiginListBean {
//"num": "100",
//        "day": 1,
//        "is_sign": 0
    public int num;
    public int day;
    public int is_sign; //0、未签  1、已签

    //定义变量
    public boolean signAble; //是否可以签到
    public boolean signHighlight; //未到时间则高亮显示
    public String signed;
}
