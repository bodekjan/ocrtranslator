package com.bodekjan.camerax.view;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bodekjan.camerax.R;
import com.bodekjan.camerax.model.LanguageLib;
import com.bodekjan.camerax.model.OneLanguage;
import com.bodekjan.camerax.util.Download;
import com.mikepenz.iconics.view.IconicsImageView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;

public class LangActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 2;
    private ArrayList<OneLanguage> mLangList;
    IconicsImageView arrow;
    ListView languageList;
    double[] fileSize=new double[100];
    double[] currentSize=new double[100];
    java.text.DecimalFormat df;
    private String sdcard; /* 语言文件的地址 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lang);
        arrow=(IconicsImageView)findViewById(R.id.backarrow);
        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LangActivity.this.finish();
            }
        });
        languageList=(ListView)findViewById(R.id.languagelist);
        try {
            mLangList= LanguageLib.get(LangActivity.this).getLanguage();
            MyAdapter mAdapter = new MyAdapter(this);//得到一个MyAdapter对象
            languageList.setAdapter(mAdapter);//为ListView绑定Adapter
        } catch (ParseException e) {
            e.printStackTrace();
        }
        df=new   java.text.DecimalFormat("#.##");
        /*文件地址*/
        this.sdcard = Environment.getExternalStorageDirectory() + "/global/tessdata/";
    }
    private class MyAdapter extends BaseAdapter {
        private LayoutInflater mInflater;//得到一个LayoutInfalter对象用来导入布局

        /**构造函数*/
        public MyAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mLangList.size();//返回数组的长度
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        /**书中详细解释该方法*/
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            //观察convertView随ListView滚动情况
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_language,null);
                holder = new ViewHolder();
                /**得到各个控件的对象*/
                holder.title = (TextView) convertView.findViewById(R.id.langtitle);
                holder.size = (TextView) convertView.findViewById(R.id.langsize);
                holder.bt = (Button) convertView.findViewById(R.id.downbtn);
                holder.remove = (Button) convertView.findViewById(R.id.removebtn);
                holder.flags=(ImageView)convertView.findViewById(R.id.flagview);
                /* 国旗 */
                convertView.setTag(holder);//绑定ViewHolder对象
            }
            else{
                holder = (ViewHolder)convertView.getTag();//取出ViewHolder对象
            }
            /**设置TextView显示的内容，即我们存放在动态数组中的数据*/
            holder.title.setText(mLangList.get(position).title);
            String sizeValue=getResources().getString(R.string.packagesize);
            holder.size.setText(String.format(sizeValue,mLangList.get(position).size));
            Bitmap flag = getImageFromAssetsFile("flags/"+mLangList.get(position).img+".png");
            holder.flags.setImageBitmap(flag);
            if(checkFile(sdcard+mLangList.get(position).fileName+".traineddata")){
                holder.bt.setVisibility(View.GONE);
                holder.remove.setVisibility(View.VISIBLE);
            }
            holder.remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String fileName = sdcard + mLangList.get(position).fileName + ".traineddata";
                    File file = new File(fileName);
                    if (!file.exists()) {  // 不存在返回 false
                        /* 文件不存在 */
                        return;
                    }
                    if (file.isFile() ) {  // 为文件时调用删除文件方法
                        if (file.delete()) {
                            setButton(position,-1);
                        }
                    }
                }
            });
            /**为Button添加点击事件*/
            holder.bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ((ContextCompat.checkSelfPermission(LangActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) )
                    {
                        ActivityCompat.requestPermissions(LangActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_CALL_PHONE);
                    } else
                    {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                            /**/
                                Download load = new Download("http://115.159.28.64:8080/camerax/"+mLangList.get(position).fileName+".traineddata");
                                fileSize[position]=load.getLength();
                                currentSize[position]=0.0;
                                int status = load.down2sd("global/tessdata/", mLangList.get(position).fileName+".traineddata", load.new DownHandler() {
                                    @Override
                                    public void setSize(double size) {
                                        Message msg = DownloadData.obtainMessage();
                                        Bundle bundle=new Bundle();
                                        bundle.putDouble("process",size);
                                        msg.setData(bundle);
                                        msg.what = 2;
                                        msg.arg1=position;
                                        msg.setData(bundle);
                                        msg.sendToTarget();
                                    }
                                });
                                //log输出
                                if(status==1){
                                    Message msg=new Message();
                                    msg.what=3;
                                    msg.arg1=position;
                                    DownloadData.sendMessage(msg);
                                }
                            }
                        }).start();
                    }
                }
            });

            return convertView;
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    /* 提示再试 */
                } else {
                    /* 无法完成 */
                }
                return;
            }
        }
    }
    public boolean checkFile(String fileFullPath){
        File file=new File(fileFullPath);
        if(!file.exists())
        {
            return false;
        }
        return true;
    }
    public void setButton(int position,double value){
        View view=languageList.getChildAt(position);
        Button button=(Button) view.findViewById(R.id.downbtn);
        Button remove=(Button) view.findViewById(R.id.removebtn);
        if(value==0.0){
            button.setVisibility(View.GONE);
            remove.setVisibility(View.VISIBLE);
        }else if(value==-1) {
            button.setVisibility(View.VISIBLE);
            remove.setVisibility(View.GONE);
        }else {
            button.setEnabled(false);
            button.setText(df.format(value)+" %");
        }

    }
    private Bitmap getImageFromAssetsFile(String fileName)
    {
        Bitmap image = null;
        AssetManager am = getResources().getAssets();
        try
        {
            InputStream is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return image;

    }
    /**存放控件*/
    public final class ViewHolder{
        public TextView title;
        public TextView size;
        public Button bt;
        public Button remove;
        public ImageView flags;
    }
    Looper loop = Looper.getMainLooper();
    final Handler DownloadData = new Handler(loop){
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            if(msg.what == 2){
                Bundle bundle=msg.getData();
                currentSize[msg.arg1]+=bundle.getDouble("process");
                double process=(currentSize[msg.arg1]/fileSize[msg.arg1])*100;
                setButton(msg.arg1,process);
            }else if(msg.what == 3){
                setButton(msg.arg1,0.0);
            }
        }
    };
}
