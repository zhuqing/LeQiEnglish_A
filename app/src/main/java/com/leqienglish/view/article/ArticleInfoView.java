package com.leqienglish.view.article;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.leqienglish.R;
import com.leqienglish.activity.ArticleInfoActivity;
import com.leqienglish.activity.load.LoadingActivity;
import com.leqienglish.data.content.ContentWordsDataCache;
import com.leqienglish.data.segment.SegmentDataCache;
import com.leqienglish.database.ExecuteSQL;
import com.leqienglish.entity.SQLEntity;
import com.leqienglish.sf.LQService;
import com.leqienglish.util.BundleUtil;
import com.leqienglish.util.FileUtil;
import com.leqienglish.util.LOGGER;
import com.leqienglish.util.LQHandler;
import com.leqienglish.util.image.ImageUtil;
import com.leqienglish.view.adapter.LeQiBaseAdapter;
import com.leqienglish.view.recommend.RecommendArticle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xyz.tobebetter.entity.english.Content;
import xyz.tobebetter.entity.english.Segment;
import xyz.tobebetter.entity.word.Word;


import static xyz.tobebetter.entity.Consistent.SEGMENT_TYPE;

public class ArticleInfoView extends RelativeLayout {
    private LOGGER logger = new LOGGER(ArticleInfoView.class);
    private Content content;


    private TextView titleView;

    private GridView gridView;

    private Bitmap roundBlurBitMap;

    private GridViewAdapter gridViewAdapter;

    private Bitmap blurBitMap;

    public ArticleInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.article_info, this);

        this.titleView = this.findViewById(R.id.article_info_title);
        this.gridView = this.findViewById(R.id.article_info_gridview);
        this.gridViewAdapter = new GridViewAdapter(LayoutInflater.from(this.gridView.getContext()));
        addListener();
    }

    private void addListener() {
        this.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Segment segment = gridViewAdapter.getItem(position);
                if (segment == null) {
                    return;
                }

                if(position == 0){
                    return;
                }

                Intent intent = new Intent();
                Bundle bundle = BundleUtil.create(BundleUtil.DATA, segment);
                BundleUtil.create(bundle, BundleUtil.PATH, content.getAudioPath());
                intent.putExtras(bundle);
                intent.setClass(getContext(), LoadingActivity.class);
                getContext().startActivity(intent);
            }
        });
    }

    /**
     * 创建模糊图片
     * @return
     */
    private Bitmap buildBlurBitmap() {
        Bitmap sbit = BitmapFactory.decodeResource(this.getResources(), R.drawable.obm);
        return ImageUtil.fastBlur( sbit, 30);
    }

    public void load() {

        if (this.getContent() == null) {
            return;
        }

        this.blurBitMap = this.buildBlurBitmap();
        this.roundBlurBitMap = ImageUtil.createRoundCornerImage(blurBitMap,30, ImageUtil.HalfType.ALL);
        this.getRootView().setBackground(new BitmapDrawable(this.getResources(),blurBitMap));
        this.titleView.setText(this.getContent().getTitle());

        SegmentDataCache segmentDataCache = new SegmentDataCache(this.getContent());

        segmentDataCache.load(new LQHandler.Consumer<List<Segment>>() {
            @Override
            public void accept(List<Segment> segmentList) {
                showSegments(segmentList);
            }
        });


        ContentWordsDataCache contentWordsDataCache = new ContentWordsDataCache(this.getContent());
        contentWordsDataCache.load(new LQHandler.Consumer<List<Word>>() {
            @Override
            public void accept(List<Word> words) {
                logger.d(words.toString());
            }
        });

    }

    private Segment getWordsSegment(){
        Segment word = new Segment();

        word.setTitle("单词列表");
        return  word;
    }


    private void showSegments(List<Segment> segmentList) {

        if (segmentList == null || segmentList.isEmpty()) {
            return;
        }

        List<Segment> newList = new ArrayList<>();

        newList.add(this.getWordsSegment());
        newList.addAll(segmentList);

        segmentList = newList;


        this.gridViewAdapter.setItems(segmentList);
        this.gridView.setAdapter(this.gridViewAdapter);

        int size = segmentList.size();
        int length = 200;

        float density = Resources.getSystem().getDisplayMetrics().density;

        int gridviewWidth = (int) (size * (length + 40) * density);
        int itemWidth = (int) (length * density);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                gridviewWidth, LinearLayout.LayoutParams.MATCH_PARENT);
        gridView.setLayoutParams(params); // 设置GirdView布局参数,横向布局的关键
        gridView.setColumnWidth(itemWidth); // 设置列表项宽
        gridView.setHorizontalSpacing(70); // 设置列表项水平间距
        gridView.setStretchMode(GridView.NO_STRETCH);
        gridView.setNumColumns(size); // 设置列数量=列表集合数
    }


    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
        this.load();
    }


    final class ViewHolder {
        Button button;
        TextView title;

    }

    class GridViewAdapter extends LeQiBaseAdapter<Segment> {

        public GridViewAdapter(LayoutInflater mInflater) {
            super(mInflater);
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ArticleInfoView.ViewHolder holder = null;
            if (view != null) {
                holder = (ArticleInfoView.ViewHolder) view.getTag();
            } else {
                holder = new ArticleInfoView.ViewHolder();
                view = this.mInflater.inflate(R.layout.article_info_item, null);
                // holder.button = view.findViewById(R.id.article_info_item_button);
                holder.title = view.findViewById(R.id.article_info_item_title);

                view.setBackground(new BitmapDrawable(view.getResources(), roundBlurBitMap));

                view.setTag(holder);

            }

            Segment actical = this.getItem(i);
            if (actical == null) {
                return view;
            }

            holder.title.setText(actical.getTitle().substring(actical.getTitle().length() - 4));
            final ArticleInfoView.ViewHolder fviewHolder = holder;


            return view;
        }
    }
}
