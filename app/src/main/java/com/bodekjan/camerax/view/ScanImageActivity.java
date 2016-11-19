package com.bodekjan.camerax.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.blankj.utilcode.utils.ConvertUtils;
import com.bodekjan.camerax.R;
import com.bodekjan.camerax.model.LanguageLib;
import com.bodekjan.camerax.model.OneLanguage;
import com.bodekjan.camerax.util.CommonHelper;
import com.bodekjan.camerax.widget.CameraSurfaceView;
import com.bodekjan.camerax.widget.NiceSpinner;
import com.bodekjan.camerax.widget.RectOnCamera;
import com.bodekjan.camerax.widget.RectOnImage;
import com.mikepenz.iconics.view.IconicsImageView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;


public class ScanImageActivity extends AppCompatActivity implements View.OnClickListener,RectOnImage.IAutoFocus{
    private RectOnImage mRectOnCamera;
    private IconicsImageView takePicBtn;
    private String sdcard; /* 语言文件的地址 */
    private boolean isClicked;
    IconicsImageView arrow;
    ImageView phonepic;
    //MaterialSpinnerX spinner;
    NiceSpinner niceSpinner;
    List<OneLanguage> languages=new ArrayList<OneLanguage>();
    Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_scanimage);
        arrow=(IconicsImageView)findViewById(R.id.backarrow);
        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScanImageActivity.this.finish();
            }
        });
        arrow.setPadding(ConvertUtils.dp2px(ScanImageActivity.this,20),ConvertUtils.dp2px(ScanImageActivity.this,20),ConvertUtils.dp2px(ScanImageActivity.this,20),ConvertUtils.dp2px(ScanImageActivity.this,20));
        arrow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                arrow.setPadding(ConvertUtils.dp2px(ScanImageActivity.this,14),ConvertUtils.dp2px(ScanImageActivity.this,14),ConvertUtils.dp2px(ScanImageActivity.this,14),ConvertUtils.dp2px(ScanImageActivity.this,14));
                return false;
            }
        });
        mRectOnCamera = (RectOnImage) findViewById(R.id.rectOnCamera);
        takePicBtn= (IconicsImageView) findViewById(R.id.takePic);
        takePicBtn.setPadding(ConvertUtils.dp2px(ScanImageActivity.this,6),ConvertUtils.dp2px(ScanImageActivity.this,6),ConvertUtils.dp2px(ScanImageActivity.this,6),ConvertUtils.dp2px(ScanImageActivity.this,6));
        takePicBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        takePicBtn.setPadding(ConvertUtils.dp2px(ScanImageActivity.this,2),ConvertUtils.dp2px(ScanImageActivity.this,2),ConvertUtils.dp2px(ScanImageActivity.this,2),ConvertUtils.dp2px(ScanImageActivity.this,2));
                        break;
                    case MotionEvent.ACTION_UP:
                        takePicBtn.setPadding(ConvertUtils.dp2px(ScanImageActivity.this,6),ConvertUtils.dp2px(ScanImageActivity.this,6),ConvertUtils.dp2px(ScanImageActivity.this,6),ConvertUtils.dp2px(ScanImageActivity.this,6));
                        break;
                }
                return false;
            }
        });
        mRectOnCamera.setIAutoFocus(this);
        takePicBtn.setOnClickListener(this);
        this.sdcard = Environment.getExternalStorageDirectory() + "/global/tessdata/";
        phonepic=(ImageView)findViewById(R.id.phonepic);
        Intent intent=getIntent();
        bitmap= BitmapFactory.decodeFile(intent.getStringExtra("picpath"));
        phonepic.setImageBitmap(bitmap);
        /* Spinner */
        List<OneLanguage> tempList=new ArrayList<OneLanguage>();
        try {
            tempList= LanguageLib.get(ScanImageActivity.this).getLanguage();
            for(int i=0;i<tempList.size();i++){
                if(checkFile(sdcard+tempList.get(i).fileName+".traineddata")){
                    languages.add(tempList.get(i));
                }
            }
            if(languages.size()!=0){
                String[] ITEMS=new String[languages.size()];
                List<String> dataset = new ArrayList<String>();
                for(int j=0; j<languages.size();j++){
                    dataset.add(languages.get(j).title);
                }
                /* 新的spinner */
                niceSpinner = (NiceSpinner) findViewById(R.id.nice_spinner);
                niceSpinner.attachDataSource(dataset);
            }
        } catch (ParseException e) {
            e.printStackTrace();
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
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.takePic:
                int position=niceSpinner.getSelectedIndex();
                if(position==-1){
                    return;
                }
                OneLanguage oneLanguage=languages.get(position);
                /* 处理剪切的图片 */
                //Bitmap bitmap=BitmapFactory.decodeFile(getIntent().getStringExtra("picpath"));
                int top=(int)(((float)bitmap.getHeight()/(float) CommonHelper.screenHeight)*CommonHelper.top);
                int left=(int)(((float)bitmap.getWidth()/(float)CommonHelper.screenWidth)*CommonHelper.left);
                int width=(int)(bitmap.getWidth()-Math.abs(((float)bitmap.getWidth()/(float)CommonHelper.screenWidth)*CommonHelper.right)-left);
                int height=(int)(bitmap.getHeight()-Math.abs(((float)bitmap.getHeight()/(float)CommonHelper.screenHeight)*CommonHelper.down)-top);
                Bitmap bmp = Bitmap.createBitmap(bitmap, left, top, width,height , null, false);
                BufferedOutputStream bos = null;
                String filePath="--";
                try {
                    // 获得图片
                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        //filePath = path+System.currentTimeMillis()+".jpg";//照片保存路径
                        filePath = this.sdcard+"screenshut.jpg";//照片保存路径
                        File fPath = new File(this.sdcard);
                        File file = new File(filePath);
                        if (!file.exists()){
                            fPath.mkdirs();
                            file.createNewFile();
                        }
                        bos = new BufferedOutputStream(new FileOutputStream(file));
                        bmp.compress(Bitmap.CompressFormat.JPEG, 100, bos);//将图片压缩到流中

                    }else{
                    }
                /* 是否转之前弄好 */
                    bmp.recycle();// 回收bitmap空间
                    Intent intent=new Intent(ScanImageActivity.this,TranslateActivity.class);
                    intent.putExtra("picpath",filePath);
                    intent.putExtra("source",oneLanguage.fileName);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        bos.flush();//输出
                        bos.close();//关闭
                        bmp.recycle();// 回收bitmap空间
                        //bitmap.recycle();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }
    @Override
    public void autoFocus() {
    }
}
