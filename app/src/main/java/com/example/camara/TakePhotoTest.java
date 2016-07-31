package com.example.camara;

import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import com.example.camara.utils.Constants;
import com.example.camara.utils.ImageUtils;
import com.example.camara.utils.Utils;
import com.zhuchudong.toollibrary.AppUtils;
import com.zhuchudong.toollibrary.L;
import com.zhuchudong.toollibrary.StatusBarUtil;
import com.zhuchudong.toollibrary.ToastUtils;
import com.zhuchudong.toollibrary.okHttpUtils.OkHttpUtils;
import com.zhuchudong.toollibrary.okHttpUtils.callback.JsonCallBack;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import pl.droidsonroids.gif.AnimationListener;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class TakePhotoTest extends AppCompatActivity implements SurfaceHolder.Callback, View.OnClickListener, AnimationListener {
    //宽度450

    int id;
    Camera camera;
    SurfaceHolder holder;
    GifImageView media_iv;
    SurfaceView surface_camera;
    SVDraw surface_tip;
    public int screenOritation = 60;
    long netTime;
    long processTime;
    boolean show_flag = true;
    Camera.PictureCallback currentCallBack;
    public OrientationEventListener mOrientationListener;
    private TextView timeView;
    StringBuffer tv_string = new StringBuffer();
    private GifDrawable mGifDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

        holder = surface_camera.getHolder();
        holder.setKeepScreenOn(true);
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);


        initOrientationListener();

    }

    @Override
    protected void onStart() {
        super.onStart();
        currentCallBack = new SecondCallback();
        findViewById(R.id.btn_linearlayout).setVisibility(View.VISIBLE);


    }


    @Override
    protected void onStop() {
        super.onStop();
        OkHttpUtils.getInstance().getOkhttpClient().dispatcher().cancelAll();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mOrientationListener != null) {
            mOrientationListener.disable();
        }
    }

    private void initView() {
        StatusBarUtil.setColor(TakePhotoTest.this, getResources().getColor(R.color.colorPrimary));
        findViewById(R.id.btn_linearlayout).setVisibility(View.VISIBLE);
        media_iv = (GifImageView) findViewById(R.id.media_iv);
        surface_camera = (SurfaceView) findViewById(R.id.surface_camera);
        surface_tip = (SVDraw) findViewById(R.id.surface_tip);
        findViewById(R.id.btn_takepicture).setOnClickListener(this);
        findViewById(R.id.btn_again).setOnClickListener(this);
        timeView = (TextView) findViewById(R.id.tv_time);

        surface_tip.setOnTouchListener(new myTouchEventListener());
        media_iv.setOnTouchListener(new myTouchEventListener());


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_takepicture:
                processTime = System.currentTimeMillis();
                camera.takePicture(null, null, currentCallBack);
//                ArrayList<LocationBean> arrayList =new ArrayList<LocationBean>();
//                arrayList.add(new LocationBean(10,10,400,400));
//                surface_tip.drawlocation(arrayList,screenOritation);
                break;
            case R.id.btn_again:
                surface_tip.clearDraw();
                camera.startPreview();
                break;
            default:
                break;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (camera == null) {
            camera = Camera.open();
            try {
                camera.setPreviewDisplay(holder);
                initCamera();
                camera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        initCamera();
        camera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    private void initCamera() {
        Camera.Parameters parameters = camera.getParameters();
        parameters.setPictureFormat(PixelFormat.JPEG);

        List<Camera.Size> previewsizes = parameters.getSupportedPreviewSizes();
        List<Camera.Size> picturesizes = parameters.getSupportedPictureSizes();

        Camera.Size previewMaxSize = Utils.getMaxSize(previewsizes);
        if (previewMaxSize != null) {
            parameters.setPreviewSize(previewMaxSize.width, previewMaxSize.height);
            L.e("setPreviewSize  " + previewMaxSize.width + "   " + previewMaxSize.height);

        } else {
            L.e("setPreviewSize    null");
        }


        List<Camera.Size> picturesizesScale = Utils.getScaleSize(picturesizes, (float) previewMaxSize.width / (float) previewMaxSize.height);
        Camera.Size pictureMaxSize = Utils.getMiddleSize(picturesizesScale, previewMaxSize);
        if (pictureMaxSize != null) {
            parameters.setPictureSize(pictureMaxSize.width, pictureMaxSize.height);
            L.e("setPictureSize   " + pictureMaxSize.width + "   " + pictureMaxSize.height);
        } else {
            L.e("setPictureSize    null");

        }


        if (previewsizes != null && previewsizes.size() > 0) {
            for (int i = 0; i < previewsizes.size(); i++) {
                Camera.Size size = previewsizes.get(i);
                L.i("previewsizes " + size.width + "  " + size.height);
            }
        }

        if (picturesizes != null && picturesizes.size() > 0) {
            for (int i = 0; i < picturesizes.size(); i++) {
                Camera.Size size = picturesizes.get(i);
                L.i("picturesizes " + size.width + "  " + size.height);
            }
        }
        if (picturesizesScale != null && picturesizesScale.size() > 0) {
            for (int i = 0; i < picturesizesScale.size(); i++) {
                Camera.Size size = picturesizesScale.get(i);
                L.i("picturesizesScale " + size.width + "  " + size.height);
            }
        }

        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);//连续对焦
        camera.setParameters(parameters);
        camera.setDisplayOrientation(90);
        camera.startPreview();
        camera.cancelAutoFocus();
    }

    @Override
    public void onAnimationCompleted(int loopNumber) {
        media_iv.setVisibility(View.GONE);
        surface_tip.setVisibility(View.VISIBLE);
    }


    private final class SecondCallback implements Camera.PictureCallback {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            camera.stopPreview();
            byte[] compressDada = ImageUtils.processBitmapBytesSmaller2(data, Constants.requestWidth, screenOritation);

            Utils.savepicture(TakePhotoTest.this, compressDada);
//            new UploadImageTask(Constants.url, compressDada, surface_tip, screenOritation).execute();
            tv_string.append("处理用时：" + (System.currentTimeMillis() - processTime) + "ms").append("\n");
            timeView.setText(tv_string);
            netTime = System.currentTimeMillis();
            OkHttpUtils.postBytes().url(Constants.url).data(compressDada).build().connTimeOut(5000).enqueue(secondCallback);
            // surface_tip.setOnTouchListener(new myTouchEventListener());
        }
    }


    private JsonCallBack secondCallback = new JsonCallBack() {
        @Override
        public void onError(Call call, Exception e) {
            if (!AppUtils.isTopActivity(TakePhotoTest.this, "com.example.camara.TakePhotoTest")) {
                L.e("isTopActivity==false");
                return;
            }
            tv_string.append("请求用时：" + (System.currentTimeMillis() - netTime) + "ms").append("\n").append("\n");
            timeView.setText(tv_string);
            L.e(System.currentTimeMillis() - netTime + "ms");
            L.e("网络请求出错 " + e.toString());
            ToastUtils.showToast(TakePhotoTest.this, "网络请求出错 " + (System.currentTimeMillis() - netTime) + "ms");
            netTime = System.currentTimeMillis();
            processTime = System.currentTimeMillis();
        }

        @Override
        public void onResponse(JSONObject response) {
            if (!AppUtils.isTopActivity(TakePhotoTest.this, "com.example.camara.TakePhotoTest")) {
                L.e("isTopActivity==false");
                return;
            }
            tv_string.append("请求用时：" + (System.currentTimeMillis() - netTime) + "ms").append("\\n");
            timeView.setText(tv_string);
            L.e(System.currentTimeMillis() - netTime + "");
            netTime = System.currentTimeMillis();


            if (response != null && response.has("bounding_rects")) {
                JSONArray locations = response.optJSONArray("bounding_rects");


                ArrayList<LocationBean> locationList = new ArrayList();
                if (locations != null && locations.length() > 0) {
                    for (int i = 0; i < locations.length(); i++) {
                        JSONObject locationJson = locations.optJSONObject(i);
                        LocationBean locationBean = new LocationBean();
                        id = locationJson.optInt("id");
                        locationBean.setX(locationJson.optInt("x"));
                        locationBean.setY(locationJson.optInt("y"));
                        locationBean.setWidth(locationJson.optInt("width"));
                        locationBean.setHeight(locationJson.optInt("height"));

                        locationList.add(locationBean);
                    }
                }
                if (locationList != null && locationList.size() > 0) {
                    for (int i = 0; i < locationList.size(); i++) {
                        LocationBean locationBean = locationList.get(i);
                        L.e("locationBean  " + i + "   " + locationBean.toString());
                    }
                }
                surface_tip.drawlocation(locationList, screenOritation);
            }
            processTime = System.currentTimeMillis();
        }


    };

    private void initOrientationListener() {
        mOrientationListener = new OrientationEventListener(this,
                SensorManager.SENSOR_DELAY_NORMAL) {

            @Override
            public void onOrientationChanged(int orientation) {
                if (orientation == -1) {
                } else if (orientation < 45 || orientation > 315) {
                    screenOritation = Constants.TOP;
                } else if (orientation < 135 && orientation > 45) {
                    screenOritation = Constants.LEFT;
                } else if (orientation < 225 && orientation > 135) {
                    screenOritation = Constants.BOTTOM;
                } else if (orientation < 315 && orientation > 225) {
                    screenOritation = Constants.RIGHT;
                } else {
                }
            }
        };

        if (mOrientationListener.canDetectOrientation() == true) {
            mOrientationListener.enable();
        } else {
            L.e("Cannot detect orientation");
            mOrientationListener.disable();
        }
    }


    private class myTouchEventListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
//            if (event.getX() > SVDraw.start_X && event.getX() < SVDraw.end_X && event.getY() > SVDraw.start_Y && event.getY() < SVDraw.end_Y) {
//            ToastUtils.showToast(this, "点击了绿色框内空间");

//            }

            ToastUtils.showToast(TakePhotoTest.this, "touch");
            if (show_flag) {
                surface_tip.setVisibility(View.GONE);
                media_iv.setVisibility(View.VISIBLE);
                L.e("onTouch---true");
                show_flag = false;
                showImg(SVDraw.start_X, SVDraw.start_Y);

            } else {
//                if (media_iv.isPaused()){
//
//                    media_iv.setMovie(null);
//                   // media_iv=null;
//                    media_iv.setVisibility(View.GONE);
//                    surface_tip.setVisibility(View.VISIBLE);
//                }
//                media_iv.setVisibility(View.GONE);
//                surface_tip.setVisibility(View.VISIBLE);
//                L.e("onTouch---false");
//                show_flag = true;
//              //  showImg(SVDraw.start_X, SVDraw.start_Y);

            }
            return false;
        }

    }

    private void showImg(float startX, float startY) {
//        media_iv.setX(startX);
//        media_iv.setY(startY);
//        media_iv.setMovieResource(R.mipmap.s3);
//        L.e(media_iv.isPaused()+"");
        try {
            mGifDrawable = new GifDrawable(getResources(),R.mipmap.s3);
            media_iv.setImageDrawable(mGifDrawable);
            mGifDrawable.setSpeed(1.0f);
            mGifDrawable.addAnimationListener(this);
        } catch (IOException e) {
            e.printStackTrace();
        }


        switch (screenOritation){
            case  Constants.TOP:
                L.e(" Constants.TOP:"+ Constants.TOP);
                break;
            case  Constants.LEFT:
                media_iv.setRotation(-90);
                L.e(" Constants.LEFT:"+ Constants.LEFT);
                break;
            case  Constants.RIGHT:
                media_iv.setRotation(90);
                L.e(" Constants.RIGHT:"+ Constants.RIGHT);
                break;
            case  Constants.BOTTOM:
                media_iv.setRotation(180);
                L.e(" Constants.BOTTOM:"+ Constants.BOTTOM);
                break;


        }

    }
}
