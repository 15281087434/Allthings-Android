package songqiu.allthings.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.widget.TextView;

/*******
 *
 *Created by ???
 *
 *???? 2019/9/29
 *
 *????
 *
 ********/
public class CopyUtil {
    ClipboardManager clipboardManager;
    ClipData clipData;
    Context context;
    TextView textView;
    String text;

    public CopyUtil(Context context,TextView textView) {
        this.context = context;
        this.textView = textView;
    }

    public void init(TextView textView) {
        clipboardManager = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
        String text;
        String[] str;
        text = textView.getText().toString();
        if(text.contains(":")) {
            str = text.split(":");
            if(null != str && 0!=str.length) {
                text = str[1];
            }
        }
        clipData = ClipData.newPlainText("text",text);
        clipboardManager.setPrimaryClip(clipData);
        ToastUtil.showToast(context,"?????"+text);
    }
}
