package com.example.camara;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.camara.utils.Constants;
import com.example.camara.utils.Utils;
import com.zhuchudong.toollibrary.L;

/**
 * Created by Administrator on 2016/5/8.
 */
public class SplashActivity extends AppCompatActivity {
    static final int CAMERA_CODE = 1;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        L l = new L();
        l.startWriteLogToSdcard(getExternalCacheDir() + "log.txt", true);
        setContentView(R.layout.activity_splash);

        Button start = (Button) findViewById(R.id.start);
        if (start != null) {
            start.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                    gotoActivity();
                }
            });
        }


        Button test = (Button) findViewById(R.id.test);
        if (test != null) {
            test.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent(SplashActivity.this, TakePhotoTest.class);
                    gotoActivity();
                }
            });
        }

        EditText et_id = (EditText) findViewById(R.id.et_url);
        if (et_id != null) {
            Constants.url = et_id.getText().toString();
        }


    }

    private void gotoActivity() {
        if (Utils.checkCameraHardware(SplashActivity.this)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!(checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager
                        .PERMISSION_GRANTED)) {
                    requestCameraPermission();
                } else {
                    startActivity(intent);

                }
            } else {
                startActivity(intent);
            }
        } else {
            Toast.makeText(SplashActivity.this, "手机无可摄像头", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void requestCameraPermission() {
        requestPermissions(new String[]{android.Manifest.permission.CAMERA}, CAMERA_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_CODE) {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
        }

    }
}
