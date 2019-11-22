package songqiu.allthings.mine.userpage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextPaint;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.heartfor.heartvideo.video.HeartVideo;
import com.heartfor.heartvideo.video.HeartVideoManager;
import com.heartfor.heartvideo.video.PlayerStatus;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import songqiu.allthings.Event.EventTags;
import songqiu.allthings.R;
import songqiu.allthings.activity.MainActivity;
import songqiu.allthings.adapter.TablayoutViewAdapter;
import songqiu.allthings.base.BaseActivity;
import songqiu.allthings.bean.UserMemberDetailBean;
import songqiu.allthings.http.BaseBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.http.OkHttp;
import songqiu.allthings.http.RequestCallBack;
import songqiu.allthings.util.ClickUtil;
import songqiu.allthings.util.GlideCircleTransform;
import songqiu.allthings.util.GlideLoadUtils;
import songqiu.allthings.util.LogUtil;
import songqiu.allthings.util.SharedPreferencedUtils;
import songqiu.allthings.util.ShowNumUtil;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.util.statusbar.StatusBarUtils;
import songqiu.allthings.util.theme.ThemeManager;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/8/20
 *
 *类描述：用户页面
 *
 ********/
public class UserPagerActivity extends BaseActivity {

    @BindView(R.id.backImg)
    ImageView backImg;
    @BindView(R.id.userIcon)
    ImageView userIcon;
    @BindView(R.id.compileTv)
    TextView compileTv;
    @BindView(R.id.userName)
    TextView userName;
    @BindView(R.id.userIntroduce)
    TextView userIntroduce;
    @BindView(R.id.upTv)
    TextView upTv;
    @BindView(R.id.fansTv)
    TextView fansTv;
    @BindView(R.id.attentionTv)
    TextView attentionTv;

    @BindView(R.id.magicIndicator)
    MagicIndicator magicIndicator;
    @BindView(R.id.viewpager)
    ViewPager viewPager;

    @BindView(R.id.shadowLayout)
    LinearLayout shadowLayout;

    UserMemberDetailBean userMemberDetailBean;

    int userId;
    int myUserId;
    List<Fragment> mFragments = new ArrayList<>();
    List<String> list;


