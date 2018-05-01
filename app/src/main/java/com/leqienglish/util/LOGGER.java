package com.leqienglish.util;

import android.util.Log;

public class LOGGER {
    public Class object;
    public LOGGER (Class object){
        this.object = object;
    }
    public  void v(String message){
        Log.v(object.toString(), message);
    }
    public void v(String message,Exception ex){
        Log.v(object.toString(), message,ex);
    }

    public void d(String message){
        Log.d(object.toString(), message);
    }
    public void d(String message,Exception ex){
        Log.d(object.toString(), message,ex);
    }

    public void w(String message){
        Log.w(object.toString(), message);
    }
    public void w(String message,Exception ex){
        Log.w(object.toString(), message,ex);
    }

    public void e(String message){
        Log.e(object.toString(), message);
    }
    public void e(String message,Exception ex){
        Log.e(object.toString(), message,ex);
    }

    public void i(String message){
        Log.i(object.toString(), message);
    }
    public void i(String message,Exception ex){
        Log.i(object.toString(), message,ex);
    }
}