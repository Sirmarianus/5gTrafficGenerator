package mk.trafficgenerator5g;

import android.content.Context;
import android.content.Intent;

import org.json.JSONException;

import java.time.LocalDateTime;
import java.util.LinkedList;

public class Data {

    public String serverIP;
    public boolean shouldThreadsBeGoing = true;
    private final ActivityToStart emptyActivityToStart = new ActivityToStart();
    public static final int APPLICATION_TYPE_CANCEL = 0;
    public static final int APPLICATION_TYPE_YT = 1;
    public static final int APPLICATION_TYPE_CHROME = 2;
    public static final int APPLICATION_TYPE_MAPS = 3;
    public static final int APPLICATION_TYPE_DOWNLOAD = 4;
    Intent subIntent;

    // Singleton stuff
    private final static Data INSTANCE = new Data();
    public synchronized static Data getInstance(){
        return INSTANCE;
    }

    // Handling messages from Server to app
    private final LinkedList<ActivityToStart> messagesFromServer = new LinkedList<>();
    public synchronized ActivityToStart getOrSetMessageFromServer(boolean isMethodUsedAsGetter, String message) {
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
                LocalDateTime timeToEnd = activityToStartToBeSet.startTime.plusMinutes(activityToStartToBeSet.timeToEnd);
                activityToStartToBeSet = new ActivityToStart();
                activityToStartToBeSet.type = APPLICATION_TYPE_CANCEL;
                activityToStartToBeSet.startTime = timeToEnd;
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
            } else {
                for (int i = 0; i < messagesFromServer.size(); i++) {
                    if (messagesFromServer.get(i).startTime.isAfter(activityToStartToBeSet.startTime)) {
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