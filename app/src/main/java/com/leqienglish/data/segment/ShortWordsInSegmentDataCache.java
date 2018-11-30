package com.leqienglish.data.segment;

import com.leqienglish.data.DataCacheAbstract;
import com.leqienglish.database.ExecuteSQL;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;
import java.util.List;

import xyz.tobebetter.entity.english.shortword.ShortWord;

/**
 * 端下
 */
public class ShortWordsInSegmentDataCache extends DataCacheAbstract<List<ShortWord>> {

    private static final String DATA_TYPE = "ShortWordsInSegmentDataCache";

    private String segmentId;
    public ShortWordsInSegmentDataCache(String segmentId){
        this.segmentId = segmentId;
    }
    @Override
    protected String getUpdateTimeType() {
        return "ShortWordsInSegmentDataCache_DATA";
    }

    @Override
    protected List<ShortWord> getFromCache() {
        if(this.segmentId == null){
            return null;
        }
        List<ShortWord> words =  ExecuteSQL.getDatasByType(DATA_TYPE,this.segmentId,ShortWord.class);
        return words;
    }

    @Override
    protected void putCache(List<ShortWord> shortWords) {
        if(this.segmentId == null){
            return ;
        }
        this.clearData();
        ExecuteSQL.insertLearnE(shortWords   ,this.segmentId,DATA_TYPE);
    }

    @Override
    protected List<ShortWord> getFromService() {
        MultiValueMap<String,String> param = new LinkedMultiValueMap<>();
        param.add("segmentId",this.segmentId);
        try {
            ShortWord[] words= this.getRestClient().get("/shortWord/findBySegmentId",param,ShortWord[].class);
            return Arrays.asList(words);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void add(List<ShortWord> shortWords) {

    }

    public void clearData(){
        super.clearData();
        ExecuteSQL.delete(DATA_TYPE,this.segmentId);
    }
}
