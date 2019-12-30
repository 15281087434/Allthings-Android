package songqiu.allthings.constant;

public class SnsConstants {
	public static final String APP_ID = "wx73baecbc28077101";
	public static final String APP_SECRET = "60f9587ea5a0e64d7ba4b88ca990b8fd";

	//h5变为gn 原因未知  老是在改
	public static String URL_BASE_H5 = "http://gn.jianguaiapp.com/";

	public static String URL_BASE_ARTICLE_H5 = "http://jianguaiapp.cn/";
	public static String URL_GUANWANG = "http://gn.jianguaiapp.com/download.html";


	public static String URL_DOWNLOAD;


//	//邀请好友 白天
//	public static String URL_INVENT_FRIEND = URL_BASE_H5+"share.html?invite_num=1&money=1&total_coin=10";
//	//邀请好友 夜间
//	public static String URL_INVENT_FRIEND_NIGHT = URL_BASE_H5+"share_black_android.html";
	//邀请好友 白天
	public static String getUrlInviteFriend(int num,double money,String total) {
		return URL_BASE_H5+"share.html?invite_num="+num+"&money="+money+"&total_coin="+total;
	}
	//邀请好友 夜间
	public static String getUrlInviteFriendNight(int num,double money,String total) {
		return URL_BASE_H5+"share_black_android.html?invite_num="+num+"&money="+money+"&total_coin="+total;
	}

	//隐私政策 白天
	public static String URL_PRIVACY = URL_BASE_H5+"privacy.html";
	//隐私政策 夜间
	public static String URL_PRIVACY_NIGHT = URL_BASE_H5+"privacy_black.html";
	//用户协议 白天
	public static String URL_USER_PROTOCOL = URL_BASE_H5+"agreement.html";
	//用户协议 夜间
	public static String URL_USER_PROTOCOL_NIGHT = URL_BASE_H5+"agreement_black.html";
	//关于我们 白天
	public static String URL_ABOUT = URL_BASE_H5+"about.html";
	//关于我们 夜间
	public static String URL_ABOUT_NIGHT = URL_BASE_H5+"about_black.html";
	//注销账号
	public static String URL_ZHUXIAO =  URL_BASE_H5+"zhuxiao_info.html";

	//作者招募计划
	public static String URL_RECRUITMENT =  URL_BASE_ARTICLE_H5+"#/explian/recruitment";

	//作者投稿计划
	public static String URL_CONTRIBUTE =  URL_BASE_ARTICLE_H5+"#/explian/contribute";


	//作者福利说明
	public static String URL_WELFARE =  URL_BASE_ARTICLE_H5+"#/explian/welfare";

	//
	public static String URL_ORIGINAL =  URL_BASE_ARTICLE_H5+"#/certification/original";

	//
	public static String URL_SIGNING =  URL_BASE_ARTICLE_H5+"#/certification/signing";




	//浏览文章
	public static String getUrlArticle(int id,int isEdit) {
//		return URL_BASE_ARTICLE_H5+"#/artical/preview_black?id="+id+"&isEdit="+isEdit;
		return "http://192.168.0.195:8080/artical/preview_black?id="+id+"&isEdit="+isEdit;
	}

}
