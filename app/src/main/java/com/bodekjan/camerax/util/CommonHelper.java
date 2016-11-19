package com.bodekjan.camerax.util;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by bodekjan on 2016/11/18.
 */
public class CommonHelper {
    public static int top;
    public static int right;
    public static int down;
    public static int left;
    public static int screenWidth;
    public static int screenHeight;
    public static String translate="http://115.159.28.64:8080/project.languageall/rest/translata/";
    public static String globalTranslate(String raw) throws UnsupportedEncodingException {
        BufferedReader reader = null;
        String result = null;
        StringBuffer sbf = new StringBuffer();
        String httpUrl = translate + URLEncoder.encode(raw, "utf-8");
        try {
            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setRequestMethod("GET");
            // 填入apikey到HTTP header
            connection.connect();
            InputStream is = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String strRead = null;
            while ((strRead = reader.readLine()) != null) {
                sbf.append(strRead);
                sbf.append("\r\n");
            }
            reader.close();
            result = sbf.toString();
            JSONTokener jsonTokener = new JSONTokener(result);
            JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
            String uyResult=jsonObject.getString("uyresult");
            return uyResult;   // error
        }catch (Exception e){
            return raw;
        }
    }
}
