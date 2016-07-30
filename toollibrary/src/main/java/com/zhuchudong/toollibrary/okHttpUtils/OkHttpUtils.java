package com.zhuchudong.toollibrary.okHttpUtils;

import android.os.Handler;
import android.os.Looper;

import com.zhuchudong.toollibrary.okHttpUtils.builder.GetBuilder;
import com.zhuchudong.toollibrary.okHttpUtils.builder.PostBytesBuilder;
import com.zhuchudong.toollibrary.okHttpUtils.builder.PostFileBuilder;
import com.zhuchudong.toollibrary.okHttpUtils.builder.PostFormBuilder;
import com.zhuchudong.toollibrary.okHttpUtils.builder.PostStringBuilder;
import com.zhuchudong.toollibrary.okHttpUtils.callback.BaseCallBack;
import com.zhuchudong.toollibrary.okHttpUtils.request.RequestCall;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/5/12.
 */
public class OkHttpUtils {
    public static final long DEFAULT_MILLISECONDS = 5000;

    Handler mDelivery;
    private static OkHttpUtils mInstances;
    private OkHttpClient mOkhttpClient;

    public OkHttpUtils() {
        if (mOkhttpClient == null) {
            mOkhttpClient = new OkHttpClient();
        }
        mDelivery = new Handler(Looper.getMainLooper());
    }

    public Handler getDelivery() {
        return mDelivery;
    }

    public static OkHttpUtils getInstance() {
        if (mInstances == null) {
            synchronized (OkHttpUtils.class) {
                if (mInstances == null) {
                    mInstances = new OkHttpUtils();
                }
            }
        }
        return mInstances;
    }


    public OkHttpClient getOkhttpClient() {
        return mOkhttpClient;
    }

    /**
     * Get
     */
    public static GetBuilder get() {
        return new GetBuilder();
    }

    /**
     * Post 表单数据
     */
    public static PostFormBuilder post() {
        return new PostFormBuilder();
    }

    /**
     * Post 上传文件
     */
    public static PostFileBuilder postFile() {
        return new PostFileBuilder();
    }

    /**
     * Post 上传 String 内容
     * leeco
     */
    public static PostStringBuilder PostString() {
        return new PostStringBuilder();
    }

    /**
     * Post 上传 Byte[] 数据
     */
    public static PostBytesBuilder postBytes() {
        return new PostBytesBuilder();
    }

    public void cancelTag(Object tag) {
        for (Call call : mOkhttpClient.dispatcher().queuedCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
        for (Call call : mOkhttpClient.dispatcher().runningCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
    }

    public void cancelTag() {
        for (Call call : mOkhttpClient.dispatcher().runningCalls()) {
            call.cancel();
        }
        for (Call call : mOkhttpClient.dispatcher().queuedCalls()) {
            call.cancel();
        }

    }


    public void enqueue(RequestCall requestCall, final BaseCallBack callback) {
        requestCall.getCall().enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, final IOException e) {
                if (callback != null) {
                    mDelivery.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onError(call, e);
                            callback.onAfter();
                        }
                    });
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {


                try {
                    final Object object = callback.parseNetworkResponse(response);

                    if (callback != null) {
                        mDelivery.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onResponse(object);
                                callback.onAfter();
                            }
                        });
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
