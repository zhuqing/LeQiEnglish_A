package com.leqienglish.sf.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leqienglish.entity.Message;
import com.leqienglish.util.LQHandler;

import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;

/**
 * Created by zhuqing on 2018/4/21.
 */

public class HttpDownLoadTask<T> extends HttpTask<T>{
    public HttpDownLoadTask(String path, Class<T> claz, LQHandler.Consumer consumer, Map variables) {
        super(path, claz, consumer, variables);
    }


    @Override
    protected T getT(RestTemplate restTemplate) {
        return restTemplate.getForObject(this.getPath(),this.getClaz(),this.getVariables());
    }
}
