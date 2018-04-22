package com.leqienglish.util;

import android.os.Bundle;

import java.io.Serializable;

/**
 * Created by zhuleqi on 2018/2/12.
 */
public class BundleUtil {
    public static final String DATA = "DATA";

    public static Bundle create(String key , Serializable serializable){
        Bundle bundle = new Bundle();
        bundle.putSerializable(key , serializable);
        return bundle;
    }

    public static  Bundle create(String key , int value){
        Bundle bundle = new Bundle();
        bundle.putInt(key , value);
        return bundle;
    }
}
