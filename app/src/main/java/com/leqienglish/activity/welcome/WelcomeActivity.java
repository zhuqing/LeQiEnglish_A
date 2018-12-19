package com.leqienglish.activity.welcome;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.leqienglish.MainActivity;
import com.leqienglish.R;
import com.leqienglish.sf.LQService;
import com.leqienglish.util.LOGGER;

public class WelcomeActivity extends Activity {

   private final LOGGER LOG = new LOGGER(WelcomeActivity.class);
    private boolean hasToMain = false;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //无title
       // requestWindowFeature(Window.FEATURE_NO_TITLE);
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
                WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.welcome);

       View view = this.findViewById(R.id.welcome_root);
       view.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               toMainActivity();
           }
       });


    }

    protected void onResume() {

        super.onResume();

       // LOG.d("onResume");

        LQService.checkNet((b)->{
            LQService.isConnect = b;

            this.toMainActivity();
        });

    }

    protected void onRestart() {

        super.onRestart();

      //  LOG.d("onRestart");

    }



    private void toMainActivity(){
        if(hasToMain){
            return;
        }
        hasToMain = true;
        Intent intent = new Intent();
        intent.setClass(this.getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        getApplicationContext().startActivity(intent);
    }


}
