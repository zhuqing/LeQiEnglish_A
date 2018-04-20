package com.leqienglish.controller;

import android.view.View;

/**
 * Created by zhuqing on 2017/8/19.
 */

public abstract class Controller<F extends View>{


    private F view;


    public  Controller(F view){
       this.view = view;
    }


    public F getView() {
        return view;
    }

    public abstract void init();
}
