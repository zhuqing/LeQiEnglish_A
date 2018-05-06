package com.leqienglish.pop;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by zhuleqi on 2018/2/25.
 */
public class CustomDialog extends Dialog {
  //  private Activity activity;

    public CustomDialog(Context context) {
        super(context);
       // this.setActivity(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window dialogWindow = this.getWindow();

     //   WindowManager m = this.getActivity().getWindowManager();
      //  Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        // p.height = (int) (d.getHeight() * 0.6); // 高度设置为屏幕的0.6
        p.width = (int) (200); // 宽度设置为屏幕的0.8
      //  p.w
        dialogWindow.setAttributes(p);
    }

//    public Activity getActivity() {
//        return activity;
//    }
//
//    public void setActivity(Activity activity) {
//        this.activity = activity;
//    }


}
