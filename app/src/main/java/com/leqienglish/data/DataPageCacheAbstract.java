package com.leqienglish.data;

import android.os.AsyncTask;

import com.leqienglish.util.AppType;
import com.leqienglish.util.LQHandler;

import org.springframework.util.MultiValueMap;

/**
 * 分页缓存
 *
 * @param <T>
 */
public abstract class DataPageCacheAbstract<T> extends DataCacheAbstract<T> {
    /**
     * 页码，默认是1
     */
    private Integer page = 1;
    /**
     * 每页的条数，默认是10
     */
    private Integer pageSize = AppType.PAGE_SIZE;

    public static final String PAGE = "page";
    public static final String PAGE_SIZE = "pageSize";

    protected MultiValueMap<String, String> getMutilValueMap(){
        MultiValueMap<String, String> param = super.getMutilValueMap();
        param.add(PAGE,this.page+"");
        param.add(PAGE_SIZE,this.pageSize+"");
        return param;
    }

//
//    /**
//     * 加载更多的数据,直接从服务段加载
//     *
//     * @param page
//     * @param pageSize
//     * @param finished
//     */
//    public void loadMore(int page, int pageSize, LQHandler.Consumer<T> finished) {
//        this.page = page;
//        this.pageSize = pageSize;
//
//        AsyncTask asyncTask = new AsyncTask<Object, Object, T>() {
//            @Override
//            protected T doInBackground(Object[] objects) {
//                T t = getFromService();
//                runTask(t);
//
//                return t;
//            }
//
//            @Override
//            protected void onPostExecute(T t) {
//                super.onPostExecute(t);
//
//                if (finished != null) {
//                    finished.accept(t);
//                }
//
//            }
//        };
//
//        asyncTask.execute();
//    }

    /**
     * 加载更多的数据,直接从服务段加载
     *默认每次加载十条
     * @param page
     * @param finished
     */
    public void loadMore(int page,  int pageSize , LQHandler.Consumer<T> finished) {
        this.page = page;
        this.pageSize = pageSize;

        AsyncTask asyncTask = new AsyncTask<Object, Object, T>() {
            @Override
            protected T doInBackground(Object[] objects) {
                T t = getMoreFromService();
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

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
