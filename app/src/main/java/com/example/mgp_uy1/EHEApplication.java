package com.example.mgp_uy1;

import android.app.Application;

import io.realm.Realm;

public class EHEApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
    }
}
