package com.leqienglish.sf.databasetask;

import android.os.AsyncTask;

import com.leqienglish.util.LQHandler;

/**
 * Created by zhuqing on 2018/4/29.
 */

public abstract class DataBaseTask<T> extends AsyncTask<Object, Object, T> {
    private LQHandler.Consumer consumer;
    private Boolean running = false;
    protected boolean cancel = false;

    public DataBaseTask(LQHandler.Consumer consumer) {
        this.setConsumer(consumer);
    }

    public DataBaseTask() {

    }

    public void destroy() {
        this.cancel = true;
        this.running = false;
    }

    public boolean isRunning() {
        return running;
    }

    @Override
    protected T doInBackground(Object... objects) {
        running = true;
        return run(objects);

    }

    protected abstract T run(Object... params);


    @Override
    protected void onPostExecute(T t) {

        if (cancel) {
            cancel = false;
            return;
        }
        if (this.getConsumer() == null) {
            return;
        }
        this.getConsumer().accept(t);
        running = false;
    }

    public LQHandler.Consumer getConsumer() {
        return consumer;
    }

    public void setConsumer(LQHandler.Consumer consumer) {
        this.consumer = consumer;
    }
}
