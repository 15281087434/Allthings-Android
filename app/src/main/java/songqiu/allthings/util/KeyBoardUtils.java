package songqiu.allthings.util;

import android.app.Activity;
import android.content.Context;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.lang.reflect.Method;

/**
 *
 */

public class KeyBoardUtils {
    /**
     * 显示软键盘
     *
     * @param context 上下文
     * @param view    view必须是VISIBLE的EditText，如果不是VISIBLE的，需要先将其设置为VISIBLE。
     *                当前界面必须已经加载完成，不能直接在Activity的onCreate()，onResume()，onAttachedToWindow()中使用，
     *                可以在这些方法中通过postDelayed的方式来延迟执行showSoftInput()。
     */
    public static void showSoftInput(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            view.requestFocus();
            imm.showSoftInput(view, 0);
        }
    }

    /**
     * 隐藏软键盘
     * @param context
     * @param view view可以当前布局中已经存在的任何一个View，如果找不到可以用getWindow().getDecorView()
     */
    public static void hideSoftInput(Context context, View view){
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm!=null){
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }

    //获取输入法打开的状态
    public static boolean isSoftInputActive(Context context){
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        return imm.isActive();//isOpen若返回true，则表示输入法打开
    }

    //如果输入法在窗口上已经显示，则隐藏，反之则显示
    public static void toggleSoftInput(Context context){
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm!=null){
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 打开软键盘
     *
     * @param mEditText 输入框
     * @param mContext  上下文
     */
    public static void openKeyboard(EditText mEditText, Context mContext) {
        InputMethodManager imm = (InputMethodManager) mContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mEditText, InputMethodManager.RESULT_SHOWN);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY);
//        mEditText.setSelection(mEditText.getText().length());
        mEditText.setFocusableInTouchMode(true);
        mEditText.requestFocus();
    }

    /**
     * 关闭软键盘
     *
     * @param mEditText 输入框
     * @param mContext  上下文
     */
    public static void closeKeybord(EditText mEditText, Context mContext) {
        InputMethodManager imm = (InputMethodManager) mContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) return;
        imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }

    /**
     * 关闭软键盘
     */
    public static void closeKeyboard(Activity mContext) {
        InputMethodManager imm = (InputMethodManager) mContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) return;
        imm.hideSoftInputFromWindow(mContext.getWindow().getDecorView().getWindowToken(),
                0);
    }


    /**
     * 禁止显示系统自带软键盘
     *
     * @param editText editetx
     */
    public static void disableShowSoftInput(EditText editText) {
        if (android.os.Build.VERSION.SDK_INT <= 10) {
            editText.setInputType(InputType.TYPE_NULL);
            editText.setSelection(editText.getText().length());
        } else {
            Class<EditText> cls = EditText.class;
            Method method;
            try {
                method = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
                method.setAccessible(true);
                method.invoke(editText, false);
                editText.setSelection(editText.getText().length());

            } catch (Exception e) {
            }

            try {
                method = cls.getMethod("setSoftInputShownOnFocus", boolean.class);
                method.setAccessible(true);
                method.invoke(editText, false);
                editText.setSelection(editText.getText().length());

            } catch (Exception e) {
            }
        }
    }
}
