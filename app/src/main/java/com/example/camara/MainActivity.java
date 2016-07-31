package com.example.camara;

import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.camara.utils.Constants;
import com.example.camara.utils.CrashHandler;
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
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import pl.droidsonroids.gif.AnimationListener;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback, View.OnClickListener, AnimationListener {
    private static MainActivity instance;
    //宽度450
    TimerTask task;
    int id;
    long netTime;
    long processTime;
    long takephotoTime;
    long processphotoTime;
    private Timer timer;
    Camera camera;
    SurfaceHolder holder;
    SurfaceView surface_camera;
    SVDraw surface_tip;
    TextView timeView;
    StringBuffer tv_string = new StringBuffer();
    volatile int error_count = 0;
    boolean show_flag = true;

    boolean takePhoto_flag = true;

//    LocalBroadcastReceiver localReceiver;
    GifImageView media_iv;
    public int screenOritation = 60;
    private GifDrawable mGifDrawable;
    Handler handler = new Handler();

    Camera.PictureCallback currentCallBack;
    public OrientationEventListener mOrientationListener;

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            surface_tip.setOnTouchListener(new myTouchEventListener());
            mGifDrawable.recycle();

            media_iv.setVisibility(View.GONE);
            surface_tip.setVisibility(View.VISIBLE);
            show_flag = true;
            takePhoto_flag = true;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (camera != null) {
                        // camera.startPreview();
                        camera.takePicture(null, null, new FirstCallback());
                    }
                }
            });

        }


    };

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

//        initReceiver();

        CrashHandler.getInstance().init(getApplicationContext());


    }

    public static MainActivity getInstance() {
        if (instance == null) {
            instance = new MainActivity();
        }
        return instance;
    }

    private void initReceiver() {
//        localReceiver = new LocalBroadcastReceiver();

//        LocalBroadcastManager.getInstance(this).registerReceiver(localReceiver, new IntentFilter("ACTION_LOCAL_SEND"));

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (timer == null) {
            timer = new Timer();
            initSchedule();
        }
        surface_tip.setOnTouchListener(new myTouchEventListener());
    }

    @Override
    protected void onStop() {
        super.onStop();
        OkHttpUtils.getInstance().getOkhttpClient().dispatcher().cancelAll();
        L.e("onStop");
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(localReceiver);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
        if (mOrientationListener != null) {
            mOrientationListener.disable();
        }
    }

    private void initView() {
        StatusBarUtil.setColor(MainActivity.this, getResources().getColor(R.color.colorPrimary));
        surface_camera = (SurfaceView) findViewById(R.id.surface_camera);
        surface_tip = (SVDraw) findViewById(R.id.surface_tip);
        timeView = (TextView) findViewById(R.id.tv_time);
        media_iv = (GifImageView) findViewById(R.id.media_iv);


    }

    public void initSchedule() {
        task = new TimerTask() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (camera != null) {
                            takephotoTime = System.currentTimeMillis();
                            processTime = System.currentTimeMillis();
                            camera.takePicture(null, null, new FirstCallback());

                        }

                    }
                });

            }
        };
        if (timer == null) {
            timer = new Timer();
        }
