package com.dhiman.sensorparallax;

import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.dhiman.sensorparallax.RotationSensorEventListener.RotationSensorCallback;

public class MainActivity extends AppCompatActivity implements RotationSensorCallback, ActivityFragmentCallback {
    private static final String FRAGMENT_TAG = "fragment";

    private DataPassCallback mCallback;

    private RotationSensorEventListener mRotationSensorEventListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().
                    beginTransaction().
                    add(R.id.fragment_container, MainFragment.newInstance(), FRAGMENT_TAG).
                    commit();
        }

        final SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        final WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        mRotationSensorEventListener = new RotationSensorEventListener(sensorManager, windowManager);
    }

    @Override
    public void onResume() {
        super.onResume();

        mRotationSensorEventListener.startListening(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        mRotationSensorEventListener.stopListening();
    }

    @Override
    public void onOrientationChanged(float azimuth, float pitch, float roll) {
        if (mCallback != null) {
            mCallback.onOrientationChanged(azimuth, pitch, roll);
        }
    }

    @Override
    public void register(final DataPassCallback callback) {
        mCallback = callback;
    }

    @Override
    public void unregister() {
        mCallback = null;
    }
}
