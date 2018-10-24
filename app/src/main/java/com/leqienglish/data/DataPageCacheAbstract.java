package com.leqienglish.data;

import android.os.AsyncTask;

import com.leqienglish.util.LQHandler;

/**
 * 分页缓存
 *
 * @param <T>
 */
public abstract class DataPageCacheAbstract<T> extends DataCacheAbstract<T> {
    protected Integer page;
    protected Integer pageSize;


    /**
     * 加载更多的数据,直接从服务段加载
     *
     * @param page
     * @param pageSize
     * @param finished
     */
    public void loadMore(int page, int pageSize, LQHandler.Consumer<T> finished) {
        this.page = page;
        this.pageSize = pageSize;

        AsyncTask asyncTask = new AsyncTask<Object, Object, T>() {
            @Override
            protected T doInBackground(Object[] objects) {
                T t = getFromService();
                runTask(t);

                return t;
            }

            @Override
            protected void onPostExecute(T t) {
                super.onPostExecute(t);

                if (finished != null) {
                    finished.accept(t);
                }

            }
        };

        asyncTask.execute();
    }

    /**
     * 加载更多的数据,直接从服务段加载
     *默认每次加载十条
     * @param page
     * @param finished
     */
    public void loadMore(int page,  LQHandler.Consumer<T> finished) {
        this.page = page;
        this.pageSize = 10;

        AsyncTask asyncTask = new AsyncTask<Object, Object, T>() {
            @Override
            protected T doInBackground(Object[] objects) {
                T t = getFromService();
                runTask(t);

                return t;
            }

            @Override
            protected void onPostExecute(T t) {
                super.onPostExecute(t);

                if (finished != null) {
                    finished.accept(t);
                }

            }
        };

        asyncTask.execute();
    }

    protected abstract T getMoreFromService();

}
