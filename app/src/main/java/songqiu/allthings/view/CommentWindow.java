package songqiu.allthings.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.List;

import butterknife.OnClick;
import songqiu.allthings.R;
import songqiu.allthings.iterface.CommentListener;
import songqiu.allthings.util.ClickUtil;
import songqiu.allthings.util.KeyBoardUtils;
import songqiu.allthings.util.LogUtil;
import songqiu.allthings.util.StringUtil;

/*******
 *
 *Created by 杨延辉
 *
 *创建时间 2019/9/26
 *
 *类描述：评论window
 *
 ********/
public class CommentWindow extends PopupWindow{
    private View mView;
    CommentListener commentListener;
    private EditText editText;
    private TextView publishTv;

    public CommentWindow(Context context,String hintTv) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.custom_edittext, null);
         editText = (EditText) mView.findViewById(R.id.editText);
         publishTv = (TextView) mView.findViewById(R.id.publishTv);
        //设置PopupWindow的View
        this.setContentView(mView);
        //设置PopupWindow弹出窗体的宽
        this.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        //设置PopupWindow弹出窗体的高
        this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        //设置PopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.Animation);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        initNichengEt(context,editText,publishTv);
        editText.setHint(hintTv);
        publishTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = editText.getText().toString().trim();
               if(StringUtil.isEmpty(comment)) return;
               if(ClickUtil.onClick()) {
                   commentListener.publishComment(comment);
               }
            }
        });
        new Handler().postDelayed(new Runnable() {
            public void run() {
                //execute the task
                KeyBoardUtils.showSoftInput(context, editText);
            }
        }, 100);
    }
    public CommentWindow(Context context,String hintTv,String buttonText) {
        this(context,hintTv);
        if(publishTv!=null){
            publishTv.setText(buttonText);
        }

    }


    public void initNichengEt(Context context,EditText editText,TextView publishTv) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString().trim();
                if(!StringUtil.isEmpty(text)) {
                    publishTv.setBackgroundResource(R.drawable.rectangle_common_attention);
                }else {
                    publishTv.setBackgroundResource(R.drawable.rectangle_common_no_attention);
                }
            }
        });

    }

    public void setCommentListener(CommentListener commentListener) {
        this.commentListener = commentListener;
    }
}
