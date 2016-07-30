package com.example.camara.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/5/8.
 */
public class Utils {
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    final static String TAG = "Utils";

    public static boolean isScreenOriatationPortrait(Context context) {
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    public static boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }

    public static File getOutputMediaFile(Context context, int type) {
        File mediaStorageDir = null;
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            mediaStorageDir = new File(context.getExternalCacheDir(), "MyCamera");

            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.e(TAG, "创建文件目录出错");
                    return null;
                } else {
                    Log.e(TAG, "文件目录创建成功：" + mediaStorageDir);
                }
            }
        } else {
            Log.e(TAG, "没有内存卡??");
        }


        String timeStamp = new SimpleDateFormat("yyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMAGE" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VIDEO" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    public static void savepicture(Context context, byte[] bytes) {
        File pictureFile = Utils.getOutputMediaFile(context, Utils.MEDIA_TYPE_IMAGE);
        if (!pictureFile.exists()) {
            try {
                pictureFile.createNewFile();
                Log.e(TAG, "图片文件创建成功");
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "图片文件创建失败");
            }
        }
        if (pictureFile == null) {
            Log.e(TAG, "图片文件为空");
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);

            fos.write(bytes);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static Camera.Size getMaxSize(List<Camera.Size> arrayList) {

        if (arrayList != null && arrayList.size() > 0) {
            int size =arrayList.size();
            Camera.Size maxSize = arrayList.get(size-1);
            for (int i = size-2; i >= 0; i--) {
                if ((arrayList.get(i).width + arrayList.get(i).height) > (maxSize.width + maxSize.height)) {
                    maxSize = arrayList.get(i);
                }
            }
            return maxSize;
        } else {
            return null;
        }
    }

    public static Camera.Size getMaxSize(List<Camera.Size> arrayList, float scare) {
        if (arrayList != null && arrayList.size() > 0) {
            Camera.Size maxSize = arrayList.get(arrayList.size() / 2);
            for (int i = 0; i < arrayList.size(); i++) {
                if (((arrayList.get(i).width + arrayList.get(i).height) > (maxSize.width + maxSize.height) &&
                        ((float) arrayList.get(i).width / (float) arrayList.get(i).height) == scare)) {
                    maxSize = arrayList.get(i);
                }
            }
            return maxSize;
        } else {
            return null;
        }
    }

    public static List<Camera.Size> getScaleSize(List<Camera.Size> picturesizes,float scare) {
        ArrayList<Camera.Size> scaleSize=new ArrayList<>();
        for(int i=0;i<picturesizes.size();i++){
            Camera.Size picturesize = picturesizes.get(i);
            if (((float) picturesize.width / (float) picturesize.height) == scare){
                scaleSize.add(picturesize);
            }
        }
        return scaleSize;
    }

    public static Camera.Size getMiddleSize(List<Camera.Size> arrayList,Camera.Size defaultSize) {
        if (arrayList != null && arrayList.size() > 0) {
            if (arrayList.contains(defaultSize)){
                return defaultSize;
            }else {
                if (arrayList.size()>1){
                    return arrayList.get(arrayList.size()/2);
                }else {
                    arrayList.get(0);
                }
            }

        } else {
            return null;
        }
        return null;
    }
}
