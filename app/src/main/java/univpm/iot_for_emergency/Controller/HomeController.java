package univpm.iot_for_emergency.Controller;

import univpm.iot_for_emergency.Model.TabDatiBeacon;

public class HomeController {

    private TabDatiBeacon tabDatiBeacon =new TabDatiBeacon();

    public TabDatiBeacon getTabBeacon(String address){
        return tabDatiBeacon.getDati(address);
    }

    public void updatesaveBeacon(String address, String datetime,String temperature,String humidity){

         tabDatiBeacon =getTabBeacon(address);

         if(tabDatiBeacon.address.equals("0")){
             tabDatiBeacon =new TabDatiBeacon(address,datetime,temperature,humidity);
             tabDatiBeacon.save();
         }else
         {
             tabDatiBeacon.dateTime=datetime;
             tabDatiBeacon.temperature=temperature;
             tabDatiBeacon.humidity=humidity;
             tabDatiBeacon.save();
         }
          }
}
