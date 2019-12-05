package songqiu.allthings.comment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import songqiu.allthings.Event.EventTags;
import songqiu.allthings.R;
import songqiu.allthings.adapter.CommentDetailAdapter;
import songqiu.allthings.adapter.HeaderViewAdapter;
import songqiu.allthings.bean.CommentDetailBean;
import songqiu.allthings.bean.CommentDetailCon1Bean;
import songqiu.allthings.bean.CommentDetailCon2Bean;
import songqiu.allthings.bean.CommentSubitemBean;
import songqiu.allthings.bean.DeleteCommentBean;
import songqiu.allthings.bean.ReportBean;
import songqiu.allthings.http.BaseBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.http.OkHttp;
import songqiu.allthings.http.RequestCallBack;
import songqiu.allthings.iterface.CommentListener;
import songqiu.allthings.mine.userpage.UserPagerActivity;
import songqiu.allthings.util.CopyButtonLibrary;
import songqiu.allthings.util.DateUtil;
import songqiu.allthings.util.GlideCircleTransform;
import songqiu.allthings.util.SharedPreferencedUtils;
import songqiu.allthings.util.ShowNumUtil;
import songqiu.allthings.util.StringUtil;
import songqiu.allthings.util.ToastUtil;
import songqiu.allthings.util.WindowUtil;
import songqiu.allthings.util.statusbar.StatusBarUtils;
import songqiu.allthings.view.CommentWindow;
import songqiu.allthings.view.LongClickDialog;
import songqiu.allthings.view.ReportPopupWindows;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/12/4
 *
 *类描述：
 *
 ********/
public class CommentDetailActivity extends Activity {

    @BindView(R.id.closeImg)
    ImageView closeImg;
    @BindView(R.id.reyclerView)
    RecyclerView reyclerView;
    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout smartRefreshLayout;
    @BindView(R.id.showEdit)
    TextView showEdit;
    @BindView(R.id.bottomLayout)
    LinearLayout bottomLayout;
    @BindView(R.id.shadowLayout)
    LinearLayout shadowLayout;

    TextView commentNumTv;
    TextView likeNumTv;
    ImageView likeImg;
    LinearLayout likeLayout;
    TextView contentTv;
    TextView timeTv;
    TextView userName;
    ImageView userIcon;

    int mid; //传递过来的评论id
    int type; //传递过来的type  1文章  2视频  3话题
    boolean canToReply; //能否评论

