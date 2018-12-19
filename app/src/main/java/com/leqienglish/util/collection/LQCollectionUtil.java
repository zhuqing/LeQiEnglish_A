package com.leqienglish.util.collection;

import com.leqienglish.util.LQHandler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LQCollectionUtil {

    public  static <T>  List<T>  filter(List<T>  lists, LQHandler.Callback<T,String> filterKeyCallback){
        Set<String> keys = new HashSet<>();

        List<T> newList = new ArrayList<>(lists.size());

        for(T t : lists){
            String key = filterKeyCallback.call(t);

            if(keys.contains(key)){
                continue;
            }
            keys.add(key);
            newList.add(t);
        }


        return newList;

    }
}
