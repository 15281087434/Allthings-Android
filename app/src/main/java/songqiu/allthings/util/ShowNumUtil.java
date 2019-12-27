package songqiu.allthings.util;

import java.text.DecimalFormat;

public class ShowNumUtil {
    public static String showUnm(int num) {
        if(num <= 9999) {
            return String.valueOf(num);
        }else {
            float strNum =(float)num/10000;
            DecimalFormat df = new DecimalFormat("0.0");
            return df.format(strNum)+"W";
        }
    }

    public static String showUnm1(int num) {
        if(num <= 9999) {
            return String.valueOf(num);
        }else {
            float strNum =(float)num/10000;
            DecimalFormat df = new DecimalFormat("0.0");
            return df.format(strNum)+"?";
        }
    }
}
