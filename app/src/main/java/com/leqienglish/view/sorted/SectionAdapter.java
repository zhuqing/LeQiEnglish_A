package com.leqienglish.view.sorted;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.leqienglish.R;
import com.leqienglish.view.adapter.LeQiBaseAdapter;

import java.util.Collections;
import java.util.List;

import xyz.tobebetter.entity.word.Word;

public class SectionAdapter extends LeQiBaseAdapter<Word> implements SectionIndexer {

    private Context mContext;

    public SectionAdapter(Context mContext, List<Word> list) {
        super(LayoutInflater.from(mContext));
        this.mContext = mContext;
        if(list == null){
            list = Collections.EMPTY_LIST;
        }
        this.setItems(list);
    }



    public View getView(final int position, View view, ViewGroup arg2) {
        ViewHolder viewHolder = null;
        final Word mContent = this.getItem(position);
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.word_item, null);
            viewHolder.tvTitle = (TextView) view.findViewById(R.id.word_item_word);
            view.setTag(viewHolder);
            viewHolder.tvLetter = view.findViewById(R.id.word_item_header);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        if(position == 0){
            this.setSection(viewHolder.tvLetter,getFirstChar(mContent.getWord()));
        }

        int section = getSectionForPosition(position);

        if (position == getPositionForSection(section)) {
            viewHolder.tvLetter.setVisibility(View.VISIBLE);
            this.setSection(viewHolder.tvLetter,getFirstChar(mContent.getWord()));
            viewHolder.tvLetter.setBackgroundResource(R.drawable.backgrond_squar_grey);

        } else {
            viewHolder.tvLetter.setVisibility(View.GONE);
        }

        viewHolder.tvTitle.setText(this.getItem(position).getWord());

        return view;

    }

    private void setSection(LinearLayout header, String label) {
        header.removeAllViews();
        TextView text = new TextView(this.mContext);
        header.setBackgroundColor(0x00000000);
        text.setTextColor(Color.BLACK);
        text.setText(label.substring(0, 1).toUpperCase());
        text.setTextSize(14);

        text.setPadding(5, 0, 0, 0);
        text.setGravity(Gravity.CENTER_VERTICAL);
        header.addView(text);
    }

    private String getFirstChar(String word){
        if(word == null){
            return "";
        }

        return word.substring(0,1).toUpperCase();
    }


    final static class ViewHolder {
        LinearLayout tvLetter;
        TextView tvTitle;
    }

    public int getSectionForPosition(int position) {
        return getFirstChar(this.getItem(position).getWord()).charAt(0);
    }



    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = getFirstChar(this.getItem(i).getWord());
            char firstChar = sortStr.charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public Object[] getSections() {
        return null;
    }

}
