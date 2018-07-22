package com.leqienglish.controller;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leqienglish.R;
import com.leqienglish.activity.LoadingActivity;
import com.leqienglish.activity.PlayAudioActivity;
import com.leqienglish.database.ExecuteSQL;

import com.leqienglish.entity.SQLEntity;

import com.leqienglish.sf.LQService;
import com.leqienglish.util.BundleUtil;
import com.leqienglish.util.FileUtil;
import com.leqienglish.util.LQHandler;
import com.leqienglish.view.UserBoardView;
import com.leqienglish.view.article.UserRecitingArticle;
import com.leqienglish.view.recommend.RecommendArticle;
import com.leqienglish.view.recommend.RecommendBook;

import org.springframework.http.MediaType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


import xyz.tobebetter.entity.english.Content;
import xyz.tobebetter.entity.user.User;

import static com.leqienglish.database.Constants.USER_TYPE;
import static java.util.Arrays.*;

/**
 * private
 * Created by zhuqing on 2017/8/19.
 */

public class HomeListViewController extends Controller<View> {
    private UserBoardView userBoardView;

    private UserRecitingArticle userRecitingArticle;

    private RecommendArticle recommendArticle;

    private RecommendBook recommendBook;


    public HomeListViewController(View fragment) {
        super(fragment);
        // fragment.getResources().getString(R.string.HOST);
        //  this.imagePath = fragment.getResources().getString(R.string.HOST)+fragment.getResources().getString(R.string.IMAGE_PATH);
    }


    @Override
    public void init() {

        this.userBoardView = this.getView().findViewById(R.id.home_view_user_board);
        this.userRecitingArticle = this.getView().findViewById(R.id.home_view_user_reciting);

        this.recommendArticle = this.getView().findViewById(R.id.home_view_recommend_article);
        this.recommendBook = this.getView().findViewById(R.id.home_view_recommend_book);

        findUser();

    }



    public void findUser(){
        ExecuteSQL.getInstance().getDatasByType(USER_TYPE, new LQHandler.Consumer<List<SQLEntity>>() {
            @Override
            public void accept(List<SQLEntity> sqlEntities) {
                try {
                    List<User> users = ExecuteSQL.toEntity(sqlEntities,User.class);
                    if(users.isEmpty()){
                        return;
                    }

                    User user = users.get(0);
                    recommendArticle.setUser(user);

                    userBoardView.setUser(user);

                    userRecitingArticle.setUser(user);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }


}
