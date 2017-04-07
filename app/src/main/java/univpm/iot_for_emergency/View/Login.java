package univpm.iot_for_emergency.View;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import univpm.iot_for_emergency.Controller.Funzionali.BluetoothLeService;
import univpm.iot_for_emergency.Controller.Funzionali.Sessione;
import univpm.iot_for_emergency.Controller.LoginController;
import univpm.iot_for_emergency.R;


public class Login extends AppCompatActivity {
    private String User;
    private String Pass;
    private Sessione sessione;
    private LoginController loginController;
    private final static String TAG = Login.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessione =new Sessione(this);
        loginController=new LoginController();
        final Button bLogin=(Button) findViewById(R.id.button);
        final Button bLoginGuest=(Button) findViewById(R.id.bLoginGuest);
        final TextView registerLink = (TextView) findViewById(R.id.RegisterHere);


        controlloPrimoAvvio();  // controlla se la sessione è arriva nel caso lo fosse reindirizza ad Home

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
                Login.this.startActivity(registerIntent);
            }
        });
    }

    //controlla se è già stata avviata la sessione
    private void controlloPrimoAvvio(){

        if (sessione.loggedin()){
            startActivity(new Intent(Login.this,Home.class));
            finish();
        }
    }

    private void Login(){

        final EditText username=(EditText) findViewById(R.id.User);  // dichiaro gli oggetti di vari tipi  e li associo agli elementi dell'interfaccia grafica
        final EditText password=(EditText) findViewById(R.id.Password);

        User= username.getText().toString();
        Pass=password.getText().toString();
        Log.i(TAG, "username "+User);

        if(loginController.controlUserPass(User,Pass)){  //controllo se esiste un utente registrato corrispondente
            sessione.UtenteLoggato(true,User); //avvio la sessione
            Intent intent = new Intent(Login.this, Home.class); //reinderizzo a Home passando il parametro "username"
            intent.putExtra("user", User);
            Login.this.startActivity(intent);
        }else
        {
            Toast.makeText(getApplicationContext(),"User o password sbagliati", Toast.LENGTH_SHORT).show(); //in caso di esito negativo del login mostro u nmessaggio di errore
        }

    }

    private void LoginGuest(){
        int id=loginController.countUt()+1;
        User="Guest"+id;
        sessione.UtenteLoggato(true,User); //avvio la sessione

        Intent intent = new Intent(Login.this, Home.class); //reinderizzo a Home passando il parametro "username"
        intent.putExtra("user", User);
        Login.this.startActivity(intent);

    }
}
