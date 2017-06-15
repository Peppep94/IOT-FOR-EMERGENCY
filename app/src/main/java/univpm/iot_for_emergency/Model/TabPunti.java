package univpm.iot_for_emergency.Model;

import com.orm.SugarRecord;

import java.util.List;

/**
 * Created by MacBookPro on 06/06/17.
 */

public class TabPunti extends SugarRecord{

    public String codice;
    public String x;
    public String y;
    public String quota;
    public String address;
    public String data;

    public TabPunti(){}

    public TabPunti(String codice, String x, String y, String quota,String address,String data){

        this.codice = codice;
        this.x=x;
        this.y=y;
        this.quota=quota;
        this.address = address;
        this.data = data;

    }

    public int SalvaPunti(String codice, String x, String y, String quota,String address,String data){

        if(x.isEmpty() || y.isEmpty()){
            return 0;
        }else if (quota.isEmpty()){
            return 1;
        }else{
            TabPunti Punti=new TabPunti(codice,x,y,quota,address,data);
            Punti.save();
            return 2;
        }

    }

    public TabPunti TrovaCoordQuotaModel(String address)
    {
        List<TabPunti> tabPunti = TabPunti.find(TabPunti.class,"address=?",address);
        /*int[] coord={0,0};
        coord[0]= Integer.parseInt(tabPunti.get(0).x);
        coord[1]= Integer.parseInt(tabPunti.get(0).y);
        coord[2]= Integer.parseInt(tabPunti.get(0).quota);*/
        return tabPunti.get(0);
    }


}
