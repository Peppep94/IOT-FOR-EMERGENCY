package univpm.iot_for_emergency.View;


import univpm.iot_for_emergency.View.Funzionali.BluetoothLeService;
import univpm.iot_for_emergency.View.Funzionali.Sessione;
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
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int MY_PERMISSIONS_REQUEST =1 ;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;
    private BluetoothLeService mBluetoothLeService;
    private boolean mScanning;
    private Sessione sessione;
    private String user;

    private static final int RQS_ENABLE_BLUETOOTH = 1;

    private Handler mHandler;
    private static final long SCAN_PERIOD = 10000;
    private final static String TAG = univpm.iot_for_emergency.View.Home.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sessione = new Sessione(this);

        if (!sessione.loggedin()) {
            loguot();
        }
        user=sessione.user();

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


        mHandler = new Handler();
        // scanLeDevice(true);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }



    @Override
    protected void onResume() {
        super.onResume();

       // requestPositionPermission();
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
            requestPositionPermission();
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

            // Lo scan si ferma dopo un tempo predefinito.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBluetoothLeScanner.stopScan(scanCallback);
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
                mBluetoothLeService.connect(result.getDevice().getAddress(), mBluetoothAdapter);
                scanLeDevice(false);
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



    private void requestPositionPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)) {

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
                // Permission has been granted. Start camera preview Activity.
                Snackbar.make(findViewById(android.R.id.content), "I permessi per la posizione sono stati concessi",
                        Snackbar.LENGTH_SHORT)
                        .show();
                if(mBluetoothAdapter.isEnabled())
                     scanLeDevice(true);
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
        } else if (id==R.id.ricerca) {
            Intent intent = new Intent(univpm.iot_for_emergency.View.Home.this, Home.class); //reinderizzo a Modificadati
            univpm.iot_for_emergency.View.Home.this.startActivity(intent);
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


}
