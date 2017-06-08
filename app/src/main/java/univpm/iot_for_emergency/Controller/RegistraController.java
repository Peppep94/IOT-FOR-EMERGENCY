package univpm.iot_for_emergency.Controller;

import univpm.iot_for_emergency.Model.TabUtente;


public class RegistraController {

    private TabUtente tabUtente =new TabUtente();

    public int Registracontroller(String Nome,String Cognome,String Pass,String DataN,String User,String Problemi,String Sesso,String Confpass){
        return tabUtente.Registra(Nome,Cognome,Pass,DataN,User,Problemi,Sesso,Confpass);
    }


}
