package com.jm.library;

/**
 * Created by tujiong on 2018/8/16.
 */
public interface RouteListener {

    void onIntercepted();

    void onComplete();

    void onError(Throwable throwable);
}
