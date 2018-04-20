package com.leqienglish.sf.task;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leqienglish.entity.Message;
import com.leqienglish.util.LQHandler;

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
        Message message = restTemplate.getForObject(this.getPath(), Message.class,this.getVariables());
        String data = (String) message.getData();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(MapperFeature.USE_GETTERS_AS_SETTERS, true);
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true) ;


        JsonFactory factory = new JsonFactory();//实例JSON工程对象
        //使用JsonParser处理json
        JsonParser jp = null;
        List<T> contentList = new ArrayList<>();
        try {
            jp = factory.createParser(data);

            return mapper.readValue(jp, this.getClaz());//对象化

        } catch (IOException e) {
            e.printStackTrace();
        }


        return  null;

    }


}