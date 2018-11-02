package com.leqienglish.controller.word;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.leqienglish.MainActivity;
import com.leqienglish.R;
import com.leqienglish.activity.word.MyReciteWordsInfoActivity;
import com.leqienglish.controller.ControllerAbstract;
import com.leqienglish.data.user.UserDataCache;
import com.leqienglish.data.word.RecitingWordDataCache;
import com.leqienglish.sf.LQService;
import com.leqienglish.util.TaskUtil;
import com.leqienglish.view.word.RecitingWordListView;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

import xyz.tobebetter.entity.word.Word;

public class ReciteWordsReviewController extends ControllerAbstract {

    private RecitingWordListView recitingWordListView;

    private Button finishedButton;
    private Button continueReciteWordButton;
    private TextView closeButton;
    private List<Word> wordList;
    public ReciteWordsReviewController(View view ) {
        super(view);
        //this.wordList = wordList;
    }

    @Override
    public void init() {

        this.recitingWordListView = (RecitingWordListView) this.findViewById(R.id.recite_words_review_wordlist);
        this.finishedButton = (Button) this.findViewById(R.id.recite_words_review_finished);
        this.continueReciteWordButton = (Button) this.findViewById(R.id.recite_words_review_continue);
        this.initListener();
        this.reload();
    }

    private void  initListener(){


       this.finishedButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               commitReciteWords();
               Intent intent = new Intent();
               intent.setClass(getView().getContext(), MainActivity.class);
               intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
               getView().getContext().startActivity(intent);

           }
       });

       this.continueReciteWordButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               commitReciteWords();
               Intent intent = new Intent();
               intent.setClass(getView().getContext(), MyReciteWordsInfoActivity.class);
               intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
               getView().getContext().startActivity(intent);

           }
       });

    }

    private void commitReciteWords(){
        TaskUtil.run(()->{
            for(Word word : recitingWordListView.getSelected()){
                MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
                param.add("userId", UserDataCache.getInstance().getCacheData().getId());
                param.add("wordId",word.getId());
                try {
                    LQService.getRestClient().put("userAndWord/increamReciteCount",null,param,String.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    @Override
    public void reload() {
        wordList = RecitingWordDataCache.getInstance().getCacheData();

        recitingWordListView.load(wordList);


    }

    @Override
    public void destory() {

    }
}
