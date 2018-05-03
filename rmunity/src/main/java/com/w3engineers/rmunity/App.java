package com.w3engineers.rmunity;

import android.app.Application;
import android.content.Context;

/**
 * Created by USER22 on 4/24/2018.
 */

public class App extends Application {
    private static Context context;

    public static Context onContext(){
        return context;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }
}
