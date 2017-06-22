package univpm.iot_for_emergency.View;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
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
import java.net.URLConnection;
import java.util.Calendar;
import java.util.Date;

import univpm.iot_for_emergency.Controller.RegistraController;
import univpm.iot_for_emergency.Model.TabUtente;
import univpm.iot_for_emergency.R;

public class Registrazione extends AppCompatActivity {

    private  String Nome;
    private String Cognome;
    private String User ;
    private  String Pass ;
    private String Sesso;
    private String Problemi;
    private String DataN;
    private String ConfPass;
    String result;
    String ip;
    String porta;
    private TabUtente tabUtente;
    protected TextView mDateDisplay;
    protected ImageButton mPickDate;
    protected int mYear;
    protected int mMonth;
    protected int mDay;

    JSONObject jsonObject = new JSONObject();

    protected DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year,
                                      int monthOfYear, int dayOfMonth) {
                    mYear = year;
                    mMonth = monthOfYear;
                    mDay = dayOfMonth;
                    updateDisplay();
                }

            };



    protected Dialog onCreateDialog(int id) {
        DatePickerDialog dialog = new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay);
        dialog.getDatePicker().setMaxDate(new Date().getTime());
        return dialog;

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final Button bRegister=(Button) findViewById(R.id.buttonRegistra);
        ip = getIntent().getExtras().getString("ip");
        porta = getIntent().getExtras().getString("porta");

        spinner();
        datepicker();
        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegistraServer();
            }
        });


    }

    public void spinner(){

        Spinner spinnersesso = (Spinner)findViewById(R.id.Sesso);

        final ArrayAdapter<String> adaptersesso = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"Maschio","Femmina"}
        );

        spinnersesso.setAdapter(adaptersesso);
        spinnersesso.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adaptersesso, View view,int pos, long id) {
                Sesso = (String)adaptersesso.getItemAtPosition(pos);
            }
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });


        Spinner spinnerproblemi = (Spinner)findViewById(R.id.Problemi);
        final ArrayAdapter<String> adapterproblemi = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"Nessuno","Sedia a rotelle","Stampelle"}
        );
        spinnerproblemi.setAdapter(adapterproblemi);

        spinnerproblemi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterproblemi, View view,int pos, long id) {

                Problemi = (String) adapterproblemi.getItemAtPosition(pos);


            }
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
    }

    public void datepicker(){



        mDateDisplay = (TextView) findViewById(R.id.Data);
        mPickDate = (ImageButton) findViewById(R.id.buttonData);

        mPickDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(0);
            }
        });

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        updateDisplay();
    }

    //cambia la data mostrata a video
    protected void updateDisplay() {
        mDateDisplay.setText(
                new StringBuilder()
                        .append("  ")
                        .append(mDay).append("/")
                        .append(mMonth+1).append("/")
                        .append(mYear).append(" "));
    }

    private void RegistraServer(){

        final EditText name=(EditText) findViewById(R.id.Name);
        final EditText cognome=(EditText) findViewById(R.id.Cognome);
        final EditText username=(EditText) findViewById(R.id.User);
        final EditText password=(EditText) findViewById(R.id.Password);
        final EditText confPassword=(EditText) findViewById(R.id.ConfermaPassword);


        Nome = name.getText().toString();
        Cognome = cognome.getText().toString();
        User = username.getText().toString();
        Pass = password.getText().toString();
        ConfPass = confPassword.getText().toString();

        DataN =  String.valueOf(new StringBuilder()
                .append("  ")
                .append(mDay).append("/")
                .append(mMonth+1).append("/")
                .append(mYear).append(" "));


        DataN=DataN.replace("/","-");

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

        if (!ConfPass.equals(Pass)){
            displayToast("Le password non corrispondono!");

        }else if(User.isEmpty() || Pass.isEmpty()){
            displayToast("User e password non possono essere vuoti!");
        }else{
            new Registrazione.send().execute("http://"+ip+":"+porta+"/MyFirsRestService/utenti");
        }

    }

    /*
    private void Registra(){

        final EditText name=(EditText) findViewById(R.id.Name);
        final EditText cognome=(EditText) findViewById(R.id.Cognome);
        final EditText username=(EditText) findViewById(R.id.User);
        final EditText password=(EditText) findViewById(R.id.Password);
        final EditText confPassword=(EditText) findViewById(R.id.ConfermaPassword);


        Nome = name.getText().toString();
        Cognome = cognome.getText().toString();
        User = username.getText().toString();
        Pass = password.getText().toString();
        String Confpass = confPassword.getText().toString();

        DataN =  String.valueOf(new StringBuilder()
                .append("  ")
                .append(mDay).append("/")
                .append(mMonth+1).append("/")
                .append(mYear).append(" "));
        RegistraController registraController=new RegistraController();

        int c=registraController.Registracontroller(User,Nome,Cognome,Pass,DataN,Problemi,Sesso,Confpass);

           switch (c) {
               case 0:
               if (User.isEmpty())
                   username.setError("Questo campo non può essere vuoto");
               if (Pass.isEmpty())
                   password.setError("Questo campo non può essere vuoto");
               break;
               case 1:
               confPassword.setError("Le password non corrispondono");
               break;
               case 2:
               username.setError("Username non disponibile");
               break;
               case 3:
               displayToast("Utente registrato ");
               finish();
               break;
           }

    }
    */

    //mostra a video dei messaggi
    public void displayToast(String message){
        Toast.makeText(Registrazione.this, message, Toast.LENGTH_SHORT).show();
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

                        finish();
                        break;
                }


            }else{

                displayToast("Utente non registrato in locale!");

            }


        }
    }
}
