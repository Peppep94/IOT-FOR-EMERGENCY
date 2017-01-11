package univpm.iot_for_emergency;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity {
    public static final String TAG =HomeActivity.class.getSimpleName();
    private Session session;
    DbHelper db = new DbHelper(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        session = new Session(this);
        if (!session.loggedin()) {
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
        String [] utente=db.getUtente(user);


        final TextView nome=(TextView) findViewById(R.id.HomeNome);
        final TextView cognome=(TextView) findViewById(R.id.HomeCognome);
        final TextView password=(TextView) findViewById(R.id.HomePassword);
        final TextView username=(TextView) findViewById(R.id.HomeUser);

        nome.setText(utente[2]);
        cognome.setText(utente[3]);
        password.setText(utente[4]);
        username.setText(user);
    }

    private void loguot(){
        session.setLoggedin(false);
        finish();
        startActivity(new Intent(HomeActivity.this,LoginActivity.class));
    }
}
