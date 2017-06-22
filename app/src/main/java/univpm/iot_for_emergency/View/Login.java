package univpm.iot_for_emergency.View;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import uk.co.senab.photoview.PhotoViewAttacher;
import univpm.iot_for_emergency.Controller.HomeController;
import univpm.iot_for_emergency.Controller.RegistraController;
import univpm.iot_for_emergency.Model.TabPunti;
import univpm.iot_for_emergency.Model.TabUtente;
import univpm.iot_for_emergency.View.Funzionali.BluetoothLeService;
import univpm.iot_for_emergency.View.Funzionali.Sessione;
import univpm.iot_for_emergency.Controller.LoginController;
import univpm.iot_for_emergency.R;


public class Login extends AppCompatActivity {
    private String User;
    private String Pass;
    private Sessione sessione;
    String result;
    String ip;
    String porta;
    String codice1;
    String x1;
    String y1;
    String quota1;
    String address1;
    String data1;
    private LoginController loginController;
    private final static String TAG = Login.class.getSimpleName();

    private Button bLogin;
    private TextView registerLink;
    private EditText Usertext;
    private EditText Passtext;
    int contatore;
    ProgressDialog progressDialogDB;
    Handler mHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        progressDialogDB= new ProgressDialog(Login.this, R.style.AppTheme_Dark_Dialog);
        progressDialogDB.setIndeterminate(true);
        progressDialogDB.setMessage("Connessione al Server...");
        progressDialogDB.setCancelable(false);
        progressDialogDB.show();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessione =new Sessione(this);
        loginController=new LoginController();
        bLogin=(Button) findViewById(R.id.button);
        final Button bLoginGuest=(Button) findViewById(R.id.bLoginGuest);
        registerLink = (TextView) findViewById(R.id.RegisterHere);
        contatore=0;
        Usertext = (EditText) findViewById(R.id.User);
        Passtext = (EditText) findViewById(R.id.Password);

