package songqiu.allthings.bean;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/12/26
 *
 *类描述：
 *
 ********/
public class ArticlePutawayBean {
    public int id;
    public String title;
    public long created;
    public int is_apply; //1=已申请稿酬，0=未申请稿酬
    public int view_num;
    public int collect_num;
    public int article_level; //作品评级,0=不予评级,1=A,2=B,3=C,4=D,5=T
    public int level; //用户等级，1=原创，2=签约 0 只显示下架
    public int is_show;
    public int is_sb;
}
