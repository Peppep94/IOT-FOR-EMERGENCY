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
        String user;
        user=sessione.user();

        final TextView benvenuto=(TextView) findViewById(R.id.textView);
        final Button modifica=(Button) findViewById(R.id.buttonmodifica);

        if(user.contains("Guest"))
        {
            modifica.setVisibility(View.INVISIBLE);
        }

        benvenuto.setText(new StringBuilder().append("Benvenuto ").append(user));


        modifica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, Modifica_dati.class); //reinderizzo a Modificadati
                Home.this.startActivity(intent);
            }
        });
    }

    private void loguot(){
        sessione.UtenteLoggato(false,null);
        finish();
        startActivity(new Intent(Home.this,Login.class));
    }
}
