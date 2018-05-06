package com.leqienglish.sf;

import com.leqienglish.sf.task.HttpDownLoadTask;
import com.leqienglish.sf.task.HttpGetTask;
import com.leqienglish.sf.task.HttpGetTranslateTask;
import com.leqienglish.sf.task.HttpPostTask;
import com.leqienglish.util.LQHandler;

import org.springframework.http.MediaType;

import java.net.HttpURLConnection;
import java.util.List;
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

    public static  <T> void  getTrans(String path , Class claz, Map<String,?> variables, LQHandler.Consumer<T> consumer){
        new HttpGetTranslateTask<T>(path,claz,consumer,variables).execute();
    }

    public static <T> void post(String path , Class claz,Map<String,?> variables,LQHandler.Consumer<T> consumer){
        new HttpPostTask<>(http+path,claz,consumer,variables).execute();
    }

    public static <T> void download(String path , String filePath , MediaType mediaType,Map<String, ?> variables, LQHandler.Consumer<String> consumer){
        new HttpDownLoadTask(http+path,filePath,mediaType,consumer,variables).execute();
    }

    public static void insertOrUpdate(String id,String url,String json , String type){

    }




}


