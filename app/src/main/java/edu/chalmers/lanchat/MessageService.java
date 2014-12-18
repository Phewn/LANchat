package edu.chalmers.lanchat;

import android.app.IntentService;
import android.content.Intent;
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

    public static final String ACTION_SEND = "ACTION_SEND";

    public static final String EXTRAS_HOST = "EXTRAS_HOST";
    public static final String EXTRAS_PORT = "EXTRAS_PORT";
    public static final String EXTRAS_MESSAGE = "EXTRAS_MESSAGE";

    private static final int SOCKET_TIMEOUT = 5000;

    public MessageService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent.getAction().equals(ACTION_SEND)) {
            String host = intent.getExtras().getString(EXTRAS_HOST);
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
}
