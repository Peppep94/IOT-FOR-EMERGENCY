package univpm.iot_for_emergency.View.Funzionali;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import univpm.iot_for_emergency.Controller.RegistraController;
import univpm.iot_for_emergency.View.Login;

public class InvioDatiService extends Service {
    private  String Nome;
    private String Cognome;
    private String User ;
    private  String Pass ;
    private String Sesso;
    private String Problemi;
    private String DataN;
    private String ConfPass;
    private String ip;
    private String porta;
    String result;
    String hum;
    String temp;
    String device;
    String currentdate;
    private String azione;

    JSONObject jsonObject = new JSONObject();


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final String action = intent.getAction();
        azione=action;

        if(("univpm.iot_for_emergency.View.Registrazione").equals(action)) {

            Nome = intent.getStringExtra("nome");
            Cognome = intent.getStringExtra("cognome");
            Pass = intent.getStringExtra("pass");
            User = intent.getStringExtra("user");
            Sesso = intent.getStringExtra("sesso");
            Problemi = intent.getStringExtra("problemi");
            DataN = intent.getStringExtra("datan");
            ConfPass = intent.getStringExtra("confpass");
            ip=intent.getStringExtra("ip");
            porta=intent.getStringExtra("porta");


            try {
                jsonObject.put("nome", Nome);
                jsonObject.put("cognome", Cognome);
                jsonObject.put("pass", Pass);
                jsonObject.put("user", User);
                jsonObject.put("sesso", Sesso);
                jsonObject.put("problemi", Problemi);
                jsonObject.put("datan", DataN);

            } catch (JSONException e) {
                e.printStackTrace();
            }


            if (!ConfPass.equals(Pass)) {
                displayToast("Le password non corrispondono!");

            } else if (User.isEmpty() || Pass.isEmpty()) {
                displayToast("User e password non possono essere vuoti!");
            } else {
                new send().execute("http://" + ip + ":" + porta + "/MyFirsRestService/utenti");
            }
        }
        if(("univpm.iot_for_emergency.View.Funzionali.Ricevuti.Invio").equals(action)) {

            hum= intent.getStringExtra("hum");
            temp= intent.getStringExtra("temp");
            device=intent.getStringExtra("device");
            currentdate =intent.getStringExtra("data");

            displayToast(currentdate);

            try {
                jsonObject.put("umd", hum);
                jsonObject.put("temp", temp);
                jsonObject.put("datatime", currentdate);
                jsonObject.put("address", device);


            } catch (JSONException e) {
                e.printStackTrace();
            }


            new send().execute("http://192.168.1.100:8080/MyFirsRestService/utenti/beacon");

        }

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


    private InputStream ApriConnessioneHttp(String urlString) throws IOException
    {
        InputStream in = null;
        int risposta = -1;

        URL url = new URL(urlString);


        try{

            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod("POST"); // here you are telling that it is a POST request, which can be changed into "PUT", "GET", "DELETE" etc.
            httpURLConnection.setRequestProperty("Content-Type", "application/json"); // here you are setting the `Content-Type` for the data you are sending which is `application/json`
            httpURLConnection.connect();


            DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
            wr.writeBytes(jsonObject.toString());
            wr.flush();
            wr.close();

            BufferedReader bf = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            String value = bf.readLine();
            System.out.println("Output:"+value);
            result= value;
            risposta = httpURLConnection.getResponseCode();

            if (risposta == HttpURLConnection.HTTP_OK) {
                in = httpURLConnection.getInputStream();
            }
        }
        catch (Exception ex) {
            Log.d("Connessione", ex.getLocalizedMessage());
            throw new IOException("Errore connessione");
        }
        return in;
    }

    private String avvia(String URL)
    {
        String bit = null;
        InputStream in = null;
        try {
            in = ApriConnessioneHttp(URL);

            bit = "Operazione eseguita";
            in.close();
        }
        catch (IOException e1) {
            Log.d("Servizio web", e1.getLocalizedMessage());
        }
        return bit;
    }

    private class send extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... urls) {
            return avvia(urls[0]);

        }

        protected void onPostExecute(String s) {

            if(("univpm.iot_for_emergency.View.Registrazione").equals(azione)) {

                displayToast(result);

                if (result.equals("Utente Registrato Server")){

                    RegistraController registraController=new RegistraController();
                    int c=registraController.Registracontroller(User,Nome,Cognome,Pass,DataN,Problemi,Sesso,ConfPass);

                    switch (c) {
                        case 0:
                            displayToast("I campi contrassegnati con * non posso essere vuoti");
                            break;
                        case 1:
                            displayToast("Le password non corrispondono");
                            break;
                        case 2:
                            displayToast("Username non disponibile");
                            break;
                        case 3:
                            displayToast("Utente registrato Locale ");
                            startActivity(new Intent(InvioDatiService.this,Login.class));
                            stopSelf();

                            break;
                    }


                }else{

                    displayToast("Utente non registrato in locale!");

                }


            }
            if(("univpm.iot_for_emergency.View.Funzionali.Ricevuti.Invio").equals(azione)) {

                displayToast(result);

            }

        }
    }

}
