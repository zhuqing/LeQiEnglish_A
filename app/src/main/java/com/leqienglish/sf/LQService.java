package com.leqienglish.sf;

import com.leqienglish.sf.task.HttpGetTask;
import com.leqienglish.sf.task.HttpPostTask;
import com.leqienglish.util.LQHandler;

import java.net.HttpURLConnection;
import java.util.Map;

/**
 * Created by zhuqing on 2017/8/19.
 */

public class LQService {
    private HttpURLConnection hipClient;

    private static String http = "http://192.168.1.115:8080";

    public static  <T> void  get(String path , Class claz, Map<String,?> variables, LQHandler.Consumer<T> consumer){
         new HttpGetTask(http+path,claz,consumer,variables).execute();
    }

    public static <T> void post(String path , Class claz,Map<String,?> variables,LQHandler.Consumer<T> consumer){
        new HttpPostTask<>(http+path,claz,consumer,variables).execute();
    }






}


