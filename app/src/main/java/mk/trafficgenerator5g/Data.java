package mk.trafficgenerator5g;

import android.app.DownloadManager;
import android.util.Log;

import org.json.JSONException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;

public class Data {

    private static boolean shouldThreadsBeGoing = true;

    public static final int APPLICATION_TYPE_CANCEL = 0;
    public static final int APPLICATION_TYPE_YT = 1;
    public static final int APPLICATION_TYPE_CHROME = 2;
    public static final int APPLICATION_TYPE_MAPS = 3;
    public static final int APPLICATION_TYPE_DOWNLOAD = 4;

    public static int STARTED_ACTIVITY = 0;

    public String serverIP;

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
        Log.e("Queue size: ", String.valueOf(messagesFromServer.size()));
        for (ActivityToStart ele : messagesFromServer) {
            Log.e("Queue type: ", String.valueOf(ele.type));
        }
        if (isMethodUsedAsGetter) {
            if (this.messagesFromServer.isEmpty() || messagesFromServer.getFirst().startTime.isAfter(LocalDateTime.now())) {
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
            if (activityToStartToBeSet.timeToEnd != 0) {
                LocalDateTime startTime = activityToStartToBeSet.startTime.plusMinutes(activityToStartToBeSet.timeToEnd);

                activityToStartToBeSet = new ActivityToStart();
                activityToStartToBeSet.url = "CANCEL";
                activityToStartToBeSet.type = APPLICATION_TYPE_CANCEL;
                activityToStartToBeSet.startTime = startTime;
                activityToStartToBeSet.timeToEnd = 0;
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
            else if (messagesFromServer.getLast().startTime.isEqual(activityToStartToBeSet.startTime)) {
                activityToStartToBeSet.startTime.plusMinutes(1);
                messagesFromServer.addLast(activityToStartToBeSet);
            }
            else {
                for (int i = 0; i < messagesFromServer.size(); i++) {
                    if (messagesFromServer.get(i).startTime.isAfter(activityToStartToBeSet.startTime)) {
                        messagesFromServer.add(i, activityToStartToBeSet);
                    }
                    else if (messagesFromServer.get(i).startTime.isEqual(activityToStartToBeSet.startTime)) {
                        activityToStartToBeSet.startTime.plusMinutes(1);
                        messagesFromServer.add(i, activityToStartToBeSet);
                    }
                }
            }
        }
    }

    private void clearQueueFromNotNeededStoppers() {
        for (int i=messagesFromServer.size()-1; i>0; i--) {
            if (messagesFromServer.get(i).type == APPLICATION_TYPE_CANCEL) {
                if (messagesFromServer.get(i-1).type != APPLICATION_TYPE_YT) {
                    messagesFromServer.remove(i);
                }
            }
        }
    }
}