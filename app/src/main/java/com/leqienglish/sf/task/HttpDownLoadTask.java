package com.leqienglish.sf.task;

import com.leqienglish.util.LQHandler;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * 下载文件
 */
public class HttpDownLoadTask extends HttpTask<String> {

    private MediaType  mediaType;

    private String filePath;

    public HttpDownLoadTask(String path, String filePath, MediaType mediaType, LQHandler.Consumer<String> consumer, Map<String, ?> variables) {
        super(path, null, consumer, variables);
        this.filePath = filePath;
        this.mediaType = mediaType;
    }


    @Override
    protected String getT(RestTemplate restTemplate) throws Exception {

        HttpHeaders headers = new HttpHeaders();
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {

            headers.setAccept(Arrays.asList(this.mediaType));
            headers.setContentType(mediaType);

            ResponseEntity<byte[]> response = restTemplate.exchange(
                    this.getPath(),
                    HttpMethod.GET,
                    new HttpEntity<byte[]>(headers),
                    byte[].class);

            inputStream = new ByteArrayInputStream(response.getBody());

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

        } catch (Exception ex) {
            ex.printStackTrace();
            filePath = null;
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
        }
        return filePath;
    }
}
