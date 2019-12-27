package songqiu.allthings.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import songqiu.allthings.R;
import songqiu.allthings.activity.CommentWebViewActivity;
import songqiu.allthings.constant.SnsConstants;
import songqiu.allthings.iterface.DialogDeleteListener;
import songqiu.allthings.iterface.DialogPrivacyListener;
import songqiu.allthings.util.ClickUtil;
import songqiu.allthings.util.LogUtil;
import songqiu.allthings.util.SharedPreferencedUtils;
import songqiu.allthings.util.ToastUtil;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/8/16
 *
 *类描述：
 *
 ********/
public class DialogPrivacyExplain extends Dialog{

    private Context context;
    DialogPrivacyListener dialogPrivacyListener;

    public DialogPrivacyExplain(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    public void init() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_privacy_explain, null);
        setContentView(view);

        TextView knowTv = view.findViewById(R.id.knowTv);
        knowTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogPrivacyListener.sure();
                dismiss();
            }
        });

        TextView cancelTv = view.findViewById(R.id.cancelTv);
        cancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogPrivacyListener.cancel();
                dismiss();
            }
        });



        TextView tv = view.findViewById(R.id.tv);
        tv.setHighlightColor(context.getResources().getColor(android.R.color.transparent));
        String strString = "请查看《用户协议》及《隐私政策》了解详细信息。如果你同意，请点击“同意”开始使用我们的服务。";
        SpannableString ss = new SpannableString(strString);
        ss.setSpan(protocolClickSpan,3,9,Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        ss.setSpan(privacyClickSpan,10,16,Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        tv.setText(ss);
        tv.setMovementMethod(LinkMovementMethod.getInstance());

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        lp.width = (int) (d.widthPixels * 0.8); // 宽度设置为屏幕的0.7
        dialogWindow.setAttributes(lp);
    }

    public void setDialogPrivacyListener(DialogPrivacyListener dialogPrivacyListener) {
        this.dialogPrivacyListener = dialogPrivacyListener;
    }

    ClickableSpan protocolClickSpan = new ClickableSpan() {
        @Override
        public void onClick(View widget) {
            if(ClickUtil.onClick()) {
                Intent intent = new Intent(context,CommentWebViewActivity.class);
                intent.putExtra("url", SnsConstants.URL_USER_PROTOCOL);
                context.startActivity(intent);
            }
        }
        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(ContextCompat.getColor(context,R.color.FF6FA4EB));
            ds.setUnderlineText(false);
            ds.clearShadowLayer();   //阴影
        }
    };
    ClickableSpan privacyClickSpan = new ClickableSpan() {
        @Override
        public void onClick(View widget) {
            if(ClickUtil.onClick()) {
                Intent intent = new Intent(context,CommentWebViewActivity.class);
                intent.putExtra("url", SnsConstants.URL_PRIVACY);
                context.startActivity(intent);
            }
        }
        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(ContextCompat.getColor(context,R.color.FF6FA4EB));
            ds.setUnderlineText(false);
            ds.clearShadowLayer();   //阴影
        }
    };


}
