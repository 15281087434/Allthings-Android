package songqiu.allthings.bean;

import java.io.Serializable;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/9/10
 *
 *类描述：
 *
 ********/
public class AdvertiseBean implements Serializable{
//"id": "8",
//        "title": "erfsdfsd",
//        "category": "8",
//        "type": "1",
//        "url": "upload\/1571297009946.jpg",
//        "change_type": "2",
//        "jump_url": "http:\/\/www.baidu.com",
//        "start_time": "1571296980",
//        "end_time": "1571673300",
//        "video_url": "http:\/\/www.baidu.com",
//        "status": "1"

    public int id;
    public String title;
    public int category;
    public int type;//	1=广告图片，2=视频广告
    public String url; //图片地址或视频图片地址
    public int change_type;//	图片或视频样式分类:1=大图有下载，2=大图无下载，3=小图，4=视频无下载，5=视频有下载
    public String jump_url; //跳转出去的地址
    public long start_time; //开始时间
    public long end_time; //结束时间
    public String video_url; //视频播放地址

}
