package com.leqienglish.view.word;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.leqienglish.R;
import com.leqienglish.view.adapter.simpleitem.SimpleItemAdapter;

import java.util.ArrayList;
import java.util.List;

import xyz.tobebetter.entity.word.Word;

public class RecitingWordListView extends RelativeLayout {

    private List<Word> wordList;

    private List<Word> selected = new ArrayList<>();


    private GridView gridView;

    private SimpleItemAdapter<Word> simpleItemAdapter ;

    public RecitingWordListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.reciting_word_list,this);
        this.initGridView();

    }

    private void initGridView(){

        this.gridView = this.findViewById(R.id.reciting_word_list_gridview);

        this.simpleItemAdapter= new SimpleItemAdapter<Word>( LayoutInflater.from(this.getContext())) {
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
                textView.setBackgroundResource(R.drawable.backgrond_squar_grey_stroke);
            }
        };

        this.gridView.setAdapter(simpleItemAdapter);

        this.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Word selectedWord = wordList.get(position);
                if(hasSelected(selectedWord)){
                    removeFormSelected(selectedWord);
                    view.setBackgroundResource(R.drawable.backgrond_squar_grey_stroke);
                }else{
                    addSelected(selectedWord);
                    view.setBackgroundResource(R.drawable.backgrond_squar_blue_stroke);
                }
            }
        });
    }

    private boolean hasSelected(Word selectedWord){
        if(selected == null || selected.isEmpty()){
            return false;
        }

        for(Word word : selected){
            if(word.getId().equals(selectedWord.getId())){
                return true;
            }
        }

        return false;
    }

    private void addSelected(Word selectedWord){
        this.selected.add(selectedWord);
    }

    private void removeFormSelected(Word selectedWord){
        this.selected.remove(selectedWord);
    }

    public void load(List<Word> wordList){
        this.wordList = wordList;
        this.simpleItemAdapter.updateListView(wordList);
    }

    public List<Word> getSelected() {
        return selected;
    }
}
