package com.leqienglish.sf.databasetask;

import android.os.AsyncTask;

import com.leqienglish.util.LQHandler;

/**
 * Created by zhuqing on 2018/4/29.
 */

public abstract class DataBaseTask<T> extends AsyncTask<Object, Object, T> {
    private LQHandler.Consumer consumer;
    public DataBaseTask(LQHandler.Consumer consumer){
        this.consumer = consumer;
    }


    @Override
    protected void onPostExecute(T t) {
        if(this.consumer == null){
            return;
        }
        this.consumer.applay(t);
    }
}
