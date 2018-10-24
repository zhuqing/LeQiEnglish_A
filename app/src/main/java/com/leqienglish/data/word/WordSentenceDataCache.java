package com.leqienglish.data.word;

import com.leqienglish.controller.word.WordInfoController;
import com.leqienglish.data.DataCacheAbstract;
import com.leqienglish.database.ExecuteSQL;
import com.leqienglish.util.LOGGER;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;
import java.util.List;

import xyz.tobebetter.entity.english.segment.WordAndSegment;
import xyz.tobebetter.entity.word.Word;

import static com.leqienglish.database.Constants.WORD_SEGMENT_TYPE;

public class WordSentenceDataCache extends DataCacheAbstract<List<WordAndSegment>> {
    private LOGGER logger = new LOGGER(WordInfoController.class);
    private Word word;

    public WordSentenceDataCache(Word word){
        this.word = word;
    }

    @Override
    protected String getUpdateTimeType() {
        return "WordSentenceDataCache_data";
    }

    @Override
    protected List<WordAndSegment> getFromCache() {
        if(this.getWord() == null){
            return null;
        }
      logger.d("getFromCache,wordId="+this.getWord().getId());
        List<WordAndSegment> wordList = ExecuteSQL.getDatasByType(WORD_SEGMENT_TYPE,this.getWord().getId(),WordAndSegment.class);
        if(wordList == null || wordList.isEmpty()){
            return null;
        }
        logger.d("getFromCache,size="+wordList.size());
        return wordList;
    }

    @Override
    protected void putCache(List<WordAndSegment> wordAndSegments) {
        if(this.getWord() == null){
            return ;
        }

        logger.d("putCache,wordId="+this.getWord().getId());
        ExecuteSQL.insertLearnE(wordAndSegments,this.getWord().getId(),WORD_SEGMENT_TYPE);

    }

    @Override
    protected List<WordAndSegment> getFromService() {
        if(this.getWord() == null){
            return null;
        }

        MultiValueMap<String,String> param = new LinkedMultiValueMap<>();
        param.add("wordId", word.getId());

        logger.d("getFromService,wordId="+this.getWord().getId());
        try {
            WordAndSegment[] words =  this.getRestClient().get("/english/wordandsegment/findByWordId",param,WordAndSegment[].class);
            return Arrays.asList(words);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    @Override
    public void add(List<WordAndSegment> wordAndSegments) {

    }

    @Override
    public void clearData() {
        ExecuteSQL.delete(WORD_SEGMENT_TYPE,this.getWord().getId());
    }



    public Word getWord() {
        return word;
    }

    public void setWord(Word word) {
        this.word = word;
    }
}
