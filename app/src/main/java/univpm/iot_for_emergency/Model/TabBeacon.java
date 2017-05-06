package univpm.iot_for_emergency.Model;


import com.orm.SugarRecord;

import java.util.List;

public class TabBeacon extends SugarRecord{

    public String address;
    public String dateTime;
    public String temperature;
    public String humidity;

    public TabBeacon(){}

    public TabBeacon(String address, String dateTime,String temperature,String humidity){

        this.address = address;
        this.dateTime = dateTime;
        this.temperature=temperature;
        this.humidity=humidity;

    }


    public TabBeacon getDati(String address){

        List<TabBeacon> list =TabBeacon.find(TabBeacon.class,"address=?",address);

        if (list.size()>0)
            return list.get(0);
        else
            return new TabBeacon("0","0","0","0");

    }

}
