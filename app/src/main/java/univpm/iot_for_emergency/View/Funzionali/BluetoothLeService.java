/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package univpm.iot_for_emergency.View.Funzionali;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Service for managing connection and data communication with a GATT server hosted on a
 * given Bluetooth LE device.
 */
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BluetoothLeService extends Service {
    private final static String TAG = BluetoothLeService.class.getSimpleName();

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private int mConnectionState = STATE_DISCONNECTED;
    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    private static final byte[] ENABLE_SENSOR = {0x01};
    private static final UUID UUID_CCC = UUID.fromString("0000180a-0000-1000-8000-00805f9b34fb");


    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                mConnectionState = STATE_CONNECTED;
                Log.i(TAG, "Connected to GATT server.");
                // Attempts to discover services after successful connection.
                Log.i(TAG, "Attempting to start service discovery:" + mBluetoothGatt.discoverServices());

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                mConnectionState = STATE_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server.");
            }
            gatt.discoverServices();
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            BluetoothGattService humidityService = gatt.getService(SensorTagGatt.UUID_HUM_SERV);
            BluetoothGattCharacteristic humidityCharacteristic = humidityService.getCharacteristic(SensorTagGatt.UUID_HUM_DATA);
            BluetoothGattCharacteristic enableHum = humidityService.getCharacteristic(SensorTagGatt.UUID_HUM_CONF);
            gatt.setCharacteristicNotification(humidityCharacteristic,true);
            enableHum.setValue(ENABLE_SENSOR);
            gatt.writeCharacteristic(enableHum);
            gatt.readCharacteristic(humidityCharacteristic);

        }
      @Override
      public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status){
          Log.i(TAG, "Ho scritto la caratteristica "+characteristic.getPermissions());
          BluetoothGattService humidityService = gatt.getService(SensorTagGatt.UUID_HUM_SERV);
          BluetoothGattCharacteristic humidityCharacteristic = humidityService.getCharacteristic(SensorTagGatt.UUID_HUM_DATA);
          gatt.readCharacteristic(humidityCharacteristic);

      }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.i(TAG, "Ho letto la caratteristica");
            byte []value=characteristic.getValue();
            int t = shortUnsignedAtOffset(value, 0);
            if (t==0)
            {
                readCharacteristic(characteristic);
            }else {
                broadcastUpdate("univpm.iot_for_emergency.View.Funzionali",t,value);
            }
        }

    };






    public class LocalBinder extends Binder {
        BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        close();
        return super.onUnbind(intent);
    }

    private final IBinder mBinder = new LocalBinder();
    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     *
     * @return Return true if the connection is initiated successfully. The connection result
     *         is reported asynchronously through the
     *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     *         callback.
     */
    public boolean connect(final String address,BluetoothAdapter adapter) {
        mBluetoothAdapter=adapter;
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        // Previously connected device.  Try to reconnect.
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        Log.d(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        return true;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }


    private static Integer shortUnsignedAtOffset(byte[] c, int offset) {
        Integer lowerByte = (int) c[offset] & 0xFF;
        Integer upperByte = (int) c[offset+1] & 0xFF;
        return (upperByte << 8) + lowerByte;
    }

    private void broadcastUpdate(final String action,int t,byte[] value) {
        final Intent intent = new Intent(action);
        int h = shortUnsignedAtOffset(value, 2);
        t = t - (t % 4);
        h = h - (h % 4);
        float humidity = (-6f) + 125f * (h / 65535f);
        float temperature = -46.85f + 175.72f / 65536f * (float) t;
        Log.d(TAG, String.format("Umidità: %d", ((int) humidity)));
        Log.d(TAG, String.format("Temperatura: %d", ((int) temperature)));
        intent.putExtra("hum",humidity);
        intent.putExtra("temp",temperature);
        sendBroadcast(intent);
        Log.d(TAG, String.format("Broadcast inviati"));
        sendBroadcast(intent);
        disconnect();
        close();

    }

}
