package com.tony.android.bluetoothlegatt;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class DeviceConfigActivity extends Activity {
    private final static String TAG = DeviceControlActivity.class.getSimpleName();

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    public static final String EXTRAS_UUID_SERVICE = "UUID_SERVICE";
    public static final String EXTRAS_UUID_CHARACTERISTIC = "UUID_CHARACTERISTIC";

    private String mDeviceName;
    private String mDeviceAddress;
    private String mUuidService;
    private ArrayList<BluetoothGattCharacteristic> mGattCharacteristics;
    private BluetoothGattCharacteristic mNotifyCharacteristic;

    private EditText mSecValue, mIdValue;
    private Button mbtnSave;
    private BluetoothLeService mBluetoothLeService;

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
             if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
            }
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_config);

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
        mUuidService = intent.getStringExtra(EXTRAS_UUID_SERVICE);
        mGattCharacteristics = intent.getParcelableArrayListExtra(EXTRAS_UUID_CHARACTERISTIC);
        ((TextView)findViewById(R.id.txtDevice)).setText(mDeviceName);
        ((TextView)findViewById(R.id.txtMac)).setText(mDeviceAddress);

        mSecValue = findViewById(R.id.editSC);
        mIdValue = findViewById(R.id.editID);
        mbtnSave = findViewById(R.id.btnSave);
        mbtnSave.setOnClickListener(new  View.OnClickListener(){
            @Override
            public void onClick(View view) {
                writeData();
            }
        });
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    private void writeData() {
        String sec = mSecValue.getText().toString();
        String id = mIdValue.getText().toString();
        if(sec.isEmpty() || id.isEmpty()){
            Toast.makeText(this, "Please enter sec, id value", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mGattCharacteristics != null) {
            for(byte i = 0; i < mGattCharacteristics.size(); i++){
                writeCharacteristic(i,  mGattCharacteristics.get(2));
            }
        }
    }

    private void writeCharacteristic(byte id, BluetoothGattCharacteristic characteristic){
        final int charaProp = characteristic.getProperties();

        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
            // If there is an active notification on a characteristic, clear
            // it first so it doesn't update the data field on the user interface.
            if (mNotifyCharacteristic != null) {
                mBluetoothLeService.setCharacteristicNotification(
                        mNotifyCharacteristic, false);
                mNotifyCharacteristic = null;
            }
            byte []data = {id, 0x02, 0x03, 0x04};
            characteristic.setValue(data);
            mBluetoothLeService.writeCharacteristic(characteristic);
            mBluetoothLeService.readCharacteristic(characteristic);
        }
        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            mNotifyCharacteristic = characteristic;
            mBluetoothLeService.setCharacteristicNotification(
                    characteristic, true);
        }
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
}
