package com.zhuchudong.toollibrary.okHttpUtils;


public class MyException
{
    public static void illegalArgument(String msg, Object... params)
    {
        throw new IllegalArgumentException(String.format(msg, params));
    }


}
