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
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        //设置搜索的事件
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast t = Toast.makeText(WordInfoActivity.this, query, Toast.LENGTH_SHORT);
                t.setGravity(Gravity.TOP, 0, 0);
                t.show();

                wordInfoController.search(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        wordInfoController.destory();
    }
}
