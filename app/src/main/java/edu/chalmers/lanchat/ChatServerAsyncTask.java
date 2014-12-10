package edu.chalmers.lanchat;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by oliver on 14-12-09.
 */
public class ChatServerAsyncTask extends AsyncTask{

    @Override
    protected String doInBackground(Object[] params) {
        try {
            ServerSocket serverSocket = new ServerSocket(8888);
            Socket client = serverSocket.accept();
            Log.d("LanChat", "What's up");

            InputStream inputStream = client.getInputStream();
            String message = inputStream.toString();

            serverSocket.close();

            Log.d("LanChat", message);

            return message;


        } catch (IOException e){
            Log.d("LanChat", "transfer fail");
            return null;
        }
    }
}
