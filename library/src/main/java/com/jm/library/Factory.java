package com.jm.library;

import java.util.Map;

/**
 * Created by tujiong on 2018/8/15.
 */
public interface Factory {

    void load();

    Map<String, Class> get();
}
