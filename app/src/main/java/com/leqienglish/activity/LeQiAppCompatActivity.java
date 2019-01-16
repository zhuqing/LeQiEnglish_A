package com.leqienglish.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.leqienglish.controller.ControllerAbstract;

public abstract class LeQiAppCompatActivity extends AppCompatActivity {

    protected abstract ControllerAbstract getController();
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
                backHandler();
                return true;

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
    protected void onResume() {
        super.onResume();
        if(this.getController() != null){
            this.getController().onResume();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(this.getController() != null){
            this.getController().destory();
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        if(this.getController() != null){
            this.getController().onPause();
        }

    }


}
