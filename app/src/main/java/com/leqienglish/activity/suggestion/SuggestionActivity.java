package com.leqienglish.activity.suggestion;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.leqienglish.R;
import com.leqienglish.controller.suggestion.SuggestionController;

public class SuggestionActivity extends AppCompatActivity {


    private View mainView;

    private SuggestionController suggestionController;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.suggestion_layout);
        this.mainView = this.findViewById(R.id.suggestion_layout_main);
        this.suggestionController = new SuggestionController(mainView);
        this.suggestionController.init();
    }
}
