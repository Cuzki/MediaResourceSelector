package com.example.cuzki.mediaselectordemo;

import android.app.Application;

/**
 * @author yangz
 * @version 2015/2/26.
 */
public class ClassroomApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppContextUtils.init(this);
    }
}