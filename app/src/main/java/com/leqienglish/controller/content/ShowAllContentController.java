package com.leqienglish.controller.content;

import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.leqienglish.R;
import com.leqienglish.activity.ArticleInfoActivity;
import com.leqienglish.controller.ControllerAbstract;
import com.leqienglish.data.catalog.CatalogCache;
import com.leqienglish.sf.LQService;
import com.leqienglish.util.BundleUtil;
import com.leqienglish.util.LQHandler;
import com.leqienglish.util.string.StringUtil;
import com.leqienglish.view.adapter.content.ContentItemGridViewAdapter;
import com.leqienglish.view.adapter.simpleitem.SimpleItemAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xyz.tobebetter.entity.english.Catalog;
import xyz.tobebetter.entity.english.Content;

public class ShowAllContentController extends ControllerAbstract {

    private GridView catalogGridView;

    private SimpleItemAdapter<Catalog> simpleItemAdapter;

    private ContentItemGridViewAdapter contentItemGridViewAdapter;

    private GridView contentsGridView;


    /**
     * 搜索分类
     */

    private Catalog searchCatalog;
    /**
     * 搜索文本
     */
    private String searchText;

    public ShowAllContentController(View view) {
        super(view);
    }

    @Override
    public void init() {

        catalogGridView = this.getView().findViewById(R.id.show_all_content_catalogs);

        this.simpleItemAdapter = new SimpleItemAdapter<Catalog>(LayoutInflater.from(this.getView().getContext())) {
            @Override
            protected String toString(Catalog catalog) {
                if(catalog == null){
                    return "";
                }
                return catalog.getTitle();
            }

            @Override
            protected void setStyle(TextView textView) {
                textView.setGravity(Gravity.CENTER);
            }


        };

        catalogGridView.setAdapter(simpleItemAdapter);

        this.contentsGridView = this.getView().findViewById(R.id.show_all_content_contents);

        contentItemGridViewAdapter = new ContentItemGridViewAdapter(LayoutInflater.from(this.getView().getContext()));
        this.contentsGridView.setAdapter(contentItemGridViewAdapter);
        this.searchCatalog = this.createAllCatalog();

        this.initListener();
        this.load();
    }

    private void load(){
        CatalogCache.getInstance().load(new LQHandler.Consumer<List<Catalog>>() {
            @Override
            public void accept(List<Catalog> catalogs) {

                List<Catalog> list = new ArrayList<>();
                list.add(createAllCatalog());
                list.addAll(catalogs);
                simpleItemAdapter.updateListView(list);
            }
        });
    }

    private Catalog createAllCatalog(){
        Catalog catalog = new Catalog();
        catalog.setTitle("全部");
        return catalog;
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

        this.catalogGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                searchCatalog = simpleItemAdapter.getItem(position);
                search(searchText);
            }
        });
    }

    public void search(String text){
        this.searchText = text;
        Map<String,String> param = new HashMap<String,String>();
        if(!StringUtil.isNullOrEmpty(searchCatalog.getId())) {
            param.put("catalogId", this.searchCatalog.getId());
        }
        if(!StringUtil.isNullOrEmpty(searchText)){
            param.put("title", searchText);
        }

        LQService.get("/english/content/findContentsByCatalogIdAndTitle", Content[].class, param, new LQHandler.Consumer<Content[]>() {
            @Override
            public void accept(Content[] contents) {
                if(contents == null){
                    contentItemGridViewAdapter.updateListView(null);
                }else {
                    contentItemGridViewAdapter.updateListView(Arrays.asList(contents));
                }
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
