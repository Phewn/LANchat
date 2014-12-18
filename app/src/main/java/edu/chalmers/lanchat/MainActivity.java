package edu.chalmers.lanchat;

import android.database.Cursor;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
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


public class MainActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {

    private EditText newShout;
    private ListView listViewI;
    private TextView idName;
    private String user;
    private ArrayList<ChatMessage> listItems = new ArrayList<ChatMessage>();
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
        String message = newShout.getText().toString();
        message = "Det här är en ganska tråkig mening tycker jag nog faktiskt!";
        if (!message.equals("")) {
            if(user == null){
                user = "";
            }

            ChatMessage chatObject = new ChatMessage(user, userColor);
            chatObject.setMessage(message);
            listItems.add(chatObject);
            adapter.notifyDataSetChanged();
        }
        return;
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
        int startPos = listViewI.getLastVisiblePosition();
        int lastPos = listViewI.getFirstVisiblePosition();
        Log.d("StartPos", startPos + "");
        Log.d("LastPos", lastPos + "");


        int x = listViewI.getChildCount();
        for (int i = startPos; i >= lastPos; i--) {
            if (true) {
                View view = listViewI.getChildAt(i);
                Log.d("New scale", i + "");

                if (startPos == 21)
                    Log.d("asdf", "sadf");

                TextView text = (TextView) view.findViewById(R.id.textViewNameAndMessage);
                text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            }
        }
    }
}

