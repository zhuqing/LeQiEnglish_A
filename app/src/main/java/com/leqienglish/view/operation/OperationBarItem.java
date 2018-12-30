package com.leqienglish.view.operation;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leqienglish.R;

public class OperationBarItem extends LinearLayout {
    private Drawable background;
    private String title;

    private ImageView button;
    private TextView titleTextView;

    public OperationBarItem(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.operation_bar_item, this);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.OperaiontBarItem);
        this.background = typedArray.getDrawable(R.styleable.OperaiontBarItem_background_image);
        this.title = typedArray.getString(R.styleable.OperaiontBarItem_title);

        typedArray.recycle();

        this.init();
    }

    private void init(){
        this.button = this.findViewById(R.id.operation_bar_item_button);
        this.titleTextView = this.findViewById(R.id.operation_bar_item_title);

        this.button.setImageDrawable(this.background);
        this.titleTextView.setText(this.title);

    }

    @Override
    public Drawable getBackground() {
        return background;
    }

    @Override
    public void setBackground(Drawable background) {
        this.background = background;
        if(this.button!=null && this.background !=null){
            this.button.setBackground(this.background);
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        if(this.title == null){
            this.title = "";
        }
        if(this.titleTextView != null){

            this.titleTextView.setText(title);
        }
    }
}
