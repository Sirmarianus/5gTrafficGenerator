package mk.trafficgenerator5g;

import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class MainActivity extends AppCompatActivity {
    Button connect;
    TextView phone_ip;
    EditText srv_ip;
    Data data;

    private boolean isIPAddressCorrect(String ipAddress) {
        ipAddress = ipAddress.trim();
        boolean isIpOk = false;
        try {
            Pattern pattern = Pattern.compile(getString(R.string.IP_PATTERN));
            Matcher matcher = pattern.matcher(ipAddress);
            isIpOk = matcher.matches();
        } catch (PatternSyntaxException ignored) {}
        return isIpOk;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        Debug.waitForDebugger();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        data = Data.getInstance();

        phone_ip = findViewById(R.id.phoneIP);
        WifiManager wifiMgr = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        int ipAddr = wifiInfo.getIpAddress();
        String ipText = Formatter.formatIpAddress(ipAddr);
        phone_ip.setText(ipText);
        data.deviceIP = ipText;

        srv_ip = findViewById(R.id.serverIP);

        connect = findViewById(R.id.Connect);
        connect.setOnClickListener(view -> {
            String serverIp = srv_ip.getText().toString();
            if (isIPAddressCorrect(serverIp)) {
                data.serverIP = serverIp;
                createSomeQueue();
                StatisticsJSON statisticsJSON = new StatisticsJSON(0, 0, 0, 0);
                data.getOrSetMessageToServer(false, statisticsJSON.toString());
//                startService(new Intent(this, SocketService.class));
                startService(new Intent(this, StatisticsGatheringService.class));
                startService(new Intent(this, ActivityStartingService.class));
            }
            else {
                Log.d("DUPA", "Wrong IP");
            }
        });
    }

    private void createSomeQueue() {
        String msg = "";
        msg = "{\"index\":\"1\",\"url\":\"https://www.youtube.com/watch?v=9qpixGR3A1Y\",\"type\":\"YT\",\"startTime\":\"13:00:00\",\"duration\":\"2\"}";
        data.getOrSetMessageFromServer(false, msg);
        msg = "{\"index\":\"1\",\"url\":\"https://www.youtube.com/watch?v=jb3_vnxXEaw\",\"type\":\"YT\",\"startTime\":\"13:00:00\",\"duration\":\"2\"}";
        data.getOrSetMessageFromServer(false, msg);
//        msg = "{\"index\":\"1\",\"url\":\"https://pl.wikipedia.org/wiki/Generator_liczb_losowych\",\"type\":\"PAGE\",\"startTime\":\"13:00:00\",\"duration\":\"0\"}";
//        data.getOrSetMessageFromServer(false, msg);
//        msg = "{\"index\":\"1\",\"url\":\"https://pl.investing.com/economic-calendar/polish-cpi-445\",\"type\":\"PAGE\",\"startTime\":\"13:00:00\",\"duration\":\"0\"}";
//        data.getOrSetMessageFromServer(false, msg);
//        msg = "{\"index\":\"1\",\"url\":\"https://www.google.com/maps/@50.0623798,19.9383207,15z\",\"type\":\"MAPS\",\"startTime\":\"13:00:00\",\"duration\":\"0\"}";
//        data.getOrSetMessageFromServer(false, msg);
//        msg = "{\"index\":\"1\",\"url\":\"https://www.google.com/maps/@50.0734009,20.0161113,15z\",\"type\":\"MAPS\",\"startTime\":\"13:00:00\",\"duration\":\"0\"}";
//        data.getOrSetMessageFromServer(false, msg);
//        msg = "{\"index\":\"1\",\"url\":\"https://cdimage.ubuntu.com/kubuntu/releases/22.10/release/kubuntu-22.10-desktop-amd64.iso\",\"type\":\"DOWNLOAD\",\"startTime\":\"13:00:00\",\"duration\":\"0\"}";
//        data.getOrSetMessageFromServer(false, msg);

    }
}