package com.leqienglish.sf;

import android.os.Handler;

import com.leqienglish.sf.task.HttpGetTask;
import com.leqienglish.sf.task.HttpGetTranslateTask;
import com.leqienglish.sf.task.HttpPostTask;
import com.leqienglish.sf.task.HttpPutTask;
import com.leqienglish.util.LQHandler;

import org.springframework.http.MediaType;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import xyz.tobebetter.entity.word.Word;


/**
 * Created by zhuqing on 2017/8/19.
 */

public class LQService {
    private HttpURLConnection hipClient;

    public static String http ;

    public static String getHttp(){
        if(http == null){
            //http = "http://www.leqienglish.com/";
            http = "http://192.168.43.9:8080/";
        }

        return http;
    }

    public static <T> void put(String path ,Object value, Class claz,Map<String,?> variables,LQHandler.Consumer<T> consumer){
        new HttpPutTask(getHttp()+path,value,claz,consumer,variables).execute();
    }

    public static  <T> void  get(String path , Class claz, Map<String,String> variables, LQHandler.Consumer<T> consumer){
         new HttpGetTask(getHttp()+path,claz,consumer,variables).execute();
    }

    public static  <T extends Word> void  getTrans(String word , Map<String,String> variables, LQHandler.Consumer<T> consumer){
        new HttpGetTranslateTask<T>(word,consumer,variables).execute();
    }

    public static <T> void post(String path ,Object value, Class claz,Map<String,?> variables,LQHandler.Consumer<T> consumer){
        new HttpPostTask(getHttp()+path,value,claz,consumer,variables).execute();
    }

    public static void download(String path , Handler handler) throws IOException {
        //创建一个URL对象
        URL url=new URL(getHttp()+path);
        //创建一个HTTP链接
        HttpURLConnection urlConn=(HttpURLConnection)url.openConnection();
        urlConn.setConnectTimeout(10000);
        urlConn.connect();

        if(urlConn.getResponseCode() != HttpURLConnection.HTTP_OK){
            return;
        }

        urlConn.getContentLength();
    }



    public static <T> void download(String path , String filePath , MediaType mediaType,Map<String, ?> variables, LQHandler.Consumer<String> consumer){
       // new HttpDownLoadTask(http+path,filePath,mediaType,consumer,variables).execute();
    }

    public static void insertOrUpdate(String id,String url,String json , String type){

    }




}


