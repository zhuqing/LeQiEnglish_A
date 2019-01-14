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

    private String itemId;
    private String title;
    private int imageId;

    private Drawable image;


    private ImageView button;
    private TextView titleTextView;

    public OperationBarItem(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.operation_bar_item, this);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.OperaiontBarItem);
        this.image = typedArray.getDrawable(R.styleable.OperaiontBarItem_image);
        this.title = typedArray.getString(R.styleable.OperaiontBarItem_title);

        typedArray.recycle();

        this.init();
    }

    private void init(){
        this.button = this.findViewById(R.id.operation_bar_item_button);
        this.titleTextView = this.findViewById(R.id.operation_bar_item_title);

        this.button.setImageDrawable(this.image);
        this.titleTextView.setText(this.title);

    }

    @Override
    public Drawable getBackground() {
        return image;
    }

    @Override
    public void setBackground(Drawable background) {
        this.image = background;
        if(this.button!=null && this.image !=null){
            this.button.setBackground(this.image);
        }
    }

    /**
     * 更新图标
     * @param id
     */
    public void updateImage(int id){
        this.button.setImageResource(id);
    }

    public String getTitle() {
        return title;
    }

    /**
     * 更新标题
     * @param title
     */
    public void updateTitle(String title) {
        this.title = title;
        if(this.title == null){
            this.title = "";
        }
        if(this.titleTextView != null){

            this.titleTextView.setText(title);
        }
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }
}
