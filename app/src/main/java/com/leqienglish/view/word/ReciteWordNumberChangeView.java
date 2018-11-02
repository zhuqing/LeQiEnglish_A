package com.leqienglish.view.word;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.leqienglish.R;

import xyz.tobebetter.entity.word.ReciteWordConfig;

public class ReciteWordNumberChangeView extends RelativeLayout {
    private ReciteWordConfig reciteWordConfig;

    private EditText editText;

    public ReciteWordNumberChangeView(Context context) {
        super(context, null);
        LayoutInflater.from(context).inflate(R.layout.update_recite_words_number, this);
        this.init();
    }


    private void init(){
        this.editText = findViewById(R.id.update_recite_words_number_num);


    }

    public void setItem(ReciteWordConfig reciteWordConfig){
        this.reciteWordConfig = reciteWordConfig;
        this.reload();
    }

    /**
     * 如果文本有内容，获取最新的值
     * @return
     */
    public ReciteWordConfig getReciteWordConfig() {
        if(editText!=null){
          String number =  editText.getText().toString().trim();
          if(number.isEmpty()){//默认值是10
              number = "10";
          }

          reciteWordConfig.setReciteNumberPerDay(Integer.valueOf(number));
        }
        return reciteWordConfig;
    }

    private void reload(){
        if(this.reciteWordConfig == null||reciteWordConfig.getReciteNumberPerDay() == null){
            this.editText.setText("");
            return;
        }

        this.editText.setText(reciteWordConfig.getReciteNumberPerDay().toString());
    }
}
