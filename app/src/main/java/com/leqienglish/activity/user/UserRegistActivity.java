package com.leqienglish.activity.user;

import android.os.Bundle;
import android.view.View;

import com.leqienglish.R;
import com.leqienglish.activity.LeQiAppCompatActivity;
import com.leqienglish.controller.user.UserRegistController;

public class UserRegistActivity extends LeQiAppCompatActivity {
    private UserRegistController userRegistController;
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.regist_main);
        View view = this.findViewById(R.id.regist_main_root);
        this.userRegistController = new UserRegistController(view);
        this.userRegistController.init();
    }

    @Override
    protected String getActionBarTitle() {
        return "注册";
    }
}
