package com.dhiman.sensorparallax;

/**
 * Created by dhiman_da on 11/23/2015.
 *
 * Interface to register/ unregister Activity to fragment callback
 */
public interface ActivityFragmentCallback {
    void register(final DataPassCallback callback);
    void unregister();
}
