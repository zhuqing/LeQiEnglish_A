package com.leqienglish.controller.word;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.leqienglish.MainActivity;
import com.leqienglish.R;
import com.leqienglish.activity.content.ShowAllContentActiviey;
import com.leqienglish.activity.word.ReciteWordsReviewActivity;
import com.leqienglish.activity.word.WordInfoActivity;
import com.leqienglish.controller.ControllerAbstract;
import com.leqienglish.data.word.MyWordDataCache;
import com.leqienglish.data.word.RecitingWordDataCache;
import com.leqienglish.playandrecord.LQMediaPlayer;
import com.leqienglish.playandrecord.PlayAudio;
import com.leqienglish.sf.LoadFile;
import com.leqienglish.util.BundleUtil;
import com.leqienglish.util.FileUtil;
import com.leqienglish.util.LOGGER;
import com.leqienglish.util.LQHandler;
import com.leqienglish.util.string.StringUtil;
import com.leqienglish.view.word.RecitingWordListView;
import com.leqienglish.view.word.WordInfoView;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import xyz.tobebetter.entity.word.Word;

public class ReciteWordsController extends ControllerAbstract {
    private static LOGGER logger = new LOGGER(ReciteWordsController.class);
    private Button startButton;

    private Button cancelButton;

    private Button pauseButton;

    private WordInfoView wordInfoView;

    private TextView tipTextView;

    private final static int PLAY_TIMES = 3;
    // private ViewPager viewPager;
    private int currentPage = 1;

    private int totalPage = 1;

    private List<Word> wordList;

    private LQMediaPlayer lqMediaPlayer;

    private FrameLayout cancelAndPause;

    private FrameLayout  wordInfoFrame;


    private RecitingWordListView recitingWordListView;

    private boolean playing = false;



    public ReciteWordsController(View view) {
        super(view);
    }

    @Override
    public void init() {

       // this.preButton = this.getView().findViewById(R.id.recite_words_pre_page);
        //this.nextButton = this.getView().findViewById(R.id.recite_words_next_page);
        this.wordInfoView = this.getView().findViewById(R.id.recite_words_wordinfo);
        this.tipTextView = this.getView().findViewById(R.id.recite_words_tip);
        this.startButton = this.getView().findViewById(R.id.recite_words_start);
        this.cancelButton = this.getView().findViewById(R.id.recite_words_cancel);
        this.pauseButton = this.getView().findViewById(R.id.recite_words_pause);
        this.cancelAndPause = this.getView().findViewById(R.id.recite_words_cancel_pause);

        this.wordInfoFrame = this.getView().findViewById(R.id.recite_words_wordinfo_framelayout);
        lqMediaPlayer = new LQMediaPlayer();

        // this.viewPager = this.getView().findViewById(R.id.recite_words_viewpage);
        this.initListener();
        reload();
    }

    private void initListener() {

        wordInfoFrame.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    tipTextView.setVisibility(View.GONE);
                }
                Observable.just(tipTextView).delay(3,TimeUnit.SECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<TextView>() {
                            @Override
                            public void accept(TextView textView) throws Exception {
                                tipTextView.setVisibility(View.VISIBLE);
                            }
                        });

                return false;
            }
        });

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

    private void loadAudioFile() {
        AsyncTask asyncTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                for (Word word : wordList) {
                    loadWordAudio(word,null);
                }
                return null;
            }
        };
        asyncTask.execute();
    }

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

        RecitingWordDataCache.getInstance().load(new LQHandler.Consumer<List<Word>>() {
            @Override
            public void accept(List<Word> words) {
                if (words == null) {

                    return;
                }
                wordList = words.subList(0,4);
                totalPage = wordList.size();

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

    private void showWords(){
//        this.recitingWordListView = new RecitingWordListView(this.getView().getContext(),null);
//
//        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
//                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
//
//        recitingWordListView.setLayoutParams(params);
//        this.recitingWordListView.load(this.wordList);
//        this.wordInfoFrame.addView(recitingWordListView);

        playing = false;
        Intent intent = new Intent();

        intent.setClass(getView().getContext(), ReciteWordsReviewActivity.class);

        getView().getContext().startActivity(intent);

    }

    public void play(int page)  {
        if(wordList == null){
            return;
        }
        if (page > wordList.size()) {
            showWords();
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
                Observable.just(currentPage).delay(3L, TimeUnit.SECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Integer>() {
                            @Override
                            public void accept(Integer integer) throws Exception {
                                play(currentPage++);
                            }
                        });



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
}
