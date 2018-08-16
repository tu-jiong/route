package com.jm.library;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by tujiong on 2018/8/15.
 */
public class Router {

    private static final String TAG = Router.class.getSimpleName();

    private static Map<String, Class> routeMap = new HashMap<>();

    private static class Singleton {
        private static final Router sInstance = new Router();
    }

    public static void init(Context context) {
        try {
            Set<String> set = ClassUtils.getFileNameByPackageName(context, "com.jm.route.gen");
            for (String name : set) {
                try {
                    Class<?> clz = Class.forName(name);
                    Factory factory = (Factory) clz.newInstance();
                    factory.load();
                    Map<String, Class> map = factory.get();
                    routeMap.putAll(map);
                } catch (ClassNotFoundException e) {
                    Log.e(TAG, e.getMessage());
                } catch (IllegalAccessException e) {
                    Log.e(TAG, e.getMessage());
                } catch (InstantiationException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Router() {
    }

    public static Router get() {
        return Singleton.sInstance;
    }

    public void navigation(Context context, String path) {
        Class clz = routeMap.get(path);
        try {
            Intent intent = new Intent(context, clz);
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }
}
