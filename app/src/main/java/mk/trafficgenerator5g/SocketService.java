package mk.trafficgenerator5g;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
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
        Log.d("DUPA", "SocketService -> RUN");
        try {
            Log.d("DUPA", data.serverIP);
            socket = new Socket(data.serverIP, 5001);
            Log.d("DUPA", "A");
            out = new PrintWriter(socket.getOutputStream(), true);
            Log.d("DUPA", "B");
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Log.d("DUPA", "C");
        } catch (Exception e) {
            Log.d("DUPA", String.valueOf(e));
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
            Log.d("DUPA", "receiveMessageFromServer -> " + e);
            return "";
        }
    }

    private String receiveMessageFromServerJAVA() {
        try {
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            return ois.readObject().toString();
        } catch (Exception e) {
            return "";
        }
    }

    private void sendMessageToServer(String message) {
        Log.d("DUPA", "SocketThread -> Sending message");
        out.println(message);
        Log.d("DUPA", "SocketThread -> Message sent");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("DUPA", "SocketService -> START");
        new Thread(
                () -> {
                    createSocketConnection();
                    while (Data.getShouldThreadsBeGoing()) {
                        SystemClock.sleep(THREAD_SLEEP_TIME_SEC * 1000);
                        sendMessageToServer("DUPA I TEGO TYPU");
                        String msg = receiveMessageFromServer();
//                        String msg = receiveMessageFromServerJAVA();

                        Log.d("DUPA", "SocketThread -> Message from server");
                        msg = msg.equals("") ? "EMPTY MESSAGE" : msg;
                        Log.d("DUPA", msg);

                        data.getOrSetMessageFromServer(false, msg);
//                        data.getOrSetMessageToMainActivity(false, msg);
                    }
                    try {
                        in.close();
                        out.close();
                        socket.close();
                        Log.d("DUPA", "SocketThread -> Closed socket");
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