package com.leqienglish.view.operation;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.leqienglish.R;

import java.util.HashMap;
import java.util.Map;

public class OperationBar extends LinearLayout {


    private OperationBarI operationBarI;
    private LinearLayout linearLayout;

    private String items ;

    private Map<String,OperationBarItem> operationBarItemMap;


    public OperationBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.operation_bar, this);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.OperaiontBar);
        this.items = typedArray.getString(R.styleable.OperaiontBar_items);
        this.init();
    }

    private void init(){

        operationBarItemMap = new HashMap<>();

        operationBarItemMap.put("return",createEntity("return","返回",R.drawable.return_icon));
        operationBarItemMap.put("words",createEntity("words","单词/短语",R.drawable.word_icon));
        operationBarItemMap.put("content",createEntity("content","文稿",R.drawable.content));

        operationBarItemMap.put("hearted",createEntity("hearted","赞",R.drawable.heart));

        operationBarItemMap.put("audioPlay",createEntity("audioPlay","听音频",R.drawable.listen_icon));
        operationBarItemMap.put("recite",createEntity("recite","背诵",R.drawable.recite_content));

        operationBarItemMap.put("share",createEntity("share","分享",R.drawable.share));
        String[] itemArr = items.split("\\|");

        this.linearLayout = this.findViewById(R.id.operation_bar_root);

        for(String item : itemArr){
            this.add(operationBarItemMap.get(item));
        }
    }

    private OperationBarItem createEntity(String id,String title,int imageId){
        OperationBarItem operationBarItem = new OperationBarItem(this.getContext(),null);
        operationBarItem.setItemId(id);
        operationBarItem.setImageId(imageId);
        operationBarItem.setTitle(title);
        return operationBarItem;
    }

    private void add(OperationBarItem operationBarItem){
        if(operationBarItem == null){
            return;
        }
        operationBarItem.updateTitle(operationBarItem.getTitle());
        operationBarItem.updateImage(operationBarItem.getImageId());


        operationBarItem.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getOperationBarI()!=null){
                    getOperationBarI().handler(operationBarItem.getItemId());
                }
            }
        });
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT,1.0f);
        linearLayout.addView(operationBarItem,layoutParams);
    }

    public void update(String id, String title,int imageId){
        OperationBarItem item = this.operationBarItemMap.get(id);
        if(item == null){
            return;
        }

        item.updateTitle(title);
        item.updateImage(imageId);
    }

    public void update(String id, int imageId){
        OperationBarItem item = this.operationBarItemMap.get(id);
        if(item == null){
            return;
        }

        item.updateImage(imageId);

    }

    public void update(String id, String title){
        OperationBarItem item = this.operationBarItemMap.get(id);
        if(item == null){
            return;
        }

        item.updateTitle(title);

    }



    public OperationBarI getOperationBarI() {
        return operationBarI;
    }

    public void setOperationBarI(OperationBarI operationBarI) {
        this.operationBarI = operationBarI;
    }


    public interface OperationBarI{
       public void handler(String id);
    }

}
