package com.leqienglish.popwindow;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.leqienglish.R;


/**
 * Created by zhuleqi on 2018/2/11.
 */
public class WordDetailPopupWindow extends PopupWindow {
    private Button save;
    private TextView title;

    public WordDetailPopupWindow(Context context,String title) {
        super(context);

        //xyz.leqisoft.R.anim.abc_popup_enter;
        View view = LayoutInflater.from(context).inflate(R.layout.word_detail_popup, null);
        this.setContentView(view);
        this.save = view.findViewById(R.id.word_detail_save);
        this.title = view.findViewById(R.id.word_detail_title);
        this.title.setText(title);

        // 设置弹出窗体的宽和高
        this.setHeight(RelativeLayout.LayoutParams.WRAP_CONTENT);
        this.setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);

        // 设置弹出窗体可点击
        this.setFocusable(true);

        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        // 设置弹出窗体的背景
        this.setBackgroundDrawable(dw);

        // 设置弹出窗体显示时的动画，从底部向上弹出
        this.setAnimationStyle(R.style.count_down_popwindow);
        this.initSave();

    }

    private void initSave() {
        this.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
