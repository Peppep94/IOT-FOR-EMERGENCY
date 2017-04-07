package univpm.iot_for_emergency.Controller.Funzionali;


import android.content.Context;
import android.content.SharedPreferences;

public class Sessione {
    SharedPreferences log;
    SharedPreferences.Editor editor;
    Context ctx;

    public Sessione(Context ctx){
        this.ctx = ctx;
        log = ctx.getSharedPreferences("ProveLogin", Context.MODE_PRIVATE);
        editor = log.edit();
    }

    public void UtenteLoggato(boolean loggedin,String user){
        editor.putBoolean("loggedInmode",loggedin);
        editor.putString("user",user);
        editor.commit();
    }

    public boolean loggedin(){
        return log.getBoolean("loggedInmode", false);
    }

    public String user(){
        return log.getString("user","null");
    }
}