        mHandler=new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (contatore==0) {
                    progressDialogDB.dismiss();
                    bLogin.setEnabled(false);
                    Usertext.setEnabled(false);
                    Passtext.setEnabled(false);
                    registerLink.setEnabled(false);
                    Snackbar.make(findViewById(android.R.id.content), "Sei offline", Snackbar.LENGTH_LONG).show();
                    contatore = contatore + 1;
                }
            }
        }, 5000);


        //Leggo il file Server.xls dove ci sono la porta e l'ip
        controlloServer();

        if(ip.equals("") || porta.equals("")){
           // displayToast("Errore di accesso al Server!");
            progressDialogDB.dismiss();
            bLogin.setEnabled(false);
            Usertext.setEnabled(false);
            Passtext.setEnabled(false);
            registerLink.setEnabled(false);
            Snackbar.make(findViewById(android.R.id.content), "Sei offline", Snackbar.LENGTH_LONG).show();
            contatore=contatore+1;
        }else{
            sessione.DatiServer(ip,porta);
            //Leggo il file Dati.xls dove ci sono le coordinate dei punti dove sono posizionati i beacon sulle varie mappe
            LetturaMappa();
            //Invio una richiesta di salvare in locale, tutti gli utenti registrati presenti sul server.
            new Login.send().execute("http://"+ip+":"+porta+"/MyFirsRestService/utenti/db");
        }

        //http://31.170.166.75:8080/

        controlloPrimoAvvio();  // controlla se la sessione è attiva, nel caso lo fosse reindirizza ad Home

        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {    //associo un'azione all'oggetto bRegister
                Login();
            }
        });

        bLoginGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginGuest();
            }
        });

        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent =new Intent(Login.this,Registrazione.class);
                registerIntent.putExtra("ip", ip);//passo ip e porta a Registrazione.class
                registerIntent.putExtra("porta", porta);
                startActivity(registerIntent);
            }
        });
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
    }

    //controlla se è già stata avviata la sessione
    private void controlloPrimoAvvio(){

        if (sessione.loggedin()){
            contatore=contatore+1;
            startActivity(new Intent(Login.this,Home.class));
            finish();
        }
    }

    private void LetturaMappa() {
        TabPunti.deleteAll(TabPunti.class);
        AssetManager am1 = getAssets();
        InputStream is1 = null;

        try {
            is1 = am1.open("Dati.xls");
        } catch (IOException e) {

            e.printStackTrace();
        }
        Workbook wb1 = null;
        try {
            wb1 = Workbook.getWorkbook(is1);
        } catch (IOException e) {


            e.printStackTrace();
        } catch (BiffException e) {


            e.printStackTrace();
        }
        Sheet s1 = wb1.getSheet(0);
        int row1 =s1.getRows();
        int col1= s1.getColumns();


        String xxx = "";
        for (int i = 1; i < row1; i++) {
            for (int c = 0; c < col1; c++) {
                Cell z = s1.getCell(c, i);
                xxx = xxx + z.getContents() + ",";

            }

        }
        System.out.println(xxx);

        int z1 = 0;
        String a1[] = xxx.split(",");
        LoginController salvapunti=new LoginController();
        while (z1 < a1.length) {
            codice1=a1[z1];
            codice1.trim();
            x1=a1[z1+1];
            x1.trim();
            y1=a1[z1+2];
            y1.trim();
            quota1=a1[z1+3];
            quota1.trim();
            address1=a1[z1+4];
            address1.trim();
            data1=a1[z1+5];
            data1.trim();


            new Login.send2().execute("http://"+ip+":"+porta+"/MyFirsRestService/utenti/punti?" +
                    "codice="+codice1+""+
                    "&x="+x1+""+
                    "&y="+y1+""+
                    "&quota="+quota1+""+
                    "&address="+address1+""+
                    "&data="+data1+"");

            int h=salvapunti.SalvaPuntiController(a1[z1], a1[z1 + 1], a1[z1 + 2], a1[z1 + 3], a1[z1 + 4], a1[z1 + 5]);
            switch (h) {
                case 0:
                    displayToast("Punti x,y mancanti!");
                    break;
                case 1:
                    displayToast("Quota mancante!");
                    break;
                case 2:
                    Log.e("Codice:", a1[z1] + " inserito!");

                    z1 = z1 + 6;

            }


        }

    }


    private void controlloServer() {
        AssetManager am = getAssets();
        InputStream is = null;

        try {
            is = am.open("Server.xls");
        } catch (IOException e) {

            e.printStackTrace();
        }
        Workbook wb = null;
        try {
            wb = Workbook.getWorkbook(is);
        } catch (IOException e) {


            e.printStackTrace();
        } catch (BiffException e) {


            e.printStackTrace();
        }
        Sheet s = wb.getSheet(0);



        String xx = "";
        Cell cell=null;
                if(s.getRows()<2)
                {xx=", ,";}
                else {
                    cell = s.getCell(0, 1);
                    xx = xx + cell.getContents() + ",";
                    Log.e("ip", xx);
                    cell = s.getCell(1, 1);
                    xx = xx + cell.getContents() + ",";
                    Log.e("ip e porta", xx);
                }

        int z = 0;
        String a[] = xx.split(",");
        if (a.length == 1 || a.length == 0) {
            porta = "";
            ip = "";

        } else {
            while (z <= a.length / 2) {
                ip = a[z];
                porta = a[z + 1];
                z = z + 2;

            }

        }
        //System.out.println(a.length);

    }

    private void Login(){


        User= Usertext.getText().toString();
        Pass=Passtext.getText().toString();
        Log.i(TAG, "username "+User);


        if (User.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(User).matches()) {
            Usertext.setError("Inserisci una mail valida");
        } else {
            Usertext.setError(null);
        }

        if (Pass.isEmpty() || Pass.length() < 4 || Pass.length() > 10) {
            Passtext.setError("tra 4 e 10 caratteri");
        } else {
            Passtext.setError(null);
        }

        if(loginController.controlUserPasscontroller(User,Pass)){  //controllo se esiste un utente registrato corrispondente
            bLogin.setEnabled(false);
            final ProgressDialog progressDialog = new ProgressDialog(Login.this,
                    R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Autenticazione...");
            progressDialog.show();

            sessione.UtenteLoggato(true,User); //avvio la sessione

            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            // On complete call either onLoginSuccess or onLoginFailed
                            Intent intent = new Intent(Login.this, Home.class); //reinderizzo a Home passando il parametro "username"
                            intent.putExtra("user", User);
                            Login.this.startActivity(intent);
                            // onLoginFailed();
                            progressDialog.dismiss();
                        }
                    }, 1500);
        }else
        {
            Toast.makeText(getApplicationContext(),"User o password sbagliati", Toast.LENGTH_SHORT).show(); //in caso di esito negativo del login mostro u nmessaggio di errore
        }

    }

    private void LoginGuest(){
        int id=loginController.countUtcontroller()+1;
        User="Guest"+id;
        sessione.UtenteLoggato(true,User); //avvio la sessione

        Intent intent = new Intent(Login.this, Home.class); //reinderizzo a Home passando il parametro "username"
        intent.putExtra("user", User);
        Login.this.startActivity(intent);

    }

    public void displayToast(String message){
        Toast.makeText(Login.this, message, Toast.LENGTH_SHORT).show();
    }

    private InputStream ApriConnessioneHttp(String urlString) throws IOException
    {
        InputStream in = null;
        int risposta = -1;

        URL url = new URL(urlString);
        URLConnection conn = url.openConnection();

        if (!(conn instanceof HttpURLConnection))
            throw new IOException("No connessione HTTP");

        try{
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");
            httpConn.connect();
            BufferedReader bf = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
            String value = bf.readLine();
            System.out.println("Output:"+value);
            result= value;
            risposta = httpConn.getResponseCode();
            if (risposta == HttpURLConnection.HTTP_OK) {
                in = httpConn.getInputStream();
            }
        }
        catch (Exception ex) {
            sendBroadcast(new Intent("univpm.iot_for_emergency.View.Offline"));
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
            //displayToast("Errore connessione Server");
            //Log.d("Servizio web", e1.getLocalizedMessage());
        }
        return bit;
    }

    private class send extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... urls) {
            return avvia(urls[0]);

        }
        protected void onPostExecute(String s) {
            final String controllo= result;
            TabUtente.deleteAll(TabUtente.class);

            if (controllo!=null && controllo.length()>0) {

                String pippo[] = result.split(",");

                int i = 0;
                RegistraController registraController=new RegistraController();
                while (i < (pippo.length)) {

                    int c=registraController.Registracontroller(pippo[i + 4],pippo[i], pippo[i + 1], pippo[i + 2], pippo[i + 3],  pippo[i + 5], pippo[i + 6], pippo[i + 2]);

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
                            Log.e("Utenti:", pippo[i + 4] + " inserito!");
                            i = i + 7;
                    }

                }
                if (i==pippo.length) {
                    if(contatore==0) {
                        progressDialogDB.dismiss();
                        contatore = contatore + 1;
                    }
                }
            } else{
                displayToast("Database Server vuoto");
                Log.e("prova2","Db Server Vuoto");
            }

        }
    }

    private class send2 extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... urls) {
            return avvia(urls[0]);

        }

        protected void onPostExecute(String s) {

            final String controllo=result;
            if (controllo==null)
            {
            }

           // Log.e("Punto:",result);
            //displayToast(result);
        }
    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            final String action = intent.getAction();
            if (("univpm.iot_for_emergency.View.Offline").equals(action)) {
                progressDialogDB.dismiss();
                if(contatore<1){
                    bLogin.setEnabled(false);
                    Usertext.setEnabled(false);
                    Passtext.setEnabled(false);
                    registerLink.setEnabled(false);
                    Snackbar.make(findViewById(android.R.id.content), "Sei offline", Snackbar.LENGTH_LONG).show();
                    contatore=contatore+1;
                }
            }
        }
    };



    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("univpm.iot_for_emergency.View.Offline");
        return intentFilter;
    }


}
