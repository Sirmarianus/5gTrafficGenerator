package mk.trafficgenerator5g;

import android.app.DownloadManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

public class ActivityStartingService extends Service {
    Data data;
    private static final int THREAD_SLEEP_TIME_SEC = 10;

    public ActivityStartingService() {}

    @Override
    public void onCreate() {
        super.onCreate();
        data = Data.getInstance();
        data.manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(() -> {
            Intent subIntent = new Intent(Intent.ACTION_VIEW);
            subIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            subIntent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            while (Data.getShouldThreadsBeGoing()) {
                SystemClock.sleep(THREAD_SLEEP_TIME_SEC * 1000);
                ActivityToStart message = data.getOrSetMessageFromServer(true, "");
                if (message.url.equals("")) {
                    Log.d("ActivityStartingService", "NOTHING TO START");
                } else {
                    Log.d("ActivityStartingService", "message");
                    Log.d("ActivityStartingService", "type: " + message.type + ", url: " + message.url);
                    Log.d("ActivityStartingService", "startTime: " + message.startTime + ", duration: " + message.duration);

                    subIntent.setData(Uri.parse(message.url));
                    switch (message.type) {
                        case Data.APPLICATION_TYPE_CANCEL:
                            Log.d("ActivityStartingService", "CASE CANCEL");
                            if (Data.STARTED_ACTIVITY.equals(Data.APPLICATION_TYPE_CANCEL)) {
                                break;
                            }
                            Data.STARTED_ACTIVITY = Data.APPLICATION_TYPE_CANCEL;
                            for (long ele : data.downloadingFilesIDs) {
                                data.manager.remove(ele);
                            }
                            startEmptyActivity(subIntent);
                            break;

                        case Data.APPLICATION_TYPE_YT:
                            Log.d("ActivityStartingService", "CASE YT");
                            if (Data.STARTED_ACTIVITY.equals(Data.APPLICATION_TYPE_YT)) {
                                startEmptyActivity(subIntent);
                                SystemClock.sleep(5000);
                            }
                            Data.STARTED_ACTIVITY = Data.APPLICATION_TYPE_YT;
                            subIntent.setClass(getApplicationContext(), YoutubeActivity.class);
                            break;

                        case Data.APPLICATION_TYPE_PAGE:
                            Log.d("ActivityStartingService", "CASE PAGE");
                            if (Data.STARTED_ACTIVITY.equals(Data.APPLICATION_TYPE_PAGE)) {
                                startEmptyActivity(subIntent);
                                SystemClock.sleep(5000);
                            }
                            Data.STARTED_ACTIVITY = Data.APPLICATION_TYPE_PAGE;
                            subIntent.setClass(getApplicationContext(), WebViewActivity.class);
                            break;

                        case Data.APPLICATION_TYPE_MAPS:
                            Log.d("ActivityStartingService", "CASE MAPS");
                            if (Data.STARTED_ACTIVITY.equals(Data.APPLICATION_TYPE_MAPS)) {
                                startEmptyActivity(subIntent);
                                SystemClock.sleep(5000);
                            }
                            Data.STARTED_ACTIVITY = Data.APPLICATION_TYPE_MAPS;
                            subIntent.setClass(getApplicationContext(), MapsActivity.class);
                            break;

                        case Data.APPLICATION_TYPE_DOWNLOAD:
                            Log.d("ActivityStartingService", "CASE DOWNLOAD");
                            Uri uri = Uri.parse(message.url);
                            DownloadManager.Request request = new DownloadManager.Request(uri);
                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
                            long reference = data.manager.enqueue(request);
                            data.downloadingFilesIDs.add(reference);
                        break;

                        default:
                            Log.d("ActivityStartingService", "CASE DEFAULT");
                    }
                    if (message.type.equals(Data.APPLICATION_TYPE_YT) ||
                            message.type.equals(Data.APPLICATION_TYPE_PAGE) ||
                            message.type.equals(Data.APPLICATION_TYPE_MAPS) ) {
                        startActivity(subIntent);
                    }
                }
            }
        }).start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void startEmptyActivity(Intent subIntent) {
        subIntent.setClass(getApplicationContext(), EmptyActivity.class);
        startActivity(subIntent);
    }
}