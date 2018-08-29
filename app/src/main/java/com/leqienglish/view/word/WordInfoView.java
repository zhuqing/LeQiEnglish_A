package com.leqienglish.view.word;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.leqienglish.R;
import com.leqienglish.playandrecord.LQMediaPlayer;
import com.leqienglish.playandrecord.PlayAudio;
import com.leqienglish.sf.LoadFile;
import com.leqienglish.util.LOGGER;
import com.leqienglish.util.LQHandler;
import com.leqienglish.util.string.StringUtil;

import java.util.List;

import xyz.tobebetter.entity.word.Word;
import xyz.tobebetter.entity.word.WordMean;

public class WordInfoView extends RelativeLayout {
    private LOGGER logger = new LOGGER(WordInfoView.class);
    private Word word;

    private TextView wordTextView;

    private TextView amPro;
    private ImageView amPlay;

    private TextView enPro;
    private ImageView enPlay;

    private ImageView ttsPlay;

    private TextView wordMean;


    private LinearLayout wordProLayout;


    private PlayAudio playAudio;
    private int playEnAudio = -1;
    private int playAmAudio = -1;
    private int playTtsAudio = -1;

    public WordInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.word_info_main, this);

        playAudio = new PlayAudio();
        this.wordTextView = this.findViewById(R.id.word_info_word);
        this.amPlay = this.findViewById(R.id.word_info_am_paly);
        this.enPlay = this.findViewById(R.id.word_info_en_paly);


        this.enPro = this.findViewById(R.id.word_info_en_pro);
        this.amPro = this.findViewById(R.id.word_info_am_pro);

        ttsPlay = this.findViewById(R.id.word_info_tts_paly);

        this.wordMean = this.findViewById(R.id.word_info_wordmean);

        this.wordProLayout = this.findViewById(R.id.word_info_pro_layout);
        this.initListener();
    }

    private void initListener() {
        this.amPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playAmAudio == -1) {
                    return;
                }


                playAudio.play(playAmAudio, null);


            }
        });

        this.enPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playEnAudio == -1) {
                    return;
                }

                playAudio.play(playEnAudio, null);
            }
        });

        this.ttsPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playTtsAudio == -1) {
                    return;
                }

                logger.d("playTtsAudio");
                playAudio.play(playTtsAudio, null);
            }
        });
    }

    public void load(Word word) {
        this.word = word;
        if (word == null) {

            clear();
            return;
        }
        this.wordTextView.setText(word.getWord());
        if (word.getAmAudionPath() == null || word.getAmAudionPath().isEmpty()) {
            this.wordProLayout.setVisibility(View.GONE);
            ttsPlay.setVisibility(View.VISIBLE);
        } else {
            this.ttsPlay.setVisibility(View.GONE);
            wordProLayout.setVisibility(View.VISIBLE);
            this.enPro.setText("英 [" + word.getPhEn() + "]");
            this.amPro.setText("美 [" + word.getPhAm() + "]");
        }




        initMeans();
        if (!StringUtil.isNullOrEmpty(word.getAmAudionPath())) {
            LoadFile.loadFile(this.word.getAmAudionPath(), new LQHandler.Consumer<String>() {
                @Override
                public void accept(String s) {
                    playAmAudio = playAudio.load(s);
                }
            });
        }

        if (!StringUtil.isNullOrEmpty(word.getEnAudioPath())) {
            LoadFile.loadFile(this.word.getEnAudioPath(), new LQHandler.Consumer<String>() {
                @Override
                public void accept(String s) {
                    playEnAudio = playAudio.load(s);
                }
            });
        }
        if (!StringUtil.isNullOrEmpty(word.getTtsAudioPath())) {
            LoadFile.loadFile(this.word.getTtsAudioPath(), new LQHandler.Consumer<String>() {
                @Override
                public void accept(String s) {
                    playTtsAudio = playAudio.load(s);
                }
            });
        }
    }

    private void clear(){

        logger.d("clear==========");
        this.word = null;
        this.wordTextView.setText("");
        playEnAudio = -1;
        playAmAudio = -1;
        playTtsAudio = -1;

        this.enPro.setText("");
        this.amPro.setText("");
        wordMean.setText("");
    }

    public void play() {
        if (word == null) {
            return;
        }

        logger.d("play==========");logger.d("clear==========");
        if (!StringUtil.isNullOrEmpty(word.getEnAudioPath())) {
            LoadFile.loadFile(this.word.getEnAudioPath(), new LQHandler.Consumer<String>() {
                @Override
                public void accept(String s) {
                    LQMediaPlayer lqMediaPlayer = new LQMediaPlayer();
                    lqMediaPlayer.play(s,1,null);
                }
            });

            return;
        }

        if (!StringUtil.isNullOrEmpty(word.getTtsAudioPath())) {
            LoadFile.loadFile(this.word.getTtsAudioPath(), new LQHandler.Consumer<String>() {
                @Override
                public void accept(String s) {
                    LQMediaPlayer lqMediaPlayer = new LQMediaPlayer();
                    lqMediaPlayer.play(s,1,null);
                }
            });
        }
    }

    private void initMeans() {
        List<WordMean> wordMeans = WordMean.toWordMeans(word.getMeans(), word);
        StringBuilder stringBuilder = new StringBuilder();
        for (WordMean wordMean : wordMeans) {
            stringBuilder.append(wordMean.getWordType()).append(" ").append(wordMean.getMeans()).append("\n");
        }

        this.wordMean.setText(stringBuilder.toString());
    }


    public void destory() {
        playAudio.release();

    }

}
