package univpm.iot_for_emergency.UI;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import univpm.iot_for_emergency.DbAdapter.Db;
import univpm.iot_for_emergency.R;
import univpm.iot_for_emergency.Funzionali.Sessione;

public class Login extends AppCompatActivity {
    private String User;
    private String Pass;


    private Db db; //dichiaro gli oggetti db e sessione che verranno utilizzati per fare query e per controllare se la sessione è attiva o no
    private Sessione sessione;

    @Override
    protected void onCreate(Bundle savedInstanceState) {  // comando inziale di ogni classe che crea l'oggetto Login e setta il layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        db = new Db(this);         //istanzio gli oggetti sessione e db
        sessione =new Sessione(this);

        final Button bRegister=(Button) findViewById(R.id.button);
        final Button bLoginGuest=(Button) findViewById(R.id.bLoginGuest);
        final TextView registerLink = (TextView) findViewById(R.id.RegisterHere);

        controlloPrimoAvvio();  // controlla se la sessione è arriva nel caso lo fosse reindirizza ad Home

        bRegister.setOnClickListener(new View.OnClickListener() {
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

        if(db.getUser(User,Pass)){  //controllo se esiste un utente registrato corrispondente
            sessione.UtenteLoggato(true,User); //avvio la sessione

            Intent intent = new Intent(Login.this, Home.class); //reinderizzo a Home passando il parametro "username"
            intent.putExtra("user", User);
            Login.this.startActivity(intent);
        }else
        {
            Toast.makeText(getApplicationContext(),"User o password sbagliati",Toast.LENGTH_SHORT).show(); //in caso di esito negativo del login mostro u nmessaggio di errore
        }
    }

    private void LoginGuest(){
        int id=db.MaxId();
        User="Guest"+id;
        sessione.UtenteLoggato(true,User); //avvio la sessione

        Intent intent = new Intent(Login.this, Home.class); //reinderizzo a Home passando il parametro "username"
        intent.putExtra("user", User);
        Login.this.startActivity(intent);

    }
}
