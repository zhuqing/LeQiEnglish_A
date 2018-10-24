package com.leqienglish.pop.actionsheet;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leqienglish.R;

import java.util.ArrayList;

public class ActionSheet extends Dialog {
    private Context context;

    private LinearLayout parentLayout;
    private TextView titleTextView;
    private ArrayList<String> buttonTitles;

    private String cancelTitle = "取消";

    private Button cancelButton;

    private View customeView;



    // 弹出框距离底部的高度
    private int marginBottom;

    // 取消按钮点击回调
    private View.OnClickListener cancelListener;

    // 选择项点击回调列表
    private ArrayList<View.OnClickListener> buttonListenerList;

    public ActionSheet(Context context) {
        super(context, R.style.ActionSheetStyle);
        init(context);
    }

    public ActionSheet(Context context, int theme) {
        super(context, theme);
        init(context);
    }

    private void init(Context context) {
        this.context = context;

        marginBottom = dp2px(10);
        buttonTitles = new ArrayList<>();
        buttonListenerList = new ArrayList<>();
    }

    private ActionSheet createDialog() {
        parentLayout = createRoot();

        if(this.customeView!=null){
            parentLayout.addView(this.customeView);
        }



        for (int i = 0; i < buttonTitles.size(); i++) {
            Button button = createButton(buttonTitles.get(i),R.drawable.background_green_corner,buttonListenerList.get(i));

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams
                    (ViewGroup.LayoutParams.MATCH_PARENT,  dp2px(40));
            layoutParams.setMargins(dp2px(20), dp2px(10), dp2px(20), 0);
            parentLayout.addView(button, layoutParams);


        }

        cancelButton = createButton(cancelTitle,R.drawable.background_red_corner,cancelListener);
        LinearLayout.LayoutParams cancelParams = new LinearLayout.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT, dp2px(40));
        cancelParams.setMargins(dp2px(20), dp2px(20), dp2px(20), dp2px(30));
        parentLayout.addView(cancelButton, cancelParams);



        getWindow().setGravity(Gravity.BOTTOM);
        setContentView(parentLayout);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        try{
            show();
        }catch (Exception e){

        }


        return this;
    }

    private Button createButton(String title,int background,View.OnClickListener listener){
        Button button = new Button(context);
        button.setGravity(Gravity.CENTER);
        button.setText(title);
        button.setTextColor(Color.parseColor("#FFFFFF"));
        button.setTextSize(18);
        button.setBackgroundResource(background);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if(listener != null){
                    listener.onClick(v);
                }
            }
        });
        return button;
    }

    private LinearLayout createRoot(){
        LinearLayout parentLayout = new LinearLayout(context);
        parentLayout.setBackgroundColor(Color.parseColor("#ffffff"));
        parentLayout.setOrientation(LinearLayout.VERTICAL);
        parentLayout.setMinimumWidth(100000);

        return parentLayout;

    }

    private void addButtons(String text, View.OnClickListener listener) {
        buttonTitles.add(text);
        buttonListenerList.add(listener);
    }

    public void setCancelTitle(String text) {
        this.cancelTitle = text;
    }



    public void setMargin(int bottom) {
        this.marginBottom = dp2px(bottom);
    }

    public void addCancelListener(View.OnClickListener listener) {
        this.cancelListener = listener;
    }

    private int dp2px(float dipValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public void setCustomeView(View customeView) {
        this.customeView = customeView;
    }

    public static class DialogBuilder {
        ActionSheet dialog;

        public DialogBuilder(Context context) {
            dialog = new ActionSheet(context);
        }

        /**
         * 添加一个选择项
         * @param text 选择项文字
         * @param listener 选择项点击回调监听
         * @return 当前DialogBuilder
         */
        public DialogBuilder addButton(String text, View.OnClickListener listener) {
            dialog.addButtons(text, listener);
            return this;
        }

        /**
         * 添加一个选择项
         * @param text 选择项文字
         * @param listener 选择项点击回调监听
         * @return 当前DialogBuilder
         */
        public DialogBuilder addCloseButton(String text, View.OnClickListener listener) {
            dialog.setCancelTitle(text);
            dialog.addCancelListener(listener);
            return this;
        }


        /**
         * 设置标题
         * @param text 文字内容
         * @return 当前DialogBuilder
         */
        public DialogBuilder setTitle(String text) {
            dialog.setTitle(text);
            return this;
        }


        /**
         * 设置弹出框距离底部的高度
         * @param bottom 距离值，单位dp
         * @return 当前DialogBuilder
         */
        public DialogBuilder setMargin(int bottom) {
            dialog.setMargin(bottom);
            return this;
        }


        public DialogBuilder setCustomeView(View customeView){
            dialog.setCustomeView(customeView);
            return this;
        }

        /**
         * 创建弹出框，放在最后执行
         * @return 创建的 ActionSheet 实体
         */
        public ActionSheet create() {
            return dialog.createDialog();
        }
    }
}

