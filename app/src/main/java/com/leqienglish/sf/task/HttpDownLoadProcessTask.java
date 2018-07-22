package com.leqienglish.sf.task;

import android.os.Handler;
import android.os.Message;

import com.leqienglish.util.AppType;
import com.leqienglish.util.BundleUtil;
import com.leqienglish.util.LQHandler;
import com.leqienglish.util.message.MessageUtil;

import org.springframework.web.client.RestTemplate;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * Created by zhuqing on 2018/5/10.
 */

public class HttpDownLoadProcessTask extends HttpTask {

    private Handler handler;
    private String filePath;

    public HttpDownLoadProcessTask(String httpPath, String filePath, Handler handler, Map variables) {
        super(httpPath, null, null, variables);
        this.filePath = filePath;
        this.handler = handler;
    }

    @Override
    protected Object getT() throws Exception {

        this.restClient.downLoad(getPath(), filePath, new LQHandler.Consumer<Double>() {
            @Override
            public void accept(Double aDouble) {
               handler.sendMessage(MessageUtil.createMessage(AppType.HAS_DOWNLOAD,AppType.DATA,aDouble));
            }
        });
        handler.sendMessage(MessageUtil.createMessage(AppType.DOWNLOAD_OVER,AppType.DATA,""));
        return null;
    }
}
