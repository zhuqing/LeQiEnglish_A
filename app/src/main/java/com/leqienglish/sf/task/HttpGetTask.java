package com.leqienglish.sf.task;

import com.leqienglish.util.LOGGER;
import com.leqienglish.util.LQHandler;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Map;

public  class HttpGetTask<T> extends HttpTask<T> {

    private LOGGER logger = new LOGGER(HttpGetTask.class);
    public HttpGetTask(String path, Class<T> claz, LQHandler.Consumer<T> consumer, Map<String, String> variables) {
        super(path, claz, consumer, variables);
        logger.d("new :"+this.getPath());
    }

    @Override
    protected T getT() {
        logger.d(this.getPath());
        MultiValueMap<String, String> parameter = new LinkedMultiValueMap<>();
        parameter.setAll(this.getVariables());
        try {
           return this.restClient.get(this.getPath(),parameter,this.getClaz());
        } catch (Exception e) {
            e.printStackTrace();
            logger.e(e.getMessage());
        }

        return null;
    }


}