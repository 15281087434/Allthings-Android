package songqiu.allthings.mine.userpage;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import songqiu.allthings.Event.EventTags;
import songqiu.allthings.R;
import songqiu.allthings.adapter.UserImageTextAdapter;
import songqiu.allthings.articledetail.ArticleDetailActivity;
import songqiu.allthings.base.BaseFragment;
import songqiu.allthings.bean.HomeAttentionBean;
import songqiu.allthings.bean.HomeSubitemBean;
import songqiu.allthings.bean.ReportBean;
import songqiu.allthings.bean.UserPagerBean;
import songqiu.allthings.http.BaseBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.http.OkHttp;
import songqiu.allthings.http.RequestCallBack;
import songqiu.allthings.iterface.UserPagerListenner;
import songqiu.allthings.iterface.WindowShareListener;
import songqiu.allthings.util.SharedPreferencedUtils;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.util.ToastUtil;
import songqiu.allthings.util.VibratorUtil;
import songqiu.allthings.util.WindowUtil;
import songqiu.allthings.view.ReportPopupWindows;
import songqiu.allthings.view.SharePopupWindows;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/9/17
 *
 *类描述：图文
 *
 ********/
public class UserImageTextFragment extends BaseFragment {
    @BindView(R.id.line)
    TextView line;
    @BindView(R.id.recycle)
    RecyclerView recycle;
    @BindView(R.id.iv_icon)
    ImageView ivIcon;
    @BindView(R.id.tv_message)
    TextView tvMessage;
    @BindView(R.id.emptyLayout)
    LinearLayout emptyLayout;
    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout smartRefreshLayout;

    public int userId;
    int page = 1;
    List<UserPagerBean> item;
    UserImageTextAdapter adapter;

    UserPagerActivity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (UserPagerActivity) context;//保存Context引用
    }
    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }

    @Override
    public int initView() {
        return R.layout.fragment_user_all;
    }


    @Override
    public void init() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        initRecycle();
        getData(page,false);
    }

    public void initRecycle() {
        item = new ArrayList<>();
        adapter = new UserImageTextAdapter(activity,item);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycle.setLayoutManager(linearLayoutManager);
        recycle.setAdapter(adapter);

        adapter.setUserPagerListenner(new UserPagerListenner() {
            @Override
            public void addLike(String url, int type, int mid, UserPagerBean userPagerBean) {
                like(url, type, mid, userPagerBean);
            }

            @Override
            public void share(int type,int position,int shareType) {
                if(null == item || 0 == item.size()) return;
                showReportWindow(item.get(position).articleid,item.get(position).type);
            }

            @Override
            public void delete() {

            }
        });

        smartRefreshLayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                page = page+1;
                getData(page,false);
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                page = 1;
                getData(page,true);
            }
        });
    }

    //举报弹窗
    public void showReportWindow(int mid,int type) {
        ReportPopupWindows rw = new ReportPopupWindows(activity, reportList(),mid,type);
        WindowUtil.windowDeploy(activity, rw, line);
    }

    private List<String> getReportTitle() {
        return Arrays.asList("内容低劣", "广告软文", "政治敏感", "色情低俗", "违法信息", "错误观念引导", "人身攻击", "涉嫌侵犯");
    }

    public List<ReportBean> reportList() {
        List<ReportBean> list = new ArrayList<>();
        List<String> titleList = getReportTitle();
        for (String title : titleList) {
            ReportBean reportBean = new ReportBean();
            reportBean.title = title;
            list.add(reportBean);
        }
        return list;
    }

    public void getData(int page,boolean ringDown) {
        Map<String,Object> map = new HashMap<>();
        map.put("type",1);
        map.put("userid",userId);
        map.put("num",10);
        map.put("page",page);
        OkHttp.post(activity, smartRefreshLayout, HttpServicePath.URL_CENTER, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                if(null != activity) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Gson gson = new Gson();
                            String data = gson.toJson(baseBean.data);
                            List<UserPagerBean> userPagerList = gson.fromJson(data, new TypeToken<List<UserPagerBean>>() {}.getType());
                            if(page ==1) {
                                item.clear();
                                if(null == userPagerList || 0 == userPagerList.size()) {
                                    emptyLayout.setVisibility(View.VISIBLE);
                                    recycle.setVisibility(View.GONE);
                                }else {
                                    emptyLayout.setVisibility(View.GONE);
                                    recycle.setVisibility(View.VISIBLE);
                                }
                            }
                            if(null != userPagerList && 0!=userPagerList.size()) {
                                item.addAll(userPagerList);
                                adapter.notifyDataSetChanged();
                            }
                            if(ringDown) {
                                new Handler().postDelayed(new Runnable(){
                                    public void run() {
                                        //execute the task
                                        VibratorUtil.ringDown(activity);
                                    }
                                }, 500);
                            }
                        }
                    });
                }
            }
        });
    }

    //点赞/取消点赞
    public void like(String url,int type,int mid,UserPagerBean userPagerBean) {
        Map<String,String> map = new HashMap<>();
        map.put("type",type+"");
        map.put("mid",mid+"");
        OkHttp.post(activity, url, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                if(null != activity) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(url.equals(HttpServicePath.URL_LIKE)) {
                                userPagerBean.is_up = 1;
                                userPagerBean.up_num = userPagerBean.up_num+1;
                            }else {
                                userPagerBean.is_up = 0;
                                userPagerBean.up_num = userPagerBean.up_num-1;
                            }
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }

    //视频评论数
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void videoCommentNum(EventTags.VideoCommentNum videoCommentNum) {
        if(null == item || 0 == item.size()) return;
        for(int i = 0;i<item.size();i++) {
            if(item.get(i).articleid == videoCommentNum.getId()) {
                if(!StringUtil.isEmpty(videoCommentNum.getNum())) {
                    item.get(i).comment_num = Integer.valueOf(videoCommentNum.getNum());
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshLook(EventTags.RefreshLook refreshLook) {
        if(null == item || 0 == item.size()) return;
        if(refreshLook.url.equals(HttpServicePath.URL_LIKE)) {
            for(int i =0;i<item.size();i++) {
                if(item.get(i).articleid == refreshLook.mid) {
                    item.get(i).is_up = 1;
                    item.get(i).up_num = item.get(i).up_num+1;
                    adapter.notifyDataSetChanged();
                }
            }
        }else {
            for(int i =0;i<item.size();i++) {
                if(item.get(i).articleid == refreshLook.mid) {
                    item.get(i).is_up = 0;
                    item.get(i).up_num = item.get(i).up_num-1;
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
