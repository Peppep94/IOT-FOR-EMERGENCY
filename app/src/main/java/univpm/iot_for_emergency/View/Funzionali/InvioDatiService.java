package univpm.iot_for_emergency.View.Funzionali;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class InvioDatiService extends Service {


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        displayToast("sono nell'invio dati");
        stopSelf();
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        return null;
    }

    public void displayToast(String message){
        Toast.makeText(InvioDatiService.this, message, Toast.LENGTH_SHORT).show();
    }

}
