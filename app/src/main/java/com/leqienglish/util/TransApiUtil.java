package com.leqienglish.util;

//import com.leqienglish.entity.english.TranslateEntity;
import com.leqienglish.sf.LQService;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {"from":"en","to":"zh","trans_result":[{"src":"apple","dst":"\u82f9\u679c"}]}
 */
public class TransApiUtil {
    private static final String TRANS_API_HOST = "http://api.fanyi.baidu.com/api/trans/vip/translate";

    public final static  String FROM="en";
    public final static String TO="zh";

    private final static  String APPID = "20180505000153561";
    private final static  String SECURITY_KEY = "7SDapXJkYMl_k3WMej6C";

    private final static String SRC = "src";

    private final static String DST = "dst";
    private final static String TRANS_RESULT = "trans_result";


    public static String transResult(String query, String from, String to, final LQHandler.Consumer<List<Object>> consumer) {
       String params = buildParams(query);
      //  return HttpGet.get(TRANS_API_HOST, params);
        LQService.getTrans(TRANS_API_HOST+"?"+params, String.class, null, new LQHandler.Consumer<String>() {
            @Override
            public void accept(String s) {
                if(s == null){
                    return;
                }
//                try {
////                    JSONObject jsonObject = new JSONObject(s);
////
////                    JSONArray arr = jsonObject.getJSONArray(TRANS_RESULT);
////                    List<TranslateEntity> trans = new ArrayList<TranslateEntity>(arr.length());
////                    for (int i = 0 ; i < arr.length() ; i++){
////                        JSONObject json = arr.getJSONObject(i);
////                        TranslateEntity translateEntity = new TranslateEntity();
////                        translateEntity.setDst(json.getString(DST));
////                        translateEntity.setSrc(json.getString(SRC));
////
////                        trans.add(translateEntity);
////
////                    }
//
////                    consumer.applay(trans);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
                consumer.accept(null);
            }

        });
        return "";
    }

    private static String buildParams(String query) {

        // 随机数
        String salt = String.valueOf(System.currentTimeMillis());

        // 签名
        String src = APPID + query + salt + SECURITY_KEY; // 加密前的原文

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("q=").append(query);
        stringBuffer.append("&").append("from=").append(FROM);
        stringBuffer.append("&").append("to=").append(TO);
        stringBuffer.append("&").append("appid=").append(APPID);
        stringBuffer.append("&").append("salt=").append(salt);
        stringBuffer.append("&").append("sign=").append( MD5.md5(src));
        return stringBuffer.toString();
    }

}
