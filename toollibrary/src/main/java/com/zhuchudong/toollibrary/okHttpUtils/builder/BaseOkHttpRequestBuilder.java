package com.zhuchudong.toollibrary.okHttpUtils.builder;

import com.zhuchudong.toollibrary.okHttpUtils.request.RequestCall;

import java.util.Map;

/**
 * Created by Administrator on 2016/5/12.
 */
public abstract class BaseOkHttpRequestBuilder {
    protected String url;
    protected Object tag;
    protected Map<String, String> headers;
    protected Map<String, String> params;

    public abstract BaseOkHttpRequestBuilder url(String url);

    public abstract BaseOkHttpRequestBuilder tag(Object tag);

    public abstract BaseOkHttpRequestBuilder headers(Map<String, String> headers);

    public abstract BaseOkHttpRequestBuilder addHeader(String key, String val);

    public abstract RequestCall build();

}
