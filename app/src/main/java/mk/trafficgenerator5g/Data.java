package mk.trafficgenerator5g;

import android.app.DownloadManager;
import android.util.Log;

import org.json.JSONException;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedList;

public class Data {

    private static boolean shouldThreadsBeGoing = true;

    public static final String APPLICATION_TYPE_CANCEL = "CANCEL";
    public static final String APPLICATION_TYPE_YT = "YT";
    public static final String APPLICATION_TYPE_PAGE = "PAGE";
    public static final String APPLICATION_TYPE_MAPS = "MAPS";
    public static final String APPLICATION_TYPE_DOWNLOAD = "DOWNLOAD";

    public static String STARTED_ACTIVITY = "";

    public String serverIP;
    public String deviceIP;
    public String deviceMAC = "00:0A:E6:3E:FD:E1";
    public String deviceName = "Legio";

    private final ActivityToStart emptyActivityToStart = new ActivityToStart();

    // Download files stuff
    ArrayList<Long> downloadingFilesIDs = new ArrayList<>();
    DownloadManager manager;

    // Singleton stuff
    private final static Data INSTANCE = new Data();
    public synchronized static Data getInstance(){ return INSTANCE; }
    public static synchronized boolean getShouldThreadsBeGoing() { return shouldThreadsBeGoing; }
    public static synchronized void stopServices() { shouldThreadsBeGoing=false; }

    // Handling messages from Server to app
    private final LinkedList<ActivityToStart> messagesFromServer = new LinkedList<>();
    public synchronized ActivityToStart getOrSetMessageFromServer(boolean isMethodUsedAsGetter, String message) {
        Log.e("Queue1 size: ", String.valueOf(messagesFromServer.size()));
        for (ActivityToStart ele : messagesFromServer) {
            Log.e("Queue1 type: ", String.valueOf(ele.type));
        }
        if (isMethodUsedAsGetter) {
            if (this.messagesFromServer.isEmpty() || messagesFromServer.getFirst().startTime.isAfter(LocalTime.now())) {
                return emptyActivityToStart;
            }
            else {
                return messagesFromServer.poll();
            }
        }
        else{
            ActivityToStart activityToStartToBeSet = null;
            try {
                activityToStartToBeSet = new ActivityToStart(message);
            } catch (JSONException e) {
                return emptyActivityToStart;
            }
            addToQueue(activityToStartToBeSet);
            if (activityToStartToBeSet.duration != 0) {
                LocalTime startTime = activityToStartToBeSet.startTime.plusMinutes(activityToStartToBeSet.duration);

                activityToStartToBeSet = new ActivityToStart();
                activityToStartToBeSet.url = "CANCEL";
                activityToStartToBeSet.type = APPLICATION_TYPE_CANCEL;
                activityToStartToBeSet.startTime = startTime;
                activityToStartToBeSet.duration = 0;
                addToQueue(activityToStartToBeSet);
                clearQueueFromNotNeededStoppers();
            }
            return emptyActivityToStart;
        }
    }

    private void addToQueue(ActivityToStart activityToStartToBeSet) {
        if (messagesFromServer.isEmpty()) {
            messagesFromServer.addLast(activityToStartToBeSet);
        }
        else {
            if (messagesFromServer.getLast().startTime.isBefore(activityToStartToBeSet.startTime)) {
                messagesFromServer.addLast(activityToStartToBeSet);
            }
            else if (messagesFromServer.getLast().startTime.equals(activityToStartToBeSet.startTime)) {
                activityToStartToBeSet.startTime.plusMinutes(1);
                messagesFromServer.addLast(activityToStartToBeSet);
            }
            else {
                for (int i = 0; i < messagesFromServer.size(); i++) {
                    if (messagesFromServer.get(i).startTime.isAfter(activityToStartToBeSet.startTime)) {
                        messagesFromServer.add(i, activityToStartToBeSet);
                    }
                    else if (messagesFromServer.get(i).startTime.equals(activityToStartToBeSet.startTime)) {
                        activityToStartToBeSet.startTime.plusMinutes(1);
                        messagesFromServer.add(i, activityToStartToBeSet);
                    }
                }
            }
        }
    }

    private void clearQueueFromNotNeededStoppers() {
        for (int i=messagesFromServer.size()-1; i>0; i--) {
            if (messagesFromServer.get(i).type.equals(APPLICATION_TYPE_CANCEL)) {
                if (!messagesFromServer.get(i-1).type.equals(APPLICATION_TYPE_YT)) {
                    messagesFromServer.remove(i);
                }
            }
        }
    }

    // Handling messages to Server from app
    private final LinkedList<String> messagesToServer = new LinkedList<>();
    public synchronized String getOrSetMessageToServer(boolean isMethodUsedAsGetter, String message) {
        Log.e("Queue2 size: ", String.valueOf(messagesToServer.size()));
        if (isMethodUsedAsGetter) {
            if (messagesToServer.isEmpty()) {
                return "";
            }
            else {
                return messagesToServer.poll();
            }
        }
        else {
            messagesToServer.addLast(message);
            return "";
        }
    }
}