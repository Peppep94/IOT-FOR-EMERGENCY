package univpm.iot_for_emergency.Model;

import com.orm.SugarRecord;
import java.util.List;

public class TabDatiBeacon extends SugarRecord{

    public String address;
    public String dateTime;
    public String temperature;
    public String humidity;

    public TabDatiBeacon(){}

    public TabDatiBeacon(String address, String dateTime, String temperature, String humidity){
        this.address = address;
        this.dateTime = dateTime;
        this.temperature=temperature;
        this.humidity=humidity;

    }


    public TabDatiBeacon getDati(String address){

        List<TabDatiBeacon> list = TabDatiBeacon.find(TabDatiBeacon.class,"address=?",address);

        if (list.size()>0)
            return list.get(0);
        else
            return new TabDatiBeacon("0","0","0","0");

    }

}
