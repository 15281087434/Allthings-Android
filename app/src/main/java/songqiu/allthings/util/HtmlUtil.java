package songqiu.allthings.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.webkit.WebView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import songqiu.allthings.R;

/*******
 *
 *Created by ???
 *
 *???? 2019/8/29
 *
 *????
 *
 ********/
public class HtmlUtil {
    private static final String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>"; // ??script??????
    private static final String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>"; // ??style??????
    private static final String regEx_html = "<[^>]+>"; // ??HTML????????
    private static final String regEx_space = "\\s*|\t";//?????????


    /**
     * @param htmlStr
     * @return ??Html??
     */
    public static String delHTMLTag(String htmlStr) {
        Pattern p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
        Matcher m_script = p_script.matcher(htmlStr);
        htmlStr = m_script.replaceAll(""); // ??script??

        Pattern p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
        Matcher m_style = p_style.matcher(htmlStr);
        htmlStr = m_style.replaceAll(""); // ??style??

        Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
        Matcher m_html = p_html.matcher(htmlStr);
        htmlStr = m_html.replaceAll(""); // ??html??

        Pattern p_space = Pattern.compile(regEx_space, Pattern.CASE_INSENSITIVE);
        Matcher m_space = p_space.matcher(htmlStr);
        htmlStr = m_space.replaceAll(""); // ????????
        return htmlStr.trim(); // ???????
    }

    public static String getTextFromHtml(String htmlStr) {
        htmlStr = delHTMLTag(htmlStr);
//        htmlStr = htmlStr.replaceAll(" ", "");
        return htmlStr;
    }



    /**
     * html ??
     * @param source
     * @return
     */
    public static String htmlEncode(String source) {
        if (source == null) {
            return "";
        }
        String html = "";
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < source.length(); i++) {
            char c = source.charAt(i);
            switch (c) {
                case '<':
                    buffer.append("&lt;");
                    break;
                case '>':
                    buffer.append("&gt;");
                    break;
                case '&':
                    buffer.append("&amp;");
                    break;
                case '"':
                    buffer.append("&quot;");
                    break;
                case ' ':
                    buffer.append("&nbsp;");
                    break;

                default:
                    buffer.append(c);
            }
        }
        html = buffer.toString();
        return html;
    }


    /**
     * html ??
     * @param
     * @return
     */
//    public static String htmlDecode(String source) {
//        if (TextUtils.isEmpty(source)) {
//            return "";
//        }
//        source = source.replace("&lt;", "<");
//        source = source.replace("&gt;", ">");
//        source = source.replace("&amp;", "&");
//        source = source.replace("&quot;", "\"");
//        source = source.replace("&nbsp;", " ");
//        source = source.replace("&ldquo;", "\"");
//        source = source.replace("&rdquo;", "\"");
//        source = source.replace("&hellip;", "");
//        source = source.replace("&lsquo;", "");
//        source = source.replace("&rsquo;", "");
//        source = source.replace("&mdash;", "");
//        return getTextFromHtml(source);
//    }

    public static String htmlDecode(String source) {
        if (TextUtils.isEmpty(source)) {
            return "";
        }
        source = source.replace("&lt;", "");
        source = source.replace("p&gt;", "");
        source = source.replace("&amp;", "");
        source = source.replace("/", "");
        source = source.replace("hellip;hellip;", "?");
        source = source.replace("ldquo;", "");
        source = source.replace("rdquo;", "");
        source = source.replace("div&gt;", "");
        source = source.replace("mdash;", "");
        source = source.replace("u&gt;", "");
        source = source.replace("br&gt;", "");
        source = source.replace("br &gt;", "");
        source = source.replace("strong&gt;", "");
        source = source.replace("span&gt;", "");
        return source;
    }


    public static void deleString() {

    }
    public static void setParagraphSpacing(Context context, TextView tv, String content, int paragraphSpacing, int lineSpacingExtra) {
        if (!content.contains("\n")) {
            tv.setText(content);
            return;
        }
        String str = HtmlUtil.htmlDecode(content);
        content = str.replace("\n", "\n\r");

        int previousIndex = content.indexOf("\n\r");
        //?????????index?????????????
        List<Integer> nextParagraphBeginIndexes = new ArrayList<>();
        nextParagraphBeginIndexes.add(previousIndex);
        while (previousIndex != -1) {
            int nextIndex = content.indexOf("\n\r", previousIndex + 2);
            previousIndex = nextIndex;
            if (previousIndex != -1) {
                nextParagraphBeginIndexes.add(previousIndex);
            }
        }
        //???????????????
        float lineHeight = tv.getLineHeight();

        //?\r??????????:1px?????+???
        SpannableString spanString = new SpannableString(content);
        Drawable d = ContextCompat.getDrawable(context, R.drawable.paragraph_space);
        float density = context.getResources().getDisplayMetrics().density;
        //int???????? - ?? + ??
        d.setBounds(0, 0, 1, (int) ((lineHeight - lineSpacingExtra * density) / 1.2 + (paragraphSpacing - lineSpacingExtra) * density));

        for (int index : nextParagraphBeginIndexes) {
            // \r?String????index
            spanString.setSpan(new ImageSpan(d), index + 1, index + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
//        String str = HtmlUtil.htmlDecode(spanString.toString());
//        tv.setText(str);
        tv.setText(spanString);
    }


}

