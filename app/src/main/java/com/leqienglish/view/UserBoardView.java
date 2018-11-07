package com.leqienglish.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.leqienglish.R;
import com.leqienglish.activity.word.MyReciteWordsInfoActivity;
import com.leqienglish.data.AppRefreshManager;
import com.leqienglish.data.RefreshI;
import com.leqienglish.data.user.UserDataCache;
import com.leqienglish.data.user.UserReciteRecordDataCache;
import com.leqienglish.sf.LoadFile;
import com.leqienglish.util.LQHandler;

import xyz.tobebetter.entity.user.User;
import xyz.tobebetter.entity.user.recite.UserReciteRecord;

public class UserBoardView extends RelativeLayout implements RefreshI{

    public final static String REFRESH_ID = "UserBoardView";

    private ImageView userImage;
    private TextView allMinute;
    private TextView allDays;
    private Button reciteWordsButton;


    public UserBoardView(Context context, AttributeSet attrs) {
        super(context,attrs);
        LayoutInflater.from(context).inflate(R.layout.user_board, this);
        this.userImage = this.findViewById(R.id.user_board_image);
        this.allMinute = this.findViewById(R.id.user_board_recite_minutes);
        this.allDays = this.findViewById(R.id.user_board_recite_days);
        this.reciteWordsButton = this.findViewById(R.id.user_board_recite_words);
        this.initListener();

        AppRefreshManager.getInstance().regist(REFRESH_ID,this);
    }

    private void initListener(){
        this.reciteWordsButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();

                intent.setClass(getContext(), MyReciteWordsInfoActivity.class);
                getContext().startActivity(intent);
            }
        });
    }

    public void load(){

        UserReciteRecordDataCache.getInstance().load(new LQHandler.Consumer<UserReciteRecord>() {
            @Override
            public void accept(UserReciteRecord userReciteRecord) {
                if(userReciteRecord == null){
                    return;
                }
                allMinute.setText(userReciteRecord.getLearnTime()+"");
                allDays.setText("累计打卡"+userReciteRecord.getLearnDay()+"天");
            }
        });


        loadImage();

    }

    private void loadImage(){
        User user = UserDataCache.getInstance().getUser();

        if(user == null || user.getImagePath() == null){
            return;
        }

        LoadFile.loadFile(user.getImagePath(),(path)->{
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            userImage.setImageBitmap(bitmap);

        });
    }


    @Override
    public void clearAndRefresh(LQHandler.Consumer<Boolean> fininshed) {
        UserReciteRecordDataCache.getInstance().clearData();

        this.load();
    }

    @Override
    public void refresh(LQHandler.Consumer<Boolean> fininshed) {
        this.load();

    }
}