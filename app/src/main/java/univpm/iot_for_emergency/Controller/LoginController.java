package univpm.iot_for_emergency.Controller;

import android.util.Log;

import java.util.List;

import univpm.iot_for_emergency.Model.TabUtente;

/**
 * Created by Giuseppe on 07/04/2017.
 */

public class LoginController {

    private TabUtente tabUtente =new TabUtente();

    public   boolean controlUserPasscontroller(String User, String Password){
        return tabUtente.controlUserPass(User,Password);
    }

    public int countUtcontroller(){
        return tabUtente.countUt();
    }

}

