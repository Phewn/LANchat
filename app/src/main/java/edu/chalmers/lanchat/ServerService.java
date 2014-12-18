package edu.chalmers.lanchat;

import android.app.Activity;
import android.app.IntentService;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
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

import edu.chalmers.lanchat.db.ClientContentProvider;
import edu.chalmers.lanchat.db.ClientTable;


public class ServerService extends IntentService implements Loader.OnLoadCompleteListener<Cursor> {
    public static final String EXTRAS_PORT = "EXTRAS_PORT";
    public static final String EXTRAS_ECHO = "EXTRAS_ECHO";
    public static final String ACTION_RECEIVE = "ACTION_RECEIVE";

    public static final String TAG = "ServerService";
    public static final int PORT = 8988;

    private Handler handler;
    private CursorLoader cursorLoader;
    private Cursor cursor;
    private String localIP;

    public ServerService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();

        String[] projection = { ClientTable.COLUMN_ID, ClientTable.COLUMN_IP };
        cursorLoader = new CursorLoader(this, ClientContentProvider.CONTENT_URI, projection, null, null, null);
        cursorLoader.registerListener(0, this);
        cursorLoader.startLoading();

        localIP = Utils.getLocalIPAddress();
    }

    @Override
    public void onDestroy() {
        // Stop the cursor loader
        if (cursorLoader != null) {
            cursorLoader.unregisterListener(this);
            cursorLoader.cancelLoad();
            cursorLoader.stopLoading();
        }

        //empty client data
        getContentResolver().delete(ClientContentProvider.CONTENT_URI, null, null);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Context context = getApplicationContext();
        if (intent.getAction().equals(ACTION_RECEIVE)) {
            boolean echo = intent.getBooleanExtra(EXTRAS_ECHO, false);
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
                        // save to database
                        ContentValues values = new ContentValues();
                        values.put(ClientTable.COLUMN_IP, message.substring(1));
                        getContentResolver().insert(ClientContentProvider.CONTENT_URI, values);
                    } else if (echo && cursor != null){
                        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                            String host = cursor.getString(cursor.getColumnIndex(ClientTable.COLUMN_IP));

                            if ( !(host.equals(socket.getInetAddress().getHostName()) || host.equals(localIP)) ) {
                                Intent echoIntent = new Intent(this, MessageService.class);
                                echoIntent.setAction(MessageService.ACTION_SEND);
                                echoIntent.putExtra(MessageService.EXTRAS_HOST, host);
                                echoIntent.putExtra(MessageService.EXTRAS_PORT, PORT);
                                echoIntent.putExtra(MessageService.EXTRAS_MESSAGE, message);
                                startService(echoIntent);
                            }
                        }
                    }

                    scanner.close();
                    inputStream.close();
                    client.close();
                    //stopSelf(); // TODO: Remove
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onLoadComplete(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "# database entries: " + data.getCount());
        cursor = data;
    }
}
