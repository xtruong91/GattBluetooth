package com.example.android.bluetoothlegatt.viewmodels;

import androidx.lifecycle.LiveData;

public class ScannerStateLiveData extends LiveData<ScannerStateLiveData> {
    private boolean scanningStarted;
    private boolean hasRecords;
    private boolean bluetoothEnabled;
    private boolean locationEnabled;

    public ScannerStateLiveData(final boolean bluetoothEnabled,
                                       final boolean locationEnabled) {
        this.scanningStarted = false;
        this.bluetoothEnabled = bluetoothEnabled;
        this.locationEnabled = locationEnabled;
        postValue(this);
    }

    public void refresh() {
        postValue(this);
    }

    public void scanningStarted() {
        scanningStarted = true;
        postValue(this);
    }

    public void scanningStopped() {
        scanningStarted = false;
        postValue(this);
    }

    public void bluetoothEnabled() {
        bluetoothEnabled = true;
        postValue(this);
    }

    public synchronized void bluetoothDisabled() {
        bluetoothEnabled = false;
        hasRecords = false;
        postValue(this);
    }

    public void setLocationEnabled(final boolean enabled) {
        locationEnabled = enabled;
        postValue(this);
    }

    public void recordFound() {
        hasRecords = true;
        postValue(this);
    }

    /**
     * Returns whether scanning is in progress.
     */
    boolean isScanning() {
        return scanningStarted;
    }

    /**
     * Returns whether any records matching filter criteria has been found.
     */
    public boolean hasRecords() {
        return hasRecords;
    }

    /**
     * Returns whether Bluetooth adapter is enabled.
     */
    public boolean isBluetoothEnabled() {
        return bluetoothEnabled;
    }

    /**
     * Returns whether Location is enabled.
     */
    public boolean isLocationEnabled() {
        return locationEnabled;
    }

    /**
     * Notifies the observer that scanner has no records to show.
     */
    public void clearRecords() {
        hasRecords = false;
        postValue(this);
    }
}
