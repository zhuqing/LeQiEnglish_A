package com.leqienglish.activity.word;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;

import com.leqienglish.R;
import com.leqienglish.controller.word.WordInfoController;
import com.leqienglish.util.BundleUtil;

import xyz.tobebetter.entity.english.Segment;
import xyz.tobebetter.entity.word.Word;

public class WordInfoActivity extends AppCompatActivity {

    private Word word;

    private WordInfoController wordInfoController;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_info);

        word = (Word) this.getIntent().getExtras().getSerializable(BundleUtil.DATA);
        RelativeLayout view = this.findViewById(R.id.word_info_root);

        wordInfoController = new WordInfoController(view,word);
        wordInfoController.init();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        wordInfoController.destory();
    }
}
