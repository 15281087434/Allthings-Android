package songqiu.allthings.task;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.heartfor.heartvideo.video.HeartVideo;
import com.heartfor.heartvideo.video.HeartVideoInfo;
import com.heartfor.heartvideo.video.VideoControl;
//import com.scwang.smartrefresh.layout.SmartRefreshLayout;
//import com.scwang.smartrefresh.layout.api.RefreshLayout;
//import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import pl.droidsonroids.gif.GifImageView;
import songqiu.allthings.Event.EventTags;
import songqiu.allthings.R;
import songqiu.allthings.activity.CommentWebViewActivity;
import songqiu.allthings.activity.MainActivity;
import songqiu.allthings.adapter.HeaderViewAdapter;
import songqiu.allthings.adapter.TaskPageAdapter;
import songqiu.allthings.adapter.TaskSignAdapter;
import songqiu.allthings.base.BaseFragment;
import songqiu.allthings.bean.AdvertiseBean;
import songqiu.allthings.bean.CustomTaskBean;
import songqiu.allthings.bean.InviteParameterBean;
import songqiu.allthings.bean.TaskListBean;
import songqiu.allthings.bean.TaskSiginListBean;
import songqiu.allthings.constant.SnsConstants;
import songqiu.allthings.http.BaseBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.http.OkHttp;
import songqiu.allthings.http.RequestCallBack;
import songqiu.allthings.iterface.TaskDialogSignListener;
import songqiu.allthings.iterface.TaskItemListenter;
import songqiu.allthings.iterface.TaskSignListener;
import songqiu.allthings.iterface.WindowShareListener;
import songqiu.allthings.login.LoginActivity;
import songqiu.allthings.mine.InviteCodeActivity;
import songqiu.allthings.mine.WithdrawActivity;
import songqiu.allthings.mine.attention.AttentionActivity;
import songqiu.allthings.mine.invite.MyFriendActivity;
import songqiu.allthings.util.ClickUtil;
import songqiu.allthings.util.DateUtil;
import songqiu.allthings.util.GlideCircleTransform;
import songqiu.allthings.util.LogUtil;
import songqiu.allthings.util.NetWorkUtil;
import songqiu.allthings.util.SharedPreferencedUtils;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.util.ToastUtil;
import songqiu.allthings.util.TokenManager;
import songqiu.allthings.util.VibratorUtil;
import songqiu.allthings.util.WindowUtil;
import songqiu.allthings.util.statusbar.StatusBarUtils;
import songqiu.allthings.view.DialogSign;
import songqiu.allthings.view.SharePopupWindows;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/8/6
 *
 *类描述：
 *
 ********/
public class TaskPageFragment extends BaseFragment {


    TextView moneyTv;
    TextView goldTv;
    TextView withdrawImg;
    @BindView(R.id.layout)
    LinearLayout layout;
    @BindView(R.id.iv_icon)
    ImageView ivIcon;
    @BindView(R.id.tv_message)
    TextView tvMessage;
    @BindView(R.id.tryNetworkLayout)
    LinearLayout tryNetworkLayout;
    @BindView(R.id.emptyLayout)
    LinearLayout emptyLayout;

//    @BindView(R.id.smartRefreshLayout)
//    SmartRefreshLayout smartRefreshLayout;
    @BindView(R.id.prestrainImg)
    ImageView prestrainImg;

    @BindView(R.id.reyclerView)
    RecyclerView reyclerView;
    TaskPageAdapter adapter;
    HeaderViewAdapter mHeaderAdapter;
    View mHeadView;
    View mFooterView;

    int signGold;//签到累计金币

    TaskListBean taskListBean;
    List<CustomTaskBean> list;

    //广告
    AdvertiseBean advertiseBean;
    LinearLayout advertisingLayout;
    GifImageView advertisingImg;
    HeartVideo videoView;
    TextView titleTv;
    LinearLayout downloadLayout;
    RelativeLayout jumpLayout;

