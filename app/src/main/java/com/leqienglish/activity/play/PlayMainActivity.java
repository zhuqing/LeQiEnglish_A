package com.leqienglish.activity.play;

import android.os.Bundle;
import android.view.View;

import com.leqienglish.R;
import com.leqienglish.activity.LeQiAppCompatActivity;
import com.leqienglish.controller.play.PlayMainController;

public class PlayMainActivity extends LeQiAppCompatActivity {

    private PlayMainController playMainController;
    @Override
    protected String getActionBarTitle() {
        return "播放";
    }


    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_main);

        View root = this.findViewById(R.id.play_main_root);


        playMainController = new PlayMainController(root);
        playMainController.init();

    }

    @Override
    public void onResume(){
      super.onResume();
      playMainController.onResume();


    }


    @Override
    protected void onPause(){
        super.onPause();
        playMainController.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        playMainController.destory();
    }
}
