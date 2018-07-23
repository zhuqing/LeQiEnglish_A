package com.leqienglish.data.content;

import com.leqienglish.data.DataCacheAbstract;
import com.leqienglish.data.user.UserDataCache;
import com.leqienglish.database.ExecuteSQL;
import com.leqienglish.util.LOGGER;


import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;
import java.util.List;

import xyz.tobebetter.entity.english.Content;
import xyz.tobebetter.entity.user.User;

import static com.leqienglish.database.Constants.MY_RECITING_ARITCLE_TYPE;
import static com.leqienglish.database.Constants.MY_RECOMMEND_TYPE;


/**
 * 正在背诵的文章的缓存
 */
public class MyRecitingContentDataCache extends DataCacheAbstract<List<Content>> {

    private LOGGER logger = new LOGGER(MyRecitingContentDataCache.class);

    private static MyRecitingContentDataCache myRecitingContentDataCache;



    private MyRecitingContentDataCache(){

    }


    public  static MyRecitingContentDataCache getInstance(){
        if(myRecitingContentDataCache !=null){
            return myRecitingContentDataCache;
        }
        synchronized (MyRecitingContentDataCache.class){
            if(myRecitingContentDataCache == null){
                myRecitingContentDataCache = new MyRecitingContentDataCache();
            }
        }

        return myRecitingContentDataCache;
    }

    @Override
    protected List<Content> getFromCache() {
        User user = UserDataCache.getInstance().getCacheData();
        if(user == null){
            return null;
        }
        List<Content> contents = ExecuteSQL.getDatasByType(MY_RECITING_ARITCLE_TYPE,user.getId(),Content.class);
        return contents;
    }

    @Override
    protected void putCache(List<Content> constantsList) {
        User user = UserDataCache.getInstance().getCacheData();
        ExecuteSQL.insertLearnE(constantsList,user.getId(),MY_RECITING_ARITCLE_TYPE);
    }

    @Override
    protected List<Content> getFromService() {
        User user = UserDataCache.getInstance().getCacheData();

        MultiValueMap<String,String> param = new LinkedMultiValueMap<>();
        param.add("userId", user.getId());

        try {
            Content[] contents = this.getRestClient().get("/userAndContent/findByUserId",param,Content[].class);
            return Arrays.asList(contents);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
}
