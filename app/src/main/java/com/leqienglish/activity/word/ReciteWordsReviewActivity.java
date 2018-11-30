package com.leqienglish.activity.word;

import android.os.Bundle;
import android.view.View;

import com.leqienglish.R;
import com.leqienglish.activity.LeQiAppCompatActivity;
import com.leqienglish.controller.word.ReciteWordsReviewController;

public class ReciteWordsReviewActivity extends LeQiAppCompatActivity {

    private ReciteWordsReviewController reciteWordsReviewController;



    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.recite_words_review);


        View rootView = this.findViewById(R.id.recite_words_review_root);
        this.reciteWordsReviewController = new ReciteWordsReviewController(rootView);
        reciteWordsReviewController.init();


    }

    @Override
    protected String getActionBarTitle() {
        return "单词默写";
    }


}
