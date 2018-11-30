package com.leqienglish.data.segment;


import com.leqienglish.data.DataCacheAbstract;
import com.leqienglish.database.ExecuteSQL;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;
import java.util.List;

import xyz.tobebetter.entity.word.Word;

public class WordsInSegmentDataCache  extends DataCacheAbstract<List<Word>> {
    private static final String WORDS_IN_SEGMENT = "WORDS_IN_SEGMENT";
    private String segmentId;
    public WordsInSegmentDataCache(String segmentId){
        this.segmentId = segmentId;
    }
    @Override
    protected String getUpdateTimeType() {
        return "WordsInSegmentDataCache";
    }

    @Override
    protected List<Word> getFromCache() {
        if(this.segmentId == null){
            return null;
        }
       List<Word> words =  ExecuteSQL.getDatasByType(WORDS_IN_SEGMENT,this.segmentId,Word.class);
        return words;
    }

    @Override
    protected void putCache(List<Word> words) {
        if(this.segmentId == null){
            return ;
        }
        this.clearData();
        ExecuteSQL.insertLearnE(words   ,this.segmentId,WORDS_IN_SEGMENT);
    }

    @Override
    protected List<Word> getFromService() {
        MultiValueMap<String,String> param = new LinkedMultiValueMap<>();
        param.add("segmentId",this.segmentId);
        try {
           Word[] words= this.getRestClient().get("/english/word/findBySegmentId",param,Word[].class);
           return Arrays.asList(words);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void add(List<Word> words) {

    }

    @Override
    public void clearData() {
        super.clearData();
        ExecuteSQL.delete(WORDS_IN_SEGMENT,this.segmentId);
    }
}
