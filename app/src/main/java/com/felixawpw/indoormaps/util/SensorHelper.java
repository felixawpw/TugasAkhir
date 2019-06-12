package com.felixawpw.indoormaps.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.felixawpw.indoormaps.dialog.CompassDialog;

import net.kibotu.kalmanrx.KalmanRx;

import java.util.ArrayList;

import rx.Observable;
import rx.Subscription;
import rx.android.MainThreadSubscription;
import rx.subjects.BehaviorSubject;

public class SensorHelper {
    public static final String TAG = SensorHelper.class.getSimpleName();
    SensorManager mSensorManager;
    private BehaviorSubject<SensorEvent> proxy = BehaviorSubject.create();
    Context mContext;
    float mGravity[], mGeomagnetic[];
    long counter = 1;
    float accelAvg = 0f;
    long lastResetedSensorAvg = -1;
    long lastElapsed = -1;
    double totalDistance = 0;
    ArrayList<Subscription> subs;
    CompassDialog mCompassDialog;



    public SensorHelper(Context context, CompassDialog compassDialog) {
        mContext = context;
        mCompassDialog = compassDialog;
        mSensorManager =
                (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        subs = new ArrayList<>();
        createKalman();
    }

    public void destroy() {
        for (Subscription s : subs)
            s.unsubscribe();
    }

    public void createKalman() {
        Subscription acc = KalmanRx.createFrom3D(createSensorEventObservable(Sensor.TYPE_ACCELEROMETER,
                SensorManager.SENSOR_DELAY_GAME).map(e -> e.values))
                .subscribe(value->processAccel(value));
        Subscription mag =KalmanRx.createFrom3D(createSensorEventObservable(Sensor.TYPE_MAGNETIC_FIELD,
                SensorManager.SENSOR_DELAY_GAME).map(e -> e.values))
                .subscribe(value->processMagnet(value));

        subs.add(acc);
        subs.add(mag);
    }

    public double showOrientation() {
        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                double degree = (orientation[0] / Math.PI) * 180;
                if (degree < 0)
                    degree = 360 - Math.abs(degree);

                if (mCompassDialog != null)
                    mCompassDialog.rotateImage(degree);
                return degree;
            }
        }
        return -1d;
    }

    public void processMagnet(Object value) {
        float[] floatVal = (float[])value;
        mGeomagnetic = floatVal;
        if (mCompassDialog != null)
            showOrientation();
    }

    public void processAccel(Object value) {
        float[] floatVal = (float[])value;
        mGravity = floatVal;
        if (mCompassDialog != null)
            showOrientation();

        accelAvg = ((accelAvg * counter) + floatVal[0]);
        counter++;
        accelAvg = accelAvg / counter;

        if (lastResetedSensorAvg == -1) {
            lastResetedSensorAvg = System.currentTimeMillis();
            lastElapsed = System.currentTimeMillis();
        }
        long end = System.currentTimeMillis();

        double s = Math.abs(floatVal[0]) * Math.pow((double)(end - lastElapsed)/1000, 2) /2;
        lastElapsed = end;
        totalDistance += s;

        if (end - lastResetedSensorAvg > 3000) {
            accelAvg = 0f;
            counter = 1;
            totalDistance = 0;
            lastResetedSensorAvg = end;
        }
    }

    private Observable<SensorEvent> createSensorEventObservable(int sensorType, int sensorDelay) {
        Observable<SensorEvent> res = Observable.create(subscriber -> {
            MainThreadSubscription.verifyMainThread();
            SensorEventListener sel = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    if (subscriber.isUnsubscribed())
                        return;

                    subscriber.onNext(event);
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {

                }
            };

            mSensorManager.registerListener(sel, mSensorManager.getDefaultSensor(sensorType), sensorDelay);
            subscriber.add(new MainThreadSubscription() {
                @Override
                protected void onUnsubscribe() {
                    mSensorManager.unregisterListener(sel);
                }
            });
        });
        Log.d(TAG, "Success creating observable");
        return res;
    }
}
