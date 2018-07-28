package com.leqienglish.data.content;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.leqienglish.data.DataCacheAbstract;
import com.leqienglish.data.user.UserDataCache;
import com.leqienglish.database.ExecuteSQL;
import com.leqienglish.entity.SQLEntity;
import com.leqienglish.sf.LQService;
import com.leqienglish.util.LOGGER;
import com.leqienglish.util.LQHandler;
import com.leqienglish.view.recommend.RecommendArticle;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xyz.tobebetter.entity.english.Content;
import xyz.tobebetter.entity.user.User;

import static com.leqienglish.database.Constants.MY_RECOMMEND_TYPE;

public class RecommendContentDataCache extends DataCacheAbstract<List<Content>> {
    private LOGGER logger = new LOGGER(RecommendContentDataCache.class);
    private static RecommendContentDataCache recommendContentDataCache;



    private RecommendContentDataCache(){

    }


    public  static RecommendContentDataCache getInstance(){
        if(recommendContentDataCache !=null){
            return recommendContentDataCache;
        }
        synchronized (UserDataCache.class){
            if(recommendContentDataCache == null){
                recommendContentDataCache = new RecommendContentDataCache();
            }
        }

        return recommendContentDataCache;
    }
    @Override
    protected List<Content> getFromCache() {
        User user = UserDataCache.getInstance().getCacheData();
        if(user == null){
            return null;
        }
        List<Content> contents = ExecuteSQL.getDatasByType(MY_RECOMMEND_TYPE,user.getId(),Content.class);
        return contents;
    }

    @Override
    protected void putCache(List<Content> contents) {
        User user = UserDataCache.getInstance().getCacheData();
        ExecuteSQL.insertLearnE(contents,user.getId(),MY_RECOMMEND_TYPE);
    }

    @Override
    protected List<Content> getFromService() {

        User user = UserDataCache.getInstance().getCacheData();

        logger.d("queryAndUpdate data Content ");

        MultiValueMap<String,String> param = new LinkedMultiValueMap<>();
        param.add("userId", user.getId());

        try {
            Content[] contents = this.getRestClient().get("/recommend/recommendArticle",param,Content[].class);
            return Arrays.asList(contents);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    @Override
    public void add(List<Content> contents) {

    }

    @Override
    public void remove(List<Content> contents) {

    }
}
