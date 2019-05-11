package com.felixawpw.indoormaps.services;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.felixawpw.indoormaps.mirror.Map;
import com.felixawpw.indoormaps.view.PinView;

public class LoadImage  extends AsyncTask<String, String, Bitmap> {
    private final static String TAG = "AsyncTaskLoadImage";
    private SubsamplingScaleImageView imageView;
    private PointF pin;
    private boolean cached;
    public LoadImage(SubsamplingScaleImageView imageView, boolean cached) {
        this.imageView = imageView;
        this.cached = cached;
    }

    public LoadImage(PinView imageView, PointF pin) {
        this.imageView = imageView;
        this.pin = pin;
    }

    public LoadImage(Map map) {

    }

    @Override
    protected Bitmap doInBackground(String... params) {
        Bitmap bitmap = null;
        try {
            URL url = new URL(params[0]);
            bitmap = BitmapFactory.decodeStream((InputStream)url.getContent());
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        return bitmap;
    }
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        Log.i(TAG, bitmap + "");
        if (cached)
            imageView.setImage(ImageSource.cachedBitmap(bitmap));
        else
            imageView.setImage(ImageSource.bitmap(bitmap));
        if (pin != null && imageView instanceof PinView) {
            ((PinView)imageView).setPin(pin);
        }
    }
}
