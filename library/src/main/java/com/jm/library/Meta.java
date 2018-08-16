package com.jm.library;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tujiong on 2018/8/16.
 */
public class Meta {

    private static final String TAG = Meta.class.getSimpleName();

    private String path;
    private Class clz;
    private List<Interceptor> interceptors;
    private RouteListener routeListener;

    public Meta(String path, Class clz) {
        this.path = path;
        this.clz = clz;
    }

    public Meta addInterceptor(Interceptor interceptor) {
        if (interceptors == null) {
            interceptors = new ArrayList<>();
        }
        if (interceptor != null && !interceptors.contains(interceptor)) {
            interceptors.add(interceptor);
        }
        return this;
    }

    public Meta setRouteListener(RouteListener listener) {
        routeListener = listener;
        return this;
    }

    public String getPath() {
        return path;
    }

    public void navigation(Context context) {
        if (interceptors != null) {
            for (Interceptor interceptor : interceptors) {
                if (interceptor != null) {
                    if (interceptor.intercept()) {
                        onIntercepted();
                        return;
                    }
                }
            }
        }

        try {
            Intent intent = new Intent(context, clz);
            context.startActivity(intent);
            onComplete();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            onError(e);
        }
    }

    private void onIntercepted() {
        if (routeListener != null) {
            routeListener.onIntercepted();
        }
    }

    private void onComplete() {
        if (routeListener != null) {
            routeListener.onComplete();
        }
    }

    private void onError(Throwable throwable) {
        if (routeListener != null) {
            routeListener.onError(throwable);
        }
    }
}
