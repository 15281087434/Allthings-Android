package songqiu.allthings.bean;

import java.util.List;

public class DeviceInfos {
//    public String name;
//    public String mobile;
//    public String cert_no;
//    public String channel = "yoolicard_app";
//    public DeviceInfo device_info;
    public List<AppList> app_list;

//    public static class DeviceInfo extends JsonAwareObject {
//        public String mobile_operator;//运行商
//        public String device_model; //设备型号
//        public String device_name; //设备名称
//        public String system_version; //系统版本
//        public String ram_total; //内存总容量
//        public String operating_system; //操作系统
//        public String ram_free; //内存剩余量
//        public String mac_address; //MAC地址
//        public String ip_address;//ip地址
//        public int is_root;//1或0，是否已root
//        public int is_simulator;//1或0，是否使用模拟器
//        public String imei; //设备身份码（IOS传UUID），非空且保证对设备唯一不变
//        public String battery_remain; //电池当前电量
//        public String sim_status_service;//sim卡状态-服务状态
//        public String sim_status_roaming;//sim卡状态-漫游
//        public String sim_status_network;//sim卡状态-移动网络状态
//        public String sdcard_capacity;//SD卡总容量
//        public String device_resolution;//设备分辨率
//        public String log_time;//设备上的当前日期时间
//
//    }
    public static class AppList {
        public String app_name;//app名称
        public String created_time;//该app安装的日期时间
    }
    public static class AppInfoList {
        public String n;//app名称
        public String t;//该app安装的日期时间
    }
}
