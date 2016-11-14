package com.example.camara.utils;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.example.camara.MainActivity;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;

/**
 * Created by lenovo on 2016/9/22.
 */
public class Speaker {

    private static Speaker mSpeaker;
    private Context mContext;
    private String TAG = MainActivity.class.getSimpleName();
    //语音合成对象
    private SpeechSynthesizer mTts;
    // 默认发音人
    private String voicer = "xiaoqi";

    public void speak(String bookName) {
        Log.e(TAG, "speak: "+bookName);
        mTts = SpeechSynthesizer.createSynthesizer(mContext, mTtsInitListener);
        mTts.setParameter(SpeechConstant.PARAMS, null);
        // 根据合成引擎设置相应参数

        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        // 设置在线合成发音人
        mTts.setParameter(SpeechConstant.VOICE_NAME, voicer);
        mTts.startSpeaking(bookName, mTtsListener);
    }

    public void stopSpeek() {
        if (mTts != null && mTts.isSpeaking()) {
            mTts.stopSpeaking();
        }
    }


    private Speaker(Context context) {
        mContext = context;
    }

    public static Speaker getInstance(Context context) {

        if (mSpeaker == null) {
            synchronized (Speaker.class) {
                if (mSpeaker == null) {
                    mSpeaker = new Speaker(context);
                }
            }
        }
        return mSpeaker;


    }

    private SynthesizerListener mTtsListener = new SynthesizerListener() {

        @Override
        public void onSpeakBegin() {

        }

        @Override
        public void onSpeakPaused() {

        }

        @Override
        public void onSpeakResumed() {

        }

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos,
                                     String info) {

        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {

        }

        @Override
        public void onCompleted(SpeechError error) {
            if (error == null) {
            } else if (error != null) {

            }
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
        }
    };

    /**
     * 初始化监听。
     */
    private InitListener mTtsInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            Log.d(TAG, "InitListener init() code = " + code);
            if (code != ErrorCode.SUCCESS) {

            } else {
                // 初始化成功，之后可以调用startSpeaking方法
                // 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
                // 正确的做法是将onCreate中的startSpeaking调用移至这里
            }
        }
    };
}

