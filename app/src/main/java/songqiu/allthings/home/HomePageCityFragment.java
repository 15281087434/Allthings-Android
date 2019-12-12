package songqiu.allthings.home;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.heartfor.heartvideo.video.HeartVideo;
import com.heartfor.heartvideo.video.HeartVideoManager;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import songqiu.allthings.Event.EventTags;
import songqiu.allthings.R;
import songqiu.allthings.activity.MainActivity;
import songqiu.allthings.adapter.HomeTabCityAdapter;
import songqiu.allthings.adapter.HomeTabClassAdapter;
import songqiu.allthings.base.BaseFragment;
import songqiu.allthings.bean.HomeSubitemBean;
import songqiu.allthings.bean.UnLikeBean;
import songqiu.allthings.http.BaseBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.http.OkHttp;
import songqiu.allthings.http.RequestCallBack;
import songqiu.allthings.iterface.DialogDeleteListener;
import songqiu.allthings.iterface.HomeItemListener;
import songqiu.allthings.location.LocationActivity;
import songqiu.allthings.util.SharedPreferencedUtils;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.util.ToastUtil;
import songqiu.allthings.util.VibratorUtil;
import songqiu.allthings.view.DialogDeleteCommon;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/9/3
 *
 *类描述：
 *
 ********/
public class HomePageCityFragment extends BaseFragment {

    @BindView(R.id.recycle)
    RecyclerView recycle;
    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout smartRefreshLayout;
    @BindView(R.id.emptyLayout)
    LinearLayout emptyLayout;
    @BindView(R.id.line)
    TextView line;
    @BindView(R.id.locationLayout)
    RelativeLayout locationLayout;
    @BindView(R.id.prestrainImg)
    ImageView prestrainImg;
    @BindView(R.id.hintLayout)
    RelativeLayout hintLayout;

    public String tag;

    List<HomeSubitemBean> item ;
    HomeTabCityAdapter adapter;
    int pageNo = 1;
    String city;

