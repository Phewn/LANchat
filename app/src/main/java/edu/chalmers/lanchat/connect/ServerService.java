package edu.chalmers.lanchat.connect;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import edu.chalmers.lanchat.AdminMessage;
import edu.chalmers.lanchat.ChatMessage;
import edu.chalmers.lanchat.Message;
import edu.chalmers.lanchat.db.ClientContentProvider;
import edu.chalmers.lanchat.db.ClientTable;
import edu.chalmers.lanchat.db.MessageContentProvider;
import edu.chalmers.lanchat.db.MessageTable;

/**
 * Listens for incoming messages and acts accordingly. Optionally echos received messages to all
 * connected clients.
 */
public class ServerService extends IntentService implements Loader.OnLoadCompleteListener<Cursor> {
    public static final String TAG = "ServerService";

    public static final String EXTRAS_PORT = "EXTRAS_PORT";
    public static final String EXTRAS_ECHO = "EXTRAS_ECHO";
    public static final String ACTION_RECEIVE = "ACTION_RECEIVE";

    public static final int PORT = 8988;

    private CursorLoader cursorLoader;
    private Cursor cursor;
    private String localIP;
    private Gson gson;

    public ServerService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        gson = new Gson();

        // Listen to changes in the client table in the database.
        String[] projection = { ClientTable.COLUMN_IP };
        cursorLoader = new CursorLoader(this, ClientContentProvider.CONTENT_URI, projection, null, null, null);
        cursorLoader.registerListener(0, this);
        cursorLoader.startLoading();

        localIP = IpUtils.getLocalIPAddress();
    }

    @Override
    public void onDestroy() {
        // Stop the cursor loader
        if (cursorLoader != null) {
            cursorLoader.unregisterListener(this);
            cursorLoader.cancelLoad();
            cursorLoader.stopLoading();
        }

        //empty client and message data
        getContentResolver().delete(ClientContentProvider.CONTENT_URI, null, null);
        getContentResolver().delete(MessageContentProvider.CONTENT_URI, null, null);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent.getAction().equals(ACTION_RECEIVE)) {
            // Extract intent data
            boolean echo = intent.getBooleanExtra(EXTRAS_ECHO, false);
            int port = intent.getExtras().getInt(EXTRAS_PORT, PORT);

            try {
                ServerSocket socket = new ServerSocket(port);
                Log.d(TAG, "Server socket open");

                // Listen for incoming sockets indefinitely.
                while (true) {
                    Socket client = socket.accept();
                    Log.d(TAG, "Server socket accepting");
                    InputStream inputStream = client.getInputStream();

                    // Uses a regexp trick where we read until the "next file beginning" which
                    // amounts to reading the full input stream.
                    Scanner scanner = new Scanner( inputStream ).useDelimiter("\\A");
                    final String json = scanner.next();
                    scanner.close();
                    Log.d(TAG, "Receiving message: " + json);

                    // Detect what kind of message were getting and take action accordingly.
                    Message message = gson.fromJson(json, Message.class);
                    if (message.className.equals(AdminMessage.class.getName())) {
                        handleAdminMessage(gson.fromJson(json, AdminMessage.class));
                    } else if (message.className.equals(ChatMessage.class.getName())) {
                        handleChatMessage(gson.fromJson(json, ChatMessage.class), echo);
                    }

                    client.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Handles "ordinary" chat messages by inserting them in the message database
     *
     * @param message
     * @param echo
     */
    private void handleChatMessage(ChatMessage message, boolean echo) {
        // Put the message in the database
        ContentValues values = new ContentValues();
        values.put(MessageTable.COLUMN_NAME, message.getName());
        values.put(MessageTable.COLUMN_MESSAGE, message.getMessage());
        getContentResolver().insert(MessageContentProvider.CONTENT_URI, values);

        // Echo the message to connected clients
        if (echo && cursor != null){
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                String host = cursor.getString(0 /*IP column*/);

                if ( !(host.equals(localIP)) /*|| host.equals(client.getInetAddress().getHostName()) */) {
                    Intent echoIntent = new Intent(this, MessageService.class);
                    echoIntent.setAction(MessageService.ACTION_SEND);
                    echoIntent.putExtra(MessageService.EXTRAS_HOST, host);
                    echoIntent.putExtra(MessageService.EXTRAS_PORT, PORT);
                    echoIntent.putExtra(MessageService.EXTRAS_MESSAGE, message.toJson());
                    startService(echoIntent);
                }
            }
        }
    }

    /**
     * Handles admin messages according to the message type.
     * @param message
     */
    private void handleAdminMessage(AdminMessage message) {
        if (message.getType() == AdminMessage.Type.IP_NOTIFICATION) {
            // save to client to database
            ContentValues values = new ContentValues();
            values.put(ClientTable.COLUMN_IP, message.getData());
            getContentResolver().insert(ClientContentProvider.CONTENT_URI, values);
        }
    }

    /**
     * Called when the client data changes in the database.
     */
    @Override
    public void onLoadComplete(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "# known clients: " + data.getCount());
        cursor = data;
    }
}
