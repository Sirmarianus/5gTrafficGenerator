package mk.trafficgenerator5g;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketService extends Service {
    private final int THREAD_SLEEP_TIME_SEC = 10;
    Data data;
    Socket socket;
    PrintWriter out;
    BufferedReader in;

    public SocketService() {}

    @Override
    public void onCreate() {
        super.onCreate();
        data = Data.getInstance();
    }

    private void createSocketConnection() {
        Log.d("SocketService", "RUN");
        try {
            Log.d("SocketService", data.serverIP);
            socket = new Socket(data.serverIP, 5001);
            Log.d("SocketService", "A");
            out = new PrintWriter(socket.getOutputStream(), true);
            Log.d("SocketService", "B");
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Log.d("SocketService", "C");
        } catch (Exception e) {
            Log.d("SocketService", String.valueOf(e));
            Data.stopServices();
        }
    }

    private String receiveMessageFromServer() {
        try {
            StringBuilder message = new StringBuilder();

            while (in.ready()) {
                message.append((char)in.read());
            }
            return message.toString();
        } catch (IOException e) {
            Log.d("SocketService", "receiveMessageFromServer -> " + e);
            return "";
        }
    }

    private void sendMessageToServer(String message) {
        Log.d("SocketService", "SocketThread -> Sending message");
        out.println(message);
        Log.d("SocketService", "SocketThread -> Message sent");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("SocketService", "SocketService -> START");
        new Thread(
                () -> {
                    createSocketConnection();
                    while (Data.getShouldThreadsBeGoing()) {
                        SystemClock.sleep(THREAD_SLEEP_TIME_SEC * 1000);

                        String msgToServer = data.getOrSetMessageToServer(true, "");
                        if (!msgToServer.equals("")) {
                            sendMessageToServer(msgToServer);
                        }
                        Log.d("SocketService", "MessageToServer: " + msgToServer);

                        String msgFromServer = receiveMessageFromServer();
                        if (!msgFromServer.equals("")) {
                            data.getOrSetMessageFromServer(false, msgFromServer);
                        }
                        Log.d("SocketService", "MessageFromServer: " + msgFromServer);

                    }
                    try {
                        in.close();
                        out.close();
                        socket.close();
                        Log.d("SocketService", "Closed socket");
                    } catch (Exception ignored) {}
                }
        ).start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Not implemented
        throw new UnsupportedOperationException("Not yet implemented");
    }
}