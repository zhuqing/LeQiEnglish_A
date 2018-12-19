package com.leqienglish.view.adapter;

import android.view.LayoutInflater;
import android.widget.BaseAdapter;

import java.util.Collections;
import java.util.List;

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

    public void addMore(List<T> list){
        if(list == null){
            list = Collections.EMPTY_LIST;
        }
        this.items .addAll(list);
        notifyDataSetChanged();

    }


}
