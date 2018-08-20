package com.leqienglish.activity.word;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.leqienglish.R;
import com.leqienglish.controller.word.ReciteWordsController;
import com.leqienglish.controller.word.WordInfoController;
import com.leqienglish.util.BundleUtil;

import xyz.tobebetter.entity.word.Word;

public class ReciteWordsActivity  extends AppCompatActivity {

    private ReciteWordsController reciteWordsController;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.recite_words);
        View view  = this.findViewById(R.id.recite_words_root);

        reciteWordsController = new ReciteWordsController(view);
        reciteWordsController.init();

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.title_word_recite);
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        reciteWordsController.destory();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
