package com.example.android.bluetoothlegatt.viewmodels;

import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.android.bluetoothlegatt.adapter.DiscoveredBluetoothDevice;
import com.example.android.bluetoothlegatt.services.BluetoothLeService;

public class SensorViewModel extends AndroidViewModel {

    private final static String TAG = SensorViewModel.class.getSimpleName();
    private BluetoothDevice device;
    private BluetoothLeService mBluetoothLeService;
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;
    private BluetoothGattCharacteristic mSensorCharacteristic;
    private Application mApplication;

    private MutableLiveData<Boolean> mConnectionState;
    private MutableLiveData<String> mSensorValue;

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder)service).getService();
            if(!mBluetoothLeService.initialize()){
                Log.e(TAG, "Unable to initialize Bluetooth");
            }
            // Automatically connect to the devices upon successful start-up initialization
            mBluetoothLeService.connect(device.getAddress());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBluetoothLeService = null;
        }
    };

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if(BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)){
                mConnectionState.postValue(true);
            } else if(BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)){
                mConnectionState.postValue(false);
            } else if(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)){
                // TODO
            } else if(BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)){
                // the read data is available
                mSensorValue.postValue("Template:xxx");
            }
        }
    };

    public LiveData<Boolean> getConnectionState(){
        if(mConnectionState == null){
            mConnectionState = new MutableLiveData<>();
        }
        return mConnectionState;
    }

    public LiveData<String> getSensorValue(){
        if(mSensorValue == null){
            mSensorValue = new MutableLiveData<>();
        }
        return mSensorValue;
    }

    public SensorViewModel(@NonNull Application application) {
        super(application);
        mApplication = application;
        final Intent gattServiceIntent = new Intent(mApplication, BluetoothLeService.class);
        mApplication.bindService(gattServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public boolean connect(@NonNull final DiscoveredBluetoothDevice target){
        if(device == null){
            device = target.getDevice();
            reconnect();
        }
        return true;
    }

    public void reconnect(){
        mApplication.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if(mBluetoothLeService != null){
            final boolean result = mBluetoothLeService.connect(device.getAddress());
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    public boolean disconnect(){
        mConnected = false;
        mApplication.unregisterReceiver(mGattUpdateReceiver);
        return false;
    }

    public void destroy(){
        mApplication.unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    private static IntentFilter makeGattUpdateIntentFilter(){
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    public void readData(){
        if(mSensorCharacteristic == null){
            Log.d(TAG, "The Sensor Characteristic is invalid");
            return;
        }
        final int charaProp =  mSensorCharacteristic.getProperties();
        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            Log.d(TAG, "Checking for notifying property");
            mNotifyCharacteristic = mSensorCharacteristic;
            mBluetoothLeService.setCharacteristicNotification(
                    mNotifyCharacteristic, true); //mSensorDataCharcteristics
            //mBluetoothLeService.readCharacteristic(mSensorDataCharacteristic); //newly added
        }
        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0){
            if (mNotifyCharacteristic != null) {
                Log.d(TAG, "entered this loop since notification is enabled");
                mBluetoothLeService.setCharacteristicNotification(mNotifyCharacteristic, true);  //false previously
                mNotifyCharacteristic = null;
            }
            mBluetoothLeService.readCharacteristic(mSensorCharacteristic);
        }
    }
}
