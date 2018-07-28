package com.leqienglish.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.GridView;
import android.widget.ListView;

import com.leqienglish.R;
import com.leqienglish.controller.PlayAudioController;

import com.leqienglish.util.BundleUtil;

import xyz.tobebetter.entity.english.Content;
import xyz.tobebetter.entity.english.Segment;

/**
 * Created by zhuqing on 2018/4/21.
 */

public class PlayAudioActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_audion);
        Segment content = (Segment) this.getIntent().getExtras().getSerializable(BundleUtil.DATA);
        String path = this.getIntent().getExtras().getString(BundleUtil.PATH);
        GridView view = this.findViewById(R.id.play_audio);
        new PlayAudioController(view,content,path).init();
    }
}
