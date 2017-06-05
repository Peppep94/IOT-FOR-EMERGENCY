package univpm.iot_for_emergency.View;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import univpm.iot_for_emergency.View.Funzionali.Sessione;
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
        final Button login=(Button) findViewById(R.id.button);

        User= username.getText().toString();
        Pass=password.getText().toString();
        Log.i(TAG, "username "+User);


        if (User.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(User).matches()) {
            username.setError("Inserisci una mail valida");
        } else {
            username.setError(null);
        }

        if (Pass.isEmpty() || password.length() < 4 || password.length() > 10) {
            password.setError("tra 4 e 10 caratteri");
        } else {
            password.setError(null);
        }

        if(loginController.controlUserPasscontroller(User,Pass)){  //controllo se esiste un utente registrato corrispondente
            login.setEnabled(false);
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
}
