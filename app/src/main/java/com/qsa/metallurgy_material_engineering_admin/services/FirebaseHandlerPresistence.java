package com.qsa.metallurgy_material_engineering_admin.services;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class FirebaseHandlerPresistence extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
