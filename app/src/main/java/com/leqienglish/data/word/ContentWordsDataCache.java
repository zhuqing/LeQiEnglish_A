package com.leqienglish.data.word;

import com.leqienglish.data.DataCacheAbstract;
import com.leqienglish.database.ExecuteSQL;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;
import java.util.List;

import xyz.tobebetter.entity.english.Content;
import xyz.tobebetter.entity.word.Word;

import static com.leqienglish.database.Constants.CONTENT_WORD_TYPE;

public class ContentWordsDataCache extends DataCacheAbstract<List<Word>>  {

    public Content content;

    public ContentWordsDataCache(Content content){
        this.content = content;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }




    @Override
    protected List<Word> getFromCache() {
        if(this.getContent() == null){
            return null;
        }

        List<Word> wordList = ExecuteSQL.getDatasByType(CONTENT_WORD_TYPE,this.getContent().getId(),Word.class);
        return wordList;
    }

    @Override
    protected void putCache(List<Word> words) {
        if(this.getContent() == null){
            return ;
        }
        ExecuteSQL.insertLearnE(words,this.getContent().getId(),CONTENT_WORD_TYPE);

    }

    @Override
    protected List<Word> getFromService() {
        if(this.getContent() == null){
            return null;
        }

        MultiValueMap<String,String> param = new LinkedMultiValueMap<>();
        param.add("contentId", content.getId());


        try {
          Word[] words =  this.getRestClient().get("/english/word/findByContentId",param,Word[].class);
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
    public void remove(List<Word> words) {

    }
}
