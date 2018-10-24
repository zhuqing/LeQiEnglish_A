package com.leqienglish.pop;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;

import com.leqienglish.R;

/**
 * Created by zhuleqi on 2018/2/25.
 */
public class CustomDialog extends Dialog {
  //  private Activity activity;

    private int marginBottom ;

    public CustomDialog(Context context) {
        super(context, R.style.ActionSheetStyle);
       // this.setActivity(context);
        marginBottom = dp2px(10);
    }

    private int dp2px(float dipValue) {
        float scale = this.getContext().getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    public void showDialog(){
        getWindow().setGravity(Gravity.BOTTOM);
        getWindow().getAttributes().y = marginBottom;
        show();

        setCancelable(true);
        setCanceledOnTouchOutside(true);


    }




}
