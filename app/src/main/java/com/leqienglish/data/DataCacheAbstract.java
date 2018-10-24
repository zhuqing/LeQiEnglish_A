package com.leqienglish.data;

import android.content.res.Resources;
import android.os.AsyncTask;

import com.leqienglish.database.ExecuteSQL;
import com.leqienglish.sf.LQService;
import com.leqienglish.sf.RestClient;
import com.leqienglish.util.AppType;
import com.leqienglish.util.LQHandler;
import com.leqienglish.util.date.DateUtil;
import com.leqienglish.util.network.NetWorkUtil;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import xyz.tobebetter.entity.Entity;


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
     *
     * @return
     */
    protected boolean needUpdate() {

        if(!NetWorkUtil.isConnect(AppType.mainContext)){
            return false;
        }

        Long updateTime = this.getUpdateTime();

        if(updateTime == 0L){

            return true;
        }



        Long currentTime = System.currentTimeMillis();

       return needUpdate(updateTime,currentTime);

    }

    /**
     * 是否需要更新，不在同一天就更新
     * @param lastUpdateTime
     * @param currentTime
     * @return
     */
    public boolean needUpdate(Long lastUpdateTime,Long currentTime){
        Calendar lastUpdateC = DateUtil.toCalendar(lastUpdateTime);
        Calendar currentC = DateUtil.toCalendar(currentTime);


       if(lastUpdateC.get(Calendar.DAY_OF_YEAR) != currentC.get(Calendar.DAY_OF_YEAR)){
           this.insertUpdateTime();
           return true;
       }

       return false;


    }

    /**
     * 获取更新时间的类型
     *
     * @return
     */
    protected abstract String getUpdateTimeType();


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
     * 清除缓存中的数据
     *
     * @return
     */
    public abstract void clearData();

    protected  void delete(){

    }

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

        if(!this.needUpdate()){
            if (this.getCacheData() != null) {
                if (consumer != null) {
                    consumer.accept(this.cacheData);
                }
                return;
            }
        }


        AsyncTask asyncTask = new AsyncTask<Object, Object, T>() {
            @Override
            protected T doInBackground(Object[] objects) {
                T t = null;

                //如果需要更新先清空缓存
                if(!needUpdate()){
                    t = getFromCache();
                }else{
                    clearData();
                }


                setCacheData(t);
                //没有网络直接返回
                if(!NetWorkUtil.isConnect(AppType.mainContext)){
                    return t;
                }
                if (t == null) {
                    t = getFromService();
                    runTask(t);
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
     * 加载最新的数据
     * @param consumer
     */
    public void loadNewest(LQHandler.Consumer<T> consumer){
        AsyncTask asyncTask = new AsyncTask<Object, Object, T>() {
            @Override
            protected T doInBackground(Object[] objects) {

                T t = getFromService();
                setCacheData(t);
                if (t != null) {
                    putCache(t);
                }
                return t;
            }

            @Override
            protected void onPostExecute(T t) {
                super.onPostExecute(t);
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
        AsyncTask asyncTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {

                T temp = getFromService();
                setCacheData(temp);
                putCache(temp);
                return null;
            }
        };

        asyncTask.execute();
    }


    /**
     * 在其他线程中把数据放入缓存
     * @param t
     */
    protected final void runTask(T t) {

        AsyncTask asyncTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {

                putCache(t);
                return null;
            }
        };

        asyncTask.execute();
    }

    /**
     * 在其他线程中获取数据
     */
    protected final void runTask() {
//        AsyncTask asyncTask = new AsyncTask() {
//            @Override
//            protected Object doInBackground(Object[] objects) {
//
//                T temp = getFromService();
//                putCache(temp);
//                return null;
//            }
//        };
//
//        asyncTask.execute();
    }

    /**
     * 获取缓存数据，没有从数据库中加载
     * @return
     */
    public T getCacheData() {
        return cacheData;
    }

    public void setCacheData(T cacheData) {
        this.cacheData = cacheData;
    }


    protected RestClient getRestClient() {
        if (this.restClient == null) {
            try {
                this.restClient = new RestClient(LQService.getHttp());
            } catch (Exception ex) {
                this.restClient = new RestClient("http://www.leqienglish.com");
            }

        }
        return restClient;
    }

    /**
     *插入数据更新的时间
     */
    private void insertUpdateTime(){
        String type = this.getUpdateTimeType();

        Entity entity = new Entity();
        entity.setId(type);
        entity.setCreateDate(System.currentTimeMillis());
        ExecuteSQL.insertLearnE(Arrays.asList(entity),null,type);
    }

    /**
     * 获取数据更新的时间
     * @return
     */
    private Long getUpdateTime(){
        String type = this.getUpdateTimeType();

       List<Entity> list = ExecuteSQL.getDatasByType(type,Entity.class);

        if(list == null || list.isEmpty()){
            this.insertUpdateTime();
            return 0L;
        }

        return list.get(0).getCreateDate();
    }

}
