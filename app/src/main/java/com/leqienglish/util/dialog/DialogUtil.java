package com.leqienglish.util.dialog;

import android.content.Context;
import android.support.v7.app.AlertDialog;

import com.leqienglish.R;

public class DialogUtil {

    public static void show(int title,Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(title)
                .setNegativeButton(R.string.close, null).create();

        builder.show();
    }
}
