package com.leqienglish.view.article;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.leqienglish.R;
import com.leqienglish.activity.PlayAudioActivity;
import com.leqienglish.activity.load.LoadingActivity;
import com.leqienglish.activity.word.ArticleWordListActivity;
import com.leqienglish.data.content.MyRecitingContentDataCache;
import com.leqienglish.data.content.RecitedSegmentDataCache;
import com.leqienglish.data.segment.SegmentDataCache;
import com.leqienglish.data.user.UserDataCache;
import com.leqienglish.sf.LQService;
import com.leqienglish.util.BundleUtil;
import com.leqienglish.util.FileUtil;
import com.leqienglish.util.LOGGER;
import com.leqienglish.util.LQHandler;
import com.leqienglish.util.image.ImageUtil;
import com.leqienglish.view.adapter.LeQiBaseAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xyz.tobebetter.entity.english.Content;
import xyz.tobebetter.entity.english.Segment;
import xyz.tobebetter.entity.english.word.user.UserAndSegment;
import xyz.tobebetter.entity.user.content.UserAndContent;

public class ArticleInfoView extends RelativeLayout {
    private LOGGER logger = new LOGGER(ArticleInfoView.class);
    private Content content;


    private TextView titleView;

    private GridView gridView;

    private Bitmap roundBlurBitMap;

    private GridViewAdapter gridViewAdapter;

    private TextView acticleInfoPrecent;

    private Bitmap blurBitMap;

    private RecitedSegmentDataCache recitedSegmentDataCache;

    public ArticleInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.article_info, this);

        this.titleView = this.findViewById(R.id.article_info_title);
        this.gridView = this.findViewById(R.id.article_info_gridview);
        this.acticleInfoPrecent = this.findViewById(R.id.article_info_percent);
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
                Intent intent = new Intent();
                if(position == 0){
                    intent.setClass(getContext(), ArticleWordListActivity.class);
                    Bundle bundle = BundleUtil.create(BundleUtil.DATA, content);
                    intent.putExtras(bundle);

                }else{

                    String path = FileUtil.getPath(content,segment);

                    Bundle bundle = BundleUtil.create(BundleUtil.DATA, segment);
                    BundleUtil.create(bundle, BundleUtil.PATH, path);
                    intent.putExtras(bundle);

                    String filePath = FileUtil.toLocalPath(path);
                    if(new File(filePath).exists()){
                        intent.setClass(getContext(), PlayAudioActivity.class);
                    }else{
                        intent.setClass(getContext(), LoadingActivity.class);
                    }
                }


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

        this.recitedSegmentDataCache = RecitedSegmentDataCache.getInstance(content);

        this.blurBitMap = this.buildBlurBitmap();
        this.roundBlurBitMap = ImageUtil.createRoundCornerImage(blurBitMap,30, ImageUtil.HalfType.ALL);
        this.getRootView().setBackground(new BitmapDrawable(this.getResources(),blurBitMap));
        this.titleView.setText(this.getContent().getTitle());

        SegmentDataCache segmentDataCache = new SegmentDataCache(this.getContent());

        segmentDataCache.load(new LQHandler.Consumer<List<Segment>>() {
            @Override
            public void accept(List<Segment> segmentList) {
                showSegments(segmentList);
                loadHasRecited();
            }
        });


    }

    private void loadHasRecited(){
        this.recitedSegmentDataCache.load(new LQHandler.Consumer<List<UserAndSegment>>() {
            @Override
            public void accept(List<UserAndSegment> userAndSegments) {
                if(userAndSegments == null){
                    userAndSegments = Collections.EMPTY_LIST;
                }
                acticleInfoPrecent.setText("已完成"+userAndSegments.size()+"/"+(gridViewAdapter.getCount()-1));
                updatePrecent((int)(userAndSegments.size()*1.0/(gridViewAdapter.getCount()-1)*100));
            }
        });
    }


    private void updatePrecent(Integer precent){
        if(UserDataCache.getInstance().getUser() == null){
            return;
        }
        Map<String,String> param = new HashMap<>();

        param.put("userId", UserDataCache.getInstance().getUser().getId());
        param.put("contentId",content.getId());
        param.put("precent",precent+"");


        LQService.put("userAndContent/updatePrecent", null, UserAndContent.class, param, new LQHandler.Consumer<UserAndContent>() {
            @Override
            public void accept(UserAndContent userReciteRecord) {
                MyRecitingContentDataCache.getInstance().update(content.getId(),precent);
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
