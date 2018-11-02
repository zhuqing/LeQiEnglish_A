package com.leqienglish.util;

import android.os.AsyncTask;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

public class TaskUtil {

    /**
     * 延时一定时间后在 主线程执行
     * @param finished
     * @param dlayM
     */
    public static void runlater(LQHandler.Consumer<Boolean> finished, long dlayM){

        Observable.just(true).delay(dlayM, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean v) throws Exception {
                        if(finished != null){
                            finished.accept(v);
                        }
                    }
                });
    }


    /**
     * 异步运行
     * @param runnable
     */
    public static void run(Runnable runnable){
       AsyncTask asyncTask =  new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                runnable.run();
                return null;
            }
        };

       asyncTask.execute();
    }
}
