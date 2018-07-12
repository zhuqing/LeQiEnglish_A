package com.leqienglish.view.recommend;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.leqienglish.R;

public class RecommendArticle  extends RelativeLayout {
    public RecommendArticle(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.recommend_aritcle, this);
    }
}
