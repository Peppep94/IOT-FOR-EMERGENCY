package univpm.iot_for_emergency.UI;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import univpm.iot_for_emergency.DbAdapter.Db;
import univpm.iot_for_emergency.R;


public class Registrazione extends AppCompatActivity {

    private  String Nome;
    private String Cognome;
    private String User ;
    private  String Pass ;
    private String Sesso;
    private String Problemi;
    private String DataN;

    private Db db;
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


    //operazioni effettuate automaticamente all'avvio dell'attività
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db= new Db(this);
        final Button bRegister=(Button) findViewById(R.id.buttonRegistra);

        spinner();
        datepicker();
        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Registra();
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
    // richiama l'oggetto db e gli fa inserire un nuovo utente
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
                .append(mDay).append("/")
                .append(mMonth+1).append("/")
                .append(mYear).append(" "));

        String[] controllo=db.getUtente(User);
        if(User.isEmpty() || Pass.isEmpty()){
            displayToast("I campi contrassegnati con * non posso essere vuoti");
        }else if (!Pass.equals(Confpass)){
            displayToast("Le password non corrispondono");
        }else if (!(controllo[1] ==null)){
            displayToast("Username non disponibile");
        }else{
           db.InserisciUtenti(User,Nome,Cognome,Pass, DataN,Problemi,Sesso);
            displayToast("Utente registrato ");
            finish();
        }
    }
    //cambia la data mostrata a video
    protected void updateDisplay() {
        mDateDisplay.setText(
                new StringBuilder()
                        .append(mDay).append("/")
                        .append(mMonth+1).append("/")
                        .append(mYear).append(" "));
    }
    //mostra a video dei messaggi
    public void displayToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

}
