package songqiu.allthings.bean;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/10/28
 *
 *类描述：
 *
 ********/
public class VersionBean {
//    "current_version": "2.01",
//            "type": "1",
//            "content": "1 更新ui\r\n2 增加夜间模式",
//            "url": "http:\/\/www.baiud.com"
    public int type; //升级方式 1=提示升级 2=强制升级
    public String content;
    public String url;
}
