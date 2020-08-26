package com.jarvis.mvvm;

import android.app.Application;
import android.content.Context;

/**
 * @author chenjieliang on 20-8-26
 */
public class AppApplication extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        this.mContext = getApplicationContext();
    }

    public static Context getContext(){
        return  mContext;
    }
}
