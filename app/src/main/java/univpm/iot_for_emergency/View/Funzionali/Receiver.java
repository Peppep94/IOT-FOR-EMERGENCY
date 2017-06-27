package univpm.iot_for_emergency.View.Funzionali;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.text.DateFormat;
import java.util.Date;

import univpm.iot_for_emergency.Controller.HomeController;
import univpm.iot_for_emergency.R;
import univpm.iot_for_emergency.View.Login;

import static android.content.Context.NOTIFICATION_SERVICE;

public class Receiver extends BroadcastReceiver {
    private  String Nome;
    private String Cognome;
    private String User ;
    private  String Pass ;
    private String Sesso;
    private String Problemi;
    private String DataN;
    private String ConfPass;
    private Sessione sessione;

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        sessione=new Sessione(context);


            if(("univpm.iot_for_emergency.View.Funzionali.Trovato").equals(action)){
                String device = intent.getStringExtra("device");

                if (!"".equals(sessione.ip())) {
                    Intent intentservice=new Intent(context, InvioDatiService.class);
                    intentservice.setAction("univpm.iot_for_emergency.View.Funzionali.Trovato");
                    intentservice.putExtra("device",device);
                    intentservice.putExtra("user",sessione.user());
                    context.startService(intentservice);
                }
            }
        

            if (("univpm.iot_for_emergency.View.Funzionali.Ricevuti").equals(action)) {

                    String device = intent.getStringExtra("device");
                    int humidity = (int) intent.getDoubleExtra("hum", 1000);
                    int temperature = (int) intent.getDoubleExtra("temp", 1000);
                    String humsend = String.valueOf(humidity);
                    String tempsend = String.valueOf(temperature);
                    String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

                    HomeController homeController = new HomeController();
                    homeController.updatesaveBeacon(device, currentDateTimeString, String.valueOf(temperature), String.valueOf(humidity));


                if (!"".equals(sessione.ip())) {

                    Intent intenteservice = new Intent(context, InvioDatiService.class);
                    intenteservice.setAction("univpm.iot_for_emergency.View.Funzionali.Ricevuti.Invio");
                    intenteservice.putExtra("hum", humsend);
                    intenteservice.putExtra("temp", tempsend);
                    intenteservice.putExtra("data", currentDateTimeString);
                    intenteservice.putExtra("device", device);
                    context.startService(intenteservice);

                }

            }

    }

}
