package univpm.iot_for_emergency;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Calendar;

import android.util.Log;



public class RegisterActivity extends AppCompatActivity {
    public static final String TAG =RegisterActivity.class.getSimpleName();
    private DbHelper db;
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
        return new DatePickerDialog(this,
                mDateSetListener,
                mYear, mMonth, mDay);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db= new DbHelper(this);


        final EditText Name=(EditText) findViewById(R.id.Name);
        final EditText Cognome=(EditText) findViewById(R.id.Cognome);
        final EditText Username=(EditText) findViewById(R.id.User);
        final EditText Password=(EditText) findViewById(R.id.Password);
        final EditText ConfPassword=(EditText) findViewById(R.id.ConfermaPassword);

        final Button bRegister=(Button) findViewById(R.id.buttonRegistra);
        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nome = Name.getText().toString();
                String cognome = Cognome.getText().toString();
                String user = Username.getText().toString();
                String pass = Password.getText().toString();
                String confpass = ConfPassword.getText().toString();

                StringBuilder date = new StringBuilder()
                        .append(mYear).append("-")
                        .append(mMonth+1).append("-")
                        .append(mDay).append(" ");
                String[] controllo=db.getUtente(user);
                if(user.isEmpty() || pass.isEmpty()){
                    displayToast("I campi contrassegnati con * non posso essere vuoti");
                }else if (!pass.equals(confpass)){
                    displayToast("Le password non corrispondono");
                }else if (!controllo[1].isEmpty()){
                    displayToast("Username non disponibile");
                }else{
                    db.addUser(user,nome,cognome,pass, String.valueOf(date),"nessuno");
                    displayToast("Utente registrato ");
                    finish();
                }
            }
        });




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


    protected void updateDisplay() {
        mDateDisplay.setText(
                new StringBuilder()
                        .append(mMonth + 1).append("/")
                        .append(mDay).append("/")
                        .append(mYear).append(" "));
    }

    private void displayToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

}
