package songqiu.allthings.creation.article.manage;

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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.Unbinder;
import songqiu.allthings.R;
import songqiu.allthings.adapter.SearchTxtAdapter;
import songqiu.allthings.base.BaseFragment;
import songqiu.allthings.bean.SearchTxtBean;
import songqiu.allthings.http.BaseBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.http.OkHttp;
import songqiu.allthings.http.RequestCallBack;
import songqiu.allthings.search.SearchActivity;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.util.VibratorUtil;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/12/24
 *
 *类描述：未上架
 *
 ********/
public class ArticleUnPutawayFragment extends BaseFragment {
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

    int pageNo = 1;

    ArticleManageActivity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (ArticleManageActivity) context;//保存Context引用
    }
    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }

    @Override
    public int initView() {
        return R.layout.fragment_article_unputaway;
    }

    @Override
    public void init() {
//        initRecyc();
        getUnPutaway(pageNo,1,false);
    }

//    public void initRecyc() {
//        adapter = new SearchTxtAdapter(activity,item);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
//        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        recycle.setLayoutManager(linearLayoutManager);
//        recycle.setAdapter(adapter);
//
//        smartRefreshLayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
//            @Override
//            public void onLoadmore(RefreshLayout refreshlayout) {
//                pageNo = pageNo+1;
//                getTxtSearch(pageNo,activity.keyword,false);
//            }
//
//            @Override
//            public void onRefresh(RefreshLayout refreshlayout) {
//                pageNo = 1;
//                getTxtSearch(pageNo,activity.keyword,true);
//            }
//        });
//    }

    public void getUnPutaway(int pageNo,int type,boolean ringDown) {
        Map<String,Object> map = new HashMap<>();
        map.put("type",type); //1未上架
        map.put("num",10);
        map.put("page",pageNo);
        OkHttp.post(activity, smartRefreshLayout, HttpServicePath.URL_MANAGE_DATA, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                if(null != activity) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Gson gson = new Gson();
                            String data = gson.toJson(baseBean.data);
                            if(StringUtil.isEmpty(data)) return;
//                            List<SearchTxtBean> searchTxtList = gson.fromJson(data, new TypeToken<List<SearchTxtBean>>() {}.getType());
//                            if(pageNo ==1) {
//                                item.clear();
//                                if(null == searchTxtList || 0 == searchTxtList.size()) {
//                                    emptyLayout.setVisibility(View.VISIBLE);
//                                    smartRefreshLayout.setVisibility(View.GONE);
//                                }
//                            }
//                            if(null != searchTxtList && 0!= searchTxtList.size()) {
//                                item.addAll(searchTxtList);
//                                adapter.notifyDataSetChanged();
//                            }
                            if(ringDown) {
                                new Handler().postDelayed(new Runnable(){
                                    public void run() {
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
}
