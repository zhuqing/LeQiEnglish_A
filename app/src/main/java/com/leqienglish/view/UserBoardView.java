package com.leqienglish.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.leqienglish.R;

public class UserBoardView extends RelativeLayout {

    private ImageView userImage;
    private TextView allMinute;
    private TextView allDays;

    public UserBoardView(Context context, AttributeSet attrs) {
        super(context,attrs);
        LayoutInflater.from(context).inflate(R.layout.user_board, this);
        this.userImage = this.findViewById(R.id.user_board_image);
        this.allMinute = this.findViewById(R.id.user_board_recite_minutes);
        this.allDays = this.findViewById(R.id.user_board_recite_days);
    }
}