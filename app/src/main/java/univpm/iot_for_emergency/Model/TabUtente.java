package univpm.iot_for_emergency.Model;

import com.orm.SugarRecord;

public class TabUtente extends SugarRecord {

     public String nome ;
     public String cognome ;
     public String password ;
     public String datan ;
     public String user ;
     public String problemi ;
     public String sesso;


    public TabUtente(){
    }

    public TabUtente(String nome, String cognome,String password,String datan,String user,String problemi,String sesso){
        this.nome = nome;
        this.cognome = cognome;
        this.password=password;
        this.datan=datan;
        this.user=user;
        this.problemi=problemi;
        this.sesso=sesso;
    }



}
