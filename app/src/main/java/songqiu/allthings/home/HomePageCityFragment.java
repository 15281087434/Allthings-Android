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
import songqiu.allthings.http.BaseBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.http.OkHttp;
import songqiu.allthings.http.RequestCallBack;
import songqiu.allthings.iterface.DialogDeleteListener;
import songqiu.allthings.iterface.HomeItemListener;
import songqiu.allthings.iterface.WindowShareListener;
import songqiu.allthings.location.LocationActivity;
import songqiu.allthings.util.LogUtil;
import songqiu.allthings.util.SharedPreferencedUtils;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.util.ToastUtil;
import songqiu.allthings.util.VibratorUtil;
import songqiu.allthings.util.WindowUtil;
import songqiu.allthings.view.DialogDelete;
import songqiu.allthings.view.SharePopupWindows;

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
            public void addLike(String url, int type, int mid,HomeSubitemBean homeSubitemBean) {
                like(url,type,mid,homeSubitemBean);
            }

            @Override
            public void addFollow(int parentid, int type,List<HomeSubitemBean> item) {
                follow(parentid,type,item);
            }

            @Override
            public void delete(int position,int type) {
                initDialog(position);
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

    public void initDialog(int position) {
        DialogDelete dialogDelete = new DialogDelete(activity,3);
        dialogDelete.setCanceledOnTouchOutside(true);
        dialogDelete.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialogDelete.show();
        dialogDelete.setDialogDeleteListener(new DialogDeleteListener() {
            @Override
            public void delete1() {
                if(null == item || 0 == item.size()) return;
                item.remove(position);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void delete2() {
                if(null == item || 0 == item.size()) return;
                item.remove(position);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void delete3() {
                if(null == item || 0 == item.size()) return;
                item.remove(position);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void delete4() {

            }
        });
    }

    public void getData(int page,String city,boolean ringDown) {
        String url = HttpServicePath.BaseUrl+ tag;
        Map<String,String> map = new HashMap<>();
        if(StringUtil.isEmpty(tag)) return;
        map.put("city",city);
        map.put("num",10+"");
        map.put("page",page+"");
        OkHttp.post(activity, smartRefreshLayout,url, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                if(null != activity) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            prestrainImg.setVisibility(View.GONE);
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
