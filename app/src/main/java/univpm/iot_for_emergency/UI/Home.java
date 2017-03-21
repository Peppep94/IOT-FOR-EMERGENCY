package univpm.iot_for_emergency.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;


import univpm.iot_for_emergency.Funzionali.BleAdapter;
import univpm.iot_for_emergency.Funzionali.Sessione;
import univpm.iot_for_emergency.R;

public class Home extends AppCompatActivity

        implements NavigationView.OnNavigationItemSelectedListener {

    private Sessione sessione;
    private String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sessione = new Sessione(this);
        if (!sessione.loggedin()) {
            loguot();
        }


        user=sessione.user();

        final TextView benvenuto=(TextView) findViewById(R.id.textView);


        benvenuto.setText(new StringBuilder().append("Benvenuto ").append(user));


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }




    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.modificadati) {
            if(user.contains("Guest"))
            {
                Toast.makeText(getApplicationContext(),"Non sei abilitato alla modifica",Toast.LENGTH_SHORT).show(); //in caso di utente guest non permetto la modifica
            }
            else {
                Intent intent = new Intent(Home.this, Modifica_dati.class); //reinderizzo a Modificadati
                Home.this.startActivity(intent);
            }
        } else if (id == R.id.logout) {
          loguot();
        } else if( id==R.id.ricerca)
        {
            Intent intent = new Intent(Home.this, BleAdapter.class); //reinderizzo a Modificadati
            Home.this.startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loguot(){
        sessione.UtenteLoggato(false,null);
        finish();
        startActivity(new Intent(Home.this,Login.class));
    }
}
