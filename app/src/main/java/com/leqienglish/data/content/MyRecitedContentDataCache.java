package com.leqienglish.data.content;

import com.leqienglish.data.DataCacheAbstract;
import com.leqienglish.data.user.UserDataCache;
import com.leqienglish.util.LQHandler;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import xyz.tobebetter.entity.english.content.ReciteContentVO;
import xyz.tobebetter.entity.user.User;

public class MyRecitedContentDataCache extends DataCacheAbstract<List<ReciteContentVO>> {

    private static MyRecitedContentDataCache myRecitedContentDataCache;

    private LQHandler.Consumer<List<ReciteContentVO>> consumer;



    private MyRecitedContentDataCache(){

    }


    @Override
    public void load(LQHandler.Consumer<List<ReciteContentVO>> consumer) {
        this.consumer = consumer;
        super.load(consumer);
    }

    public  static MyRecitedContentDataCache getInstance(){
        if(myRecitedContentDataCache !=null){
            return myRecitedContentDataCache;
        }
        synchronized (MyRecitingContentDataCache.class){
            if(myRecitedContentDataCache == null){
                myRecitedContentDataCache = new MyRecitedContentDataCache();
            }
        }

        return myRecitedContentDataCache;
    }

    @Override
    protected List<ReciteContentVO> getFromCache() {

        return null;
    }

    @Override
    protected void putCache(List<ReciteContentVO> reciteContentVOS) {

    }

    @Override
    protected List<ReciteContentVO> getFromService() {
        User user = UserDataCache.getInstance().getUser();
        if(user == null){
            return null;
        }
        MultiValueMap<String,String> param = new LinkedMultiValueMap<>();
        param.add("userId", user.getId());



        try {
            ReciteContentVO[] contents = this.getRestClient().get("/english/content/findUserRecited",param,ReciteContentVO[].class);
            return new ArrayList<>(Arrays.asList(contents));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void add(List<ReciteContentVO> reciteContentVOS) {
        if(this.getCacheData() == null){
            this.setCacheData(new ArrayList<>());
        }

        this.getCacheData().addAll(reciteContentVOS);

        if(this.consumer!=null){
            this.consumer.accept(this.getCacheData());
        }
    }

    @Override
    public void remove(List<ReciteContentVO> reciteContentVOS) {

    }
}
