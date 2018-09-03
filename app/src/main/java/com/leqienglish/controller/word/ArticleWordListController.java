package com.leqienglish.controller.word;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.leqienglish.R;
import com.leqienglish.activity.word.WordInfoActivity;
import com.leqienglish.controller.ControllerAbstract;
import com.leqienglish.data.word.ContentWordsDataCache;
import com.leqienglish.util.BundleUtil;
import com.leqienglish.util.LQHandler;
import com.leqienglish.view.adapter.LeQiBaseAdapter;
import com.leqienglish.view.word.WordInfoView;

import java.util.Collections;
import java.util.List;

import xyz.tobebetter.entity.english.Content;
import xyz.tobebetter.entity.word.Word;

public class ArticleWordListController extends ControllerAbstract {

    private Content content;

    private ListView listView;

    private ContentWordsDataCache contentWordsDataCache;

    private ArticleWordListViewAdapter articleWordListViewAdapter;



    public ArticleWordListController(View view,Content content) {
        super(view);
        this.content = content;
    }

    @Override
    public void init() {

      listView =  this.getView().findViewById(R.id.article_word_list_listview);
      this.articleWordListViewAdapter = new ArticleWordListViewAdapter(LayoutInflater.from(this.getView().getContext()));

      listView.setAdapter(articleWordListViewAdapter);

      this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
          @Override
          public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
              Word word = articleWordListViewAdapter.getItem(position);
              if (content == null) {
                  return;
              }

              Intent intent = new Intent();
              intent.putExtras(BundleUtil.create(BundleUtil.DATA, word));
              intent.setClass(getView().getContext(), WordInfoActivity.class);
              getView().getContext().startActivity(intent);
          }
      });

      this.contentWordsDataCache = new ContentWordsDataCache(content);

      this.reload();
    }





    @Override
    public void reload() {
        this.contentWordsDataCache.load(new LQHandler.Consumer<List<Word>>() {
            @Override
            public void accept(List<Word> words) {
                if(words == null){
                    words = Collections.EMPTY_LIST;
                }

                articleWordListViewAdapter.updateListView(words);
            }
        });
    }

    @Override
    public void destory() {

    }

    private class  ArticleWordListViewAdapter extends LeQiBaseAdapter<Word>{

        public ArticleWordListViewAdapter(LayoutInflater mInflater) {
            super(mInflater);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = new WordInfoView(ArticleWordListController.this.getView().getContext(),null);
            }


            WordInfoView wordInfoView = (WordInfoView) convertView;
            wordInfoView.load(this.getItem(position));
            return convertView;
        }
    }
}
