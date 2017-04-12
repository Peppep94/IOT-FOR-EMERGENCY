package univpm.iot_for_emergency.Controller;

import univpm.iot_for_emergency.Model.TabUtente;

/**
 * Created by Giuseppe on 07/04/2017.
 */

public class ModificaController {

    private TabUtente tabUtente =new TabUtente();

    public TabUtente getDaticontroller(String user){
        return tabUtente.getDati(user);

    }
}
