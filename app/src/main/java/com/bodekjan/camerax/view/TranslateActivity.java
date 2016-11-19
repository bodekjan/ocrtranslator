package com.bodekjan.camerax.view;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.utils.ConvertUtils;
import com.blankj.utilcode.utils.NetworkUtils;
import com.blankj.utilcode.utils.ShellUtils;
import com.bodekjan.camerax.R;
import com.bodekjan.camerax.util.CommonHelper;
import com.googlecode.tesseract.android.TessBaseAPI;
import com.mikepenz.iconics.view.IconicsImageView;

import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;

public class TranslateActivity extends AppCompatActivity implements Runnable{
    private String picturePath="";
    private ImageView rawPicture;
    IconicsImageView arrow;
    //识别语言英文
    private String source = "eng";
    //识别语言简体中文
    private String target = "chi_sim";
    //训练数据路径，必须包含tesseract文件夹
    private String sdcard; /* 语言文件的地址 */
    EditText scanValue;
    EditText translateValue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);
        arrow=(IconicsImageView)findViewById(R.id.backarrow);
        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TranslateActivity.this.finish();
            }
        });
        Intent intent=getIntent();
        picturePath=intent.getStringExtra("picpath");
        source=intent.getStringExtra("source");
        rawPicture=(ImageView)findViewById(R.id.abc);
        Bitmap bm = BitmapFactory.decodeFile(picturePath);
        rawPicture.setImageBitmap(bm);
        /*文件地址*/
        this.sdcard = Environment.getExternalStorageDirectory() + "/global/";
        scanValue=(EditText)findViewById(R.id.scanvalue);
        translateValue=(EditText)findViewById(R.id.translatevalue);
        /* 自动识别语言 */
        if(source.equals("--")){

        }else {
            scanValue.setText("识别中请稍等");
            Thread thread=new Thread(this);
            thread.start();
        }
    }
    final Handler TranslateData = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            if(msg.what == 1){
                Bundle bundle=msg.getData();
                scanValue.setText(bundle.getString("value"));
                translateValue.setText(bundle.getString("uyvalue"));
            }
        }
    };
    @Override
    public void run()
    {
        try{
            //设置图片可以缓存
            rawPicture.setDrawingCacheEnabled(true);
            //获取缓存的bitmap
            final Bitmap bmp = rawPicture.getDrawingCache();
            Bitmap bitmap=BitmapFactory.decodeFile(picturePath);
            final TessBaseAPI baseApi = new TessBaseAPI();
            //初始化OCR的训练数据路径与语言
            baseApi.init(this.sdcard, source);
            //设置识别模式
            baseApi.setPageSegMode(TessBaseAPI.PageSegMode.PSM_AUTO_OSD);
            //设置要识别的图片
            baseApi.setImage(bitmap);
            Message message = new Message();
            message.what = 1;
            Bundle value=new Bundle();
            value.putString("value",baseApi.getUTF8Text());
            value.putString("uyvalue",baseApi.getUTF8Text());
            if(!value.getString("value").equals("")){
                try {
                    value.putString("uyvalue",CommonHelper.globalTranslate(value.getString("value")));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            message.setData(value);
            TranslateData.sendMessage(message);
            baseApi.clear();
            baseApi.end();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
