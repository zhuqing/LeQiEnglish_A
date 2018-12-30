package com.leqienglish.activity.word;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.leqienglish.R;
import com.leqienglish.controller.word.ArticleWordListController;
import com.leqienglish.util.BundleUtil;

import xyz.tobebetter.entity.english.Content;

public class ArticleWordListActivity extends AppCompatActivity {

    private ArticleWordListController articleWordListController;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_word_list);

        Content content= (Content) this.getIntent().getExtras().getSerializable(BundleUtil.DATA);
        View view = this.findViewById(R.id.article_word_list_root);
        this.articleWordListController = new ArticleWordListController(view,content);
        articleWordListController.init();

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.title_word_recite);
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
    public void onDestroy() {
        super.onDestroy();
        //  reciteWordsController.destory();
    }
}
