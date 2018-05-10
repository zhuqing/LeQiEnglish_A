package com.leqienglish.sf;

import android.os.Handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leqienglish.entity.Message;
import com.leqienglish.sf.task.HttpDownLoadTask;
import com.leqienglish.sf.task.HttpGetTask;
import com.leqienglish.sf.task.HttpGetTranslateTask;
import com.leqienglish.sf.task.HttpPostTask;
import com.leqienglish.util.LQHandler;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by zhuqing on 2017/8/19.
 */

public class LQService {
    private HttpURLConnection hipClient;

    public static String http = "http://192.168.1.115:8080";

    public static  <T> void  get(String path , Class claz, Map<String,?> variables, LQHandler.Consumer<T> consumer){
         new HttpGetTask(http+path,claz,consumer,variables).execute();
    }

    public static  <T> void  getTrans(String path , Class claz, Map<String,?> variables, LQHandler.Consumer<T> consumer){
        new HttpGetTranslateTask<T>(path,claz,consumer,variables).execute();
    }

    public static <T> void post(String path , Class claz,Map<String,?> variables,LQHandler.Consumer<T> consumer){
        new HttpPostTask<>(http+path,claz,consumer,variables).execute();
    }

    public static void download(String path , Handler handler) throws IOException {
        //创建一个URL对象
        URL url=new URL(http+path);
        //创建一个HTTP链接
        HttpURLConnection urlConn=(HttpURLConnection)url.openConnection();
        urlConn.setConnectTimeout(10000);
        urlConn.connect();

        if(urlConn.getResponseCode() != HttpURLConnection.HTTP_OK){
            return;
        }

        urlConn.getContentLength();
    }

    public static <T>  Object post(String path , Class claz,Map<String,?> variables,T data) throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ObjectMapper mapper = new ObjectMapper();

        String value = mapper.writeValueAsString(data);
        HttpEntity<String> requestEntity = new HttpEntity<String>(value, headers);
        Message message =  restTemplate.postForObject(path,requestEntity, Message.class,variables);

        if(message == null || Objects.equals(message.getStatus(),Message.ERROR)){
            return null;
        }

        if(message.getData() == null || message.getData().isEmpty()){
            return null;
        }

        return mapper.readValue(message.getData(),claz);

    }

    public static <T> void download(String path , String filePath , MediaType mediaType,Map<String, ?> variables, LQHandler.Consumer<String> consumer){
        new HttpDownLoadTask(http+path,filePath,mediaType,consumer,variables).execute();
    }

    public static void insertOrUpdate(String id,String url,String json , String type){

    }




}


