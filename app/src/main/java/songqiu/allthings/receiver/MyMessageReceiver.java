package songqiu.allthings.receiver;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.alibaba.sdk.android.push.MessageReceiver;
import com.alibaba.sdk.android.push.notification.CPushMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import songqiu.allthings.activity.MainActivity;
import songqiu.allthings.http.BaseBean;
import songqiu.allthings.http.HttpServicePath;
import songqiu.allthings.http.OkHttp;
import songqiu.allthings.http.RequestCallBack;
import songqiu.allthings.util.LogUtil;
import songqiu.allthings.util.StringUtil;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/10/11
 *
 *类描述：
 *
 ********/
public class MyMessageReceiver extends MessageReceiver {
    // 消息接收部分的LOG_TAG
    public static final String REC_TAG = "receiver";
    @Override
    public void onNotification(Context context, String title, String summary, Map<String, String> extraMap) {
        // TODO 处理推送通知
        LogUtil.i("=====>title: " + title + ", summary: " + summary + ", extraMap: " + extraMap);
        //{type=1, _ALIYUN_NOTIFICATION_ID_=287894, target=0}
//        Log.e("MyMessageReceiver", "Receive notification, title: " + title + ", summary: " + summary + ", extraMap: " + extraMap);
    }
    @Override
    public void onMessage(Context context, CPushMessage cPushMessage) {
        LogUtil.i("=========================onMessage");
        Log.e("MyMessageReceiver", "onMessage, messageId: " + cPushMessage.getMessageId() + ", title: " + cPushMessage.getTitle() + ", content:" + cPushMessage.getContent());
        getRecord(context);
    }
    @Override
    public void onNotificationOpened(Context context, String title, String summary, String extraMap) {
        //type 0：打开APP、 1：文章详情  target：id、 2：视频详情 target：id
        LogUtil.i("=====>extraMap: " + extraMap);
        //{type=1, _ALIYUN_NOTIFICATION_ID_=287894, target=0}
        if(!StringUtil.isEmpty(extraMap)) {
            try {
                JSONObject jsonObject = new JSONObject(extraMap);
                String type = jsonObject.optString("type");
//                LogUtil.i("type:"+type);
                 int target = jsonObject.optInt("id");
                 int isMoment = jsonObject.getInt("isMoment");
//                LogUtil.i("target:"+target);
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("type",type);
                intent.putExtra("id",target);
                intent.putExtra("isMoment",isMoment);
                context.startActivity(intent);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

//        LogUtil.i("=========================onNotificationOpened:"+"  title:"+title+"  summary:"+summary+"  extraMap:"+extraMap);
//        Log.e("MyMessageReceiver", "onNotificationOpened, title: " + title + ", summary: " + summary + ", extraMap:" + extraMap);
    }
    @Override
    protected void onNotificationClickedWithNoAction(Context context, String title, String summary, String extraMap) {
        LogUtil.i("=========================onNotificationClickedWithNoAction:"+"  title:"+title+"  summary:"+summary+"  extraMap:"+extraMap);
        Log.e("MyMessageReceiver", "onNotificationClickedWithNoAction, title: " + title + ", summary: " + summary + ", extraMap:" + extraMap);
    }
    @Override
    protected void onNotificationReceivedInApp(Context context, String title, String summary, Map<String, String> extraMap, int openType, String openActivity, String openUrl) {
        LogUtil.i("=========================onNotificationReceivedInApp:"+"  title:"+title+"  summary:"+summary+"  extraMap:"+extraMap);
        Log.e("MyMessageReceiver", "onNotificationReceivedInApp, title: " + title + ", summary: " + summary + ", extraMap:" + extraMap + ", openType:" + openType + ", openActivity:" + openActivity + ", openUrl:" + openUrl);
    }
    @Override
    protected void onNotificationRemoved(Context context, String messageId) {
        LogUtil.i("=========================onNotificationRemoved");
        Log.e("MyMessageReceiver", "onNotificationRemoved");
    }

    public void getRecord(Context context) {
        Map<String, String> map = new HashMap<>();
        map.put("type",1+"");
        OkHttp.post(context, HttpServicePath.URL_RECORD, map, new RequestCallBack() {
            @Override
            public void httpResult(BaseBean baseBean) {
            }
        });
    }
}
