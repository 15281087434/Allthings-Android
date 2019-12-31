package songqiu.allthings.home;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import songqiu.allthings.R;
import songqiu.allthings.adapter.HomeSolictAdapter;
import songqiu.allthings.auth.bean.CashOutRecordBean;
import songqiu.allthings.bean.BannerBean;
import songqiu.allthings.bean.HomeSolictBean;
import songqiu.allthings.bean.HomeSubitemBean;
import songqiu.allthings.http.BaseBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.http.OkHttp;
import songqiu.allthings.http.RequestCallBack;
import songqiu.allthings.util.ScreenUtils;
import songqiu.allthings.util.VibratorUtil;

/**
 * create by: ADMIN
 * time:2019/12/3017:14
 * e_mail:734090232@qq.com
 * description:征文栏
 */
public class HomeSolicitFragment extends Fragment {


    int page = 1;
    List<HomeSolictBean> item = new ArrayList<>();
    HomeSolictAdapter adapter;
    @BindView(R.id.line)
    TextView line;
    @BindView(R.id.hintLayout)
    RelativeLayout hintLayout;
    @BindView(R.id.recycle)
    RecyclerView recycle;
    @BindView(R.id.iv_icon)
    ImageView ivIcon;
    @BindView(R.id.tv_message)
    TextView tvMessage;

    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout smartRefreshLayout;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_page_solicit, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        initRecycle();
        getBanner();
        getSolict();
    }


    public void initRecycle() {

        adapter = new HomeSolictAdapter(getContext(), item);
        adapter.setmCallBack(new HomeSolictAdapter.TpCallBack() {
            @Override
            public void onTp(int position) {
                //TODO 投票
                HashMap<String,String>map =new HashMap<>();
                map.put("mid",item.get(position).getArticleid());
                map.put("activityid",item.get(position).getActivityid());
                OkHttp.post(getContext(), HttpServicePath.URL_TP, map, new RequestCallBack() {
                    @Override
                    public void httpResult(BaseBean baseBean) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(baseBean.code.equals("200")){
                                    item.get(position).setSupport_num(  item.get(position).getSupport_num()+1);
                                    adapter.notifyItemChanged(position);
                                }
                            }
                        });
                    }
                });
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycle.setLayoutManager(linearLayoutManager);
        recycle.setAdapter(adapter);


        smartRefreshLayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                page++;
                getBanner();
                getSolict();
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                page = 1;
                getSolict();
            }
        });
    }

    public void getSolict() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("page", page);
        OkHttp.post(getActivity(), smartRefreshLayout, HttpServicePath.URL_SOLICIT, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                Gson gson = new Gson();
                String data = gson.toJson(baseBean.data);
                if(TextUtils.isEmpty(data)){
                    return;
                }

                List<HomeSolictBean> beans = gson.fromJson(data, new TypeToken<List<HomeSolictBean>>() {
                }.getType());

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(page ==1) {
                            item.clear();
//                            if(null == beans || 0 == beans.size()) {
//                                emptyLayout.setVisibility(View.VISIBLE);
//                                recycle.setVisibility(View.GONE);
//                            }else {
//                                emptyLayout.setVisibility(View.GONE);
//                                recycle.setVisibility(View.VISIBLE);
//                            }
                        }
                        if(null == beans || 0==beans.size()) {
//                            for (int i = 0; i < 10; i++) {
//                                HomeSolictBean bean = new HomeSolictBean();
//                                bean.setTitle("Test" + i);
//                                bean.setCreated("2019-2");
//                                bean.setUser_nickname("Test" + i);
//                                bean.setSupport_num((10 - i) );
//                                bean.setDescriptions("Test" + i);
//                                beans.add(bean);
//                            }
                            return;
                        }
                        item.addAll(beans);
                        adapter.notifyDataSetChanged();
                    }
                });

            }
        });
    }

    public void getBanner() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("type", 5);
        OkHttp.post(getActivity(), smartRefreshLayout, HttpServicePath.URL_BANNER, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                Gson gson = new Gson();
                String data = gson.toJson(baseBean.data);

                if(TextUtils.isEmpty(data)){
                    return;
                }
                List<BannerBean> bannerBeanList = gson.fromJson(data, new TypeToken<List<BannerBean>>() {
                }.getType());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(bannerBeanList!=null) {
                            bannerBeanList.clear();
                            adapter.setBannerBeans(bannerBeanList);
                        }
                    }
                });

            }
        });
    }
}
