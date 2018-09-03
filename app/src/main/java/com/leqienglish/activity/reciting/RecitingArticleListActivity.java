package com.leqienglish.activity.reciting;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.leqienglish.R;
import com.leqienglish.controller.reciting.RecitingArticleListController;

public class RecitingArticleListActivity extends AppCompatActivity {

   private RecitingArticleListController recitingArticleListController;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.reciting_article_list);

        View view = this.findViewById(R.id.reciting_article_list_root);
        recitingArticleListController = new RecitingArticleListController(view);
        recitingArticleListController.init();

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
}
