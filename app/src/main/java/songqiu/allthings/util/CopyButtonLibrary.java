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
public class CopyButtonLibrary {

    private ClipboardManager myClipboard;
    private ClipData myClip;
    private Context context;
    private TextView textView;
    private String text;

    public CopyButtonLibrary(Context context, TextView textView) {
        this.context = context;
        this.textView = textView;
    }

    public CopyButtonLibrary(Context context, String text) {
        this.context = context;
        this.text = text;
    }

    public void init(TextView textView) {
        myClipboard = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
        String text;
        text = textView.getText().toString();
        myClip = ClipData.newPlainText("text", text);
        myClipboard.setPrimaryClip(myClip);
    }

    public void init(String text) {
        myClipboard = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
        myClip = ClipData.newPlainText("text", text);
        myClipboard.setPrimaryClip(myClip);
    }

}
