package com.zhuchudong.toollibrary.okHttpUtils.builder;

import java.util.Map;

/**
 * Created by zhy on 16/3/1.
 */
public interface HasParamsable
{
    public abstract BaseOkHttpRequestBuilder params(Map<String, String> params);

    public abstract BaseOkHttpRequestBuilder addParams(String key, String val);

}
