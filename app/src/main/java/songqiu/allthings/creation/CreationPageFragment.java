package songqiu.allthings.creation;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import songqiu.allthings.R;
import songqiu.allthings.activity.CommentWebViewActivity;
import songqiu.allthings.activity.MainActivity;
import songqiu.allthings.adapter.BannerMineAdapter;
import songqiu.allthings.articledetail.ArticleDetailActivity;
import songqiu.allthings.auth.activity.AuthActivity;
import songqiu.allthings.base.BaseFragment;
import songqiu.allthings.bean.BannerBean;
import songqiu.allthings.bean.CreationBroadcastBean;
import songqiu.allthings.bean.InviteParameterBean;
import songqiu.allthings.classification.AllClassificationActivity;
import songqiu.allthings.constant.SnsConstants;
import songqiu.allthings.creation.article.income.CreationIncomeActivity;
import songqiu.allthings.creation.article.manage.ArticleManageActivity;
import songqiu.allthings.creation.article.publish.PublicArticleActivity;
import songqiu.allthings.http.BaseBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.http.OkHttp;
import songqiu.allthings.http.RequestCallBack;
import songqiu.allthings.login.LoginActivity;
import songqiu.allthings.mine.income.IncomeRecordActivity;
import songqiu.allthings.mine.userpage.UserPagerActivity;
import songqiu.allthings.search.SearchActivity;
import songqiu.allthings.util.ClickUtil;
import songqiu.allthings.util.CopyButtonLibrary;
import songqiu.allthings.util.LogUtil;
import songqiu.allthings.util.SharedPreferencedUtils;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.util.ToastUtil;
import songqiu.allthings.util.TokenManager;
import songqiu.allthings.videodetail.VideoDetailActivity;
import songqiu.allthings.view.DialogArticleCommon;
import songqiu.allthings.view.banner.ColorPointHintView;
import songqiu.allthings.view.banner.RollPagerView;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/12/19
 *
 *类描述：创作
 *
 ********/
public class CreationPageFragment extends BaseFragment{

    @BindView(R.id.notificationTv)
    TextView notificationTv;
    @BindView(R.id.hintTv)
    TextView hintTv;
    @BindView(R.id.roll_page_mine)
    RollPagerView roll_page_mine;
    List<BannerBean> mBannerList;
    BannerMineAdapter mBannerAdapter;

