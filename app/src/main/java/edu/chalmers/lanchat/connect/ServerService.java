package edu.chalmers.lanchat.connect;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONObject;

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
    private Gson gson;

    public ServerService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        gson = new Gson();

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
                    final String json = scanner.next();
                    scanner.close();
                    inputStream.close();
                    Log.d(TAG, json);

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

    private void handleChatMessage(ChatMessage message, boolean echo) {
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

    private void handleAdminMessage(AdminMessage message) {
        if (message.getType() == AdminMessage.MessageType.IP_NOTIFICATION) {
            // save to database
            ContentValues values = new ContentValues();
            values.put(ClientTable.COLUMN_IP, message.getData());
            getContentResolver().insert(ClientContentProvider.CONTENT_URI, values);
        }
    }

    @Override
    public void onLoadComplete(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "# database entries: " + data.getCount());
        cursor = data;
    }
}
