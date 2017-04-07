package univpm.iot_for_emergency.Controller;

import android.util.Log;

import java.util.List;

import univpm.iot_for_emergency.Model.TabUtente;

/**
 * Created by Giuseppe on 07/04/2017.
 */

public class LoginController {

    private final static String TAG = LoginController.class.getSimpleName();

    public   boolean controlUserPass(String User, String Password){
        List<TabUtente> tabUtente=TabUtente.find(TabUtente.class,"user=? and password=?",User,Password);
        if (tabUtente.size()==0){
            return false;}
        return true;
    }

    public int countUt(){
        int c;
        return c= (int) TabUtente.count(TabUtente.class);
    }

}

