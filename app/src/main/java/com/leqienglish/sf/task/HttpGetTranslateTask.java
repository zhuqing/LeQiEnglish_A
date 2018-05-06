package com.leqienglish.sf.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leqienglish.entity.Message;
import com.leqienglish.util.LQHandler;

import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;

public  class HttpGetTranslateTask<T> extends HttpTask<T> {

    public HttpGetTranslateTask(String path, Class<T> claz, LQHandler.Consumer<T> consumer, Map<String, ?> variables) {
        super(path, claz, consumer, variables);
    }

    @Override
    protected T getT(RestTemplate restTemplate) {
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

        try {
           return restTemplate.getForObject(this.getPath(), this.getClaz(),this.getVariables());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}