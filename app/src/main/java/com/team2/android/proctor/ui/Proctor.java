package com.team2.android.proctor.ui;

import android.app.Application;

import com.team2.android.proctor.util.SessionManager;

/**
 * Created by kofikyei on 4/16/16.
 */
public class Proctor extends Application{

    SessionManager manager;

    private static Proctor instance;

    public Proctor getInstance(){
        if(instance == null) return instance;
        return null;
    }
    @Override
    public void onCreate() {
        manager = new SessionManager(this);
        super.onCreate();
        instance = this;
    }

    public SessionManager getSession(){
        return manager;
    }
}
