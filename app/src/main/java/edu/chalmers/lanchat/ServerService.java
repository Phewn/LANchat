package edu.chalmers.lanchat;

import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class ServerService extends IntentService {
    public static final String EXTRAS_PORT = "EXTRAS_PORT";
    public static final String ACTION_RECEIVE = "ACTION_RECEIVE";

    public static final String TAG = "ServerService";
    private Handler handler;

    private List<String> clients = new ArrayList<>();

    public ServerService() {
        super(TAG);
    }



    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Context context = getApplicationContext();
        if (intent.getAction().equals(ACTION_RECEIVE)) {
            int port = intent.getExtras().getInt(EXTRAS_PORT);

            try {
                ServerSocket socket = new ServerSocket(port);
                Log.d(TAG, "Server socket open");

                while (true) {
                    Socket client = socket.accept();
                    Log.d(TAG, "Server socket accepting");
                    InputStream inputStream = client.getInputStream();
                    Scanner scanner = new Scanner( inputStream ).useDelimiter("\\A");
                    final String message = scanner.next();
                    Log.d(TAG, message);

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ServerService.this, message, Toast.LENGTH_LONG).show();
                        }
                    });

                    if (message.charAt(0) == '&') {
                        // TODO: save to database
                    }

                    scanner.close();
                    inputStream.close();
                    client.close();
                    stopSelf(); // TODO: Remove
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