    @Override
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main_pager);
    }

    @Override
    public void init() {
        StatusBarUtils.with(UserPagerActivity.this).init().setStatusTextColorWhite(true, UserPagerActivity.this);
        boolean dayModel = SharedPreferencedUtils.getBoolean(this,SharedPreferencedUtils.dayModel,true);
        modeUi(dayModel);
        myUserId = SharedPreferencedUtils.getInteger(this, "SYSUSERID", 0);
        userId = getIntent().getIntExtra("userId", 0);
        if (userId == myUserId) {
            compileTv.setText("编辑资料");
        } else {
            compileTv.setText("关注");
        }
        list = getTableTitle();
        initMagicindicator();
    }
    public void modeUi(boolean isDay) {
        if(isDay) {
            shadowLayout.setVisibility(View.GONE);
        }else {
            shadowLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    public void initUi(UserMemberDetailBean userMemberDetailBean) {
        if (null == userMemberDetailBean) return;
        if(!StringUtil.isEmpty(userMemberDetailBean.avatar)) {
            if(!userMemberDetailBean.avatar.contains("http")) {
                userMemberDetailBean.avatar = HttpServicePath.BasePicUrl+userMemberDetailBean.avatar;
            }
        }
        GlideLoadUtils.getInstance().glideLoadHead(this,userMemberDetailBean.avatar,userIcon);
        userName.setText(userMemberDetailBean.user_nickname);
        userIntroduce.setText(userMemberDetailBean.signature);
        upTv.setText(ShowNumUtil.showUnm(userMemberDetailBean.total_up));
        fansTv.setText(ShowNumUtil.showUnm(userMemberDetailBean.fs_num));
        attentionTv.setText(ShowNumUtil.showUnm(userMemberDetailBean.gz_num));
        if (userId == myUserId) {
            compileTv.setText("编辑资料");
        }else {
            if(0 == userMemberDetailBean.is_follow) { //未关注
                compileTv.setText("关注");
                compileTv.setTextColor(getResources().getColor(R.color.normal_color));
            }else { //已关注
                compileTv.setText("已关注");
                compileTv.setTextColor(getResources().getColor(R.color.FF999999));
            }
        }
    }

    private List<String> getTableTitle() {
        return Arrays.asList("全部", "话题", "图文", "视频");
    }


    private void initMagicindicator() {
        mFragments.clear();
        if (null == list || 0 == list.size()) return;
        for (int i = 0; i < list.size(); i++) {
            switch (i) {
                case 0:
                    UserAllFragment userAllFragment = new UserAllFragment();
                    userAllFragment.userId = userId;
                    mFragments.add(userAllFragment);
                    break;
                case 1:
                    UserTopicFragment userTopicFragment = new UserTopicFragment();
                    userTopicFragment.userId = userId;
                    mFragments.add(userTopicFragment);
                    break;
                case 2:
                    UserImageTextFragment userImageTextFragment = new UserImageTextFragment();
                    userImageTextFragment.userId = userId;
                    mFragments.add(userImageTextFragment);
                    break;
                case 3:
                    UserVideoFragment userVideoFragment = new UserVideoFragment();
                    userVideoFragment.userId = userId;
                    mFragments.add(userVideoFragment);
                    break;
            }
        }

        TablayoutViewAdapter viewAdapter = new TablayoutViewAdapter(getSupportFragmentManager(), mFragments, list);
        viewPager.setAdapter(viewAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                magicIndicator.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {

                magicIndicator.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                magicIndicator.onPageScrollStateChanged(state);
            }
        });
        viewPager.setCurrentItem(0);

        magicIndicator.setBackgroundColor(getResources().getColor(R.color.white));
        //新建导航栏
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setEnablePivotScroll(true);
        commonNavigator.setAdjustMode(true);

        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return list == null ? 0 : list.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                //设置Magicindicator的一种标题模式， 标题模式有很多种，这是最基本的一种
                SimplePagerTitleView simplePagerTitleView = new SimplePagerTitleView(context);
                simplePagerTitleView.setText(list.get(index));
                //设置被选中的item颜色
                simplePagerTitleView.setSelectedColor(getResources().getColor(R.color.normal_color));
                //设置为被选中item颜色
                simplePagerTitleView.setNormalColor(getResources().getColor(R.color.FFA2A2A2));
                simplePagerTitleView.setSelectedSize(19);
                simplePagerTitleView.setDeselectedSize(17);
                TextPaint tp = simplePagerTitleView.getPaint();
                tp.setFakeBoldText(true);
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewPager.setCurrentItem(index);
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                //设置标题指示器，也有多种,可不选，即没有标题指示器。
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setColors(getResources().getColor(R.color.normal_color));
                indicator.setYOffset(13);
                indicator.setXOffset(45);
                return indicator;
            }

        });

        //绑定导航栏
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, viewPager);
    }

    public void getData() {
        Map<String, String> map = new HashMap<>();
        map.put("userid", userId + "");
        OkHttp.post(this, HttpServicePath.URL_MEMBER_DETAIL, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        String data = gson.toJson(baseBean.data);
                        if (StringUtil.isEmpty(data)) return;
                        userMemberDetailBean = gson.fromJson(data, UserMemberDetailBean.class);
                        initUi(userMemberDetailBean);
                    }
                });
            }
        });
    }

    public void addFollow(int parentid, int type) { //用户id 	type、1=添加关注，2=取消关注
        Map<String, String> map = new HashMap<>();
        map.put("parentid", parentid + "");
        map.put("type", type + "");
        OkHttp.post(this, HttpServicePath.URL_ADD_FOLLOW, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (1 == type) {
                            userMemberDetailBean.is_follow = 1;
                            compileTv.setText("已关注");
                            compileTv.setTextColor(getResources().getColor(R.color.FF999999));
                            EventBus.getDefault().post(new EventTags.Attention(parentid, 1));
                        } else {
                            userMemberDetailBean.is_follow = 0;
                            compileTv.setText("关注");
                            compileTv.setTextColor(getResources().getColor(R.color.normal_color));
                            EventBus.getDefault().post(new EventTags.Attention(parentid, 0));
                        }
                    }
                });
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        HeartVideoManager.getInstance().pause();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            HeartVideo heartVideo = HeartVideoManager.getInstance().getCurrPlayVideo();
            if(null != heartVideo && heartVideo.getCurrModeStatus()== PlayerStatus.MODE_FULL_SCREEN) {
                HeartVideoManager.getInstance().getCurrPlayVideo().exitFullScreen();
            }else {
                finish();
            }
        }
        return false;
    }

    @OnClick({R.id.backImg, R.id.compileTv})
    public void onViewClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.backImg:
                finish();
                break;
            case R.id.compileTv:
                if (ClickUtil.onClick()) {
                    if (userId == myUserId) { //去编辑资料
                        intent = new Intent(UserPagerActivity.this, ModificationInfoActivity.class);
                        startActivity(intent);
                    }else { //关注、取消关注
                        if(null == userMemberDetailBean) return;
                        if (0 == userMemberDetailBean.is_follow) {//去关注
                            addFollow(userId, 1);
                        } else { //取消关注
                            addFollow(userId, 2);
                        }

                    }
                }
                break;
        }
    }


}
