package univpm.iot_for_emergency.Funzionali;


import univpm.iot_for_emergency.R;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class BleAdapter extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST =1 ;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;
    private BluetoothLeService mBluetoothLeService;
    private boolean mScanning;

    private static final int RQS_ENABLE_BLUETOOTH = 1;

    private View mLayout;
    ListView listViewLE;

    List<BluetoothDevice> listBluetoothDevice;
    ListAdapter adapterLeScanResult;

    private Handler mHandler;
    private static final long SCAN_PERIOD = 10000;
    private final static String TAG = BleAdapter.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ricerca_ble);
        mBluetoothLeService=new BluetoothLeService();

        requestPositionPermission();

        int permissionCheck = ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION);


        // Controllo se il BLE è supportato
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this,
                    "BLE non supportato!",
                    Toast.LENGTH_SHORT).show();
            finish();
        }

        getBluetoothAdapterAndLeScanner();

        // Controlla se il Bluetooth è supportato.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this,
                    "Bluetooth non supportato",
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        listViewLE = (ListView)findViewById(R.id.lelist);

        listBluetoothDevice = new ArrayList<>();
        adapterLeScanResult = new ArrayAdapter<BluetoothDevice>(
                this, android.R.layout.simple_list_item_1, listBluetoothDevice);
        listViewLE.setAdapter(adapterLeScanResult);
        listViewLE.setOnItemClickListener(scanResultOnItemClickListener);

        mHandler = new Handler();
        scanLeDevice(true);

    }

    AdapterView.OnItemClickListener scanResultOnItemClickListener =
            new AdapterView.OnItemClickListener(){

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    final BluetoothDevice device = (BluetoothDevice) parent.getItemAtPosition(position);
                    String msg = device.getAddress() + "\n"
                            + device.getBluetoothClass().toString() + "\n"
                            + getBTDevieType(device);

                    new AlertDialog.Builder(BleAdapter.this)
                            .setTitle(device.getName())
                            .setMessage(msg)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mBluetoothLeService.disconnect();
                                }
                            })
                            .show();

                }
            };

    private String getBTDevieType(BluetoothDevice d){
        String type = "";

        switch (d.getType()){
            case BluetoothDevice.DEVICE_TYPE_CLASSIC:
                type = "DEVICE_TYPE_CLASSIC";
                break;
            case BluetoothDevice.DEVICE_TYPE_DUAL:
                type = "DEVICE_TYPE_DUAL";
                break;
            case BluetoothDevice.DEVICE_TYPE_LE:
                type = "DEVICE_TYPE_LE";
                break;
            case BluetoothDevice.DEVICE_TYPE_UNKNOWN:
                type = "DEVICE_TYPE_UNKNOWN";
                break;
            default:
                type = "unknown...";
        }

        return type;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, RQS_ENABLE_BLUETOOTH);
                scanLeDevice(true);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == RQS_ENABLE_BLUETOOTH && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }

        getBluetoothAdapterAndLeScanner();


        // Controlla se il Bluetooth è supportato.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this,
                    "Bluetooth non supportato",
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void getBluetoothAdapterAndLeScanner(){
        // BluetoothAdapter e BluetoothLeScanner.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        mScanning = false;
    }

    /*
    per richiamare (ScanCallback callback),
    è necessario avere i permessi BLUETOOTH_ADMIN .
    Bisogna assere abilitati i permessi per ACCESS_COARSE_LOCATION o ACCESS_FINE_LOCATION pper ottenere il risultato.
     */
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            listBluetoothDevice.clear();
            listViewLE.invalidateViews();

            // Lo scan si ferma dopo un tempo predefinito.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBluetoothLeScanner.stopScan(scanCallback);
                    listViewLE.invalidateViews();

                    Toast.makeText(BleAdapter.this,
                            "Scan timeout",
                            Toast.LENGTH_LONG).show();

                    mScanning = false;
                }
            }, SCAN_PERIOD);

            mBluetoothLeScanner.startScan(scanCallback);
            mScanning = true;
        } else {
            mBluetoothLeScanner.stopScan(scanCallback);
            mScanning = false;

        }
    }

    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            addBluetoothDevice(result.getDevice());
            if(result.getDevice().getName().contains("SensorTag")) {
                mBluetoothLeService.connect(result.getDevice().getAddress(), mBluetoothAdapter);
                scanLeDevice(false);
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            for(ScanResult result : results){
                addBluetoothDevice(result.getDevice());
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Toast.makeText(BleAdapter.this,
                    "onScanFailed: " + String.valueOf(errorCode),
                    Toast.LENGTH_LONG).show();
        }

        private void addBluetoothDevice(BluetoothDevice device){
            if(!listBluetoothDevice.contains(device)){
                listBluetoothDevice.add(device);
                listViewLE.invalidateViews();
            }
        }
    };

    private void requestPositionPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)) {

            Snackbar.make(mLayout, "I permessi per la posizione servonpo per effettuare lo scan",
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction("ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(BleAdapter.this,
                                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                    MY_PERMISSIONS_REQUEST);

                        }
                    })
                    .show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST);
        }

    }


}
