package com.leqienglish.sf.task;

import com.leqienglish.util.FileUtil;
import com.leqienglish.util.LQHandler;
import com.leqienglish.util.tran.iciba.ICIBATranslateUtil;

import org.springframework.core.io.FileSystemResource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.function.Consumer;

import xyz.tobebetter.entity.word.Word;


public class HttpGetTranslateTask<T extends Word> extends HttpTask<T> {

    private String word;

    public HttpGetTranslateTask(String word, LQHandler.Consumer<T> consumer, Map<String, String> variables) {
        super("", null, consumer, variables);
        this.word = word;
    }

    @Override
    protected T getT() {
        MultiValueMap<String, String> parameter = new LinkedMultiValueMap<>();
        parameter.add("word", word.toLowerCase());
        Word hasWord = null;
        try {
            hasWord = this.restClient.get(host + "/english/word/findByWord", parameter, Word.class);
        } catch (Exception e) {
            e.printStackTrace();

        }
        if (hasWord != null) {
            return (T) hasWord;
        }

        final  StringBuilder newWord = new StringBuilder();

        try {
            ICIBATranslateUtil.transResult(word, new Consumer<String>() {
                @Override
                public void accept(String s) {
                    newWord.append(s);
                }
            });


                Word innword = Word.icibaJsontoWord(newWord.toString());
                changePath(innword);
                innword = restClient.post(host + "/english/word/create", innword, null, Word.class);
                return (T) innword;

        } catch (Exception e) {
            e.printStackTrace();

        }

        return null;
    }




    private void changePath(Word word) throws Exception {

        String path = downAndUpload(word.getAmAudionPath(), word.getWord());
        word.setAmAudionPath(path);

        path = downAndUpload(word.getEnAudioPath(), word.getWord());
        word.setEnAudioPath(path);

        path = downAndUpload(word.getTtsAudioPath(), word.getWord());
        word.setTtsAudioPath(path);

    }

    private String downAndUpload(String urlPath, String word) throws IOException, Exception {
        String localPath = FileUtil.wordFilelPath(word);
        this.restClient.downLoad(urlPath, localPath, null);

        MultiValueMap<String, Object> audioMap = new LinkedMultiValueMap();
        audioMap.add("file", new FileSystemResource(new File(localPath)));

        MultiValueMap<String, String> param = new LinkedMultiValueMap();
        param.add("word", word);

        return this.restClient.upload(host + "/file/uploadWordAudio", audioMap, param, String.class);

    }


}