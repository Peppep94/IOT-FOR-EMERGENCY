package univpm.iot_for_emergency.Controller;

import android.util.Log;

import java.util.List;

import univpm.iot_for_emergency.Model.TabUtente;

/**
 * Created by Giuseppe on 07/04/2017.
 */

public class ModificaController {

    public TabUtente getDati(String user){

        List<TabUtente> list =TabUtente.find(TabUtente.class,"user=?",user);
        if (list.size()>0)
        return list.get(0);
        else
            return null;

    }
}
