package com.leqienglish.sf.task;

import android.os.Handler;
import android.os.Message;

import com.leqienglish.util.AppType;
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
    protected Object getT(RestTemplate restTemplate) throws Exception {
        //创建一个URL对象
        URL url = new URL(this.getPath());
        //创建一个HTTP链接
        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
        urlConn.setConnectTimeout(10000);
        urlConn.connect();

        if (urlConn.getResponseCode() != HttpURLConnection.HTTP_OK) {
            handler.sendEmptyMessage(AppType.DOWNLOAD_ERROR);
            return null;
        }

        handler.sendMessage(MessageUtil.createMessage(AppType.DOWNLOAD_ALLLEGTH, AppType.DATA, urlConn.getContentLength() + ""));
        InputStream inputStream = urlConn.getInputStream();
        OutputStream os = new FileOutputStream(this.filePath);
        int length;
        int lengtsh = 0;
        byte[] bytes = new byte[1024];
        while ((length = inputStream.read(bytes)) != -1) {
            os.write(bytes, 0, length);
            //获取当前进度值
            lengtsh += length;
            //把值传给handler
            handler.sendMessage(MessageUtil.createMessage(AppType.HAS_DOWNLOAD,AppType.DATA,lengtsh+""));
        }
        //关闭流
        inputStream.close();
        os.close();
        os.flush();
        handler.sendEmptyMessage(AppType.DOWNLOAD_OVER);
        return null;
    }
}
