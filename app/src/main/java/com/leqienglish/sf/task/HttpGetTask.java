package com.leqienglish.sf.task;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leqienglish.entity.Message;
import com.leqienglish.util.LQHandler;

import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public  class HttpGetTask<T> extends HttpTask<T> {

    public HttpGetTask(String path, Class<T> claz, LQHandler.Consumer<T> consumer, Map<String, ?> variables) {
        super(path, claz, consumer, variables);
    }

    @Override
    protected T getT(RestTemplate restTemplate) {
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

        try {
            Message message = restTemplate.getForObject(this.getPath(), Message.class,this.getVariables());
            String data = (String) message.getData();
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(data, this.getClaz());//对象化
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}