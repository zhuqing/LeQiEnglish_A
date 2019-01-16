package com.leqienglish.view.percent;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.leqienglish.R;

/**
 * 圆形百分比视图
 */
public class CirclePrecentView extends View{

    /**
     * 百分比 从0到1
     */
    private float percent = 0.0f;

    private int backgroundColor = Color.WHITE;

    private int frontColor = 0xff4cd964;



    public CirclePrecentView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CirclePrecentView);
        this.backgroundColor = typedArray.getInt(R.styleable.CirclePrecentView_background_color,this.backgroundColor);
        this.frontColor = typedArray.getInt(R.styleable.CirclePrecentView_front_color,this.frontColor);
        this.percent = typedArray.getFloat(R.styleable.CirclePrecentView_precent, this.getPercent());

        typedArray.recycle();
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float radius = getRadius();

        Paint  paint= new Paint();

        if(percent>=1f){
            paint.setColor(this.frontColor);
            paint.setAntiAlias(true);
            canvas.drawCircle(radius, radius, radius, paint);
            return;
        }

        paint.setColor(this.backgroundColor);
        paint.setAntiAlias(true);
        canvas.drawCircle(radius, radius, radius, paint);

        if(percent<=0f){
            return;
        }

        paint.setColor(this.frontColor);
        paint.setAntiAlias(true);
        RectF rectf = new RectF(0, 0, getWidth(), getHeight());
        canvas.drawArc(rectf, 270, 360f*percent, true, paint);

    }

    protected float getRadius(){
        return  Math.min(getWidth(), getHeight())/2.0f;
    }

    /**
     * 百分比，大于等于0，小于等于1
     */
    public float getPercent() {
        return percent;
    }

    public void setPercent(float percent) {
        this.percent = percent;
        this.invalidate();
    }

    /**
     *背景颜色，默认白色
     */
    public int getBackgroundColor() {
        return backgroundColor;
    }

    @Override
    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    /**
     * 前景颜色，先当前百分比的大小,默认绿色
     */
    public int getFrontColor() {
        return frontColor;
    }

    public void setFrontColor(int frontColor) {
        this.frontColor = frontColor;
    }
}
