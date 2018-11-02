package com.leqienglish.activity.word;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.leqienglish.R;
import com.leqienglish.controller.word.ReciteWordsController;
import com.leqienglish.controller.word.WriteWordsController;
import com.leqienglish.data.word.RecitingWordDataCache;
import com.leqienglish.util.BundleUtil;

public class ReciteWordsActivity  extends AppCompatActivity {

    private ReciteWordsController reciteWordsController;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.recite_words);
        View view  = this.findViewById(R.id.recite_words_root);
        boolean isRecite =  this.getIntent().getExtras().getBoolean(BundleUtil.DATA,true);
        boolean hasData =  this.getIntent().getExtras().getBoolean(BundleUtil.DATA_BL,false);

        if(isRecite){
            reciteWordsController = new ReciteWordsController(view);

        }else{
            reciteWordsController = new WriteWordsController(view);

        }

        if(hasData){
            reciteWordsController.setWordList(RecitingWordDataCache.getInstance().getCacheData());
        }

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
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), MyReciteWordsInfoActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                getApplicationContext().startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
