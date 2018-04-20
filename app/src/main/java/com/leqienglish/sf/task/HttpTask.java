package com.leqienglish.sf.task;

import android.os.AsyncTask;
import android.util.Log;

import com.leqienglish.util.LQHandler;

import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;

/**
 * get请求
 * @param <T>
 */
public abstract   class HttpTask<T> extends AsyncTask<Object, Object, T> {
    private  String path;
    private LQHandler.Consumer<T> consumer;
    private Class<T> claz;
    private Map<String,?> variables;

    public HttpTask(String path , Class<T> claz, LQHandler.Consumer<T> consumer,Map<String,?> variables){
        this.path = path;
        this.claz = claz;
        this.consumer = consumer;
        this.variables = variables;
    }
    @Override
    protected T doInBackground(Object... params) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());


            restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
            return this.getT(restTemplate);
        } catch (Exception e) {
            Log.e("MainActivity", e.getMessage(), e);
        }

        return null;
    }

    protected abstract T getT(RestTemplate restTemplate);

    @Override
    protected void onPostExecute(T t) {
        this.consumer.applay(t);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public LQHandler.Consumer<T> getConsumer() {
        return consumer;
    }

    public void setConsumer(LQHandler.Consumer<T> consumer) {
        this.consumer = consumer;
    }

    public Class<T> getClaz() {
        return claz;
    }

    public void setClaz(Class<T> claz) {
        this.claz = claz;
    }

    public Map<String, ?> getVariables() {
        if(this.variables == null){
            return Collections.EMPTY_MAP;
        }
        return variables;
    }

    public void setVariables(Map<String, ?> variables) {

        this.variables = variables;
    }

}