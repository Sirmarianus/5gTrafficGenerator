package mk.trafficgenerator5g;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class Parser {
    public static JSONObject fromStringToJSON(String s) {
        Log.d("DUPA", "fromStringToJSON -> PARSING");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(s);
        } catch (JSONException e) {
            Log.d("DUPA", "fromStringToJSON -> " + e);
        }

        return jsonObject;
    }

    public static String fromJSONToString(JSONObject arr){
        return arr.toString();
    }
}
