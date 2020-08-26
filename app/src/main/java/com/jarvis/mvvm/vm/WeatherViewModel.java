package com.jarvis.mvvm.vm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.jarvis.mvvm.repository.JsonCacheObjectSource;
import com.jarvis.mvvm.repository.UtLiveData;

/**
 * @author chenjieliang on 20-8-26
 */
public class WeatherViewModel extends AndroidViewModel {

    private AreaDataSource areaDataSource;

    public WeatherViewModel(@NonNull Application application) {
        super(application);
        areaDataSource = new AreaDataSource();
    }

    public UtLiveData<String> getAreaLiveData() {
        return areaDataSource.getLiveData();
    }

    public void getWeather(String areaName) {
        areaDataSource.fetchData(areaName);
    }
}
