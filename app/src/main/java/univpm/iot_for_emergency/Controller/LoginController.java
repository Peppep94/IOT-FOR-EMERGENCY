package univpm.iot_for_emergency.Controller;


import univpm.iot_for_emergency.Model.TabUtente;


public class LoginController {

    private TabUtente tabUtente =new TabUtente();

    public   boolean controlUserPasscontroller(String User, String Password){return tabUtente.controlUserPass(User,Password);}

    public int countUtcontroller(){
        return tabUtente.countUt();
    }

}

