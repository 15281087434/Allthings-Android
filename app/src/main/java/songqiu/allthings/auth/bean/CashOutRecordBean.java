package songqiu.allthings.auth.bean;

import java.util.ArrayList;

/**
 * create by: ADMIN
 * time:2019/12/269:27
 * e_mail:734090232@qq.com
 * description:稿酬提现纪录bean
 */
public class CashOutRecordBean {
    //{"code":"200","msg":"返回成功","data":[{"order_id":"A884D9DE7AE5F31D44BC2E8BD16006C4","userid":"449","type":"3","num":9,"zfb":"15882205742","zfb_name":"李洪波","created":"1577270807","flag":"1","pay_name":"进行中"}]}
    //
    //

    private String order_id;
    private String userid;
    private String type;
    private int num;
    private String zfb;
    private String zfb_name;
    private String created;
    private String flag;
    private String pay_name;

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getZfb() {
        return zfb;
    }

    public void setZfb(String zfb) {
        this.zfb = zfb;
    }

    public String getZfb_name() {
        return zfb_name;
    }

    public void setZfb_name(String zfb_name) {
        this.zfb_name = zfb_name;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getPay_name() {
        return pay_name;
    }

    public void setPay_name(String pay_name) {
        this.pay_name = pay_name;
    }
}
