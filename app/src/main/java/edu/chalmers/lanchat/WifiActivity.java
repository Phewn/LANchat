package edu.chalmers.lanchat;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


public class WifiActivity extends Activity implements WifiP2pManager.PeerListListener {
    private WifiP2pManager manager;
    private Channel channel;
    private WifiBroadcastReceiver receiver;
    private IntentFilter intentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);
        receiver = new WifiBroadcastReceiver(manager, channel, this);

        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

                Log.d("LANChat", "Success!");
            }

            @Override
            public void onFailure(int reason) {

                Log.d("LANChat", "Failure!");
            }
        });
    }

    public void buttonClick(View view) {
        switch (view.getId()) {
            case R.id.createWifiButton:
                manager.removeGroup(channel, null); // remove any old group if it exists.
                manager.createGroup(channel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(WifiActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int reason) {
                        Toast.makeText(WifiActivity.this, "Failure!", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
        }
    }

    /* register the broadcast receiver with the intent values to be matched */
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, intentFilter);
    }

    /* unregister the broadcast receiver */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_wifi, menu);
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

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peers) {
        Log.d("LANChat", "Peer List");
        Log.d("LANChat", "=========");
        for (WifiP2pDevice peer : peers.getDeviceList()) {
            Log.d("LANChat", peer.deviceName);
        }
        Log.d("LANChat", "=========");
        if ( peers.getDeviceList().iterator().hasNext()){
            WifiP2pDevice device = peers.getDeviceList().iterator().next();
            connectDevice(device);

        }
    }

    public void connectDevice(WifiP2pDevice device){
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        manager.connect(channel, config, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                //success logic
            }

            @Override
            public void onFailure(int reason) {
                //failure logic
            }
        });
    }

}
