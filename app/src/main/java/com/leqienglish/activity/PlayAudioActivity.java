package com.leqienglish.activity;

import android.os.Bundle;
import android.view.View;

import com.leqienglish.R;
import com.leqienglish.controller.PlayAudioAController;
import com.leqienglish.util.BundleUtil;

import xyz.tobebetter.entity.english.Segment;

/**
 * Created by zhuqing on 2018/4/21.
 */

public class PlayAudioActivity extends  LeQiAppCompatActivity {
    private Segment segment;
    private PlayAudioAController playAudioAController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.play_audio);
        segment = (Segment) this.getIntent().getExtras().getSerializable(BundleUtil.DATA);
        String path = this.getIntent().getExtras().getString(BundleUtil.PATH);
        View view = this.findViewById(R.id.play_audio_view);
        playAudioAController = new PlayAudioAController(view,segment,path);
        playAudioAController.init();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected String getActionBarTitle() {
        if(segment == null){
            return "背诵";
        }
        return segment.getTitle();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        playAudioAController.destory();
    }
}
