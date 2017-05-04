package univpm.iot_for_emergency.View;


import univpm.iot_for_emergency.View.Funzionali.BluetoothLeService;
import univpm.iot_for_emergency.View.Funzionali.Sessione;
import univpm.iot_for_emergency.R;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import java.util.List;
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int MY_PERMISSIONS_REQUEST =1 ;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;
    private BluetoothLeService mBluetoothLeService;
    private boolean mConnected;
    private boolean mScanning;
    private Sessione sessione;
    boolean mServiceBound = false;
    private String user;
    private Toolbar toolbar;
    private ProgressBar progressbar;

    private static final int RQS_ENABLE_BLUETOOTH = 1;
    private Handler mHandler;
    private static final long SCAN_PERIOD = 10000;
    private final static String TAG = univpm.iot_for_emergency.View.Home.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progressbar=(ProgressBar) findViewById(R.id.progressBar);
        sessione = new Sessione(this);

        if (!sessione.loggedin()) {
            loguot();
        }
        user=sessione.user();

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


        mHandler = new Handler();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, BluetoothLeService.class);
        startService(intent);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mServiceBound) {
            unbindService(mServiceConnection);
            mServiceBound = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if(mBluetoothAdapter.isEnabled()) {
            scanLeDevice(true);
            toolbar.setTitle("Scanning...");
            progressbar.setVisibility(View.VISIBLE);
        }
        else
        {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, RQS_ENABLE_BLUETOOTH);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == RQS_ENABLE_BLUETOOTH && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }


        getBluetoothAdapterAndLeScanner();

        if (requestCode== RQS_ENABLE_BLUETOOTH &&resultCode == RESULT_OK) {
            Log.i(TAG, "Bluetooth acceso");
          scanLeDevice(true);
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void getBluetoothAdapterAndLeScanner(){
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
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

            // Lo scan si ferma dopo un tempo predefinito.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBluetoothLeScanner.stopScan(scanCallback);
                    if(toolbar.getTitle().equals("Scanning...") && !mConnected){
                        toolbar.setTitle("Beacon not found");
                        progressbar.setVisibility(View.INVISIBLE);
                }
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
            if(result.getDevice().getName().contains("SensorTag")) {
                if(mBluetoothLeService.initialize()) {
                    mBluetoothLeService.connect(result.getDevice().getAddress());
                    mConnected=true;
                    scanLeDevice(false);
                }
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Toast.makeText(univpm.iot_for_emergency.View.Home.this,
                    "onScanFailed: " + String.valueOf(errorCode),
                    Toast.LENGTH_LONG).show();
        }

    };


/*
    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            Snackbar.make(findViewById(android.R.id.content), "I permessi per la posizione servono per effettuare lo scan",
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction("Dai permessi", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(univpm.iot_for_emergency.View.Home.this,
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // BEGIN_INCLUDE(onRequestPermissionsResult)
        if (requestCode == MY_PERMISSIONS_REQUEST) {
            // Request for camera permission.
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if(mBluetoothAdapter.isEnabled()) {
                    scanLeDevice(true);
                    toolbar.setTitle("Scanning...");
                }
                else {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, RQS_ENABLE_BLUETOOTH);
                    }

            } else {
                // Permission request was denied.
                Snackbar.make(findViewById(android.R.id.content), "I permessi per la posizione sono stati negati",
                        Snackbar.LENGTH_SHORT)
                        .show();

            }
        }
    }
*/


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }




    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.modificadati) {
            if(user.contains("Guest"))
            {
                Toast.makeText(getApplicationContext(),"Non sei abilitato alla modifica",Toast.LENGTH_SHORT).show(); //in caso di utente guest non permetto la modifica
            }
            else {
                Intent intent = new Intent(univpm.iot_for_emergency.View.Home.this, Modifica_dati.class); //reinderizzo a Modificadati
                univpm.iot_for_emergency.View.Home.this.startActivity(intent);
            }
        } else if (id == R.id.logout) {
            loguot();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loguot(){
        sessione.UtenteLoggato(false,null);
        finish();
        startActivity(new Intent(univpm.iot_for_emergency.View.Home.this,Login.class));
    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (("univpm.iot_for_emergency.View.Funzionali.Ricevuti").equals(action)) {
                int humidity=(int)intent.getDoubleExtra("hum",1000);
                int temperature=(int)intent.getDoubleExtra("temp",1000);
                progressbar.setVisibility(View.INVISIBLE);
                toolbar.setTitle("Temperatura "+String.valueOf(temperature)+"° Umidità "+ String.valueOf(humidity)+"%");
                mConnected=false;

            }else if(("univpm.iot_for_emergency.View.Funzionali.Connesso").equals(action)) {
                mConnected=true;
                String device=intent.getStringExtra("device");
                Snackbar.make(findViewById(android.R.id.content), "Connesso a "+device,
                        Snackbar.LENGTH_SHORT)
                        .show();
            }
        }
    };



    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("univpm.iot_for_emergency.View.Funzionali.Ricevuti");
        intentFilter.addAction("univpm.iot_for_emergency.View.Funzionali.Connesso");
        return intentFilter;
    }


    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServiceBound = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BluetoothLeService.MyBinder myBinder = (BluetoothLeService.MyBinder) service;
            mBluetoothLeService = myBinder.getService();
            mServiceBound = true;
        }
    };

}
