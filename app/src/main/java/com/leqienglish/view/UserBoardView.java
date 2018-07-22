package com.leqienglish.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.leqienglish.R;
import com.leqienglish.sf.LQService;
import com.leqienglish.util.LQHandler;

import java.util.HashMap;
import java.util.Map;


import xyz.tobebetter.entity.user.User;
import xyz.tobebetter.entity.user.recite.UserReciteRecord;

public class UserBoardView extends RelativeLayout {

    private ImageView userImage;
    private TextView allMinute;
    private TextView allDays;

    private User user;

    public UserBoardView(Context context, AttributeSet attrs) {
        super(context,attrs);
        LayoutInflater.from(context).inflate(R.layout.user_board, this);
        this.userImage = this.findViewById(R.id.user_board_image);
        this.allMinute = this.findViewById(R.id.user_board_recite_minutes);
        this.allDays = this.findViewById(R.id.user_board_recite_days);
    }

    public void load(){

        if(this.getUser() == null){
            return;
        }

        Map<String,String> param = new HashMap<>();
        param.put("userId",this.getUser().getId());
        LQService.get("/userReciteRecord/findByUserId", UserReciteRecord.class, param, new LQHandler.Consumer<UserReciteRecord>() {
            @Override
            public void accept(UserReciteRecord userReciteRecord) {
                allMinute.setText(userReciteRecord.getLearnTime()+"");
                allDays.setText(userReciteRecord.getLearnDay()+"");
            }
        });

    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        this.load();
    }
}