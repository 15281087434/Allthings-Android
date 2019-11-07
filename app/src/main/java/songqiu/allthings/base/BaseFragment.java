package songqiu.allthings.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import songqiu.allthings.http.BaseBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.http.OkHttp;
import songqiu.allthings.http.RequestCallBack;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/8/6
 *
 *类描述：
 *
 ********/
public abstract class BaseFragment extends Fragment{

    /**
     * 贴附的activity
     */
    protected Activity mActivity;
    protected Context mContext;
    /**
     * 根view
     */
    protected View mRootView;
    protected Unbinder unbinder;


    /**
     * 当Fragment与Activity发生关联时调用
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getActivity();
        mRootView = inflater.inflate(initView(), container, false);
        unbinder = ButterKnife.bind(this, mRootView);
        init();
        return mRootView;
    }


    /**
     * 设置根布局资源id
     */
    public abstract int initView();

    public abstract void init();

    public void totalShare(int type,int mid) {
        Map<String, String> map = new HashMap<>();
        map.put("type",type+""); //1=文章，2=视频，3=话题
        map.put("mid",mid+"");
        OkHttp.post(getActivity(), HttpServicePath.URL_SHARE_TOTAL, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
            }
        });
    }

}
