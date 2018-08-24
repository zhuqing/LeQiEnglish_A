package com.leqienglish.popwindow;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.leqienglish.R;
import com.leqienglish.activity.word.WordInfoActivity;
import com.leqienglish.data.word.WordInfoDataCache;
import com.leqienglish.pop.CustomDialog;
import com.leqienglish.util.BundleUtil;
import com.leqienglish.util.LQHandler;
import com.leqienglish.view.word.WordInfoView;

import xyz.tobebetter.entity.word.Word;

public class WordInfoPopupWindow extends PopupWindow {

    private WordInfoView wordInfoView;
    private Button checkInfo;

    private Word word;


    public WordInfoPopupWindow(Context context) {
        super(context);
        View view = LayoutInflater.from(context).inflate(R.layout.word_info_pop, null);
        this.setContentView(view);
        init(view);

    }

    public void load(String word){

        WordInfoDataCache.getInstance(word).load(new LQHandler.Consumer<Word>() {
            @Override
            public void accept(Word word) {
                if(wordInfoView== null){
                    return;
                }
                WordInfoPopupWindow.this.word = word;
                wordInfoView.load(word);
                wordInfoView.play();
            }
        });

        clear();
    }


    public void clear(){
        WordInfoPopupWindow.this.word = null;
        wordInfoView.load(null);
    }



    protected void init(View view) {


        wordInfoView = view.findViewById(R.id.word_info_pop_word_info);
        checkInfo = view.findViewById(R.id.word_info_pop_checkinfo);
        this.setHeight(RelativeLayout.LayoutParams.WRAP_CONTENT);
        this.setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);
        this.setAnimationStyle(R.style.count_down_popwindow);
        this.setOutsideTouchable(true);
        this.initListener();
    }


    private void initListener(){
        this.checkInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(word == null){
                    return;
                }

                Intent intent = new Intent();
                intent.putExtras(BundleUtil.create(BundleUtil.DATA, word));
                intent.setClass(v.getContext(), WordInfoActivity.class);
                v.getContext().startActivity(intent);
            }


        });
    }

}
