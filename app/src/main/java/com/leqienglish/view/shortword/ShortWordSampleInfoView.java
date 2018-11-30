package com.leqienglish.view.shortword;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.leqienglish.R;
import com.leqienglish.data.shortword.SentencesInShortWordDataCache;
import com.leqienglish.view.adapter.LeQiBaseAdapter;
import com.leqienglish.view.sentence.SentenceSimpleInfoView;

import xyz.tobebetter.entity.english.sentence.Sentence;
import xyz.tobebetter.entity.english.shortword.ShortWord;

public class ShortWordSampleInfoView extends LinearLayout {

    private TextView wordTextView;
    private TextView infoTextView;
    private ListView sentencesListView;
    private ShortWord shortWord;

    private SentenceListViewAdapter sentenceListViewAdapter;

    public ShortWordSampleInfoView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.shortword_simple_info, this);
        this.init();
    }

    private void init(){
        this.wordTextView = this.findViewById(R.id.shortword_simple_info_word);
        this.infoTextView = this.findViewById(R.id.shortword_simple_info_info);
        this.sentencesListView = this.findViewById(R.id.shortword_simple_info_sentences);

        this.sentenceListViewAdapter = new SentenceListViewAdapter(LayoutInflater.from(getContext()));
        this.sentencesListView.setAdapter(sentenceListViewAdapter);
    }


    public void setItem(ShortWord shortWord){
        this.shortWord = shortWord;
        if(shortWord == null || shortWord.getId() == null){
            return;
        }

        this.load(shortWord);

        if(shortWord.getWord() == null|| shortWord.getWord().isEmpty()){
            wordTextView.setText("");
        }else{
            wordTextView.setText(shortWord.getWord());
        }

        if(shortWord.getInfo() == null|| shortWord.getInfo().isEmpty()){
            infoTextView.setText("");
        }else{
            infoTextView.setText(shortWord.getInfo());
        }


    }

    private void load(ShortWord shortWord){
        if(shortWord == null || shortWord.getId() == null){
            return;
        }
        SentencesInShortWordDataCache sentencesInShortWordDataCache = new SentencesInShortWordDataCache(shortWord.getId());
        sentencesInShortWordDataCache.load((sentences -> {

            int length = 3 > sentences.size()? sentences.size():3;

            sentenceListViewAdapter.updateListView(sentences.subList(0,length));
        }));
    }


    private class  SentenceListViewAdapter extends LeQiBaseAdapter<Sentence> {

        public SentenceListViewAdapter(LayoutInflater mInflater) {
            super(mInflater);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = new SentenceSimpleInfoView(getContext(),null);
            }


            SentenceSimpleInfoView sentenceSimpleInfoView = (SentenceSimpleInfoView) convertView;
            sentenceSimpleInfoView.setItem(this.getItem(position));
            return convertView;
        }
    }

}
