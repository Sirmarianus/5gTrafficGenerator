package mk.trafficgenerator5g;

import org.json.*;

public class ParserJSON {
    public static JSONObject createJsonWithStatistics(long rxBytes, long rxPackets, long txBytes, long txPackets) {
        Data data = Data.getInstance();
        try {
            return new JSONObject()
                    .put("Join", false)
                    .put("OSType", "Android")
                    .put("interfaces", new JSONArray()
                            .put(new JSONObject()
                                    .put("ip4Addres", data.deviceIP)
                                    .put("Name", data.deviceName)
                                    .put("MacAddr", data.deviceMAC)
                            )
                    )
                    .put("rxBytes", rxBytes)
                    .put("rxPackets", rxPackets)
                    .put("txBytes", txBytes)
                    .put("txPackets", txPackets);
        }
        catch (Exception e) { return null; }
    }

    public static JSONObject createJsonWithoutStatistics() {
        Data data = Data.getInstance();
        try {
            return new JSONObject()
                    .put("join", true)
                    .put("OSType", "Android")
                    .put("interfaces", new JSONArray()
                            .put(new JSONObject()
                                    .put("ip4Addres", data.deviceIP)
                                    .put("Name", data.deviceName)
                                    .put("MacAddr", data.deviceMAC)
                            )
                    )
                    .put("rxBytes", 0)
                    .put("rxPackets", 0)
                    .put("txBytes", 0)
                    .put("txPackets", 0);
        }
        catch (Exception e) { return null; }
    }
}
