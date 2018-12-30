package com.leqienglish.activity.segment;

import android.os.Bundle;
import android.view.View;

import com.leqienglish.R;
import com.leqienglish.activity.LeQiAppCompatActivity;
import com.leqienglish.controller.segment.SegmentPlayController;
import com.leqienglish.util.BundleUtil;

import xyz.tobebetter.entity.english.Content;

/**
 * 播放单个Segment ， 也可以播放多个Segment，取决于传人的数据
 *
 * 可以从播放列表中传人，也可以从Segment背诵界面传人
 */
public class SegmentPlayActivity extends LeQiAppCompatActivity {

    private Content content;


    private Integer currentIndex;

    private SegmentPlayController segmentPlayController;

    public void onCreate(Bundle savedInstanceState) {

        this.setContentView(R.layout.segment_play_audio);

        View root = this.findViewById(R.id.segment_play_audio_root);

        content = (Content) this.getIntent().getExtras().getSerializable(BundleUtil.DATA);

        currentIndex = (Integer) this.getIntent().getExtras().getSerializable(BundleUtil.INDEX);

        segmentPlayController = new SegmentPlayController(root,currentIndex,content);
        segmentPlayController.init();
        segmentPlayController.reload();
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume(){
        super.onResume();
        segmentPlayController.onResume();
    }


    @Override
    protected void onPause(){
        super.onPause();
        segmentPlayController.onPause();

    }

    @Override
    protected void backHandler(){
        super.backHandler();
    }

    @Override
    protected String getActionBarTitle() {
        if(content == null){
            return "";
        }
        return content.getTitle();
    }
}
