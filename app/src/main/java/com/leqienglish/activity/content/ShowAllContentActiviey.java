package com.leqienglish.activity.content;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.leqienglish.R;
import com.leqienglish.controller.content.ShowAllContentController;
import com.leqienglish.util.LOGGER;

public class ShowAllContentActiviey extends AppCompatActivity {
    private LOGGER logger = new LOGGER(ShowAllContentActiviey.class);

    private ShowAllContentController showAllContentController;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_all_cotent);

        View rootView = this.findViewById(R.id.show_all_content_root);
        this.showAllContentController = new ShowAllContentController(rootView);
        this.showAllContentController.init();

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.title_search_content);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        //设置搜索的事件
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast t = Toast.makeText(ShowAllContentActiviey.this, query, Toast.LENGTH_SHORT);
                t.setGravity(Gravity.TOP, 0, 0);
                t.show();

                showAllContentController.search(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if(newText == null || newText.isEmpty()){
                    showAllContentController.search("");
                }
                return false;
            }
        });



        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String q = searchView.getQuery().toString();
                logger.d("setOnSearchClickListener:"+q);
            }
        });



        return super.onCreateOptionsMenu(menu);
    }
}
