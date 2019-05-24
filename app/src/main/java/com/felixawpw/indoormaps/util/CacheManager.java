package com.felixawpw.indoormaps.util;

import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

public class CacheManager {
    private static final CacheManager ourInstance = new CacheManager();

    public static CacheManager getInstance() {
        return ourInstance;
    }
    private LruCache<String, Bitmap> mapCache;
    public static final String CACHE_MAP_KEY_PREFIX = "map_id_";

    private CacheManager() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 3;
        Log.i("CacheManager", "Cache size = " + cacheSize);
        mapCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mapCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mapCache.get(key);
    }

}
