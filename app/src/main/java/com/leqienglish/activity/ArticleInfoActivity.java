package com.leqienglish.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.leqienglish.R;
import com.leqienglish.controller.article.ArticleInfoController;
import com.leqienglish.util.BundleUtil;

import xyz.tobebetter.entity.english.Content;

/**
 * 文章详情，段列表
 */
public class ArticleInfoActivity extends AppCompatActivity {

    private Content content;
    private TextView titleView;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_recite_article_info);
        content = (Content) this.getIntent().getExtras().getSerializable(BundleUtil.DATA);
        View view = this.findViewById(R.id.add_recite_article_info_root);

        ArticleInfoController articleInfoController = new ArticleInfoController(view);
        articleInfoController.init();
        articleInfoController.setContent(content);


    }


}
