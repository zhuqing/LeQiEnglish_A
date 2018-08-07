package com.leqienglish.controller.word;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.leqienglish.R;
import com.leqienglish.activity.ArticleInfoActivity;
import com.leqienglish.controller.ControllerAbstract;
import com.leqienglish.data.content.ContentDataCache;
import com.leqienglish.data.word.WordSentenceDataCache;
import com.leqienglish.playandrecord.PlayAudio;
import com.leqienglish.playandrecord.PlayMediaPlayer;
import com.leqienglish.playandrecord.RecordAudioThread;
import com.leqienglish.sf.LoadFile;
import com.leqienglish.util.BundleUtil;
import com.leqienglish.util.FileUtil;
import com.leqienglish.util.LOGGER;
import com.leqienglish.util.LQHandler;
import com.leqienglish.view.LeQiTextView;
import com.leqienglish.view.adapter.LeQiBaseAdapter;
import com.leqienglish.view.recommend.RecommendArticle;

import java.io.File;
import java.io.IOException;
import java.util.List;

import xyz.tobebetter.entity.Consistent;
import xyz.tobebetter.entity.english.Content;
import xyz.tobebetter.entity.english.play.AudioPlayPoint;
import xyz.tobebetter.entity.english.segment.WordAndSegment;
import xyz.tobebetter.entity.word.Word;
import xyz.tobebetter.entity.word.WordMean;

public class WordInfoController extends ControllerAbstract {
    private LOGGER logger = new LOGGER(WordInfoController.class);
    private Word word;

    private TextView wordTextView;

    private TextView amPro;
    private ImageView amPlay;

    private TextView enPro;
    private ImageView enPlay;

    private ImageView ttsPlay;

    private ListView sentences;

    private TextView wordMean;

    private PlayAudio playAudio;
    private int playEnAudio = -1;
    private int playAmAudio = -1;
    private int playTtsAudio = -1;
    private ListViewAdapter listViewAdapter;


    private LinearLayout wordProLayout;


    public WordInfoController(View view, Word word) {
        super(view);
        this.word = word;
    }

    @Override
    public void init() {

        logger.d(this.getView() + "");
        playAudio = new PlayAudio();
        this.wordTextView = this.getView().findViewById(R.id.word_info_word);
        this.amPlay = this.getView().findViewById(R.id.word_info_am_paly);
        this.enPlay = this.getView().findViewById(R.id.word_info_en_paly);

        wordProLayout = this.getView().findViewById(R.id.word_info_pro_layout);
        this.enPro = this.getView().findViewById(R.id.word_info_en_pro);
        this.amPro = this.getView().findViewById(R.id.word_info_am_pro);

        ttsPlay = this.getView().findViewById(R.id.word_info_tts_paly);

        this.sentences = this.getView().findViewById(R.id.word_info_sentences);

        this.wordMean = this.getView().findViewById(R.id.word_info_wordmean);

        this.listViewAdapter = new ListViewAdapter(LayoutInflater.from(this.getView().getContext()));
        this.sentences.setAdapter(this.listViewAdapter);

        this.initListener();
        this.initData();

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

    public void update(Word word) {
        this.word = word;
        this.initData();
    }


    private void initData() {
        if (word == null) {
            return;
        }
        this.wordTextView.setText(word.getWord());
        if (word.getAmAudionPath() == null || word.getAmAudionPath().isEmpty()) {
            this.wordProLayout.setVisibility(View.GONE);

        } else {
            this.ttsPlay.setVisibility(View.GONE);
            this.enPro.setText("英 [" + word.getPhEn() + "]");
            this.amPro.setText("美 [" + word.getPhAm() + "]");
        }


        initMeans();
        if (this.word.getAmAudionPath() == null || this.word.getAmAudionPath().isEmpty()) {

        } else {
            LoadFile.loadFile(this.word.getAmAudionPath(), new LQHandler.Consumer<String>() {
                @Override
                public void accept(String s) {
                    playAmAudio = playAudio.load(s);
                }
            });
        }

        if (this.word.getEnAudioPath() != null && !this.word.getEnAudioPath().isEmpty()) {
            LoadFile.loadFile(this.word.getEnAudioPath(), new LQHandler.Consumer<String>() {
                @Override
                public void accept(String s) {
                    playEnAudio = playAudio.load(s);
                }
            });
        } else {
            LoadFile.loadFile(this.word.getTtsAudioPath(), new LQHandler.Consumer<String>() {
                @Override
                public void accept(String s) {
                    playTtsAudio = playAudio.load(s);
                }
            });
        }

        WordSentenceDataCache wordSentenceDataCache = new WordSentenceDataCache(this.word);
        wordSentenceDataCache.load(new LQHandler.Consumer<List<WordAndSegment>>() {
            @Override
            public void accept(List<WordAndSegment> wordAndSegments) {
                logger.d(word.getWord() + "\t" + wordAndSegments.size() + " \twordandSegments");
                listViewAdapter.updateListView(wordAndSegments);
            }
        });

    }

    private void initMeans() {
        List<WordMean> wordMeans = WordMean.toWordMeans(word.getMeans(), word);
        StringBuilder stringBuilder = new StringBuilder();
        for (WordMean wordMean : wordMeans) {
            stringBuilder.append(wordMean.getWordType()).append(" ").append(wordMean.getMeans()).append("\n");
        }

        this.wordMean.setText(stringBuilder.toString());
    }

    @Override
    public void reload() {

    }

    @Override
    public void destory() {
        playAudio.release();

    }


    final class ViewHolder {

        LeQiTextView title;

        TextView chTextView;

        TextView toContent;

        ImageView play;

    }

    class ListViewAdapter extends LeQiBaseAdapter<WordAndSegment> {


        public ListViewAdapter(LayoutInflater mInflater) {
            super(mInflater);
        }


        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            WordInfoController.ViewHolder holder = null;
            if (view != null) {
                holder = (WordInfoController.ViewHolder) view.getTag();
            } else {
                holder = new WordInfoController.ViewHolder();
                view = this.mInflater.inflate(R.layout.word_scentences_item, null);

                holder.title = view.findViewById(R.id.word_scentences_item_en);
                holder.chTextView = view.findViewById(R.id.word_scentences_item_ch);
                holder.play = view.findViewById(R.id.word_scentences_item_play);
                holder.toContent = view.findViewById(R.id.word_scentences_item_to_content);

                holder.toContent.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
                view.setTag(holder);
                holder.play.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        WordAndSegment wordAndSegment = (WordAndSegment) v.getTag();
                        logger.d("play..."+wordAndSegment.getWord());
                        play(wordAndSegment);
                    }
                });

