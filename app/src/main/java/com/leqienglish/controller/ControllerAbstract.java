package com.leqienglish.controller;

import android.content.Intent;
import android.view.View;

import com.leqienglish.MainActivity;

/**
 * Created by zhuqing on 2017/8/19.
 */

public abstract class ControllerAbstract<F extends View>{


    private F view;


    public  ControllerAbstract(F view){
       this.view = view;
    }


    public F getView() {
        return view;
    }

    protected <T extends View> T  findViewById(int id){
      return   this.getView().findViewById(id);
    }

    public abstract void init();

    public abstract void reload();

    public abstract void destory();


    /**
     * 返回主页
     */
    public void goBackHome(){
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setClass(getView().getContext(), MainActivity.class);
        getView().getContext().startActivity(intent);
    }
}
