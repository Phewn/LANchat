package edu.chalmers.lanchat;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.google.gson.Gson;

import edu.chalmers.lanchat.connect.IpUtils;
import edu.chalmers.lanchat.connect.MessageService;
import edu.chalmers.lanchat.db.MessageContentProvider;
import edu.chalmers.lanchat.db.MessageTable;


public class ChatActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String EXTRA_GROUP_OWNER = "EXTRA_GROUP_OWNER";
    public static final String EXTRA_DEBUG = "EXTRA_DEBUG";

    private SimpleCursorAdapter adapter;
    private ListView chatList;
    private Button sendButton;
    private EditText inputText;
    private TextView groupOwnerText;
    private Gson gson;
    private boolean debug;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        debug = getIntent().getBooleanExtra(EXTRA_DEBUG, false);

        gson = new Gson();

        chatList = (ListView) findViewById(R.id.chatList);

        // Subscribe to the message database table
        getLoaderManager().initLoader(0, null, this);

        // Make the list reflect the database
        adapter = new ChatAdapter(this);
        chatList.setAdapter(adapter);

        groupOwnerText = (TextView) findViewById(R.id.groupOwnerText);

        // Make it visible whether the phone acts as server or client.
        if (getIntent().getBooleanExtra(EXTRA_GROUP_OWNER, false)) {
            groupOwnerText.setText("Server");
        } else {
            groupOwnerText.setText("Client");
        }


        inputText = (EditText) findViewById(R.id.inputText);

        sendButton = (Button) findViewById(R.id.sendButton);
        sendButton.setOnClickListener( (debug) ? new SendListenerDebug() : new SendListener() );
    }

    /**
     * Override the back button pressed in order to send back an empty result to the activity
     * which started this one.
     */
    @Override
    public void onBackPressed() {
        // Make sure the activity gets notified when finishing
        setResult(RESULT_OK, new Intent());
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Creates a loader which monitors the message table in the database.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = { MessageTable.COLUMN_ID, MessageTable.COLUMN_NAME, MessageTable.COLUMN_MESSAGE };
        CursorLoader cursorLoader = new CursorLoader(this, MessageContentProvider.CONTENT_URI, projection, null, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data); // Update the list
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // data is not available anymore, delete reference
        adapter.swapCursor(null);
    }

    private class SendListenerDebug implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String input = inputText.getText().toString().trim();
            if (input.length() > 0) {
                ChatMessage message = new ChatMessage(input);
                // Put the message in the database
                ContentValues values = new ContentValues();
                values.put(MessageTable.COLUMN_NAME, message.getName());
                values.put(MessageTable.COLUMN_MESSAGE, message.getMessage());
                getContentResolver().insert(MessageContentProvider.CONTENT_URI, values);
            }
            // Clear the input field
            inputText.setText("");
        }
    }

    private class SendListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String input = inputText.getText().toString().trim();
            if (input.length() > 0) {

                // For now, send the ip as username
                ChatMessage message = new ChatMessage(IpUtils.getLocalIPAddress(), input);

                // Send the message to the server
                Intent serviceIntent = new Intent(ChatActivity.this, MessageService.class);
                serviceIntent.setAction(MessageService.ACTION_SEND);
                serviceIntent.putExtra(MessageService.EXTRAS_MESSAGE, message.toJson());
                startService(serviceIntent);
            }
            // Clear the input field
            inputText.setText("");
        }
    }
}
