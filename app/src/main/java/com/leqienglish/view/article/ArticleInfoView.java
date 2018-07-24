package com.leqienglish.view.article;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.leqienglish.R;
import com.leqienglish.database.ExecuteSQL;
import com.leqienglish.entity.SQLEntity;
import com.leqienglish.sf.LQService;
import com.leqienglish.util.FileUtil;
import com.leqienglish.util.LQHandler;
import com.leqienglish.view.recommend.RecommendArticle;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xyz.tobebetter.entity.english.Content;
import xyz.tobebetter.entity.english.Segment;

import static com.leqienglish.database.Constants.MY_SEGMENT_TYPE;
import static xyz.tobebetter.entity.Consistent.SEGMENT_TYPE;

public class ArticleInfoView extends RelativeLayout {

    private Content content;

    private List<Segment> segments;

    private TextView titleView;

    private GridView gridView;

    private GridViewAdapter gridViewAdapter;

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
            }
        });
    }


    public void load() {

        if (this.getContent() == null) {
            return;
        }

        this.titleView.setText(this.getContent().getTitle());


        ExecuteSQL.getInstance().getDatasByTypeAndParentId(content.getId(), MY_SEGMENT_TYPE, new LQHandler.Consumer<List<SQLEntity>>() {
            @Override
            public void accept(List<SQLEntity> sqlEntities) {
                try {
                    List<Segment> segments = ExecuteSQL.toEntity(sqlEntities, Segment.class);
                    if (segments.isEmpty()) {
                        query(true);
                    } else {
                        showSegments(segments);
                        query(false);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });


    }

    private void query(boolean isShow) {
        Map<String, String> param = new HashMap<>();
        param.put("contentId", this.getContent().getId());

        LQService.get("/segment/findByContentId", Segment[].class, param, new LQHandler.Consumer<Segment[]>() {
            @Override
            public void accept(Segment[] segments) {
                List<Segment> segmentList = Collections.EMPTY_LIST;
                if (segments != null && segments.length != 0) {

                    segmentList = Arrays.asList(segments);
                }

                if (isShow) {
                    showSegments(segmentList);
                }

                try {
                   List<SQLEntity> sqlEntities =  ExecuteSQL.toSQLEntitys(MY_SEGMENT_TYPE,content.getId(),segmentList);
                    ExecuteSQL.getInstance().insertLearnE(sqlEntities,null);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void showSegments(List<Segment> segmentList) {
        this.gridViewAdapter.setItems(segmentList);
        this.gridView.setAdapter(this.gridViewAdapter);

    }


    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
        this.load();
    }

    public List<Segment> getSegments() {
        return segments;
    }

    public void setSegments(List<Segment> segments) {
        this.segments = segments;
    }


    final class ViewHolder {
        Button button;
        TextView title;

    }

    class GridViewAdapter<T> extends BaseAdapter {

        private LayoutInflater mInflater;

        public GridViewAdapter(LayoutInflater mInflater) {
            this.mInflater = mInflater;
        }

        private List<Segment> items;

        public List<Segment> getItems() {
            return items;
        }

        public void setItems(List<Segment> items) {
            this.items = items;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Segment getItem(int i) {
            return items.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0L;
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

                view.setTag(holder);

            }

            Segment actical = this.getItem(i);
            if (actical == null) {
                return view;
            }

            holder.title.setText(actical.getTitle().substring(actical.getTitle().length()-4));
            final ArticleInfoView.ViewHolder fviewHolder = holder;


            return view;
        }
    }
}
