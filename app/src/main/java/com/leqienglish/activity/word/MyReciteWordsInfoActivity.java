package com.leqienglish.activity.word;

import android.os.Bundle;

import com.leqienglish.R;
import com.leqienglish.activity.LeQiAppCompatActivity;
import com.leqienglish.controller.word.MyReciteWordsInfoController;

public class MyReciteWordsInfoActivity extends LeQiAppCompatActivity {
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_recite_word_info);

        new MyReciteWordsInfoController(this.findViewById(R.id.my_recite_word_info_root)).init();

    }

    @Override
    protected String getActionBarTitle() {
        return this.getApplicationContext().getString(  R.string.title_my_word_recite);
    }
}
