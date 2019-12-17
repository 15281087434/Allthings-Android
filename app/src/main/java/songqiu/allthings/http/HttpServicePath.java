package songqiu.allthings.http;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/8/7
 *
 *类描述：服务器地址
 *
 ********/
public class HttpServicePath {

//    public static String BaseUrl = "https://jianguaiapp.com:100/"; //线上测试、审核
//    public static String BaseUrl = "http://192.168.0.88/"; //测试  内网
//    public static String BasePicUrl = "http://47.108.29.87:81"; //图片地址
    public static String BasePicUrl = "http://1097.oss-cn-chengdu.aliyuncs.com/"; //OSS图片前缀

    //官方安卓 511、OPPO 512、vivo 513、华为 514、应用宝 515 、UC 516、360 517、百度 518、小米 519 魅族 520
    public static String BaseUrl = "https://jianguaiapp.com:511/"; //正式 渠道

    //检测版本号
    public static String URL_VERSION = BaseUrl+"version";

    //导航   zone	否	int	1=默认首页内的导航，2=快看内导航
    public static String URL_NAVIGATION = BaseUrl +"nav";

//    //文章和视频
    public static String URL_VIDEO = BaseUrl +"lists";

    public static String URL_STRING = BaseUrl;

    //banner
    public static String URL_BANNER = BaseUrl+"banner"; //1、文章  2、话题

    //热门话题
    public static String URL_RAND_LIST = BaseUrl+"randlist";

    //所有话题列表
    public static String URL_TALKLIST = BaseUrl+"talklist";

    //动态分享  朋友圈
    public static String URL_FRIENDS = BaseUrl+"friends";

    //话题详情
    public static String URL_TALK_DETAIL = BaseUrl+"talk_detail";

    //参与话题
    public static String URL_JOIN_ADD_TALK = BaseUrl+"join_addtalk";

    //话题详情
    public static String URL_TALKLIST_DETAIL = BaseUrl+"talklist_detail";

    //关注话题
    public static String URL_FOLLOW_TALK = BaseUrl+"follow_talk";

    //取消关注话题
    public static String URL_FOLLOW_TALK_NO = BaseUrl+"follow_talk_no";

    //删除跟进话题
    public static String URL_TALK_LIST_DEL = BaseUrl+"talklist_del";

    //跟进话题 热评列表
    public static String URL_TALK_LIST_HOT = BaseUrl+"talklist_hot";

    //跟进话题 最新列表
    public static String URL_TALK_LIST_LIST = BaseUrl+"talklist_list";

    //获取验证码
    public static String URL_GET_CODE = BaseUrl +"sendsms";

    //登录
    public static String URL_LOGIN = BaseUrl +"login";

    //微信登录
    public static String URL_WECHAT_LOGIN = BaseUrl +"weixin_login";

    //QQ登录
    public static String URL_QQ_LOGIN = BaseUrl +"qq_login";

    //微信qq验证手机号
    public static String URL_LOGIN_BAN = BaseUrl +"loginban";

    //个人中心
    public static String URL_USER_CENTER = BaseUrl+"usercenter";

    //上传头像
    public static String URL_UPLOADS = BaseUrl+"uploads";

    //搜索
    public static String URL_SEARCH = BaseUrl+"search";

    //搜索热门话题
    public static String URL_SEARCH_TALK = BaseUrl+"search_talk";

    //搜索热门标签
    public static String URL_SEARCH_LABEL = BaseUrl+"search_label";

    //详情
    public static String URL_DETAILS = BaseUrl+"details";  //	1=文章，2=视频，默认1

    //点赞
    public static String URL_LIKE = BaseUrl+"up";

    //取消点赞
    public static String URL_NO_LIKE = BaseUrl+"no_up";

    //收藏
    public static String URL_COLLECT = BaseUrl+"collect";

    //取消收藏
    public static String URL_NO_COLLECT= BaseUrl+"no_collect";

    //相关视频 文章 列表 type 1、文章  2、视频  3、话题  0、文章和视频
    public static String URL_RAND= BaseUrl+"rand";

    //评论列表
    public static String URL_COMMENT = BaseUrl+"comment"; //评论类型,1=文章，2=视频，3=话题

    //添加评论
    //type	是	string	评论类型,1=文章，2=视频，3=话题
    //articleid	是	string	文章id
    //content	是	string	评论内容
    //grade	是	int	评论等级，0=一级评论，1=二级评论，2=三级评论
    //pid	是	int	上级id,0=没有上级
    public static String URL_ADD_COMMENT = BaseUrl+"add_comment";

    //删除评论
    public static String URL_DEL_COMMENT = BaseUrl+"del_comment";

