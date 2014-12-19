package edu.chalmers.lanchat;

import android.database.Cursor;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

/*
*To do:
*Dynamic flow for update texsize list depending on what screensize and device you have.
*Make all messages viewable even the ones on the very top.
 */


public class MainActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {

    private EditText newShout;
    private ListView listViewI;
    private String user = "";
    private ArrayList<ChatMessage> listItems = new ArrayList<>();
    private CustomAdapter adapter;
    private int[] colorList = {Color.BLUE, Color.CYAN, Color.GREEN, Color.MAGENTA, Color.RED};
    private int userColor;
    private Random r = new Random();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        newShout = (EditText) findViewById(R.id.chatLine);
        listViewI = (ListView) findViewById(R.id.listViewI);

        listViewI.setOnItemClickListener(this);

        listViewI.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                updateRowSize();
            }
        });

        adapter = new CustomAdapter(this, listItems);

        listViewI.setAdapter(adapter);

        user = "";

        userColor = colorList[r.nextInt(colorList.length)];

        getUsername();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.shoutButton) {

            return true;
        }

        //noinspection SimplifiableIfStatement
        else if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void updateChatbox() {
        //Get message from textEdit box;
        String message = newShout.getText().toString();

        if (!message.equals("")) {

            ChatMessage chatObject = new ChatMessage(user, userColor);
            chatObject.setMessage(message);

            listItems.add(chatObject);

            adapter.notifyDataSetChanged();
        }
    }

    public void ButtonOnClick(View v) {
        switch (v.getId()) {
            case R.id.shoutButton:
                updateChatbox();
                newShout.getText().clear();
                break;
        }
    }

    public void getUsername() {
        Cursor c = getApplication().getContentResolver().query(ContactsContract.Profile.CONTENT_URI, null, null, null, null);
        c.moveToFirst();
        user = c.getString(c.getColumnIndex("display_name"));
        c.close();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ChatMessage chatMessage  = adapter.getItem(position);
        float pop = chatMessage.getPopularity();

        pop += 1;
        
        chatMessage.setPopularity(pop);
        adapter.notifyDataSetChanged();

    }

    public void updateRowSize(){

        int lastPos = listViewI.getFirstVisiblePosition();


        int x = listViewI.getChildCount();
        int displacement = 1;
        for (int i = 0; i < x; i++) {

            if ( i < x-displacement ) {


                //Get popularity from ChatMessege
                ChatMessage chat = (ChatMessage) listViewI.getItemAtPosition(i+lastPos);
                float textSize = chat.getTextSize();

                //Get row and change size on that row
                View view = listViewI.getChildAt(i);

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
}

