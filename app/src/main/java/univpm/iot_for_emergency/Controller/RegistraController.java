package univpm.iot_for_emergency.Controller;

import java.util.List;

import univpm.iot_for_emergency.Model.TabUtente;


/**
 * Created by Giuseppe on 07/04/2017.
 */

public class RegistraController {

    private TabUtente tabUtente =new TabUtente();

    public int Registracontroller(String User,String Nome,String Cognome,String Pass,String DataN,String Problemi,String Sesso,String Confpass){
        return tabUtente.Registra(User,Nome,Cognome,Pass,DataN,Problemi,Sesso,Confpass);
    }


}