    //关注
    public static String URL_ADD_FOLLOW = BaseUrl+"add_follow"; //1=添加关注，2=取消关注

    //获取关注列表
    public static String URL_USER_FOLLOW = BaseUrl+"user_follow";

    //任务列表
    public static String URL_TASKLISK= BaseUrl+"tasklist";

    //签到列表
    public static String URL_SIGNIN_LIST = BaseUrl+"signin_list";

    //签到
    public static String URL_SIGNIN = BaseUrl+"signin";

    //邀请码
    public static String URL_INVITE = BaseUrl+"invite";

    //用户信息
    public static String URL_USER_DETAIL = BaseUrl+"user_detail";

    //用户编辑
    public static String URL_EDIT_USER = BaseUrl+"edit_user";

    //收藏列表
    public static String URL_COLLECT_LIST = BaseUrl+"collect_list";

    //反馈列表
    public static String URL_FEEDBACK_LIST = BaseUrl+"feedback_list";

    //提交反馈
    public static String URL_FEED_BACK = BaseUrl+"feedback";

    //反馈详情
    public static String URL_FEEDBACK_DETAIL = BaseUrl+"feedback_detail";

    //收入记录、收益流水
    public static String URL_GET_LIST = BaseUrl+"get_list";

    //提现列表
    public static String URL_COINLIST = BaseUrl+"coinlist";

    //生成订单、提现
    public static String URL_ORDER = BaseUrl+"order";

    //绑定支付宝
    public static String URL_BIND_ZFB = BaseUrl +"bind_zfb";

    //提现记录
    public static String URL_TAKE_LOG = BaseUrl+"take_log";

    //用户页面
    public static String URL_CENTER = BaseUrl+"center"; //用户

    //用户中心
    public static String URL_MEMBER_DETAIL = BaseUrl+"member_detail";

    //消息列表
    public static String URL_NEWS = BaseUrl+"news"; //	1=互动，2=评论，3=系统

    //退出消息
    public static String URL_NEW_LAYOUT= BaseUrl+"news_layout";

    //消息列表
    public static String URL_RED_NEWS = BaseUrl+"red_news"; //红点

    //我的好友
    public static String URL_FRIEND_MONEY= BaseUrl+"friend_money";

    //文章转圈圈接口
    public static String URL_READ_ARTICLE = BaseUrl+"read_article";

    //视频转圈圈
    public static String URL_READ_VIDEO = BaseUrl+"read_video";

    //分享统计
    public static String URL_SHARE_TOTAL = BaseUrl+"share_total";

    //邀请好友参数
    public static String URL_INVITE_PARAMETER = BaseUrl+"load_conf";

    //新手红包状态
    public static String URL_NEW_RED_STATE = BaseUrl+"red_paper_status";

    //领取新手红包
    public static String URL_NEW_RED_RECEIVE = BaseUrl+"red_paper";

    //奖励规则
    public static String URL_AWARD_RULES = BaseUrl+"award_rules";

    //固定位置广告 //1=启动页，5=文章详细页，6=视频详细页，7=搜索页，8=任务页
    public static String URL_ADVERTISE = BaseUrl+"advertise";

    //不喜欢
    public static String URL_UNLIKE = BaseUrl+"interest";

    //举报  //label:0=内容低俗，1=广告软文，2=政治敏感，3=色情低俗，4=违法信息，5=错误观念引导，6=人身攻击，7=涉嫌侵权，如：[1,3,4,5]   mid:内容id    type:1=文章，2=视频，3=话题，4=评论
    public static String URL_REPORT = BaseUrl+"report";

    //添加阅读记录
    public static String URL_MY_READLOG = BaseUrl+"my_readlog"; //type	是	int	类型,1=文章，2=视频，3=话题  mid	是	int	内容id

    //添加推荐统计
    public static String URL_ADD_RES = BaseUrl+"add_res";  //article_id	是	int	文章id

    //进入app调用
    public static String URL_DEL_READLOG = BaseUrl+"del_readlog";

    //进入app 调用  退出登录调用
    public static String URL_DEL_RD = BaseUrl+"del_rd";

    //根据文章获取不喜欢
    public static String URL_REPORT_LIST = BaseUrl+"report_list"; //type=1,说明后台上传，全部显示，type=0,抓取的数据，显示最上面两条

    //评论详情 mid:一级评论id  type:1=文章，2=视频，3=话题  page页码（1）  num数量（10）
    public static String URL_COMENT_DETAIL = BaseUrl+"comment_detail";

    //全部分类
    public static String URL_ALL_LABELS = BaseUrl+"labels";

    //标签搜索
    public static String URL_LABELS_SEARCH = BaseUrl+"labels_search";


}
