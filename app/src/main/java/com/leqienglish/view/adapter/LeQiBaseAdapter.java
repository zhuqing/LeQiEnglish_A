package com.leqienglish.view.adapter;

import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.leqienglish.R;
import com.leqienglish.view.article.ArticleInfoView;

import java.util.Collections;
import java.util.List;

import xyz.tobebetter.entity.english.Segment;
import xyz.tobebetter.entity.word.Word;

public abstract class LeQiBaseAdapter<T> extends BaseAdapter {



    protected LayoutInflater mInflater;

    public LeQiBaseAdapter(LayoutInflater mInflater) {
        this.mInflater = mInflater;
        items = Collections.EMPTY_LIST;
    }

    private List<T> items;

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public T getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0L;
    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     *
     * @param list
     */
    public void updateListView(List<T> list) {
        if(list == null){
            list = Collections.EMPTY_LIST;
        }
        this.items = list;
        notifyDataSetChanged();
    }


}
