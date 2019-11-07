package songqiu.allthings.mine.attention;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import songqiu.allthings.R;
import songqiu.allthings.adapter.AttentionGambitAdapter;
import songqiu.allthings.adapter.AttentionUserAdapter;
import songqiu.allthings.base.BaseFragment;
import songqiu.allthings.bean.AttentionGambitBean;
import songqiu.allthings.bean.AttentionUserBean;
import songqiu.allthings.http.BaseBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.http.OkHttp;
import songqiu.allthings.http.RequestCallBack;
import songqiu.allthings.iterface.CancelAttentionGambitListener;
import songqiu.allthings.iterface.CancelAttentionListener;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/8/29
 *
 *类描述：
 *
 ********/
public class AttentionGambitFragment extends BaseFragment {
    @BindView(R.id.recycle)
    RecyclerView recycle;

    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout smartRefreshLayout;
    @BindView(R.id.iv_icon)
    ImageView ivIcon;
    @BindView(R.id.tv_message)
    TextView tvMessage;
    @BindView(R.id.emptyLayout)
    LinearLayout emptyLayout;

    List<AttentionGambitBean> item ;
    AttentionGambitAdapter adapter;

    AttentionActivity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (AttentionActivity) context;//保存Context引用
    }
    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }

    @Override
    public int initView() {
        return R.layout.fragment_attent_list;
    }

    @Override
    public void init() {
        initRecycle();
        getDate();
    }

    public void initRecycle() {
        item = new ArrayList<>();
        adapter = new AttentionGambitAdapter(activity,item);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycle.setLayoutManager(linearLayoutManager);
        recycle.setAdapter(adapter);

        adapter.setCancelAttentionListener(new CancelAttentionGambitListener() {
            @Override
            public void cancelAttention(String url,int parentid,int position) {
                attention(url,parentid,position);
            }
        });
    }

    //1=用户关注，2=话题关注。默认1
    public void getDate() {
        Map<String,String> map = new HashMap<>();
        map.put("type",2+"");
        OkHttp.post(activity, HttpServicePath.URL_USER_FOLLOW, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                if(null != activity) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Gson gson = new Gson();
                            String data = gson.toJson(baseBean.data);
                            List<AttentionGambitBean> attentionGambitList = gson.fromJson(data, new TypeToken<List<AttentionGambitBean>>() {}.getType());
                            if(null != attentionGambitList && 0 != attentionGambitList.size()) {
                                item.addAll(attentionGambitList);
                                adapter.notifyDataSetChanged();
                                emptyLayout.setVisibility(View.GONE);
                                recycle.setVisibility(View.VISIBLE);
                            }else {
                                recycle.setVisibility(View.GONE);
                                emptyLayout.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
            }
        });
    }

    public void attention(String url,int parentid,int position) {
        Map<String, String> map = new HashMap<>();
        map.put("talk_id", parentid + "");
        OkHttp.post(activity,url , map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                if(null != activity) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(null != item && 0!=item.size()) {
                                item.remove(position);
                                adapter.notifyDataSetChanged();
                            }
//                        EventBus.getDefault().post(new EventTags.Attention(userId,0));
//                        getDate();
                        }
                    });
                }
            }
        });
    }
}
