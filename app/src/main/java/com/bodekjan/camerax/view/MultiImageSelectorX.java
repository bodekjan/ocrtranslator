package com.bodekjan.camerax.view;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.bodekjan.camerax.R;

import java.util.ArrayList;


/**
 * Created by bodekjan on 2016/11/17.
 */
public class MultiImageSelectorX {

    public static final String EXTRA_RESULT = ImageSelectorActivity.EXTRA_RESULT;

    private boolean mShowCamera = true;
    private int mMaxCount = 9;
    private int mMode = ImageSelectorActivity.MODE_MULTI;
    private ArrayList<String> mOriginData;
    private static MultiImageSelectorX sSelector;

    @Deprecated
    private MultiImageSelectorX(Context context){

    }

    private MultiImageSelectorX(){}

    @Deprecated
    public static MultiImageSelectorX create(Context context){
        if(sSelector == null){
            sSelector = new MultiImageSelectorX(context);
        }
        return sSelector;
    }

    public static MultiImageSelectorX create(){
        if(sSelector == null){
            sSelector = new MultiImageSelectorX();
        }
        return sSelector;
    }

    public MultiImageSelectorX showCamera(boolean show){
        mShowCamera = show;
        return sSelector;
    }

    public MultiImageSelectorX count(int count){
        mMaxCount = count;
        return sSelector;
    }

    public MultiImageSelectorX single(){
        mMode = ImageSelectorActivity.MODE_SINGLE;
        return sSelector;
    }

    public MultiImageSelectorX multi(){
        mMode = ImageSelectorActivity.MODE_MULTI;
        return sSelector;
    }

    public MultiImageSelectorX origin(ArrayList<String> images){
        mOriginData = images;
        return sSelector;
    }

    public void start(Activity activity, int requestCode){
        final Context context = activity;
        if(hasPermission(context)) {
            activity.startActivityForResult(createIntent(context), requestCode);
        }else{
            Toast.makeText(context, R.string.mis_error_no_permission, Toast.LENGTH_SHORT).show();
        }
    }

    public void start(Fragment fragment, int requestCode){
        final Context context = fragment.getContext();
        if(hasPermission(context)) {
            fragment.startActivityForResult(createIntent(context), requestCode);
        }else{
            Toast.makeText(context, R.string.mis_error_no_permission, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean hasPermission(Context context){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
            // Permission was added in API Level 16
            return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    private Intent createIntent(Context context){
        Intent intent = new Intent(context, ImageSelectorActivity.class);
        intent.putExtra(ImageSelectorActivity.EXTRA_SHOW_CAMERA, mShowCamera);
        intent.putExtra(ImageSelectorActivity.EXTRA_SELECT_COUNT, mMaxCount);
        if(mOriginData != null){
            intent.putStringArrayListExtra(ImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, mOriginData);
        }
        intent.putExtra(ImageSelectorActivity.EXTRA_SELECT_MODE, mMode);
        return intent;
    }
}