package com.leqienglish.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.leqienglish.util.AppType;

public class AppContextActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppType.mainContext = this.getApplicationContext();
        //this.setContentView();
    }
}
