package com.example.camara;

import android.graphics.PixelFormat;
import android.graphics.drawable.Animatable;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.camara.utils.Constants;
import com.example.camara.utils.CrashHandler;
import com.example.camara.utils.ImageUtils;
import com.example.camara.utils.Utils;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
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

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback, View
        .OnClickListener {
    private static MainActivity instance;
    //宽度450
    TimerTask task;
    int id;
    byte[] compressDada;
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
    LinearLayout media_ll;
    TextView media_text;
    boolean takePhoto_flag = true;
    SimpleDraweeView media_iv;
    Animatable animation;
    DraweeController draweeController;
    ImageButton close_ib;
    //    GifImageView media_iv;
    public int screenOritation = 60;

    Handler handler = new Handler();

    Camera.PictureCallback currentCallBack;
    public OrientationEventListener mOrientationListener;

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            surface_tip.setOnTouchListener(new myTouchEventListener());
            SVDraw.location_startX = SVDraw.start_X = 0;
            SVDraw.location_startY = SVDraw.start_Y = 0;
            SVDraw.location_endX = SVDraw.end_X = 0;
            SVDraw.location_endY = SVDraw.end_Y = 0;
            media_ll.setVisibility(View.GONE);
            surface_tip.setVisibility(View.VISIBLE);
            show_flag = true;
            takePhoto_flag = true;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (camera != null) {
                        camera.takePicture(null, null, new FirstCallback());
                    }
                }
            });

        }


    };

    Runnable runnable2 = new Runnable() {
        @Override
        public void run() {
            OkHttpUtils.postBytes().url(Constants.url).data(compressDada).build().connTimeOut
                    (5000).enqueue(firstcallback);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_main);
        initView();

        holder = surface_camera.getHolder();
        holder.setKeepScreenOn(true);
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        initOrientationListener();


        CrashHandler.getInstance().init(getApplicationContext());


    }

    public static MainActivity getInstance() {
        if (instance == null) {
            instance = new MainActivity();
        }
        return instance;
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
        media_iv = (SimpleDraweeView) findViewById(R.id.media_iv);
        media_ll = (LinearLayout) findViewById(R.id.media_ll);
        media_text = (TextView) findViewById(R.id.media_text);
        media_iv.setOnClickListener(this);
        close_ib = (ImageButton) findViewById(R.id.close_ib);
        close_ib.setOnClickListener(this);
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


        List<Camera.Size> picturesizesScale = Utils.getScaleSize(picturesizes, (float)
                previewMaxSize.width / (float) previewMaxSize.height);
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
        animation = draweeController.getAnimatable();
        switch (v.getId()) {
            case R.id.media_ll:
                if (animation != null) {
                    if (animation.isRunning()) {
                        animation.stop();

                    } else {
                        animation.start();
                    }
                }
                break;
            case R.id.close_ib:
                if (animation != null) {
                    if (animation.isRunning()) {
                        animation.stop();
                        handler.post(runnable);
                    }
                }
                break;
        }


    }


    private final class FirstCallback implements Camera.PictureCallback {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            camera.startPreview();
            tv_string.append("拍照用时：" + (System.currentTimeMillis() - takephotoTime) + "ms")
                    .append("\n");
            timeView.setText(tv_string);
            processphotoTime = System.currentTimeMillis();
            compressDada = ImageUtils.processBitmapBytesSmaller2(data, Constants
                    .requestWidth, screenOritation);
//            new UploadImageTask(Constants.url, compressDada, surface_tip, screenOritation)
// .execute();
            tv_string.append("处理图片用时：" + (System.currentTimeMillis() - processphotoTime) + "ms")
                    .append("\n");
            tv_string.append("总处理用时：" + (System.currentTimeMillis() - processTime) + "ms").append
                    ("\n");
            timeView.setText(tv_string);
            netTime = System.currentTimeMillis();

            if (takePhoto_flag) {
                handler.postDelayed(runnable2, 1000);
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

            tv_string.append("请求用时：" + (System.currentTimeMillis() - netTime) + "ms").append
                    ("\n").append("\n");
            timeView.setText(tv_string);
            L.e(System.currentTimeMillis() - netTime + "ms");
            L.e("网络请求出错 " + e.toString());
            ToastUtils.showToast(MainActivity.this, "网络请求出错 " + (System.currentTimeMillis() -
                    netTime) + "ms");
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

            if (event.getX() > SVDraw.start_X && event.getX() < SVDraw.end_X &&
                    event.getY() > SVDraw.start_Y && event.getY() < SVDraw.end_Y) {
                ToastUtils.showToast(MainActivity.this, "点击了绿色框内空间");
                if (show_flag) {
                    surface_tip.setVisibility(View.GONE);
                    media_ll.setVisibility(View.VISIBLE);
                    media_ll.setOrientation(LinearLayout.VERTICAL);
                    show_flag = false;
                    showImg(SVDraw.start_X, SVDraw.start_Y, SVDraw.end_X, SVDraw.end_Y);


                }

            }


            return false;
        }


    }

    private void showImg(float startX, float startY, float endX, float endY) {
        LinearLayout.LayoutParams laParams;
        FrameLayout.LayoutParams clParams;


        surface_tip.setOnTouchListener(null);
//        WindowManager wm = (WindowManager) MainActivity.this
//                .getSystemService(Context.WINDOW_SERVICE);
//
//        int width = wm.getDefaultDisplay().getWidth();
//        int height = wm.getDefaultDisplay().getHeight();
//        L.e("size==", "width=" + width + "height=" + height);

        switch (id) {
            case 1:

                media_iv.setAspectRatio(1.33f);
                draweeController = Fresco.newDraweeControllerBuilder()
                        .setUri("res://" + getPackageName() + "/" + R.mipmap.s3)
                        .setAutoPlayAnimations(true)
                        .setControllerListener(new ControllerListener<ImageInfo>() {

                            @Override
                            public void onSubmit(String id, Object callerContext) {

                            }

                            @Override
                            public void onFinalImageSet(String id, ImageInfo imageInfo,
                                                        Animatable animatable) {

                            }

                            @Override
                            public void onIntermediateImageSet(String id, ImageInfo imageInfo) {

                            }

                            @Override
                            public void onIntermediateImageFailed(String id, Throwable throwable) {

                            }

                            @Override
                            public void onFailure(String id, Throwable throwable) {

                            }

                            @Override
                            public void onRelease(String id) {

                            }
                        })
                        .build();

                media_iv.setController(draweeController);

                media_text.setText("发动机");
                break;
            case 2:
                media_iv.setAspectRatio(1.33f);
                draweeController = Fresco.newDraweeControllerBuilder()
                        .setUri("res://" + getPackageName() + "/" + R.mipmap.s3)
                        .setAutoPlayAnimations(true)
                        .setControllerListener(new ControllerListener<ImageInfo>() {

                            @Override
                            public void onSubmit(String id, Object callerContext) {

                            }

                            @Override
                            public void onFinalImageSet(String id, ImageInfo imageInfo,
                                                        Animatable animatable) {

                            }

                            @Override
                            public void onIntermediateImageSet(String id, ImageInfo imageInfo) {

                            }

                            @Override
                            public void onIntermediateImageFailed(String id, Throwable throwable) {

                            }

                            @Override
                            public void onFailure(String id, Throwable throwable) {

                            }

                            @Override
                            public void onRelease(String id) {

                            }
                        })
                        .build();
                media_iv.setController(draweeController);

                media_text.setText("启动机");
                break;

        }

        task = new TimerTask() {

            @Override
            public void run() {
                animation = draweeController.getAnimatable();
                if (animation != null) {
                    if (!animation.isRunning()) {
                        animation.stop();

                        handler.postDelayed(runnable, 2000);
                    }
                }

            }
        };
        if (timer == null) {
            timer = new Timer();
        }

        timer.schedule(task, 100, 100);


        switch (screenOritation) {
            case Constants.TOP:
                L.e(" Constants.TOP:" + Constants.TOP);
                break;
            case Constants.LEFT:
                laParams = (LinearLayout.LayoutParams) media_iv.getLayoutParams();
                laParams.width = 320;
                laParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                media_iv.setLayoutParams(laParams);
                clParams = (FrameLayout.LayoutParams) close_ib.getLayoutParams();
                clParams.gravity = Gravity.LEFT;
                close_ib.setLayoutParams(clParams);
                close_ib.setRotation(-90);
                media_iv.setAspectRatio(1.33f);
                media_ll.setRotation(-90);
                L.e(" Constants.LEFT:" + Constants.LEFT);
                break;
            case Constants.RIGHT:
                laParams = (LinearLayout.LayoutParams) media_iv.getLayoutParams();
                laParams.width = 320;
//                laParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                clParams = (FrameLayout.LayoutParams) close_ib.getLayoutParams();
                clParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;
                close_ib.setLayoutParams(clParams);
                close_ib.setRotation(-90);
                close_ib.setTranslationY(-20);
                media_iv.setAspectRatio(1.33f);
                media_ll.setRotation(90);
                L.e(" Constants.RIGHT:" + Constants.RIGHT);
                break;
            case Constants.BOTTOM:
                media_ll.setRotation(180);
                clParams = (FrameLayout.LayoutParams) close_ib.getLayoutParams();
                clParams.gravity = Gravity.BOTTOM | Gravity.LEFT;
                close_ib.setLayoutParams(clParams);
                L.e(" Constants.BOTTOM:" + Constants.BOTTOM);
                break;


        }

        takePhoto_flag = false;

    }


}
