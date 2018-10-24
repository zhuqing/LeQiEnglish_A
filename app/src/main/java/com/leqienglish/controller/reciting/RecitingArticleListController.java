package com.leqienglish.controller.reciting;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.RadioGroup;

import com.leqienglish.R;
import com.leqienglish.controller.ControllerAbstract;
import com.leqienglish.data.content.MyRecitedContentDataCache;
import com.leqienglish.data.content.MyRecitingContentDataCache;
import com.leqienglish.util.LQHandler;
import com.leqienglish.view.adapter.content.RecitingArticleItemAdapter;

import java.util.List;

import xyz.tobebetter.entity.english.content.ReciteContentVO;

public class RecitingArticleListController extends ControllerAbstract {

    private final static String RECITING="1";
    private final static String RECITED="2";

    private RadioGroup radioGroup;
    private GridView gridView;

    private RecitingArticleItemAdapter recitingArticleItemAdapter;

    public RecitingArticleListController(View view) {
        super(view);
    }

    @Override
    public void init() {
      this.radioGroup = this.getView().findViewById(R.id.reciting_article_list_radiogroiup);
        this.gridView = this.getView().findViewById(R.id.reciting_acticle_list_gridview);
        this.recitingArticleItemAdapter = new RecitingArticleItemAdapter(LayoutInflater.from(this.getView().getContext()));
        this.gridView.setAdapter(this.recitingArticleItemAdapter);
        this.initRadioGroup();
        loadRecitedData();
    }

    private void initRadioGroup(){
        this.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.reciting_article_list_recited){
                    loadRecitedData();
                }else{
                    loadRecitingData();
                }
            }
        });



    }

    private void loadRecitedData(){
        MyRecitedContentDataCache.getInstance().load(new LQHandler.Consumer<List<ReciteContentVO>>() {
            @Override
            public void accept(List<ReciteContentVO> reciteContentVOS) {
                recitingArticleItemAdapter.updateListView(reciteContentVOS);
            }
        });
    }

    private void loadRecitingData(){
        MyRecitingContentDataCache.getInstance().load(new LQHandler.Consumer<List<ReciteContentVO>>() {
            @Override
            public void accept(List<ReciteContentVO> reciteContentVOS) {
                recitingArticleItemAdapter.updateListView(reciteContentVOS);
            }
        });
    }
    @Override
    public void reload() {

    }

    @Override
    public void destory() {

    }
}
