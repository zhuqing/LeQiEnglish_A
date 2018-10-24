package com.leqienglish.activity.word;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.leqienglish.R;
import com.leqienglish.controller.word.MyReciteWordsInfoController;

public class MyReciteWordsInfoActivity extends AppCompatActivity {
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_recite_word_info);

        new MyReciteWordsInfoController(this.findViewById(R.id.my_recite_word_info_root)).init();
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.title_my_word_recite);
        }
    }
}
