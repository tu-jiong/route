package com.jm.library;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tujiong on 2018/8/16.
 */
public class Meta {

    private static final String TAG = Meta.class.getSimpleName();

    private int enterAnim = -1;
    private int exitAnim = -1;
    private int requestCode = -1;
    private int flags = -1;
    private String action;
    private String path;
    private Class clz;
    private Bundle bundle;
    private List<Interceptor> interceptors;
    private RouteListener routeListener;

    Meta(String path, Class clz) {
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

    public Meta setBundle(Bundle bundle) {
        if (this.bundle == null) {
            this.bundle = new Bundle();
        }
        this.bundle.putAll(bundle);
        return this;
    }

    public Meta requestCode(int requestCode) {
        this.requestCode = requestCode;
        return this;
    }

    public Meta setFlags(int flags) {
        this.flags = flags;
        return this;
    }

    public Meta setAnim(int enterAnim, int exitAnim) {
        this.enterAnim = enterAnim;
        this.exitAnim = exitAnim;
        return this;
    }

    public Meta setAction(String action) {
        this.action = action;
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
            if (bundle != null) {
                intent.putExtras(bundle);
            }

            if (-1 != flags) {
                intent.setFlags(flags);
            } else if (!(context instanceof Activity)) {  // Non activity, need flag.
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }

            if (!TextUtils.isEmpty(action)) {
                intent.setAction(action);
            }

            if (requestCode >= 0 && context instanceof Activity) {
                ActivityCompat.startActivityForResult((Activity) context, intent, requestCode, bundle);
            } else {
                ActivityCompat.startActivity(context, intent, bundle);
            }

            if ((-1 != enterAnim && -1 != exitAnim && context instanceof Activity)) {  // Old version.
                ((Activity) context).overridePendingTransition(enterAnim, exitAnim);
            }

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
