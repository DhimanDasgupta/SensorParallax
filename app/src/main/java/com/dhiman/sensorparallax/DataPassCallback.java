package com.dhiman.sensorparallax;

/**
 * Created by dhiman_da on 11/23/2015.
 */
public interface DataPassCallback {
    void onOrientationChanged(float azimuth, float pitch, float roll);
}
