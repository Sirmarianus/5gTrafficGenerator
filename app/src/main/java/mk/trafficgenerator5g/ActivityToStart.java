package mk.trafficgenerator5g;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ActivityToStart {
    String url = "";
    int type;
    LocalDateTime startTime;
    int timeToEnd;

    ActivityToStart() {}
    ActivityToStart(String initValues) throws JSONException {
        Log.d("DUPA", "ActivityToStart -> CREATE");

        JSONObject jsonObject = new JSONObject(initValues);
        this.type = jsonObject.getInt("type");
        this.url = jsonObject.getString("url");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy");
        this.startTime = LocalDateTime.parse(jsonObject.getString("startTime"), formatter);

        this.timeToEnd = jsonObject.getInt("time");

        Log.d("DUPA", "ActivityToStart CREATED Activity");
        Log.d("DUPA", "Type -> " + this.type);
        Log.d("DUPA", "URL -> " + this.url);
        Log.d("DUPA", "StartTime -> " + this.startTime);
        Log.d("DUPA", "Time -> " + this.timeToEnd);
    }
}
