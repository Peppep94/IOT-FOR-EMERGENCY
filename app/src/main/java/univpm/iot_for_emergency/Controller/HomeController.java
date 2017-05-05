package univpm.iot_for_emergency.Controller;

import univpm.iot_for_emergency.Model.TabBeacon;

public class HomeController {

    private TabBeacon tabBeacon=new TabBeacon();

    public TabBeacon getTabBeacon(String address){
        return tabBeacon.getDati(address);
    }

    public void updatesaveBeacon(String address, String datetime,String temperature,String humidity){
         tabBeacon=getTabBeacon(address);

         if(tabBeacon.equals(null)){
             tabBeacon=new TabBeacon(address,datetime,temperature,humidity);
             tabBeacon.save();
         }else
         {
             tabBeacon.dateTime=datetime;
             tabBeacon.temperature=temperature;
             tabBeacon.humidity=humidity;
             tabBeacon.save();
         }
          }
}