    MainActivity activity;
    boolean visible;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;//保存Context引用
    }
    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }
    @Override
    public int initView() {
        return R.layout.fragment_home_page_city;
    }

    @Override
    public void init() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        initRecycle();
        locationLayout.setVisibility(View.VISIBLE);
        city = SharedPreferencedUtils.getString(activity,"LOCATION_CITY");
        getData(pageNo,city,false);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        visible = isVisibleToUser;
        if(!isVisibleToUser) {
            HeartVideoManager.getInstance().pause();
        }
    }


    public void initRecycle() {
        item = new ArrayList<>();
        adapter = new HomeTabCityAdapter(activity,item);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycle.setLayoutManager(linearLayoutManager);
        recycle.setAdapter(adapter);

        adapter.setHomeItemListener(new HomeItemListener() {
            @Override
            public void addSetting(int position) {
//                showShareWindow(1);
            }

            @Override
            public void addLike(String url, int type, int mid,HomeSubitemBean homeSubitemBean,RecyclerView.ViewHolder viewHolder) {
                like(url,type,mid,homeSubitemBean);
            }

            @Override
            public void addFollow(int parentid, int type,List<HomeSubitemBean> item) {
                follow(parentid,type,item);
            }

            @Override
            public void delete(int position,int type) {
                if(null != item && 0!= item.size()) {
                    getUnLikeParameter(item.get(position).articleid,item.get(position).type,position);
                }
//                initDialog(position);
            }
        });


        smartRefreshLayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                pageNo = pageNo+1;
                getData(pageNo,city,false);
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                pageNo = 1;
                getData(pageNo,city,true);
            }
        });
    }

    //不喜欢列表
    public void getUnLikeParameter(int articleid,int type,int position) {
        Map<String, String> map = new HashMap<>();
        map.put("articleid",String.valueOf(articleid));
        map.put("type",type+"");
        OkHttp.post(activity, HttpServicePath.URL_REPORT_LIST, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        String data = gson.toJson(baseBean.data);
                        if (StringUtil.isEmpty(data)) return;
                        UnLikeBean unLikeBean = gson.fromJson(data, UnLikeBean.class);
                        if(null == unLikeBean) return;
                        initDialog(unLikeBean,position);
                    }
                });
            }
        });
    }

    //调用不喜欢接口
    public void unLike(int bid,int mid,int type) {
        //bid 1=不敢兴趣，2=反馈垃圾内容，3=拉黑作者
        //mid 文章视频id
        //type 1=文章，2=视频
        Map<String, String> map = new HashMap<>();
        map.put("bid",bid+"");
        map.put("mid",mid+"");
        map.put("type",type+"");
        OkHttp.post(activity, HttpServicePath.URL_UNLIKE, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {

            }
        });
    }


    public void doDeletel(int position,int bid,int mid) {
        if(1 == item.get(position).type) {
            unLike(bid,mid,1);
        }else {
            unLike(bid,mid,2);
        }
        item.remove(position);
        adapter.notifyDataSetChanged();
        ToastUtil.showToast(activity,"将减少推荐类似内容");
    }


    public void initDialog(UnLikeBean unLikeBean,int position) {
        DialogDeleteCommon dialog = new DialogDeleteCommon(activity,unLikeBean,true);
        dialog.showDialog();
        dialog.setOnItemClickListener(new DialogDeleteCommon.OnItemClick() {
            @Override
            public void onWhichItemClick(int pos) {
                switch (pos) {
                    case 1:
                        if(null == item || 0 == item.size()) return;
                        doDeletel(position,1,item.get(position).articleid);
                        break;
                    case 2:
                        if(null == item || 0 == item.size()) return;
                        doDeletel(position,2,item.get(position).articleid);
                        break;
                    case 3:
                        if(null == item || 0 == item.size()) return;
                        doDeletel(position,3,item.get(position).articleid);
                        break;
                    case 4:
                        if(null == item || 0 == item.size()) return;
                        doDeletel(position,4,item.get(position).articleid);
                        break;
                    case 5:

                        break;
                }
            }
        });
    }

    public void getData(int page,String city,boolean ringDown) {
        String url = HttpServicePath.BaseUrl+ tag;
        Map<String,Object> map = new HashMap<>();
        if(StringUtil.isEmpty(tag)) return;
        map.put("city",city);
        map.put("num",10);
        map.put("page",page);
        OkHttp.post(activity, smartRefreshLayout,url, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                if(null != activity) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Gson gson = new Gson();
                            String data = gson.toJson(baseBean.data);
                            List<HomeSubitemBean> classifyBeanList = gson.fromJson(data, new TypeToken<List<HomeSubitemBean>>() {}.getType());
                            if(pageNo ==1) {
                                item.clear();
                                if(null == classifyBeanList || 0 == classifyBeanList.size()) {
                                    emptyLayout.setVisibility(View.VISIBLE);
                                    recycle.setVisibility(View.GONE);
                                }else {
                                    emptyLayout.setVisibility(View.GONE);
                                    recycle.setVisibility(View.VISIBLE);
                                }
                            }
                            if(null != classifyBeanList && 0!=classifyBeanList.size()) {
                                item.addAll(classifyBeanList);
                                adapter.notifyDataSetChanged();
                            }
                            if(ringDown) {
                                hintLayout.setVisibility(View.VISIBLE);
                                new Handler().postDelayed(new Runnable(){
                                    public void run() {
                                        //execute the task
                                        hintLayout.setVisibility(View.GONE);
                                    }
                                }, 2000);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void chooseCity(EventTags.ChooseCity chooseCity) {
        pageNo = 1;
        String city = chooseCity.getCity();
        this.city = city;
        getData(pageNo,city,false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void homeRefresh(EventTags.HomeRefresh homeRefresh) {
        if(visible) {
            smartRefreshLayout.autoRefresh();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void hidePrestrain(EventTags.HidePrestrain hidePrestrain) {
        prestrainImg.setVisibility(View.GONE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void toDeleteItemById(EventTags.DeleteItemById deleteItemById) {
        if(null == item || 0 == item.size()) return;
        for(int i = 0;i<item.size();i++) {
            if(item.get(i).articleid == deleteItemById.getId()) {
                item.remove(i);
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void attention(EventTags.Attention attention) {
        if(null == item || 0 == item.size()) return;
        if(null == adapter) return;
        for(int i = 0;i<item.size();i++) {
            if(item.get(i).userid == attention.getUserId()) {
                item.get(i).is_follow = attention.getFollow();
            }
        }
        adapter.notifyDataSetChanged();
    }

    //点赞/取消点赞
    public void like(String url,int type,int mid,HomeSubitemBean homeSubitemBean) {
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
                                homeSubitemBean.is_up = 1;
                                homeSubitemBean.up_num = homeSubitemBean.up_num+1;
                            }else {
                                homeSubitemBean.is_up = 0;
                                homeSubitemBean.up_num = homeSubitemBean.up_num-1;
                            }
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }

    //用户id 	type、1=添加关注，2=取消关注
    public void follow(int parentid,int type,List<HomeSubitemBean> item) {
        Map<String, String> map = new HashMap<>();
        map.put("parentid", parentid + "");
        map.put("type", type + "");
        OkHttp.post(activity, HttpServicePath.URL_ADD_FOLLOW, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                if(null != activity) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(1 == type) {
                                for(int i = 0;i<item.size();i++) {
                                    if(parentid == item.get(i).userid) {
                                        item.get(i).is_follow = 1;
                                    }
                                }
                                EventBus.getDefault().post(new EventTags.Attention(parentid,1));
                            }else {
                                for(int i = 0;i<item.size();i++) {
                                    if(parentid == item.get(i).userid) {
                                        item.get(i).is_follow = 0;
                                    }
                                }
                                EventBus.getDefault().post(new EventTags.Attention(parentid,0));
                            }
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        HeartVideoManager.getInstance().release();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @OnClick(R.id.locationLayout)
    public void onLocationClick() {
        Intent intent = new Intent(activity, LocationActivity.class);
        startActivity(intent);
    }
}
