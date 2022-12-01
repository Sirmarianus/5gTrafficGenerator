package mk.trafficgenerator5g;

import android.app.Service;
import android.content.Intent;
import android.net.TrafficStats;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

public class StatisticsGatheringService extends Service {
    Data data;
    private final int THREAD_SLEEP_TIME_SEC = 60;
    long totalRxBytes = TrafficStats.getTotalRxBytes();
    long totalRxPackets = TrafficStats.getTotalRxPackets();
    long totalTxBytes = TrafficStats.getTotalTxBytes();
    long totalTxPackets = TrafficStats.getTotalTxPackets();

    public StatisticsGatheringService() {}

    public int onStartCommand(Intent intent, int flags, int startId) {
        data = Data.getInstance();
        Log.d("DUPA", "StatisticsGatheringService -> START");
        new Thread(
                () -> {
                    while (Data.getShouldThreadsBeGoing()) {
                        SystemClock.sleep(THREAD_SLEEP_TIME_SEC * 1000);
                        getStatistics();
                        // TODO save statistics to file
                    }
                }
        ).start();
        return super.onStartCommand(intent, flags, startId);
    }

    private void getStatistics() {
        long _totalRxBytes = TrafficStats.getTotalRxBytes();
        long _totalRxPackets = TrafficStats.getTotalRxPackets();
        long _totalTxBytes = TrafficStats.getTotalTxBytes();
        long _totalTxPackets = TrafficStats.getTotalTxPackets();

        long deltaRxBytes = _totalRxBytes - totalRxBytes;
        long deltaRxPackets = _totalRxPackets - totalRxPackets;
        long deltaTxBytes = _totalTxBytes - totalTxBytes;
        long deltaTxPackets = _totalTxPackets - totalTxPackets;

        totalRxBytes = _totalRxBytes;
        totalRxPackets = _totalRxPackets;
        totalTxBytes = _totalTxBytes;
        totalTxPackets = _totalTxPackets;

        StatisticsJSON statisticsJSON = new StatisticsJSON(deltaRxBytes, deltaRxPackets, deltaTxBytes, deltaTxPackets);
        data.getOrSetMessageToServer(false, statisticsJSON.toString());


        Log.e("DUPA", "deltaRxBytes: " + deltaRxBytes);
        Log.e("DUPA", "deltaRxPackets: " + deltaRxPackets);
        Log.e("DUPA", "deltaTxBytes: " + deltaTxBytes);
        Log.e("DUPA", "deltaTxPackets: " + deltaTxPackets);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}