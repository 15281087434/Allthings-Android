package songqiu.allthings.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import songqiu.allthings.R;
import songqiu.allthings.util.DensityUtil;

/**
 * Created by Administrator on 2018/5/29.
 */

public class ProGrossCircleView extends View {
    private int outer_circle_color;
    private int inner_circle_color;

    private int big_text_color;
    private float big_text_size;

    private float small_text_size;
    private int small_text_color;

    private Context mContext;

    public ProGrossCircleView(Context context) {
        this(context,null,0);
    }

    public ProGrossCircleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);

    }
    private int[] arcColors= new int[] {Color.parseColor("#00C5C8"), Color.parseColor("#0ED1AB"), Color.parseColor("#00C5C8")};

    public void setArcColors(int[] arcColors) {
        this.arcColors = arcColors;
    }

    public ProGrossCircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ProGrossCircleView);
        outer_circle_color = typedArray.getColor(R.styleable.ProGrossCircleView_outer_circle_color, Color.parseColor("#FFC700"));
        inner_circle_color = typedArray.getColor(R.styleable.ProGrossCircleView_inner_circle_color, Color.WHITE);
        big_text_color = typedArray.getColor(R.styleable.ProGrossCircleView_big_text_color, Color.BLACK);
        small_text_color= typedArray.getColor(R.styleable.ProGrossCircleView_small_text_color, Color.BLACK);
        big_text_size = typedArray.getFloat(R.styleable.ProGrossCircleView_big_text_size,24);
        small_text_size = typedArray.getFloat(R.styleable.ProGrossCircleView_small_text_size,12);
        unit = typedArray.getString(R.styleable.ProGrossCircleView_unit);
        desc = typedArray.getString(R.styleable.ProGrossCircleView_desc);
        typedArray.recycle();

        mOuterPaint = new Paint();
        mOuterPaint.setAntiAlias(true);
        mOuterPaint.setColor(outer_circle_color);
        mOuterPaint.setStrokeCap(Paint.Cap.ROUND);
        mOuterPaint.setStyle(Paint.Style.STROKE);
        mOuterPaint.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,5,getResources().getDisplayMetrics()));

        mInnerPaint = new Paint();
        mInnerPaint.setAntiAlias(true);
        mInnerPaint.setColor(inner_circle_color);
        mInnerPaint.setStrokeCap(Paint.Cap.ROUND);
        mInnerPaint.setStyle(Paint.Style.STROKE);
        mInnerPaint.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,5,getResources().getDisplayMetrics()));



        bigTextPaint = new Paint();
//        bigTextPaint.setTextSize(DensityUtil.dip2px(context,big_text_size));
        bigTextPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,big_text_size,getResources().getDisplayMetrics()));
        bigTextPaint.setAntiAlias(true);
        bigTextPaint.setColor(big_text_color);

        smallTextPaint = new Paint();
//        smallTextPaint.setTextSize(DensityUtil.dip2px(context,small_text_size));
        smallTextPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,small_text_size,getResources().getDisplayMetrics()));
        smallTextPaint.setAntiAlias(true);
        smallTextPaint.setColor(small_text_color);


    }



    Paint mOuterPaint;
    Paint mInnerPaint;
    Paint bigTextPaint;
    Paint smallTextPaint;
    String unit;
    int currentScore = 0;
    String desc;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);



        RectF rect = new RectF();
        int space = DensityUtil.dip2px(mContext,5);
        rect.set(space,space,getWidth()-space,getHeight()-space);
        canvas.drawArc(rect,-90,360,false,mOuterPaint);

        SweepGradient sweepGradient = new SweepGradient(getWidth()/2,getHeight()/2,arcColors,null);
        mInnerPaint.setShader(sweepGradient);
        canvas.save();
        canvas.rotate(-90,getWidth()/2,getHeight()/2);

        canvas.drawArc(rect,0,currentScore*3.6f,false,mInnerPaint);
        canvas.restore();

        Rect textBounds = new Rect();
        try {
            bigTextPaint.getTextBounds(currentScore+unit,0,(currentScore+unit).length(),textBounds);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int dx = (getWidth()-textBounds.width())/2;
        Paint.FontMetrics fontMetrics = bigTextPaint.getFontMetrics();
        int baseLine = (int) (getHeight()/2+((fontMetrics.bottom-fontMetrics.top)/2-fontMetrics.bottom)+space/2);

        canvas.drawText(currentScore+unit,dx,baseLine-space*2,bigTextPaint);

        Rect sTextBounds = new Rect();
        smallTextPaint.getTextBounds(desc,0,desc.length(),sTextBounds);
        int dx2 = (getWidth()-sTextBounds.width())/2;
        Paint.FontMetrics fontMetrics2 = smallTextPaint.getFontMetrics();
        int baseLine2 = (int) (getHeight()/2-space/2+(fontMetrics.bottom-fontMetrics.top)/2+(fontMetrics2.bottom-fontMetrics2.top)/2-fontMetrics2.bottom);


        canvas.drawText(desc,dx2,baseLine2,smallTextPaint);




    }

    public void setScore(int currentScore) {
        this.currentScore = currentScore;
        postInvalidate();
    }
}
