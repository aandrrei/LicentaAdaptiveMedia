package com.example.andre.licentaadaptivemedia;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import static android.hardware.Sensor.TYPE_STEP_COUNTER;

/**
 * Created by andre on 20.03.2018.
 */

public class StepsBoundService extends Service implements SensorEventListener {

    private static String LOG_TAG = "StepsBoundService";
    private IBinder mBinder = new MyBinder();

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private boolean isSensorPresent;

    private long firstTimeStamp;
    private long secondTimeStamp;
    private long firstNoSteps;
    private long secondNoSteps;
    private float pacePerMinute = 0.0f;

    @Override
    public void onCreate() {
        super.onCreate();

        mSensorManager = (SensorManager)this.getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager.getDefaultSensor(TYPE_STEP_COUNTER) != null) {
            mSensor = mSensorManager.getDefaultSensor(TYPE_STEP_COUNTER);
            isSensorPresent = true;
        } else {
            isSensorPresent = false;
        }
        Log.v(LOG_TAG, "in onCreate");

        firstTimeStamp = System.currentTimeMillis();
        firstNoSteps = 0;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.v(LOG_TAG, "in onBind");
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.v(LOG_TAG, "in onRebind");
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.v(LOG_TAG, "in onUnbind");
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(LOG_TAG, "in onDestroy");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //convert la milisecunde
        secondTimeStamp = event.timestamp * 1000000;
        //obtinere pasi
        secondNoSteps = (long) event.values[0];
        if (Math.abs(secondTimeStamp - firstTimeStamp) > 10000.0) {
            pacePerMinute = 6 * Math.abs(secondNoSteps - firstNoSteps) / Math.abs(secondTimeStamp - firstTimeStamp);
            firstTimeStamp = secondTimeStamp;
            firstNoSteps = secondNoSteps;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public float getPacePerMinute() {
        return pacePerMinute;
    }

    public class MyBinder extends Binder {
        StepsBoundService getService() {
            return StepsBoundService.this;
        }
    }
}
