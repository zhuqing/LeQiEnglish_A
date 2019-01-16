package com.leqienglish.view.play.floatplay;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.leqienglish.R;
import com.leqienglish.util.LQHandler;
import com.leqienglish.util.TaskUtil;
import com.leqienglish.view.event.MultipleTouchEventInterface;
import com.leqienglish.view.percent.CirclePrecentView;
import com.leqienglish.view.play.PlayBarDelegate;
import com.leqienglish.view.play.PlayBarViewInterface;
import com.leqienglish.view.play.PlayStatus;

import java.util.ArrayList;
import java.util.List;

public class FloatPlayView extends CirclePrecentView implements MultipleTouchEventInterface, PlayBarViewInterface {

    private PlayBarDelegate playBarDelegate;

    private List<OnTouchListener> onTouchListenerList;

  private  Boolean couldExce = true;

    private PlayStatus playStatus = PlayStatus.STOP;

    private static final int playProgressBarColor = Color.RED;

    public FloatPlayView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.setFrontColor(playProgressBarColor);
        this.setBackgroundColor(Color.GRAY);
        this.setClickable(true);
        this.init();
    }

    private void init() {

        View.OnTouchListener otl = new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();


                if(onTouchListenerList != null){
                    for(View.OnTouchListener onTouchListener : onTouchListenerList){
                        if(couldExce){
                            couldExce = onTouchListener.onTouch(v,event);
                        }else{
                            onTouchListener.onTouch(v,event);
                        }

                    }
                }


                if(!couldExce){
                    //300毫秒不移动
                    TaskUtil.runlater(new LQHandler.Consumer<Boolean>(){
                        @Override
                        public void accept(Boolean aBoolean) {
                            couldExce = true;
                        }
                    },300L);
                    return true;

                }
                if (action != MotionEvent.ACTION_UP) {
                   return true;
                }
                switch (playStatus){
                    case STOP:
                        if(playBarDelegate != null){
                            playBarDelegate.play();
                        }
                        playStatus = PlayStatus.PLAY;
                        break;
                    case PLAY:
                        playStatus = PlayStatus.STOP;
                        if(playBarDelegate != null){
                            playBarDelegate.stop();
                        }
                        break;
                }

                invalidate();


                return false;
            }
        };
        this.setOnTouchListener(otl);


    }

    @Override
    public void setPlayBarDelegate(PlayBarDelegate playBarI) {
        this.playBarDelegate = playBarI;
    }

    @Override
    public void updateProgress(int progress, int max) {
       this.setPercent( progress*1.0f/max);
    }

    @Override
    public void play() {
        playStatus = PlayStatus.PLAY;
        invalidate();
    }

    @Override
    public void stop() {
        playStatus = PlayStatus.STOP;
        invalidate();
    }





    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float radius = this.getRadius();
        Paint paint = new Paint();
        paint.setColor(Color.LTGRAY);
        paint.setAntiAlias(true);
        canvas.drawCircle(radius, radius, radius - 4, paint);
        Bitmap sbit = null;

        switch (this.playStatus) {
            case PLAY:
                sbit = BitmapFactory.decodeResource(this.getResources(), R.drawable.pause_orange);
                break;
            case STOP:
                sbit = BitmapFactory.decodeResource(this.getResources(), R.drawable.play_orange);
                break;
        }

        Bitmap bitmap = Bitmap.createScaledBitmap(sbit, this.getWidth() / 2, this.getHeight() / 2, true);
        float top = radius - bitmap.getWidth() / 2;
        canvas.drawBitmap(bitmap, top, top, paint);

    }


    @Override
    public void addTouchEventHandler(OnTouchListener onTouchListener) {
        if(onTouchListenerList == null){
            onTouchListenerList = new ArrayList<>();
        }
        onTouchListenerList.add(onTouchListener);
    }


}
