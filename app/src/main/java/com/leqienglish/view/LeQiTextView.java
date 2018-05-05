package com.leqienglish.view;


import com.leqienglish.util.LOGGER;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.text.Layout;
import android.text.Selection;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.EditText;

public class LeQiTextView extends android.support.v7.widget.AppCompatEditText {
    public static LOGGER logger = new LOGGER(LeQiTextView.class);
    private int off; //字符串的偏移值

    public LeQiTextView(Context context) {
        this(context, null);
        // TODO Auto-generated constructor stub

    }

    public LeQiTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LeQiTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();

    }

    public String getSelectText() {
        if (!this.hasSelection()) {
            return null;
        }

        StringBuffer selectStr = new StringBuffer();
        logger.v("this.getSelectionStart()=" + this.getSelectionStart());
        logger.v("this.getSelectionEnd()=" + this.getSelectionEnd());
        int start = this.getSelectionStart();
        int end = this.getSelectionEnd();
        if (start > end) {
            int temp = start;
            start = end;
            end = temp;
        }
        getPreStr(start - 1, selectStr);
        selectStr.append(this.getText().toString().subSequence(start, end).toString());
        getRemindStr(end, selectStr);

        return selectStr.toString();
    }

    private void getPreStr(int startIndex, StringBuffer startStr) {
        if (startIndex < 0) {
            return;
        }
        char currentChar = this.getText().charAt(startIndex);
        if (currentChar >= 'A' && currentChar <= 'z' || currentChar == '-') {
            startStr.insert(0, currentChar);
            getPreStr(startIndex - 1, startStr);
        }
    }

    private void getRemindStr(int endIndex, StringBuffer endStr) {
        if (endIndex >= this.getText().toString().length()) {
            return;
        }
        char currentChar = this.getText().charAt(endIndex);
        if (currentChar >= 'A' && currentChar <= 'z' || currentChar == '-') {
            endStr.append(currentChar);
            getRemindStr(endIndex + 1, endStr);
        }

    }

    private void initialize() {
        setGravity(Gravity.TOP);
        setBackgroundColor(Color.WHITE);
    }

    @Override
    protected void onCreateContextMenu(ContextMenu menu) {
        //不做任何处理，为了阻止长按的时候弹出上下文菜单
    }

    @Override
    public boolean getDefaultEditable() {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        Layout layout = getLayout();
        int line = 0;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                line = layout.getLineForVertical(getScrollY() + (int) event.getY());
                off = layout.getOffsetForHorizontal(line, (int) event.getX());
                Selection.setSelection(getEditableText(), off);
                break;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                line = layout.getLineForVertical(getScrollY() + (int) event.getY());
                int curOff = layout.getOffsetForHorizontal(line, (int) event.getX());
                Selection.setSelection(getEditableText(), off, curOff);
                break;
        }
        return true;
    }

}