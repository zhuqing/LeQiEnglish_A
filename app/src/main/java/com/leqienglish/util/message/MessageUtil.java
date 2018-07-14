package com.leqienglish.util.message;

import android.os.Bundle;
import android.os.Message;

/**
 * Created by zhuqing on 2018/5/10.
 */

public class MessageUtil {
    public static Message createMessage(int what ,String key , String data){
        Message message = new Message();
        message.what = what;
        message.setData(new Bundle());
        message.getData().putString(key,data);
        return message;
    }

    public static Message createMessage(int what ,String key , double data){
        Message message = new Message();
        message.what = what;
        message.setData(new Bundle());
        message.getData().putDouble(key,data);
        return message;
    }
}
