package com.jm.library;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import java.net.URLDecoder;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by tujiong on 2018/8/17.
 */
public class DefaultParser implements Parser {

    private static final String TAG = DefaultParser.class.getSimpleName();

    private Map<String, Class> map;

    DefaultParser(Map<String, Class> map) {
        this.map = map;
    }

    @Override
    public Meta parse(String route) {
        if (map == null) {
            return null;
        }

        try {
            Uri uri = Uri.parse(route);
            String scheme = uri.getScheme();
            String host = uri.getHost();
            String path = uri.getPath();
            String query = uri.getEncodedQuery();

            Log.e(TAG, scheme + " , " + host + " , " + path + " , " + query);

            Class clz = map.get(scheme + "://" + host + path);
            Meta meta = new Meta(route, clz);
            if (!TextUtils.isEmpty(query)) {
                Map<String, String> map = parseParams(query);
                Bundle bundle = parseMapToBundle(map);
                meta.setBundle(bundle);
            }

            return meta;
        } catch (Exception e) {
            return null;
        }
    }

    private Map<String, String> parseParams(String query) {
        Map<String, String> params = new IdentityHashMap<>();
        String[] split = query.split("&");
        for (String param : split) {
            if (!param.contains("=")) {
                continue;
            }
            int index = param.indexOf("=");
            params.put(param.substring(0, index), URLDecoder.decode(param.substring(index + 1)));
        }
        return params;
    }

    private Bundle parseMapToBundle(Map<String, String> map) {
        Bundle bundle = new Bundle();
        Set<String> keySet = map.keySet();
        for (String key : keySet) {
            bundle.putString(key, map.get(key));
        }
        return bundle;
    }
}
