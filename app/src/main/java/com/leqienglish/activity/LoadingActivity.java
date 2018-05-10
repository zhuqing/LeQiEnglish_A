package com.leqienglish.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import com.leqienglish.R;
import com.leqienglish.controller.HomeListViewController;
import com.leqienglish.entity.english.Content;
import com.leqienglish.sf.LQService;
import com.leqienglish.sf.task.HttpDownLoadProcessTask;
import com.leqienglish.util.AppType;
import com.leqienglish.util.BundleUtil;
import com.leqienglish.util.FileUtil;
import com.leqienglish.util.LQHandler;
import com.leqienglish.view.LoadingView;

import org.springframework.http.MediaType;

import java.io.File;
import java.io.IOException;

/**
 * Created by zhuqing on 2018/5/9.
 */

public class LoadingActivity extends AppCompatActivity {

    private LoadingView loadingView;
    private Content content;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        content = (Content) this.getIntent().getExtras().getSerializable(BundleUtil.DATA);

        loadingView = (LoadingView) this.findViewById(R.id.dp_game_progress);
        loadAudioFile(content);
    }


    /**
     * 加载图片
     *
     * @param content
     * @throws IOException
     */
    private void loadAudioFile(Content content) {
        final String filePath;
        try {
            filePath = FileUtil.getFileAbsolutePath(content.getAudioPath());
            if (!FileUtil.exists(content.getAudioPath())) {
                new HttpDownLoadProcessTask("http://192.168.1.115:8080"+getResources().getString(R.string.AUDIO_PATH) + content.getId(), filePath, new LoadingHandler(), null).execute();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public class LoadingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            String data = msg.getData() == null ? "0" : msg.getData().getString(AppType.DATA);
            switch (msg.what) {
                case AppType.DOWNLOAD_ALLLEGTH:
                    loadingView.setMaxValue(Float.valueOf(data));
                    break;
                case AppType.HAS_DOWNLOAD:
                    loadingView.setCurrentValue(Float.valueOf(data));
                    break;
                case AppType.DOWNLOAD_OVER:
                    Intent intent = new Intent();
                    intent.putExtras(BundleUtil.create(BundleUtil.DATA, content));
                    intent.setClass(LoadingActivity.this, PlayAudioActivity.class);
                  //  intent.
                    LoadingActivity.this.startActivity(intent);
                    break;
                case AppType.DOWNLOAD_ERROR:
                    break;
            }
        }
    }

}
