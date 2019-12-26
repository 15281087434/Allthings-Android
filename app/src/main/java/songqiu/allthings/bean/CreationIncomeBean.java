package songqiu.allthings.bean;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/12/26
 *
 *类描述：
 *
 ********/
public class CreationIncomeBean {
//    "now_money": 0,
//            "frozen_money": 0,
//            "total_money": 0,
//            "is_bind": 0,
//            "zfb": null
    public String now_money; //当前金额（元）
    public String frozen_money; //冻结金额（元）
    public String total_money;  //	总金额（元）
    public int is_bind; //是否绑支付宝，0=未绑定，1=已绑定
    public String zfb;
}
