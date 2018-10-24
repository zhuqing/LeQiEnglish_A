package com.leqienglish.view.recommend;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.leqienglish.R;
import com.leqienglish.activity.content.ArticleInfoActivity;
import com.leqienglish.activity.content.ShowAllContentActiviey;
import com.leqienglish.data.AppRefreshManager;
import com.leqienglish.data.RefreshI;
import com.leqienglish.data.content.RecommendContentDataCache;
import com.leqienglish.util.BundleUtil;
import com.leqienglish.util.LOGGER;
import com.leqienglish.util.LQHandler;
import com.leqienglish.view.adapter.content.ContentItemGridViewAdapter;

import java.util.List;


import xyz.tobebetter.entity.english.Content;

public class RecommendArticle extends RelativeLayout implements RefreshI {
    private LOGGER logger = new LOGGER(RecommendArticle.class);

    public static final  String REFRESH_ID = "RecommendArticle";

    private GridView gridView;
    private TextView showAllTextView;
    private ContentItemGridViewAdapter gridViewAdapter;

    public RecommendArticle(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.recommend_aritcle, this);
        this.gridView = this.findViewById(R.id.recommend_article_gridView);
        this.showAllTextView = this.findViewById(R.id.recommend_article_show_all);

        gridViewAdapter = new ContentItemGridViewAdapter(LayoutInflater.from(this.gridView.getContext()));
        this.gridView.setAdapter(gridViewAdapter);
        init();
    }

    private void init() {

        this.showAllTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();

                intent.setClass(getContext(), ShowAllContentActiviey.class);
                getContext().startActivity(intent);
            }
        });

        this.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Content content = gridViewAdapter.getItem(position);
                if (content == null) {
                    return;
                }

                Intent intent = new Intent();
                intent.putExtras(BundleUtil.create(BundleUtil.DATA, content));
                intent.setClass(getContext(), ArticleInfoActivity.class);
                getContext().startActivity(intent);

            }
        });


        //注册到缓存管理中
        AppRefreshManager.getInstance().regist(REFRESH_ID,this);

    }




    /**
     * 加载数据
     */
    public void load() {

        RecommendContentDataCache.getInstance().load(new LQHandler.Consumer<List<Content>>() {
            @Override
            public void accept(List<Content> contents) {
                showData(contents);
            }
        });

    }


    private void showData(List<Content> contents) {
        logger.d("showData data Content " );
        if(contents == null){
            return;
        }

        logger.d("showData data Content " + contents.size());





        int size = contents.size();
        int length = 170;

        float density = Resources.getSystem().getDisplayMetrics().density;

        int gridviewWidth = (int) (size * (length + 4) * density);
        int itemWidth = (int) (length * density);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                gridviewWidth, LinearLayout.LayoutParams.MATCH_PARENT);
        gridView.setLayoutParams(params); // 设置GirdView布局参数,横向布局的关键
        gridView.setColumnWidth(itemWidth); // 设置列表项宽
        gridView.setHorizontalSpacing(4); // 设置列表项水平间距
        gridView.setStretchMode(GridView.NO_STRETCH);
        gridView.setNumColumns(size); // 设置列数量=列表集合数

        this.gridViewAdapter.updateListView(contents);


    }


    @Override
    public void clearAndRefresh(LQHandler.Consumer<Boolean> fininshed) {
        RecommendContentDataCache.getInstance().clearData();
        load();
    }

    //刷新数据，先把缓存中的数据清空，加载数据
    @Override
    public void refresh(LQHandler.Consumer<Boolean> fininshed) {

        load();
    }
}
