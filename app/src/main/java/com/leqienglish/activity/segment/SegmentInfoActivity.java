package com.leqienglish.activity.segment;

import android.os.Bundle;
import android.view.View;

import com.leqienglish.R;
import com.leqienglish.activity.LeQiAppCompatActivity;
import com.leqienglish.controller.ControllerAbstract;
import com.leqienglish.controller.segment.SegmentInfoController;
import com.leqienglish.util.BundleUtil;

import xyz.tobebetter.entity.english.Segment;

/**
 * Created by zhuqing on 2018/4/21.
 */

public class SegmentInfoActivity extends LeQiAppCompatActivity {
    private Segment segment;
    private SegmentInfoController segmentInfoController;

    @Override
    protected ControllerAbstract getController() {
        return this.segmentInfoController;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.play_audio);
        segment = (Segment) this.getIntent().getExtras().getSerializable(BundleUtil.DATA);
        String path = this.getIntent().getExtras().getString(BundleUtil.PATH);
        View view = this.findViewById(R.id.play_audio_view);
        segmentInfoController = new SegmentInfoController(view,segment,path);
        segmentInfoController.init();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected String getActionBarTitle() {
        if(segment == null){
            return "背诵";
        }
        return segment.getTitle();
    }


}
