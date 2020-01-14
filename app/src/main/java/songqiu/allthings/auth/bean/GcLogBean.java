package songqiu.allthings.auth.bean;

import java.util.ArrayList;

/**
 * create by: ADMIN
 * time:2019/12/2514:30
 * e_mail:734090232@qq.com
 * description:稿酬收入bean
 */
public class GcLogBean {

    private String num;
    private String start;
    private ArrayList<DataBean> logdata;

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public ArrayList<DataBean> getLogdata() {
        return logdata;
    }

    public void setLogdata(ArrayList<DataBean> logdata) {
        this.logdata = logdata;
    }

    public static class DataBean{
        //         "title":"标题",
        //                "mid":"文章id",
        //                "created":"创建时间",
        //                "status":"评审状态 0 带初审 1 初审通过 2 初审失败 3 二审成功 4 二审失败 5 已发放稿酬",
        //                "remuneration":"稿酬",
        //                "fronzon":"冻结时间",

        private String id;
        private String title;

        private String mid;

        private String created;

        private String status;
        private String chanel;
        private String remuneration;

        private String fronzon;

        private String userid;

        public DataBean() {
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getMid() {
            return mid;
        }

        public void setMid(String mid) {
            this.mid = mid;
        }

        public String getCreated() {
            return created;
        }

        public void setCreated(String created) {
            this.created = created;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getChanel() {
            return chanel;
        }

        public void setChanel(String chanel) {
            this.chanel = chanel;
        }

        public String getRemuneration() {
            return remuneration;
        }

        public void setRemuneration(String remuneration) {
            this.remuneration = remuneration;
        }

        public String getFronzon() {
            return fronzon;
        }

        public void setFronzon(String fronzon) {
            this.fronzon = fronzon;
        }

        public String getUserid() {
            return userid;
        }

        public void setUserid(String userid) {
            this.userid = userid;
        }
    }
}
