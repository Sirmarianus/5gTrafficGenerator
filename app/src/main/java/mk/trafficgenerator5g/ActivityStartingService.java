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
    private static final int THREAD_SLEEP_TIME_SEC = 7;

    public ActivityStartingService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(() -> {
            Data data = Data.getInstance();
            data.subIntent = new Intent(Intent.ACTION_VIEW);
            data.subIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            data.subIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//            data.subIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            data.subIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//            data.subIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//            data.subIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            data.subIntent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

            while (data.shouldThreadsBeGoing) {
                SystemClock.sleep(THREAD_SLEEP_TIME_SEC * 1000);
                ActivityToStart message = data.getOrSetMessageFromServer(true, "");
                if (message.url.equals("")) {
                    Log.d("DUPA", "EMPTY MESSAGE");
                } else {
                    Log.d("DUPA", "ActivityStartingService -> message");
                    Log.d("DUPA", "type: " + message.type + ", url: " + message.url);
                    Log.d("DUPA", "startTime: " + message.startTime + ", time: " + message.timeToEnd);

                    data.subIntent.setData(Uri.parse(message.url));
                    switch (message.type) {
                        case Data.APPLICATION_TYPE_CANCEL:
                            Log.d("DUPA", "ActivityStartingService -> CASE 0");
                            data.subIntent.setClass(getApplicationContext(), EmptyActivity.class);
                            break;
                        case Data.APPLICATION_TYPE_YT:
                            Log.d("DUPA", "ActivityStartingService -> CASE 1");
                            data.subIntent.setPackage("com.google.android.youtube");
                            break;
                        case Data.APPLICATION_TYPE_CHROME:
                            Log.d("DUPA", "ActivityStartingService -> CASE 2");
                            data.subIntent.setPackage("com.android.chrome");
                            break;
                        case Data.APPLICATION_TYPE_MAPS:
                            Log.d("DUPA", "ActivityStartingService -> CASE 3");
                            data.subIntent.setPackage("com.google.android.apps.maps");
                            break;
                        case Data.APPLICATION_TYPE_DOWNLOAD:
                            DownloadManager manager;
                            manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                            Uri uri = Uri.parse("https://releases.ubuntu.com/22.04.1/ubuntu-22.04.1-desktop-amd64.iso");
                            DownloadManager.Request request = new DownloadManager.Request(uri);
                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
                            long reference = manager.enqueue(request);
                        break;
                        default:
                            Log.d("DUPA", "ActivityStartingService -> CASE DEFAULT");
                    }
                    startActivity(data.subIntent);
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