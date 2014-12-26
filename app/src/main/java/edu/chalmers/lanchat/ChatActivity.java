package edu.chalmers.lanchat;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
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
import edu.chalmers.lanchat.util.Faker;


public class ChatActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String EXTRA_GROUP_OWNER = "EXTRA_GROUP_OWNER";
    public static final String EXTRA_DEBUG = "EXTRA_DEBUG";
    private static final String TAG = "ChatActivity";

    private ChatAdapter adapter;
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

        chatList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ChatMessage chatMessage = (ChatMessage) view.getTag();
                chatMessage.incPopularity(1);
                ContentValues values = new ContentValues();
                values.put(MessageTable.COLUMN_LIKES, chatMessage.getPopularity());
                getContentResolver().update(ContentUris.withAppendedId(MessageContentProvider.CONTENT_URI, id), values, null, null);
            }
        });

        chatList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {}

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                updateRowSize();
            }
        });

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

    private void updateRowSize() {
        int lastPos = chatList.getFirstVisiblePosition();


        int x = chatList.getChildCount();
        int displacement = 1;
        for (int i = 0; i < x; i++) {

            if ( i < x-displacement ) {


                //Get popularity from ChatMessege
                //ChatMessage chat = (ChatMessage) chatList.getItemAtPosition(i+lastPos);
                View v = chatList.getChildAt(i + lastPos);
                if (v == null) {
                    return;
                }
                ChatMessage chat = (ChatMessage) v.getTag();
                float textSize = chat.getTextSize();

                //Get row and change size on that row
                View view = chatList.getChildAt(i);

                TextView text = (TextView) view.findViewById(R.id.textViewNameAndMessage);
                text.setTextSize(TypedValue.COMPLEX_UNIT_SP, (float) logUpdateList(i+displacement, x, textSize));
            }
        }
    }

    private double logUpdateList(int pos, int listSize, float textSize){
        /*
        Changes size on the message dependent on its popularity and position.
         */
        int x = listSize - pos;
        double intensityOfCurve = 0.1;
        int minimumTextSize = 3;


        double eq = textSize*(1/(1+Math.exp(x*intensityOfCurve)))/0.5;

        if(eq < minimumTextSize){

            return minimumTextSize;
        }

        else{

            return eq;
        }
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
        String[] projection = {
            MessageTable.COLUMN_ID,
            MessageTable.COLUMN_NAME,
            MessageTable.COLUMN_MESSAGE,
            MessageTable.COLUMN_COLOR,
            MessageTable.COLUMN_LIKES
        };
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


            if (input.length() == 0) {
                // Generate fake input.
                Faker faker = new Faker();
                input = faker.sentence(3, 8);
            }

            ChatMessage message = new ChatMessage(input);
            // Put the message in the database
            ContentValues values = new ContentValues();
            values.put(MessageTable.COLUMN_NAME, message.getName());
            values.put(MessageTable.COLUMN_MESSAGE, message.getMessage());
            values.put(MessageTable.COLUMN_COLOR, message.getColor());
            values.put(MessageTable.COLUMN_LIKES, message.getPopularity());
            getContentResolver().insert(MessageContentProvider.CONTENT_URI, values);

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
