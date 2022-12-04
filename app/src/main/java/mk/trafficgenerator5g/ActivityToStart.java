package mk.trafficgenerator5g;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ActivityToStart {
    String url = "";
    String type;
    LocalTime startTime;
    int duration;

    ActivityToStart() {
        Log.d("ActivityToStart", "CREATE EMPTY");
    }
    ActivityToStart(String initValues) throws JSONException {
        Log.d("ActivityToStart", "CREATE");

        JSONObject jsonObject = new JSONObject(initValues);
        this.type = jsonObject.getString("type");
        this.url = jsonObject.getString("url");

        this.startTime = LocalTime.parse(jsonObject.getString("startTime"), DateTimeFormatter.ISO_TIME);

        this.duration = jsonObject.getInt("duration");

        Log.d("ActivityToStart", "CREATED Activity");
        Log.d("ActivityToStart", "Type -> " + this.type);
        Log.d("ActivityToStart", "URL -> " + this.url);
        Log.d("ActivityToStart", "StartTime -> " + this.startTime);
        Log.d("ActivityToStart", "Duration -> " + this.duration);
    }
}
