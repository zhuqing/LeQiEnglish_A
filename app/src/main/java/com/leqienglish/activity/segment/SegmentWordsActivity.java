package com.leqienglish.activity.segment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.TabHost;

import com.leqienglish.R;
import com.leqienglish.activity.LeQiAppCompatActivity;
import com.leqienglish.controller.ControllerAbstract;
import com.leqienglish.controller.segment.SegmentWordsController;
import com.leqienglish.util.BundleUtil;

import xyz.tobebetter.entity.english.Segment;

public class SegmentWordsActivity extends LeQiAppCompatActivity {
    private Segment segment;
    private TabHost tabHost;
    private ViewPager viewPager;
    private SegmentWordsController segmentWordsController;

    @Override
    protected ControllerAbstract getController() {
        return segmentWordsController;
    }

    public void onCreate(Bundle savedInstanceState) {

        this.setContentView(R.layout.segment_words);

        this.tabHost = this.findViewById(android.R.id.tabhost);

        //初始化TabHost容器
        tabHost.setup();
        //在TabHost创建标签，然后设置：标题／图标／标签页布局
        tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("单词" , null).setContent(R.id.segment_words_words));
        tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator("短语" , null).setContent(R.id.segment_words_shortword));


        segment = (Segment) this.getIntent().getExtras().getSerializable(BundleUtil.DATA);

        segmentWordsController = new SegmentWordsController(tabHost,segment);
        segmentWordsController.reload();

        super.onCreate(savedInstanceState);
    }
    @Override
    protected String getActionBarTitle() {
        return "相关单词";
    }
}
