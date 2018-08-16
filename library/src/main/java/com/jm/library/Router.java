package com.jm.library;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Map;

/**
 * Created by tujiong on 2018/8/15.
 */
public class Router {

    private static final String TAG = Router.class.getSimpleName();

    private Factory factory;

    private static class Singleton {
        private static final Router sInstance = new Router();
    }

    private Router() {
        try {
            Class<?> clz = Class.forName("com.jm.route.DefaultFactory");
            factory = (Factory) clz.newInstance();
            factory.load();
        } catch (ClassNotFoundException e) {
            Log.e(TAG, e.getMessage());
        } catch (IllegalAccessException e) {
            Log.e(TAG, e.getMessage());
        } catch (InstantiationException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public static Router get() {
        return Singleton.sInstance;
    }

    public void navigation(Context context, String path) {
        if (factory != null) {
            Map<String, Class> map = factory.get();
            Class clz = map.get(path);
            try {
                Intent intent = new Intent(context, clz);
                context.startActivity(intent);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }
}
