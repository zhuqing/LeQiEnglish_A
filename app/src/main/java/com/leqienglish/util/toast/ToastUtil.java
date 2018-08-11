package com.leqienglish.util.toast;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
    public static  void showShort(Context context,int resId){
        Toast.makeText(context,resId,Toast.LENGTH_SHORT).show();
    }

    public static  void showShort(Context context,String message){
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
    }
}
