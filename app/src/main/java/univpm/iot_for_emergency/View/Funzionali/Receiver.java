package univpm.iot_for_emergency.View.Funzionali;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class Receiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intentservice = new Intent(context, InvioDatiService.class);
        context.startService(intentservice);
    }
}
