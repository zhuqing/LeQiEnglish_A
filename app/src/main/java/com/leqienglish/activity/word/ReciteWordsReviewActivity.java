package com.leqienglish.activity.word;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.leqienglish.R;
import com.leqienglish.controller.word.ReciteWordsReviewController;

public class ReciteWordsReviewActivity extends AppCompatActivity {

    private ReciteWordsReviewController reciteWordsReviewController;



    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.recite_words_review);


        View rootView = this.findViewById(R.id.recite_words_review_root);
        this.reciteWordsReviewController = new ReciteWordsReviewController(rootView);
        reciteWordsReviewController.init();

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), MyReciteWordsInfoActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                getApplicationContext().startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
