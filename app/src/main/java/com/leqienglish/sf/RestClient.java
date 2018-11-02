package com.leqienglish.sf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leqienglish.util.AppType;
import com.leqienglish.util.FileUtil;
import com.leqienglish.util.LOGGER;
import com.leqienglish.util.LQHandler;
import com.leqienglish.util.network.NetWorkUtil;
import com.leqienglish.util.toast.ToastUtil;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import xyz.tobebetter.entity.Message;

/**
 * rest客户端
 *
 * @author zhuqing
 */

public class RestClient {
    private LOGGER logger = new LOGGER(RestClient.class);
    private static RestTemplate restTemplate;

    private String serverPath = "http://192.168.1.108:8080";

    protected final ObjectMapper mapper = new ObjectMapper();

    public RestClient(String host) {
        restTemplate = new RestTemplate();
        this.serverPath = host;
    }

    public <T> T post(String path, Object obj, MultiValueMap<String, String> parameter, Class<T> claz) throws Exception {
        return excute(HttpMethod.POST, path, obj, parameter, claz);
    }

    public <T> T put(String path, Object obj, MultiValueMap<String, String> parameter, Class<T> claz) throws Exception {
        return excute(HttpMethod.PUT, path, obj, parameter, claz);
    }

    public <T> T delete(String path, Object obj, MultiValueMap<String, String> parameter, Class<T> claz) throws Exception {
        return excute(HttpMethod.DELETE, path, obj, parameter, claz);
    }

    public <T> T upload(String path, MultiValueMap<String, Object> value, MultiValueMap<String, String> parameter, Class<T> claz) throws Exception {


        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(serverPath + "/" + path).queryParams(parameter);

            HttpEntity entity = new HttpEntity(value, initHeaders());
            //  entity.getHeaders().
            ResponseEntity resEntity = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, entity, Message.class, new HashMap());
            Message resultMessage = (Message) resEntity.getBody();

            if (resultMessage.getStatus() == Message.ERROR) {
                throw new Exception(resultMessage.getMessage());
            }
            return mapper.readValue(resultMessage.getData(), claz);
        } catch (Exception ex) {
            ToastUtil.showShort(AppType.mainContext, "服务端异常");
            throw ex;
        }

    }

    public <T> T get(String path, MultiValueMap<String, String> parameter, Class<T> claz) throws Exception {
        return excute(HttpMethod.GET, path, null, parameter, claz);
    }

    private <T> T excute(HttpMethod method, String path, Object obj, MultiValueMap<String, String> parameter, Class<T> claz) throws Exception {


        if (!NetWorkUtil.isConnect(AppType.mainContext)) {
            //ToastUtil.showShort(AppType.mainContext, "没有网络");
            return null;
        }

        if (parameter == null) {
            parameter = new LinkedMultiValueMap<>();
        }

        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(serverPath + path).queryParams(parameter);
            HttpEntity entity = new HttpEntity(obj, initHeaders());
            logger.d(method + ":" + builder.toUriString());

            ResponseEntity resEntity = restTemplate.exchange(builder.toUriString(), method, entity, Message.class);
            Message resultMessage = (Message) resEntity.getBody();

            if (resultMessage.getStatus() == Message.ERROR) {
                ToastUtil.showShort(AppType.mainContext, "resultMessage.getMessage()");
                throw new Exception(resultMessage.getMessage());
            }
            if (claz == null) {
                return null;
            }


            logger.d(resultMessage.getData());
            return mapper.readValue(resultMessage.getData(), claz);
        } catch (Exception ex) {
            ToastUtil.showShort(AppType.mainContext, "服务端异常");
            throw ex;
        }
    }

    private HttpHeaders initHeaders() {
        HttpHeaders headers = new HttpHeaders();
        return headers;
    }

    /**
     * 下载文件
     *
     * @param path
     * @param filePath
     * @param hasdownload
     * @throws MalformedURLException
     * @throws IOException
     * @throws Exception
     */
    public void downLoad(String path, String filePath, LQHandler.Consumer<Double> hasdownload) throws MalformedURLException, IOException, Exception {
        FileUtil.delete(filePath);
        File newFile = new File(filePath);
        newFile.createNewFile();
        //创建一个URL对象
        URL url = new URL(serverPath + "/" + path);
        logger.d(serverPath + "/" + path);
        //创建一个HTTP链接
        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
        urlConn.setConnectTimeout(300000);
        urlConn.connect();

        if (urlConn.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new Exception(urlConn.getResponseMessage());
        }


        InputStream inputStream = urlConn.getInputStream();

        int totalLength = urlConn.getContentLength();
        OutputStream os = new FileOutputStream(newFile);
        int length;
        int lengtsh = 0;
        byte[] bytes = new byte[1024];
        while ((length = inputStream.read(bytes)) != -1) {
            os.write(bytes, 0, length);
            // long totalLength = inputStream.available();
            //获取当前进度值
            lengtsh += length;

            //把值传给handler
            if (hasdownload != null) {
                hasdownload.accept(lengtsh * 1.0 / totalLength);
            }
        }
        //关闭流
        inputStream.close();
        os.flush();
        os.close();
        urlConn.disconnect();

        logger.d(filePath + "\nfile ex-" + newFile.exists());

    }

}
