package com.zhuchudong.toollibrary.okHttpUtils.builder;


import com.zhuchudong.toollibrary.okHttpUtils.request.PostBytesRequest;
import com.zhuchudong.toollibrary.okHttpUtils.request.RequestCall;

import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.MediaType;

/**
 * Created by Administrator on 2016/5/13.
 */
public class PostBytesBuilder extends BaseOkHttpRequestBuilder {

    private byte[] data;
    private MediaType mediaType;

    @Override
    public PostBytesBuilder url(String url) {
        this.url = url;
        return this;
    }

    @Override
    public PostBytesBuilder tag(Object tag) {
        this.tag = tag;
        return this;
    }

    @Override
    public PostBytesBuilder headers(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    @Override
    public PostBytesBuilder addHeader(String key, String val) {
        if (headers == null) {
            headers = new LinkedHashMap<>();
        }
        headers.put(key, val);
        return this;
    }

    public PostBytesBuilder data(byte[] data) {
        this.data = data;
        return this;
    }

    public PostBytesBuilder mediaType(MediaType mediaType) {
        this.mediaType = mediaType;
        return this;
    }

    @Override
    public RequestCall build() {
        return new PostBytesRequest(url, tag, params, headers, data, mediaType).build();
    }
}
