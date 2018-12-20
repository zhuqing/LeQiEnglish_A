package com.leqienglish.data.content;

import com.leqienglish.data.DataCacheAbstract;
import com.leqienglish.data.user.UserDataCache;
import com.leqienglish.database.ExecuteSQL;
import com.leqienglish.util.LQHandler;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import xyz.tobebetter.entity.english.Content;
import xyz.tobebetter.entity.user.User;

public class MyContentDataCache extends DataCacheAbstract<List<Content>> {

    private static MyContentDataCache instance;

    private static String DATA_TYPE = "MyContentDataCache";

    private static String UPDATE_TYPE = "MyContentDataCache";

    private LQHandler.Consumer<List<Content>> consumer;



    private MyContentDataCache(){

    }


    @Override
    public void load(LQHandler.Consumer<List<Content>> consumer) {
        this.consumer = consumer;
        super.load(consumer);
    }

    public  static MyContentDataCache getInstance(){
        if(instance !=null){
            return instance;
        }
        synchronized (MyContentDataCache.class){
            if(instance == null){
                instance = new MyContentDataCache();
            }
        }

        return instance;
    }

    @Override
    protected String getUpdateTimeType() {
        return null;
    }

    @Override
    protected List<Content> getFromCache() {
        User user = UserDataCache.getInstance().getUser();
        if(user == null){
            return Collections.emptyList();
        }
        ExecuteSQL.getDatasByType(DATA_TYPE,user.getId(),Content.class);
        return null;
    }

    @Override
    protected void putCache(List<Content> reciteContentVOS) {
        this.clearData();
        User user = UserDataCache.getInstance().getUser();
        if(user == null){
            return;
        }
        ExecuteSQL.insertLearnE(reciteContentVOS, user.getId(),DATA_TYPE);
    }

    @Override
    protected List<Content> getFromService() {
        User user = UserDataCache.getInstance().getUser();
        if(user == null){
            return null;
        }
        MultiValueMap<String,String> param = new LinkedMultiValueMap<>();
        param.add("userId", user.getId());



        try {
            Content[] contents = this.getRestClient().get("/english/content/findByUserId",param,Content[].class);
            return new ArrayList<>(Arrays.asList(contents));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void add(List<Content> contents) {
        if(this.getCacheData() == null){
            this.setCacheData(new ArrayList<>());
        }

        this.getCacheData().addAll(contents);

        if(this.consumer!=null){
            this.consumer.accept(this.getCacheData());
        }
    }

    @Override
    public void clearData() {
        super.clearData();
        User user = UserDataCache.getInstance().getUser();
        if(user == null){
            return ;
        }
        ExecuteSQL.delete(DATA_TYPE, user.getId());
    }


}
