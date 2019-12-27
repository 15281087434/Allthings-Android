package songqiu.allthings.creation;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import butterknife.OnClick;
import songqiu.allthings.R;
import songqiu.allthings.activity.MainActivity;
import songqiu.allthings.auth.activity.AuthActivity;
import songqiu.allthings.base.BaseFragment;
import songqiu.allthings.creation.article.income.CreationIncomeActivity;
import songqiu.allthings.creation.article.manage.ArticleManageActivity;
import songqiu.allthings.creation.article.publish.PublicArticleActivity;
import songqiu.allthings.iterface.DialogPrivacyListener;
import songqiu.allthings.login.LoginActivity;
import songqiu.allthings.mine.userpage.UserPagerActivity;
import songqiu.allthings.util.ClickUtil;
import songqiu.allthings.util.SharedPreferencedUtils;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.util.TokenManager;
import songqiu.allthings.view.DialogArticleCommon;

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

    }

    @Override
    public void onResume() {
        super.onResume();
        token = TokenManager.getRequestToken(activity);
    }


    @OnClick({R.id.authenticationTv,R.id.publishTv,R.id.manageTv,R.id.incomeTv,R.id.planLayout,R.id.strategyLayout,R.id.explainLayout,
                R.id.activityLayout,R.id.guideLayout})
    public void onViewClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.authenticationTv: //验证身份证
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
            case R.id.planLayout: //计划招募
                if (ClickUtil.onClick()) {

                }
                break;
            case R.id.strategyLayout: //加V认证攻略
                if (ClickUtil.onClick()) {

                }
                break;
            case R.id.explainLayout: //作者福利说明
                if (ClickUtil.onClick()) {

                }
                break;
            case R.id.activityLayout: //创作活动
                if (ClickUtil.onClick()) {

                }
                break;
            case R.id.guideLayout: //内容指南
                if (ClickUtil.onClick()) {

                }
                break;
        }
    }
}
