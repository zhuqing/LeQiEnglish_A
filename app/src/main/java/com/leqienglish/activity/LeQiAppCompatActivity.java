package com.leqienglish.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public abstract class LeQiAppCompatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(this.getActionBarTitle());

        }
    }

    protected abstract String getActionBarTitle();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // back button
                backHandler();
                return true;
            //    case android.R.id.
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 返回按钮事件处理
     */
    protected void backHandler(){
        this.finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //  reciteWordsController.destory();
    }

}
