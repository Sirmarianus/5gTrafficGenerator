package mk.trafficgenerator5g;

import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Debug;
import android.os.SystemClock;
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

        srv_ip = findViewById(R.id.serverIP);

        connect = findViewById(R.id.Connect);
        connect.setOnClickListener(view -> {
            String serverIp = srv_ip.getText().toString();
            if (isIPAddressCorrect(serverIp)) {
                createSomeQueue();
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
        msg = "{\"index\":\"1\",\"url\":\"https://cdimage.ubuntu.com/kubuntu/releases/22.10/release/kubuntu-22.10-desktop-amd64.iso\",\"type\":\"4\",\"startTime\":\"15:00:00 12-11-2022\",\"time\":\"0\"}";
        data.getOrSetMessageFromServer(false, msg);
//        msg = "{\"index\":\"1\",\"url\":\"https://pl.wikipedia.org/wiki/Generator_liczb_losowych\",\"type\":\"2\",\"startTime\":\"15:01:00 12-11-2022\",\"time\":\"0\"}";
//        data.getOrSetMessageFromServer(false, msg);
        msg = "{\"index\":\"1\",\"url\":\"https://www.youtube.com/watch?v=9qpixGR3A1Y\",\"type\":\"1\",\"startTime\":\"15:00:00 12-11-2022\",\"time\":\"2\"}";
        data.getOrSetMessageFromServer(false, msg);
        msg = "{\"index\":\"1\",\"url\":\"https://www.youtube.com/watch?v=jb3_vnxXEaw\",\"type\":\"1\",\"startTime\":\"15:02:00 12-11-2022\",\"time\":\"2\"}";
        data.getOrSetMessageFromServer(false, msg);
        msg = "{\"index\":\"1\",\"url\":\"https://www.youtube.com/watch?v=CYiGyaJyPMk\",\"type\":\"1\",\"startTime\":\"15:04:00 12-11-2022\",\"time\":\"3\"}";
        data.getOrSetMessageFromServer(false, msg);
    }
}