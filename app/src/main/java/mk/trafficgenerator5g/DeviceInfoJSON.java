package mk.trafficgenerator5g;

import org.json.JSONObject;

public class DeviceInfoJSON extends JSONObject {

    public DeviceInfoJSON () {
        Data data = Data.getInstance();
        try {
            this.put("OSType", "Android");

            JSONObject interface1 = new JSONObject();
            interface1.put("ip4Addres", data.deviceIP);
            interface1.put("Name", data.deviceName);
            interface1.put("MacAddr", data.deviceMAC);
            this.put("interfaces", interface1);
            this.put("join", true);



        } catch (Exception ignore) {
        }
    }
}
