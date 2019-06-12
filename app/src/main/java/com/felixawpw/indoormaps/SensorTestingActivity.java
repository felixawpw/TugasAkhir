package com.felixawpw.indoormaps;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import net.kibotu.kalmanrx.KalmanRx;

import rx.Observable;
import rx.Observer;
import rx.Scheduler;
import rx.android.MainThreadSubscription;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

public class SensorTestingActivity extends AppCompatActivity implements SensorEventListener{

    public static final String TAG = SensorTestingActivity.class.getSimpleName();
    SensorManager mSensorManager;
    private Sensor mSensorAccelerometer;
    private Sensor mSensorMagneticField;
    private BehaviorSubject<SensorEvent> proxy = BehaviorSubject.create();

    TextView textAccel, textAccelAvg, textCompass, textCompassAvg, textMagnetic, textMagneticAvg;
    LinearLayout scrollDistanceLayout;
    float mGravity[], mGeomagnetic[];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_testing);
        textAccel = findViewById(R.id.sensor_accelerometer);
        textAccelAvg = findViewById(R.id.sensor_accelerometer_average);
        textCompass = findViewById(R.id.sensor_compass);
        textCompassAvg = findViewById(R.id.sensor_compass_average);
        textMagnetic = findViewById(R.id.sensor_magnetic_field);
        textMagneticAvg = findViewById(R.id.sensor_magnetic_field_average);
        scrollDistanceLayout = findViewById(R.id.sensor_scroll_layout);
//        KalmanRx.createFrom3D(createSensorEventObservable(Sensor.TYPE_MAGNETIC_FIELD,
//                SensorManager.SENSOR_DELAY_NORMAL).map(e -> e.values));


//        if (mSensorManager != null)
//            mSensorManager.registerListener(this, mSensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
//        if (mSensorMagneticField != null)
//            mSensorManager.registerListener(this, mSensorMagneticField, SensorManager.SENSOR_DELAY_NORMAL);
//
//        proxy.subscribe(new Observer<SensorEvent>() {
//            @Override
//            public void onCompleted() {
//
//            }
//
//            @Override
//            public void onError(Throwable e) {
//
//            }
//
//            @Override
//            public void onNext(SensorEvent event) {
//                int type = event.sensor.getType();
//
//                if (type == Sensor.TYPE_ACCELEROMETER) {
//                    mGravity = event.values;
//                    textAccel.setText("Accelerometer Value = " + mGravity[0]);
//                }
//
//                if (type == Sensor.TYPE_MAGNETIC_FIELD) {
//                    mGeoMagnetic = event.values;
//                    textMagnetic.setText("Magnetic Value = " + mGeoMagnetic[0]);
//                }
//
//                if (mGravity != null && mGeoMagnetic != null) {
//                    float R[] = new float[9];
//                    float I[] = new float[9];
//                    boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeoMagnetic);
//                    if (success) {
//                        float orientation[] = new float[3];
//                        SensorManager.getOrientation(R, orientation);
//                        textCompass.setText("Orientation = " + orientation[0]);
//                    }
//                }
//            }
//        });


    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        proxy.onNext(event);
    }

}
