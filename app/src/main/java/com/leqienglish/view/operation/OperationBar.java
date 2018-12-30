package com.leqienglish.view.operation;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.leqienglish.R;

public class OperationBar extends RelativeLayout {

    private OperationBarItem heartButton;
    private OperationBarItem contentButton;
    private OperationBarItem shareButton;

    private OperationBarI operationBarI;


    public OperationBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.operation_bar, this);
        this.init();
    }

    private void init(){

        this.heartButton = this.findViewById(R.id.operation_bar_heart);
        this.contentButton = this.findViewById(R.id.operation_bar_content);
        this.shareButton = this.findViewById(R.id.operation_bar_share);


        this.shareButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getOperationBarI()!=null){
                    getOperationBarI().shareClickHandler();
                }
            }
        });

        this.contentButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getOperationBarI()!=null){
                    getOperationBarI().contentClickHandler();
                }
            }
        });

        this.heartButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getOperationBarI()!=null){
                    getOperationBarI().heartClickHandler();
                }
            }
        });
    }

    /**
     * 是否已经点击红心
     * @param hearted
     */
    public void hasHearted(boolean hearted){

    }

    public OperationBarI getOperationBarI() {
        return operationBarI;
    }

    public void setOperationBarI(OperationBarI operationBarI) {
        this.operationBarI = operationBarI;
    }


    public interface OperationBarI{
        public void heartClickHandler();
        public void contentClickHandler();
        public void shareClickHandler();
    }

}
