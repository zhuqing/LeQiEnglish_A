package com.leqienglish.sf;

import android.os.AsyncTask;
import android.os.Handler;

import com.leqienglish.util.AppType;
import com.leqienglish.util.FileUtil;
import com.leqienglish.util.LQHandler;
import com.leqienglish.util.message.MessageUtil;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public class LoadFile {


    public static void downLoad( String url,String filePath, MediaType... mdiaTypes) throws FileNotFoundException, IOException {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
//headers.setAcceptCharset(Arrays.asList(Charset.forName("binary")));
            headers.setAccept(Arrays.asList(mdiaTypes));
            headers.setContentType(MediaType.IMAGE_JPEG);

            ResponseEntity<byte[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<byte[]>(headers),
                    byte[].class);

            byte[] result = response.getBody();

            inputStream = new ByteArrayInputStream(result);

            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            }

            outputStream = new FileOutputStream(file);
            int len = 0;
            byte[] buf = new byte[1024];
            while ((len = inputStream.read(buf, 0, 1024)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.flush();
        }catch(Exception ex){
            ex.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }

    private static String loadFile(String urlPath) {
        if (urlPath == null || urlPath.isEmpty()) {
            return "";
        }
        try {
            String filePath = FileUtil.getFileAbsolutePath(urlPath);
            File file = new File(filePath);
            if (file.exists() && file.length() != 0) {

            }


            RestClient restClient = new RestClient(LQService.getHttp());
            try {
                restClient.downLoad("/file/download?path=" + urlPath, filePath, null);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return filePath;


        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static void loadFile(String path, LQHandler.Consumer<String> consumer) {

        if (path == null || path.isEmpty()) {
            return;
        }
        try {
            String filePath = FileUtil.getFileAbsolutePath(path);
            File file = new File(filePath);
            if (file.exists() && file.length() != 0) {
                if (consumer != null) {
                    consumer.accept(filePath);
                }
                return;
            }

            AsyncTask asyncTask = new AsyncTask<Object, Object, String>() {
                @Override
                protected String doInBackground(Object... objects) {
                    RestClient restClient = new RestClient(LQService.getHttp());
                    try {
                        restClient.downLoad("/file/download?path=" + path, filePath, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return filePath;
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
                    if (consumer != null) {
                        consumer.accept(s);
                    }
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
                        restClient.downLoad("/file/download?path=" + path, filePath
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
