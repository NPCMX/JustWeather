package com.npcmx.justweather.app.util;

/**
 * Created by CMX on 2016/5/13.
 */
public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}
