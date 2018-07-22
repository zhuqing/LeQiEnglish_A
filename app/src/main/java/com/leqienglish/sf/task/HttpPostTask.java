package com.leqienglish.sf.task;

import com.leqienglish.util.LQHandler;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Created by zhuqing on 2017/8/20.
 */

public class HttpPostTask<T> extends HttpTask<T> {
    private Object value;
    public HttpPostTask(String path, Object value, Class<T> claz, LQHandler.Consumer consumer, Map<String, String> variables) {
        super(path, claz, consumer, variables);
        this.value = value;
    }

    @Override
    protected T getT() {
        MultiValueMap<String, String> parameter = new LinkedMultiValueMap<>();
        parameter.setAll(this.getVariables());
        try {
            return (T)this.restClient.post(getPath(),value,parameter,getClaz());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }
}
