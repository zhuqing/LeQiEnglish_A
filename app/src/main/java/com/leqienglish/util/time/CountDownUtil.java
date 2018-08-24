package com.leqienglish.util.time;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;

import com.leqienglish.util.BundleUtil;
import com.leqienglish.util.string.StringUtil;

/**
 * 倒计时
 * Created by zhuleqi on 2018/2/10.
 */
public class CountDownUtil {

    public static final int COUNT_DOWN = 1;
    public static final int OVER = -1;


    private long millis;

    private  CountDownTimer timer;

    private CountDownUtil(long millis) {
       this.millis = millis;
    }

    public static CountDownUtil createTime(long millis) {
        return new CountDownUtil(millis);
    }

    public void cancel(){

        if(timer!=null){
            timer.cancel();
        }
    }

    public void runCount(final Handler handler){

        timer = new CountDownTimer(this.millis, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                millis = millisUntilFinished;
                int seconds = (int) (millisUntilFinished/1000);
                int minute = (int) (seconds/60);

                minute = minute%60;
                seconds = seconds%60;
                Message message = new Message();
                message.what = COUNT_DOWN;
                Bundle data = BundleUtil.create(BundleUtil.DATA,StringUtil.toTime(minute,seconds));

                message.setData(data);
                handler.sendMessage(message);
            }

            @Override
            public void onFinish() {
                Message message = new Message();
                message.what = OVER;
                Bundle data = new Bundle();
                data.putString(BundleUtil.DATA,"00:00");
                message.setData(data);
                handler.sendMessage(message);
            }
        };
        timer.start();


    }



}
