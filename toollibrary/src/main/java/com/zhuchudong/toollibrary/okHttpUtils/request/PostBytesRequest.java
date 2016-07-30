package com.zhuchudong.toollibrary.okHttpUtils.request;



import com.zhuchudong.toollibrary.okHttpUtils.MyException;

import java.util.Map;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by Administrator on 2016/5/13.
 */
public class PostBytesRequest extends BaseOkHttpRequest {

    private static MediaType MEDIA_TYPE_STREAM = MediaType.parse("application/octet-stream");
    private byte[] data;
    private MediaType mediaType;

    public PostBytesRequest(String url, Object tag, Map<String, String> params, Map<String, String> headers, byte[] data, MediaType mediaType) {
        super(url, tag, params, headers);
        this.data = data;
        this.mediaType = mediaType;

        if (data == null) {
            MyException.illegalArgument("the data can not be null !");
        }
        if (this.mediaType == null) {
            this.mediaType = MEDIA_TYPE_STREAM;
        }
    }

    @Override
    protected RequestBody buildRequestBody() {
        return RequestBody.create(mediaType, data);
    }

    @Override
    protected Request buildRequest(RequestBody requestBody) {
        return builder.post(requestBody).build();
    }
}
