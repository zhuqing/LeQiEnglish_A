package com.leqienglish.data.word;

import com.leqienglish.data.DataPageCacheAbstract;
import com.leqienglish.data.user.UserDataCache;
import com.leqienglish.database.ExecuteSQL;
import com.leqienglish.util.AppType;
import com.leqienglish.util.network.NetWorkUtil;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;
import java.util.List;

import xyz.tobebetter.entity.word.Word;

import static com.leqienglish.database.Constants.WORD_TYPE;

/**
 * 我的单词的缓存
 */
public class RecitingWordDataCache extends DataPageCacheAbstract<List<Word>> {


    private static RecitingWordDataCache recitingWordDataCache;



    private RecitingWordDataCache() {

    }


    public static RecitingWordDataCache getInstance() {
        if (recitingWordDataCache != null) {
            return recitingWordDataCache;
        }
        synchronized (RecitingWordDataCache.class) {
            if (recitingWordDataCache == null) {
                recitingWordDataCache = new RecitingWordDataCache();
            }
        }

        return recitingWordDataCache;
    }

    @Override
    protected String getUpdateTimeType() {
        return "RecitingWordDataCache_update";
    }

    @Override
    protected List<Word> getFromCache() {
        //没有联网，才从缓存中加载单词
       if(NetWorkUtil.isConnect(AppType.mainContext)){
           return null;
       }

        int count = MyReciteWordConfigDataCache.getInstance().getReciteNumberPerDay();
        List<Word> wordList =  ExecuteSQL.getDatasByType(WORD_TYPE,null,Word.class,this.getPage(),count);

        return  wordList;
    }

    @Override
    protected void putCache(List<Word> words) {
        if(UserDataCache.getInstance().getCacheData() == null){
            return ;
        }
        ExecuteSQL.insertLearnE(words,null ,WORD_TYPE);

    }

    @Override
    protected List<Word> getFromService() {

        MultiValueMap<String,String> param = new LinkedMultiValueMap<>();
        param.add("userId", UserDataCache.getInstance().getUserId());
        param.add("number", MyReciteWordConfigDataCache.getInstance().getReciteNumberPerDay().toString());
        try {
            Word[] segments = this.getRestClient().get("/english/word/findMyReciteByUserIdAndNumber",param,Word[].class);
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
    public void clearData() {

    }


    @Override
    protected List<Word> getMoreFromService() {

        return null;
    }
}
