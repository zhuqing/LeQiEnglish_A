package com.leqienglish.controller;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.leqienglish.R;
import com.leqienglish.activity.ArticleInfoActivity;
import com.leqienglish.sf.LQService;
import com.leqienglish.util.BundleUtil;
import com.leqienglish.util.LQHandler;
import com.leqienglish.view.adapter.content.ContentItemGridViewAdapter;

import java.util.Arrays;

import xyz.tobebetter.entity.english.Content;

public class ShowAllContentController extends ControllerAbstract {

    private GridView catalogGridView;

    private ContentItemGridViewAdapter contentItemGridViewAdapter;

    private GridView contentsGridView;

    public ShowAllContentController(View view) {
        super(view);
    }

    @Override
    public void init() {

        catalogGridView = this.getView().findViewById(R.id.show_all_content_catalogs);

        this.contentsGridView = this.getView().findViewById(R.id.show_all_content_contents);

        contentItemGridViewAdapter = new ContentItemGridViewAdapter(LayoutInflater.from(this.getView().getContext()));
        this.contentsGridView.setAdapter(contentItemGridViewAdapter);

        this.initListener();
    }

    private void initListener(){
        this.contentsGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Content content = contentItemGridViewAdapter.getItem(position);
                if (content == null) {
                    return;
                }

                Intent intent = new Intent();
                intent.putExtras(BundleUtil.create(BundleUtil.DATA, content));
                intent.setClass(getView().getContext(), ArticleInfoActivity.class);
                getView().getContext().startActivity(intent);
            }
        });
    }

    public void search(String text){

        LQService.get("/english/content/findAll", Content[].class, null, new LQHandler.Consumer<Content[]>() {
            @Override
            public void accept(Content[] contents) {
                contentItemGridViewAdapter.updateListView(Arrays.asList(contents));
            }
        });
    }

    @Override
    public void reload() {

    }

    @Override
    public void destory() {

    }
}
