package univpm.iot_for_emergency.UI;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import univpm.iot_for_emergency.DbAdapter.Db;
import univpm.iot_for_emergency.R;
import univpm.iot_for_emergency.Funzionali.Sessione;

public class Home extends AppCompatActivity {
    public static final String TAG =Home.class.getSimpleName();
    private Sessione sessione;
    Db db = new Db(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sessione = new Sessione(this);
        if (!sessione.loggedin()) {
            loguot();
        }

        final Button blogout = (Button) findViewById(R.id.buttonLogout);
        blogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loguot();
            }
        });
        Intent intent =getIntent();
        String user=intent.getStringExtra("user");
        user=sessione.user();
        String [] utente=db.getUtente(user);
        final TextView benvenuto=(TextView) findViewById(R.id.textView);
        final TextView nome=(TextView) findViewById(R.id.HomeNome);
        final TextView cognome=(TextView) findViewById(R.id.HomeCognome);
        final TextView password=(TextView) findViewById(R.id.HomePassword);
        final TextView username=(TextView) findViewById(R.id.HomeUser);

        benvenuto.setText(new StringBuilder().append("Benvenuto ").append(user));
        nome.setText(utente[2]);
        cognome.setText(utente[3]);
        password.setText(utente[4]);
        username.setText(user);
    }

    private void loguot(){
        sessione.UtenteLoggato(false,null);
        finish();
        startActivity(new Intent(Home.this,Login.class));
    }
}
