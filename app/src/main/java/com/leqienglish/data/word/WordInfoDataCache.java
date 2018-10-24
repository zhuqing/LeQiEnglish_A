package com.leqienglish.data.word;

import com.leqienglish.data.DataCacheAbstract;
import com.leqienglish.data.user.UserDataCache;
import com.leqienglish.database.ExecuteSQL;
import com.leqienglish.sf.LoadFile;
import com.leqienglish.util.LOGGER;
import com.leqienglish.util.WordUtil;
import com.leqienglish.util.file.AndroidFileUtil;
import com.leqienglish.util.file.FileUtil;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import xyz.tobebetter.entity.user.User;
import xyz.tobebetter.entity.user.word.UserAndWord;
import xyz.tobebetter.entity.word.Word;

import static com.leqienglish.database.Constants.WORD_TYPE;

/**
 * 单词信息的缓存
 */
public class WordInfoDataCache extends DataCacheAbstract<Word> {
    private LOGGER logger = new LOGGER(WordInfoDataCache.class);
    private static WordInfoDataCache wordInfoDataCache;
    private String word;


    private WordInfoDataCache() {

    }


    public static WordInfoDataCache getInstance(String word) {
        if (wordInfoDataCache == null) {
            wordInfoDataCache = new WordInfoDataCache();
        }

        wordInfoDataCache.word = word;
        wordInfoDataCache.setCacheData(null);
        return wordInfoDataCache;
    }

    @Override
    protected String getUpdateTimeType() {
        return "WordInfoDataCache_update";
    }

    @Override
    protected Word getFromCache() {
        if (word == null) {
            return null;
        }

        List<Word> wordList = ExecuteSQL.getDatasByType(WORD_TYPE, word, Word.class);
        if (wordList == null || wordList.isEmpty()) {
            return null;
        }
        return wordList.get(0);
    }

    @Override
    protected void putCache(Word word) {
        if (word == null) {
            return;
        }
        ExecuteSQL.insertLearnE(Arrays.asList(word), word.getWord(), WORD_TYPE);

    }

    @Override
    protected Word getFromService() {

        if (word == null) {
            return null;
        }
        MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
        param.add("word", word);

        try {
            Word words = this.getRestClient().get("/english/word/findByWord", param, Word.class);
            if (words != null) {
                checkAndSave(words);
                return words;
            }

            return loadWord(word);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private void checkAndSave(Word word) throws Exception {
        if(!MyWordDataCache.getInstance().hasWord(word.getId())){
            return;
        }
        MyWordDataCache.getInstance().add(Arrays.asList(word));
        this.saveUserAndWord(word);
    }

    private Word loadWord(String word) throws ExecutionException, InterruptedException {
        word = word.toLowerCase();
        String json = WordUtil.transResult(word);
        logger.d("logging word: "+json);
        if (json == null || json.isEmpty()) {
            return null;
        }
        Word newWord = Word.icibaJsontoWord(json);

        try {
            changePath(newWord);
            newWord = this.saveWord(newWord);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return newWord;
    }

    private Word saveWord(Word word) throws Exception {

       Word savedWord = this.getRestClient().post("/english/word/create",word,null,Word.class);
        MyWordDataCache.getInstance().add(Arrays.asList(savedWord));
        saveUserAndWord(savedWord);
       return savedWord;
    }

    private void saveUserAndWord(Word word) throws Exception {

        if(word==null || word.getId() == null){
            return;
        }

        User user = UserDataCache.getInstance().getCacheData();
        if(user == null){
            return;
        }

        UserAndWord userAndWord = new UserAndWord();
        userAndWord.setUserId(user.getId());
        userAndWord.setWordId(word.getId());
        userAndWord.setReciteCount(0);
        userAndWord.setType(0);

        this.getRestClient().post("/userAndWord/create",userAndWord,null,UserAndWord.class);
    }

    private void changePath(Word word) throws Exception {
        if (word == null) {
            return;
        }

        String path = downAndUpload(word.getAmAudionPath(), word.getWord(), FileUtil.AM_WORD_TYPE);
        word.setAmAudionPath(path);

        path = downAndUpload(word.getEnAudioPath(), word.getWord(), FileUtil.EN_WORD_TYPE);
        word.setEnAudioPath(path);

        path = downAndUpload(word.getTtsAudioPath(), word.getWord(), FileUtil.TTS_WORD_TYPE);
        word.setTtsAudioPath(path);

    }

    private String downAndUpload(String urlPath, String word, String type) throws IOException, Exception {
        if (urlPath == null || urlPath.isEmpty()) {
            return null;
        }
        String localPath = AndroidFileUtil.getInstence().wordFilelPath(word, type);
        if (!AndroidFileUtil.getInstence().fileExit(localPath)) {
            LoadFile.downLoad(urlPath, localPath, MediaType.ALL);

        }

        File file = new File(localPath);

        MultiValueMap<String, Object> audioMap = new LinkedMultiValueMap();
        audioMap.add("file", new FileSystemResource(file));

        MultiValueMap<String, String> param = new LinkedMultiValueMap();
        param.add("word", word);
        param.add("type", type);


        return this.getRestClient().upload("/file/uploadWordAudio", audioMap, param, String.class);

    }

    @Override
    public void add(Word word) {

    }

    @Override
    public void clearData() {
        ExecuteSQL.delete(WORD_TYPE,this.word);
    }


}
