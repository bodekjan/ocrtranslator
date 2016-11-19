package com.bodekjan.camerax.model;
import android.content.Context;
import android.content.res.AssetManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;

/**
 * Created by bodekjan on 2016/9/6.
 */
public class LanguageLib {
    private ArrayList<OneLanguage> mLangList;
    private Context mContext;
    private static LanguageLib languageLib;
    private LanguageLib(Context context) throws ParseException {
        mContext=context;
        mLangList=new ArrayList<OneLanguage>();
        initData();
    }
    private void initData() throws ParseException {
        /* 数据准备 */
        AssetManager assetManager = mContext.getAssets();
        try {
            InputStream is = assetManager.open("language.json");
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuffer stringBuffer = new StringBuffer();
            String str = null;
            while((str = br.readLine())!=null){
                stringBuffer.append(str);
            }
            JSONTokener jsonTokener = new JSONTokener(stringBuffer.toString());
            try {
                JSONObject jsonObject=(JSONObject)jsonTokener.nextValue();
                JSONArray languageArray=jsonObject.getJSONArray("language");
                for(int i=0; i<languageArray.length();i++){
                    OneLanguage language=new OneLanguage();
                    JSONObject object=languageArray.getJSONObject(i);
                    language.title=object.getString("title");
                    language.img=object.getString("img");
                    language.size=object.getDouble("size");
                    language.status=object.getString("status");
                    language.fileName=object.getString("filename");
                    mLangList.add(language);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static LanguageLib get(Context context) throws ParseException {
        if(languageLib==null){
            languageLib = new LanguageLib(context);
        }
        return languageLib;
    }
    public ArrayList<OneLanguage> getLanguage(){
        return mLangList;
    }
}
