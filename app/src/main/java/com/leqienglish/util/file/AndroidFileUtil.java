package com.leqienglish.util.file;

import android.app.Application;
import android.os.Environment;

import java.io.File;

public class AndroidFileUtil extends FileUtil {

    public static Application application;

    public final static  AndroidFileUtil androidFileUtil = new AndroidFileUtil();

    protected AndroidFileUtil(){

    }

    public static AndroidFileUtil getInstence(){
        return androidFileUtil;
    }



    @Override
    public String appRootPath() {
        File file = Environment.getExternalStorageDirectory();

        String userDir = "";
        if(file!=null){
            userDir = file.getAbsolutePath();
        }else{
            userDir = application.getBaseContext().getFilesDir().getAbsolutePath();
        }
        StringBuffer sb = new StringBuffer();
        sb.append(userDir).append(File.separatorChar).append(APP_NAME);
        String rootpath = sb.toString();
        initDirectory(rootpath);
        return rootpath;
    }

    public  String wordFilelPath(String word) {
        StringBuffer sb = new StringBuffer();
        sb.append(appRootPath()).append(File.separatorChar).append("word").append(File.separatorChar).append(word);
        initDirectory(sb.toString());
        sb.append(File.separatorChar).append(fileName("mp3"));
        return sb.toString();
    }


    public String createImagePath(String fileName){
        StringBuffer sb = new StringBuffer();
        sb.append(appRootPath()).append(File.separatorChar).append(imageDirectory()).append(File.separatorChar);
        initDirectory(sb.toString());
        sb.append(fileName);
        return sb.toString();
    }
}
