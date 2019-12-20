package songqiu.allthings.creation;

import android.content.Context;
import android.content.Intent;

import butterknife.OnClick;
import songqiu.allthings.R;
import songqiu.allthings.activity.MainActivity;
import songqiu.allthings.base.BaseFragment;
import songqiu.allthings.creation.article.CreationArticleActivity;
import songqiu.allthings.iterface.DialogPrivacyListener;
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

    @OnClick(R.id.tv)
    public void onViewClick() {
//        startActivity(new Intent(activity, CreationArticleActivity.class));

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
    }
}
