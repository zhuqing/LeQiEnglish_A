package com.leqienglish.data.word;

import com.leqienglish.data.DataPageCacheAbstract;
import com.leqienglish.data.user.UserDataCache;
import com.leqienglish.database.ExecuteSQL;
import com.leqienglish.util.AppType;

import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import xyz.tobebetter.entity.word.Word;

import static com.leqienglish.database.Constants.MY_WORDS_TYPE;

/**
 * 我的单词的缓存
 */
public class MyWordDataCache extends DataPageCacheAbstract<List<Word>> {


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
    protected String getUpdateTimeType() {
        return "MyWordDataCache_update";
    }

    @Override
    protected List<Word> getFromCache() {
        if (UserDataCache.getInstance().getCacheData() == null) {
            return null;
        }

        List<Word> wordList = ExecuteSQL.getDatasByType(MY_WORDS_TYPE, UserDataCache.getInstance().getCacheData().getId(), Word.class);

        return wordList;
    }

    @Override
    protected void putCache(List<Word> words) {
        if (UserDataCache.getInstance().getCacheData() == null) {
            return;
        }

        ExecuteSQL.insertLearnE(words, UserDataCache.getInstance().getCacheData().getId(), MY_WORDS_TYPE);
    }


    protected boolean needUpdate() {
        return false;
    }

    @Override
    protected List<Word> getFromService() {
        if (UserDataCache.getInstance().getCacheData() == null) {
            return null;
        }
        MultiValueMap<String, String> param = this.getMutilValueMap();
        param.add("userId", UserDataCache.getInstance().getCacheData().getId());
        param.add(PAGE, "1");
        param.add(PAGE_SIZE, AppType.PAGE_SIZE+"");
        try {
            Word[] segments = this.getRestClient().get("/english/word/findByUserId", param, Word[].class);
            if (segments == null) {
                return null;
            }

            List<Word> wordList = new ArrayList<>(Arrays.asList(segments));

            return wordList;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void add(List<Word> words) {

        if (words == null || words.isEmpty()) {
            return;
        }
        this.putCache(words);

        if (this.getCacheData() == null ) {
            this.setCacheData(new ArrayList<>());
        }
        this.getCacheData().addAll(words);

    }

    @Override
    public void clearData() {
        super.clearData();
        ExecuteSQL.delete(MY_WORDS_TYPE, UserDataCache.getInstance().getCacheData().getId());
    }


    public boolean hasWord(String wordId) {

        if (this.getCacheData() == null || this.getCacheData().isEmpty()) {
            return false;
        }

        for (Word word : this.getCacheData()) {
            if (wordId.equals(word.getId())) {
                return true;
            }
        }
        return false;

    }


    @Override
    protected List<Word> getMoreFromService() {

        if (UserDataCache.getInstance().getCacheData() == null) {
            return null;
        }
        MultiValueMap<String, String> param = this.getMutilValueMap();
        param.add("userId", UserDataCache.getInstance().getCacheData().getId());
        try {
            Word[] segments = this.getRestClient().get("/english/word/findByUserId", param, Word[].class);
            if (segments == null) {
                return null;
            }

            List<Word> wordList = new ArrayList<>(Arrays.asList(segments));

            return wordList;
        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;
    }
}
