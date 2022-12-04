package mk.trafficgenerator5g;

import android.app.Service;
import android.content.Intent;
import android.net.TrafficStats;
import android.os.Environment;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

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
        Log.d("StatisticsGatheringService", "START");
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

        JSONObject statisticsJSON = ParserJSON.createJsonWithStatistics(deltaRxBytes, deltaRxPackets, deltaTxBytes, deltaTxPackets);
        assert statisticsJSON != null;
        data.getOrSetMessageToServer(false, statisticsJSON.toString());

        String stats = deltaRxBytes + ";" + deltaRxPackets + ";" + deltaTxBytes + ";" + deltaTxPackets + "\n";
        writeStatisticsToFile("trafficGenerator5g.txt", stats);


        Log.e("StatisticsGatheringService", "deltaRxBytes: " + deltaRxBytes);
        Log.e("StatisticsGatheringService", "deltaRxPackets: " + deltaRxPackets);
        Log.e("StatisticsGatheringService", "deltaTxBytes: " + deltaTxBytes);
        Log.e("StatisticsGatheringService", "deltaTxPackets: " + deltaTxPackets);
    }

    public void writeStatisticsToFile(String fileName, String content) {
        File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        Log.e("write", root + "/" + fileName);
        File file = new File(root, fileName);
        try {
            if (!file.exists()) {
                Log.e("create", "file");
                file.createNewFile();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        try {
            FileOutputStream fout = new FileOutputStream(file, true);
            fout.write(content.getBytes(StandardCharsets.UTF_8));
            fout.close();
        } catch (Exception e) {
            Log.e("StatisticsGatheringService", e.toString());
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}