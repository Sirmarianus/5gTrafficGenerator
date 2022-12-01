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

    ActivityToStart() {}
    ActivityToStart(String initValues) throws JSONException {
        Log.d("DUPA", "ActivityToStart -> CREATE");

        JSONObject jsonObject = new JSONObject(initValues);
        this.type = jsonObject.getString("type");
        this.url = jsonObject.getString("url");

        this.startTime = LocalTime.parse(jsonObject.getString("startTime"), DateTimeFormatter.ISO_TIME);

        this.duration = jsonObject.getInt("duration");

        Log.d("DUPA", "ActivityToStart CREATED Activity");
        Log.d("DUPA", "Type -> " + this.type);
        Log.d("DUPA", "URL -> " + this.url);
        Log.d("DUPA", "StartTime -> " + this.startTime);
        Log.d("DUPA", "Duration -> " + this.duration);
    }
}
