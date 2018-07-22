package com.leqienglish.sf;

import android.os.Handler;

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
    /**
     * 下载图片，如果存在不下载
     * @param path
     * @return
     */
    public static String loadImage(String path) {

        String filePath = FileUtil.toLocalPath(path);
        File file = new File(filePath);
        if (file.exists()) {
            return filePath;
        }

        try {
            FutureTaskUtil.run(() -> {
                RestClient restClient = new RestClient("");
                restClient.downLoad(path, filePath, null);
                return null;
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return filePath;
    }

    public static void loadFile(String path, Handler handler){
        String filePath = FileUtil.toLocalPath(path);
        File file = new File(filePath);
        if (file.exists()) {
            return ;
        }
        try {
            FutureTaskUtil.run(() -> {
                RestClient restClient = new RestClient("");
                restClient.downLoad(path, filePath, new LQHandler.Consumer<Double>() {
                    @Override
                    public void accept(Double aDouble) {
                        handler.sendMessage(MessageUtil.createMessage(AppType.HAS_DOWNLOAD,AppType.DATA,aDouble));
                    }
                });

                handler.sendMessage(MessageUtil.createMessage(AppType.DOWNLOAD_OVER,AppType.DATA,""));
                return null;
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
