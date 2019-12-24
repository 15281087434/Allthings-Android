package songqiu.allthings.creation;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import butterknife.OnClick;
import songqiu.allthings.R;
import songqiu.allthings.activity.MainActivity;
import songqiu.allthings.base.BaseFragment;
import songqiu.allthings.creation.article.CreationArticleActivity;
import songqiu.allthings.creation.article.CreationExplainActivity;
import songqiu.allthings.creation.article.manage.ArticleManageActivity;
import songqiu.allthings.iterface.DialogPrivacyListener;
import songqiu.allthings.util.ClickUtil;
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

//    @OnClick(R.id.tv)
//    public void onViewClick() {
//        DialogArticleCommon dialogArticleCommon = new DialogArticleCommon(activity,"保存","是否保存已输入内容？");
//        dialogArticleCommon.setCanceledOnTouchOutside(true);
//        dialogArticleCommon.setCancelable(true);
//        dialogArticleCommon.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//        dialogArticleCommon.show();
//        dialogArticleCommon.setDialogPrivacyListener(new DialogPrivacyListener() {
//            @Override
//            public void cancel() {
//
//            }
//
//            @Override
//            public void sure() {
//
//            }
//        });
//    }

    @OnClick({R.id.authenticationTv,R.id.publishTv,R.id.manageTv,R.id.incomeTv,R.id.planLayout,R.id.strategyLayout,R.id.explainLayout,
                R.id.activityLayout,R.id.guideLayout})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.authenticationTv: //验证身份证
                if(ClickUtil.onClick()) {

                }
                break;
            case R.id.publishTv: //发布内容
                if(ClickUtil.onClick()) {
                    startActivity(new Intent(activity, CreationArticleActivity.class));
                }
                break;
            case R.id.manageTv: //作品管理
                if (ClickUtil.onClick()) {
//                    startActivity(new Intent(activity, CreationExplainActivity.class));
                    startActivity(new Intent(activity, ArticleManageActivity.class));
                }
                break;
            case R.id.incomeTv: //创作收入
                if (ClickUtil.onClick()) {

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
