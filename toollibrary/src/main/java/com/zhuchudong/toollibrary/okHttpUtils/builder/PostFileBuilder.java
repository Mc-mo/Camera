package com.zhuchudong.toollibrary.okHttpUtils.builder;


import com.zhuchudong.toollibrary.okHttpUtils.request.PostFileRequest;
import com.zhuchudong.toollibrary.okHttpUtils.request.RequestCall;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.MediaType;

/**
 * Created by Administrator on 2016/5/13.
 */
public class PostFileBuilder extends BaseOkHttpRequestBuilder {

    private File file;
    private MediaType mediaType;

    @Override
    public PostFileBuilder url(String url) {
        this.url = url;
        return this;
    }

    @Override
    public PostFileBuilder tag(Object tag) {
        this.tag = tag;
        return this;
    }

    @Override
    public PostFileBuilder headers(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    @Override
    public PostFileBuilder addHeader(String key, String val) {
        if (headers == null) {
            headers = new LinkedHashMap<>();
        }
        headers.put(key, val);
        return this;
    }

    public PostFileBuilder file(File file) {
        this.file = file;
        return this;
    }

    public PostFileBuilder mediaType(MediaType mediaType) {
        this.mediaType = mediaType;
        return this;
    }

    @Override
    public RequestCall build() {
        return new PostFileRequest(url, tag, params, headers, file, mediaType).build();
    }
}
