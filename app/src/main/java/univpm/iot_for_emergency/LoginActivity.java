package univpm.iot_for_emergency;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private DbHelper db; //dichiaro gli oggetti db e session che verranno utilizzati per fare query e per controllare se la sessione è attiva o no
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {  // comando inziale di ogni classe che crea l'oggetto LoginActivity e setta il layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        db = new DbHelper(this);         //istanzio gli oggetti session e db
        session=new Session(this);
        final EditText Username=(EditText) findViewById(R.id.User);  // dichiaro gli oggetti di vari tipi  e li associo agli elementi dell'interfaccia grafica
        final EditText Password=(EditText) findViewById(R.id.Password);
        final Button bRegister=(Button) findViewById(R.id.button);
        final TextView registerLink = (TextView) findViewById(R.id.RegisterHere);

        controlloPrimoAvvio();  // controlla se la sessione è arriva nel caso lo fosse reindirizza ad HomeActivity

        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {    //associo un'azione all'oggetto bRegister
                String username= Username.getText().toString();
                String pass=Password.getText().toString();

                if(db.getUser(username,pass)){  //controllo se esiste un utente registrato corrispondente
                    session.setLoggedin(true); //avvio la sessione

                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class); //reinderizzo a HomeActivity passando il parametro "username"
                    intent.putExtra("user", username);
                    LoginActivity.this.startActivity(intent);
                }else
                {
                    Toast.makeText(getApplicationContext(),"User o password sbagliati",Toast.LENGTH_SHORT).show(); //in caso di esito negativo del login mostro u nmessaggio di errore
                }
            }
        });
        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent =new Intent(LoginActivity.this,RegisterActivity.class);
                LoginActivity.this.startActivity(registerIntent);
            }
        });


    }


    private void controlloPrimoAvvio(){

        if (session.loggedin()){
            startActivity(new Intent(LoginActivity.this,HomeActivity.class));
            finish();
        }
    }
}
