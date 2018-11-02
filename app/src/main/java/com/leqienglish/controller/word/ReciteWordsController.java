package com.leqienglish.controller.word;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.leqienglish.MainActivity;
import com.leqienglish.R;
import com.leqienglish.activity.word.ReciteWordsActivity;
import com.leqienglish.controller.ControllerAbstract;
import com.leqienglish.data.word.RecitingWordDataCache;
import com.leqienglish.playandrecord.LQMediaPlayer;
import com.leqienglish.pop.actionsheet.ActionSheet;
import com.leqienglish.sf.LoadFile;
import com.leqienglish.util.BundleUtil;
import com.leqienglish.util.FileUtil;
import com.leqienglish.util.LOGGER;
import com.leqienglish.util.LQHandler;
import com.leqienglish.util.TaskUtil;
import com.leqienglish.util.string.StringUtil;
import com.leqienglish.view.word.RecitingWordListView;
import com.leqienglish.view.word.WordInfoView;

import java.io.File;
import java.io.IOException;
import java.util.List;

import xyz.tobebetter.entity.word.Word;

public class ReciteWordsController extends ControllerAbstract {
    private static LOGGER logger = new LOGGER(ReciteWordsController.class);
    private Button startButton;

    private Button cancelButton;

    private Button pauseButton;

    private WordInfoView wordInfoView;

    protected TextView tipTextView;

    /**
     * 每个单词的播放次数
     */
    private final static int PLAY_TIMES = 3;
    // private ViewPager viewPager;
    private int currentPage = 1;


    private List<Word> wordList;

    private LQMediaPlayer lqMediaPlayer;

    private FrameLayout cancelAndPause;

    protected FrameLayout  wordInfoFrame;


    private RecitingWordListView recitingWordListView;

    private boolean playing = false;





    public ReciteWordsController(View view) {
        super(view);
    }

    @Override
    public void init() {

        this.wordInfoView = this.getView().findViewById(R.id.recite_words_wordinfo);
        this.tipTextView = this.getView().findViewById(R.id.recite_words_tip);
        this.startButton = this.getView().findViewById(R.id.recite_words_start);
        this.cancelButton = this.getView().findViewById(R.id.recite_words_cancel);
        this.pauseButton = this.getView().findViewById(R.id.recite_words_pause);
        this.cancelAndPause = this.getView().findViewById(R.id.recite_words_cancel_pause);

        this.wordInfoFrame = this.getView().findViewById(R.id.recite_words_wordinfo_framelayout);
        lqMediaPlayer = new LQMediaPlayer();


        this.initListener();
        reload();
    }

    protected void initListener() {

        tipTextView.setVisibility(View.GONE);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playing = true;
                cancelAndPause.setVisibility(View.VISIBLE);
                startButton.setVisibility(View.GONE);
                play(currentPage);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();

                intent.setClass(getView().getContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                getView().getContext().startActivity(intent);

                playing = false;
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playing = false;
                cancelAndPause.setVisibility(View.GONE);
                startButton.setVisibility(View.VISIBLE);
                lqMediaPlayer.pause();
                currentPage--;
            }
        });

    }


    /**
     * 异步加载音频文件
     */
    private void loadAudioFile() {
        TaskUtil.run(()->{
            for (Word word : wordList) {
                loadWordAudio(word,null);
            }
        });

    }

    /**
     * 重新设置单词的界面的显示
     * @param page
     */
    private void reSetWord(int page) {

        if (wordList == null) {
            return;
        }

        if (page > wordList.size()) {
            return;
        }
        Word word = wordList.get(page - 1);
        wordInfoView.load(word);
    }

    @Override
    public void reload() {
        //如果有数据就不加载
        if(wordList != null && !wordList.isEmpty()){
            loadAudioFile();
            if(playing){
                play(currentPage);
            }
            return;
        }

        //情况掉缓存中的数据
        RecitingWordDataCache.getInstance().setCacheData(null);
        RecitingWordDataCache.getInstance().load(new LQHandler.Consumer<List<Word>>() {
            @Override
            public void accept(List<Word> words) {
                if (words == null) {

                    return;
                }

                wordList = words;
                loadAudioFile();
                if(playing){
                    play(currentPage);
                }

            }
        });
    }



    @Override
    public void destory() {
        playing = false;
        if(this.lqMediaPlayer!=null){
            this.lqMediaPlayer.pause();
        }
    }

    /**
     * 背诵完成
     */
    protected void reciteFilished(){


        playing = false;

        new ActionSheet.DialogBuilder(this.getView().getContext())
                .addButton("再背诵一次",(v)->{
                   currentPage = 1;
                   play(currentPage);

                })
        .addButton("开始默写",(v)->{
            Intent intent = new Intent();
            intent.putExtras(BundleUtil.create(BundleUtil.DATA,false));
            intent.putExtras(BundleUtil.create(BundleUtil.DATA_BL,true));
            intent.setClass(getView().getContext(), ReciteWordsActivity.class);
            getView().getContext().startActivity(intent);
        })
        .addCloseButton("返回主页",(v)->{
            Intent intent = new Intent();
            intent.setClass(getView().getContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            getView().getContext().startActivity(intent);
        }).create();



    }

    public void play(int page)  {
        if(wordList == null){
            return;
        }
        if (page > wordList.size()) {
            reciteFilished();
            return;
        }

        Word word = this.wordList.get(page - 1);

        this.reSetWord(page);
        String audiopath = word.getAmAudionPath();

        if (StringUtil.isNullOrEmpty(audiopath)) {
            audiopath = word.getTtsAudioPath();
        }

        if(audiopath == null){
            play(currentPage++);
            return;
        }

        if(FileUtil.exists(audiopath)){
            try {
                playAudio(FileUtil.getFileAbsolutePath(audiopath));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            loadWordAudio(word, new LQHandler.Consumer<String>() {
                @Override
                public void accept(String s) {
                    File file = new File(s);
                    if(!file.exists()||file.length()==0){
                        play(currentPage++);
                    }else {
                        playAudio(s);
                    }
                }
            });
        }
    }



    private void playAudio(String audioFile) {


        this.lqMediaPlayer.play(audioFile, PLAY_TIMES,new LQHandler.Consumer() {
            @Override
            public void accept(Object o) {

                //三秒后播放下一个

                TaskUtil.runlater((t)->{
                    play(++currentPage);
                },3000L);

            }
        });

    }

    /**
     * 加载音频文件
     *
     * @param word
     */
    private void loadWordAudio(Word word, LQHandler.Consumer<String> complete) {
        String audiopath = word.getAmAudionPath();
        if (StringUtil.isNullOrEmpty(audiopath)) {
            audiopath = word.getTtsAudioPath();
        }

        if(audiopath == null){
            return;
        }


        if (!FileUtil.exists(audiopath)) {
            LoadFile.loadFile(audiopath, complete);
            return;
        }

        if(complete!=null){
            try {
                complete.accept(FileUtil.getFileAbsolutePath(audiopath));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



    }

    public List<Word> getWordList() {
        return wordList;
    }

    public void setWordList(List<Word> wordList) {
        this.wordList = wordList;
    }
}
