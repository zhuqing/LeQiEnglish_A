package com.leqienglish.sf;

import android.os.AsyncTask;
import android.os.Handler;

import com.leqienglish.sf.task.HttpGetTask;
import com.leqienglish.sf.task.HttpGetTranslateTask;
import com.leqienglish.sf.task.HttpPostTask;
import com.leqienglish.sf.task.HttpPutTask;
import com.leqienglish.util.LQHandler;

import org.springframework.http.MediaType;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import xyz.tobebetter.entity.word.Word;


/**
 * Created by zhuqing on 2017/8/19.
 */

public class LQService {
    private HttpURLConnection hipClient;

    public static String http;

    public static boolean isConnect = false;

    private static RestClient restClient;

    public static RestClient getRestClient() {
        if (restClient == null) {
            try {
                restClient = new RestClient(LQService.getHttp());
            } catch (Exception ex) {
                restClient = new RestClient("http://www.leqienglish.com");
            }

        }
        return restClient;
    }

    public static String getHttp() {
        if (http == null) {
            http = "http://www.leqienglish.com/";
//            http = "http://192.168.8.146:8080/";
//            http = "http://192.168.2.165:8080/";
          ////  http = "http://192.168.43.9:8080/";
        }

        return http;
    }

    public static void startCheckNet(){
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {

            }
        };
        timer.schedule(timerTask,1,600000);
    }

    /**
     * 获取App的logo的路径
     *
     * @return
     */
    public static String getLogoPath() {
        return LQService.getHttp() + "res/static/images/logo.png";
    }

    public static <T> void put(String path, Object value, Class claz, Map<String, ?> variables, LQHandler.Consumer<T> consumer) {
        new HttpPutTask(getHttp() + path, value, claz, consumer, variables).execute();
    }

    public static <T> void get(String path, Class claz, Map<String, String> variables, LQHandler.Consumer<T> consumer) {
        new HttpGetTask(getHttp() + path, claz, consumer, variables).execute();
    }

    public static <T extends Word> void getTrans(String word, Map<String, String> variables, LQHandler.Consumer<T> consumer) {
        new HttpGetTranslateTask<T>(word, consumer, variables).execute();
    }

    public static <T> void post(String path, Object value, Class claz, Map<String, ?> variables, LQHandler.Consumer<T> consumer) {
        new HttpPostTask(getHttp() + path, value, claz, consumer, variables).execute();
    }

    public static void download(String path, Handler handler) throws IOException {
        //创建一个URL对象
        URL url = new URL(getHttp() + path);
        //创建一个HTTP链接
        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
        urlConn.setConnectTimeout(10000);
        urlConn.connect();

        if (urlConn.getResponseCode() != HttpURLConnection.HTTP_OK) {
            return;
        }

        urlConn.getContentLength();
    }


    public static <T> void download(String path, String filePath, MediaType mediaType, Map<String, ?> variables, LQHandler.Consumer<String> consumer) {
        // new HttpDownLoadTask(http+path,filePath,mediaType,consumer,variables).execute();
    }

    /**
     * 检查网络
     * @param consumer
     */
    public static void checkNet(LQHandler.Consumer<Boolean> consumer) {


        AsyncTask asyncTask = new AsyncTask<Object, Object, Boolean>() {
            @Override
            protected Boolean doInBackground(Object[] objects) {
                RestClient restClient = new RestClient(LQService.getHttp());

                try {
               String value =     restClient.get("/check/check", null, String.class);
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            }

            @Override
            protected void onPostExecute(Boolean t) {
                super.onPostExecute(t);
                if (consumer == null) {
                    return;
                }
                consumer.accept(t);
            }
        };

        asyncTask.execute();

    }


}