    MainActivity activity;
    String token;

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
        return R.layout.fragment_creation_page;
    }

    @Override
    public void init() {
        initBanner();
        initBannerEvent();
        getBroadcast();
    }

    @Override
    public void onResume() {
        super.onResume();
        token = TokenManager.getRequestToken(activity);
    }

    public void initBanner() {
        mBannerList = new ArrayList<>();
        mBannerAdapter = new BannerMineAdapter(roll_page_mine, (ArrayList<BannerBean>) mBannerList);
        roll_page_mine.setAdapter(mBannerAdapter);
        roll_page_mine.setHintView(new ColorPointHintView(activity, Color.WHITE, Color.GRAY));
        roll_page_mine.setHintPadding(0, 0, 0, 10);
        roll_page_mine.resume();
        getBanner();
    }

    public void initBannerEvent() {
        roll_page_mine.setOnItemClickListener(position -> {
            if (mBannerList == null || position >= mBannerList.size()) {
                return;
            }
            //0 不跳转 1 跳转文章 2视频 3 h5url 4 收入记录页面 5 邀请好友页面
            Intent intent;
            if (1 == mBannerList.get(position).jump_type) { //文章
                intent = new Intent(activity, ArticleDetailActivity.class);
                intent.putExtra("articleid", mBannerList.get(position).url_id);
                startActivity(intent);
            } else if (2 == mBannerList.get(position).jump_type) { //视频
                intent = new Intent(activity, VideoDetailActivity.class);
                intent.putExtra("articleid", mBannerList.get(position).url_id);
                startActivity(intent);
            } else if (3 == mBannerList.get(position).jump_type) { //h5
                intent = new Intent(activity, CommentWebViewActivity.class);
                intent.putExtra("url", mBannerList.get(position).url);
                startActivity(intent);
            } else if (4 == mBannerList.get(position).jump_type) { //
                if (StringUtil.isEmpty(token)) {
                    intent = new Intent(activity, LoginActivity.class);
                } else {
                    intent = new Intent(activity, IncomeRecordActivity.class);
                }
                startActivity(intent);
            } else if (5 == mBannerList.get(position).jump_type) { //邀请好友
                if (StringUtil.isEmpty(token)) {
                    intent = new Intent(activity, LoginActivity.class);
                    startActivity(intent);
                } else {
                    getInviteParameter();
                }
            }
        });
    }

    public void getBanner() {
        Map<String, String> map = new HashMap<>();
        map.put("type", 6 + ""); //6:创作中心
        OkHttp.post(activity, HttpServicePath.URL_BANNER, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                if (null != activity) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Gson gson = new Gson();
                            String data = gson.toJson(baseBean.data);
                            if (StringUtil.isEmpty(data)) return;
                            List<BannerBean> bannerBeanList = gson.fromJson(data, new TypeToken<List<BannerBean>>() {
                            }.getType());
                            if (null != bannerBeanList && 0 != bannerBeanList.size()) {
                                mBannerList.addAll(bannerBeanList);
                                mBannerAdapter.notifyDataSetChanged();
                                if (1 == bannerBeanList.size()) {
                                    roll_page_mine.pause();
                                    roll_page_mine.setHintViewVisibility(false);
                                }
                            } else {
                                roll_page_mine.setVisibility(View.GONE);
                            }
                        }
                    });
                }
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


    public void getBroadcast() {
        Map<String, String> map = new HashMap<>();
        OkHttp.post(activity, HttpServicePath.URL_BROADCAST, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        String data = gson.toJson(baseBean.data);
                        if (StringUtil.isEmpty(data)) return;
                        CreationBroadcastBean creationBroadcastBean = gson.fromJson(data, CreationBroadcastBean.class);
                        if(null == creationBroadcastBean) return;
                        notificationTv.setText(creationBroadcastBean.name);
                    }
                });
            }
        });
    }

    @OnClick({R.id.hintTv,R.id.searchImg,R.id.classification,R.id.authenticationLayout,R.id.publishTv,R.id.manageTv,R.id.incomeTv,R.id.planLayout,R.id.strategyLayout,R.id.explainLayout,
                })
    public void onViewClick(View view) {
        Intent intent;
        boolean dayModel = SharedPreferencedUtils.getBoolean(activity, SharedPreferencedUtils.dayModel, true);
        switch (view.getId()) {
            case R.id.hintTv:
                CopyButtonLibrary copyButtonLibrary = new CopyButtonLibrary(activity, hintTv);
                copyButtonLibrary.init(hintTv);
                ToastUtil.showToast(activity, "复制成功:" + hintTv.getText().toString());
                break;
            case R.id.searchImg:
                if (ClickUtil.onClick()) {
                    intent = new Intent(activity, SearchActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.classification:
                if (ClickUtil.onClick()) {
                    intent = new Intent(activity, AllClassificationActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.authenticationLayout: //验证身份证
                if(ClickUtil.onClick()) {
                    if(StringUtil.isEmpty(token)) {
                        intent = new Intent(activity,LoginActivity.class);
                        startActivity(intent);
                    }else {
                        intent = new Intent(activity, AuthActivity.class);
                        startActivity(intent);
                    }
                }
                break;
            case R.id.publishTv: //发布内容
                if(ClickUtil.onClick()) {
                    if(StringUtil.isEmpty(token)) {
                        intent = new Intent(activity,LoginActivity.class);
                        startActivity(intent);
                    }else {
                        intent = new Intent(activity,PublicArticleActivity.class);
                        startActivity(intent);
                    }
                }
                break;
            case R.id.manageTv: //作品管理
                if (ClickUtil.onClick()) {
                    if(StringUtil.isEmpty(token)) {
                        intent = new Intent(activity,LoginActivity.class);
                        startActivity(intent);
                    }else {
                        intent = new Intent(activity,ArticleManageActivity.class);
                        startActivity(intent);
                    }
                }
                break;
            case R.id.incomeTv: //创作收入
                if (ClickUtil.onClick()) {
                    if(StringUtil.isEmpty(token)) {
                        intent = new Intent(activity,LoginActivity.class);
                        startActivity(intent);
                    }else {
                        intent = new Intent(activity,CreationIncomeActivity.class);
                        startActivity(intent);
                    }
                }
                break;
            case R.id.planLayout: //招募计划
                if (ClickUtil.onClick()) {
                    intent = new Intent(activity, CommentWebViewActivity.class);
                    if(dayModel) {
                        intent.putExtra("url", SnsConstants.URL_RECRUITMENT);
                    }else {
                        intent.putExtra("url", SnsConstants.URL_RECRUITMENT_NIGHT);
                    }
                    startActivity(intent);
                }
                break;
            case R.id.strategyLayout: //投稿协议
                if (ClickUtil.onClick()) {
                    intent = new Intent(activity, CommentWebViewActivity.class);
                    if(dayModel) {
                        intent.putExtra("url", SnsConstants.URL_CONTRIBUTE);
                    }else {
                        intent.putExtra("url", SnsConstants.URL_CONTRIBUTE_NIGHT);
                    }
                    startActivity(intent);
                }
                break;
            case R.id.explainLayout: //作者福利说明
                if (ClickUtil.onClick()) {
                    intent = new Intent(activity, CommentWebViewActivity.class);
                    if(dayModel) {
                        intent.putExtra("url", SnsConstants.URL_WELFARE);
                    }else {
                        intent.putExtra("url", SnsConstants.URL_WELFARE_NIGHT);
                    }
                    startActivity(intent);
                }
                break;

        }
    }
}
