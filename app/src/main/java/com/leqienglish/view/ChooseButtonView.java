package com.leqienglish.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.leqienglish.util.LQHandler;
import com.leqienglish.util.sourceitem.SourceItem;

import java.util.List;

public class ChooseButtonView extends LinearLayout {
    private LQHandler.Consumer<SourceItem> selectedConsumer;
    private List<SourceItem> sourceItems;

    public ChooseButtonView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.setOrientation(HORIZONTAL);


    }

    public void load(List<SourceItem> texts){
        this.sourceItems = texts;
        for(SourceItem text : texts){
            Button button = new Button(this.getContext());
            LinearLayout.LayoutParams  layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.weight = 1;
            layoutParams.setMargins(-8,0,-8,0);
            button.setLayoutParams(layoutParams);

            button.setText(text.getDisplay());
            this.addView(button);
            button.setTag(text);
            button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(selectedConsumer == null){
                        return;
                    }

                    SourceItem s = (SourceItem) v.getTag();
                    selectedConsumer.accept(s);

                }
            });
        }
    }


    public LQHandler.Consumer<SourceItem> getSelectedConsumer() {
        return selectedConsumer;
    }

    public void setSelectedConsumer(LQHandler.Consumer<SourceItem> selectedConsumer) {
        this.selectedConsumer = selectedConsumer;
    }
}
