package com.leqienglish.pop;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.leqienglish.R;
import com.leqienglish.activity.word.WordInfoActivity;
import com.leqienglish.data.word.WordInfoDataCache;
import com.leqienglish.util.BundleUtil;
import com.leqienglish.util.LQHandler;
import com.leqienglish.view.word.WordInfoView;

import xyz.tobebetter.entity.word.Word;

public class WordInfoDialog extends CustomDialog {

    private WordInfoView wordInfoView;
    private Button checkInfo;

    private Word word;


    public WordInfoDialog(Context context) {
        super(context);

    }

    private void load(String word){

        WordInfoDataCache.getInstance(word).load(new LQHandler.Consumer<Word>() {
            @Override
            public void accept(Word word) {
                if(wordInfoView== null){
                    return;
                }
                WordInfoDialog.this.word = word;
                wordInfoView.load(word);
                wordInfoView.play();
            }
        });
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.word_info_pop);

        wordInfoView = this.findViewById(R.id.word_info_pop_word_info);
        checkInfo = this.findViewById(R.id.word_info_pop_checkinfo);

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
                intent.setClass(getContext(), WordInfoActivity.class);
                getContext().startActivity(intent);
            }


        });
    }

}
