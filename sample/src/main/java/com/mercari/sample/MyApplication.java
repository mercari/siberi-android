package com.mercari.sample;

import android.app.Application;

import com.mercari.siberi.Siberi;


public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Siberi.setUp(this);
        // if you want to use your own storage
        //Siberi.setUpCustomStorage(new SiberiRealmDB(this));
    }
}
