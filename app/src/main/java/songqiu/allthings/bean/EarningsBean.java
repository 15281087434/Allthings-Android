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
public class EarningsBean {
//	"prop_coin": 10000,
//            "real_coin": 2008,
//            "total_coin": 2008,
//            "today_coin": 0

//    "prop_coin": 10000,
//            "real_money": 5000000,
//            "total_money": 5000000,
//            "today_money": 0

    public int prop_coin; //金币兑换金钱比例
    public int real_coin;
    public int total_coin;
    public int today_coin;
    public List<EarningsListBean> list;

    public String real_money;
    public String total_money;
    public String today_money;

//    public int earningsType; //1、金币  2金钱
}
