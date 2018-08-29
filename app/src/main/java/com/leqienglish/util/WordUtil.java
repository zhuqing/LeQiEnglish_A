package com.leqienglish.util;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import com.leqienglish.util.tran.TranslateBaiduUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * {"word_name":"good","is_CRI":"1",
 * "exchange":{"word_pl":["goods"],"word_third":"","word_past":"","word_done":"","word_ing":"","word_er":["better"],"word_est":["best"]},
 * "symbols":[
 * {
 * "ph_en":"g\u028ad","ph_am":"\u0261\u028ad","ph_other":"",
 * "ph_en_mp3":"http:\/\/res.iciba.com\/resource\/amp3\/oxford\/0\/28\/a2\/28a24294fed307cf7e65361b8da4f6e5.mp3",
 * "ph_am_mp3":"http:\/\/res.iciba.com\/resource\/amp3\/1\/0\/75\/5f\/755f85c2723bb39381c7379a604160d8.mp3",
 * "ph_tts_mp3":"http:\/\/res-tts.iciba.com\/7\/5\/5\/755f85c2723bb39381c7379a604160d8.mp3",
 * "parts":[{"part":"adj.","means":["\u597d\u7684","\u4f18\u79c0\u7684","\u6709\u76ca\u7684","\u6f02\u4eae\u7684\uff0c\u5065\u5168\u7684"]},
 * {"part":"n.","means":["\u597d\u5904\uff0c\u5229\u76ca","\u5584\u826f","\u5584\u884c","\u597d\u4eba"]},
 * {"part":"adv.","means":["\u540cwell"]}]
 * }]
 * <p>
 * }
 *
 * @author zhuleqi
 */
public class WordUtil {
    private static final LOGGER logger = new LOGGER(WordUtil.class);

    private static final String TRANS_API_HOST = "http://dict-co.iciba.com/api/dictionary.php";


    private final static String KEY = "8471D7E06DF4F75F193372742B7FC2FB";

    private final static String TYPE = "json";
    private final static String SRC = "src";

    private final static String DST = "dst";
    private final static String TRANS_RESULT = "trans_result";


    public static String transResult(String query) throws InterruptedException, ExecutionException {
        String params = buildParams(query);
        String path = TRANS_API_HOST + "?" + params;
        logger.d("path\t"+params);


        try {
            //创建一个URL对象
            URL url = new URL(path);
            //创建一个HTTP链接
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setConnectTimeout(10000);
            urlConn.connect();

            if (urlConn.getResponseCode() != HttpURLConnection.HTTP_OK) {

                return "";
            }

            InputStream inputStream = urlConn.getInputStream();
            OutputStream os = new ByteArrayOutputStream(inputStream.available());
            int length;

            byte[] bytes = new byte[1024];
            while ((length = inputStream.read(bytes)) != -1) {
                os.write(bytes, 0, length);

            }
            String result = os.toString();
            //关闭流
            inputStream.close();
            os.close();
            os.flush();
            return result;


        } catch (MalformedURLException ex) {
            Logger.getLogger(TranslateBaiduUtil.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TranslateBaiduUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        ;
        return "";


    }


    private static String buildParams(String query) {


        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("w=").append(query);
        stringBuffer.append("&").append("type=").append(TYPE);
        stringBuffer.append("&").append("key=").append(KEY);
        return stringBuffer.toString();
    }


}
