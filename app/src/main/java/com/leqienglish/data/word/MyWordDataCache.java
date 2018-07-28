package com.leqienglish.data.word;

import com.leqienglish.data.DataCacheAbstract;

import java.util.List;

import xyz.tobebetter.entity.word.Word;

/**
 * 我的单词的缓存
 */
public class MyWordDataCache extends DataCacheAbstract<List<Word>> {
    @Override
    protected List<Word> getFromCache() {
        return null;
    }

    @Override
    protected void putCache(List<Word> words) {

    }

    @Override
    protected List<Word> getFromService() {
        return null;
    }

    @Override
    public void add(List<Word> words) {

    }

    @Override
    public void remove(List<Word> words) {

    }
}
