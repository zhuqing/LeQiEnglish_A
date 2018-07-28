package com.leqienglish.activity.load;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import com.leqienglish.R;
import com.leqienglish.activity.PlayAudioActivity;

import com.leqienglish.data.content.MyRecitingContentDataCache;
import com.leqienglish.sf.LoadFile;
import com.leqienglish.util.AppType;
import com.leqienglish.util.BundleUtil;
import com.leqienglish.util.FileUtil;
import com.leqienglish.util.LOGGER;
import com.leqienglish.view.LoadingView;

import java.io.IOException;

import xyz.tobebetter.entity.english.Content;
import xyz.tobebetter.entity.english.Segment;

/**
 * Created by zhuqing on 2018/5/9.
 */

public class LoadingActivity extends AppCompatActivity {
    private LOGGER logger = new LOGGER(LoadingActivity.class);
    private LoadingView loadingView;
    private Segment content;

    private String path;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        content = (Segment) this.getIntent().getExtras().getSerializable(BundleUtil.DATA);
        this.path = this.getIntent().getExtras().getString(BundleUtil.PATH);
        loadingView = this.findViewById(R.id.loading_progress_view);

        loadingView.setMaxValue(1.0f);
        loadAudioFile(path);
    }


    /**
     * 加载图片
     *
     * @param path
     * @throws IOException
     */
    private void loadAudioFile(String path) {
        final String filePath;

        filePath = FileUtil.toLocalPath(path);
        //  if (!FileUtil.exists(content.getAudioPath())) {
        logger.d("loadAudioFile path=" + filePath);
        LoadFile.loadFile( path, new LoadingHandler());

        // }


    }

    public class LoadingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Double data = msg.getData() == null ? 0.0 : msg.getData().getDouble(AppType.DATA);
            switch (msg.what) {
                case AppType.DOWNLOAD_ALLLEGTH:
                    loadingView.setMaxValue(data.floatValue());
                    break;
                case AppType.HAS_DOWNLOAD:

                    loadingView.setCurrentValue(data.floatValue());
                    break;
                case AppType.DOWNLOAD_OVER:
                    Intent intent = new Intent();
                    Bundle bundle = BundleUtil.create(BundleUtil.DATA, content);
                    BundleUtil.create(bundle, BundleUtil.PATH, path);
                    intent.putExtras(bundle);
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
