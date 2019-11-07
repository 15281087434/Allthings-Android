package songqiu.allthings.mine.help;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import songqiu.allthings.R;
import songqiu.allthings.base.BaseFragment;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/8/29
 *
 *类描述：全部
 *
 ********/
public class AllFeedbackFragment extends BaseFragment {
    @BindView(R.id.questionImg)
    ImageView questionImg;
    @BindView(R.id.questionLayout)
    LinearLayout questionLayout;
    @BindView(R.id.questionSub)
    LinearLayout questionSub;
    @BindView(R.id.inviteImg)
    ImageView inviteImg;
    @BindView(R.id.inviteLayout)
    LinearLayout inviteLayout;
    @BindView(R.id.inviteSub)
    LinearLayout inviteSub;
    @BindView(R.id.goldImg)
    ImageView goldImg;
    @BindView(R.id.goldLayout)
    LinearLayout goldLayout;
    @BindView(R.id.goldSub)
    LinearLayout goldSub;
    @BindView(R.id.withdrawImg)
    ImageView withdrawImg;
    @BindView(R.id.withdrawLayout)
    LinearLayout withdrawLayout;
    @BindView(R.id.withdrawSub)
    LinearLayout withdrawSub;
    @BindView(R.id.accountImg)
    ImageView accountImg;
    @BindView(R.id.accountLayout)
    LinearLayout accountLayout;
    @BindView(R.id.accountSub)
    LinearLayout accountSub;
    @BindView(R.id.submitTv)
    TextView submitTv;

    FeedbackAndHelpActivity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (FeedbackAndHelpActivity) context;//保存Context引用
    }
    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }
    
    
    @Override
    public int initView() {
        return R.layout.fragment_all_feedback;
    }

    @Override
    public void init() {

    }

    @OnClick({R.id.questionLayout,R.id.inviteLayout,R.id.goldLayout,R.id.withdrawLayout,R.id.accountLayout,R.id.submitTv})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.questionLayout:
                if(questionSub.getVisibility() == View.GONE) {
                    questionSub.setVisibility(View.VISIBLE);
                    questionImg.setImageResource(R.mipmap.arrow_top);
                }else {
                    questionSub.setVisibility(View.GONE);
                    questionImg.setImageResource(R.mipmap.arrow_below);
                }
                break;
            case R.id.inviteLayout:
                if(inviteSub.getVisibility() == View.GONE) {
                    inviteSub.setVisibility(View.VISIBLE);
                    inviteImg.setImageResource(R.mipmap.arrow_top);
                }else {
                    inviteSub.setVisibility(View.GONE);
                    inviteImg.setImageResource(R.mipmap.arrow_below);
                }
                break;
            case R.id.goldLayout:
                if(goldSub.getVisibility() == View.GONE) {
                    goldSub.setVisibility(View.VISIBLE);
                    goldImg.setImageResource(R.mipmap.arrow_top);
                }else {
                    goldSub.setVisibility(View.GONE);
                    goldImg.setImageResource(R.mipmap.arrow_below);
                }
                break;
            case R.id.withdrawLayout:
                if(withdrawSub.getVisibility() == View.GONE) {
                    withdrawSub.setVisibility(View.VISIBLE);
                    withdrawImg.setImageResource(R.mipmap.arrow_top);
                }else {
                    withdrawSub.setVisibility(View.GONE);
                    withdrawImg.setImageResource(R.mipmap.arrow_below);
                }
                break;
            case R.id.accountLayout:
                if(accountSub.getVisibility() == View.GONE) {
                    accountSub.setVisibility(View.VISIBLE);
                    accountImg.setImageResource(R.mipmap.arrow_top);
                }else {
                    accountSub.setVisibility(View.GONE);
                    accountImg.setImageResource(R.mipmap.arrow_below);
                }
                break;
            case R.id.submitTv:
                    Intent intent = new Intent(activity,MyQuestionActivity.class);
                    startActivity(intent);
                break;
        }
    }

}
