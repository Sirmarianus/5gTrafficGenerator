package mk.trafficgenerator5g;

import org.json.JSONObject;

public class StatisticsJSON extends JSONObject{

    public StatisticsJSON (long rxBytes, long rxPackets, long txBytes, long txPackets) {
        Data data = Data.getInstance();
        try {
            this.put("OSType", "Android");

            JSONObject interface1 = new JSONObject();
            interface1.put("ip4Addres", data.deviceIP);
            interface1.put("Name", data.deviceName);
            interface1.put("MacAddr", data.deviceMAC);
            this.put("interfaces", interface1);

            JSONObject statistics = new JSONObject();
            statistics.put("rxBytes", rxBytes);
            statistics.put("rxPackets", rxPackets);
            statistics.put("txBytes", txBytes);
            statistics.put("txPackets", txPackets);
            this.put("statistics", statistics);

        } catch (Exception ignore) {}

    }
}
