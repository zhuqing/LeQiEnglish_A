package com.leqienglish.view.sentence;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leqienglish.R;

import xyz.tobebetter.entity.english.sentence.Sentence;

/**
 * 只显示句子的中英文的
 */
public class SentenceSimpleInfoView extends LinearLayout {
    private TextView englishTextView;
    private TextView chineaseTextView;
    private Sentence sentence;

    public SentenceSimpleInfoView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.sentence_simple_info, this);
        this.init();
    }

    private void init(){
        this.chineaseTextView = this.findViewById(R.id.sentence_simple_info_chinease);
        this.englishTextView = this.findViewById(R.id.sentence_simple_info_english);
    }

    public void setItem(Sentence sentence,int index){
        this.sentence = sentence;
        if(sentence.getEnglish()== null|| sentence.getEnglish().isEmpty()){
            this.englishTextView.setText("");
        }else{
            this.englishTextView.setText(index+"."+sentence.getEnglish());
        }
        if(sentence.getChinese()== null|| sentence.getChinese().isEmpty()){
            this.chineaseTextView.setText("");
        }else{
            this.chineaseTextView.setText(sentence.getChinese());
        }

    }
}
