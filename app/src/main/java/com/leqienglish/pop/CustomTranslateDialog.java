package com.leqienglish.pop;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.leqienglish.R;
import com.leqienglish.entity.english.TranslateEntity;
import com.leqienglish.util.LQHandler;
import com.leqienglish.util.TransApiUtil;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;


/**
 * Created by zhuleqi on 2018/2/25.
 */
public class CustomTranslateDialog extends CustomDialog {

    private TextView titleTextView;
    private TextView contentTextView;
    private String selected;


    private  View.OnClickListener onClickListener;
    public CustomTranslateDialog(Context context, View.OnClickListener onClickListener, String selected) {
        super(context);
        this.onClickListener = onClickListener;
        this.selected = selected;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.word_detail_popup);
        Button okButton = this.findViewById(R.id.word_detail_save);
        okButton.setOnClickListener(this.onClickListener);
        this.titleTextView = this.findViewById(R.id.word_detail_title);
        this.contentTextView = this.findViewById(R.id.word_detail_tans);
        this.titleTextView.setText(this.selected);
        okButton.setOnClickListener(this.onClickListener);

    }
    private void loadTrans(){
        TransApiUtil.transResult(this.selected, TransApiUtil.FROM, TransApiUtil.TO, new LQHandler.Consumer<List<TranslateEntity>>() {
            @Override
            public void applay(final List<TranslateEntity> translateEntities) {
                if(translateEntities == null || translateEntities.isEmpty()){
                    return;
                }
                Observable.fromIterable(translateEntities).map(new Function<TranslateEntity, String>() {
                    @Override
                    public String apply(@NonNull TranslateEntity translateEntity) throws Exception {
                        return translateEntity.getDst();
                    }
                }).subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        contentTextView.setText(contentTextView.getText()+"\n"+s);
                    }
                });

            }
        });
    }

}
