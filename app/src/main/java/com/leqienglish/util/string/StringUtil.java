package com.leqienglish.util.string;

public class StringUtil {

    public static String replace(String str ,String src , String target){
        String lowStr = src.toLowerCase();

        String[] wordArr = str.split(" ");
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0 ; i< wordArr.length ; i++){
            String word = wordArr[i];
            if(word.toLowerCase().equals(lowStr)){
                wordArr[i] = target;
            }
            stringBuilder.append(word).append(" ");
        }

        return stringBuilder.toString();
    }


    public static   boolean isNullOrEmpty(String str){
        return str == null || str.isEmpty();
    }
}
