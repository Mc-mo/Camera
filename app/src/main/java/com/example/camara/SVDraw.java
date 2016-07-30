package com.example.camara;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.camara.utils.Constants;
import com.zhuchudong.toollibrary.L;

import java.util.ArrayList;


/**
 * Created by Administrator on 2016/5/6.
 */
public class SVDraw extends SurfaceView implements SurfaceHolder.Callback {
    public SurfaceHolder mSurfaceHolder;
    private int mWidth;
    private int mHeight;
    private double widthScare;
    private double widthScare_LR;
    private double widthScare_TB;

    private double heighScare;
    private double heighScare_LR;
    private double heighScare_TB;

    public static float start_X;
    public static float start_Y;
    public static float end_Y;
    public static float end_X;



    public SVDraw(Context context, AttributeSet attrs) {
        super(context, attrs);
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);
        setZOrderOnTop(true);


    }

    public SVDraw(Context context) {
        super(context);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mWidth = width;
        mHeight = height;
        initScare();
    }

    private void initScare() {
        widthScare_TB = (double) mWidth / (double) Constants.requestWidth;
        widthScare_LR = (double) mHeight / (double) Constants.requestWidth;
        heighScare_TB = (double) mHeight / (double) Constants.height;
        heighScare_LR = (double) mWidth / (double) Constants.height;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    public void drawlocation(int left, int top, int width, int height) {
        Canvas canvas = mSurfaceHolder.lockCanvas();

        if (canvas != null) {
            canvas.drawColor(Color.TRANSPARENT);

            Paint mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setColor(Color.GREEN);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(4);
            canvas.drawRect(left, top, left + width, top + height, mPaint);

            mSurfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    public void drawlocation(ArrayList<LocationBean> locationBeanArrayList, int orientation) {

        initScare();
        Canvas canvas = mSurfaceHolder.lockCanvas();
        if (canvas != null) {
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

            Paint mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setColor(Color.GREEN);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(4);

            widthScare = widthScare_TB;
            heighScare = heighScare_TB;

            switch (orientation) {
                case Constants.TOP:
                    break;
                case Constants.LEFT:
                    canvas.rotate(-90);
                    canvas.translate(-mHeight, 0);

                    widthScare = widthScare_LR;
                    heighScare = heighScare_LR;
                    L.e("左横屏绘制  Constants.height" + Constants.height);

                    break;
                case Constants.BOTTOM:
                    canvas.rotate(180);
                    canvas.translate(-mWidth, -mHeight);
                    break;
                case Constants.RIGHT:
                    canvas.rotate(90);
                    canvas.translate(0, -mWidth);

                    widthScare = widthScare_LR;
                    heighScare = heighScare_LR;

                    L.e("右横屏绘制 Constants.height" + Constants.height);
                    break;
                default:
                    break;
            }
            L.e("orientation  " + orientation);
            L.e("widthScare  " + mWidth + "   " + widthScare);
            L.e("heighScare  " + mHeight + "   " + heighScare);
            if (locationBeanArrayList != null && locationBeanArrayList.size() > 0) {
                for (int i = 0; i < locationBeanArrayList.size(); i++) {
                    LocationBean locationBean = locationBeanArrayList.get(i);
                    start_X = (float) (locationBean.getX() * widthScare);
                    start_Y = (float) (locationBean.getY() * heighScare);
                    end_X = (float) ((locationBean.getX() + locationBean.getWidth()) * widthScare);
                    end_Y = (float) ((locationBean.getY() + locationBean.getHeight()) * heighScare);
                    canvas.drawRect(start_X, start_Y, end_X, end_Y, mPaint);
                }
            }

            mSurfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    public void clearDraw() {
        Canvas canvas = mSurfaceHolder.lockCanvas();
        if (canvas != null) {
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            mSurfaceHolder.unlockCanvasAndPost(canvas);
        }

    }



}
