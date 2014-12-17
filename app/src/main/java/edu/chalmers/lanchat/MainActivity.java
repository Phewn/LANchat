package edu.chalmers.lanchat;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.database.Cursor;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
        if (message != "") {
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
        /*
        ChatMessage chatMessage  = adapter.getItem(position);
        chatMessage.setPopularity(+1);
        */

    }
}

