package com.leqienglish.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.leqienglish.R;
import com.leqienglish.controller.article.ArticleInfoController;

import com.leqienglish.data.user.UserDataCache;
import com.leqienglish.util.BundleUtil;
import com.leqienglish.util.LQHandler;
import com.leqienglish.util.image.ImageUtil;

import xyz.tobebetter.entity.english.Content;
import xyz.tobebetter.entity.user.User;

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
        UserDataCache.getInstance().load(new LQHandler.Consumer<User>() {
            @Override
            public void accept(User user) {
                articleInfoController.setUser(user);
            }
        });

        setBackground(view);

    }

    private void setBackground(View rootView){
        Bitmap sbit = BitmapFactory.decodeResource(this.getResources(),R.drawable.obm);
        Bitmap bitmap = ImageUtil.fastBlur(sbit,30);
        Drawable drawable =new  BitmapDrawable(bitmap);
        rootView.setBackground(drawable);
    }


}
