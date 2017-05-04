package com.example.robert.opennlptest;

import android.app.Application;
import android.content.Context;

/**
 * Created by robert on 04/05/17.
 */

public class App extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getContext(){
        return mContext;
    }
}
