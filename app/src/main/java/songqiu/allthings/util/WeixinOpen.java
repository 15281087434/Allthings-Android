package songqiu.allthings.util;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import songqiu.allthings.R;
import songqiu.allthings.application.MyApplication;
import songqiu.allthings.constant.SnsConstants;


public class WeixinOpen {
	private IWXAPI mApi;
	private static WeixinOpen mWeixinOpen = null;
	
	private WeixinOpen() {
		registApp();
	}
	public static WeixinOpen getInstance() {
		if (null == mWeixinOpen) {
			mWeixinOpen = new WeixinOpen();
		}
		return mWeixinOpen;
	}
	
	private void registApp() {
		mApi = WXAPIFactory.createWXAPI(MyApplication.getInstance(),
				SnsConstants.APP_ID, true);
		mApi.registerApp(SnsConstants.APP_ID);
	}
	
	private String buildTransaction(String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis())
				: type + System.currentTimeMillis();
	}
	
	/**
	 * 实现IWXAPIEventHandler接口，微信发送的请求将回调到onReq方法，发送到微信请求的响应结果将回调到onResp方法
	 * 
	 * @param intent
	 * @param handler
	 */
	public void handleWeixinIntent(Intent intent, IWXAPIEventHandler handler) {
		mApi.handleIntent(intent, handler);
	}
	
	public void wechatShare(int flag){  
	    WXWebpageObject webpage = new WXWebpageObject();
	    webpage.webpageUrl = "https://www.baidu.com";
	    WXMediaMessage msg = new WXMediaMessage(webpage);
	    msg.title = "测试标题";
	    msg.description = "测试描述";
	    //这里替换一张自己工程里的图片资源  
	    Bitmap thumb = BitmapFactory.decodeResource(MyApplication.getInstance().getResources(), R.mipmap.app_icon);
	    msg.setThumbImage(thumb);  
	      
	    SendMessageToWX.Req req = new SendMessageToWX.Req();
	    req.transaction = String.valueOf(System.currentTimeMillis());  
	    req.message = msg;  
	    req.scene = flag==0?SendMessageToWX.Req.WXSceneSession:SendMessageToWX.Req.WXSceneTimeline;  
	    LogUtil.i("sendReq:"+ mApi.sendReq(req)); //true
	    mApi.sendReq(req);  
	} 
	
//	public void wechatShare(int flag,String title,String content,String url){
//	    WXWebpageObject webpage = new WXWebpageObject();
//	    webpage.webpageUrl = url;
//	    WXMediaMessage msg = new WXMediaMessage(webpage);
//	    msg.title = title;
//	    msg.description = content;
//	    //这里替换一张自己工程里的图片资源
//	    Bitmap thumb = BitmapFactory.decodeResource(MyApplication.getInstance().getResources(), R.drawable.icon);
//	    msg.setThumbImage(thumb);
//
//	    SendMessageToWX.Req req = new SendMessageToWX.Req();
//	    req.transaction = String.valueOf(System.currentTimeMillis());
//	    req.message = msg;
//	    req.scene = flag==0?SendMessageToWX.Req.WXSceneSession:SendMessageToWX.Req.WXSceneTimeline;
//	    Logger.i("sendReq:"+ mApi.sendReq(req)); //true
//	    mApi.sendReq(req);
//	}
	
	/**
	 * 分享到微信
	 * 
	 * @param text
	 * @param flag
	 */
//	public void shareFriend(String text, boolean flag) {
//		if (text == null || text.length() == 0) {
//			return;
//		}
//		// 初始化一个WXTextObject对象
//		WXTextObject textObj = new WXTextObject();
//		textObj.text = text;
//
//		// 用WXTextObject对象初始化一个WXMediaMessage对象
//		WXMediaMessage msg = new WXMediaMessage();
//		msg.mediaObject = textObj;
//		// 发送文本类型的消息时，title字段不起作用
//		// msg.title = "Will be ignored";
//		msg.description = text;
//
//		// 构造一个Req
//		SendMessageToWX.Req req = new SendMessageToWX.Req();
//		req.transaction = buildTransaction("text"); // transaction字段用于唯一标识一个请求
//		req.message = msg;
//		req.scene = flag ? SendMessageToWX.Req.WXSceneTimeline
//				: SendMessageToWX.Req.WXSceneSession;
//		// 调用api接口发送数据到微信
//		mApi.sendReq(req);
//	}
}
