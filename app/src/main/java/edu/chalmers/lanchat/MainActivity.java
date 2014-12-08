package edu.chalmers.lanchat;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    private EditText newShout;
    private ListView listViewI;
    private ArrayList<String> listItems = new ArrayList<String>();
    private ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        newShout = (EditText) findViewById(R.id.chatLine);
        listViewI = (ListView) findViewById(R.id.listViewI);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems);

        listViewI.setAdapter(adapter);

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
        String txt = newShout.getText().toString();
        if (txt != "") {
            listItems.add(txt);
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
}