    //签到
    TextView signGoldTv;
    GridView siginRecycler;
    List<TaskSiginListBean> signList;
    TaskSignAdapter signAdapter;

    InviteParameterBean inviteParameterBean;
    String token;

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
        return R.layout.fragment_task_page;
    }

    @Override
    public void init() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        initRecycle();
        getSiginList();//获取签到列表
        getAdvertise();
    }



    @Override
    public void onResume() {
        super.onResume();
        getTaskList(); //获取任务列表
    }

    public void initRecycle() {
        list = new ArrayList<>();
        adapter = new TaskPageAdapter(activity,list);
        mHeadView = LayoutInflater.from(activity).inflate(R.layout.item_task_page_signin, null, false);
        mFooterView = LayoutInflater.from(activity).inflate(R.layout.advertising_big, null, false);
        moneyTv = mHeadView.findViewById(R.id.moneyTv);
        goldTv = mHeadView.findViewById(R.id.goldTv);
        withdrawImg = mHeadView.findViewById(R.id.withdrawImg);
        siginRecycler = mHeadView.findViewById(R.id.siginRecycler);
        signGoldTv = mHeadView.findViewById(R.id.signGoldTv);

        //footer广告
        advertisingLayout = mFooterView.findViewById(R.id.advertisingLayout);
        advertisingImg = mFooterView.findViewById(R.id.advertisingImg);
        videoView = mFooterView.findViewById(R.id.videoView);
        titleTv = mFooterView.findViewById(R.id.titleTv);
        downloadLayout = mFooterView.findViewById(R.id.downloadLayout);
        jumpLayout = mFooterView.findViewById(R.id.jumpLayout);

        initSiginRecycler();
        mHeaderAdapter = new HeaderViewAdapter(adapter);
        mHeaderAdapter.addHeaderView(mHeadView);
        mHeaderAdapter.addFooterView(mFooterView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        reyclerView.setLayoutManager(linearLayoutManager);
        reyclerView.setAdapter(mHeaderAdapter);
        adapter.settaskItemListenter(new TaskItemListenter() {
            @Override
            public void toTask(String taskname) {
                Intent intent;
                boolean dayModel = SharedPreferencedUtils.getBoolean(activity,SharedPreferencedUtils.dayModel,true);
                switch (taskname) {
                    case "new_invite": //新手 首次邀请好友
                        token = TokenManager.getRequestToken(activity);
                        if(!StringUtil.isEmpty(token)) {
                            getInviteParameter();
                        }else if(StringUtil.isEmpty(token)) {
                            intent = new Intent(activity,LoginActivity.class);
                            startActivity(intent);
                        }
                        break;
                    case "new_firstread": //新手 去阅读文章
                        EventBus.getDefault().post(new EventTags.ToJump(0,1));
                        break;
                    case "task_read": //日常 阅读文章
                        EventBus.getDefault().post(new EventTags.ToJump(0,1));
                        break;
                    case "new_code": //新手任务 输入邀请码
                        token = TokenManager.getRequestToken(activity);
                        if(StringUtil.isEmpty(token)) {
                            intent = new Intent(activity,LoginActivity.class);
                        }else {
                            intent = new Intent(activity,InviteCodeActivity.class);
                        }
                        startActivity(intent);
                        break;
                    case "task_video": //日常 看视频
                        EventBus.getDefault().post(new EventTags.ToJump(1,10));
                        break;
                    case "task_invite": //日常 邀请好友
                        token = TokenManager.getRequestToken(activity);
                        if(!StringUtil.isEmpty(token)) {
                            getInviteParameter();
                        }else if(StringUtil.isEmpty(token)) {
                            intent = new Intent(activity,LoginActivity.class);
                            startActivity(intent);
                        }
                        break;
                    case "task_friend_read": //日常 好友阅读奖励
                        token = TokenManager.getRequestToken(activity);
                        if(StringUtil.isEmpty(token)) {
                            intent = new Intent(activity,LoginActivity.class);
                        }else {
                            intent = new Intent(activity,MyFriendActivity.class);
                        }
                        startActivity(intent);
                        break;
                    case "task_share"://日常 去分享
                        EventBus.getDefault().post(new EventTags.ToJump(0,1));
                        break;
                }
            }
        });
//        smartRefreshLayout.setEnableLoadmore(false);
//        smartRefreshLayout.setEnableRefresh(false);
//        smartRefreshLayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
//            @Override
//            public void onLoadmore(RefreshLayout refreshlayout) {
//
//            }
//
//            @Override
//            public void onRefresh(RefreshLayout refreshlayout) {
//                getTaskList(true); //获取任务列表
//                getSiginList();//获取签到列表
//            }
//        });
        withdrawImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                token = TokenManager.getRequestToken(activity);
                if(StringUtil.isEmpty(token)) {
                    intent = new Intent(activity,LoginActivity.class);
                }else {
                    intent = new Intent(activity,WithdrawActivity.class);
                    intent.putExtra("withdrawType",1);
                }
                startActivity(intent);
            }
        });


        //广告跳转
        jumpLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ClickUtil.onClick()) {
                    if(null == advertiseBean) return;
                    Intent intent = new Intent(activity,CommentWebViewActivity.class);
                    intent.putExtra("url", advertiseBean.jump_url);
                    startActivity(intent);
                }
            }
        });
        advertisingImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ClickUtil.onClick()) {
                    if(null == advertiseBean) return;
                    Intent intent = new Intent(activity,CommentWebViewActivity.class);
                    intent.putExtra("url", advertiseBean.jump_url);
                    startActivity(intent);
                }
            }
        });
    }

    public void initSiginRecycler() {
        signList = new ArrayList<>();
        signAdapter = new TaskSignAdapter(activity,signList);
        siginRecycler.setAdapter(signAdapter);
        signAdapter.setTaskSignListener(new TaskSignListener() {
            @Override
            public void sign(TaskSiginListBean taskSiginListBean, int num) {
                if(StringUtil.isEmpty(TokenManager.getRequestToken(activity))) {
                    startActivity(new Intent(activity,LoginActivity.class));
                }else {
                    toSign(taskSiginListBean,num);
                }
            }
        });
    }

    public void setUi(TaskListBean taskListBean) {
        signGold = taskListBean.total_sigin;
        signGoldTv.setText(String.valueOf(signGold));
        String money = taskListBean.real_money;
        if(!StringUtil.isEmpty(money) && !"0".equals(money) && !"0.0".equals(money) && !"0.00".equals(money)) {
            SpannableString spannableString = new SpannableString(money);
            RelativeSizeSpan relativeSizeSpan = new RelativeSizeSpan(0.75f);
            if(String.valueOf(money).contains(".")) {
                int position = String.valueOf(money).indexOf(".");
                spannableString.setSpan(relativeSizeSpan, position + 1, String.valueOf(money).length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                moneyTv.setText(spannableString);
            }else {
                moneyTv.setText(money);
            }
        }else {
            moneyTv.setText("0");
        }
        goldTv.setText("金币："+taskListBean.real_coin+"个");
    }

    public void getSiginList() {
        Map<String, String> map = new HashMap<>();
        OkHttp.post(activity, HttpServicePath.URL_SIGNIN_LIST, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                if(null != activity) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            signList.clear();
                            int dayOfWeek =  DateUtil.getDayOfWeek();
                            Gson gson = new Gson();
                            String data = gson.toJson(baseBean.data);
                            if (StringUtil.isEmpty(data)) return;
                            List<TaskSiginListBean> taskSiginListBean = gson.fromJson(data, new TypeToken<List<TaskSiginListBean>>() {}.getType());
                            if(null == taskSiginListBean)return;
                            for(int i = 0;i<taskSiginListBean.size();i++) {
                                if(taskSiginListBean.get(i).day <dayOfWeek) {
                                    taskSiginListBean.get(i).signHighlight = false;
                                }else if(taskSiginListBean.get(i).day == dayOfWeek){
                                    taskSiginListBean.get(i).signHighlight = false;
                                    taskSiginListBean.get(i).signAble = true;
                                    if(1 == taskSiginListBean.get(i).is_sign) {
                                        taskSiginListBean.get(i).signed = "已签";
                                    }
                                }else {
                                    taskSiginListBean.get(i).signHighlight = true;
                                }
                            }
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    //execute the task
                                    signList.addAll(taskSiginListBean);
                                    signAdapter.notifyDataSetChanged();
                                }
                            }, 500);

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
                if(null != activity) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Gson gson = new Gson();
                            String data = gson.toJson(baseBean.data);
                            if (StringUtil.isEmpty(data)) return;
                            inviteParameterBean = gson.fromJson(data, InviteParameterBean.class);
                            if(null == inviteParameterBean) return;
                            Intent intent = new Intent(activity,CommentWebViewActivity.class);
                            boolean dayModel = SharedPreferencedUtils.getBoolean(activity,SharedPreferencedUtils.dayModel,true);
                            if(dayModel) {
                                intent.putExtra("url", SnsConstants.getUrlInviteFriend(inviteParameterBean.friend_num,inviteParameterBean.money,inviteParameterBean.total_coin));
                            }else {
                                intent.putExtra("url", SnsConstants.getUrlInviteFriendNight(inviteParameterBean.friend_num,inviteParameterBean.money,inviteParameterBean.total_coin));
                            }
                            startActivity(intent);
                        }
                    });
                }
            }
        });
    }

    public void getTaskList() {
        if (!NetWorkUtil.isNetworkConnected(activity)) {
            ToastUtil.showToast(activity,"网络无连接，请检查网络！");
            prestrainImg.setVisibility(View.GONE);
            return;
        }
        Map<String, String> map = new HashMap<>();
        OkHttp.post(activity,HttpServicePath.URL_TASKLISK, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                if(null != activity) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            list.clear();
                            Gson gson = new Gson();
                            String data = gson.toJson(baseBean.data);
                            if (StringUtil.isEmpty(data)) return;
                            taskListBean = gson.fromJson(data, TaskListBean.class);
                            if(null == taskListBean)return;
                            setUi(taskListBean);
                            //新手任务
                            if(null != taskListBean.task_new && 0 != taskListBean.task_new.size()) {
                                CustomTaskBean customTaskBean = new CustomTaskBean();
                                customTaskBean.headline = "task_new";
                                if(0 != taskListBean.day) {
                                    customTaskBean.headlineTag = "限时"+taskListBean.day+"天";
                                }
                                list.add(customTaskBean);
                                for(int i = 0;i<taskListBean.task_new.size();i++) {
                                    list.add(taskListBean.task_new.get(i));
                                }
                            }
                            //日常任务
                            if(null != taskListBean.day_task && 0 != taskListBean.day_task.size()) {
                                CustomTaskBean customTaskBean = new CustomTaskBean();
                                customTaskBean.headline = "day_task";
                                list.add(customTaskBean);
                                for(int i = 0;i<taskListBean.day_task.size();i++) {
                                    list.add(taskListBean.day_task.get(i));
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }

    public void toSign(TaskSiginListBean taskSiginListBean,int num) {
        Map<String,String> map = new HashMap<>();
        OkHttp.post(activity, HttpServicePath.URL_SIGNIN, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                if(null != activity) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            signGold = signGold + num;
                            signGoldTv.setText(String.valueOf(signGold));
                            goldTv.setText("金币："+(taskListBean.real_coin+num)+"个");
                            taskSiginListBean.is_sign = 1;
                            taskSiginListBean.signed = "已签";
                            signAdapter.notifyDataSetChanged();
                            //弹出已签框
                            initDialog("+"+num+"金币");
                        }
                    });
                }
            }
        });
    }


    public void getAdvertise() {
        Map<String, String> map = new HashMap<>();
        map.put("category",8+"");
        OkHttp.post(activity, HttpServicePath.URL_ADVERTISE, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                if(null != activity) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Gson gson = new Gson();
                            String data = gson.toJson(baseBean.data);
                            if (StringUtil.isEmpty(data)) return;
                            List<AdvertiseBean> advertiseBeanListBean = gson.fromJson(data, new TypeToken<List<AdvertiseBean>>() {}.getType());
                            if(null==advertiseBeanListBean || 0==advertiseBeanListBean.size()) {
                                advertisingLayout.setVisibility(View.GONE);
                                return;
                            }
                            advertiseBean = advertiseBeanListBean.get(0);
                            if(null != advertiseBean) {
                                String url = advertiseBean.url.replaceAll("\"","");;
                                if(!StringUtil.isEmpty(url)) {
                                    if (!url.contains("http")) {
                                        url = HttpServicePath.BasePicUrl + url;
                                    }
                                }
                                advertisingLayout.setVisibility(View.VISIBLE);
                                titleTv.setText(advertiseBean.title);
                                if(1==advertiseBean.type) { //广告图片
                                    advertisingImg.setVisibility(View.VISIBLE);
                                    RequestOptions options = new RequestOptions()
                                            .error(R.mipmap.pic_default)
                                            .placeholder(R.mipmap.pic_default);
                                    Glide.with(activity).load(url).apply(options).into(advertisingImg);
                                    if(2==advertiseBean.change_type) { //大图无下载
                                        downloadLayout.setVisibility(View.GONE);
                                    }
                                }else { //广告视频
                                    videoView.setVisibility(View.VISIBLE);
                                    String path = advertiseBean.video_url;//
                                    HeartVideoInfo info = HeartVideoInfo.Builder().setTitle("").setPath(path).setImagePath(url).setSaveProgress(false).builder();
                                    VideoControl control = new VideoControl(activity);
                                    control.setInfo(info);
                                    videoView.setHeartVideoContent(control);
                                    if(5==advertiseBean.change_type) { //大图无下载
                                        downloadLayout.setVisibility(View.GONE);
                                    }
                                }
                            }
                        }
                    });
                }
            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void noNetwork(EventTags.TaskNoNetwork noNetwork) {
        prestrainImg.setVisibility(View.GONE);
        if (noNetwork.type) {
            layout.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);
        } else {
            layout.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loginSucceed(EventTags.LoginSucceed loginSucceed) {
        getTaskList(); //获取任务列表
        getSiginList();//获取签到列表
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loginSucceed(EventTags.TaskRefresh taskRefresh) {
        getTaskList(); //获取任务列表
//        getSiginList();//获取签到列表
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void initDialog(String content) {
        DialogSign dialogCommon = new DialogSign(activity,content);
        dialogCommon.setCanceledOnTouchOutside(true);
        dialogCommon.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialogCommon.show();
        dialogCommon.setTaskDialogSignListener(new TaskDialogSignListener() {
            @Override
            public void toInvite() {
                getInviteParameter();
                dialogCommon.dismiss();
            }
        });
    }

//    @OnClick({R.id.withdrawImg})
//    public void onViewClick(View view) {
//        switch (view.getId()) {
//            case R.id.withdrawImg:
//                String token = TokenManager.getRequestToken(activity);
//                Intent intent;
//                if(StringUtil.isEmpty(token)) {
//                    intent = new Intent(activity,LoginActivity.class);
//                }else {
//                    intent = new Intent(activity,WithdrawActivity.class);
//                    intent.putExtra("withdrawType",1);
//                }
//                startActivity(intent);
//                break;
//        }
//    }

}
