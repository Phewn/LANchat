package edu.chalmers.lanchat.connect;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * A service which sends a string message to a server socket. By default messages are sent
 * to the group owner in a wifi direct group.
 */
public class MessageService extends IntentService {
    public static final String TAG = "MessageService";

    public static final String ACTION_SEND = "ACTION_SEND";

    public static final String EXTRAS_HOST = "EXTRAS_HOST";
    public static final String EXTRAS_PORT = "EXTRAS_PORT";
    public static final String EXTRAS_MESSAGE = "EXTRAS_MESSAGE";

    // Default host and port values
    public static final String IP_SERVER = "192.168.49.1";
    public static int PORT = 8988;

    private static final int SOCKET_TIMEOUT = 5000;

    public MessageService() {
        super(TAG);
    }

    /**
     * Any calls to the service is queued and handled consecutively in a different thread using
     * this callback.
     *
     * @param intent
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent.getAction().equals(ACTION_SEND)) {
            // Collect information from the intent
            String host = intent.getExtras().getString(EXTRAS_HOST, IP_SERVER);
            int port = intent.getExtras().getInt(EXTRAS_PORT, PORT);
            String message = intent.getExtras().getString(EXTRAS_MESSAGE);

            Socket socket = new Socket();

            try {
                Log.d(TAG, "Opening client socket");
                socket.bind(null);
                socket.connect((new InetSocketAddress(host, port)), SOCKET_TIMEOUT);

                if (socket.isConnected()) {
                    Log.d(TAG, "Sending message: " + message);
                    OutputStream stream = socket.getOutputStream();
                    stream.write(message.getBytes());
                    Log.d(TAG, "Client: Data written");
                } else {
                    Log.d(TAG, "Connection to " + host + " failed.");
                }
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            } finally {
                // Make sure the socket is properly closed
                if (socket != null && socket.isConnected()) {
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
