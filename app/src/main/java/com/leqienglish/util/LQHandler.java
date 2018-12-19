package com.leqienglish.util;

/**
 * Created by zhuqing on 2017/8/19.
 */

public class LQHandler{
    public interface Callback<T,R> {
        public R call(T t);
    }

    public interface predict<T,Boolean> {
        public Boolean call(T t);
    }

    public interface Consumer<T>{

        public void accept(T t);
    }
}


