package songqiu.allthings.Event;


import android.support.v7.widget.RecyclerView;

import songqiu.allthings.bean.EarningsBean;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/8/19
 *
 *类描述：
 *
 ********/

public class EventTags {


    //新增消息页面MessageConversation增加未读消息传递到MessageActivity显示
    public static class UnReadMessageCount {
        int count;

        public UnReadMessageCount(int count) {
            this.count = count;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }



    public static class TransmitCity {
        public String currentCity;
        public TransmitCity(String currentCity) {
            this.currentCity = currentCity;
        }

        public String getCity() {
            return currentCity;
        }

        public void setCity(String currentCity) {
            this.currentCity = currentCity;
        }
    }

    public static class Attention {
        public int userId;
        public int is_follow;
        public Attention(int userId,int is_follow) {
            this.userId = userId;
            this.is_follow = is_follow;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public int getFollow() {
            return is_follow;
        }

        public void setFollow(int is_follow) {
            this.is_follow = is_follow;
        }
    }

    public static class ToLogin {

    }

    public static class HomeNoNetwork {

    }

    public static class LookNoNetwork {

    }

    public static class Advertising {

    }

    public static class HidePrestrain {

    }

    public static class TaskNoNetwork {
        public boolean type;
        public TaskNoNetwork(boolean type) {
            this.type = type;
        }

        public boolean getType() {
            return type;
        }

        public void setType(boolean type) {
            this.type = type;
        }
    }

    public static class DayMoulde {
        public boolean isDay;
        public DayMoulde(boolean isDay) {
            this.isDay = isDay;
        }

        public boolean getMoulde() {
            return isDay;
        }

        public void setMoulde(boolean isDay) {
            this.isDay = isDay;
        }
    }

    public static class ShowDot { //是否显示红点
        public boolean show;
        public int position;
        public ShowDot(boolean show,int position) {
            this.show = show;
            this.position = position;
        }
        public boolean getShow() {
            return show;
        }

        public void setShow(boolean show) {
            this.show = show;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }
    }

    public static class RefreshLook { //刷新视频列表
        public String url;
        public int mid;
        public RefreshLook(String url,int mid) {
            this.url = url;
            this.mid = mid;
        }
        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getMid() {
            return mid;
        }

        public void setMid(int mid) {
            this.mid = mid;
        }
    }

    public static class  RefreshDot { //刷新
        public int position;
        public RefreshDot(int position) {
            this.position = position;
        }
        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }
    }


    public static class LoginSucceed {

    }

    public static class LoginLose {

    }

    public static class BaindedPhone {

    }

    public static class GambitRefresh {

    }

    public static class HotGambitDetailRefresh {

    }

    public static class TaskRefresh {

    }

    public static class ColseLookVideo {

    }

    public static class ToBaindPhone {
        public int type;
        public ToBaindPhone(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }

    public static class ToJump {
        public int position;
        public int childPosition;
        public ToJump(int position,int childPosition) {
            this.position = position;
            this.childPosition = childPosition;
        }

        public int getJump() {
            return position;
        }

        public void setJump(int position) {
            this.position = position;
        }

        public int getChildPosition() {
            return childPosition;
        }

        public void setChildPosition(int childPosition) {
            this.childPosition = childPosition;
        }
    }

    public static class ChooseCity {
        public String city;
        public ChooseCity(String city) {
            this.city = city;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }
    }

}
