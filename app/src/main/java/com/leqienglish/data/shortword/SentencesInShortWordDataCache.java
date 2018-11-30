package com.leqienglish.data.shortword;

import com.leqienglish.data.DataCacheAbstract;
import com.leqienglish.database.ExecuteSQL;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;
import java.util.List;

import xyz.tobebetter.entity.english.sentence.Sentence;

/**
 * 短语下
 */
public class SentencesInShortWordDataCache extends DataCacheAbstract<List<Sentence>> {
    private String shortWordId;
    private static final String DATA_TYPE = "SentencesInShortWordDataCache";

    public SentencesInShortWordDataCache(String shortWordId){
        this.shortWordId = shortWordId;
    }

    @Override
    protected String getUpdateTimeType() {
        return "SentencesInShortWordDataCache_updatedata";
    }

    @Override
    protected List<Sentence> getFromCache() {
        if(this.shortWordId == null){
            return null;
        }
        List<Sentence> words =  ExecuteSQL.getDatasByType(DATA_TYPE,this.shortWordId,Sentence.class);
        return words;
    }

    @Override
    protected void putCache(List<Sentence> shortWords) {
        if(this.shortWordId == null){
            return ;
        }
        this.clearData();
        ExecuteSQL.insertLearnE(shortWords   ,this.shortWordId,DATA_TYPE);
    }

    @Override
    protected List<Sentence> getFromService() {
        MultiValueMap<String,String> param = new LinkedMultiValueMap<>();
        param.add("shortWordId",this.shortWordId);
        try {
            Sentence[] words= this.getRestClient().get("/sentence/findByShortWordId",param,Sentence[].class);
            return Arrays.asList(words);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void add(List<Sentence> shortWords) {

    }

    public void clearData(){
        super.clearData();
        ExecuteSQL.delete(DATA_TYPE,this.shortWordId);
    }
}
