package univpm.iot_for_emergency.Controller;

import java.util.List;

import univpm.iot_for_emergency.Model.TabUtente;


/**
 * Created by Giuseppe on 07/04/2017.
 */

public class RegistraController {

    public int Registra(String User,String Nome,String Cognome,String Pass,String DataN,String Problemi,String Sesso,String Confpass){

        List<TabUtente> tabUtente=TabUtente.find(TabUtente.class,"user=?",User);

        if(User.isEmpty() || Pass.isEmpty()){
            return 0;
        }else if (!Pass.equals(Confpass)){
            return 1;
        }else if (tabUtente.size()>0){
            return 2;
        }else{
            TabUtente Utente=new TabUtente(Nome,Cognome,Pass,DataN, User,Problemi,Sesso);
            Utente.save();
            return 3;
        }
    }




}
