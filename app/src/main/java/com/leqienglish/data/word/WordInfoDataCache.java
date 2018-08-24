package com.leqienglish.data.word;

import com.leqienglish.data.DataCacheAbstract;
import com.leqienglish.database.ExecuteSQL;
import com.leqienglish.util.task.LQExecutors;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import xyz.tobebetter.entity.english.segment.WordAndSegment;
import xyz.tobebetter.entity.word.Word;

import static com.leqienglish.database.Constants.WORD_TYPE;

public class WordInfoDataCache extends DataCacheAbstract<Word> {
    private  static WordInfoDataCache wordInfoDataCache;
    private String word;


    private  WordInfoDataCache(){

    }


    public static WordInfoDataCache getInstance(String word){
        if(wordInfoDataCache == null){
            wordInfoDataCache = new WordInfoDataCache();
        }

        wordInfoDataCache.word = word;
        return wordInfoDataCache;
    }
    @Override
    protected Word getFromCache() {
        if(word == null){
            return null;
        }

        List<Word> wordList = ExecuteSQL.getDatasByType(WORD_TYPE,word,Word.class);
        if(wordList == null || wordList.isEmpty()){
            return null;
        }
        return wordList.get(0);
    }

    @Override
    protected void putCache(Word word) {
        if(word == null){
            return;
        }
        ExecuteSQL.insertLearnE(Arrays.asList(word),word.getWord(),WORD_TYPE);

    }

    @Override
    protected Word getFromService() {

        if(word == null){
            return null;
        }
        MultiValueMap<String,String> param = new LinkedMultiValueMap<>();
        param.add("word", word);

        try {
            Word words =  this.getRestClient().get("/english/word/findByWord",param,Word.class);
            if(words != null ){
                return words;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void add(Word word) {

    }

    @Override
    public void remove(Word word) {

    }
}
