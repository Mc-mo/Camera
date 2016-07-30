package com.zhuchudong.toollibrary.okHttpUtils.request;



import com.zhuchudong.toollibrary.okHttpUtils.callback.BaseCallBack;

import java.util.Map;

import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by Administrator on 2016/5/12.
 */
public abstract class BaseOkHttpRequest {
    protected String url;
    protected Object tag;
    protected Map<String, String> params;
    protected Map<String, String> headers;

    protected Request.Builder builder = new Request.Builder();

    protected BaseOkHttpRequest(String url, Object tag,
                                Map<String, String> params, Map<String, String> headers) {
        this.url = url;
        this.tag = tag;
        this.params = params;
        this.headers = headers;

        initBuilder();
    }

    protected void initBuilder() {
        builder.url(url).tag(tag);
        appendHeaders();
    }

    protected void appendHeaders() {
        Headers.Builder headerBuilder = new Headers.Builder();
        if (headers == null || headers.isEmpty()) return;

        for (String key : headers.keySet()) {
            headerBuilder.add(key, headers.get(key));
        }
        builder.headers(headerBuilder.build());
    }


    public RequestCall build() {
        return new RequestCall(this);
    }

    protected abstract RequestBody buildRequestBody();

    protected abstract Request buildRequest(RequestBody requestBody);

    protected RequestBody wrapRequestBody(RequestBody requestBody, final BaseCallBack callback)
    {
        return requestBody;
    }

    public Request generateRequest(BaseCallBack callback)
    {
        RequestBody requestBody = buildRequestBody();
        RequestBody wrappedRequestBody = wrapRequestBody(requestBody, callback);
        Request request = buildRequest(wrappedRequestBody);
        return request;
    }
}
