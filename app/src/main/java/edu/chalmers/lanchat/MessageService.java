package edu.chalmers.lanchat;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by Daniel on 2014-12-17.
 */
public class MessageService extends IntentService {
    public static final String TAG = "MessageService";

    public static final String ACTION_SEND_ADDRESS = "ACTION_SEND_ADDRESS";
    public static final String ACTION_SEND_MESSAGE = "ACTION_SEND_MESSAGE";

    public static final String EXTRAS_ADDRESS = "EXTRAS_HOST";
    public static final String EXTRAS_PORT = "EXTRAS_PORT";
    public static final String EXTRAS_MESSAGE = "EXTRAS_MESSAGE";

    private static final int SOCKET_TIMEOUT = 5000;

    public MessageService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent.getAction().equals(ACTION_SEND_MESSAGE)) {
            sendMessage(intent);
        } else if (intent.getAction().equals(ACTION_SEND_ADDRESS)) {
            sendAddress(intent);
        }
    }

    private void sendAddress(Intent intent) {
        Log.d(TAG, "Sending Address");
        String host = intent.getExtras().getString(EXTRAS_ADDRESS);
        int port = intent.getExtras().getInt(EXTRAS_PORT);
        String localIP = Utils.getLocalIPAddress();
        Socket socket = new Socket();

        try {
            Log.d(TAG, "Opening client socket - ");
            socket.bind(null);
            socket.connect((new InetSocketAddress(host, port)), SOCKET_TIMEOUT);

            Log.d(WiFiDirectActivity.TAG, "Client socket - " + socket.isConnected());
            OutputStream stream = socket.getOutputStream();

            stream.write(("&" + localIP).getBytes());

            Log.d(WiFiDirectActivity.TAG, "Client: Address written");
        } catch (IOException e) {
            Log.e(WiFiDirectActivity.TAG, e.getMessage());
        } finally {
            if (socket != null) {
                if (socket.isConnected()) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // Give up
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void sendMessage(Intent intent) {
        Log.d(TAG, "Sending Message");
        String host = intent.getExtras().getString(EXTRAS_ADDRESS);
        int port = intent.getExtras().getInt(EXTRAS_PORT);
        String message = intent.getExtras().getString(EXTRAS_MESSAGE);
        Socket socket = new Socket();

        try {
            Log.d(TAG, "Opening client socket - ");
            socket.bind(null);
            socket.connect((new InetSocketAddress(host, port)), SOCKET_TIMEOUT);

            Log.d(WiFiDirectActivity.TAG, "Client socket - " + socket.isConnected());
            OutputStream stream = socket.getOutputStream();

            stream.write(message.getBytes());

            Log.d(WiFiDirectActivity.TAG, "Client: Data written");
        } catch (IOException e) {
            Log.e(WiFiDirectActivity.TAG, e.getMessage());
        } finally {
            if (socket != null) {
                if (socket.isConnected()) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // Give up
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
