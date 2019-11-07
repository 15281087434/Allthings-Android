package songqiu.allthings.util;

import android.text.TextUtils;
import android.util.Log;

import songqiu.allthings.BuildConfig;

/**
 * log工具类
 * Created by lyl on 2016/11/29.
 */

public class LogUtil {
    private static final String TAG = "way";
    // 是否需要打印bug，可以在application的onCreate函数里面初始化
    public static boolean isDebug = BuildConfig.DEBUG;
    static int showLengthMax = 3000;
    private LogUtil() {
        throw new UnsupportedOperationException("cannt be init");
    }

    // 下面四个是默认tag的函数
    public static void i(String msg) {
        if (isDebug)
            Log.i(TAG, msg);
    }

    public static void d(String msg) {
        if (isDebug)
            Log.d(TAG, msg);
    }

    public static void e(String msg) {
        if (isDebug)
            Log.e(TAG, msg);
    }

    public static void v(String msg) {
        if (isDebug)
            Log.v(TAG, msg);
    }

    // 下面是传入自定义tag的函数
    public static void i(String tag, String msg) {
        if (isDebug)
            Log.i(tag, msg);
    }

    public static void d(String tag, String msg) {
        if (isDebug)
            Log.d(tag, msg);
    }

    public static void e(String tag, String msg) {
        if (isDebug)
            Log.e(tag, msg);
    }

    public static void v(String tag, String msg) {
        if (isDebug)
            Log.v(tag, msg);
    }

    /**
     * 分段打印出较长log文本
     *
     * @param logContent 打印文本
     * @param showLength 规定每段显示的长度（AndroidStudio控制台打印log的最大信息量大小为4k）
     * @param tag        打印log的标记
     */
    public static void showLargeLog(String logContent, int showLength, String tag) {

        if (logContent != null && logContent.length() > showLength) {
            String show = logContent.substring(0, showLength);
//            e(tag, show);
            iJsonFormat(tag, show, false);
            /*剩余的字符串如果大于规定显示的长度，截取剩余字符串进行递归，否则打印结果*/
            if ((logContent.length() - showLength) > showLength) {
                String partLog = logContent.substring(showLength, logContent.length());
                showLargeLog(partLog, showLength, tag);
            } else {
                String printLog = logContent.substring(showLength, logContent.length());
//                e(tag, printLog);
                iJsonFormat(tag, printLog, false);
            }

        } else {
            e(tag, logContent);
        }
    }

    /**
     * 分段打印出较长log文本
     *
     * @param logContent 打印文本
     * @param tag        打印log的标记
     */
    public static void showLargeLog(String logContent, String tag) {

        if (logContent != null && logContent.length() > showLengthMax) {
            String show = logContent.substring(0, showLengthMax);
            e(tag, show);
//            iJsonFormat(tag,show,false);
            /*剩余的字符串如果大于规定显示的长度，截取剩余字符串进行递归，否则打印结果*/
            if ((logContent.length() - showLengthMax) > showLengthMax) {
                String partLog = logContent.substring(showLengthMax, logContent.length());
                showLargeLog(partLog, tag);
            } else {
                String printLog = logContent.substring(showLengthMax, logContent.length());
                e(tag, printLog);
//                iJsonFormat(tag,printLog,false);
            }

        } else {
            e(tag, logContent);
        }
    }


    /**
     * Json格式化输出
     *
     * @param tag
     * @param message                 内容
     * @param isOutputOriginalContent 是否输入原内容
     */
    public static void iJsonFormat(String tag, String message, boolean isOutputOriginalContent) {
        if (isDebug && !TextUtils.isEmpty(message)) {
            if (isOutputOriginalContent)
                Log.i(tag, "-->" + message);
//            android.util.Log.i(tag, "-->" +"\n" + format(convertUnicode(message)));
            Log.i(tag, "-->" + "\n" + format(message));
        }
    }

    public static String format(String jsonStr) {
        int level = 0;
        StringBuffer jsonForMatStr = new StringBuffer();
        for (int i = 0; i < jsonStr.length(); i++) {
            char c = jsonStr.charAt(i);
            if (level > 0 && '\n' == jsonForMatStr.charAt(jsonForMatStr.length() - 1)) {
                jsonForMatStr.append(getLevelStr(level));
            }
            switch (c) {
                case '{':
                case '[':
                    jsonForMatStr.append(c + "\n");
                    level++;
                    break;
                case ',':
                    jsonForMatStr.append(c + "\n");
                    break;
                case '}':
                case ']':
                    jsonForMatStr.append("\n");
                    level--;
                    jsonForMatStr.append(getLevelStr(level));
                    jsonForMatStr.append(c);
                    break;
                default:
                    jsonForMatStr.append(c);
                    break;
            }
        }

        return jsonForMatStr.toString();

    }

    private static String getLevelStr(int level) {
        StringBuffer levelStr = new StringBuffer();
        for (int levelI = 0; levelI < level; levelI++) {
            levelStr.append("\t");
        }
        return levelStr.toString();
    }

    public static String convertUnicode(String ori) {
        char aChar;
        int len = ori.length();
        StringBuffer outBuffer = new StringBuffer(len);
        for (int x = 0; x < len - 1; ) {
            aChar = ori.charAt(x++);
            if (aChar == '\\') {
                aChar = ori.charAt(x++);
                if (aChar == 'u') {
                    // Read the xxxx
                    int value = 0;
                    for (int i = 0; i < 4; i++) {
                        aChar = ori.charAt(x++);
                        switch (aChar) {
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                throw new IllegalArgumentException(
                                        "Malformed   \\uxxxx   encoding.");
                        }
                    }
                    outBuffer.append((char) value);
                } else {
                    if (aChar == 't')
                        aChar = '\t';
                    else if (aChar == 'r')
                        aChar = '\r';
                    else if (aChar == 'n')
                        aChar = '\n';
                    else if (aChar == 'f')
                        aChar = '\f';
                    outBuffer.append(aChar);
                }
            } else
                outBuffer.append(aChar);

        }
        return outBuffer.toString();
    }
}
