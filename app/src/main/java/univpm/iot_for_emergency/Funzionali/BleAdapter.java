package univpm.iot_for_emergency.Funzionali;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Set;

import univpm.iot_for_emergency.R;

public class BleAdapter extends AppCompatActivity {

    private BluetoothAdapter btAdapter;
    private Set<BluetoothDevice> dispositivi;
    private ListView lv;
    private ArrayAdapter<String> adapter = null;
    private static final int BLUETOOTH_ON=1000;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ricerca_ble);
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        lv = (ListView)findViewById(R.id.listviewble);
        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
        lv.setAdapter(adapter);
    }


    public void scan(View v)
    {
        if (!btAdapter.isEnabled())
        {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, BLUETOOTH_ON);
        }
        else
            load();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==BLUETOOTH_ON && resultCode==RESULT_OK)
        {
            load();
        }
    }
    private void load()
    {
        dispositivi = btAdapter.getBondedDevices();
        adapter.clear();
        for(BluetoothDevice bt : dispositivi)
            adapter.add(bt.getName());
    }
}
