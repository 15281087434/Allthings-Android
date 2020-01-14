package songqiu.allthings.bean;

import android.os.Parcel;
import android.os.Parcelable;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/12/26
 *
 *类描述：
 *
 ********/
public class CreationIncomeBean implements Parcelable {
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


    protected CreationIncomeBean(Parcel in) {
        now_money = in.readString();
        frozen_money = in.readString();
        total_money = in.readString();
        is_bind = in.readInt();
        zfb = in.readString();
    }

    public static final Creator<CreationIncomeBean> CREATOR = new Creator<CreationIncomeBean>() {
        @Override
        public CreationIncomeBean createFromParcel(Parcel in) {
            return new CreationIncomeBean(in);
        }

        @Override
        public CreationIncomeBean[] newArray(int size) {
            return new CreationIncomeBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(now_money);
        dest.writeString(frozen_money);
        dest.writeString(total_money);
        dest.writeInt(is_bind);
        dest.writeString(zfb);
    }
}
