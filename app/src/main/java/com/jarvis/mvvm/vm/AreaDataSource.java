package com.jarvis.mvvm.vm;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.jarvis.mvvm.net.NetworkApi;
import com.jarvis.mvvm.repository.JsonCacheObjectSource;
import com.jarvis.mvvm.repository.Request;

import io.reactivex.Observable;

/**
 * @author chenjieliang on 20-8-26
 */
public class AreaDataSource extends JsonCacheObjectSource<String,String> {

    @NonNull
    @Override
    protected String getCacheKey(String request) {
        return "weather-" + request ;
    }

    @NonNull
    @Override
    protected Observable<String> requestNetwork(@Nullable String areaName) {
        return NetworkApi.getWeather(areaName);
    }
}
