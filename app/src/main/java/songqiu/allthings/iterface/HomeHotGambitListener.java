package songqiu.allthings.iterface;

import android.support.v7.widget.RecyclerView;

import java.util.List;

import songqiu.allthings.bean.HomeGambitHotBean;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/9/18
 *
 *类描述：
 *
 ********/
public interface HomeHotGambitListener {
    void addFollow(String url,int parentid,RecyclerView.ViewHolder viewHolder);
}
