package com.leqienglish.activity.suggestion;

import android.os.Bundle;
import android.view.View;

import com.leqienglish.R;
import com.leqienglish.activity.LeQiAppCompatActivity;
import com.leqienglish.controller.ControllerAbstract;
import com.leqienglish.controller.suggestion.SuggestionController;

public class SuggestionActivity extends LeQiAppCompatActivity {


    private View mainView;

    private SuggestionController suggestionController;

    @Override
    protected ControllerAbstract getController() {
        return suggestionController;
    }

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.suggestion_layout);
        this.mainView = this.findViewById(R.id.suggestion_layout_main);
        this.suggestionController = new SuggestionController(mainView);
        this.suggestionController.init();
    }

    @Override
    protected String getActionBarTitle() {
        return "感谢您的宝贵意见";
    }
}
