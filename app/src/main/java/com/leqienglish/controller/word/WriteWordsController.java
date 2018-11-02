package com.leqienglish.controller.word;

import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;

import com.leqienglish.activity.word.ReciteWordsReviewActivity;
import com.leqienglish.util.TaskUtil;

public class WriteWordsController extends ReciteWordsController {
    public WriteWordsController(View view) {
        super(view);
    }

    protected void initListener() {
        super.initListener();
        tipTextView.setVisibility(View.VISIBLE);
        wordInfoFrame.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    tipTextView.setVisibility(View.GONE);
                }

                TaskUtil.runlater((t)->tipTextView.setVisibility(View.VISIBLE),3000L);

                return false;
            }
        });
    }

    @Override
    protected void reciteFilished() {
        Intent intent = new Intent();

        intent.setClass(getView().getContext(), ReciteWordsReviewActivity.class);

        getView().getContext().startActivity(intent);
    }
}
