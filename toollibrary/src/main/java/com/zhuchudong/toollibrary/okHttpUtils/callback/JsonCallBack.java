package com.zhuchudong.toollibrary.okHttpUtils.callback;

import org.json.JSONObject;

import okhttp3.Response;

/**
 * Created by Administrator on 2016/5/12.
 */
public abstract class JsonCallBack extends BaseCallBack<JSONObject> {
    @Override
    public JSONObject parseNetworkResponse(Response response) throws Exception {
        return new JSONObject(response.body().string());
    }

}
