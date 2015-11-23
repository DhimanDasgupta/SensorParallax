package com.dhiman.sensorparallax;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;

/**
 * Created by dhiman_da on 11/23/2015.
 */
public class RotationSensorEventListener implements SensorEventListener {

    public interface RotationSensorCallback {
        void onOrientationChanged(float azimuth, float pitch, float roll);
    }

    private static final int SENSOR_DELAY_MICROS = 50 * 1000; // 50ms

    private final SensorManager mSensorManager;
    private final Sensor mRotationSensor;
    private final WindowManager mWindowManager;

    private int mLastAccuracy;
    private RotationSensorCallback mRotationSensorCallback;

    private float[] mRotationMatrix = new float[9];
    private float[] mAdjustedRotationMatrix = new float[9];
    private float[] mOrientation = new float[3];

    public RotationSensorEventListener(SensorManager sensorManager,
                                       WindowManager windowManager) {
        mSensorManager = sensorManager;
        mWindowManager = windowManager;

        // Can be null if the sensor hardware is not available
        mRotationSensor = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
    }

    public void startListening(RotationSensorCallback listener) {
        if (mRotationSensorCallback == listener) {
            return;
        }
        mRotationSensorCallback = listener;
        if (mRotationSensor == null) {
            Log.e("Hello", "Rotation vector sensor not available; will not provide orientation data.");
            return;
        }
        mSensorManager.registerListener(this, mRotationSensor,
                SENSOR_DELAY_MICROS);
    }

    public void stopListening() {
        mSensorManager.unregisterListener(this);
        mRotationSensorCallback = null;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if (mLastAccuracy != accuracy) {
            mLastAccuracy = accuracy;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (mRotationSensorCallback == null) {
            return;
        }
        if (mLastAccuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
            return;
        }
        if (event.sensor == mRotationSensor) {
            updateOrientation(event.values);
        }
    }

    private void updateOrientation(float[] rotationVector) {
        // float[] rotationMatrix = new float[9];
        SensorManager.getRotationMatrixFromVector(mRotationMatrix,
                rotationVector);

        // By default, remap the axes as if the front of the
        // device screen was the instrument panel.
        int worldAxisForDeviceAxisX = SensorManager.AXIS_X;
        int worldAxisForDeviceAxisY = SensorManager.AXIS_Z;

        // Adjust the rotation matrix for the device orientation
        int screenRotation = mWindowManager.getDefaultDisplay().getRotation();
        switch (screenRotation) {
            case Surface.ROTATION_0:
                worldAxisForDeviceAxisX = SensorManager.AXIS_X;
                worldAxisForDeviceAxisY = SensorManager.AXIS_Z;
                break;

            case Surface.ROTATION_90:
                worldAxisForDeviceAxisX = SensorManager.AXIS_Z;
                worldAxisForDeviceAxisY = SensorManager.AXIS_MINUS_X;
                break;

            case Surface.ROTATION_180:
                worldAxisForDeviceAxisX = SensorManager.AXIS_MINUS_X;
                worldAxisForDeviceAxisY = SensorManager.AXIS_MINUS_Z;
                break;

            case Surface.ROTATION_270:
                worldAxisForDeviceAxisX = SensorManager.AXIS_MINUS_Z;
                worldAxisForDeviceAxisY = SensorManager.AXIS_X;
                break;
        }

        // float[] adjustedRotationMatrix = new float[9];
        SensorManager.remapCoordinateSystem(mRotationMatrix,
                worldAxisForDeviceAxisX, worldAxisForDeviceAxisY,
                mAdjustedRotationMatrix);

        // Transform rotation matrix into azimuth/pitch/roll
        // float[] orientation = new float[3];
        SensorManager.getOrientation(mAdjustedRotationMatrix, mOrientation);

        // Convert radians to degrees
		/*
		 * float azimuth = mOrientation[0] * -57;
		 * float pitch = mOrientation[1] * -57;
		 * float roll = mOrientation[2] * -57;
		 */

        float azimuth = (float) Math.toDegrees(mOrientation[0]);
        float pitch = (float) Math.toDegrees(mOrientation[1]);
        float roll = (float) Math.toDegrees(mOrientation[2]);
        Log.d("Data", "" + mOrientation[0] + ", " + mOrientation[1] + ", " + mOrientation[2]);

        mRotationSensorCallback.onOrientationChanged(mOrientation[0], mOrientation[1], mOrientation[2]);
    }
}
