package com.rei.aph;

import android.app.Application;

public class SketchApplication extends Application {

    private static SketchApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static SketchApplication getInstance() {
        return instance;
    }
}
