package com.leqienglish.sf;

import android.os.AsyncTask;
import android.os.Handler;

import com.leqienglish.entity.Message;
import com.leqienglish.sf.RestClient;
import com.leqienglish.util.AppType;
import com.leqienglish.util.FileUtil;
import com.leqienglish.util.LQHandler;
import com.leqienglish.util.message.MessageUtil;
import com.leqienglish.util.task.FutureTaskUtil;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class LoadFile {

    public static void loadFile(String path, LQHandler.Consumer<String> consumer) {

        if(path ==null || path.isEmpty()){
            return;
        }
        try {
            String filePath = FileUtil.getFileAbsolutePath(path);
            File file = new File(filePath);
            if (file.exists()) {
                consumer.accept(filePath);
                return;
            }

            AsyncTask asyncTask = new AsyncTask<Object, Object, String>() {
                @Override
                protected String doInBackground(Object... objects) {
                    RestClient restClient = new RestClient(LQService.getHttp());
                    try {
                        restClient.downLoad("/file/download?path=" +path, filePath, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return filePath;
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
                    consumer.accept(s);
                }
            };

            asyncTask.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadFile(String path, Handler handler) {

        try {
            String filePath = FileUtil.getFileAbsolutePath(path);
//            File file = new File(filePath);
//            if (file.exists()) {
//                handler.sendMessage(MessageUtil.createMessage(AppType.DOWNLOAD_OVER, AppType.DATA, 1.0));
//                return;
//            }

            AsyncTask asyncTask = new AsyncTask<Object, Object, String>() {
                @Override
                protected String doInBackground(Object... objects) {
                    RestClient restClient = new RestClient(LQService.getHttp());
                    try {
                        restClient.downLoad("/file/download?path=" +path, filePath
                                , new LQHandler.Consumer<Double>() {
                                    @Override
                                    public void accept(Double aDouble) {
                                        handler.sendMessage(MessageUtil.createMessage(AppType.HAS_DOWNLOAD, AppType.DATA, aDouble));
                                    }
                                });

                        handler.sendMessage(MessageUtil.createMessage(AppType.DOWNLOAD_OVER, AppType.DATA, 1.0));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return filePath;
                }


            };

            asyncTask.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
