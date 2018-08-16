package com.jm.route;

import android.app.Application;

import com.jm.library.Router;

/**
 * Created by tujiong on 2018/8/16.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Router.init(this);
    }
}
