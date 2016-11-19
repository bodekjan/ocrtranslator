package com.bodekjan.camerax.view;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.blankj.utilcode.utils.ConvertUtils;
import com.bodekjan.camerax.R;
import com.bodekjan.camerax.model.LanguageLib;
import com.bodekjan.camerax.model.OneLanguage;
import com.bodekjan.camerax.widget.CameraSurfaceView;
import com.bodekjan.camerax.widget.NiceSpinner;
import com.bodekjan.camerax.widget.RectOnCamera;
import com.mikepenz.iconics.view.IconicsImageView;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;


public class ScanActivity extends AppCompatActivity implements View.OnClickListener,RectOnCamera.IAutoFocus{
    private CameraSurfaceView mCameraSurfaceView;
    private RectOnCamera mRectOnCamera;
    private IconicsImageView takePicBtn;
    private String sdcard; /* 语言文件的地址 */
    private boolean isClicked;
    IconicsImageView arrow;
    //MaterialSpinnerX spinner;
    NiceSpinner niceSpinner;
    List<OneLanguage> languages=new ArrayList<OneLanguage>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_scan);
        arrow=(IconicsImageView)findViewById(R.id.backarrow);
        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScanActivity.this.finish();
            }
        });
        arrow.setPadding(ConvertUtils.dp2px(ScanActivity.this,20),ConvertUtils.dp2px(ScanActivity.this,20),ConvertUtils.dp2px(ScanActivity.this,20),ConvertUtils.dp2px(ScanActivity.this,20));
        arrow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                arrow.setPadding(ConvertUtils.dp2px(ScanActivity.this,14),ConvertUtils.dp2px(ScanActivity.this,14),ConvertUtils.dp2px(ScanActivity.this,14),ConvertUtils.dp2px(ScanActivity.this,14));
                return false;
            }
        });
        mCameraSurfaceView = (CameraSurfaceView) findViewById(R.id.cameraSurfaceView);
        mRectOnCamera = (RectOnCamera) findViewById(R.id.rectOnCamera);
        takePicBtn= (IconicsImageView) findViewById(R.id.takePic);
        takePicBtn.setPadding(ConvertUtils.dp2px(ScanActivity.this,6),ConvertUtils.dp2px(ScanActivity.this,6),ConvertUtils.dp2px(ScanActivity.this,6),ConvertUtils.dp2px(ScanActivity.this,6));
        takePicBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        takePicBtn.setPadding(ConvertUtils.dp2px(ScanActivity.this,2),ConvertUtils.dp2px(ScanActivity.this,2),ConvertUtils.dp2px(ScanActivity.this,2),ConvertUtils.dp2px(ScanActivity.this,2));
                        break;
                    case MotionEvent.ACTION_UP:
                        takePicBtn.setPadding(ConvertUtils.dp2px(ScanActivity.this,6),ConvertUtils.dp2px(ScanActivity.this,6),ConvertUtils.dp2px(ScanActivity.this,6),ConvertUtils.dp2px(ScanActivity.this,6));
                        break;
                }
                return false;
            }
        });
        mRectOnCamera.setIAutoFocus(this);
        takePicBtn.setOnClickListener(this);
        this.sdcard = Environment.getExternalStorageDirectory() + "/global/tessdata/";
        /* Spinner */
        List<OneLanguage> tempList=new ArrayList<OneLanguage>();
        try {
            tempList= LanguageLib.get(ScanActivity.this).getLanguage();
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
                mCameraSurfaceView.takePicture(this.sdcard,oneLanguage);
                break;
            default:
                break;
        }
    }
    @Override
    public void autoFocus() {
        mCameraSurfaceView.setAutoFocus();
    }
}
