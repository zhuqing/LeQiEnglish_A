package com.leqienglish.activity.user;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.leqienglish.R;
import com.leqienglish.controller.user.LoginController;

public class UserLoginActivity extends AppCompatActivity {

    private LoginController loginController;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_main);
        View view = this.findViewById(R.id.login_main_root);

        loginController = new LoginController(view);
        loginController.setActivity(this);
        loginController.init();
    }



}
