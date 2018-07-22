package com.leqienglish.sf.task;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leqienglish.entity.Message;
import com.leqienglish.sf.RestClient;
import com.leqienglish.util.LQHandler;

import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public  class HttpGetTask<T> extends HttpTask<T> {


    public HttpGetTask(String path, Class<T> claz, LQHandler.Consumer<T> consumer, Map<String, String> variables) {
        super(path, claz, consumer, variables);

    }

    @Override
    protected T getT() {
        MultiValueMap<String, String> parameter = new LinkedMultiValueMap<>();
        parameter.setAll(this.getVariables());
        try {
           return this.restClient.get(this.getPath(),parameter,this.getClaz());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


}