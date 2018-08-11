package com.leqienglish.controller;

import android.view.View;
import android.widget.TextView;

import com.leqienglish.R;
import com.leqienglish.data.user.UserDataCache;

import com.leqienglish.util.LQHandler;
import com.leqienglish.view.UserBoardView;
import com.leqienglish.view.article.UserRecitingArticleView;
import com.leqienglish.view.recommend.RecommendArticle;
import com.leqienglish.view.recommend.RecommendBook;


import xyz.tobebetter.entity.user.User;

/**
 * private
 * Created by zhuqing on 2017/8/19.
 */

public class HomeListViewController extends ControllerAbstract<View> {
    private UserBoardView userBoardView;

    private UserRecitingArticleView userRecitingArticleView;

    private RecommendArticle recommendArticle;

    private RecommendBook recommendBook;

    private TextView showAllContent;


    public HomeListViewController(View fragment) {
        super(fragment);
        // fragment.getResources().getString(R.string.HOST);
        //  this.imagePath = fragment.getResources().getString(R.string.HOST)+fragment.getResources().getString(R.string.IMAGE_PATH);
    }


    @Override
    public void init() {

        this.userBoardView = this.getView().findViewById(R.id.home_view_user_board);
        this.userRecitingArticleView = this.getView().findViewById(R.id.home_view_user_reciting);

        this.recommendArticle = this.getView().findViewById(R.id.home_view_recommend_article);
        this.recommendBook = this.getView().findViewById(R.id.home_view_recommend_book);

        loadUser();

    }

    @Override
    public void reload() {

    }


    public void loadUser(){
        UserDataCache.getInstance().load(new LQHandler.Consumer<User>() {
            @Override
            public void accept(User user) {
                recommendArticle.load();
                userBoardView.load();
                userRecitingArticleView.load();

            }
        });

    }
    @Override
    public void destory() {

    }

}
