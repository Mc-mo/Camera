package com.example.lenovo.frescodemo;

import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;

public class MainActivity extends AppCompatActivity {
    Animatable animation;
    DraweeController draweeController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_main);

        Button play = (Button) findViewById(R.id.button);
        Button stop = (Button) findViewById(R.id.button2);

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animation = draweeController.getAnimatable();
                if (animation != null) {
                    animation.start();
                    Log.e("1111", "动画正在播放");
                }
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animation = draweeController.getAnimatable();
                if (animation.isRunning()) {
                    animation.stop();
                    Log.e("1111", "动画停止播放");
                }
            }
        });

        final SimpleDraweeView view_gif = (SimpleDraweeView) findViewById(R.id.my_image_view);
        view_gif.setAspectRatio(1.33f);
         draweeController = Fresco.newDraweeControllerBuilder()
                .setUri("res://" + getPackageName() + "/" + R.mipmap.s3)
                .setAutoPlayAnimations(true)
                .setControllerListener(new ControllerListener<ImageInfo>() {

                    @Override
                    public void onSubmit(String id, Object callerContext) {

                    }

                    @Override
                    public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable
                            animatable) {

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

        view_gif.setController(draweeController);



//        ControllerListener controllerListener = new BaseControllerListener<ImageInfo>() {
//            @Override
//            public void onFinalImageSet(
//                    String id,
//                    @Nullable ImageInfo imageInfo,
//                    @Nullable Animatable anim) {
//                if (anim != null) {
//                    // 其他控制逻辑
//                    anim.start();
//                }
//            }
//        };
//
//        Uri uri = Uri.parse("res://" + getPackageName() + "/" + R.mipmap.s3);
//        DraweeController controller = Fresco.newDraweeControllerBuilder()
//                .setUri(uri)
//
//                .setControllerListener(controllerListener)
//                // 其他设置（如果有的话）
//                .build();
//        view_gif.setController(controller);


//        Log.e("1", controller.toString());

//        try {
//            sleep(2000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        if (animation!=null){
//        animation.stop();}
////        animation.start();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Log.e("121", "-----------------------");
//                if (animation != null) {
//
//                    while (animation.isRunning()) {
//                        try {
//                            sleep(100);
//                            Log.e("111", "-----------------------");
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                    animation.stop();
//                }
//            }
//        });
//        ControllerListener controllerListener = new BaseControllerListener(){
//            @Override
//            public void onFinalImageSet(String id, Object imageInfo, Animatable animatable) {
//
//                if (animatable!=null){
//                    Animatable animation = view_gif.getController().getAnimatable();
//                    if (animation != null) {
//                        // 开始播放
//                        animation.start();
//
//                    }
//                }
//            }
//        };
//          Uri uri;
//        draweeController controller = Fresco.newDraweeControllerBuilder()
//                .setControllerListener(controllerListener)
//                .setUri(uri);
//        // other setters
//        .build();
//        view_gif.setController(controller);


    }
}
