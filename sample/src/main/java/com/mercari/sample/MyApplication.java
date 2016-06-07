package com.mercari.sample;

import android.app.Application;

import com.mercari.sample.db.SiberiRealmDB;
import com.mercari.siberi.Siberi;


public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //Siberi.setUp(this);
        Siberi.setUpCustomStorage(new SiberiRealmDB(this));
    }
}
