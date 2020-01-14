package songqiu.allthings.util;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 2016/6/23.
 */
public class DateUtil {
    private static final long ONE_MINUTE = 60;
    private static final long ONE_HOUR = 3600;
    private static final long ONE_DAY = 86400;
    private static final long ONE_MONTH = 2592000;
    private static final long ONE_YEAR = 31104000;
    private final static long minute = 60 * 1000;// 1分钟
    private final static long hour = 60 * minute;// 1小时
    private final static long day = 24 * hour;// 1天
    private final static long month = 31 * day;// 月
    private final static long year = 12 * month;// 年

    /**
     * 返回文字描述的日期
     */
    public static String getTimeFormatText(String time, String format) {
        Date date = str2Date(time,format);
        if (date == null) {
            return null;
        }
        long diff = new Date().getTime() - date.getTime();
        long r = 0;
        if (diff > year) {
            r = (diff / year);
            return r + "年前";
        }
        if (diff > month) {
            r = (diff / month);
            return r + "个月前";
        }
        if (diff > day) {
            r = (diff / day);
            return r + "天前";
        }
        if (diff > hour) {
            r = (diff / hour);
            return r + "小时前";
        }
        if (diff > minute) {
            r = (diff / minute);
            return r + "分钟前";
        }
        return "刚刚";
    }

    public static String getDate(String t) {
        if (t != null && !"null".equals(t) && !"".equals(t)) {
            long time = Long.parseLong(t);
            Date date = new Date(time);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return format.format(date);
        } else {
            return "";
        }
    }

    public static String getDateHHmm(String t) {
        if (t != null && !"null".equals(t) && !"".equals(t)) {
            long time = Long.parseLong(t);
            Date date = new Date(time);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            return format.format(date);
        } else {
            return "";
        }
    }

    public static String getDateHHmmLine(String t) {
        if (t != null && !"null".equals(t) && !"".equals(t)) {
            long time = Long.parseLong(t);
            Date date = new Date(time);
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
            return format.format(date);
        } else {
            return "";
        }
    }

    public static String getDateHM(String str) {
        if (str != null && !"null".equals(str) && !"".equals(str)) {
            long time = Long.parseLong(str);
            Date date = new Date(time);
            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            return format.format(date);
        } else {
            return "";
        }
    }

    public static String getDateType2(String t) {
        if (t != null && !"null".equals(t) && !"".equals(t)) {
            long time = Long.parseLong(t);
            Date date = new Date(time);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            return format.format(date);
        } else {
            return "";
        }
    }

    public static String getDateTypeDot(String t) {
        if (t != null && !"null".equals(t) && !"".equals(t)) {
            long time = Long.parseLong(t);
            Date date = new Date(time);
            SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
            return format.format(date);
        } else {
            return "";
        }
    }

    public static String getDateType3(String t) {
        if (t != null && !"null".equals(t) && !"".equals(t)) {
            long time = Long.parseLong(t);
            Date date = new Date(time);
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            return format.format(date);
        } else {
            return "";
        }
    }

    public static String getDateType4(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = sdf.format(date);  //当期日期
        return currentDate;
    }


    public static String getDateType5(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String currentDate = sdf.format(date);  //当期日期
        return currentDate;
    }



    public static String getDateType4(String time) {
        if (!TextUtils.isEmpty(time)) {
            long t = Long.parseLong(time);
            Date date = new Date(t);
            return getDateType4(date);
        }
        return "";
    }

    public static String getTime(String t) {
        if (t != null && !"null".equals(t) && !"".equals(t)) {
            long time = Long.parseLong(t);
            Date date = new Date(time);
            SimpleDateFormat format = new SimpleDateFormat("hh:mm");
            return format.format(date);
        } else {
            return "";
        }
    }

    public static String getTime(String timeStamp, String format) {
        if (timeStamp != null && !"null".equals(timeStamp) && !"".equals(timeStamp)) {
            long time = Long.parseLong(timeStamp);
            Date date = new Date(time);
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.format(date);
        } else {
            return "";
        }
    }