//        timer.schedule(task, 2000, 2000);
        timer.schedule(task, 2000);
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
    public void onClick(View v) {

    }

    @Override
    public void onAnimationCompleted(int loopNumber) {
       handler.postDelayed(runnable,2000);

    }

    private final class FirstCallback implements Camera.PictureCallback {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            camera.startPreview();
            tv_string.append("拍照用时：" + (System.currentTimeMillis() - takephotoTime) + "ms").append("\n");
            timeView.setText(tv_string);
            processphotoTime = System.currentTimeMillis();
            byte[] compressDada = ImageUtils.processBitmapBytesSmaller2(data, Constants.requestWidth, screenOritation);
//            new UploadImageTask(Constants.url, compressDada, surface_tip, screenOritation).execute();
            tv_string.append("处理图片用时：" + (System.currentTimeMillis() - processphotoTime) + "ms").append("\n");
            tv_string.append("总处理用时：" + (System.currentTimeMillis() - processTime) + "ms").append("\n");
            timeView.setText(tv_string);
            netTime = System.currentTimeMillis();
//            ToastUtils.showToast(MainActivity.this, error_count + "");
//            if (error_count >= 1) {
//                if (media_iv != null) {
//                    media_iv = null;
//                    media_iv.setVisibility(View.GONE);
//                    surface_tip.setVisibility(View.VISIBLE);
//                    L.e("onTouch---false");
//                    show_flag = true;
//                }
//
//            }
            if (takePhoto_flag) {
                OkHttpUtils.postBytes().url(Constants.url).data(compressDada).build().connTimeOut(5000).enqueue(firstcallback);
            }

        }
    }


    private JsonCallBack firstcallback = new JsonCallBack() {
        @Override
        public void onError(Call call, Exception e) {
            if (!AppUtils.isTopActivity(MainActivity.this, "com.example.camara.MainActivity")) {
                L.e("isTopActivity==false");
                return;
            }

            tv_string.append("请求用时：" + (System.currentTimeMillis() - netTime) + "ms").append("\n").append("\n");
            timeView.setText(tv_string);
            L.e(System.currentTimeMillis() - netTime + "ms");
            L.e("网络请求出错 " + e.toString());
            ToastUtils.showToast(MainActivity.this, "网络请求出错 " + (System.currentTimeMillis() - netTime) + "ms");
            netTime = System.currentTimeMillis();
            processTime = System.currentTimeMillis();
            takephotoTime = System.currentTimeMillis();
            camera.takePicture(null, null, new FirstCallback());
        }

        @Override
        public void onResponse(JSONObject response) {
            if (!AppUtils.isTopActivity(MainActivity.this, "com.example.camara.MainActivity")) {
                L.e("isTopActivity==false");
                return;
            }
            L.e("response", response.toString());
            tv_string.append("请求用时：" + (System.currentTimeMillis() - netTime) + "ms").append("\\n");
            timeView.setText(tv_string);
            L.e(System.currentTimeMillis() - netTime + "");

            netTime = System.currentTimeMillis();
            if (response != null && response.has("bounding_rects")) {

                JSONArray locations = response.optJSONArray("bounding_rects");

//                if (locations.length()==0){
//
//                    error_count++;
//                    if (error_count>=1){
//                        handler.post(runnable);
//                    }
//                }else {
//                    error_count=0;
//                }

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
            takephotoTime = System.currentTimeMillis();
            processTime = System.currentTimeMillis();
            camera.takePicture(null, null, new FirstCallback());
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
//            if (GifView.isPlaying) {
//                return false;
//            }
//            if (mGifDrawable.isRunning()) {
//                return false;
//            }
            WindowManager wm = (WindowManager) MainActivity.this
                    .getSystemService(Context.WINDOW_SERVICE);
            int width = wm.getDefaultDisplay().getWidth();
            int height = wm.getDefaultDisplay().getHeight();
            switch (screenOritation) {
                case Constants.TOP:
                    if (event.getX() > SVDraw.start_X && event.getX() < SVDraw.end_X && event.getY() > SVDraw.start_Y && event.getY() < SVDraw.end_Y) {
                        ToastUtils.showToast(MainActivity.this, "点击了绿色框内空间");
                        if (show_flag) {
                            surface_tip.setVisibility(View.GONE);
                            media_iv.setVisibility(View.VISIBLE);
                            show_flag = false;
                            showImg(SVDraw.start_X, SVDraw.start_Y, SVDraw.end_X, SVDraw.end_Y);


                        }
                    }
                    break;
                case Constants.BOTTOM:

                    if (event.getX() > (width - SVDraw.end_X) && event.getX() < (width - SVDraw.start_X) && event.getY() > (height - SVDraw.end_Y) && event.getY() < (height - SVDraw.start_Y)) {
                        ToastUtils.showToast(MainActivity.this, "点击了绿色框内空间");
                        if (show_flag) {
                            surface_tip.setVisibility(View.GONE);
                            media_iv.setVisibility(View.VISIBLE);
                            show_flag = false;
                            showImg(SVDraw.start_X, SVDraw.start_Y, SVDraw.end_X, SVDraw.end_Y);


                        }
                    }
                    break;
                case Constants.LEFT:
                    if (event.getX() > SVDraw.start_Y && event.getX() < SVDraw.end_Y && event.getY() > (width - SVDraw.end_X) && event.getY() < (width - SVDraw.start_X)) {
                        ToastUtils.showToast(MainActivity.this, "点击了绿色框内空间");
                        if (show_flag) {
                            surface_tip.setVisibility(View.GONE);
                            media_iv.setVisibility(View.VISIBLE);
                            show_flag = false;
                            showImg(SVDraw.start_X, SVDraw.start_Y, SVDraw.end_X, SVDraw.end_Y);
                        }

                    }
                    break;
                case Constants.RIGHT:
                    if (event.getX() > (height - SVDraw.end_Y) && event.getX() < (height - SVDraw.start_Y) && event.getY() > SVDraw.start_X && event.getY() < SVDraw.end_X) {
                        ToastUtils.showToast(MainActivity.this, "点击了绿色框内空间");
                        if (show_flag) {
                            surface_tip.setVisibility(View.GONE);
                            media_iv.setVisibility(View.VISIBLE);
                            show_flag = false;
                            showImg(SVDraw.start_X, SVDraw.start_Y, SVDraw.end_X, SVDraw.end_Y);
                        }

                    }
                    break;

            }

            return false;
        }


    }

    private void showImg(float startX, float startY, float endX, float endY) {
        surface_tip.setOnTouchListener(null);
        WindowManager wm = (WindowManager) MainActivity.this
                .getSystemService(Context.WINDOW_SERVICE);

        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        L.e("size==", "width=" + width + "height=" + height);
//        media_iv.setX(startX);
//        media_iv.setY(startY);

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) media_iv.getLayoutParams();

        if (isScreenOriatationPortrait(MainActivity.this)) {
            params.width = width;
            media_iv.setLayoutParams(params);
        } else {
            params.height = height;
            media_iv.setLayoutParams(params);
        }

//        media_iv.setPaused(false);
        switch (id) {
            case 1:
                try {
                    mGifDrawable = new GifDrawable(getResources(),R.mipmap.s3);
                    media_iv.setImageDrawable(mGifDrawable);
                    mGifDrawable.setSpeed(1.0f);
                    mGifDrawable.addAnimationListener(this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case 2:
                try {
                    mGifDrawable = new GifDrawable(getResources(),R.mipmap.s2);
                    media_iv.setImageDrawable(mGifDrawable);
                    mGifDrawable.setSpeed(1.0f);
                    mGifDrawable.addAnimationListener(this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }

        switch (screenOritation) {
            case Constants.TOP:
                break;
            case Constants.LEFT:
                media_iv.setRotation(-90);
                L.e("size===", media_iv.getHeight() + "");
                media_iv.setTranslationX(-media_iv.getWidth());
                break;
            case Constants.RIGHT:
                media_iv.setRotation(90);
                media_iv.setTranslationY(-media_iv.getWidth());
                break;
            case Constants.BOTTOM:
                media_iv.setRotation(180);
                media_iv.setTranslationX(-media_iv.getWidth());
                media_iv.setTranslationY(- media_iv.getHeight());
                break;


        }

        takePhoto_flag = false;

    }


//    public class LocalBroadcastReceiver extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//
//        }
//    }

    /**
     * 返回当前屏幕是否为竖屏。
     *
     * @param context
     * @return 当且仅当当前屏幕为竖屏时返回true, 否则返回false。
     */
    public static boolean isScreenOriatationPortrait(Context context) {
        if (context.getResources().getConfiguration().orientation == 1) {
            return true;
        } else {
            return false;
        }
    }


}
