package univpm.iot_for_emergency.Controller;


import univpm.iot_for_emergency.Model.TabPunti;
import univpm.iot_for_emergency.Model.TabUtente;


public class LoginController {

    private TabUtente tabUtente =new TabUtente();
    private TabPunti tabPunti =new TabPunti();

    public   boolean controlUserPasscontroller(String User, String Password){return tabUtente.controlUserPass(User,Password);}

    public int countUtcontroller(){
        return tabUtente.countUt();
    }

    public int SalvaPuntiController(String codice, String x, String y, String quota,String address,String data){
        return tabPunti.SalvaPunti(codice,x,y,quota,address,data);
    }

}

