package com.leqienglish.view.word;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.leqienglish.R;
import com.leqienglish.view.adapter.simpleitem.SimpleItemAdapter;

import java.util.List;

import xyz.tobebetter.entity.Entity;
import xyz.tobebetter.entity.word.Word;

public class RecitingWordListView extends RelativeLayout {

    private List<Word> wordList;


    private GridView gridView;

    private SimpleItemAdapter<Word> simpleItemAdapter ;

    public RecitingWordListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.reciting_word_list,this);

        this.gridView = this.findViewById(R.id.reciting_word_list_gridview);

        this.simpleItemAdapter= new SimpleItemAdapter<Word>( LayoutInflater.from(context)) {
            @Override
            protected String toString(Word entity) {
                if(entity == null){
                    return "";
                }
                return entity.getWord();
            }

            @Override
            protected void setStyle(TextView textView) {
                textView.setGravity(Gravity.CENTER);
            }
        };

        this.gridView.setAdapter(simpleItemAdapter);

    }

    public void load(List<Word> wordList){
        this.wordList = wordList;
        this.simpleItemAdapter.updateListView(wordList);
    }
}
