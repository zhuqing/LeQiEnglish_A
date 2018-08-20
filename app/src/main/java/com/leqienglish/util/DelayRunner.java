package com.leqienglish.util;

import java.util.Timer;
import java.util.TimerTask;

public class DelayRunner {
    public static void runner(long delay,Runnable runnable){

        if(runnable == null){
            return;
        }
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runnable.run();
            }
        };
        Timer timer = new Timer();
        timer.schedule(task,delay);

    }
}
