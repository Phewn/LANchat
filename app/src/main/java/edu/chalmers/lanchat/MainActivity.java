package edu.chalmers.lanchat;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
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
import java.util.LinkedList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private EditText newShout;
    private ListView listViewI;
    private TextView idName;
    private String user;
    private ArrayList<String> listItems = new ArrayList<String>();
    private ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        newShout = (EditText) findViewById(R.id.chatLine);
        listViewI = (ListView) findViewById(R.id.listViewI);
        idName = (TextView) findViewById(R.id.textView2);

        adapter = new ArrayAdapter<String>(this, R.layout.rowlayout, R.id.textView, listItems);

        listViewI.setAdapter(adapter);

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

    public void getUsername() {
        AccountManager manager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
        Account[] list = manager.getAccounts();

        for (Account item : list) {
            if (item.type == "com.google") {
                user = item.name;
                return;
            }
        }

        if (list.length != -1 && list[0] != null) {
            user = list[0].name;
        }
    }
}

