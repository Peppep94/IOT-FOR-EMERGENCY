package univpm.iot_for_emergency.Model;

import com.orm.SugarRecord;

import java.util.List;
/*
Classe che rappresenta l'utente, per la sua realizzazione è stato usato SugarOrm (per la sintassi fare riferimento a http://satyan.github.io/sugar/)
 */
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


    public TabUtente getDati(String user){

        List<TabUtente> list =TabUtente.find(TabUtente.class,"user=?",user);
        if (list.size()>0)
            return list.get(0);
        else
            return null;

    }

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
