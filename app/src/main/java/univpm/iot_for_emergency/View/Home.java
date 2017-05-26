package univpm.iot_for_emergency.View;


import uk.co.senab.photoview.PhotoViewAttacher;
import univpm.iot_for_emergency.Controller.HomeController;
import univpm.iot_for_emergency.Model.TabDatiBeacon;
import univpm.iot_for_emergency.View.Funzionali.BluetoothLeService;
import univpm.iot_for_emergency.View.Funzionali.Mappa;
import univpm.iot_for_emergency.View.Funzionali.SensorTagData;
import univpm.iot_for_emergency.View.Funzionali.SensorTagGatt;
import univpm.iot_for_emergency.View.Funzionali.Sessione;
import univpm.iot_for_emergency.R;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static android.R.attr.uiOptions;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int MY_PERMISSIONS_REQUEST =1 ;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;
    private BluetoothLeService mBluetoothLeService;
    private boolean mConnected;
    private Sessione sessione;
    boolean mServiceBound = false;
    boolean mScanning=false;
    private String user;
    private Toolbar toolbar;
    private ProgressBar progressbar;
    private String Device;
    private static final int RQS_ENABLE_BLUETOOTH = 1;
    private Handler mHandler;
    private static final long SCAN_PERIOD = 10000;
    private final static String TAG = univpm.iot_for_emergency.View.Home.class.getSimpleName();
    private Mappa imageView=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progressbar=(ProgressBar) findViewById(R.id.progressBar);
        sessione = new Sessione(this);
        imageView = (Mappa) findViewById(R.id.mappa);
        if (!sessione.loggedin()) {
            loguot();
        }
        user=sessione.user();

        Device="";

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

        requestPermission();

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
        if(mBluetoothAdapter.isEnabled())
        scanLeDevice(false);
        mScanning=false;
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if(mBluetoothAdapter.isEnabled()) {
            if(!mScanning)
            scanLeDevice(true);
            mScanning=true;
            toolbar.setTitle("Scanning...");
            toolbar.setSubtitle("");
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
            if(!mScanning)
             scanLeDevice(true);
            mScanning=true;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void getBluetoothAdapterAndLeScanner(){
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
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
                        if(Device.equals(""))
                            toolbar.setTitle("Beacon not found");
                        else {
                        HomeController homeController=new HomeController();
                        TabDatiBeacon tabDatiBeacon =homeController.getTabBeacon(Device);
                        toolbar.setTitle("Temperatura "+String.valueOf(tabDatiBeacon.temperature)+"° Umidità "+ String.valueOf(tabDatiBeacon.humidity)+"%");
                        toolbar.setSubtitle(("Aggiornato il "+ tabDatiBeacon.dateTime));
                            imageView.init(toolbar);
                            PhotoViewAttacher photoViewAttacher =new PhotoViewAttacher(imageView);
                            photoViewAttacher.update();
                            photoViewAttacher.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
                                @Override
                                public void onViewTap(View view, float v, float v1) {
                                    String posizione = "(" + Math.round(v) + "," + Math.round(v1) + ")";
                                    Log.e("dp ", "Posizione " + posizione);
                                }
                            });
                        }
                        progressbar.setVisibility(View.INVISIBLE);
                }
                }
            }, SCAN_PERIOD);
           //mBluetoothLeScanner.startScan(scanCallback)


            String Sensortag_Service = String.valueOf(SensorTagGatt.UUID_DEVINFO_SERV);
            ParcelUuid ParcelUuid_Sensortag_Service = ParcelUuid.fromString(Sensortag_Service);
            ScanFilter scanFilter =
                    new ScanFilter.Builder()
                            .setServiceUuid(ParcelUuid_Sensortag_Service)
                            .build();
            List<ScanFilter> scanFilters = new ArrayList<ScanFilter>();
            scanFilters.add(scanFilter);

            ScanSettings scanSettings =
                    new ScanSettings.Builder().build();

            mBluetoothLeScanner.startScan(scanFilters, scanSettings, scanCallback);
        } else {
            mBluetoothLeScanner.stopScan(scanCallback);
        }
    }


    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);


            Log.e("UUID", String.valueOf(result.getDevice().getUuids()));

                if(callbackType==1)
                {
                    Log.e("Callback", String.valueOf(callbackType));
                }
                else if(result.getDevice().getName().contains("SensorTag")) {
                    if(mBluetoothLeService.initialize()) {
                    Device=result.getDevice().getAddress();
                    toolbar.setTitle("SensorTag "+Device);
                    mBluetoothLeService.connect(Device);
                    mConnected=true;
                    scanLeDevice(false);
                    mScanning=false;
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
            finish();

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
        public void onReceive(final Context context, Intent intent) {
            final String action = intent.getAction();
            if (("univpm.iot_for_emergency.View.Funzionali.Ricevuti").equals(action)) {
                int humidity=(int)intent.getDoubleExtra("hum",1000);
                int temperature=(int)intent.getDoubleExtra("temp",1000);
                progressbar.setVisibility(View.INVISIBLE);
                toolbar.setTitle("Temperatura "+String.valueOf(temperature)+"° Umidità "+ String.valueOf(humidity)+"%");
                String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                toolbar.setSubtitle("Aggiornato il "+ currentDateTimeString);
                imageView.init(toolbar);
                PhotoViewAttacher photoViewAttacher =new PhotoViewAttacher(imageView);
                photoViewAttacher.update();
                photoViewAttacher.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
                    @Override
                    public void onViewTap(View view, float v, float v1) {
                        String posizione = "(" + Math.round(v) + "," + Math.round(v1) + ")";
                        Log.e("dp ", "Posizione " + posizione);
                        final Dialog dialog = new Dialog(context);
                        dialog.setContentView(R.layout.custom_dialog);
                        dialog.setTitle("Title...");
                        TextView text = (TextView) dialog.findViewById(R.id.message);
                        text.setText("Android custom dialog example!");
                        Button dialogButton = (Button) dialog.findViewById(R.id.ok);
                        // if button is clicked, close the custom dialog
                        dialogButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }
                });

                photoViewAttacher.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        fullScreen();
                        return false;
                    }
                });
                HomeController homeController=new HomeController();
                homeController.updatesaveBeacon(Device,currentDateTimeString,String.valueOf(temperature),String.valueOf(humidity));
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


    public void fullScreen() {

        // BEGIN_INCLUDE (get_current_ui_flags)
        // The UI options currently enabled are represented by a bitfield.
        // getSystemUiVisibility() gives us that bitfield.
        int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;
        // END_INCLUDE (get_current_ui_flags)
        // BEGIN_INCLUDE (toggle_ui_flags)
        boolean isImmersiveModeEnabled = isImmersiveModeEnabled();
        if (isImmersiveModeEnabled) {
            Log.i("TEST", "Turning immersive mode mode off. ");
        } else {
            Log.i("TEST", "Turning immersive mode mode on.");

        }

        // Navigation bar hiding:  Backwards compatible to ICS.
        if (Build.VERSION.SDK_INT >= 14) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }

        // Status bar hiding: Backwards compatible to Jellybean
        if (Build.VERSION.SDK_INT >= 16) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        }

        // Immersive mode: Backward compatible to KitKat.
        // Note that this flag doesn't do anything by itself, it only augments the behavior
        // of HIDE_NAVIGATION and FLAG_FULLSCREEN.  For the purposes of this sample
        // all three flags are being toggled together.
        // Note that there are two immersive mode UI flags, one of which is referred to as "sticky".
        // Sticky immersive mode differs in that it makes the navigation and status bars
        // semi-transparent, and the UI flag does not get cleared when the user interacts with
        // the screen.
        if (Build.VERSION.SDK_INT >= 18) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }

        getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
        //END_INCLUDE (set_ui_flags)
    }

    private boolean isImmersiveModeEnabled() {
        return ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);
    }


}
