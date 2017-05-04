package univpm.iot_for_emergency.View;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import java.util.Calendar;
import univpm.iot_for_emergency.View.Funzionali.Sessione;
import univpm.iot_for_emergency.Controller.ModificaController;
import univpm.iot_for_emergency.Model.TabUtente;
import univpm.iot_for_emergency.R;

/**
 * Created by Giuseppe on 07/04/2017.
 */

public class Modifica_dati extends AppCompatActivity {


    private String User ;
    private String Sesso;
    private String Problemi;

    private int contatore=0;
    private ModificaController modificaController;
    private TabUtente tabUtente;
    private Sessione sessione;
    protected TextView mDateDisplay;
    protected ImageButton mPickDate;
    protected int mYear;
    protected int mMonth;
    protected int mDay;
    protected String sesso;
    protected String problemi;
    private final static String TAG = ModificaController.class.getSimpleName();


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
        return dialog;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifica_dati);
        sessione=new Sessione(this);
        modificaController=new ModificaController();
        User=sessione.user();
        tabUtente=modificaController.getDaticontroller(User);
        final EditText name=(EditText) findViewById(R.id.UNome);
        final EditText cognome=(EditText) findViewById(R.id.UCognome);
        final EditText username=(EditText) findViewById(R.id.UUser);
        final EditText password=(EditText) findViewById(R.id.UPassword);
        final EditText confPassword=(EditText) findViewById(R.id.ConfermaUPassword);
        final Button bconfmodifica=(Button) findViewById(R.id.buttonConfModifica);

        username.setText(User);
        name.setText(tabUtente.nome);
        cognome.setText(tabUtente.cognome);
        password.setText(tabUtente.password);
        confPassword.setText(tabUtente.password);

        problemi=(tabUtente.problemi);
        sesso=(tabUtente.sesso);

        String[] datasezionata=tabUtente.datan.split("/");


        mDay= Integer.parseInt(datasezionata[0].trim());
        mMonth= Integer.parseInt(datasezionata[1])-1;
        mYear= Integer.parseInt(datasezionata[2].trim());
        datepicker();
        spinner();

        bconfmodifica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data= String.valueOf(new StringBuilder()
                        .append("  ")
                        .append(mDay).append("/")
                        .append(mMonth+1).append("/")
                        .append(mYear).append(" "));

                      TabUtente controllo;

                if (!username.getText().toString().contentEquals(User)) {
                    controllo=modificaController.getDaticontroller(username.getText().toString());
                }else
                {
                    controllo=null;
                }
                if(String.valueOf(username.getText()).isEmpty() || String.valueOf(password.getText()).isEmpty()){
                    displayToast("I campi username e password sono obbligatori");
                }else if (!String.valueOf(password.getText()).equals(String.valueOf(confPassword.getText()))){
                    displayToast("Le password non corrispondono");
                }else if (controllo!=null){
                    displayToast("Username non disponibile");
                }else{
                      tabUtente.user=username.getText().toString();
                      tabUtente.nome=name.getText().toString();
                      tabUtente.cognome=cognome.getText().toString();
                      tabUtente.password=password.getText().toString();
                      tabUtente.datan=data;
                      tabUtente.problemi=problemi;
                      tabUtente.sesso=sesso;
                      tabUtente.save();
                    sessione.UtenteLoggato(true,String.valueOf(username.getText()));
                    displayToast("Modifica avvenuta con successo ");
                    Intent intent = new Intent(Modifica_dati.this, Home.class); //reinderizzo a Home passando il parametro "username"
                    Modifica_dati.this.startActivity(intent);
                }
            }
        });

    }


    //mostra a video dei messaggi
    private void displayToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }


    public void spinner(){
        String compareValuesesso=sesso;
        Spinner spinnersesso = (Spinner)findViewById(R.id.USesso);
        final ArrayAdapter<String> adaptersesso = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"Maschio","Femmina"}
        );
        spinnersesso.setAdapter(adaptersesso);
        if (!compareValuesesso.equals(null)) {
            int spinnerPosition = adaptersesso.getPosition(compareValuesesso);
            spinnersesso.setSelection(spinnerPosition);
        }
        spinnersesso.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adaptersesso, View view,int pos, long id) {

                Sesso = (String)adaptersesso.getItemAtPosition(pos);



            }
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        String compareValueprob=problemi;
        Spinner spinnerproblemi = (Spinner)findViewById(R.id.UProblemi);
        final ArrayAdapter<String> adapterproblemi = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"Nessuno","Sedia a rotelle","Stampelle"}
        );
        spinnerproblemi.setAdapter(adapterproblemi);
        if (!compareValueprob.equals(null)) {
            int spinnerPosition = adapterproblemi.getPosition(compareValueprob);
            spinnerproblemi.setSelection(spinnerPosition);
        }
        spinnerproblemi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterproblemi, View view,int pos, long id) {

                Problemi = (String) adapterproblemi.getItemAtPosition(pos);


            }
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
    }
    public void datepicker(){


        mDateDisplay = (TextView) findViewById(R.id.UData);
        mPickDate = (ImageButton) findViewById(R.id.buttonUData);

        mPickDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(0);
            }
        });
        if(contatore!=0) {
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
            updateDisplay();
        }
        else
        {updateDisplay();}
        contatore++;
    }


    protected void updateDisplay() {
        mDateDisplay.setText(
                new StringBuilder()
                        .append("  ")
                        .append(mDay).append("/")
                        .append(mMonth+1).append("/")
                        .append(mYear).append(" "));
    }



}
