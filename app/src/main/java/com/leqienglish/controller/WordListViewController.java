package com.leqienglish.controller;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.leqienglish.R;
import com.leqienglish.activity.word.WordInfoActivity;
import com.leqienglish.data.word.MyWordDataCache;
import com.leqienglish.util.BundleUtil;
import com.leqienglish.util.LOGGER;
import com.leqienglish.util.LQHandler;
import com.leqienglish.util.collection.LQCollectionUtil;
import com.leqienglish.util.toast.ToastUtil;
import com.leqienglish.view.sorted.SectionAdapter;
import com.leqienglish.view.sorted.slidbar.SideBar;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import xyz.tobebetter.entity.word.Word;

import static com.leqienglish.util.AppType.PAGE_SIZE;


/**
 * Created by zhuqing on 2017/8/19.
 */

public class WordListViewController extends ControllerAbstract<View> {

    private LOGGER logger = new LOGGER(WordListViewController.class);

    private PullToRefreshListView wordListView;

    private Integer currentPage;

    private SideBar sideBar;
    private Integer lastSize = 0;

    private Boolean loading = false;

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
                    wordListView.getRefreshableView().setSelection(position);

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

        wordListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> pullToRefreshBase) {
                MyWordDataCache.getInstance().clearData();
                reload();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> pullToRefreshBase) {
                loadMore();
            }
        });
    }

    /**
     * 向上划的时候，加载更多的数据
     */
    public void loadMore(){
        if(loading){
            return;
        }
        loading = true;
        MyWordDataCache.getInstance().loadMore(currentPage, PAGE_SIZE, new LQHandler.Consumer<List<Word>>() {
            @Override
            public void accept(List<Word> words) {

                wordListView.onRefreshComplete();
                if(words == null|| words.isEmpty()){
                    loading = false;
                    ToastUtil.showShort(getView().getContext(),"没有更多数据了");
                    return;
                }
                wordListView.onRefreshComplete();
                currentPage+=1;
                loading = false;
                if(lastSize!=0){
                    sectionAdapter.addMore(words.subList(lastSize,words.size()));
                    lastSize = 0;
                }else{
                    sectionAdapter.addMore(words);
                }

            }
        });
    }

    @Override
    public void reload() {
        if(loading){
            return;
        }
        loading = true;
        MyWordDataCache.getInstance().load(new LQHandler.Consumer<List<Word>>() {
            @Override
            public void accept(List<Word> words) {
                if(words == null){
                    currentPage = 1;
                    sectionAdapter.updateListView(words);
                    wordListView.onRefreshComplete();
                    loading = false;
                    return;
                }

                words = LQCollectionUtil.filter(words, new LQHandler.Callback<Word, String>() {
                    @Override
                    public String call(Word word) {
                        return word.getWord();
                    }
                });
                Collections.sort(words,new Comparator<Word>() {
                    @Override
                    public int compare(Word o1, Word o2) {
                        return o1.getWord().charAt(0)-o2.getWord().charAt(0);
                    }
                });
                loading = false;
                currentPage = getCurrentPage(words.size(), PAGE_SIZE);
                sectionAdapter.updateListView(words);
                wordListView.onRefreshComplete();
            }
        });
    }

    private Integer getCurrentPage(Integer count , Integer pageSize){
        lastSize = count % pageSize;

        return count/pageSize+1;

    }

    @Override
    public void destory() {

    }


}
