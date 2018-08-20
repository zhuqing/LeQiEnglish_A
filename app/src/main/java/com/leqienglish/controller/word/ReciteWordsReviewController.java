package com.leqienglish.controller.word;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.leqienglish.MainActivity;
import com.leqienglish.R;
import com.leqienglish.activity.ArticleInfoActivity;
import com.leqienglish.controller.ControllerAbstract;
import com.leqienglish.data.word.RecitingWordDataCache;
import com.leqienglish.util.BundleUtil;
import com.leqienglish.util.LQHandler;
import com.leqienglish.view.word.RecitingWordListView;

import java.util.List;

import io.reactivex.android.plugins.RxAndroidPlugins;
import xyz.tobebetter.entity.word.Word;

public class ReciteWordsReviewController extends ControllerAbstract {

    private RecitingWordListView recitingWordListView;

    private Button continueReciteButton;
    private TextView closeButton;
    private List<Word> wordList;
    public ReciteWordsReviewController(View view ) {
        super(view);
        //this.wordList = wordList;
    }

    @Override
    public void init() {

        this.recitingWordListView = (RecitingWordListView) this.findViewById(R.id.recite_words_review_wordlist);
        this.continueReciteButton = (Button) this.findViewById(R.id.recite_words_review_continue);
        this.closeButton = (TextView) this.findViewById(R.id.recite_words_review_close);
        this.initListener();
        this.reload();
    }

    private void  initListener(){
       this.closeButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent();
               intent.setClass(getView().getContext(), MainActivity.class);
               intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
               getView().getContext().startActivity(intent);
           }
       });

       this.continueReciteButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

           }
       });

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
                recitingWordListView.load(wordList);

            }
        });


    }

    @Override
    public void destory() {

    }
}
