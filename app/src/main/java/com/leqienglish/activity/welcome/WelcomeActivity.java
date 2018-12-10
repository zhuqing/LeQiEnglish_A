package com.leqienglish.activity.welcome;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import com.leqienglish.MainActivity;
import com.leqienglish.R;
import com.leqienglish.sf.LQService;

public class WelcomeActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //无title
       // requestWindowFeature(Window.FEATURE_NO_TITLE);
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
                WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.welcome);



        LQService.checkNet((b)->{
            LQService.isConnect = b;
            this.toMainActivity();
        });
    }

    private void toMainActivity(){
        Intent intent = new Intent();
        intent.setClass(this.getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        getApplicationContext().startActivity(intent);
    }
}