    public static String getTimeBig(String t) {
        if (t != null && !"null".equals(t) && !"".equals(t)) {
            long time = Long.parseLong(t);
            Date date = new Date(time);
            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            return format.format(date);
        } else {
            return "";
        }
    }

    public static String getTimeBig(long time) {
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(date);
    }

    public static String getTimeBig2(String t) {
        if (t != null && !"null".equals(t) && !"".equals(t)) {
            long time = Long.parseLong(t);
            Date date = new Date(time);
            SimpleDateFormat format = new SimpleDateFormat("MM/dd HH:mm");
            return format.format(date);
        } else {
            return "";
        }
    }

    public static String getTimeBig2(String t, String dateFormat) {
        if (t != null && !"null".equals(t) && !"".equals(t)) {
            long time = Long.parseLong(t);
            Date date = new Date(time);
            SimpleDateFormat format = new SimpleDateFormat(dateFormat);
            return format.format(date);
        } else {
            return "";
        }
    }

    public static String getTimeBig2(long time) {
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("MM/dd HH:mm");
        return format.format(date);
    }

    public static String getTimeBig1(long time) {
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("MM月dd日");
        return format.format(date);
    }

    public static String getTimeBig4(long time) {
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return format.format(date);
    }

    public static String getTimeBig3(long time) {
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    public static String getTimeSecond(String t) {
        try {
            if (t != null && !"null".equals(t) && !"".equals(t)) {
                long time = Long.parseLong(t);
                Date date = new Date(time);
                SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                return format.format(date);
            } else {
                return "";
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getTimetype2(String t) {
        if (t != null && !"null".equals(t) && !"".equals(t)) {
            long time = Long.parseLong(t);
            Date date = new Date(time);
            SimpleDateFormat format = new SimpleDateFormat("MM/dd");
            return format.format(date);
        } else {
            return "";
        }
    }

    public static String formatSecond(Integer second) {
        String html = "00:00:00";
        if (second != null) {
            int hours = second / (60 * 60);
            int minutes = second / 60 - hours * 60;
            int seconds = second - minutes * 60 - hours * 60 * 60;
            String hoursStr = String.format("%02d", hours);
            String minutesStr = String.format("%02d", minutes);
            String secondsStr = String.format("%02d", seconds);
            html = String.format("%s:%s:%s", hoursStr, minutesStr, secondsStr);
        }

        return html;

    }

    /**
     * 根据日期获取星期几
     *
     * @param date
     * @return
     */
    public static String getWeekOfDate(Date date) {
        String[] weekDaysName = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        String[] weekDaysCode = {"0", "1", "2", "3", "4", "5", "6"};
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int intWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        return weekDaysName[intWeek];
    }

    /**
     * 根据日期获取星期几
     *
     * @param date
     * @return
     */
    public static String getWeekOfDate2(Date date) {
        String[] weekDaysName = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        String[] weekDaysCode = {"0", "1", "2", "3", "4", "5", "6"};
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int intWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        return weekDaysName[intWeek];
    }

    public static String isNull(String str) {
        if (str == null || "".equals(str) || "null".equals(str)) {
            return "";
        } else {
            return str;
        }

    }

    public static boolean isNullStr(String str) {
        return str == null || "".equals(str) || "null".equals(str);

    }

    public static String formatLongToTimeStr(Long l) {
        int hour = 0;
        int minute = 0;
        int second = 0;
        second = l.intValue();
        if (second > 60) {
            minute = second / 60;         //取整
            second = second % 60;         //取余
        }

        if (minute > 60) {
            hour = minute / 60;
            minute = minute % 60;
        }
        String strtime = hour + ":" + minute + ":" + second;
        return strtime;

    }

    /**
     * 判断是否为今天(效率比较高)
     *
     * @param time 传入的毫秒时间
     * @return true今天 false不是
     */
    public static boolean IsToday(long time) {

        Calendar pre = Calendar.getInstance();
        pre.setTimeInMillis(System.currentTimeMillis());

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);

        if (cal.get(Calendar.YEAR) == (pre.get(Calendar.YEAR))) {
            int diffDay = cal.get(Calendar.DAY_OF_YEAR)
                    - pre.get(Calendar.DAY_OF_YEAR);

            return diffDay == 0;
        }
        return false;
    }

    /**
     * 判断是否为昨天(效率比较高)
     *
     * @param time 传入的毫妙时间
     * @return true今天 false不是
     */
    public static boolean IsYesterday(long time) {

        Calendar pre = Calendar.getInstance();
        pre.setTimeInMillis(System.currentTimeMillis());

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);

        if (cal.get(Calendar.YEAR) == (pre.get(Calendar.YEAR))) {
            int diffDay = cal.get(Calendar.DAY_OF_YEAR)
                    - pre.get(Calendar.DAY_OF_YEAR);

            return diffDay == -1;
        }
        return false;
    }

    public static boolean IsTomorrowDay(long time) {
        Calendar pre = Calendar.getInstance();
        pre.setTimeInMillis(System.currentTimeMillis());

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);

        if (cal.get(Calendar.YEAR) == (pre.get(Calendar.YEAR))) {
            int diffDay = cal.get(Calendar.DAY_OF_YEAR)
                    - pre.get(Calendar.DAY_OF_YEAR);

            return diffDay == 1;
        }
        return false;
    }

    /**
     * 计算时间差，返回XX天XX时XX分
     *
     * @param timeEnd   结束时间
     * @param timeStart 开始时间
     * @return
     */
    public static String getTimeDistance(String timeEnd, String timeStart) {
        if (!TextUtils.isEmpty(timeEnd) && !TextUtils.isEmpty(timeStart)) {
            try {
                long endTime = Long.parseLong(timeEnd);
                long startTime = Long.parseLong(timeStart);
                long diff = endTime - startTime;
                long days = diff / (1000 * 60 * 60 * 24);
                long hours = (diff - days * 1000 * 60 * 60 * 24) / (1000 * 60 * 60);
                long minutes = (diff - days * 1000 * 60 * 60 * 24 - hours * 1000 * 60 * 60) / (1000 * 60);
                return days + "天" + hours + "小时" + minutes + "分";
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            return "";
        } else {
            return "";
        }
    }

    /**
     * 计算时间差，返回小时
     *
     * @param timeEnd   结束时间
     * @param timeStart 开始时间
     * @return
     */
    public static String getHourTimeDistance(String timeEnd, String timeStart) {
        if (!TextUtils.isEmpty(timeEnd) && !TextUtils.isEmpty(timeStart)) {
            try {
                long endTime = Long.parseLong(timeEnd);
                long startTime = Long.parseLong(timeStart);
                long diff = endTime - startTime;
//                long days = diff / (1000 * 60 * 60 * 24);
//                long hours = (diff - days * 1000 * 60 * 60 * 24) / (1000 * 60 * 60);
//                long minutes = (diff - days * 1000 * 60 * 60 * 24 - hours * 1000 * 60 * 60) / (1000 * 60);
                return String.valueOf(diff);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            return "";
        } else {
            return "";
        }
    }

    //日期格式为yyyy-MM-dd HH:MM:SS
    public static long getTimeMillion(String timeDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long time = 0;
        try {
            time = sdf.parse(timeDate).getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return time;
    }

    public static long getTimeMillion(String timeDate, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        long time = 0;
        try {
            time = sdf.parse(timeDate).getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return time;
    }

    /**
     * 获取当前时间
     */
    public static String getNowTime(String dataFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dataFormat);
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        return str;
    }

    /**
     * 字符串转Date日期
     *
     * @param str
     * @return
     */
    public static Date str2Date(String str) {
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月");
            return df.parse(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Date str2Date(String str, String format) {
        try {
            SimpleDateFormat df = new SimpleDateFormat(format);
            return df.parse(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将时间转换为时间戳
     *
     * @param s
     * @return
     */
    public static String dateToStamp(String dataFormat, String s) throws Exception {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dataFormat);
        Date date = simpleDateFormat.parse(s);
        //long ts = date.getTime()/1000;
        res = String.valueOf((simpleDateFormat.parse(simpleDateFormat.format(date))).getTime() / 1000);
        return res;
    }

    /**
     * 将时间戳转为时间
     *
     * @param s
     * @return
     */
    public static String stampToDate(String dataFormat, String s) {
        if (dataFormat == null) {
            dataFormat = "yyyy年MM月dd日";
        }
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dataFormat);
        if (s == null) {
            return "";
        }
        long lt = Long.parseLong(s) * 1000;
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    /**
     * 获取日期中的天
     *
     * @param dateStr yyyy-MM-dd HH:mm:ss
     */
    public static String getDayFormStr(String dateStr) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = df.parse(dateStr);
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
            return dayFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    /**
     * 改变时间的格式
     *
     * @return
     */
    public static String changeTimeFormat(String beforeFormat, String targetFormat, String time) {
        if (TextUtils.isEmpty(time)) {
            return "";
        }
        try {
            time = dateToStamp(beforeFormat, time);
            time = stampToDate(targetFormat, time);
            return time;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String toHourMs(int time) {
        if (time <= 0) {
            return "0秒";
        }else {
            if (time < 60) {
                return time+"秒";
            }else if(3600<time && time>60) {
                return time/60+"分"+ time%60+"秒";
            }else if(60 ==time) {
                return time/60+"分";
            }else if(3600 == time){
                return time/3600+"小时";
            }else {
                int hour = 0;
                int m = 0;
                int s = 0;
                hour = time/3600;
                m = time%3600/60;
                s = time%3600%60;
                return hour+"小时"+m+"分"+s+"秒";
            }
        }
    }

    public static int getDayOfWeek() {
//        int dayNames[] = {1,2, 3, 4, 5,6};
        Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        calendar.setTime(date);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)-1;
        if(dayOfWeek <0){
            dayOfWeek=0;
        }
//        return dayNames[dayOfWeek];
        return dayOfWeek;
    }
    public static String fromToday(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        long time = date.getTime() / 1000;
        long now = new Date().getTime() / 1000;
        long ago = now - time;
        if(ago<=ONE_HOUR/60){
            return "刚刚";
        }else if (ago <= ONE_HOUR)
            return ago / ONE_MINUTE + "分钟前";
        else if (ago <= ONE_DAY)
            return ago / ONE_HOUR + "小时前";
        else if (ago <= ONE_DAY * 2)
            return "昨天" ;
//                    + calendar.get(Calendar.HOUR_OF_DAY) + "点"
//                    + calendar.get(Calendar.MINUTE) + "分";
        else if (ago <= ONE_DAY * 3)
            return "前天";
//                    + calendar.get(Calendar.HOUR_OF_DAY) + "点"
//                    + calendar.get(Calendar.MINUTE) + "分";
        else if (ago <= ONE_MONTH) {
            long day = ago / ONE_DAY;
            return day + "天前";
//            + calendar.get(Calendar.HOUR_OF_DAY) + "点"
//                    + calendar.get(Calendar.MINUTE) + "分";
        } else if (ago <= ONE_YEAR) {
            long month = ago / ONE_MONTH;
            long day = ago % ONE_MONTH / ONE_DAY;
//            return month + "个月" ;
//            + day + "天前"
//                    + calendar.get(Calendar.HOUR_OF_DAY) + "点"
//                    + calendar.get(Calendar.MINUTE) + "分";
            return (date.getMonth()+1)+"-"+date.getDay();
        } else {
//            long year = ago / ONE_YEAR;
//            int month = calendar.get(Calendar.MONTH) + 1;// JANUARY which is 0 so month+1
            return date.getYear()+"-"+(date.getMonth()+1)+"-"+date.getDay();
        }

    }
}
