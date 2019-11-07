package songqiu.allthings.bean;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/9/16
 *
 *类描述：
 *
 ********/
public class WithdrawRecordBean {
//    	"order_id": "22C1D21154B6C39EF0806482E4AB4DDE",
//                "userid": 326,
//                "type": 2,
//                "num": 50,
//                "zfb": "15281087434",
//                "zfb_name": "杨延辉",
//                "created": 1568624549,
//                "flag": 1,
//                "pay_name": "进行中",
//                "take_type": "支付宝提现"
    public String order_id;
    public int userid;
    public int type;
    public int num;
    public String zfb;
    public String zfb_name;
    public long created;
    public int flag;  //订单状态，1=生成订单，2=审核通过 ==> 进行中    3=订单成功（支付成功） ==>已到账   4=审核不通过，5=支付失败==>未成功
    public String pay_name;
    public String take_type;

}