    int page = 1;
    View mHeadView;
    HeaderViewAdapter mHeaderAdapter;
    CommentDetailAdapter adapter;
    List<CommentDetailCon2Bean> item;
    CommentDetailBean commentDetailBean;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_detail);
        ButterKnife.bind(this);
        init();
    }


    public void init() {
        boolean dayModel = SharedPreferencedUtils.getBoolean(this,SharedPreferencedUtils.dayModel,true);
        StatusBarUtils.with(CommentDetailActivity.this).init().setStatusTextColorWhite(true, CommentDetailActivity.this);
        modeUi(dayModel);
        mid = getIntent().getIntExtra("mid",0);
        type = getIntent().getIntExtra("type",1);
        canToReply = getIntent().getBooleanExtra("canToReply",false);
        mHeadView = LayoutInflater.from(this).inflate(R.layout.head_commet_detail, null, false);
        initHeadView();
        initSmartRefresh();
        item = new ArrayList<>();
        adapter = new CommentDetailAdapter(this,item);
        mHeaderAdapter = new HeaderViewAdapter(adapter);
        mHeaderAdapter.addHeaderView(mHeadView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        reyclerView.setLayoutManager(linearLayoutManager);
        reyclerView.setAdapter(mHeaderAdapter);


        adapter.setCommentDetailListener(new CommentDetailAdapter.CommentDetailListener() {
            @Override
            public void addSubitemLike(String url, int type, int subMid, CommentDetailCon2Bean commentDetailCon2Bean) {
                like(url,type,subMid,commentDetailCon2Bean);
            }

            @Override
            public void toReply(int type, int grade, int pid, String name) {
                if(canToReply) {
                    showPopupwindow(type,grade,pid,"回复@"+name);
                }else {
                    ToastUtil.showToast(CommentDetailActivity.this,"暂时不能评论!");
                }
            }

            @Override
            public void longClick(int userId, int commentId, int articleid, int type, int position, boolean isSub,String content) {
                int user_Id = SharedPreferencedUtils.getInteger(CommentDetailActivity.this,"SYSUSERID",0);
                if(userId == user_Id) {
                    //长按删除
                    longClickDialog(CommentDetailActivity.this,true,commentId,userId,type,position,isSub,content);
                }else {
                    //长按举报
                    longClickDialog(CommentDetailActivity.this,false,commentId,userId,type,position,isSub,content);
                }
            }
        });

        getCommentDetails(page);
    }

    public void modeUi(boolean isDay) {
        if(isDay) {
            shadowLayout.setVisibility(View.GONE);
        }else {
            shadowLayout.setVisibility(View.VISIBLE);
        }
    }

    public void initHeadView() {
        commentNumTv = mHeadView.findViewById(R.id.commentNumTv);
        likeNumTv = mHeadView.findViewById(R.id.likeNumTv);
        likeImg = mHeadView.findViewById(R.id.likeImg);
        likeLayout = mHeadView.findViewById(R.id.likeLayout);
        contentTv = mHeadView.findViewById(R.id.contentTv);
        timeTv = mHeadView.findViewById(R.id.timeTv);
        userName = mHeadView.findViewById(R.id.userName);
        userIcon = mHeadView.findViewById(R.id.userIcon);
    }

    public void initSmartRefresh() {
        smartRefreshLayout.setEnableRefresh(false);
        smartRefreshLayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                page = page + 1;
                getCommentDetails(page);
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {

            }
        });
    }

    public void setHeadData(CommentDetailBean commentDetailBean) {
        if(null == commentDetailBean.con1) return;
        RequestOptions options = new RequestOptions()
                .circleCrop().transforms(new GlideCircleTransform(this))
                .error(R.mipmap.head_default)
                .placeholder(R.mipmap.head_default);
        if(!StringUtil.isEmpty(commentDetailBean.con1.avatar)) {
            if(!commentDetailBean.con1.avatar.contains("http")) {
                commentDetailBean.con1.avatar = HttpServicePath.BasePicUrl+commentDetailBean.con1.avatar;
            }
        }
        Glide.with(this).load(commentDetailBean.con1.avatar).apply(options).into(userIcon);
        userName.setText(commentDetailBean.con1.user_nickname);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(commentDetailBean.con1.created*1000);
        String  time = simpleDateFormat.format(date);
        timeTv.setText(DateUtil.getTimeFormatText(time, "yyyy-MM-dd HH:mm:ss"));
        contentTv.setText(commentDetailBean.con1.content);
        likeNumTv.setText(ShowNumUtil.showUnm(commentDetailBean.con1.up_num));
        if(0 == commentDetailBean.con1.is_up) {
            likeImg.setImageResource(R.mipmap.item_like);
            likeNumTv.setTextColor(getResources().getColor(R.color.FF666666));
        }else {
            likeImg.setImageResource(R.mipmap.item_like_pre);
            likeNumTv.setTextColor(getResources().getColor(R.color.FFDE5C51));
        }
        commentNumTv.setText(ShowNumUtil.showUnm(commentDetailBean.con1.comment_num));
        likeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(commentDetailBean.con1.is_up==0) {
                    like(HttpServicePath.URL_LIKE,4,commentDetailBean.con1.commentid,commentDetailBean.con1);
                }else {
                    like(HttpServicePath.URL_NO_LIKE,4,commentDetailBean.con1.commentid,commentDetailBean.con1);
                }
            }
        });
    }

    public void getCommentDetails(int page) {
        Map<String, String> map = new HashMap<>();
        map.put("mid",mid+"");
        map.put("type",type+"");
        map.put("page",page+"");
        map.put("num",10+"");
        OkHttp.post(this, smartRefreshLayout, HttpServicePath.URL_COMENT_DETAIL, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        String data = gson.toJson(baseBean.data);
                        if (StringUtil.isEmpty(data)) return;
                        commentDetailBean = gson.fromJson(data, CommentDetailBean.class);
                        if(null == commentDetailBean) return;
                        if(page == 1) {
                            item.clear();
                            setHeadData(commentDetailBean);
                        }
                        if(null != commentDetailBean.con2 && 0!=commentDetailBean.con2.size()) {
                            item.addAll(commentDetailBean.con2);
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        });
    }

    //一级评论点赞/取消点赞
    public void like(String url, int type, int mid,CommentDetailCon1Bean commentDetailCon1Bean) {
        Map<String, String> map = new HashMap<>();
        map.put("type", type + "");
        map.put("mid", mid + "");
        OkHttp.post(this, url, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (url.equals(HttpServicePath.URL_LIKE)) {
                            commentDetailCon1Bean.up_num = commentDetailCon1Bean.up_num + 1;
                            commentDetailCon1Bean.is_up = 1;
                            likeNumTv.setText(ShowNumUtil.showUnm(commentDetailCon1Bean.up_num));
                            likeImg.setImageResource(R.mipmap.item_like_pre);
                            likeNumTv.setTextColor(getResources().getColor(R.color.FFDE5C51));
                            EventBus.getDefault().post(new EventTags.LikeComment(mid,true));
                        } else {
                            commentDetailCon1Bean.up_num = commentDetailCon1Bean.up_num - 1;
                            commentDetailCon1Bean.is_up = 0;
                            likeNumTv.setText(ShowNumUtil.showUnm(commentDetailCon1Bean.up_num));
                            likeImg.setImageResource(R.mipmap.item_like);
                            likeNumTv.setTextColor(getResources().getColor(R.color.FF666666));
                            EventBus.getDefault().post(new EventTags.LikeComment(mid,false));
                        }
                    }
                });
            }
        });
    }

    //二级评论点赞/取消点赞
    public void like(String url, int type, int subMid,CommentDetailCon2Bean commentDetailCon2Bean) {
        Map<String, String> map = new HashMap<>();
        map.put("type", type + "");
        map.put("mid", subMid + "");
        OkHttp.post(this, url, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (url.equals(HttpServicePath.URL_LIKE)) {
                            commentDetailCon2Bean.up_num = commentDetailCon2Bean.up_num + 1;
                            commentDetailCon2Bean.is_up = 1;
                            EventBus.getDefault().post(new EventTags.LikeSubComment(mid,subMid,true));
                        } else {
                            commentDetailCon2Bean.up_num = commentDetailCon2Bean.up_num - 1;
                            commentDetailCon2Bean.is_up = 0;
                            EventBus.getDefault().post(new EventTags.LikeSubComment(mid,subMid,false));
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    public void showPopupwindow(int type,int grade,int pid,String hint) {
        CommentWindow fw = new CommentWindow(this,hint);
        fw.showAtLocation(commentNumTv, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        fw.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1.0f;
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                getWindow().setAttributes(lp);
            }
        });
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.6f;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setAttributes(lp);
        fw.setCommentListener(new CommentListener() {
            @Override
            public void publishComment(String comment) {
                if (null != commentDetailBean) {
                    addComment(type, commentDetailBean.con1.article_id, comment,grade,pid);
                }
                fw.dismiss();
            }
        });
    }

    public void addComment(int type, int articleid, String content,int grade,int pid) {
        Map<String, String> map = new HashMap<>();
        map.put("type", type + "");
        map.put("articleid", articleid + "");
        map.put("content", content);
        map.put("grade", grade + "");
        map.put("pid", pid + "");
        OkHttp.post(this, HttpServicePath.URL_ADD_COMMENT, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        page = 1;
                        getCommentDetails(page);
                        Gson gson = new Gson();
                        String data = gson.toJson(baseBean.data);
                        if (StringUtil.isEmpty(data)) return;
                        CommentSubitemBean commentSubitemBean = gson.fromJson(data, CommentSubitemBean.class);
                        if(null == commentSubitemBean) return;
                        EventBus.getDefault().post(new EventTags.AddComment(mid,commentSubitemBean));
                    }
                });
            }
        });
    }

    //删除评论
    public void delComment(int commentId, int article_id, int type, int position,boolean isSub) {
        Map<String, String> map = new HashMap<>();
        map.put("commentid", commentId + "");
        map.put("articleid", article_id + "");
        map.put("type", type + "");
        OkHttp.post(this, HttpServicePath.URL_DEL_COMMENT, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //
                        Gson gson = new Gson();
                        String data = gson.toJson(baseBean.data);
                        if (StringUtil.isEmpty(data)) return;
                        DeleteCommentBean deleteCommentBean = gson.fromJson(data, DeleteCommentBean.class);
                        if(null != deleteCommentBean) {
                            commentNumTv.setText(ShowNumUtil.showUnm(Integer.valueOf(commentNumTv.getText().toString())-deleteCommentBean.num));
                        }

                        if(null == item) return;
                        if(isSub) { //三级评论则只删掉当前一行评论
                            item.remove(position);
                            EventBus.getDefault().post(new EventTags.DeleteComment(mid,commentId,deleteCommentBean.num));
                        }else { //二级评论则删掉当前一行评论及回复的评论
                            for(int i = 0;i<item.size();i++) {
                                if(item.get(i).commentid == commentId || item.get(i).pid == commentId) {
                                    item.remove(i);
                                    i--;
                                }
                            }
                            EventBus.getDefault().post(new EventTags.DeleteComment(mid,commentId,deleteCommentBean.num));
                        }
                        adapter.notifyDataSetChanged();

                    }
                });
            }
        });
    }


    public void longClickDialog(Context context,boolean isMyself,int commentId,int article_id,int type,int position,boolean isSub,String content) {
        LongClickDialog dialog = new LongClickDialog(context,isMyself);
        dialog.showDialog();
        dialog.setOnItemClickListener(new LongClickDialog.OnItemClick() {
            @Override
            public void onWhichItemClick(int pos) {
                switch (pos) {
                    //复制
                    case 0:
                        CopyButtonLibrary copyButtonLibrary = new CopyButtonLibrary(CommentDetailActivity.this,content);
                        copyButtonLibrary.init(content);
                        ToastUtil.showToast(CommentDetailActivity.this,"复制成功");
                        break;
                    case 1:
                        if(isMyself) { //删除自己的评论
                            delComment(commentId, article_id, type, position,isSub);
                        }else { //举报他人的评论
                            showReportWindow(commentId,4);
                        }
                        break;
                }

            }
        });
    }


    private List<String> getReportTitle() {
        return Arrays.asList("内容低劣", "广告软文", "政治敏感", "色情低俗", "违法信息", "错误观念引导", "人身攻击", "涉嫌侵犯");
    }

    public List<ReportBean> reportList() {
        List<ReportBean> list = new ArrayList<>();
        List<String> titleList = getReportTitle();
        for (String title : titleList) {
            ReportBean reportBean = new ReportBean();
            reportBean.title = title;
            list.add(reportBean);
        }
        return list;
    }

    //举报弹窗
    public void showReportWindow(int mid,int type) {
        ReportPopupWindows rw = new ReportPopupWindows(this, reportList(),mid,type);
        WindowUtil.windowDeploy(this, rw, closeImg);
    }

    @OnClick({R.id.closeImg,R.id.showEdit})
    public void viewClick(View view) {
        switch (view.getId()){
            case R.id.closeImg:
                finish();
                break;
            case R.id.showEdit:
                if (canToReply) {
                    showPopupwindow(type,1,mid,"优质评论会被优先展示哦!");
                } else {
                    ToastUtil.showToast(this, "暂时不能评论!");
                }
                break;
        }
    }

}
