package songqiu.allthings.view;

import android.annotation.SuppressLint;
import android.content.Context;

import android.graphics.Canvas;

import android.graphics.Color;

import android.graphics.Paint;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.EditText;

import songqiu.allthings.util.LogUtil;


/**

 * Created by loonggg on 15/8/29.

 */

@SuppressLint("AppCompatCustomView")
public class MyEditText extends EditText {

    private String str;

    public MyEditText(Context context){

        super(context);

    }

    public MyEditText(Context context, AttributeSet attrs) {

        super(context, attrs);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MyEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public MyEditText(Context context, AttributeSet attrs, int defStyle) {

        super(context, attrs, defStyle);

    }

    public void setStr(String str){
        this.str = str;

    }

    @Override

    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        Paint paint =  new Paint();

        paint.setTextSize(29);
        paint.setColor(Color.parseColor("#BFBFBF"));
        paint.setAntiAlias(true);

        //编写提示文字。

        canvas.drawText(str,20,32,paint);

        super.onDraw(canvas);

    }

}
