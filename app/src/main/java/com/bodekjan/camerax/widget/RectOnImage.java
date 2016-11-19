package com.bodekjan.camerax.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.blankj.utilcode.utils.ConvertUtils;
import com.bodekjan.camerax.R;
import com.bodekjan.camerax.util.CommonHelper;

/**
 * Created by bodekjan on 2016/11/17.
 */
public class RectOnImage extends View {
    private static final String TAG = "CameraSurfaceView";
    private int mScreenWidth;
    private int mScreenHeight;
    private Paint mPaint;
    private RectF mRectF;
    // 圆
    private Point centerPoint;
    private int radio;
    // 运动检测器
    private GestureDetector gestureDetector;
    // 方向和距离检测器
    boolean upDown=false; //false为上，true为下
    boolean leftRight=false; //false为左，true为右
    float xxDistance=20;
    float yyDistance=20;
    float xxPoint=0;
    float yyPoint=0;
    int top=80;
    int right=80;
    int down=640;
    int left=80;

    public RectOnImage(Context context) {
        this(context, null);
    }

    public RectOnImage(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RectOnImage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getScreenMetrix(context);
        initView(context);
        gestureDetector=new GestureDetector(context,onGestureListener);
    }
    private GestureDetector.OnGestureListener onGestureListener=new GestureDetector.OnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            xxPoint=e.getX();
            yyPoint=e.getY();
            return true; //这个true了才可以传下去
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//            xxDistance=e2.getX()-e1.getX();
//            yyDistance=e2.getY()-e1.getY();
//            xxPoint=e1.getX();
//            yyPoint=e1.getY();
//            if(xxDistance>0){
//                Log.e("ACTION","RIGHT!!!");
//                leftRight=false;
//            }
//            if(xxDistance<0){
//                Log.e("ACTION","LEFT!!!");
//                leftRight=true;
//            }
//            if(yyDistance>0){
//                Log.e("ACTION","DOWN!!!");
//            }
//            if(yyDistance<0){
//                Log.e("ACTION","UP!!!");
//            }
//            initView(getContext());



            xxDistance=e2.getX();
            yyDistance=e2.getY();
            initView(getContext());
            return false;
        }
    };
    private void getScreenMetrix(Context context) {
        WindowManager WM = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        WM.getDefaultDisplay().getMetrics(outMetrics);
        CommonHelper.screenWidth=mScreenWidth = outMetrics.widthPixels;
        right=mScreenWidth - right;
        mScreenHeight = outMetrics.heightPixels;
        mScreenHeight=mScreenHeight- ConvertUtils.dp2px(context,160);
        CommonHelper.screenHeight=mScreenHeight;
        down=mScreenHeight - down;
    }

    private void initView(Context context) {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);// 抗锯齿
        mPaint.setDither(true);// 防抖动
        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(5);
        mPaint.setStyle(Paint.Style.STROKE);// 空心
        // 新的计算模式
//        if(Math.abs(xxPoint-left)<30){
//            /* 在左线附近 */
//            if(leftRight){
//                left+=xxDistance;
//            }else {
//                left+=xxDistance;
//            }
//        }
//        if(Math.abs(xxPoint-right)<30){
//            /* 在左线附近 */
//            if(leftRight){
//                right+=xxDistance;
//            }else {
//                right+=xxDistance;
//            }
//        }
//        if(Math.abs(yyPoint-top)<30){
//            /* 在左线附近 */
//            top+=yyDistance;
//        }
//        if(Math.abs(yyPoint-down)<30){
//            /* 在左线附近 */
//            down+=yyDistance;
//        }
        // 更新的方式
        if(Math.abs(xxPoint-left)<30){
            /* 在左线附近 */
            xxPoint=left=(int)xxDistance;
        }
        if(Math.abs(xxPoint-right)<30){
            /* 在左线附近 */
            xxPoint=right=(int)xxDistance;
        }
        if(Math.abs(yyPoint-top)<30){
            /* 在左线附近 */
            yyPoint=top=(int)yyDistance;
        }
        if(Math.abs(yyPoint-down)<30){
            /* 在左线附近 */
            yyPoint=down=(int)yyDistance;
        }
        mRectF = new RectF(left, top, right, down);
        CommonHelper.left=left;
        CommonHelper.right=right-mScreenWidth;
        CommonHelper.top=top;
        CommonHelper.down=down-mScreenHeight;
        centerPoint = new Point(mScreenWidth/2, mScreenHeight/2);
        radio = (int) (mScreenWidth*0.1);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(8);
        mPaint.setColor(getResources().getColor(R.color.colorScanLine));
        canvas.drawRect(mRectF, mPaint);
        mPaint.setColor(Color.WHITE);
        //canvas.drawCircle(centerPoint.x,centerPoint.y, radio,mPaint);// 外圆
        //canvas.drawCircle(centerPoint.x,centerPoint.y, radio - 20,mPaint); // 内圆
        mPaint.setColor(getResources().getColor(R.color.colorHeader));
        mPaint.setTextSize(32);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawText("By bodekjan. Wechat: AsQar27", 50, 50, mPaint);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                xxPoint=event.getX();
                yyPoint=event.getY();
            case MotionEvent.ACTION_MOVE:
                xxDistance=event.getX();
                yyDistance=event.getY();
                initView(getContext());
                break;
        }
        return true;
//        return gestureDetector.onTouchEvent(event);
    }
    private IAutoFocus mIAutoFocus;

    /** 聚焦的回调接口 */
    public interface  IAutoFocus{
        void autoFocus();
    }

    public void setIAutoFocus(IAutoFocus mIAutoFocus) {
        this.mIAutoFocus = mIAutoFocus;
    }
}