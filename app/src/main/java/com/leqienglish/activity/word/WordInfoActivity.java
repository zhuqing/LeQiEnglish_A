package com.leqienglish.activity.word;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.leqienglish.R;
import com.leqienglish.activity.content.ShowAllContentActiviey;
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

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(word.getWord());
        }
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


    @Override
    public void onDestroy(){
        super.onDestroy();
        wordInfoController.destory();
    }
}
