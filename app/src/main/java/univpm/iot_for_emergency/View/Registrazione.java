package univpm.iot_for_emergency.View;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
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
import univpm.iot_for_emergency.View.Funzionali.Sessione;

public class Registrazione extends AppCompatActivity {

    private  String Nome;
    private String Cognome;
    private String User ;
    private  String Pass ;
    private String Sesso;
    private String Problemi;
    private String DataN;
    private String ConfPass;
    String ip;
    String porta;
    private Sessione sessione;
    protected TextView mDateDisplay;
    protected ImageButton mPickDate;
    protected int mYear;
    protected int mMonth;
    protected int mDay;


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
        sessione=new Sessione(this);
        ip=sessione.ip();
        porta=sessione.porta();
        final Button bRegister=(Button) findViewById(R.id.buttonRegistra);

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

        Intent intent=new Intent();
        intent.setAction("univpm.iot_for_emergency.View.Registrazione");
        intent.putExtra("nome", Nome);
        intent.putExtra("cognome", Cognome);
        intent.putExtra("pass", Pass);
        intent.putExtra("user", User);
        intent.putExtra("sesso", Sesso);
        intent.putExtra("problemi", Problemi);
        intent.putExtra("datan", DataN);
        intent.putExtra("confpass", ConfPass);
        sendBroadcast(intent);

    }

    //mostra a video dei messaggi
    public void displayToast(String message){
        Toast.makeText(Registrazione.this, message, Toast.LENGTH_SHORT).show();
    }



}
