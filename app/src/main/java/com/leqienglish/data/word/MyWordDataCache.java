package com.leqienglish.data.word;

import com.leqienglish.data.DataCacheAbstract;
import com.leqienglish.data.user.UserDataCache;
import com.leqienglish.database.ExecuteSQL;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;
import java.util.List;

import xyz.tobebetter.entity.word.Word;

import static com.leqienglish.database.Constants.MY_WORDS_TYPE;

/**
 * 我的单词的缓存
 */
public class MyWordDataCache extends DataCacheAbstract<List<Word>> {


    private static MyWordDataCache myWordDataCache;

    private MyWordDataCache() {

    }


    public static MyWordDataCache getInstance() {
        if (myWordDataCache != null) {
            return myWordDataCache;
        }
        synchronized (MyWordDataCache.class) {
            if (myWordDataCache == null) {
                myWordDataCache = new MyWordDataCache();
            }
        }

        return myWordDataCache;
    }

    @Override
    protected List<Word> getFromCache() {
        if(UserDataCache.getInstance().getCacheData() == null){
            return null;
        }

        List<Word> wordList =  ExecuteSQL.getDatasByType(MY_WORDS_TYPE,UserDataCache.getInstance().getCacheData().getId(),Word.class);

        return wordList;
    }

    @Override
    protected void putCache(List<Word> words) {
        if(UserDataCache.getInstance().getCacheData() == null){
            return ;
        }

        ExecuteSQL.insertLearnE(words,UserDataCache.getInstance().getCacheData().getId(),MY_WORDS_TYPE);
    }

    @Override
    protected List<Word> getFromService() {
        if(UserDataCache.getInstance().getCacheData()== null){
            return null;
        }
        MultiValueMap<String,String> param = new LinkedMultiValueMap<>();
        param.add("contentId", UserDataCache.getInstance().getCacheData().getId());
        try {
            Word[] segments = this.getRestClient().get("/english/word/findAll",param,Word[].class);
            if(segments == null){
                return null;
            }

            return Arrays.asList(segments);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void add(List<Word> words) {

    }

    @Override
    public void remove(List<Word> words) {

    }
}
