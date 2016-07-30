package com.zhuchudong.toollibrary.okHttpUtils.callback;


import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/5/12.
 */
public abstract class BaseCallBack<T> {

    public void onBefore(Request request) {
    }

    public void onAfter() {
    }

    public void inProgress(float progress) {

    }

    public abstract T parseNetworkResponse(Response response) throws Exception;

    public abstract void onError(Call call, Exception e);

    public abstract void onResponse(T response);

}
