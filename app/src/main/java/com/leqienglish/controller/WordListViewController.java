package com.leqienglish.controller;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.leqienglish.R;
import com.leqienglish.activity.ArticleInfoActivity;
import com.leqienglish.activity.word.WordInfoActivity;
import com.leqienglish.data.word.MyWordDataCache;
import com.leqienglish.util.BundleUtil;
import com.leqienglish.util.LOGGER;
import com.leqienglish.util.LQHandler;
import com.leqienglish.view.sorted.SectionAdapter;
import com.leqienglish.view.sorted.slidbar.SideBar;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import xyz.tobebetter.entity.word.Word;

/**
 * Created by zhuqing on 2017/8/19.
 */

public class WordListViewController extends ControllerAbstract<View> {

    private LOGGER logger = new LOGGER(WordListViewController.class);

    private ListView wordListView;

    private SideBar sideBar;
    private SectionAdapter sectionAdapter;
    public WordListViewController(View fragment) {
        super(fragment);
    }

    @Override
    public void init() {
        this.wordListView = this.getView().findViewById(R.id.myword_wordlist);
        this.sideBar = this.getView().findViewById(R.id.myword_wordList_sidebar);

        this.sectionAdapter = new SectionAdapter(this.getView().getContext(),null);
        this.wordListView.setAdapter(this.sectionAdapter);

        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                int position = sectionAdapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    wordListView.setSelection(position);
                }


            }
        });

        this.wordListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Word word= sectionAdapter.getItem(position);
                logger.d(word.getWord());
                Intent intent = new Intent();
                intent.putExtras(BundleUtil.create(BundleUtil.DATA, word));
                intent.setClass(getView().getContext(), WordInfoActivity.class);
                getView().getContext().startActivity(intent);
            }
        });
    }

    @Override
    public void reload() {
        MyWordDataCache.getInstance().load(new LQHandler.Consumer<List<Word>>() {
            @Override
            public void accept(List<Word> words) {
                if(words == null){
                    sectionAdapter.updateListView(words);
                    return;
                }
                Collections.sort(words,new Comparator<Word>() {
                    @Override
                    public int compare(Word o1, Word o2) {
                        return o1.getWord().charAt(0)-o2.getWord().charAt(0);
                    }
                });

                sectionAdapter.updateListView(words);
            }
        });
    }

    @Override
    public void destory() {

    }


}
