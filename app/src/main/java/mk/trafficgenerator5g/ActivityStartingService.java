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
    private static final int THREAD_SLEEP_TIME_SEC = 7;

    public ActivityStartingService() {}

    @Override
    public void onCreate() {
        super.onCreate();
        data = Data.getInstance();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(() -> {
            Intent subIntent = new Intent(Intent.ACTION_VIEW);
            subIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            subIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//            data.subIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            data.subIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//            data.subIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//            data.subIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            subIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            subIntent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            while (Data.getShouldThreadsBeGoing()) {
                SystemClock.sleep(THREAD_SLEEP_TIME_SEC * 1000);
                ActivityToStart message = data.getOrSetMessageFromServer(true, "");
                if (message.url.equals("")) {
                    Log.d("DUPA", "NOTHING TO START");
                } else {
                    Log.d("DUPA", "ActivityStartingService -> message");
                    Log.d("DUPA", "type: " + message.type + ", url: " + message.url);
                    Log.d("DUPA", "startTime: " + message.startTime + ", time: " + message.timeToEnd);

                    subIntent.setData(Uri.parse(message.url));
                    switch (message.type) {
                        case Data.APPLICATION_TYPE_CANCEL:
                            Log.d("DUPA", "ActivityStartingService -> CASE 0");
                            subIntent.setClass(getApplicationContext(), EmptyActivity.class);
                            break;
                        case Data.APPLICATION_TYPE_YT:
                            Log.d("DUPA", "ActivityStartingService -> CASE 1");
                            subIntent.setClass(getApplicationContext(), YoutubeActivity.class);
                            break;
                        case Data.APPLICATION_TYPE_CHROME:
                            Log.d("DUPA", "ActivityStartingService -> CASE 2");
                            subIntent.setPackage("com.android.chrome");
                            break;
                        case Data.APPLICATION_TYPE_MAPS:
                            Log.d("DUPA", "ActivityStartingService -> CASE 3");
                            subIntent.setPackage("com.google.android.apps.maps");
                            break;
                        case Data.APPLICATION_TYPE_DOWNLOAD:
                            Log.d("DUPA", "ActivityStartingService -> CASE 4");
                            DownloadManager manager;
                            manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                            Uri uri = Uri.parse(message.url);
                            DownloadManager.Request request = new DownloadManager.Request(uri);
                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
                            long reference = manager.enqueue(request);
                        break;
                        default:
                            Log.d("DUPA", "ActivityStartingService -> CASE DEFAULT");
                    }
                    if (message.type != Data.APPLICATION_TYPE_DOWNLOAD) {
                        startActivity(subIntent);
                    }
                }
            }
        }).start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}