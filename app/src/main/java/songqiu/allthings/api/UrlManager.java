package songqiu.allthings.api;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/8/7
 *
 *类描述：
 *
 ********/
public class UrlManager {
    public static String BASE_URL = setBaseUrl();

    public static String setBaseUrl() {
//        return "https://t.itaojin.cn/";           //线上环境
        return "http://t.itaojintest.cn/";          //本地测试环境
//        try {
//            if (SharedPreferenceUtils.isTestEnvironment()) {
//                if (SharedPreferenceUtils.isPreOnLineEnvironment()) {
//                    return "http://t1.itaojin.cn/";          //预上线环境
//                } else {
//                    return "http://t.itaojintest.cn/";          //本地测试环境
//                }
//
//            } else {
//                return "https://t.itaojin.cn/";           //线上环境
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "https://t.itaojin.cn/";
//        }

    }
}
