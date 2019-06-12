package com.felixawpw.indoormaps.services;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.felixawpw.indoormaps.dialog.LoadingDialog;
import com.felixawpw.indoormaps.mirror.Map;
import com.felixawpw.indoormaps.navigation.ImageCustom;
import com.felixawpw.indoormaps.util.CacheManager;
import com.felixawpw.indoormaps.view.PinView;

public class LoadImage extends AsyncTask<String, String, Bitmap> {
    private final static String TAG = "AsyncTaskLoadImage";
    private SubsamplingScaleImageView imageView;
    private PointF pin;
    private boolean cached;
    private Map map;
    private int type;
    public LoadImage(SubsamplingScaleImageView imageView, boolean cached) {
        this.imageView = imageView;
        this.cached = cached;
        type = 1;
    }

    public LoadImage(PinView imageView, PointF pin) {
        this.imageView = imageView;
        this.pin = pin;
        type = 2;
    }

    public LoadImage(PinView imageView, Map map) {
        this.imageView = imageView;
        this.map = map;
        type = 3;
    }

    public LoadImage(PinView imageView, Map map, boolean cached) {
        this.imageView = imageView;
        this.map = map;
        type = 3;
        this.cached = cached;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        Bitmap bitmap = null;
        try {
            switch (type) {
                case 3:
                    Bitmap cachedMap = CacheManager.getInstance().getBitmapFromMemCache(CacheManager.CACHE_MAP_KEY_PREFIX + map.getId());
                    if (cachedMap != null && cached) {
                        bitmap = cachedMap;
                        Log.i(TAG, "Map is cached");
                    }
                    else {
                        URL url = new URL(params[0]);
                        bitmap = BitmapFactory.decodeStream((InputStream)url.getContent());
                        CacheManager.getInstance().addBitmapToMemoryCache(CacheManager.CACHE_MAP_KEY_PREFIX + map.getId(), bitmap);
                        bitmap = CacheManager.getInstance().getBitmapFromMemCache(CacheManager.CACHE_MAP_KEY_PREFIX + map.getId());
                    }

                    map.setCustomImage(new ImageCustom(bitmap));
                    break;
                default:
                    URL url = new URL(params[0]);
                    bitmap = BitmapFactory.decodeStream((InputStream)url.getContent());
                    break;
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        return bitmap;
    }
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (map != null) {
            Log.i(TAG, bitmap + " + " + CacheManager.getInstance().getBitmapFromMemCache(CacheManager.CACHE_MAP_KEY_PREFIX + map.getId()));
            Log.i(TAG, "Bitmap = " + bitmap.isRecycled());
        }

        if (imageView != null) {
//            imageView.invalidate();
            if (cached)
                imageView.setImage(ImageSource.cachedBitmap(CacheManager.getInstance().getBitmapFromMemCache(CacheManager.CACHE_MAP_KEY_PREFIX + map.getId())));
            else
                imageView.setImage(ImageSource.bitmap(bitmap));


            if (pin != null && imageView instanceof PinView) {
                ((PinView)imageView).setPin(pin);
            }
        }
    }
}
