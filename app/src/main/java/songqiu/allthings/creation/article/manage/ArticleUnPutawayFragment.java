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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.Unbinder;
import songqiu.allthings.R;
import songqiu.allthings.adapter.ArticleUnPutawayAdapter;
import songqiu.allthings.adapter.HeaderViewAdapter;
import songqiu.allthings.adapter.SearchTxtAdapter;
import songqiu.allthings.base.BaseFragment;
import songqiu.allthings.bean.ArticleUnPutawayBean;
import songqiu.allthings.bean.SearchTxtBean;
import songqiu.allthings.bean.UserInfoBean;
import songqiu.allthings.http.BaseBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.http.OkHttp;
import songqiu.allthings.http.RequestCallBack;
import songqiu.allthings.iterface.DialogPrivacyListener;
import songqiu.allthings.search.SearchActivity;
import songqiu.allthings.util.CopyButtonLibrary;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.util.ToastUtil;
import songqiu.allthings.util.VibratorUtil;
import songqiu.allthings.view.DialogArticleCommon;

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
    ArticleUnPutawayAdapter adapter;
    List<ArticleUnPutawayBean> item;
    View mFooterView;
    TextView hintTv;
    HeaderViewAdapter mHeaderAdapter;

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
        initRecyc();
        getUnPutaway(pageNo,1,false);
    }

    public void initRecyc() {
        mFooterView = LayoutInflater.from(activity).inflate(R.layout.layout_source_hint, null, false);
        hintTv = mFooterView.findViewById(R.id.hintTv);
        hintTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CopyButtonLibrary copyButtonLibrary = new CopyButtonLibrary(activity, hintTv);
                copyButtonLibrary.init(hintTv);
                ToastUtil.showToast(activity, "复制成功:" + hintTv.getText().toString());
            }
        });
        item = new ArrayList<>();
        adapter = new ArticleUnPutawayAdapter(activity,item);
        mHeaderAdapter = new HeaderViewAdapter(adapter);
        mHeaderAdapter.addFooterView(mFooterView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycle.setLayoutManager(linearLayoutManager);
        recycle.setAdapter(mHeaderAdapter);
        adapter.setDeleteListener(new ArticleUnPutawayAdapter.DeleteListener() {
            @Override
            public void delete(int articleid) {
                initDeleteDialog(articleid);
            }
        });
        smartRefreshLayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                pageNo = pageNo+1;
                getUnPutaway(pageNo,1,false);
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                pageNo = 1;
                getUnPutaway(pageNo,1,true);
            }
        });
    }

    public void initDeleteDialog(int articleid) {
        DialogArticleCommon dialogArticleCommon = new DialogArticleCommon(activity,"删除","删除后内容不可恢复，确认删除吗？");
        dialogArticleCommon.setCanceledOnTouchOutside(true);
        dialogArticleCommon.setCancelable(true);
        dialogArticleCommon.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialogArticleCommon.show();
        dialogArticleCommon.setDialogPrivacyListener(new DialogPrivacyListener() {
            @Override
            public void cancel() {

            }

            @Override
            public void sure() {
                for(int i = 0;i<item.size();i++) {
                    if(item.get(i).id == articleid) {
                        item.remove(i);
                        adapter.notifyDataSetChanged();
                        if(0 == item.size()) {
                            emptyLayout.setVisibility(View.VISIBLE);
                            recycle.setVisibility(View.GONE);
                        }
                        deleteArticle(articleid);
                    }
                }
            }
        });
    }

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
                            List<ArticleUnPutawayBean> articleUnPutawayList = gson.fromJson(data, new TypeToken<List<ArticleUnPutawayBean>>() {}.getType());
                            if(pageNo ==1) {
                                item.clear();
                                if(null == articleUnPutawayList || 0 == articleUnPutawayList.size()) {
                                    emptyLayout.setVisibility(View.VISIBLE);
                                    recycle.setVisibility(View.GONE);
                                }
                            }
                            if(null != articleUnPutawayList && 0!= articleUnPutawayList.size()) {
                                item.addAll(articleUnPutawayList);
                                adapter.notifyDataSetChanged();
                            }
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

    public void deleteArticle(int articleid) {
        Map<String, Object> map = new HashMap<>();
        map.put("articleid",articleid);
        OkHttp.postObject(activity, HttpServicePath.URL_DEL_DATA, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
        });
    }
}
