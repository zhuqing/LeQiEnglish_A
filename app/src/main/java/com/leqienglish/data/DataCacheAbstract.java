package com.leqienglish.data;

import android.content.res.Resources;
import android.os.AsyncTask;

import com.leqienglish.R;
import com.leqienglish.sf.RestClient;
import com.leqienglish.util.LQHandler;


/**
 * 从缓存中加载数据或从服务端加载数据
 *
 * @param <T>
 */
public abstract class DataCacheAbstract<T> {

    private T cacheData;

    private RestClient restClient;

    /**
     * 根据ID获取字符串
     *
     * @param id
     * @return
     */
    protected String getString(int id) {
        return Resources.getSystem().getString(id);
    }

    /**
     * 是否需要更新
     * @return
     */
    protected boolean shouldUpdate(T t){
        return true;
    }

    /**
     * 从缓存中取数据
     *
     * @return
     */
    protected abstract T getFromCache();

    /**
     * 把数据放入缓存
     *
     * @param t
     */

    protected abstract void putCache(T t);

    /**
     * 从服务端取数据
     *
     * @return
     */
    protected abstract T getFromService();

    /**
     * 从服务端取数据
     *
     * @return
     */
    public abstract void add(T t);

    /**
     * 从服务端取数据
     *
     * @return
     */
    public abstract void remove(T t);

    /**
     * 向缓存中增加数据
     * @param t
     */
   // public abstract void add(T t);

    /**
     * 加载数据
     *
     * @param consumer
     */
    public void load(LQHandler.Consumer<T> consumer) {

        if (this.getCacheData() != null) {
            if (consumer != null) {
                consumer.accept(this.cacheData);
            }
        }

        AsyncTask asyncTask = new AsyncTask<Object, Object, T>() {
            @Override
            protected T doInBackground(Object[] objects) {

                T t = getFromCache();
                setCacheData(t);
                if (t == null) {
                    t = getFromService();
                    runTask(t);
                } else {
                    runTask();
                }
                return t;
            }

            @Override
            protected void onPostExecute(T t) {
                super.onPostExecute(t);
                setCacheData(t);
                if (consumer != null) {
                    consumer.accept(t);
                }

            }
        };

        asyncTask.execute();
    }

    /**
     * 刷新数据
     */
    public void refresh() {

    }


    private void runTask(T t) {

        AsyncTask asyncTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {

                putCache(t);
                return null;
            }
        };

        asyncTask.execute();
    }

    private void runTask() {
        AsyncTask asyncTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                if(!shouldUpdate(getCacheData())){
                    return null;
                }
                T temp = getFromService();
                putCache(temp);
                return null;
            }
        };

        asyncTask.execute();
    }



    public T getCacheData() {
        return cacheData;
    }

    public void setCacheData(T cacheData) {
        this.cacheData = cacheData;
    }

    protected RestClient getRestClient() {
        if (this.restClient == null) {
            try {
                this.restClient = new RestClient(this.getString(R.string.HOST));
            }catch (Exception ex){
                this.restClient = new RestClient("http://192.168.43.9:8080");
            }

        }
        return restClient;
    }


}
