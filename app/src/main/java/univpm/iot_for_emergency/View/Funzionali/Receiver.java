package univpm.iot_for_emergency.View.Funzionali;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.text.DateFormat;
import java.util.Date;

import univpm.iot_for_emergency.Controller.HomeController;

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

                if (!"".equals(sessione.ip())) {
                    String device = intent.getStringExtra("device");
                    Intent intentservice=new Intent(context, InvioDatiService.class);
                    intentservice.setAction("univpm.iot_for_emergency.View.Funzionali.Trovato");
                    intentservice.putExtra("device",device);
                    intentservice.putExtra("user",sessione.user());
                    context.startService(intentservice);
                }
            }

            if (("univpm.iot_for_emergency.View.Registrazione").equals(action)) {

                if (!"".equals(sessione.ip())) {

                    Nome = intent.getStringExtra("nome");
                    Cognome = intent.getStringExtra("cognome");
                    Pass = intent.getStringExtra("pass");
                    User = intent.getStringExtra("user");
                    Sesso = intent.getStringExtra("sesso");
                    Problemi = intent.getStringExtra("problemi");
                    DataN = intent.getStringExtra("datan");
                    ConfPass = intent.getStringExtra("confpass");

                    Intent intentservice = new Intent(context, InvioDatiService.class);
                    intentservice.setAction("univpm.iot_for_emergency.View.Registrazione");
                    intentservice.putExtra("nome", Nome);
                    intentservice.putExtra("cognome", Cognome);
                    intentservice.putExtra("pass", Pass);
                    intentservice.putExtra("user", User);
                    intentservice.putExtra("sesso", Sesso);
                    intentservice.putExtra("problemi", Problemi);
                    intentservice.putExtra("datan", DataN);
                    intentservice.putExtra("confpass", ConfPass);
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
