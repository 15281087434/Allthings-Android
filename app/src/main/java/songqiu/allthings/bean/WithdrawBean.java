package songqiu.allthings.bean;

import java.util.List;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/9/16
 *
 *类描述：
 *
 ********/
public class WithdrawBean {
    public int type; //1、金币  2、现金
    public double take_money; //钱多少
    public int real_coin; //金币多少个
    public int prop_coin;//比例
    public List<WithdrawListBean> num;

}
