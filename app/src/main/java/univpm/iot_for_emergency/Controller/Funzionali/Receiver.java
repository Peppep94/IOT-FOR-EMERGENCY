package univpm.iot_for_emergency.Controller.Funzionali;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import java.text.DateFormat;
import java.util.Date;

import univpm.iot_for_emergency.Controller.HomeController;

public class Receiver extends BroadcastReceiver {
    private Sessione sessione;
    private boolean logout=false;
    private Handler mHandler;
    private Context ReceiverContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        sessione=new Sessione(context);
        mHandler=new Handler();
        ReceiverContext=context;


            if(("univpm.iot_for_emergency.View.Funzionali.Stop").equals(action)){
                logout=true;
            }

            if(("univpm.iot_for_emergency.View.Funzionali.Scaduto").equals(action)){
                int stopperiod = intent.getIntExtra("stopperiod",5000);
                if (!logout) {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intentservice=new Intent(ReceiverContext, BluetoothLeService.class);
                            ReceiverContext.startService(intentservice);
                        }
                    }, stopperiod);
                }
            }


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