                holder.toContent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        WordAndSegment wordAndSegment = (WordAndSegment) v.getTag();
                        turnToContentActivity(wordAndSegment);
                    }
                });
            }

            WordAndSegment actical = this.getItem(i);
            if (actical == null) {
                return view;
            }

            Spanned result = Html.fromHtml((i + 1) + "." + addStyle(getEnStr(actical.getScentence())));
            holder.title.setText(result);
            holder.chTextView.setText(this.getEnStr(actical.getScentence()));
            holder.toContent.setText("来自："+actical.getContentTitle());

            holder.play.setTag(actical);
            holder.toContent.setTag(actical);
            String ch = this.getCh(actical.getScentence());
            if (ch != null && !ch.isEmpty()) {
                holder.chTextView.setText(ch);
            }


            return view;
        }

        public void play(WordAndSegment wordAndSegment){
            LoadFile.loadFile(wordAndSegment.getAudioPath(), new LQHandler.Consumer<String>() {
                @Override
                public void accept(String s) {
                    PlayMediaPlayer playMediaPlayer = PlayMediaPlayer.getInstance();
                    try {
                        playMediaPlayer.setResource(s);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return;
                    }
                    AudioPlayPoint audioPlayPoint = new AudioPlayPoint();
                    audioPlayPoint.setStartTime(wordAndSegment.getStartTime());
                    audioPlayPoint.setEndTime(wordAndSegment.getEndTime());
                    playMediaPlayer.play(audioPlayPoint,null);
                }
            });
        }

        private void turnToContentActivity(WordAndSegment wordAndSegment){
            String contentId = wordAndSegment.getContentId();

            ContentDataCache contentDataCache = new ContentDataCache(contentId);
            contentDataCache.load(new LQHandler.Consumer<Content>() {
                @Override
                public void accept(Content content) {
                    Intent intent = new Intent();
                    intent.putExtras(BundleUtil.create(BundleUtil.DATA, content));
                    intent.setClass(WordInfoController.this.getView().getContext(), ArticleInfoActivity.class);
                    WordInfoController.this.getView().getContext().startActivity(intent);
                }
            });
        }

        private String addStyle(String enScentence) {
            String wordStr = word.getWord();

            String html = enScentence.replaceAll("(?i)" + wordStr, "<font color='#ff3b30'>" + wordStr + "</font>");
            logger.d(html);
            return html;
        }

        private String getEnStr(String scentence) {
            String[] arrs = scentence.split(Consistent.SLIP_EN_AND_CH);
            return arrs[0];
        }

        private String getCh(String scentence) {
            String[] arrs = scentence.split(Consistent.SLIP_EN_AND_CH);
            if (arrs.length == 2) {
                if (arrs[1].equals("null")) {
                    return null;
                } else {
                    return arrs[1];
                }
            }

            return null;
        }
    }


}
