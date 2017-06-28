package univpm.iot_for_emergency.Controller;


import univpm.iot_for_emergency.Model.TabPunti;


public class LoginController {


    private TabPunti tabPunti =new TabPunti();


    public int SalvaPuntiController(String codice, String x, String y, String quota,String address,String data){
        return tabPunti.SalvaPunti(codice,x,y,quota,address,data);
    }

}

