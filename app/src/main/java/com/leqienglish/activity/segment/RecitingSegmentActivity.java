package com.leqienglish.activity.segment;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.leqienglish.R;
import com.leqienglish.controller.segment.RecitingSegmentController;
import com.leqienglish.util.BundleUtil;

import xyz.tobebetter.entity.english.Segment;

public class RecitingSegmentActivity extends AppCompatActivity {

    private RecitingSegmentController recitingSegmentController;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.reciting_segment);

        View root = this.findViewById(R.id.reciting_borad_root);

        Segment segment = (Segment) this.getIntent().getExtras().getSerializable(BundleUtil.DATA);

        this.recitingSegmentController = new RecitingSegmentController(root,segment);
        this.recitingSegmentController.init();
    }
}
