package com.leqienglish.activity.segment;

import android.os.Bundle;
import android.view.View;

import com.leqienglish.R;
import com.leqienglish.activity.LeQiAppCompatActivity;
import com.leqienglish.controller.ControllerAbstract;
import com.leqienglish.controller.segment.RecitingSegmentController;
import com.leqienglish.util.BundleUtil;

import xyz.tobebetter.entity.english.Segment;

public class RecitingSegmentActivity extends LeQiAppCompatActivity {


    private RecitingSegmentController recitingSegmentController;
    private Segment segment;

    @Override
    protected ControllerAbstract getController() {
        return recitingSegmentController;
    }

    public void onCreate(Bundle savedInstanceState) {

        this.setContentView(R.layout.reciting_segment);

        View root = this.findViewById(R.id.reciting_borad_root);

         segment = (Segment) this.getIntent().getExtras().getSerializable(BundleUtil.DATA);

        this.recitingSegmentController = new RecitingSegmentController(root,segment);
        this.recitingSegmentController.init();

        super.onCreate(savedInstanceState);
    }

    @Override
    protected String getActionBarTitle() {
        return segment.getTitle();
    }
}
