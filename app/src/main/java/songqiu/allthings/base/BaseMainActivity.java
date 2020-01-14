package songqiu.allthings.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import songqiu.allthings.http.BaseBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.http.OkHttp;
import songqiu.allthings.http.RequestCallBack;
import songqiu.allthings.util.StringUtil;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/8/6
 *
 *类描述：
 *
 ********/
public abstract class BaseMainActivity extends AppCompatActivity{

    //对应上下文
    protected Context mContext;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        initView(savedInstanceState);
        ButterKnife.bind(this);

        init();
    }

    /**
     * 设置界面布局
     *
     */
    public abstract void initView(Bundle savedInstanceState);

    public abstract void init();

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        MyApplication.getInstance().okHttpClient.dispatcher().cancelAll();
    }

    /**
     * 弹出消息
     *
     * @param msg
     */
    protected void showToast(String msg) {
        if (StringUtil.isEmpty(msg))
            return;
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    public void totalShare(int type,int mid) {
        Map<String, String> map = new HashMap<>();
        map.put("type",type+""); //1=文章，2=视频，3=话题
        map.put("mid",mid+"");
        OkHttp.post(this, HttpServicePath.URL_SHARE_TOTAL, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
            }
        });
    }
}
