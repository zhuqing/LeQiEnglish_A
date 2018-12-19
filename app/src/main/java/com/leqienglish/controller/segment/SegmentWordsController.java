package com.leqienglish.controller.segment;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.leqienglish.R;
import com.leqienglish.activity.word.WordInfoActivity;
import com.leqienglish.controller.ControllerAbstract;
import com.leqienglish.data.segment.ShortWordsInSegmentDataCache;
import com.leqienglish.data.segment.WordsInSegmentDataCache;
import com.leqienglish.util.BundleUtil;
import com.leqienglish.view.adapter.LeQiBaseAdapter;
import com.leqienglish.view.shortword.ShortWordSampleInfoView;
import com.leqienglish.view.word.WordInfoView;

import xyz.tobebetter.entity.english.Segment;
import xyz.tobebetter.entity.english.shortword.ShortWord;
import xyz.tobebetter.entity.word.Word;

public class SegmentWordsController extends ControllerAbstract {

    private ListView wordsListView;
    private ListView shortWordsListView;
    private Segment segment;
    private WordsInSegmentDataCache wordsInSegmentDataCache;
    private ShortWordsInSegmentDataCache shortWordsInSegmentDataCache;

    private SegmentWordListViewAdapter segmentWordListViewAdapter;
    private SegmentShortWordListViewAdapter segmentShortWordListViewAdapter;

    public SegmentWordsController(View view,Segment segment) {
        super(view);
        this.segment = segment;
        init();

    }

    @Override
    public void init() {

        this.wordsListView = this.getView().findViewById(R.id.segment_words_words);
        this.shortWordsListView = this.getView().findViewById(R.id.segment_words_shortword);

        if(this.segment == null || this.segment.getId() == null){
            return;
        }

        this.wordsInSegmentDataCache = new WordsInSegmentDataCache(this.segment.getId());
        this.shortWordsInSegmentDataCache = new ShortWordsInSegmentDataCache(this.segment.getId());


        this.initShortWordListView();
        this.initWordsListView();

    }

    @Override
    public void reload() {
        this.wordsInSegmentDataCache.load((words)->{
            segmentWordListViewAdapter.updateListView(words);
        });

        this.shortWordsInSegmentDataCache.load((shortWords -> {
            segmentShortWordListViewAdapter.updateListView(shortWords);
        }));
    }

    private void initShortWordListView(){
        this.segmentShortWordListViewAdapter = new SegmentShortWordListViewAdapter(LayoutInflater.from(this.getView().getContext()));
        this.shortWordsListView.setAdapter(segmentShortWordListViewAdapter);
    }

    private void initWordsListView(){
        this.segmentWordListViewAdapter = new SegmentWordListViewAdapter(LayoutInflater.from(this.getView().getContext()));

        this.wordsListView.setAdapter(segmentWordListViewAdapter);

        this.wordsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Word word = segmentWordListViewAdapter.getItem(position);
                if (word == null) {
                    return;
                }

                Intent intent = new Intent();
                intent.putExtras(BundleUtil.create(BundleUtil.DATA, word));
                intent.setClass(getView().getContext(), WordInfoActivity.class);
                getView().getContext().startActivity(intent);
            }
        });

    }

    @Override
    public void destory() {

    }

    private class  SegmentWordListViewAdapter extends LeQiBaseAdapter<Word> {

        public SegmentWordListViewAdapter(LayoutInflater mInflater) {
            super(mInflater);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = new WordInfoView(SegmentWordsController.this.getView().getContext(),null);
            }


            WordInfoView wordInfoView = (WordInfoView) convertView;
            wordInfoView.load(this.getItem(position));
            return convertView;
        }
    }

    private class  SegmentShortWordListViewAdapter extends LeQiBaseAdapter<ShortWord> {

        public SegmentShortWordListViewAdapter(LayoutInflater mInflater) {
            super(mInflater);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = new ShortWordSampleInfoView(SegmentWordsController.this.getView().getContext(),null);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,520);
                convertView.setLayoutParams(layoutParams);
            }

            ShortWordSampleInfoView wordInfoView = (ShortWordSampleInfoView) convertView;

            wordInfoView.setItem(this.getItem(position));
            return convertView;
        }
    }
}


