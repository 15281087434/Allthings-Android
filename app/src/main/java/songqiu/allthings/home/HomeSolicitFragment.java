package songqiu.allthings.home;

import android.content.Context;
import android.content.Intent;
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
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import songqiu.allthings.R;
import songqiu.allthings.activity.CommentWebViewActivity;
import songqiu.allthings.activity.MainActivity;
import songqiu.allthings.adapter.HomeSolictAdapter;
import songqiu.allthings.articledetail.ArticleDetailActivity;
import songqiu.allthings.auth.bean.CashOutRecordBean;
import songqiu.allthings.bean.BannerBean;
import songqiu.allthings.bean.HomeSolictBean;
import songqiu.allthings.bean.HomeSubitemBean;
import songqiu.allthings.bean.InviteParameterBean;
import songqiu.allthings.constant.SnsConstants;
import songqiu.allthings.http.BaseBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.http.OkHttp;
import songqiu.allthings.http.RequestCallBack;
import songqiu.allthings.login.LoginActivity;
import songqiu.allthings.mine.income.IncomeRecordActivity;
import songqiu.allthings.util.ScreenUtils;
import songqiu.allthings.util.SharedPreferencedUtils;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.util.TokenManager;
import songqiu.allthings.util.VibratorUtil;
import songqiu.allthings.videodetail.VideoDetailActivity;

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

    MainActivity activity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_page_solicit, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

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

    private void initView() {
        initRecycle();
        getBanner();
        getSolict();
    }


    public void initRecycle() {
        adapter = new HomeSolictAdapter(activity, item);
        adapter.setmCallBack(new HomeSolictAdapter.TpCallBack() {
            @Override
            public void onTp(int position) {
                //TODO 投票
                HashMap<String,String>map =new HashMap<>();
                map.put("mid",item.get(position).getArticleid());
                map.put("activityid",item.get(position).getActivityid());
                OkHttp.post(activity, HttpServicePath.URL_TP, map, new RequestCallBack() {
                    @Override
                    public void httpResult(BaseBean baseBean) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                    item.get(position).setSupport_num(  item.get(position).getSupport_num()+1);
                                    RecyclerView.LayoutManager manager=recycle.getLayoutManager();
                                    View chatAt=manager.findViewByPosition(position+1);
                                    if(chatAt!=null){
                                        ((TextView) chatAt.findViewById(R.id.tv_tickets)).setText(item.get(position).getSupport_num()+"");
                                        ((TextView) chatAt.findViewById(R.id.tv_tp)).setEnabled(false);
                                        item.get(position).setIs_support(2);
                                    }


                            }
                        });
                    }
                });
            }
        });

        //广告banner点击事件
        adapter.setSolictListener(new HomeSolictAdapter.SolictListener() {
            @Override
            public void onSolictListener(List<BannerBean> bannerBeans, int mPosition) {
                String token = TokenManager.getRequestToken(activity);
            if (bannerBeans == null || mPosition >= bannerBeans.size()) {
                return;
            }
            //0 不跳转 1 跳转文章 2视频 3 h5url 4 收入记录页面 5 邀请好友页面
            Intent intent;
            if (1 == bannerBeans.get(mPosition).jump_type) { //文章
                intent = new Intent(activity, ArticleDetailActivity.class);
                intent.putExtra("articleid", bannerBeans.get(mPosition).url_id);
                activity.startActivity(intent);
            } else if (2 == bannerBeans.get(mPosition).jump_type) { //视频
                intent = new Intent(activity, VideoDetailActivity.class);
                intent.putExtra("articleid", bannerBeans.get(mPosition).url_id);
                activity.startActivity(intent);
            } else if (3 == bannerBeans.get(mPosition).jump_type) { //h5
                intent = new Intent(activity, CommentWebViewActivity.class);
                intent.putExtra("url", bannerBeans.get(mPosition).url);
                activity.startActivity(intent);
            } else if (4 == bannerBeans.get(mPosition).jump_type) { //
                if (StringUtil.isEmpty(token)) {
                    intent = new Intent(activity, LoginActivity.class);
                } else {
                    intent = new Intent(activity, IncomeRecordActivity.class);
                }
                activity.startActivity(intent);
            } else if (5 == bannerBeans.get(mPosition).jump_type) { //邀请好友
                if (StringUtil.isEmpty(token)) {
                    intent = new Intent(activity, LoginActivity.class);
                    activity.startActivity(intent);
                } else {
                    getInviteParameter();
                }
            }
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycle.setLayoutManager(linearLayoutManager);
        recycle.setAdapter(adapter);


        smartRefreshLayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                page++;
                getSolict();
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                page = 1;
                getBanner();
                getSolict();
            }
        });
    }

    public void getInviteParameter() {
        Map<String, String> map = new HashMap<>();
        OkHttp.post(activity, HttpServicePath.URL_INVITE_PARAMETER, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                if (null != activity) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Gson gson = new Gson();
                            String data = gson.toJson(baseBean.data);
                            if (StringUtil.isEmpty(data)) return;
                            InviteParameterBean inviteParameterBean = gson.fromJson(data, InviteParameterBean.class);
                            if (null == inviteParameterBean) return;
                            Intent intent = new Intent(activity, CommentWebViewActivity.class);
                            boolean dayModel = SharedPreferencedUtils.getBoolean(activity, SharedPreferencedUtils.dayModel, true);
                            if (dayModel) {
                                intent.putExtra("url", SnsConstants.getUrlInviteFriend(inviteParameterBean.friend_num, inviteParameterBean.money, inviteParameterBean.total_coin));
                            } else {
                                intent.putExtra("url", SnsConstants.getUrlInviteFriendNight(inviteParameterBean.friend_num, inviteParameterBean.money, inviteParameterBean.total_coin));
                            }
                            startActivity(intent);
                        }
                    });
                }
            }
        });
    }

    public void getSolict() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("page", page);
        OkHttp.post(activity, smartRefreshLayout, HttpServicePath.URL_SOLICIT, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                Gson gson = new Gson();
                String data = gson.toJson(baseBean.data);
                if(TextUtils.isEmpty(data)){
                    return;
                }

                List<HomeSolictBean> beans = gson.fromJson(data, new TypeToken<List<HomeSolictBean>>() {
                }.getType());

                activity.runOnUiThread(new Runnable() {
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
                            adapter.setBannerBeans(bannerBeanList);
                        }
                    }
                });

            }
        });
    }
}
