package com.leqienglish.view.aricle;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.leqienglish.R;

public class UserRecitingArticle extends RelativeLayout {

    private Button addArticle;

    private TextView showAllRecitingArticle;

    private GridView recitingArticles;

    public UserRecitingArticle(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.user_reciting_article, this);
        this.addArticle = this.findViewById(R.id.user_reciting_add_button);
    }
}
